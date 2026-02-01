package org.example.discord;

import net.dv8tion.jda.api.JDA;
//import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class CommandRegistrar {

    public static void register(JDA jda/*, String guildId*/) {

//        Guild guild = jda.getGuildById(guildId);
//
//        if (guild == null) {
//            throw new IllegalStateException("❌ Guild introuvable : " + guildId);
//        }

        jda.updateCommands().addCommands(
                Commands.slash("server", "Gestion du serveur Minecraft")
                        .addSubcommands(
                                new SubcommandData("start", "Démarre le serveur Minecraft"),
                                new SubcommandData("stop", "Arrête le serveur Minecraft"),
                                new SubcommandData("restart", "Redémarre le serveur Minecraft"),
                                new SubcommandData("status", "Affiche le statut du serveur Minecraft")
                        ),
                Commands.slash("player", "Gestion des joueurs du serveur Minecraft")
                        .addSubcommands(
                                new SubcommandData("kick", "Kick ce joueur du serveur")
                                        .addOption(OptionType.STRING, "joueur", "Nom du joueur à kicker", true)
                                        .addOption(OptionType.STRING, "raison", "Raison du kick", false),

                                new SubcommandData("ban", "Ban quelqu'un du serveur")
                                        .addOption(OptionType.STRING, "joueur", "Nom du joueur à bannir", true)
                                        .addOption(OptionType.STRING, "raison", "Raison du ban", false),
                                new SubcommandData("deban", "déban quelqu'un du serveur")
                                        .addOption(OptionType.STRING, "joueur", "Nom du joueur à bannir", true),

                                new SubcommandData("op", "Donne les perms du serveur Minecraft")
                                        .addOption(OptionType.STRING, "joueur", "Nom du joueur à op", true),

                                new SubcommandData("deop", "Enlève les perms du serveur Minecraft")
                                        .addOption(OptionType.STRING, "joueur", "Nom du joueur à deop", true)
                        ),
                Commands.slash("sound", "Gestion des sons a jouer en jeu")
                        .addSubcommands(
                                new SubcommandData("creeper", "Joue le son d'un creeper sur un joueur")
                                        .addOption(OptionType.STRING, "joueur", "Nom du joueur cible", true)
                        ),
                Commands.slash("help", "Affiche la liste des commandes disponibles")
        ).queue();
    }
}
