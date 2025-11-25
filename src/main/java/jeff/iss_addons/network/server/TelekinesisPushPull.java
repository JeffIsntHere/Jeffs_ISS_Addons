package jeff.iss_addons.network.server;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import jeff.iss_addons.ExtendedTelekinesisData;
import jeff.iss_addons.network.common.TelekinesisPushPullData;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class TelekinesisPushPull
{
    public static void handle(TelekinesisPushPullData telekinesisPushPullData, IPayloadContext payloadContext)
    {
        var magicData = MagicData.getPlayerMagicData(payloadContext.player());
        if (magicData.getAdditionalCastData() instanceof ExtendedTelekinesisData extendedTelekinesisData)
        {
            payloadContext.enqueueWork(() -> {extendedTelekinesisData.addFlag(telekinesisPushPullData.data());});
        }
    }
}
