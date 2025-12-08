package jeff.iss_addons.mixin;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.entity.IMagicEntity;
import io.redspace.ironsspellbooks.api.events.CounterSpellEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.capabilities.magic.RecastResult;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.spells.ender.CounterspellSpell;
import jeff.iss_addons.ExtendedTelekinesisData;
import jeff.iss_addons.JeffsISSAddons;
import jeff.iss_addons.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CounterspellSpell.class)
public abstract class CounterspellMixin extends AbstractSpell
{
    @Shadow
    public abstract ResourceLocation getSpellResource();

    @Shadow
    public abstract DefaultConfig getDefaultConfig();

    @Shadow
    public abstract CastType getCastType();

    @Inject(method="onCast", at = @At("HEAD"), cancellable = true)
    public void jeffsissaddons$onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData, CallbackInfo ci)
    {
        if (!JeffsISSAddons._configServer._counterSpellEnable.get())
        {
            return;
        }
        ci.cancel();
        Vec3 start = entity.getEyePosition();
        Vec3 end = start.add(entity.getForward().normalize().scale(80));
        HitResult hitResult = Utils.raycastForEntity(entity.level(), entity, start, end, true, 0.35f, ExtendedTelekinesisData::counterSpellCheck);
        Vec3 forward = entity.getForward().normalize();
        if (hitResult instanceof EntityHitResult entityHitResult)
        {
            var hitEntity = entityHitResult.getEntity();
            if (!(NeoForge.EVENT_BUS.post(new CounterSpellEvent(entity, hitEntity)).isCanceled()))
            {
                switch (hitEntity)
                {
                    case Projectile projectile ->
                    {
                        if (!JeffsISSAddons._configServer._counterSpellDeflectsNonMagicProjectiles.get() && !(projectile instanceof AbstractMagicProjectile))
                        {
                            break;
                        }
                        projectile.setOwner(entity);
                        var force = projectile.getDeltaMovement().length();
                        if (force < JeffsISSAddons._configServer._counterSpellBaseDelta.get())
                        {
                            force = JeffsISSAddons._configServer._counterSpellBaseDelta.get();
                        }
                        projectile.setDeltaMovement(Util.clampVec3(entity.getForward().normalize().scale(force * JeffsISSAddons._configServer._counterSpellDeltaMultiplier.get()), JeffsISSAddons._configServer._counterSpellMaxDelta.get()));
                        projectile.hurtMarked = true;
                    }
                    case AntiMagicSusceptible antiMagicSusceptible ->
                    {
                        if (antiMagicSusceptible instanceof IMagicSummon summon)
                        {
                            if (summon.getSummoner() == entity)
                            {
                                if (summon instanceof Mob mob && mob.getTarget() == null)
                                {
                                    antiMagicSusceptible.onAntiMagic(playerMagicData);
                                }
                            }
                            else
                            {
                                antiMagicSusceptible.onAntiMagic(playerMagicData);
                            }
                        }
                        else
                        {
                            antiMagicSusceptible.onAntiMagic(playerMagicData);
                        }
                    }
                    case ServerPlayer serverPlayer ->
                    {
                        Utils.serverSideCancelCast(serverPlayer, true);
                        MagicData.getPlayerMagicData(serverPlayer).getPlayerRecasts().removeAll(RecastResult.COUNTERSPELL);
                    }
                    case IMagicEntity abstractSpellCastingMob -> abstractSpellCastingMob.cancelCast();
                    default ->
                    {
                    }
                }
                if (hitEntity instanceof LivingEntity livingEntity)
                {
                    //toList to avoid concurrent modification
                    for (Holder<MobEffect> mobEffect : livingEntity.getActiveEffectsMap().keySet().stream().toList())
                    {
                        if (mobEffect.value() instanceof MagicMobEffect)
                        {
                            livingEntity.removeEffect(mobEffect);
                        }
                    }
                }
            }
        }
        double distance = entity.position().distanceTo(hitResult.getLocation());
        for (float i = 1; i < distance; i += .5f)
        {
            Vec3 pos = entity.getEyePosition().add(forward.scale(i));
            MagicManager.spawnParticles(world, ParticleTypes.ENCHANT, pos.x, pos.y, pos.z, 1, 0, 0, 0, 0, false);
        }
        super.onCast(world, spellLevel, entity, castSource, playerMagicData);
    }
}
