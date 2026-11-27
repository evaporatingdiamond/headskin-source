package com.dibya.headskins.mixin;

import com.dibya.headskins.client.HeadSkinRenderStateExt;
import com.dibya.headskins.client.StandHeadType;
import net.minecraft.client.renderer.entity.state.ArmorStandRenderState;
import net.minecraft.world.item.component.ResolvableProfile;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ArmorStandRenderState.class)
public class ArmorStandRenderStateMixin implements HeadSkinRenderStateExt {
    @Unique
    private StandHeadType headskins$headType = StandHeadType.NONE;

    @Unique
    @Nullable
    private ResolvableProfile headskins$headProfile;

    @Override
    public StandHeadType headskins$getHeadType() {
        return this.headskins$headType;
    }

    @Override
    public void headskins$setHeadType(StandHeadType headType) {
        this.headskins$headType = headType != null ? headType : StandHeadType.NONE;
    }

    @Override
    @Nullable
    public ResolvableProfile headskins$getHeadProfile() {
        return this.headskins$headProfile;
    }

    @Override
    public void headskins$setHeadProfile(@Nullable ResolvableProfile profile) {
        this.headskins$headProfile = profile;
    }
}
