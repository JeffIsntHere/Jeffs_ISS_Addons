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
    public final ModConfigSpec.ConfigValue<Boolean> _telekinesisEnable;
    public final ModConfigSpec.ConfigValue<Double> _telekinesisThrowPower;
    public final ModConfigSpec.ConfigValue<Double> _telekinesisTargetPreviousDeltaCarryOver;
    public final ModConfigSpec.ConfigValue<Double> _telekinesisCasterDeltaClamp;
    public final ModConfigSpec.ConfigValue<Double> _telekinesisTargetDeltaClamp;
    public final ModConfigSpec.ConfigValue<Double> _telekinesisThrowDeltaClamp;
    public final ModConfigSpec.ConfigValue<Boolean> _telekinesisDamageVehicleHorizontal;
    public final ModConfigSpec.ConfigValue<Boolean> _telekinesisDamageVehicleVertical;
    public final ModConfigSpec.ConfigValue<Boolean> _telekinesisDamageHostileVehicle;
    public final ModConfigSpec.ConfigValue<Boolean> _counterSpellEnable;
    public final ModConfigSpec.ConfigValue<Boolean> _counterSpellDeflectsNonMagicProjectiles;
    public final ModConfigSpec.ConfigValue<Double> _counterSpellBaseDelta;
    public final ModConfigSpec.ConfigValue<Double> _counterSpellDeltaMultiplier;
    public final ModConfigSpec.ConfigValue<Double> _counterSpellMaxDelta;
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
        _telekinesisEnable = builder.define("Enable", true);
        _telekinesisThrowPower = builder.define("Base throw power", 25.0);
        builder.comment("how much of the target's previous movement is to be used for the new movement calculation");
        builder.comment("lower values tend to be more snappy, higher values tend to be more jittery");
        _telekinesisTargetPreviousDeltaCarryOver = builder.define("Telekinesis target previous delta carry over", 0.15);
        builder.comment("set to 0 if you don't want to limit the movement delta of the target / caster");
        _telekinesisCasterDeltaClamp = builder.define("Caster delta clamp", 5.0);
        _telekinesisTargetDeltaClamp = builder.define("Target delta clamp", 5.0);
        builder.comment("set to 0 to set this value to Target delta clamp.");
        _telekinesisThrowDeltaClamp = builder.define("Telekinesis throw delta clamp", 0.0);
        builder.comment("for context, you can ride a mob that you are using telekinesis on");
        builder.comment("setting this to false will disable the horizontal damage of the vehicle");
        _telekinesisDamageVehicleHorizontal = builder.define("Damage Vehicle Horizontal", false);
        builder.comment("setting this to false will disable the fall damage of the vehicle.");
        _telekinesisDamageVehicleVertical = builder.define("Damage vehicle vertical", false);
        builder.comment("setting this to true will still apply the damage for hostile vehicles.");
        _telekinesisDamageHostileVehicle = builder.define("Damage hostile vehicle", true);
        builder.pop();

        builder.push("Counterspell");
        _counterSpellEnable = builder.define("Enable", true);
        _counterSpellDeflectsNonMagicProjectiles = builder.define("Deflects non-magic projectiles", true);
        _counterSpellBaseDelta = builder.define("Base delta", 1.0);
        _counterSpellDeltaMultiplier = builder.define("Delta multiplier", 1.15);
        builder.comment("Set to 0 to disable max delta.");
        _counterSpellMaxDelta = builder.define("Counter spell max delta", 0.0);
        builder.pop();
    }
}
