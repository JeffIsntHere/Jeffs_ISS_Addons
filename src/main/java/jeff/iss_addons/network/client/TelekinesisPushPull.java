package jeff.iss_addons.network.client;

import com.mojang.blaze3d.platform.InputConstants;
import jeff.iss_addons.JeffsISSAddons;
import jeff.iss_addons.network.common.TelekinesisPushPullData;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

@Mod(value = JeffsISSAddons.MODID, dist = Dist.CLIENT)
public class TelekinesisPushPull
{
    public static final KeyMapping telekinesisPush = new KeyMapping(JeffsISSAddons.MODID,
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_W,
            KeyMapping.CATEGORY_MISC
    );

    public static final KeyMapping telekinesisPull = new KeyMapping(JeffsISSAddons.MODID,
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_S,
            KeyMapping.CATEGORY_MISC
    );

    public TelekinesisPushPull(IEventBus modEventBus, ModContainer modContainer)
    {
        modEventBus.register(TelekinesisPushPull.class);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        while (telekinesisPull.consumeClick())
        {
            PacketDistributor.sendToServer(new TelekinesisPushPullData(true));
        }
        while (telekinesisPush.consumeClick())
        {
            PacketDistributor.sendToServer(new TelekinesisPushPullData(false));
        }
    }
}
