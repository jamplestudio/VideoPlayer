package com.github.NGoedix.watchvideo.client.gui;

import com.github.NGoedix.watchvideo.util.displayers.VideoScreenDisplay;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;

import java.util.ArrayList;
import java.util.List;

public class OverlayVideo extends Screen {

    public static List<VideoScreenDisplay> videoPlayers = new ArrayList<>();

    public OverlayVideo() {
        super(new TextComponent(""));
    }

    public void renderOverlay(PoseStack stack) {
        for (VideoScreenDisplay player : videoPlayers) {

        }
    }
}
