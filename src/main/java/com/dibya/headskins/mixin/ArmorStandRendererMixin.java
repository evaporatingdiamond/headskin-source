package com.dibya.headskins.mixin;

import com.dibya.headskins.client.HeadSkinRenderStateExt;
import com.dibya.headskins.client.PlayerHeadSkinLayer;
import com.dibya.headskins.client.StandHeadType;
import net.minecraft.client.model.object.armorstand.ArmorStandArmorModel;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.ArmorStandRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorStandRenderer.class)
public abstract class ArmorStandRendererMixin
        extends LivingEntityRenderer<ArmorStand, ArmorStandRenderState, ArmorStandArmorModel> {

    protected ArmorStandRendererMixin(
            EntityRendererProvider.Context context,
            ArmorStandArmorModel model,
            float shadowRadius
    ) {
        super(context, model, shadowRadius);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void headskins$addSkinLayer(EntityRendererProvider.Context context, CallbackInfo ci) {
        this.addLayer(new PlayerHeadSkinLayer(this, context.getModelSet()));
    }

    @Inject(
            method = "extractRenderState(Lnet/minecraft/world/entity/decoration/ArmorStand;Lnet/minecraft/client/renderer/entity/state/ArmorStandRenderState;F)V",
            at = @At("TAIL")
    )
    private void headskins$extractHeadProfileAndRotation(
            ArmorStand entity,
            ArmorStandRenderState state,
            float partialTicks,
            CallbackInfo ci
    ) {
        // Keep client body/head rotation locked to entity yRot so Armor Poser rotation updates immediately
        float yaw = entity.getYRot();
        entity.yBodyRot = yaw;
        entity.yBodyRotO = yaw;
        entity.yHeadRot = yaw;
        entity.yHeadRotO = yaw;
        state.bodyRot = yaw;
        state.yRot = 0.0F;

        ItemStack headStack = entity.getItemBySlot(EquipmentSlot.HEAD);
        HeadSkinRenderStateExt ext = (HeadSkinRenderStateExt) state;
        StandHeadType headType = StandHeadType.fromHeadStack(headStack);

        ext.headskins$setHeadType(headType);
        if (headType == StandHeadType.PLAYER) {
            ext.headskins$setHeadProfile(headStack.get(DataComponents.PROFILE));
        } else {
            ext.headskins$setHeadProfile(null);
        }
    }
}
