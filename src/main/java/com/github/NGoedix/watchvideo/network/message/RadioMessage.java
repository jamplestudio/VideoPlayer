package com.github.NGoedix.watchvideo.network.message;

import com.github.NGoedix.watchvideo.client.ClientHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class RadioMessage implements IMessage<RadioMessage> {

    private String url;
    private BlockPos pos;
    private boolean playing;

    public RadioMessage() {}

    public RadioMessage(String url, BlockPos pos, boolean playing) {
        this.url = url;
        this.pos = pos;
        this.playing = playing;
    }

    @Override
    public void encode(RadioMessage message, PacketBuffer buffer) {
        buffer.writeUtf(message.url);
        buffer.writeBlockPos(message.pos);
        buffer.writeBoolean(message.playing);
    }

    @Override
    public RadioMessage decode(PacketBuffer buffer) {
        return new RadioMessage(buffer.readUtf(), buffer.readBlockPos(), buffer.readBoolean());
    }

    @Override
    public void handle(RadioMessage message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> ClientHandler.manageRadio(message.url, message.pos, message.playing));
        supplier.get().setPacketHandled(true);
    }
}
