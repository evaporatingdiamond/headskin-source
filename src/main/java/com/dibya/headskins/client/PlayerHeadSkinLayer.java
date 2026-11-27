package com.dibya.headskins.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.monster.creeper.CreeperModel;
import net.minecraft.client.model.monster.piglin.PiglinModel;
import net.minecraft.client.model.monster.skeleton.SkeletonModel;
import net.minecraft.client.model.object.armorstand.ArmorStandArmorModel;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.ArmorStandRenderState;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Rotations;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.component.ResolvableProfile;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class PlayerHeadSkinLayer extends RenderLayer<ArmorStandRenderState, ArmorStandArmorModel> {
    private static final float PLAYER_CHIBI_HEAD_SCALE = 1.5F;

    private final ArmorStandPlayerModel wideModel;
    private final ArmorStandPlayerModel slimModel;
    private final Map<StandHeadType, MobStandModel> mobModels = new EnumMap<>(StandHeadType.class);

    public PlayerHeadSkinLayer(
            RenderLayerParent<ArmorStandRenderState, ArmorStandArmorModel> parent,
            EntityModelSet modelSet
    ) {
        super(parent);
        this.wideModel = new ArmorStandPlayerModel(createVanillaPlayerRoot(false), false);
        this.slimModel = new ArmorStandPlayerModel(createVanillaPlayerRoot(true), true);

        ModelPart zombieRoot = createVanillaHumanoidRoot(64, 64);
        ModelPart skeletonRoot = SkeletonModel.createBodyLayer().bakeRoot();
        ModelPart witherSkeletonRoot = SkeletonModel.createBodyLayer().bakeRoot();
        ModelPart piglinRoot = createVanillaPiglinRoot();
        ModelPart creeperRoot = CreeperModel.createBodyLayer(CubeDeformation.NONE).bakeRoot();

        this.mobModels.put(StandHeadType.ZOMBIE, new HumanoidMobModel(zombieRoot, this.wideModel, true, false));
        this.mobModels.put(StandHeadType.SKELETON, new HumanoidMobModel(skeletonRoot, this.wideModel, true, false));
        this.mobModels.put(StandHeadType.WITHER_SKELETON, new HumanoidMobModel(witherSkeletonRoot, this.wideModel, true, false));
        this.mobModels.put(StandHeadType.PIGLIN, new HumanoidMobModel(piglinRoot, this.wideModel, false, true));
        this.mobModels.put(StandHeadType.CREEPER, new CreeperStandModel(creeperRoot, this.wideModel));
    }

    private static ModelPart createVanillaPlayerRoot(boolean slim) {
        return LayerDefinition.create(PlayerModel.createMesh(CubeDeformation.NONE, slim), 64, 64).bakeRoot();
    }

    private static ModelPart createVanillaHumanoidRoot(int texWidth, int texHeight) {
        return LayerDefinition.create(HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F), texWidth, texHeight).bakeRoot();
    }

    private static ModelPart createVanillaPiglinRoot() {
        return LayerDefinition.create(PiglinModel.createMesh(CubeDeformation.NONE, 0.0F), 64, 64).bakeRoot();
    }

    @Override
    public void submit(
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            int packedLight,
            ArmorStandRenderState state,
            float yRot,
            float xRot
    ) {
        if (state.isInvisible) {
            return;
        }

        HeadSkinRenderStateExt ext = (HeadSkinRenderStateExt) state;
        StandHeadType headType = ext.headskins$getHeadType();
        if (headType == StandHeadType.NONE) {
            return;
        }

        if (headType == StandHeadType.PLAYER) {
            submitPlayerSkin(poseStack, submitNodeCollector, packedLight, state, ext.headskins$getHeadProfile());
            return;
        }

        MobStandModel mobModel = this.mobModels.get(headType);
        Identifier texture = VanillaMobTextures.resolve(headType);
        if (mobModel == null || texture == null) {
            return;
        }

        poseStack.pushPose();
        if (state.isSmall) {
            poseStack.translate(0.0F, 0.75F, 0.0F);
            poseStack.scale(0.5F, 0.5F, 0.5F);
        }
        submitNodeCollector.submitModel(
                mobModel,
                state,
                poseStack,
                this.wideModel.renderType(texture),
                packedLight,
                OverlayTexture.NO_OVERLAY,
                state.outlineColor
        );
        poseStack.popPose();
    }

    private void submitPlayerSkin(
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            int packedLight,
            ArmorStandRenderState state,
            @Nullable ResolvableProfile profile
    ) {
        PlayerSkin skin = PlayerSkinCache.resolve(profile);
        boolean isSlim = skin.model() == PlayerModelType.SLIM;
        ArmorStandPlayerModel activeModel = isSlim ? this.slimModel : this.wideModel;

        ArmorStandAvatarRenderState avatarState = new ArmorStandAvatarRenderState();
        avatarState.standState = state;
        avatarState.isBaby = false;
        avatarState.ageInTicks = state.ageInTicks;
        avatarState.yRot = 0.0F;
        avatarState.xRot = 0.0F;
        avatarState.showHat = true;
        avatarState.showJacket = true;
        avatarState.showLeftSleeve = state.showArms;
        avatarState.showRightSleeve = state.showArms;
        avatarState.showLeftPants = true;
        avatarState.showRightPants = true;

        Identifier skinTexture = skin.body().texturePath();

        poseStack.pushPose();
        if (state.isSmall) {
            poseStack.translate(0.0F, 0.75F, 0.0F);
            poseStack.scale(0.5F, 0.5F, 0.5F);
        }
        submitNodeCollector.submitModel(
                activeModel,
                avatarState,
                poseStack,
                activeModel.renderType(skinTexture),
                packedLight,
                OverlayTexture.NO_OVERLAY,
                state.outlineColor
        );
        poseStack.popPose();
    }

    private static final class ArmorStandAvatarRenderState extends AvatarRenderState {
        @Nullable
        ArmorStandRenderState standState;
    }

    private static final class ArmorStandPlayerModel extends PlayerModel {
        ArmorStandPlayerModel(ModelPart root, boolean slim) {
            super(root, slim);
        }

        @Override
        public void setupAnim(AvatarRenderState state) {
            this.resetPose();
            if (state instanceof ArmorStandAvatarRenderState standAvatar && standAvatar.standState != null) {
                ArmorStandRenderState stand = standAvatar.standState;
                applyRotation(this.head, stand.headPose);
                applyRotation(this.body, stand.bodyPose);
                applyRotation(this.leftArm, stand.leftArmPose);
                applyRotation(this.rightArm, stand.rightArmPose);
                applyRotation(this.leftLeg, stand.leftLegPose);
                applyRotation(this.rightLeg, stand.rightLegPose);

                float headScale = stand.isSmall ? PLAYER_CHIBI_HEAD_SCALE : 1.0F;
                applyHeadScale(this.head, headScale);
                applyHeadScale(this.hat, headScale);

                this.leftArm.visible = stand.showArms;
                this.rightArm.visible = stand.showArms;
            }
        }
    }

    private abstract static class MobStandModel extends Model<ArmorStandRenderState> {
        protected MobStandModel(ModelPart root, PlayerModel renderTypeSource) {
            super(root, renderTypeSource::renderType);
        }
    }

    private static final class HumanoidMobModel extends MobStandModel {
        private final ModelPart head;
        private final ModelPart body;
        private final ModelPart leftArm;
        private final ModelPart rightArm;
        private final ModelPart leftLeg;
        private final ModelPart rightLeg;

        HumanoidMobModel(
                ModelPart root,
                PlayerModel renderTypeSource,
                boolean hideHatLayer,
                boolean hidePlayerOverlayParts
        ) {
            super(root, renderTypeSource);
            this.head = root.getChild("head");
            this.body = root.getChild("body");
            this.leftArm = root.getChild("left_arm");
            this.rightArm = root.getChild("right_arm");
            this.leftLeg = root.getChild("left_leg");
            this.rightLeg = root.getChild("right_leg");

            if (hideHatLayer) {
                hideIfPresent(this.head, "hat");
            }
            if (hidePlayerOverlayParts) {
                hideIfPresent(root, "jacket");
                hideIfPresent(root, "left_sleeve");
                hideIfPresent(root, "right_sleeve");
                hideIfPresent(root, "left_pants");
                hideIfPresent(root, "right_pants");
            }
        }

        private static void hideIfPresent(ModelPart parent, String childName) {
            if (parent.hasChild(childName)) {
                parent.getChild(childName).visible = false;
            }
        }

        @Override
        public void setupAnim(ArmorStandRenderState state) {
            this.resetPose();
            applyRotation(this.head, state.headPose);
            applyRotation(this.body, state.bodyPose);
            applyRotation(this.leftArm, state.leftArmPose);
            applyRotation(this.rightArm, state.rightArmPose);
            applyRotation(this.leftLeg, state.leftLegPose);
            applyRotation(this.rightLeg, state.rightLegPose);

            this.leftArm.visible = state.showArms;
            this.rightArm.visible = state.showArms;
        }
    }

    private static final class CreeperStandModel extends MobStandModel {
        private final ModelPart head;
        private final ModelPart body;
        private final ModelPart leftHindLeg;
        private final ModelPart rightHindLeg;
        private final ModelPart leftFrontLeg;
        private final ModelPart rightFrontLeg;

        CreeperStandModel(ModelPart root, PlayerModel renderTypeSource) {
            super(root, renderTypeSource);
            this.head = root.getChild("head");
            this.body = root.getChild("body");
            this.leftHindLeg = root.getChild("left_hind_leg");
            this.rightHindLeg = root.getChild("right_hind_leg");
            this.leftFrontLeg = root.getChild("left_front_leg");
            this.rightFrontLeg = root.getChild("right_front_leg");
        }

        @Override
        public void setupAnim(ArmorStandRenderState state) {
            this.resetPose();
            applyRotation(this.head, state.headPose);
            applyRotation(this.body, state.bodyPose);
            applyRotation(this.leftHindLeg, state.leftLegPose);
            applyRotation(this.rightHindLeg, state.rightLegPose);
            applyRotation(this.leftFrontLeg, state.leftArmPose);
            applyRotation(this.rightFrontLeg, state.rightArmPose);
        }
    }

    private static void applyRotation(ModelPart part, Rotations rot) {
        part.xRot = rot.x() * Mth.DEG_TO_RAD;
        part.yRot = rot.y() * Mth.DEG_TO_RAD;
        part.zRot = rot.z() * Mth.DEG_TO_RAD;
    }

    private static void applyHeadScale(ModelPart part, float scale) {
        part.xScale = scale;
        part.yScale = scale;
        part.zScale = scale;
    }
}
