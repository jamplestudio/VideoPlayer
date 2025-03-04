package com.github.NGoedix.videoplayer.jample;

import com.github.NGoedix.videoplayer.client.ClientHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;


public class RemoteVideoExecution {

    private static final @NotNull Gson GSON = new GsonBuilder().create();

    public static void initClient() {
        ClientPlayNetworking.registerGlobalReceiver(new ResourceLocation("jamvideo", "remote_execute"), (client, handler, buf, responseSender) -> {
            String json = buf.readUtf();
            JamVideoPacket packet = GSON.fromJson(json, JamVideoPacket.class);
            client.execute(() -> {
                System.out.println("Remote Video Executed: " + packet.toString());
                ClientHandler.stopVideoIfExists(Minecraft.getInstance());
                ClientHandler.openVideo(Minecraft.getInstance(), packet.url(), packet.volume(), packet.isControlBlocked(), packet.canSkip());
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(new ResourceLocation("jamvideo", "remote_stop"), (client, handler, buf, responseSender) -> {
            client.execute(() -> {
                System.out.println("Remote Video Stopped.");
                ClientHandler.stopVideoIfExists(Minecraft.getInstance());
            });
        });
    }

}
