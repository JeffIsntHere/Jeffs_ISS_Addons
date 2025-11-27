package jeff.iss_addons;

import jeff.iss_addons.network.common.TelekinesisPushPullData;
import jeff.iss_addons.network.server.TelekinesisPushPull;
import jeff.iss_addons.recipes.RecipeAdder;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
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
    public static void addReloadListenerEvent(AddReloadListenerEvent addReloadListenerEvent)
    {
        RecipeAdder.work();
        copyResource(new File(net.neoforged.neoforge.common.CommonHooks.prefixNamespace(ResourceLocation.withDefaultNamespace(JeffsISSAddons._configStartup._dataPacksFolderPath.get()))+ "\\" + JeffsISSAddons._configStartup._modPrefix.get() + "\\pack.mcmeta"), "/pack_marker.json");
    }
}
