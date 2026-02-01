package org.example.utils;

import java.io.IOException;
import java.io.InputStream;

public class ProcessUtils {

    public static String exec(String... command) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        Process p = pb.start();

        InputStream in = p.getInputStream();
        byte[] bytes = in.readAllBytes();
        return new String(bytes);
    }
}
