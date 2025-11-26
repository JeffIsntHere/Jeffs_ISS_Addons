package jeff.iss_addons.config;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Arrays;
import java.util.List;

public class Config
{
    public final ModConfigSpec.ConfigValue<Boolean> _enableArcaneEssenceRecipe;
    public final ModConfigSpec.ConfigValue<Boolean> _enableCinderEssenceRecipe;
    public final ModConfigSpec.ConfigValue<Boolean> _enableCommonInkEssenceRecipe;
    public final ModConfigSpec.ConfigValue<Boolean> _enableScrollMod;
    public final ModConfigSpec.ConfigValue<List<Double>> _scrollHealthDamage;
    public final ModConfigSpec.ConfigValue<Boolean> _enableTelekinesisMod;
    public final ModConfigSpec.ConfigValue<Boolean> _enableCounterspellMod;

    public Config(ModConfigSpec.Builder builder)
    {
        builder.push("Recipes");
        _enableArcaneEssenceRecipe = builder.define("Enable Arcane Essence Recipe", true);
        _enableCinderEssenceRecipe = builder.define("Enable Cinder Essence Recipe", true);
        _enableCommonInkEssenceRecipe = builder.define("Enable Common Ink Recipe", true);
        builder.pop();

        builder.push("Scroll");
        _enableScrollMod = builder.define("Enable", true);
        builder.comment("damage to player (in hearts) per scroll use, ordered from common to legendary.");
        _scrollHealthDamage = builder.define("Damage", Arrays.asList(1.0, 1.5, 2.0, 2.5, 3.0), new EnsureListSameSize<>(5));
        builder.pop();

        builder.push("Telekinesis");
        _enableTelekinesisMod = builder.define("Enable", true);
        builder.pop();

        builder.push("Counterspell");
        _enableCounterspellMod = builder.define("Enable", true);
        builder.pop();
    }
}
