package com.dibya.headskins.client;

import net.minecraft.world.item.component.ResolvableProfile;
import org.jetbrains.annotations.Nullable;

public interface HeadSkinRenderStateExt {
    StandHeadType headskins$getHeadType();

    void headskins$setHeadType(StandHeadType headType);

    default boolean headskins$hasCustomBody() {
        return headskins$getHeadType() != StandHeadType.NONE;
    }

    @Nullable
    ResolvableProfile headskins$getHeadProfile();

    void headskins$setHeadProfile(@Nullable ResolvableProfile profile);
}
