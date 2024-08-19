package com.github.NGoedix.watchvideo.util.displayers;

import me.srrapero720.watermedia.api.player.SyncVideoPlayer;
import net.minecraft.client.Minecraft;

public class VideoScreenDisplay {

    private final SyncVideoPlayer player;
    private final String url;
    private final int volume;
    private final int position;
    private final int optionInMode;
    private final int optionInSecs;
    private final int optionOutMode;
    private final int optionOutSecs;

    public VideoScreenDisplay(String url, int volume, int position, int optionInMode, int optionInSecs, int optionOutMode, int optionOutSecs) {
        player = new SyncVideoPlayer(null, Minecraft.getInstance());
        this.url = url;
        this.volume = volume;
        this.position = position;
        this.optionInMode = optionInMode;
        this.optionInSecs = optionInSecs;
        this.optionOutMode = optionOutMode;
        this.optionOutSecs = optionOutSecs;
    }

    public String getUrl() {
        return url;
    }

    public int getPosition() {
        return position;
    }

    public SyncVideoPlayer getPlayer() {
        return player;
    }

    public int getVolume() {
        return volume;
    }

    public int getOptionInMode() {
        return optionInMode;
    }

    public int getOptionInSecs() {
        return optionInSecs;
    }

    public int getOptionOutMode() {
        return optionOutMode;
    }

    public int getOptionOutSecs() {
        return optionOutSecs;
    }
}
