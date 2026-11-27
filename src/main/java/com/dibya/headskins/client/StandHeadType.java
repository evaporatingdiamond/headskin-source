package com.dibya.headskins.client;

import com.dibya.headskins.config.HeadSkinsConfig;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

public enum StandHeadType {
    NONE(null),
    PLAYER(null),
    ZOMBIE(Identifier.withDefaultNamespace("textures/entity/zombie/zombie.png")),
    SKELETON(Identifier.withDefaultNamespace("textures/entity/skeleton/skeleton.png")),
    WITHER_SKELETON(Identifier.withDefaultNamespace("textures/entity/skeleton/wither_skeleton.png")),
    PIGLIN(Identifier.withDefaultNamespace("textures/entity/piglin/piglin.png")),
    CREEPER(Identifier.withDefaultNamespace("textures/entity/creeper/creeper.png"));

    @Nullable
    private final Identifier mobTexture;

    StandHeadType(@Nullable Identifier mobTexture) {
        this.mobTexture = mobTexture;
    }

    @Nullable
    public Identifier getMobTexture() {
        return this.mobTexture;
    }

    public boolean isMob() {
        return this != NONE && this != PLAYER;
    }

    public static StandHeadType fromHeadStack(ItemStack stack) {
        if (stack.isEmpty()) {
            return NONE;
        }
        if (stack.is(Items.PLAYER_HEAD)) {
            return PLAYER;
        }
        if (!HeadSkinsConfig.get().enableMobHeads) {
            return NONE;
        }
        if (stack.is(Items.ZOMBIE_HEAD)) {
            return ZOMBIE;
        }
        if (stack.is(Items.SKELETON_SKULL)) {
            return SKELETON;
        }
        if (stack.is(Items.WITHER_SKELETON_SKULL)) {
            return WITHER_SKELETON;
        }
        if (stack.is(Items.PIGLIN_HEAD)) {
            return PIGLIN;
        }
        if (stack.is(Items.CREEPER_HEAD)) {
            return CREEPER;
        }
        return NONE;
    }
}
