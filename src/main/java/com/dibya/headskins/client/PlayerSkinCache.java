package com.dibya.headskins.client;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.component.ResolvableProfile;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerSkinCache {
    private static final UUID NIL_UUID = new UUID(0L, 0L);
    private static final Map<String, CompletableFuture<Optional<PlayerSkin>>> CACHE = new ConcurrentHashMap<>();

    private PlayerSkinCache() {}

    public static PlayerSkin resolve(@Nullable ResolvableProfile profile) {
        if (profile == null) {
            return DefaultPlayerSkin.get(NIL_UUID);
        }

        GameProfile partial = profile.partialProfile();
        String textureValue = extractTextureValue(partial);

        // Offline / SkinsRestorer support: if head only has a username, check live tab list first
        if (textureValue == null && partial.name() != null && !partial.name().isEmpty()) {
            ClientPacketListener connection = Minecraft.getInstance().getConnection();
            if (connection != null) {
                PlayerInfo onlineInfo = connection.getPlayerInfo(partial.name());
                if (onlineInfo != null) {
                    return onlineInfo.getSkin();
                }
            }
        }

        String key = buildCacheKey(profile, textureValue);
        SkinManager skinManager = Minecraft.getInstance().getSkinManager();

        CompletableFuture<Optional<PlayerSkin>> future = CACHE.computeIfAbsent(key, k ->
                profile.resolveProfile(Minecraft.getInstance().services().profileResolver())
                        .thenCompose(resolved -> skinManager.get(resolved))
                        .exceptionally(ex -> Optional.empty())
        );

        Optional<PlayerSkin> loaded = future.getNow(Optional.empty());
        if (loaded.isPresent()) {
            return loaded.get();
        }

        UUID fallbackId = partial.id();
        if (fallbackId == null || fallbackId.equals(NIL_UUID)) {
            String name = (partial.name() != null && !partial.name().isEmpty()) ? partial.name() : "Steve";
            fallbackId = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
        }
        return DefaultPlayerSkin.get(fallbackId);
    }

    @Nullable
    private static String extractTextureValue(GameProfile profile) {
        Collection<Property> textures = profile.properties().get("textures");
        if (textures != null && !textures.isEmpty()) {
            return textures.iterator().next().value();
        }
        return null;
    }

    private static String buildCacheKey(ResolvableProfile profile, @Nullable String textureValue) {
        if (textureValue != null && !textureValue.isEmpty()) {
            return "tex:" + textureValue.hashCode() + ":" + textureValue.length();
        }
        GameProfile partial = profile.partialProfile();
        UUID id = partial.id();
        if (id != null && !id.equals(NIL_UUID)) {
            return "uuid:" + id;
        }
        String name = partial.name();
        if (name != null && !name.isEmpty()) {
            return "name:" + name.toLowerCase();
        }
        return "hash:" + profile.hashCode();
    }
}
