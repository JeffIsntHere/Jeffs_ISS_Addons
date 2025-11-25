package jeff.iss_addons.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import io.redspace.ironsspellbooks.player.ClientInputEvents;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import jeff.iss_addons.network.common.TelekinesisPushPullData;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientInputEvents.class)
public class ClientInputEventsMixin
{
    @Inject(method="clientMouseScrolled", at = @At("TAIL"))
    private static void clientMouseScrolled(InputEvent.MouseScrollingEvent event, CallbackInfo ci)
    {
        if (ClientMagicData.isCasting())
        {
            if (event.getScrollDeltaY() > 0)
            {
                PacketDistributor.sendToServer(new TelekinesisPushPullData(TelekinesisPushPullData.sUp));
            }
            else
            {
                PacketDistributor.sendToServer(new TelekinesisPushPullData(TelekinesisPushPullData.sDown));
            }
        }
    }

    @Inject(method="onUseInput", at = @At("TAIL"))
    private static void onUseInput(InputEvent.InteractionKeyMappingTriggered event, CallbackInfo ci)
    {
        if (ClientMagicData.isCasting() && event.isAttack())
        {
            PacketDistributor.sendToServer(new TelekinesisPushPullData(TelekinesisPushPullData.tThrow));
        }
    }

    @Inject(method="handleInputEvent", at = @At("HEAD"))
    private static void handleInputEvent(int button, int action, CallbackInfo ci)
    {
        if (ClientMagicData.isCasting() && button == InputConstants.MOUSE_BUTTON_MIDDLE)
        {
            PacketDistributor.sendToServer(new TelekinesisPushPullData(TelekinesisPushPullData.tStop));
        }
    }
}
