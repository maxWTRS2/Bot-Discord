package org.example.config;

public class MinecraftConfig {

    private String ip;
    private int port;
    private int rconPort;
    private int protocol;
    private String dockerContainer;
    private String rconPassword;

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public int getRconPort() {
        return rconPort;
    }

    public String getRconPassword() {
        return rconPassword;
    }

    public int getProtocol() {
        return protocol;
    }

    public String getDockerContainer() {
        return dockerContainer;
    }
}

