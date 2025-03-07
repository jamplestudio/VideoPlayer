package com.github.NGoedix.videoplayer.jample;

import com.github.NGoedix.videoplayer.client.ClientHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;


public class RemoteVideoExecution {

    private static final @NotNull Gson GSON = new GsonBuilder().create();

    private static final @NotNull ResourceLocation IDENTITY_EXECUTE = new ResourceLocation("jamvideo", "remote_execute");
    private static final @NotNull ResourceLocation IDENTITY_STOP = new ResourceLocation("jamvideo", "remote_execute");

    public static void initClient() {
        ClientPlayNetworking.registerGlobalReceiver(IDENTITY_EXECUTE, (client, handler, buf, responseSender) -> {
            String json = buf.readUtf();
            JamVideoPacket packet = GSON.fromJson(json, JamVideoPacket.class);
            client.execute(() -> {
                System.out.println("Remote Video Executed: " + packet.toString());
                ClientHandler.stopVideoIfExists(Minecraft.getInstance());
                ClientHandler.openVideo(Minecraft.getInstance(), packet.url(), packet.volume(), packet.isControlBlocked(), packet.canSkip(), () -> {
                    // 영상 재생 완료 시 서버로 콜백
                    FriendlyByteBuf responseBuf = PacketByteBufs.create();
                    responseBuf.writeInt(json.length());
                    responseBuf.writeUtf(json);
                    ClientPlayNetworking.send(IDENTITY_EXECUTE, responseBuf);
                });
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(IDENTITY_STOP, (client, handler, buf, responseSender) -> {
            client.execute(() -> {
                System.out.println("Remote Video Stopped.");
                ClientHandler.stopVideoIfExists(Minecraft.getInstance());
            });
        });
    }

}
