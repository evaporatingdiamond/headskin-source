package com.dibya.headskins;

import com.dibya.headskins.command.HeadSkinsCommands;
import com.dibya.headskins.config.HeadSkinsConfig;
import com.dibya.headskins.server.ServerSkinManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeadSkinsMod implements ModInitializer {
    public static final String MOD_ID = "headskins";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        HeadSkinsConfig.load();

        ServerEntityEvents.EQUIPMENT_CHANGE.register((livingEntity, equipmentSlot, previousStack, currentStack) -> {
            if (livingEntity instanceof ArmorStand armorStand && equipmentSlot == EquipmentSlot.HEAD) {
                ServerSkinManager.onHeadEquipped(armorStand, currentStack);
            }
        });

        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof ArmorStand armorStand) {
                float yaw = armorStand.getYRot();
                armorStand.setYBodyRot(yaw);
                armorStand.setYHeadRot(yaw);
                ServerSkinManager.onHeadEquipped(armorStand, armorStand.getItemBySlot(EquipmentSlot.HEAD));
            }
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                HeadSkinsCommands.register(dispatcher)
        );

        LOGGER.info("ArmorPoser Head Skins (Player + Mob Heads, Client + Server) initialized.");
    }
}
