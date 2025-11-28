package jeff.iss_addons.mixin;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.spells.root.PreventDismount;
import io.redspace.ironsspellbooks.network.casting.SyncTargetingDataPacket;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.spells.eldritch.TelekinesisSpell;
import jeff.iss_addons.ExtendedTelekinesisData;
import jeff.iss_addons.JeffsISSAddons;
import jeff.iss_addons.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TelekinesisSpell.class)
public abstract class TelekinesisSpellMixin extends AbstractSpell
{
    @Shadow
    protected abstract int getRange(int spellLevel, LivingEntity caster);

    @Shadow
    public abstract ResourceLocation getSpellResource();

    @Shadow
    public abstract DefaultConfig getDefaultConfig();

    @Shadow
    public abstract CastType getCastType();

    @Unique
    public void jeffsissaddons$handleProjectile(Projectile projectile, LivingEntity caster, MagicData magicData)
    {
        magicData.setAdditionalCastData(new ExtendedTelekinesisData(caster, projectile, 0.0f, this.getRange(magicData.getCastingSpellLevel(), caster) * 5.0f));
        if (caster instanceof ServerPlayer serverPlayer)
        {
            //pray this is unused....
            //PacketDistributor.sendToPlayer(serverPlayer, new SyncTargetingDataPacket(livingEntity, this));
            serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(Component.translatable("ui.irons_spellbooks.spell_target_success", projectile.getDisplayName().getString(), this.getDisplayName(serverPlayer)).withStyle(ChatFormatting.GREEN)));
        }
    }

    @Unique
    public void jeffsissaddons$handleLiving(LivingEntity livingEntity, LivingEntity caster, MagicData magicData)
    {
        magicData.setAdditionalCastData(new ExtendedTelekinesisData(caster, livingEntity, 0.0f, this.getRange(magicData.getCastingSpellLevel(), caster) * 5.0f));
        if (caster instanceof ServerPlayer serverPlayer)
        {
            PacketDistributor.sendToPlayer(serverPlayer, new SyncTargetingDataPacket(livingEntity, this));
            serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(Component.translatable("ui.irons_spellbooks.spell_target_success", livingEntity.getDisplayName().getString(), this.getDisplayName(serverPlayer)).withStyle(ChatFormatting.GREEN)));
        }
        if (livingEntity instanceof ServerPlayer serverPlayer)
        {
            Utils.sendTargetedNotification(serverPlayer, caster, this);
        }
    }

    @Inject(method="checkPreCastConditions", at = @At("HEAD"), cancellable = true)
    public void jeffsissaddons$checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData, CallbackInfoReturnable<Boolean> cir)
    {
        if (!JeffsISSAddons._configServer._telekinesisEnable.get())
        {
            return;
        }
        cir.cancel();
        Vec3 start = entity.getEyePosition();
        Vec3 end = entity.getLookAngle().normalize().scale(getRange(spellLevel, entity)).add(start);
        var result = Utils.raycastForEntity(level, entity, start, end, true, .15f, ExtendedTelekinesisData::telekinesisSpellCheck);
        if (result instanceof EntityHitResult entityHitResult)
        {
            var entityHit = entityHitResult.getEntity();
            switch (entityHit)
            {
                case Projectile projectile -> jeffsissaddons$handleProjectile(projectile, entity, playerMagicData);
                case LivingEntity livingEntity -> jeffsissaddons$handleLiving(livingEntity, entity, playerMagicData);
                case PartEntity<?> partEntity when partEntity.getParent() instanceof LivingEntity livingParent && !entity.equals(livingParent) ->
                        jeffsissaddons$handleLiving(livingParent, entity, playerMagicData);
                case PreventDismount preventDismount when entityHit.getFirstPassenger() instanceof LivingEntity livingRooted ->
                        jeffsissaddons$handleLiving(livingRooted, entity, playerMagicData);
                default ->
                {
                    if (entity instanceof ServerPlayer serverPlayer)
                    {
                        serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(Component.translatable("ui.irons_spellbooks.cast_error_target").withStyle(ChatFormatting.RED)));
                    }
                    cir.setReturnValue(false);
                    return;
                }
            }
            cir.setReturnValue(true);
            return;
        }
        if (entity instanceof ServerPlayer serverPlayer)
        {
            serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(Component.translatable("ui.irons_spellbooks.cast_error_target").withStyle(ChatFormatting.RED)));
        }
        cir.setReturnValue(false);
    }

    @Unique
    public double jeffsissaddons$mass(Entity entity)
    {
        var bb = entity.getBoundingBox();
        //root 6 because bounding box sizes != mass
        return Math.pow(bb.getXsize() * bb.getYsize() * bb.getZsize(), 1.0/6.0);
    }

    @Unique
    public void jeffsissaddons$applyForce(Vec3 force, Entity target, LivingEntity caster, MagicData magicData)
    {
        Vec3 deltaMovement = target.getDeltaMovement();
        deltaMovement.scale(Math.min(force.length() / deltaMovement.length(), JeffsISSAddons._configServer._telekinesisTargetPreviousDeltaCarryOver.get()));
        Vec3 finalForce = deltaMovement.add(force);
        var casterMass = jeffsissaddons$mass(target);
        var targetMass = jeffsissaddons$mass(caster);
        var sumMass = casterMass + targetMass;
        target.setDeltaMovement(Util.clampVec3(finalForce.scale(targetMass/sumMass), JeffsISSAddons._configServer._telekinesisTargetDeltaClamp.get()));
        caster.setDeltaMovement(Util.clampVec3(caster.getDeltaMovement().add(finalForce.scale(-casterMass/sumMass)), JeffsISSAddons._configServer._telekinesisCasterDeltaClamp.get()));
        if (target instanceof LivingEntity livingEntity)
        {
            if (force.y > 0) {
                livingEntity.resetFallDistance();
            }
            if ((magicData.getCastDurationRemaining()) % 10 == 0) {
                Vec3 travel = new Vec3(livingEntity.getX() - livingEntity.xOld, livingEntity.getY() - livingEntity.yOld, livingEntity.getZ() - livingEntity.zOld);
                int airborne = (int) (travel.x * travel.x + travel.z * travel.z) / 2;
                if (JeffsISSAddons._configServer._telekinesisDamageVehicle.get() || !livingEntity.getPassengers().contains(caster))
                {
                    livingEntity.addEffect(new MobEffectInstance(MobEffectRegistry.AIRBORNE, 31, airborne));
                }
                livingEntity.addEffect(new MobEffectInstance(MobEffectRegistry.ANTIGRAVITY, 11, 0));
            }
            livingEntity.hurtMarked = true;
        }
    }

    @Inject(method="handleTelekinesis", at = @At("HEAD"), cancellable = true)
    private void jeffsissaddons$handleTelekinesis(ServerLevel world, LivingEntity entity, MagicData playerMagicData, float strength, CallbackInfo ci)
    {
        if (!JeffsISSAddons._configServer._telekinesisEnable.get())
        {
            return;
        }
        ci.cancel();
        if (playerMagicData.getAdditionalCastData() instanceof ExtendedTelekinesisData extendedTelekinesisData)
        {
            var target = extendedTelekinesisData.entity(world);
            if (entity instanceof ServerPlayer serverPlayer && (target == null || (target.isRemoved() || (target instanceof LivingEntity livingEntity && livingEntity.isDeadOrDying()))))
            {
                Utils.serverSideCancelCast(serverPlayer);
                return;
            }
            if (target instanceof Projectile projectile)
            {
                projectile.setOwner(entity);
            }
            extendedTelekinesisData.processFlag(entity);
            if (extendedTelekinesisData.shouldThrow())
            {
                if (entity instanceof ServerPlayer serverPlayer)
                {
                    Utils.serverSideCancelCast(serverPlayer);
                }
                var force = entity.getForward().normalize().scale(JeffsISSAddons._configServer._telekinesisThrowPower.get() * strength);
                jeffsissaddons$applyForce(force, target, entity, playerMagicData);
                playerMagicData.setAdditionalCastData(null);
                return;
            }
            var position = extendedTelekinesisData.direction(entity).scale(extendedTelekinesisData.distance()).add(entity.position());
            var previous = extendedTelekinesisData.prev(position);
            var multiplier = position.distanceTo(previous);
            if (multiplier < 1)
            {
                multiplier = 1;
            }
            var force = position.subtract(target.position()).scale(multiplier * strength * (1.0f/world.tickRateManager().tickrate()));
            jeffsissaddons$applyForce(force, target, entity, playerMagicData);
        }
    }
}
