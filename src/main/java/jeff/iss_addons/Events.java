package jeff.iss_addons;

import jeff.iss_addons.network.common.TelekinesisPushPullData;
import jeff.iss_addons.network.server.TelekinesisPushPull;
import jeff.iss_addons.recipes.RecipeAdder;
import jeff.iss_addons.recipes.Recipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.concurrent.CompletableFuture;

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
        addReloadListenerEvent.addListener(new RecipeAdder());
    }

    /*
    doesn't work,
    could be because: it's neoforge's issue
    or: some other mod removing this one's gather data event
    or: i'm using it wrong (but how??) <- "Its for data gen only" :OOOOOO
    ...
     */
    /*
    solutions:
    1. use craftweaker
    2. make a class for generating data files (recipes) before they are evaluated
    3. inject into recipe manager & add the recipes manually.
     */
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        JeffsISSAddons.LOGGER.info("gatherDataEvent");
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(
                event.includeServer(),
                new Recipes(output, lookupProvider)
        );
        JeffsISSAddons.LOGGER.info("added gather data event");
    }
}
