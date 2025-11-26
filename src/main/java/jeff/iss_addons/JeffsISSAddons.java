package jeff.iss_addons;

import jeff.iss_addons.config.ConfigServer;
import jeff.iss_addons.config.ConfigStartup;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.fml.common.Mod;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(JeffsISSAddons.MODID)
public class JeffsISSAddons
{
    public static final ConfigStartup _configStartup;
    public static final ModConfigSpec _modConfigSpecStartup;
    public static final ConfigServer _configServer;
    public static final ModConfigSpec _modConfigSpecServer;
    static
    {
        Pair<ConfigStartup, ModConfigSpec> startup =
                new ModConfigSpec.Builder().configure(ConfigStartup::new);

        _configStartup = startup.getLeft();
        _modConfigSpecStartup = startup.getRight();

        Pair<ConfigServer, ModConfigSpec> server =
                new ModConfigSpec.Builder().configure(ConfigServer::new);
        _configServer = server.getLeft();
        _modConfigSpecServer = server.getRight();
    }

    // Define mod id in a common place for everything to reference
    public static final String MODID = "jeffsissaddons";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public JeffsISSAddons(ModContainer modContainer)
    {
        modContainer.registerConfig(ModConfig.Type.STARTUP, _modConfigSpecStartup);
        modContainer.registerConfig(ModConfig.Type.SERVER, _modConfigSpecServer);
        JeffsISSAddons.LOGGER.info("Loaded!");
    }
}
