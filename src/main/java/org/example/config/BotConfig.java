package org.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class BotConfig {

    private String token;
    private String guildId;
    private MinecraftConfig minecraft;
    private static BotConfig instance;
    private List<String> adminRoles;

    public static BotConfig getInstance() {
        if (instance == null) {
            instance = load("config.json");
        }
        return instance;
    }

    public String getToken() {
        return token;
    }

    public String getGuildId() {
        return guildId;
    }

    public List<String> getAdminRoles() {
        return adminRoles; // ou le nom exact de ton champ
    }

    public MinecraftConfig getMinecraft() {
        return minecraft;
    }

    public static BotConfig load(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                throw new RuntimeException("❌ Fichier de config introuvable : " + path);
            }

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(file, BotConfig.class);

        } catch (IOException e) {
            throw new RuntimeException("❌ Erreur lecture config : " + e.getMessage(), e);
        }
    }
}
