package com.github.NGoedix.watchvideo.client.gui;

import com.github.NGoedix.watchvideo.Reference;
import com.github.NGoedix.watchvideo.VideoPlayer;
import com.github.NGoedix.watchvideo.util.math.VideoMathUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.lib720.caprica.vlcj.player.base.State;
import me.srrapero720.watermedia.api.image.ImageAPI;
import me.srrapero720.watermedia.api.image.ImageRenderer;
import me.srrapero720.watermedia.api.math.MathAPI;
import me.srrapero720.watermedia.api.player.SyncVideoPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.loading.FMLLoader;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class VideoScreen extends Screen {
    private static final DateFormat FORMAT = new SimpleDateFormat("HH:mm:ss");
    static {
        FORMAT.setTimeZone(TimeZone.getTimeZone("GMT-00:00"));
    }

    // STATUS
    private int tick = 0;
    private int closingOnTick = -1;
    private float fadeLevel = 0;
    private float fadeStep10 = 0;
    private float fadeStep5 = 0;
    private boolean started;
    private boolean closing = false;
    private float volume;

    // CONTROL
    private final boolean controlBlocked;
    private final boolean canSkip;
    private int optionInMode;
    private int optionInSecs;
    private int optionOutMode;
    private int optionOutSecs;

    // TOOLS
    private final SyncVideoPlayer player;

    // VIDEO INFO
    private int videoTexture = -1;

    public VideoScreen(String url, int volume, boolean controlBlocked, boolean canSkip, int optionInMode, int optionInSecs, int optionOutMode, int optionOutSecs) {
        this(url, volume, controlBlocked, canSkip, optionInMode != -1 && optionInSecs > 0);
        this.optionInMode = optionInMode;
        this.optionInSecs = optionInSecs;
        this.optionOutMode = optionOutMode;
        this.optionOutSecs = optionOutSecs;
    }

    public VideoScreen(String url, int volume, boolean controlBlocked, boolean canSkip, boolean fadeIn) {
        super(new StringTextComponent(""));

        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getSoundManager().pause();

        this.volume = volume;
        this.controlBlocked = controlBlocked;
        this.canSkip = canSkip;
        this.optionInMode = -1;
        this.optionInSecs = -1;
        this.optionOutMode = -1;
        this.optionOutSecs = -1;

        this.player = new SyncVideoPlayer(null, minecraft);
        Reference.LOGGER.info("Playing video (" + (!controlBlocked ? "not" : "") + "blocked) (" + url + " with volume: " + (int) (minecraft.options.getSoundSourceVolume(SoundCategory.MASTER) * volume));

        player.setVolume((int) (minecraft.options.getSoundSourceVolume(SoundCategory.MASTER) * volume));
        if (!fadeIn) {
            started = true;
            player.start(url);
        } else {
            player.startPaused(url);
        }
    }

    @Override
    public void render(MatrixStack stack, int pMouseX, int pMouseY, float pPartialTicks) {
        if (started && !closing) {
            videoTexture = player.getGlTexture();
        }

        // Handle easing for fade-in
        if ((tick < optionInSecs * 20 && optionInMode != -1) || !started) {
            float t = tick / (float) (optionInSecs * 20);
            fadeLevel = (float) applyEasing(optionInMode, 0, 1, t);
            if (!started && fadeLevel >= 1.0) {
                player.play();
                started = true;
                fadeLevel = 0;
            }
        }

        // Handle easing for fade-out
        if (closing || player.isEnded() || player.isBroken()) {
            if (optionOutMode == -1) {
                System.out.println("Closing without fading out");
                onClose();
            }
            if (optionInMode != -1 || closing) {
                closing = true;
                if (closingOnTick == -1) closingOnTick = tick + optionOutSecs * 20;
                float t = (tick - closingOnTick + optionOutSecs * 20) / (float)(optionOutSecs * 20);
                fadeLevel = (float) applyEasing(optionOutMode, 1, 0, t);
                renderBlackBackground(stack);
                if (fadeLevel == 0) onClose();
                return;
            }
        }

        // BLACK SCREEN
        if (!player.isPaused())
            renderBlackBackground(stack);

        if (!started) return;

        boolean playingState = (player.isPlaying() || player.isPaused());

        // RENDER VIDEO
        if (playingState || player.isStopped() || player.isEnded()) {
            renderTexture(stack, videoTexture);
        }

        // BLACK SCREEN
        if (!player.isPaused())
            renderBlackBackground(stack);

        // RENDER GIF
        if (!player.isPlaying() || !player.isPlaying()) {
            if (player.isPaused() && player.isPaused()) {
                renderIcon(stack, VideoPlayer.pausedImage());
            } else {
                renderIcon(stack, ImageAPI.loadingGif());
            }
        }

        // Render icons 10 and -5 seconds
        renderStepIcon(stack, pPartialTicks, true);
        renderStepIcon(stack, pPartialTicks, false);

        // DEBUG RENDERING
        if (!FMLLoader.isProduction()) {
            draw(stack, String.format("State: %s", player.getRawPlayerState().name()), getHeightCenter(-12));
            draw(stack, String.format("Time: %s (%s) / %s (%s)", FORMAT.format(new Date(player.getTime())), player.getTime(), FORMAT.format(new Date(player.getDuration())), player.getDuration()), getHeightCenter(0));
            draw(stack, String.format("Media Duration: %s (%s)", FORMAT.format(new Date(player.getMediaInfoDuration())), player.getMediaInfoDuration()), getHeightCenter(12));
        }
    }

    private void renderBlackBackground(MatrixStack stack) {
        RenderSystem.enableBlend();
        fill(stack, 0, 0, width, height, MathAPI.argb((int) (fadeLevel * 255), 0, 0, 0));
        RenderSystem.disableBlend();
    }

    private void renderTexture(MatrixStack stack, int texture) {
        if (player.getDimensions() == null) return; // Checking if video available

        RenderSystem.enableBlend();
        fill(stack, 0, 0, width, height, MathAPI.argb(255, 0, 0, 0));
        RenderSystem.disableBlend();

        RenderSystem.bindTexture(texture);

        // Get video dimensions
        Dimension videoDimensions = player.getDimensions();
        double videoWidth = videoDimensions.getWidth();
        double videoHeight = videoDimensions.getHeight();

        // Calculate aspect ratios for both the screen and the video
        float screenAspectRatio = (float) width / height;
        float videoAspectRatio = (float) ((float) videoWidth / videoHeight);

        // New dimensions for rendering video texture
        int renderWidth, renderHeight;

        // If video's aspect ratio is greater than screen's, it means video's width needs to be scaled down to screen's width
        if(videoAspectRatio > screenAspectRatio) {
            renderWidth = width;
            renderHeight = (int) (width / videoAspectRatio);
        } else {
            renderWidth = (int) (height * videoAspectRatio);
            renderHeight = height;
        }

        int xOffset = (width - renderWidth) / 2; // xOffset for centering the video
        int yOffset = (height - renderHeight) / 2; // yOffset for centering the video

        RenderSystem.enableBlend();
        RenderSystem.clearColor(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        AbstractGui.blit(stack, xOffset, yOffset, 0.0F, 0.0F, renderWidth, renderHeight, renderWidth, renderHeight);
        RenderSystem.disableBlend();
    }

    private void renderIcon(MatrixStack stack, ImageRenderer image) {
        RenderSystem.enableBlend();
        RenderSystem.bindTexture(image.texture(tick, 1, true));
        AbstractGui.blit(stack, width - 36, height - 36 , 0, 0, 36, 36, 28, 28);
        RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        RenderSystem.disableBlend();
    }

    private void renderStepIcon(MatrixStack stack, float pPartialTicks, boolean forward) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.bindTexture(forward ? VideoPlayer.step10Image().texture(tick, 1, true) : VideoPlayer.step5Image().texture(tick, 1, true));
        float alpha = forward ? fadeStep10 : fadeStep5;
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, alpha);
        AbstractGui.blit(stack, width / 2 + (forward ? 70 : -134), height / 2 - 32, 0, 0, 64, 64, 64, 64);
        if (forward) {
            fadeStep10 = Math.max(fadeStep10 - (pPartialTicks / 8), 0.0f);
        } else {
            fadeStep5 = Math.max(fadeStep5 - (pPartialTicks / 8), 0.0f);
        }
        RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        RenderSystem.disableBlend();
    }

    private double applyEasing(int mode, double start, double end, double t) {
        switch (mode) {
            case 0:
                return VideoMathUtil.easeIn(start, end, t);
            case 1:
                return VideoMathUtil.easeOut(start, end, t);
            default:
                return end;
        }
    }

    private int getHeightCenter(int offset) {
        return (height / 2) + offset;
    }

    private void draw(MatrixStack stack, String text, int height) {
        drawString(stack, Minecraft.getInstance().font, text, 5, height, 0xffffff);
    }

    @Override
    public boolean isPauseScreen() { return false; }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        // Shift + ESC (Exit)
        if (canSkip && hasShiftDown() && pKeyCode == 256) {
            this.onClose();
        }

        // Up arrow key (Volume)
        if (pKeyCode == 265) {
            if (volume <= 120) {
                volume += 5;
            } else {
                volume = 125;
                float masterVolume = Minecraft.getInstance().options.getSoundSourceVolume(SoundCategory.MASTER);
                Minecraft.getInstance().options.setSoundCategoryVolume(SoundCategory.MASTER, masterVolume <= 0.95 ? masterVolume + 0.1F : 1.0F);
            }

            float actualVolume = Minecraft.getInstance().options.getSoundSourceVolume(SoundCategory.MASTER);
            float newVolume = volume * actualVolume;
            Reference.LOGGER.info("Volume UP to: " + newVolume);
            player.setVolume((int) newVolume);
        }

        // Down arrow key (Volume)
        if (pKeyCode == 264) {
            if (volume >= 5) {
                volume -= 5;
            } else {
                volume = 0;
            }
            float actualVolume = Minecraft.getInstance().options.getSoundSourceVolume(SoundCategory.MASTER);
            float newVolume = volume * actualVolume;
            Reference.LOGGER.info("Volume DOWN to: " + newVolume);
            player.setVolume((int) newVolume);
        }

        // M to mute
        if (pKeyCode == 77) {
            if (player.isMuted()) {
                player.unmute();
            } else {
                player.mute();
            }
        }

        // If control blocked can't modify the video time
        if (controlBlocked) return super.keyPressed(pKeyCode, pScanCode, pModifiers);

        // Shift + Right arrow key (Forwards)
        if (hasShiftDown() && pKeyCode == 262) {
            player.seekTo(player.getTime() + 10000);
            fadeStep10 = 1;
        }

        // Shift + Left arrow key (Backwards)
        if (hasShiftDown() && pKeyCode == 263) {
            player.seekTo(player.getTime() - 5000);
            fadeStep5 = 1;
        }

        // Shift + Space (Pause / Play)
        if (hasShiftDown() && pKeyCode == 32) {
            player.togglePlayback();
        }

        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void onClose() {
        super.onClose();
        if (started) {
            started = false;
            player.stop();
            Minecraft.getInstance().getSoundManager().resume();
            GlStateManager._deleteTexture(videoTexture);
            player.release();
        }
    }

    @Override
    protected void init() {
        if (Minecraft.getInstance().screen != null) {
            this.width = Minecraft.getInstance().screen.width;
            this.height = Minecraft.getInstance().screen.height;
        }
        super.init();
    }

    @Override
    public void tick() {
        super.tick();
        tick++;
    }
}

