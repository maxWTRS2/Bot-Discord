package org.example.config;

public class MinecraftConfig {

    private String ip;
    private int port;
    private int protocol;
    private String dockerContainer;

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public int getProtocol() {
        return protocol;
    }

    public String getDockerContainer() {
        return dockerContainer;
    }
}

