package jeff.iss_addons.network.common;

import io.netty.buffer.ByteBuf;
import jeff.iss_addons.JeffsISSAddons;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record TelekinesisPushPullData(int data, double one) implements CustomPacketPayload
{
    public static final int tThrow = 1;
    public static final int tStop = 2;
    public static final int sUp = 4;
    public static final int sDown = 8;

    public static final CustomPacketPayload.Type<TelekinesisPushPullData> _TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(JeffsISSAddons.MODID, "telekinesisPushPullData"));

    public static final StreamCodec<ByteBuf, TelekinesisPushPullData> _STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            TelekinesisPushPullData::data,
            ByteBufCodecs.DOUBLE,
            TelekinesisPushPullData::one,
            TelekinesisPushPullData::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type()
    {
        return _TYPE;
    }
}
