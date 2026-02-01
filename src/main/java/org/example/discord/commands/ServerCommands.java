package org.example.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.example.minecraft.MinecraftServerManager;

public class ServerCommands {

    public static void start(SlashCommandInteractionEvent event) {
        try {
            event.reply("🟢 Démarrage du serveur...").queue();
            MinecraftServerManager.start();
            event.getHook().sendMessage("✅ Serveur démarré !").queue();
        } catch (Exception e) {
            event.getHook().sendMessage("❌ Impossible de démarrer le serveur : " + e.getMessage()).queue();
        }
    }

    public static void stop(SlashCommandInteractionEvent event) {
        try {
            event.reply("🛑 Arrêt du serveur...").queue();
            MinecraftServerManager.stop();
            event.getHook().sendMessage("✅ Serveur arrêté !").queue();
        } catch (Exception e) {
            event.getHook().sendMessage("❌ Impossible d'arrêter le serveur : " + e.getMessage()).queue();
        }
    }

    public static void status(SlashCommandInteractionEvent event) {
        String status = MinecraftServerManager.fetchStatusFormatted();
        event.reply(status).queue();
    }

    public static void restart(SlashCommandInteractionEvent event) {
        try {
            event.reply("🔄 Redémarrage du serveur...").queue();
            MinecraftServerManager.restart();
            event.getHook().sendMessage("✅ Serveur redémarré !").queue();
        } catch (Exception e) {
            event.getHook().sendMessage("❌ Impossible de redémarrer le serveur : " + e.getMessage()).queue();
        }
    }

    public static void kick(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue(); // réponse différée éphémère

        new Thread(() -> {
            try {
                String player = event.getOption("joueur").getAsString();
                String reason = event.getOption("raison") != null ? event.getOption("raison").getAsString() : "Kick via Discord";
                System.out.println(">>> DISCORD: commande kick reçue"); System.out.flush();
                MinecraftServerManager.kickPlayer(player, reason);

                event.getHook().sendMessage("✅ Joueur " + player + " kické (" + reason + ")").queue();
            } catch (Exception e) {
                event.getHook().sendMessage("❌ Impossible de kicker le joueur : " + e.getMessage()).queue();
            }
        }).start();
    }


    public static void ban(SlashCommandInteractionEvent event) {
        try {
            String player = event.getOption("joueur").getAsString();
            String reason = event.getOption("raison") != null ? event.getOption("raison").getAsString() : "Banni via Discord";

            MinecraftServerManager.banPlayer(player, reason);

            event.reply("✅ Joueur **" + player + "** banni (" + reason + ")").queue();
        } catch (Exception e) {
            event.reply("❌ Impossible de bannir le joueur : " + e.getMessage()).queue();
        }
    }

    public static void op(SlashCommandInteractionEvent event) {
        try {
            String player = event.getOption("joueur").getAsString();

            MinecraftServerManager.giveOp(player);

            event.reply("✅ Joueur **" + player + "** est maintenant opérateur").queue();
        } catch (Exception e) {
            event.reply("❌ Impossible de donner les droits OP : " + e.getMessage()).queue();
        }
    }

    public static void deop(SlashCommandInteractionEvent event) {
        try {
            String player = event.getOption("joueur").getAsString();

            MinecraftServerManager.removeOp(player);

            event.reply("✅ Joueur **" + player + "** n'est plus opérateur").queue();
        } catch (Exception e) {
            event.reply("❌ Impossible de retirer les droits OP : " + e.getMessage()).queue();
        }
    }

    public static void creeper(SlashCommandInteractionEvent event) {
        try {
            String player = event.getOption("joueur").getAsString();

            MinecraftServerManager.playCreeperSound(player);

            event.reply("✅ Son de creeper joué sur " + player).setEphemeral(true).queue();
        } catch (Exception e) {
            event.reply("❌ Impossible de retirer les droits OP : " + e.getMessage()).queue();
        }
    }

    public static void help(SlashCommandInteractionEvent event) {
        String helpMessage = """
                **📘 Commandes disponibles**
                **/server start** — Démarre le serveur
                **/server stop** — Arrête le serveur *(admin)*
                **/server restart** — Redémarre le serveur *(admin)*
                **/server status** — Affiche le statut du serveur
                
                **/player kick <joueur> [raison]** — Kick un joueur *(admin)*
                **/player ban <joueur> [raison]** — Ban un joueur *(admin)*
                **/player op <joueur>** — Donne les permissions OP *(admin)*
                **/player deop <joueur>** — Retire les permissions OP *(admin)*
                
                **/sound creeper <joueur>** — Joue un son de creeper *(admin)*
                """;
        event.reply(helpMessage).setEphemeral(true).queue();
    }
}
