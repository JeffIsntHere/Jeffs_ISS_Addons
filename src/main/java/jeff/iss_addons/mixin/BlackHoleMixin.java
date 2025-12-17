package jeff.iss_addons.mixin;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
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
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
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
import java.util.HashMap;
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

    @Shadow
    public void setDuration(int duration) {}

    @Shadow
    public int getDuration()
    {
        return 0;
    }

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

    @Unique
    protected double jeffsissaddons$mass(Entity entity)
    {
        var mass = 10.0;
        if (entity instanceof FallingBlockEntity)
        {
            mass += 10.0;
        }
        if (entity instanceof AbstractMagicProjectile abstractMagicProjectile)
        {
            mass += Math.pow(Util.volume(entity), 1.2/3.0);
            mass *= abstractMagicProjectile.getDamage();
        }
        if (entity instanceof LivingEntity livingEntity)
        {
            mass += 20;
            mass += livingEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
            mass *= livingEntity.getMaxHealth();
            mass += livingEntity.getAttributeValue(Attributes.ARMOR);
        }
        return mass;
    }

    @Unique
    protected void jeffsissaddons$applyForce(Entity entity, double mass, Vec3 force)
    {
        entity.setDeltaMovement(entity.getDeltaMovement().add(force.normalize().scale(force.length() / (mass + Math.pow(Util.volume(entity), 2)) / 100)));
        entity.hurtMarked = true;
    }

    //perhaps add crafting via black hole gravity?
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
        if (JeffsISSAddons._configServer._blackHoleCanMove.get())
        {
            this.noPhysics = true;
            move(MoverType.SELF, getDeltaMovement());
        }
        else
        {
            setDeltaMovement(Vec3.ZERO);
        }
        var blackHoleMovement = getDeltaMovement().scale(JeffsISSAddons._configServer._blackHoleDeltaMultiplier.get());
        setDeltaMovement(blackHoleMovement);
        if (JeffsISSAddons._configServer._blackHoleDamageEveryTicks.get() < 1)
        {
            JeffsISSAddons._configServer._blackHoleDamageEveryTicks.set(1);
            JeffsISSAddons._configServer._blackHoleDamageEveryTicks.save();
        }
        boolean hitTick = this.tickCount % JeffsISSAddons._configServer._blackHoleDamageEveryTicks.get() == 0;
        Vec3 center = bb.getCenter();
        var up = jeffsissaddons$up();
        var sm = 0.0F;
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
                var clampValue = Math.pow(radius, 0.85) * JeffsISSAddons._configServer._blackHoleBlockRadiusMultiplier.get();
                do
                {
                    var dst = center.add(dir.add(Utils.getRandomVec3(1).normalize().scale(randomness)).normalize().scale(clampValue));
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
                    ++sm;
                }
            }
        }
        var eventHorizon = Math.pow(radius, 0.5) / 2.2;
        setDuration(getDuration() - (int)(sm/radius));
        //we don't care abt lag.
        updateTrackingEntities();
        var blackHoleMass = jeffsissaddons$mass(this);
        var items = new HashMap<ItemStack, Integer>();
        for (Entity entity : trackingEntities)
        {
            if (entity != getOwner() && !DamageSources.isFriendlyFireBetween(getOwner(), entity) && !entity.isSpectator())
            {
                entity.move(MoverType.SELF, blackHoleMovement);
                float distance = (float) center.distanceTo(entity.position());
                if (distance > radius)
                {
                    continue;
                }
                var direction = center.subtract(entity.position()).normalize();
                var spinDirection = up.cross(direction);
                var spinValue = Math.pow(distance / radius, 1.0/2.0) * JeffsISSAddons._configServer._blackHoleSpinRatio.get();
                var entityMass = jeffsissaddons$mass(entity);
                var force = direction.scale(1 - spinValue).add(spinDirection.scale(spinValue)).scale(JeffsISSAddons._configServer._blackHoleGravity.get() * blackHoleMass * entityMass);
                //TODO: set delta movement based on hitbox size.
                jeffsissaddons$applyForce(entity, entityMass, force);
                jeffsissaddons$applyForce(this, blackHoleMass, force.scale(-1.0));
                entity.fallDistance = 0;
                if (!level().isClientSide())
                {
                    if (distance < eventHorizon)
                    {
                        switch (entity)
                        {
                            case FallingBlockEntity fallingBlockEntity -> entity.kill();
                            case AbstractMagicProjectile abstractMagicProjectile ->
                            {
                                if (Util.volume(entity) < Util.volume(this))
                                {
                                    entity.kill();
                                    setDuration(getDuration() + (int) abstractMagicProjectile.getDamage());
                                }
                            }
                            case LivingEntity livingEntity ->
                            {
                                if (hitTick)
                                {
                                    DamageSources.applyDamage(entity, damage, SpellRegistry.BLACK_HOLE_SPELL.get().getDamageSource(this, getOwner()));
                                }
                            }
                            case ItemEntity itemEntity ->
                            {
                                var item = itemEntity.getItem();
                                items.putIfAbsent(item, 0);
                                items.put(item,items.get(item) + 1);
                            }
                            default ->
                            {
                            }
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
