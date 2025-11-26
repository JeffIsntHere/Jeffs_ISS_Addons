package jeff.iss_addons;

import jeff.iss_addons.config.Config;
import jeff.iss_addons.network.common.TelekinesisPushPullData;
import jeff.iss_addons.network.server.TelekinesisPushPull;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.fml.common.Mod;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(JeffsISSAddons.MODID)
@EventBusSubscriber(modid = JeffsISSAddons.MODID)
public class JeffsISSAddons
{
    public static final Config _config;
    public static final ModConfigSpec _modConfigSpec;
    static
    {
        Pair<Config, ModConfigSpec> pair =
                new ModConfigSpec.Builder().configure(Config::new);

        _config = pair.getLeft();
        _modConfigSpec = pair.getRight();
    }
    // Define mod id in a common place for everything to reference
    public static final String MODID = "jeffsissaddons";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public JeffsISSAddons(ModContainer modContainer)
    {
        modContainer.registerConfig(ModConfig.Type.SERVER, _modConfigSpec);
    }

    @SubscribeEvent // on the mod event bus
    public static void register(RegisterPayloadHandlersEvent event)
    {
        // Sets the current network version
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
                TelekinesisPushPullData._TYPE,
                TelekinesisPushPullData._STREAM_CODEC,
                TelekinesisPushPull::handle
        );
    }
}
