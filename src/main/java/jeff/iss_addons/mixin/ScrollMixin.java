package jeff.iss_addons.mixin;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.item.Scroll;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Scroll.class)
public class ScrollMixin
{
    @Overwrite
    protected void removeScrollAfterCast(ServerPlayer serverPlayer, ItemStack stack) {
        if (serverPlayer.isCreative()) {
            return;
        }
        SpellData spellData = MagicData.getPlayerMagicData(serverPlayer).getCastingSpell();
        float damage = 0;
        SpellRarity spellRarity = spellData.getRarity();
        if (spellRarity == SpellRarity.COMMON)
        {
            //1 heart
            damage = 2.0f;
        }
        else if (spellRarity == SpellRarity.UNCOMMON)
        {
            damage = 3.0f;
        }
        else if (spellRarity == SpellRarity.RARE)
        {
            damage = 4.0f;
        }
        else if (spellRarity == SpellRarity.EPIC)
        {
            damage = 5.0f;
        }
        else if (spellRarity == SpellRarity.LEGENDARY)
        {
            damage = 6.0f;
        }
        serverPlayer.hurt(SpellDamageSource.source(serverPlayer, spellData.getSpell()), damage);
    }
}
