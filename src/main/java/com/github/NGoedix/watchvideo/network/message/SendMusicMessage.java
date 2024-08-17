package com.github.NGoedix.watchvideo.network.message;

import com.github.NGoedix.watchvideo.client.ClientHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

public class SendMusicMessage implements IMessage<SendMusicMessage> {

    private String url;
    private int volume;
    private final MusicMessageType state;

    public SendMusicMessage() {
        this.state = MusicMessageType.STOP;
    }

    public SendMusicMessage(String url, int volume) {
        this.url = url;
        this.volume = volume;
        this.state = MusicMessageType.START;
    }

    @Override
    public void encode(SendMusicMessage message, PacketBuffer buffer) {
        buffer.writeEnum(message.state);
        if (message.state == MusicMessageType.START) {
            buffer.writeInt(message.url.length());
            buffer.writeCharSequence(message.url, StandardCharsets.UTF_8);
            buffer.writeInt(message.volume);
        }
    }

    @Override
    public SendMusicMessage decode(PacketBuffer buffer) {
        MusicMessageType state = buffer.readEnum(MusicMessageType.class);
        if (state == MusicMessageType.START) {
            int l = buffer.readInt();
            String url = String.valueOf(buffer.readCharSequence(l, StandardCharsets.UTF_8));
            int volume = buffer.readInt();
            return new SendMusicMessage(url, volume);
        }
        return new SendMusicMessage();
    }

    @Override
    public void handle(SendMusicMessage message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            if (message.state == MusicMessageType.START) ClientHandler.playMusic(message.url, message.volume);
            if (message.state == MusicMessageType.STOP) ClientHandler.stopMusicIfPlaying();
        });
        supplier.get().setPacketHandled(true);
    }

    enum MusicMessageType {
        START,
        STOP
    }
}
