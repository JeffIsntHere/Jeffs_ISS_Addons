package jeff.iss_addons;

import jeff.iss_addons.network.common.TelekinesisPushPullData;
import jeff.iss_addons.network.server.TelekinesisPushPull;
import jeff.iss_addons.recipes.RecipeAdder;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

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

    private static void recipes()
    {
        if (ServerLifecycleHooks.getCurrentServer() != null)
        {
            JeffsISSAddons._configStartup._targetFolder.set(ServerLifecycleHooks.getCurrentServer().getWorldPath(LevelResource.DATAPACK_DIR).toFile().getAbsolutePath());
            JeffsISSAddons._configStartup._targetFolder.save();
        }
        if (JeffsISSAddons._configStartup._targetFolder.get().isEmpty() && !new File(JeffsISSAddons._configStartup._targetFolder.get()).exists())
        {
            return;
        }
        var target = JeffsISSAddons._configStartup._targetFolder.get() + "\\" + JeffsISSAddons._configStartup._modPrefix.get();
        RecipeAdder.work(target + "\\data\\" + JeffsISSAddons._configStartup._modPrefix.get() + "\\recipe");
        copyResource(new File(target + "\\pack.mcmeta"), "/pack_marker.json");
    }

    @SubscribeEvent
    public static void serverAboutToStartEvent(ServerAboutToStartEvent serverAboutToStartEvent)
    {
        recipes();
    }

    @SubscribeEvent
    public static void addReloadListenerEvent(AddReloadListenerEvent reloadListenerEvent)
    {
        recipes();
    }
}
