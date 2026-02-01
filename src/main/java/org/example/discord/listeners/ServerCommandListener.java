package org.example.discord.listeners;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.example.config.BotConfig;
import org.example.discord.commands.ServerCommands;
import java.util.List;

public class ServerCommandListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        String name = event.getName();          // server, player, sound
        String sub = event.getSubcommandName(); // kick, ban, start, etc.

        Member member = event.getMember();
        if (member == null) return;

        List<String> adminRoles = BotConfig.getInstance().getAdminRoles();
        boolean isAdmin = member.getRoles().stream()
                .map(role -> role.getName().toLowerCase())
                .anyMatch(roleName -> adminRoles.stream()
                        .map(String::toLowerCase)
                        .anyMatch(roleName::equals));

        // ============================
        // 1. Vérification des permissions
        // ============================

        boolean requiresAdmin = switch (name) {
            case "player" -> true; // toutes les commandes player sont admin
            case "sound" -> true;  // toutes les commandes sound sont admin
            case "server" -> {
                assert sub != null;
                yield switch (sub) {
                    case "stop", "restart" -> true;
                    default -> false; // start/status accessibles à tous
                };
            }
            default -> false;
        };

        if (requiresAdmin && !isAdmin) {
            event.reply("❌ Vous n'avez pas la permission pour utiliser cette commande.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        // ============================
        // 2. Exécution des commandes
        // ============================

        switch (name) {

            case "server" -> {
                switch (sub) {
                    case "start" -> ServerCommands.start(event);
                    case "stop" -> ServerCommands.stop(event);
                    case "restart" -> ServerCommands.restart(event);
                    case "status" -> ServerCommands.status(event);
                }
            }

            case "player" -> {
                switch (sub) {
                    case "kick" -> ServerCommands.kick(event);
                    case "ban" -> ServerCommands.ban(event);
                    case "op" -> ServerCommands.op(event);
                    case "deop" -> ServerCommands.deop(event);
                    case null -> {}
                    default -> throw new IllegalStateException("Unexpected value: " + sub);
                }
            }

            case "sound" -> {
                assert sub != null;
                if (sub.equals("creeper")) ServerCommands.creeper(event);
            }

            case "help" -> ServerCommands.help(event);
        }
    }

}

