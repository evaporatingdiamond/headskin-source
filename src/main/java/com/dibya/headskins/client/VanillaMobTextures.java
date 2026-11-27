package com.dibya.headskins.client;

import com.dibya.headskins.HeadSkinsMod;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;

public final class VanillaMobTextures {
    private static final Map<StandHeadType, Identifier> CLEAN_TEXTURES = new EnumMap<>(StandHeadType.class);

    private VanillaMobTextures() {}

    public static Identifier resolve(StandHeadType headType) {
        Identifier fallback = headType.getMobTexture();
        if (fallback == null) {
            return Identifier.withDefaultNamespace("textures/entity/zombie/zombie.png");
        }
        Identifier cached = CLEAN_TEXTURES.get(headType);
        if (cached != null) {
            return cached;
        }

        Minecraft client = Minecraft.getInstance();
        try {
            // Read directly from Minecraft's classpath JAR (/assets/minecraft/textures/...)
            // so active resource packs (Fresh Animations, Joyful Motions) cannot override it.
            String assetPath = "/assets/" + fallback.getNamespace() + "/" + fallback.getPath();
            InputStream stream = Minecraft.class.getResourceAsStream(assetPath);
            if (stream == null) {
                stream = VanillaMobTextures.class.getClassLoader().getResourceAsStream(assetPath.substring(1));
            }
            if (stream != null) {
                try (InputStream in = stream) {
                    NativeImage image = NativeImage.read(in);
                    Identifier dynamicId = Identifier.fromNamespaceAndPath(
                            HeadSkinsMod.MOD_ID,
                            "vanilla_mob/" + headType.name().toLowerCase()
                    );
                    DynamicTexture texture = new DynamicTexture(() -> "headskins_" + headType.name().toLowerCase(), image);
                    client.getTextureManager().register(dynamicId, texture);
                    CLEAN_TEXTURES.put(headType, dynamicId);
                    return dynamicId;
                }
            }
        } catch (Exception e) {
            HeadSkinsMod.LOGGER.warn("Failed to extract vanilla texture for {}, falling back to resource pack texture.", headType, e);
        }

        CLEAN_TEXTURES.put(headType, fallback);
        return fallback;
    }
}
