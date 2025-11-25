package jeff.iss_addons.network.common;

import io.netty.buffer.ByteBuf;
import jeff.iss_addons.JeffsISSAddons;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record TelekinesisPushPullData(boolean push) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<TelekinesisPushPullData> _TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(JeffsISSAddons.MODID, "telekinesisPushPullData"));

    public static final StreamCodec<ByteBuf, TelekinesisPushPullData> _STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            TelekinesisPushPullData::push,
            TelekinesisPushPullData::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type()
    {
        return _TYPE;
    }
}
