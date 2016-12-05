package xyz.hexagons.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.Serializable;
import java.security.SecureRandom;

public class Settings implements Serializable {
    public static final Settings instance;
    public final String siteRedir = "http://10.1.3.1:9000";

    public final String dbAddress = "10.1.0.1";
    public final String dbDatabase = "hexagons";
    public final String dbUser = "hexagons";
    public final String dbPass = "";
    public final byte[] signSecret = new byte[32];

    static {
        Settings s = null;
        try {
            Gson gson = new GsonBuilder().create();
            File file = new File("settings.json");
            if(!file.exists()) {
                s = new Settings();
                new SecureRandom().nextBytes(s.signSecret);
            } else
                s = gson.fromJson(new FileReader(file), Settings.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        instance = s;
    }
}