package jeff.iss_addons.mixin;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.item.Scroll;
import jeff.iss_addons.JeffsISSAddons;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Scroll.class)
public class ScrollMixin
{
    @Inject(method="removeScrollAfterCast", at = @At("HEAD"), cancellable = true)
    protected void jeffsissaddons$removeScrollAfterCast(ServerPlayer serverPlayer, ItemStack stack, CallbackInfo ci)
    {
        if (!JeffsISSAddons._configServer._scrollDamagesInsteadOfDisappearing.get())
        {
            return;
        }
        ci.cancel();
        if (serverPlayer.isCreative())
        {
            return;
        }
        var magicData = MagicData.getPlayerMagicData(serverPlayer);
        var spellData = magicData.getCastingSpell();
        var damage = 0.0f;
        var spellRarity = spellData.getRarity();
        var effectiveCooldown = MagicManager.getEffectiveSpellCooldown(spellData.getSpell(), serverPlayer, magicData.getCastSource());
        if (spellRarity == SpellRarity.COMMON)
        {
            damage = JeffsISSAddons._configServer._scrollDamage.get().getFirst().floatValue() * 2.0f;
        }
        else if (spellRarity == SpellRarity.UNCOMMON)
        {
            damage = JeffsISSAddons._configServer._scrollDamage.get().get(1).floatValue() * 2.0f;
        }
        else if (spellRarity == SpellRarity.RARE)
        {
            damage = JeffsISSAddons._configServer._scrollDamage.get().get(2).floatValue() * 2.0f;
        }
        else if (spellRarity == SpellRarity.EPIC)
        {
            damage = JeffsISSAddons._configServer._scrollDamage.get().get(3).floatValue() * 2.0f;
        }
        else if (spellRarity == SpellRarity.LEGENDARY)
        {
            damage = JeffsISSAddons._configServer._scrollDamage.get().get(4).floatValue() * 2.0f;
        }
        serverPlayer.hurt(SpellDamageSource.source(serverPlayer, spellData.getSpell()), damage + damage * (float) effectiveCooldown * JeffsISSAddons._configServer._scrollScaler.get().floatValue());
    }
}
