package jeff.iss_addons.mixin;

import io.redspace.ironsspellbooks.api.spells.CastSource;
import jeff.iss_addons.JeffsISSAddons;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CastSource.class)
public class CastSourceMixin
{
    @Inject(method="consumesMana", at=@At("HEAD"), cancellable = true)
    public void jeffsissaddons$consumesMana(CallbackInfoReturnable<Boolean> cir) {
        if (!JeffsISSAddons._configServer._scrollConsumeMana.get())
        {
            return;
        }
        if ((Object)this == CastSource.SCROLL)
        {
            cir.setReturnValue(true);
        }
    }
}
