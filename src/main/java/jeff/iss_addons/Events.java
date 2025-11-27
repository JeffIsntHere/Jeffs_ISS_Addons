package jeff.iss_addons;

import jeff.iss_addons.network.common.TelekinesisPushPullData;
import jeff.iss_addons.network.server.TelekinesisPushPull;
import jeff.iss_addons.recipes.RecipeAdder;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.io.File;

import static jeff.iss_addons.recipes.RecipeAdder.copyResource;

@EventBusSubscriber(modid = JeffsISSAddons.MODID)
public class Events
{
    @SubscribeEvent // on the mod event bus
    public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event)
    {
        JeffsISSAddons.LOGGER.info("registerPayloadHandlers");
        // Sets the current network version
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
                TelekinesisPushPullData._TYPE,
                TelekinesisPushPullData._STREAM_CODEC,
                TelekinesisPushPull::handle
        );
    }

    @SubscribeEvent
    public static void serverAboutToStartEvent(ServerAboutToStartEvent serverAboutToStartEvent)
    {
        RecipeAdder.work();
        copyResource(new File(serverAboutToStartEvent.getServer().getWorldPath(LevelResource.DATAPACK_DIR).toFile().getAbsolutePath() + "\\" + JeffsISSAddons._configStartup._modPrefix.get() + "\\pack.mcmeta"), "/pack_marker.json");
    }
}
