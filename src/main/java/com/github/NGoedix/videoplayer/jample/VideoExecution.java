package com.github.NGoedix.videoplayer.jample;

import com.github.NGoedix.videoplayer.client.ClientHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;


public class VideoExecution {

    public static void initClient() {
        System.out.println("================================================= Initialize Video Execution.");

        ClientPlayNetworking.registerGlobalReceiver(ResourceLocation.of("jample:jamvideo", ':'), (client, handler, buf, responseSender) -> {
            System.out.println("Play Video");
            String url = "https://storage.jamplestudio.com/transition_travel.mp4";
            ClientHandler.stopVideoIfExists(Minecraft.getInstance());
            ClientHandler.openVideo(Minecraft.getInstance(), url, 100, false, false);
        });
    }

    public static void initServer() {
    }

}
