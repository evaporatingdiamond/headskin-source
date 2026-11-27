package com.dibya.headskins.mixin;

import com.dibya.headskins.client.HeadSkinRenderStateExt;
import net.minecraft.client.model.object.armorstand.ArmorStandArmorModel;
import net.minecraft.client.model.object.armorstand.ArmorStandModel;
import net.minecraft.client.renderer.entity.state.ArmorStandRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorStandModel.class)
public abstract class ArmorStandModelMixin extends ArmorStandArmorModel {
    protected ArmorStandModelMixin(net.minecraft.client.model.geom.ModelPart root) {
        super(root);
    }

    @Inject(
            method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/ArmorStandRenderState;)V",
            at = @At("HEAD")
    )
    private void headskins$restoreFrameVisibility(ArmorStandRenderState state, CallbackInfo ci) {
        this.root().getAllParts().forEach(part -> part.visible = true);
    }

    @Inject(
            method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/ArmorStandRenderState;)V",
            at = @At("TAIL")
    )
    private void headskins$hideWoodenFrameUniversally(ArmorStandRenderState state, CallbackInfo ci) {
        if (state instanceof HeadSkinRenderStateExt ext && ext.headskins$hasCustomBody()) {
            this.root().getAllParts().forEach(part -> part.visible = false);
        }
    }
}
