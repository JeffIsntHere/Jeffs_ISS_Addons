package jeff.iss_addons.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import io.redspace.ironsspellbooks.player.ClientInputEvents;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import jeff.iss_addons.JeffsISSAddons;
import jeff.iss_addons.network.common.TelekinesisPushPullData;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static java.lang.Math.abs;

@Mixin(ClientInputEvents.class)
public class ClientInputEventsMixin
{
    @Inject(method="clientMouseScrolled", at = @At("TAIL"))
    private static void clientMouseScrolled(InputEvent.MouseScrollingEvent event, CallbackInfo ci)
    {
        if (ClientMagicData.isCasting() && ClientMagicData.getCastingSpellId().equals("irons_spellbooks:telekinesis"))
        {
            if (event.getScrollDeltaY() > 0)
            {
                PacketDistributor.sendToServer(new TelekinesisPushPullData(TelekinesisPushPullData.sUp, event.getScrollDeltaY()));
            }
            else
            {
                PacketDistributor.sendToServer(new TelekinesisPushPullData(TelekinesisPushPullData.sDown, -event.getScrollDeltaY()));
            }
            event.setCanceled(true);
        }
    }

    @Inject(method="onUseInput", at = @At("TAIL"))
    private static void onUseInput(InputEvent.InteractionKeyMappingTriggered event, CallbackInfo ci)
    {
        if (ClientMagicData.isCasting() && event.isAttack() && ClientMagicData.getCastingSpellId().equals("irons_spellbooks:telekinesis"))
        {
            event.setSwingHand(true);
            PacketDistributor.sendToServer(new TelekinesisPushPullData(TelekinesisPushPullData.tThrow, 0));
        }
    }

    @Inject(method="handleInputEvent", at = @At("HEAD"))
    private static void handleInputEvent(int button, int action, CallbackInfo ci)
    {
        if (action != InputConstants.PRESS)
        {
            return;
        }
        if (ClientMagicData.isCasting() && button == InputConstants.MOUSE_BUTTON_MIDDLE && ClientMagicData.getCastingSpellId().equals("irons_spellbooks:telekinesis"))
        {
            PacketDistributor.sendToServer(new TelekinesisPushPullData(TelekinesisPushPullData.tStop, 0));
        }
    }
}
