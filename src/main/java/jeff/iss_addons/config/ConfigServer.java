package jeff.iss_addons.config;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Arrays;
import java.util.List;

public class ConfigServer
{
    public final ModConfigSpec.ConfigValue<Boolean> _scrollDamagesInsteadOfDisappearing;
    public final ModConfigSpec.ConfigValue<List<Double>> _scrollDamage;
    public final ModConfigSpec.ConfigValue<Double> _scrollScaler;
    public final ModConfigSpec.ConfigValue<Boolean> _scrollConsumeMana;
    public final ModConfigSpec.ConfigValue<Boolean> _enableTelekinesisMod;
    public final ModConfigSpec.ConfigValue<Boolean> _enableCounterspellMod;
    public ConfigServer(ModConfigSpec.Builder builder)
    {
        builder.push("Scroll");
        _scrollDamagesInsteadOfDisappearing = builder.define("Damages instead of disappearing", true);
        builder.comment("base damage to player (in hearts) per scroll use, ordered from common to legendary.");
        _scrollDamage = builder.define("Damage", Arrays.asList(1.0, 1.5, 2.0, 2.5, 3.0), new EnsureListSameSize<>(5));
        builder.comment("final damage to player (in hearts) = base damage + base damage * cooldown * scaler");
        builder.comment("cooldown is in tick (see minecraft tick), 20 ticks = 1 second by default.");
        _scrollScaler = builder.define("Scaler", 0.001);
        _scrollConsumeMana = builder.define("Consumes mana", true);
        builder.pop();

        builder.push("Telekinesis");
        _enableTelekinesisMod = builder.define("Enable", true);
        builder.pop();

        builder.push("Counterspell");
        _enableCounterspellMod = builder.define("Enable", true);
        builder.pop();
    }
}
