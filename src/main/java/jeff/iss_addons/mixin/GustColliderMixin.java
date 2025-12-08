package jeff.iss_addons.mixin;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractConeProjectile;
import io.redspace.ironsspellbooks.entity.spells.gust.GustCollider;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import jeff.iss_addons.JeffsISSAddons;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mixin(GustCollider.class)
public abstract class GustColliderMixin extends AbstractConeProjectile
{
    @Shadow
    public float strength;
    @Shadow
    public float range;
    @Shadow
    public int amplifier;

    protected GustColliderMixin(EntityType<? extends AbstractConeProjectile> entityType, Level level, LivingEntity entity)
    {
        super(entityType, level, entity);
    }

    @Shadow
    public abstract Entity getOwner();

    @Inject(method="onHitEntity", at = @At("HEAD"), cancellable = true)
    void jeffsissaddons$onHitEntity(EntityHitResult entityHitResult, CallbackInfo ci) {
        if (!JeffsISSAddons._configServer._gustSpellEnable.get())
        {
            return;
        }
        ci.cancel();
        var entity = getOwner();
        var target = entityHitResult.getEntity();
        if (entity != null && target.distanceToSqr(entity) < range * range) {
            if (!DamageSources.isFriendlyFireBetween(entity, target)) {
                var knockback = new Vec3(entity.getX() - target.getX(), entity.getY() - target.getY(), entity.getZ() - target.getZ()).normalize().scale(-strength);
                target.setDeltaMovement(target.getDeltaMovement().add(knockback));
                target.hurtMarked = true;
                if (target instanceof LivingEntity livingEntity)
                {
                    livingEntity.addEffect(new MobEffectInstance(MobEffectRegistry.AIRBORNE, 60, amplifier));
                }
            }
        }
    }

    @Override
    protected Set<Entity> getSubEntityCollisions() {
        if (!JeffsISSAddons._configServer._gustSpellEnable.get())
        {
            return super.getSubEntityCollisions();
        }
        List<Entity> collisions = new ArrayList<>();
        for (Entity conepart : subEntities) {
            collisions.addAll(level().getEntities(conepart, conepart.getBoundingBox()));
        }

        return collisions.stream().filter(target ->
                target != getOwner() && Utils.hasLineOfSight(level(), this, target, true)
        ).collect(Collectors.toSet());
    }
}
