package com.github.NGoedix.watchvideo.network.message;

import com.github.NGoedix.watchvideo.client.ClientHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenRadioManagerScreen implements IMessage<OpenRadioManagerScreen> {

    private ItemStack stack;
    private BlockPos blockPos;
    private String url;
    private int volume;
    private boolean isPlaying;

    public OpenRadioManagerScreen() {}

    public OpenRadioManagerScreen(BlockPos blockPos, String url, int volume, boolean isPlaying) {
        this.blockPos = blockPos;
        this.stack = null;
        this.url = url;
        this.volume = volume;
        this.isPlaying = isPlaying;
    }

    public OpenRadioManagerScreen(ItemStack itemStack, String url, int volume, boolean isPlaying) {
        this.blockPos = null;
        this.stack = itemStack;
        this.url = url;
        this.volume = volume;
        this.isPlaying = isPlaying;
    }

    @Override
    public void encode(OpenRadioManagerScreen message, PacketBuffer buffer) {
        buffer.writeBoolean(message.blockPos != null);
        if (message.blockPos == null) {
            buffer.writeItem(message.stack);
        } else {
            buffer.writeBlockPos(message.blockPos);
        }
        buffer.writeUtf(message.url);
        buffer.writeInt(message.volume);
        buffer.writeBoolean(message.isPlaying);
    }

    @Override
    public OpenRadioManagerScreen decode(PacketBuffer buffer) {
        boolean hasBlockPos = buffer.readBoolean();
        if (hasBlockPos) {
            return new OpenRadioManagerScreen(buffer.readBlockPos(), buffer.readUtf(), buffer.readInt(), buffer.readBoolean());
        }
        return new OpenRadioManagerScreen(buffer.readItem(), buffer.readUtf(), buffer.readInt(), buffer.readBoolean());
    }

    @Override
    public void handle(OpenRadioManagerScreen message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            if (message.stack == null)
                ClientHandler.openRadioGUI(message.blockPos, message.url, message.volume, message.isPlaying);
            else
                ClientHandler.openRadioGUI(message.stack, message.url, message.volume, message.isPlaying);
        });
    }
}
