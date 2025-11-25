package jeff.iss_addons;

import jeff.iss_addons.network.common.TelekinesisPushPullData;
import jeff.iss_addons.network.server.TelekinesisPushPull;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(JeffsISSAddons.MODID)
public class JeffsISSAddons {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "jeffsissaddons";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public JeffsISSAddons(IEventBus modEventBus, ModContainer modContainer)
    {
        modEventBus.register(JeffsISSAddons.class);
    }

    @SubscribeEvent // on the mod event bus
    public static void register(RegisterPayloadHandlersEvent event) {
        // Sets the current network version
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
                TelekinesisPushPullData._TYPE,
                TelekinesisPushPullData._STREAM_CODEC,
                TelekinesisPushPull::handle
        );
    }
}
