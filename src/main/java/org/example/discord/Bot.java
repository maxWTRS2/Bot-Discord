package org.example.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.example.config.BotConfig;
import org.example.discord.listeners.ServerCommandListener;

public class Bot {

    private static JDA jda;
    private static BotConfig config;

    public static void start() throws Exception {
        config = BotConfig.load("config.json");

        jda = JDABuilder
                .createDefault(config.getToken())
                .addEventListeners(new ServerCommandListener())
                .build();

        jda.awaitReady();

        CommandRegistrar.register(jda, config.getGuildId());
    }

    public static JDA getJda() {
        return jda;
    }

    public static BotConfig getConfig() {
        return config;
    }
}
