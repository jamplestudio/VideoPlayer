package com.github.NGoedix.videoplayer.network;

import com.github.NGoedix.videoplayer.network.packet.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.LevelChunk;

public class PacketHandler {

    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(VideoUpdateMessage.ID, VideoUpdateMessage::receive);
        ServerPlayNetworking.registerGlobalReceiver(RadioUpdateMessage.ID, RadioUpdateMessage::receive);
    }

    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(FrameVideoMessage.ID, FrameVideoMessage::receive);
        ClientPlayNetworking.registerGlobalReceiver(OpenVideoManagerScreenMessage.ID, OpenVideoManagerScreenMessage::receive);
        ClientPlayNetworking.registerGlobalReceiver(OpenRadioManagerScreenMessage.ID, OpenRadioManagerScreenMessage::receive);
        ClientPlayNetworking.registerGlobalReceiver(SendVideoMessage.ID, SendVideoMessage::receive);
        ClientPlayNetworking.registerGlobalReceiver(SendCustomVideoMessage.ID, SendCustomVideoMessage::receive);
        ClientPlayNetworking.registerGlobalReceiver(SendMusicMessage.ID, SendMusicMessage::receive);
    }

    // SEND MESSAGES S2C
    public static void sendS2CSendVideoStart(ServerPlayer player, String url, int volume, boolean controlBlocked, boolean canSkip) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeEnum(SendVideoMessage.VideoMessageType.START);
        buf.writeUtf(url);
        buf.writeInt(volume);
        buf.writeBoolean(controlBlocked);
        buf.writeBoolean(canSkip);
        ServerPlayNetworking.send(player, SendVideoMessage.ID, buf);
    }

    public static void sendS2CSendVideoStop(ServerPlayer player) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeEnum(SendVideoMessage.VideoMessageType.STOP);
        ServerPlayNetworking.send(player, SendVideoMessage.ID, buf);
    }

    public static void sendS2CSendMusicStart(ServerPlayer player, String url, int volume) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeEnum(SendMusicMessage.MusicMessageType.START);
        buf.writeUtf(url);
        buf.writeInt(volume);
        ServerPlayNetworking.send(player, SendMusicMessage.ID, buf);
    }

    public static void sendS2CSendMusicStop(ServerPlayer player) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeEnum(SendMusicMessage.MusicMessageType.STOP);
        ServerPlayNetworking.send(player, SendMusicMessage.ID, buf);
    }

    public static void sendS2CSendVideoStart(ServerPlayer player, String url, int volume, boolean controlBlocked, boolean canSkip, int mode, int position, int optionInMode, int optionInSecs, int optionOutMode, int optionOutSecs) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeEnum(SendCustomVideoMessage.VideoMessageType.START);
        buf.writeUtf(url);
        buf.writeInt(volume);
        buf.writeBoolean(controlBlocked);
        buf.writeBoolean(canSkip);
        buf.writeInt(mode);
        buf.writeInt(position);
        buf.writeInt(optionInMode);
        buf.writeInt(optionInSecs);
        buf.writeInt(optionOutMode);
        buf.writeInt(optionOutSecs);
        ServerPlayNetworking.send(player, SendCustomVideoMessage.ID, buf);
    }

    public static void sendS2COpenVideoManagerScreen(ServerPlayer player, BlockPos pos, String url, int volume, int tick, boolean isPlaying) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(pos);
        buf.writeUtf(url);
        buf.writeInt(volume);
        buf.writeInt(tick);
        buf.writeBoolean(isPlaying);
        ServerPlayNetworking.send(player, OpenVideoManagerScreenMessage.ID, buf);
    }

    public static void sendS2COpenRadioManagerScreen(ServerPlayer player, BlockPos pos, String url, int volume, boolean isPlaying) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(pos);
        buf.writeUtf(url);
        buf.writeInt(volume);
        buf.writeBoolean(isPlaying);
        ServerPlayNetworking.send(player, OpenRadioManagerScreenMessage.ID, buf);
    }

    public static void sendS2CFrameVideoMessage(LevelChunk chunk, String url, BlockPos pos, boolean playing, int tick) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeUtf(url);
        buf.writeBlockPos(pos);
        buf.writeBoolean(playing);
        buf.writeInt(tick);

        for (ServerPlayer player : PlayerLookup.tracking((ServerLevel) chunk.getLevel(), chunk.getPos()))
            ServerPlayNetworking.send(player, FrameVideoMessage.ID, buf);
    }

    public static void sendS2CRadioMessage(LevelChunk chunk, String url, BlockPos pos, boolean playing) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeUtf(url);
        buf.writeBlockPos(pos);
        buf.writeBoolean(playing);

        for (ServerPlayer player : PlayerLookup.tracking((ServerLevel) chunk.getLevel(), chunk.getPos()))
            ServerPlayNetworking.send(player, RadioMessage.ID, buf);
    }

    public static void sendC2SVideoUpdateMessage(BlockPos pos, String url, int volume, int tick, boolean isPlaying, boolean stopped, boolean exit) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(pos);
        buf.writeUtf(url);
        buf.writeInt(volume);
        buf.writeInt(tick);
        buf.writeBoolean(isPlaying);
        buf.writeBoolean(stopped);
        buf.writeBoolean(exit);
        ClientPlayNetworking.send(VideoUpdateMessage.ID, buf);
    }

    public static void sendC2SRadioUpdateMessage(BlockPos pos, String url, int volume, int tick, boolean isPlaying, boolean exit) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(pos);
        buf.writeUtf(url);
        buf.writeInt(volume);
        buf.writeInt(tick);
        buf.writeBoolean(isPlaying);
        buf.writeBoolean(exit);
        ClientPlayNetworking.send(RadioUpdateMessage.ID, buf);
    }
}
