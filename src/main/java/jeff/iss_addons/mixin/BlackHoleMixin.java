package jeff.iss_addons.mixin;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.black_hole.BlackHole;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import jeff.iss_addons.JeffsISSAddons;
import jeff.iss_addons.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(BlackHole.class)
public abstract class BlackHoleMixin extends Projectile
{
    protected BlackHoleMixin(EntityType<? extends Projectile> entityType, Level level)
    {
        super(entityType, level);
    }

    @Shadow
    public abstract float getRadius();

    @Shadow
    List<Entity> trackingEntities = new ArrayList<>();

    @Shadow
    private float damage;

    @Shadow
    private int duration;

    @Shadow
    private static final int loopSoundDurationInTicks = 40;

    @Shadow
    private void updateTrackingEntities() {}

    @Unique
    private Vec3 jeffsissaddons$_up;

    @Unique
    private static final EntityDataAccessor<Vector3f> jeffsissaddons$d_up = SynchedEntityData.defineId(BlackHole.class, EntityDataSerializers.VECTOR3);

    @Unique
    protected Vec3 jeffsissaddons$up()
    {
        return new Vec3(getEntityData().get(jeffsissaddons$d_up));
    }

    @Inject(method= "<init>*", at=@At("RETURN"))
    protected void jeffsissaddons$setUp(CallbackInfo ci)
    {
        if (getOwner() == null)
        {
            jeffsissaddons$_up = new Vec3(0, 1, 0);
        }
        else
        {
            var random = getOwner().getRandom();
            jeffsissaddons$_up = new Vec3(random.nextFloat(), random.nextFloat(), random.nextFloat()).normalize();
        }
    }

    @Inject(method="defineSynchedData", at=@At("HEAD"))
    protected void jeffsissaddons$defineSyncedData(SynchedEntityData.Builder pBuilder, CallbackInfo ci)
    {
        pBuilder.define(jeffsissaddons$d_up, new Vector3f(0, 1,0));
    }

    @Inject(method="addAdditionalSaveData", at=@At("HEAD"))
    protected void jeffsissaddons$addAdditionalSaveData(CompoundTag pCompound, CallbackInfo ci)
    {
        pCompound.putDouble("jeffsissaddons$upx", jeffsissaddons$_up.x);
        pCompound.putDouble("jeffsissaddons$upy", jeffsissaddons$_up.y);
        pCompound.putDouble("jeffsissaddons$upz", jeffsissaddons$_up.z);
    }

    @Inject(method="readAdditionalSaveData", at=@At("HEAD"))
    protected void jeffsissaddons$readAdditionalSaveData(CompoundTag pCompound, CallbackInfo ci)
    {
        jeffsissaddons$_up = new Vec3(pCompound.getDouble("jeffsissaddons$upx"), pCompound.getDouble("jeffsissaddons$upy"), pCompound.getDouble("jeffsissaddons$upz"));
    }

    @Inject(method="setRadius", at=@At("HEAD"))
    protected void jeffsissaddons$setRadius(float radius, CallbackInfo ci)
    {
        if(!level().isClientSide())
        {
            getEntityData().set(jeffsissaddons$d_up, jeffsissaddons$_up.toVector3f());
        }
    }

    @Inject(method="tick", at=@At("HEAD"), cancellable = true)
    public void jeffsissaddons$tick(CallbackInfo ci)
    {
        if (!JeffsISSAddons._configServer._blackHoleEnable.get())
        {
            return;
        }
        ci.cancel();
        super.tick();
        var bb = this.getBoundingBox();
        float radius = (float) (bb.getXsize());
        if (JeffsISSAddons._configServer._blackHoleDamageEveryTicks.get() < 1)
        {
            JeffsISSAddons._configServer._blackHoleDamageEveryTicks.set(1);
            JeffsISSAddons._configServer._blackHoleDamageEveryTicks.save();
        }
        boolean hitTick = this.tickCount % JeffsISSAddons._configServer._blackHoleDamageEveryTicks.get() == 0;
        Vec3 center = bb.getCenter();
        var up = jeffsissaddons$up();
        if (ServerConfigs.SPELL_GREIFING.get() && !level().isClientSide())
        {
            var dir = new Vec3(1, 0, -up.x/up.z).normalize();
            int upper = JeffsISSAddons._configServer._blackHoleBlockSpread.get();
            var theta = Math.toRadians(360.0/(double)upper);
            for (int i = 0; i < upper; ++i)
            {
                dir = Util.rotateVec3(dir, up, theta);
                BlockHitResult hitResult;
                int ii = 0;
                double randomness = JeffsISSAddons._configServer._blackHoleBlockRandomnessStart.get();
                var clampValue = radius * JeffsISSAddons._configServer._blackHoleBlockRadiusMultiplier.get();
                do
                {
                    var dst = center.add(dir.add(Utils.getRandomVec3(1).normalize().scale(randomness)).scale(clampValue));
                    Util.clampVec3(dst, clampValue);
                    hitResult = Utils.raycastForBlock(level(), center, dst, ClipContext.Fluid.NONE);
                    ++ii;
                    randomness += JeffsISSAddons._configServer._blackHoleBlockRandomnessAdder.get();
                }
                while(ii <= JeffsISSAddons._configServer._blackHoleBlockFailRetryCount.get() && hitResult.getType() == HitResult.Type.MISS);
                if (hitResult.getType() == HitResult.Type.MISS)
                {
                    continue;
                }
                var blockpos = hitResult.getBlockPos();
                if (level().getBlockEntity(blockpos) == null)
                {
                    var blockState = level().getBlockState(blockpos);
                    if (JeffsISSAddons._configServer._blackHoleDisallowedBlocks.get().contains(blockState.getBlockHolder().getRegisteredName()))
                    {
                        continue;
                    }
                    FallingBlockEntity fallingBlockEntity = FallingBlockEntity.fall(level(), blockpos, blockState);
                    fallingBlockEntity.setDeltaMovement(dir.scale(-.1));
                    level().addFreshEntity(fallingBlockEntity);
                }
            }
        }
        //we don't care abt lag.
        updateTrackingEntities();
        for (Entity entity : trackingEntities)
        {
            if (entity != getOwner() && !DamageSources.isFriendlyFireBetween(getOwner(), entity) && !entity.isSpectator())
            {
                float distance = (float) center.distanceTo(entity.position());
                if (distance > radius)
                {
                    continue;
                }
                var direction = center.subtract(entity.position()).normalize();
                var spinDirection = up.cross(direction);
                var spinValue = Math.clamp(JeffsISSAddons._configServer._blackHoleSpinPullRatio.get(), 0, 1);
                entity.setDeltaMovement(direction.scale(1 - spinValue).add(spinDirection.scale(spinValue)).scale(JeffsISSAddons._configServer._blackHoleSpinPower.get()).add(entity.getDeltaMovement().scale(JeffsISSAddons._configServer._blackHoleDeltaMultiplier.get())));
                entity.fallDistance = 0;
                if (!level().isClientSide())
                {
                    if (entity instanceof FallingBlockEntity)
                    {
                        if (entity.position().distanceTo(center) < JeffsISSAddons._configServer._blackHoleBlockKillWhenDistanceLessThan.get())
                        {
                            entity.kill();
                        }
                    }
                    else if (entity instanceof LivingEntity)
                    {
                        if (hitTick && entity.position().distanceTo(center) < JeffsISSAddons._configServer._blackHoleDamageRadiusMultiplier.get() * radius)
                        {
                            DamageSources.applyDamage(entity, damage, SpellRegistry.BLACK_HOLE_SPELL.get().getDamageSource(this, getOwner()));
                        }
                    }
                }
            }
        }
        if (!level().isClientSide())
        {
            if (tickCount > duration)
            {
                this.discard();
                this.playSound(SoundRegistry.BLACK_HOLE_CAST.get(), getRadius() / 2f, 1);
                MagicManager.spawnParticles(level(), ParticleHelper.UNSTABLE_ENDER, getX(), getY() + getRadius(), getZ(), 200, 1, 1, 1, 1, true);
                for (Entity entity : trackingEntities) {
                    if (entity.distanceToSqr(center) < 9) {
                        entity.setDeltaMovement(entity.getDeltaMovement().add(entity.position().subtract(center).normalize().scale(0.5f)));
                        entity.hurtMarked = true;
                    }
                }
            }
            else if ((tickCount - 1) % loopSoundDurationInTicks == 0 && (duration < loopSoundDurationInTicks || tickCount + loopSoundDurationInTicks < duration)) {
                this.playSound(SoundRegistry.BLACK_HOLE_LOOP.get(), getRadius() / 3f, .9f + Utils.random.nextFloat() * .2f);
            }
        }
    }
}
