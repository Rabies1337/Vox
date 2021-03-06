package dev.rabies.vox.mixins;

import com.google.common.base.Predicates;
import dev.rabies.vox.VoxMod;
import dev.rabies.vox.cheats.cheats.HitBoxCheat;
import dev.rabies.vox.cheats.cheats.ReachCheat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.RandomUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {

    @Shadow
    @Final
    private Minecraft mc;
    @Shadow
    private Entity pointedEntity;

    @Inject(method = "getMouseOver", at = @At("HEAD"), cancellable = true)
    public void getMouseOver(float partialTicks, CallbackInfo ci) {
        ReachCheat reachCheat = (ReachCheat) VoxMod.get().getCheatByName("Reach");
        if (reachCheat == null || !reachCheat.isEnabled()) return;
        double reach = reachCheat.likeLegitSetting.getValue() ?
                RandomUtils.nextDouble(3.08, 3.655) :
                reachCheat.reachSetting.getValue();

        HitBoxCheat hitBoxCheat = (HitBoxCheat) VoxMod.get().getCheatByName("Hitbox");
        if (hitBoxCheat != null && hitBoxCheat.isEnabled()) {
            reach -= hitBoxCheat.sizeSetting.getValue();
            if (reach < 3.1)
                reach = 3.1;
        }

        ci.cancel();

        Entity entity = mc.getRenderViewEntity();
        if (entity != null) {
            if (mc.world != null) {
                mc.profiler.startSection("pick");
                mc.pointedEntity = null;
                double d0 = mc.playerController.getBlockReachDistance();
                mc.objectMouseOver = entity.rayTrace(d0, partialTicks);
                Vec3d vec3d = entity.getPositionEyes(partialTicks);
                boolean flag = false;
                double d1 = d0;

                if (mc.playerController.extendedReach()) {
                    d1 = 6.0D;
                    d0 = d1;
                } else if (d0 > reach) {
                    flag = true;
                }

                if (mc.objectMouseOver != null) {
                    d1 = mc.objectMouseOver.hitVec.distanceTo(vec3d);
                }

                Vec3d vec3d1 = entity.getLook(1.0F);
                Vec3d vec3d2 = vec3d.add(vec3d1.x * d0, vec3d1.y * d0, vec3d1.z * d0);
                pointedEntity = null;
                Vec3d vec3d3 = null;
                List<Entity> list = mc.world.getEntitiesInAABBexcluding(entity,
                        entity.getEntityBoundingBox()
                                .expand(vec3d1.x * d0, vec3d1.y * d0, vec3d1.z * d0)
                                .grow(1.0D, 1.0D, 1.0D),
                        Predicates.and(EntitySelectors.NOT_SPECTATING, p_apply_1_ -> p_apply_1_ != null && p_apply_1_.canBeCollidedWith()));
                double d2 = d1;

                for (int j = 0; j < list.size(); ++j) {
                    Entity entity1 = list.get(j);
                    AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow((double) entity1.getCollisionBorderSize());
                    RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(vec3d, vec3d2);

                    if (axisalignedbb.contains(vec3d)) {
                        if (d2 >= 0.0D) {
                            pointedEntity = entity1;
                            vec3d3 = raytraceresult == null ? vec3d : raytraceresult.hitVec;
                            d2 = 0.0D;
                        }
                    } else if (raytraceresult != null) {
                        double d3 = vec3d.distanceTo(raytraceresult.hitVec);

                        if (d3 < d2 || d2 == 0.0D) {
                            if (entity1.getLowestRidingEntity() == entity.getLowestRidingEntity() && !entity1.canRiderInteract()) {
                                if (d2 == 0.0D) {
                                    pointedEntity = entity1;
                                    vec3d3 = raytraceresult.hitVec;
                                }
                            } else {
                                pointedEntity = entity1;
                                vec3d3 = raytraceresult.hitVec;
                                d2 = d3;
                            }
                        }
                    }
                }

                if (pointedEntity != null && flag && vec3d.distanceTo(vec3d3) > reach) {
                    pointedEntity = null;
                    mc.objectMouseOver = new RayTraceResult(RayTraceResult.Type.MISS, vec3d3, null, new BlockPos(vec3d3));
                }

                if (pointedEntity != null && (d2 < d1 || mc.objectMouseOver == null)) {
                    mc.objectMouseOver = new RayTraceResult(pointedEntity, vec3d3);

                    if (pointedEntity instanceof EntityLivingBase || pointedEntity instanceof EntityItemFrame) {
                        mc.pointedEntity = pointedEntity;
                    }
                }

                mc.profiler.endSection();
            }
        }
    }
}
