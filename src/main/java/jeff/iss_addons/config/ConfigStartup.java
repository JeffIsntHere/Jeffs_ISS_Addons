package jeff.iss_addons.config;

import jeff.iss_addons.JeffsISSAddons;
import net.neoforged.neoforge.common.ModConfigSpec;


public class ConfigStartup
{
    public final ModConfigSpec.ConfigValue<String> _modsFolderPath;
    public final ModConfigSpec.ConfigValue<String> _dataPacksFolderPath;
    public final ModConfigSpec.ConfigValue<String> _modPrefix;
    public final ModConfigSpec.ConfigValue<String> _targetFolder;
    public final String _recipeSource = "/recipes/";
    public final ModConfigSpec.ConfigValue<Boolean> _enableArcaneEssenceRecipe;
    public final ModConfigSpec.ConfigValue<Boolean> _enableCinderEssenceRecipe;
    public final ModConfigSpec.ConfigValue<Boolean> _enableCommonInkRecipe;

    public ConfigStartup(ModConfigSpec.Builder builder)
    {
        builder.push("Misc");
        builder.comment("do not change anything in misc if you don't know what you're doing!");
        _modsFolderPath = builder.define("Mods Folder Path","mods");
        _modPrefix = builder.define("Mod Prefix", JeffsISSAddons.MODID);
        _dataPacksFolderPath = builder.define("Datapacks Folder Path", "datapacks");
        _targetFolder = builder.define("Target Folder", "");
        builder.pop();

        builder.push("Recipes");
        _enableArcaneEssenceRecipe = builder.define("Enable Arcane Essence Recipe", true);
        _enableCinderEssenceRecipe = builder.define("Enable Cinder Essence Recipe", true);
        _enableCommonInkRecipe = builder.define("Enable Common Ink Recipe", true);
        builder.pop();
    }
}
