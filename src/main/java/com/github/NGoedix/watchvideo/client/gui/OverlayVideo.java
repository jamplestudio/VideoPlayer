package com.github.NGoedix.watchvideo.client.gui;

import com.github.NGoedix.watchvideo.util.displayers.VideoScreenDisplay;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
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
