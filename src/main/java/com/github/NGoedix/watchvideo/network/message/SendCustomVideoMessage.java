package com.github.NGoedix.watchvideo.network.message;

import com.github.NGoedix.watchvideo.client.ClientHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

public class SendCustomVideoMessage implements IMessage<SendCustomVideoMessage> {

    private String url;
    private int volume;
    private boolean isControlBlocked;
    private boolean canSkip;
    private int mode;
    private int position;
    private int optionInMode;
    private int optionInSecs;
    private int optionOutMode;
    private int optionOutSecs;
    private final VideoMessageType state;

    public SendCustomVideoMessage() {
        this.state = VideoMessageType.STOP;
    }

    public SendCustomVideoMessage(String url, int volume, boolean isControlBlocked, boolean canSkip, int mode, int position, int optionInMode, int optionInSecs, int optionOutMode, int optionOutSecs) {
        this.url = url;
        this.volume = volume;
        this.isControlBlocked = isControlBlocked;
        this.canSkip = canSkip;
        this.mode = mode;
        this.position = position;
        this.optionInMode = optionInMode;
        this.optionInSecs = optionInSecs;
        this.optionOutMode = optionOutMode;
        this.optionOutSecs = optionOutSecs;
        this.state = VideoMessageType.START;
    }

    @Override
    public void encode(SendCustomVideoMessage message, PacketBuffer buffer) {
        buffer.writeEnum(message.state);
        if (message.state == VideoMessageType.START) {
            buffer.writeInt(message.url.length());
            buffer.writeCharSequence(message.url, StandardCharsets.UTF_8);
            buffer.writeInt(message.volume);
            buffer.writeBoolean(message.isControlBlocked);
            buffer.writeBoolean(message.canSkip);
            buffer.writeInt(message.mode);
            buffer.writeInt(message.position);
            buffer.writeInt(message.optionInMode);
            buffer.writeInt(message.optionInSecs);
            buffer.writeInt(message.optionOutMode);
            buffer.writeInt(message.optionOutSecs);
        }
    }

    @Override
    public SendCustomVideoMessage decode(PacketBuffer buffer) {
        VideoMessageType state = buffer.readEnum(VideoMessageType.class);
        if (state == VideoMessageType.START) {
            int l = buffer.readInt();
            String url = String.valueOf(buffer.readCharSequence(l, StandardCharsets.UTF_8));
            int volume = buffer.readInt();
            boolean isControlBlocked = buffer.readBoolean();
            boolean canSkip = buffer.readBoolean();
            int mode = buffer.readInt();
            int position = buffer.readInt();
            int optionInMode = buffer.readInt();
            int optionInSecs = buffer.readInt();
            int optionOutMode = buffer.readInt();
            int optionOutSecs = buffer.readInt();

            return new SendCustomVideoMessage(url, volume, isControlBlocked, canSkip, mode, position, optionInMode, optionInSecs, optionOutMode, optionOutSecs);
        }
        return new SendCustomVideoMessage();
    }

    @Override
    public void handle(SendCustomVideoMessage message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            if (message.state == VideoMessageType.START) {
                // Fullscreen = 0, Partial = 1
                if (mode == 0) ClientHandler.openVideo(message.url, message.volume, message.isControlBlocked, message.canSkip, message.optionInMode, message.optionInSecs, message.optionOutMode, message.optionOutSecs);
//                if (mode == 1) ClientHandler.openVideo(message.url, message.volume, message.posX, message.posY, message.width, message.height);
            }
            if (message.state == VideoMessageType.STOP) ClientHandler.stopVideoIfExists();
        });
        supplier.get().setPacketHandled(true);
    }

    enum VideoMessageType {
        START,
        STOP
    }
}
