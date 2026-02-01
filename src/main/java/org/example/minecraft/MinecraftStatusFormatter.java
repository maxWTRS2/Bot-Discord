package org.example.minecraft;

import java.util.List;

public class MinecraftStatusFormatter {

    public static String toText(MinecraftStatus s) {

        if (!s.online) {
            return "🔴 Serveur hors ligne";
        }

        String players = s.players.isEmpty() ? "Aucun joueur connecté"
                : String.join(", ", s.players);

        return """
                🟢 **Serveur Minecraft en ligne**
                📦 Version : `%s`
                👥 Joueurs : **%d/%d**
                🎮 Connectés : %s
                📝 MOTD : %s
                """.formatted(
                s.version,
                s.playersOnline,
                s.playersMax,
                players,
                s.motd
        );
    }
}
