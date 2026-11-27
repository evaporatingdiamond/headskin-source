package com.dibya.headskins.server;

import com.dibya.headskins.config.HeadSkinsConfig;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;

import java.util.UUID;

public final class ServerSkinManager {
    private ServerSkinManager() {}

    public static void onHeadEquipped(ArmorStand armorStand, ItemStack headStack) {
        if (!(armorStand.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        if (!headStack.is(Items.PLAYER_HEAD)) {
            return;
        }

        ResolvableProfile profile = headStack.get(DataComponents.PROFILE);
        if (profile == null) {
            return;
        }

        GameProfile partial = profile.partialProfile();
        // Keep embedded Base64 "textures" properties (e.g., SkinsRestorer /skull or custom heads) intact
        if (!partial.properties().get("textures").isEmpty()) {
            return;
        }

        MinecraftServer server = serverLevel.getServer();
        String targetName = partial.name();

        // 1. Offline / SkinsRestorer support: pull live textures from matching online player
        if (HeadSkinsConfig.get().resolveOnlinePlayerSkins && targetName != null && !targetName.isEmpty()) {
            ServerPlayer onlinePlayer = server.getPlayerList().getPlayerByName(targetName);
            if (onlinePlayer != null) {
                GameProfile liveProfile = onlinePlayer.getGameProfile();
                if (!liveProfile.properties().get("textures").isEmpty()) {
                    ItemStack updatedHead = headStack.copy();
                    updatedHead.set(DataComponents.PROFILE, ResolvableProfile.createResolved(liveProfile));
                    armorStand.setItemSlot(EquipmentSlot.HEAD, updatedHead);
                    return;
                }
            }
        }

        // 2. Otherwise resolve via Mojang's profile resolver asynchronously
        if (HeadSkinsConfig.get().resolveMojangProfiles) {
            UUID standUuid = armorStand.getUUID();
            profile.resolveProfile(server.services().profileResolver()).thenAccept(resolved -> {
                if (resolved.properties().get("textures").isEmpty()) {
                    return;
                }
                server.execute(() -> {
                    if (!armorStand.isAlive() || !armorStand.getUUID().equals(standUuid)) {
                        return;
                    }
                    ItemStack currentHead = armorStand.getItemBySlot(EquipmentSlot.HEAD);
                    if (currentHead.is(Items.PLAYER_HEAD)) {
                        ItemStack updatedHead = currentHead.copy();
                        updatedHead.set(DataComponents.PROFILE, ResolvableProfile.createResolved(resolved));
                        armorStand.setItemSlot(EquipmentSlot.HEAD, updatedHead);
                    }
                });
            });
        }
    }
}
