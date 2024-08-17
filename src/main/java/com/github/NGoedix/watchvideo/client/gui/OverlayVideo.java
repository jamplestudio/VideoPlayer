package com.github.NGoedix.watchvideo.client.gui;

import com.github.NGoedix.watchvideo.util.displayers.VideoScreenDisplay;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.List;

public class OverlayVideo extends Screen {

    public static List<VideoScreenDisplay> videoPlayers = new ArrayList<>();

    public OverlayVideo() {
        super(new StringTextComponent(""));
    }

    public void renderOverlay(MatrixStack stack) {
        for (VideoScreenDisplay player : videoPlayers) {

        }
    }
}
