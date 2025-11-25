package jeff.iss_addons.network.server;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import jeff.iss_addons.ExtendedTelekinesisData;
import jeff.iss_addons.JeffsISSAddons;
import jeff.iss_addons.network.common.TelekinesisPushPullData;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@Mod(value = JeffsISSAddons.MODID, dist = Dist.DEDICATED_SERVER)
public class TelekinesisPushPull
{
    public static void handle(TelekinesisPushPullData telekinesisPushPullData, IPayloadContext payloadContext)
    {
        var magicData = MagicData.getPlayerMagicData(payloadContext.player());
        if (magicData.getAdditionalCastData() instanceof ExtendedTelekinesisData extendedTelekinesisData)
        {
            if (telekinesisPushPullData.push())
            {
                payloadContext.enqueueWork(() -> {
                    extendedTelekinesisData._push = true;
                });
            }
            else
            {
                payloadContext.enqueueWork(() -> {
                    extendedTelekinesisData._pull = true;
                });
            }
        }
    }
}
