package org.example.minecraft;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.config.BotConfig;
import org.example.utils.ProcessUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MinecraftServerManager {

    private static final BotConfig config = BotConfig.getInstance();

    private static String container() {
        return config.getMinecraft().getDockerContainer();
    }

    private static void rcon(String... args) throws Exception {
        String[] cmd = new String[args.length + 4];
        cmd[0] = "docker";
        cmd[1] = "exec";
        cmd[2] = "-i";
        cmd[3] = container();

        System.arraycopy(args, 0, cmd, 4, args.length);
        ProcessUtils.exec(cmd);
    }

    // =====================
    // START / STOP / RESTART
    // =====================

    public static void start() throws Exception {
        ProcessUtils.exec("docker", "start", container());
    }

    public static void stop() throws Exception {
        // On tente un arrêt propre via rcon-cli
        try {
            rcon("rcon-cli", "stop");
        } catch (Exception ignored) {
            // Si le serveur est déjà éteint, on ignore
        }

        // Puis on stoppe le conteneur
        ProcessUtils.exec("docker", "stop", container());
    }

    public static void restart() throws Exception {
        ProcessUtils.exec("docker", "restart", container());
    }

    // =====================
    // COMMANDES JOUEURS
    // =====================

    public static void kickPlayer(String player, String reason) throws Exception {
        System.out.println("kick for " + player + " " + reason);
        System.out.flush();
        rcon("rcon-cli", "kick", player, player + " " + reason);
    }


    public static void banPlayer(String player, String reason) throws Exception {
        rcon("rcon-cli", "ban", player, reason);
    }

    public static void giveOp(String player) throws Exception {
        rcon("rcon-cli", "op", player);
    }

    public static void removeOp(String player) throws Exception {
        rcon("rcon-cli", "deop", player);
    }

    public static void playCreeperSound(String player) throws Exception {
        rcon("rcon-cli", "execute", player, "~", "~", "~",
                "playsound", "minecraft:entity.creeper.primed", "master", player, "~", "~", "~", "1", "1");
    }

//    public static void sendCommand(String command) throws Exception {
//        rcon("rcon-cli", command);
//    }

    // =====================
    // FETCH STATUS
    // =====================

    public static MinecraftStatus fetchStatus() {
        MinecraftStatus status = new MinecraftStatus();

        try (Socket socket = new Socket()) {
            String ip = config.getMinecraft().getIp();
            int port = config.getMinecraft().getPort();
            int protocol = config.getMinecraft().getProtocol();

            socket.connect(new InetSocketAddress(ip, port), 3000);
            socket.setSoTimeout(3000);

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());

            // HANDSHAKE
            ByteArrayOutputStream handshakeBytes = new ByteArrayOutputStream();
            DataOutputStream handshake = new DataOutputStream(handshakeBytes);

            handshake.writeByte(0x00);
            writeVarInt(handshake, protocol);

            byte[] hostBytes = ip.getBytes(StandardCharsets.UTF_8);
            writeVarInt(handshake, hostBytes.length);
            handshake.write(hostBytes);

            handshake.writeShort(port);
            writeVarInt(handshake, 1);

            writeVarInt(out, handshakeBytes.size());
            out.write(handshakeBytes.toByteArray());

            // STATUS REQUEST
            writeVarInt(out, 1);
            writeVarInt(out, 0);

            // READ RESPONSE
            readVarInt(in);
            int packetId = readVarInt(in);

            if (packetId != 0x00) throw new RuntimeException("Réponse invalide du serveur");

            int jsonLength = readVarInt(in);
            byte[] jsonData = new byte[jsonLength];
            in.readFully(jsonData);

            String json = new String(jsonData, StandardCharsets.UTF_8);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);

            status.version = root.path("version").path("name").asText("?");
            status.playersOnline = root.path("players").path("online").asInt(0);
            status.playersMax = root.path("players").path("max").asInt(0);

            status.players = new ArrayList<>();
            JsonNode sample = root.path("players").path("sample");
            if (sample.isArray()) {
                for (JsonNode p : sample) {
                    status.players.add(p.path("name").asText());
                }
            }

            JsonNode descNode = root.path("description");
            status.motd = descNode.isTextual() ? descNode.asText() : descNode.toString();

            status.online = true;

        } catch (Exception e) {
            status.online = false;
        }

        return status;
    }

    // =====================
    // UTIL VARINT
    // =====================

    private static void writeVarInt(DataOutputStream out, int value) throws Exception {
        while ((value & 0xFFFFFF80) != 0) {
            out.writeByte((value & 0x7F) | 0x80);
            value >>>= 7;
        }
        out.writeByte(value);
    }

    private static int readVarInt(DataInputStream in) throws Exception {
        int numRead = 0;
        int result = 0;
        byte read;

        do {
            read = in.readByte();
            int value = (read & 0x7F);
            result |= (value << (7 * numRead));
            numRead++;
            if (numRead > 5) throw new RuntimeException("VarInt trop long");
        } while ((read & 0x80) != 0);

        return result;
    }

    public static String fetchStatusFormatted() {
        MinecraftStatus status = fetchStatus();
        return MinecraftStatusFormatter.toText(status);
    }
}
