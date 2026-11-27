package com.dibya.headskins.config;

import com.dibya.headskins.HeadSkinsMod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class HeadSkinsConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("headskins.json");

    public static class Data {
        public boolean resolveOnlinePlayerSkins = true;
        public boolean resolveMojangProfiles = true;
        public boolean enableMobHeads = true;
    }

    private static Data instance = new Data();

    private HeadSkinsConfig() {}

    public static Data get() {
        return instance;
    }

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                Data loaded = GSON.fromJson(reader, Data.class);
                if (loaded != null) {
                    instance = loaded;
                }
            } catch (Exception e) {
                HeadSkinsMod.LOGGER.warn("Failed to read headskins.json, using defaults: {}", e.getMessage());
            }
        }
        save();
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(instance, writer);
            }
        } catch (Exception e) {
            HeadSkinsMod.LOGGER.warn("Failed to save headskins.json: {}", e.getMessage());
        }
    }
}
