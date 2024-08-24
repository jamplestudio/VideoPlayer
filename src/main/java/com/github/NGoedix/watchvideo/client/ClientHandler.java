package com.github.NGoedix.watchvideo.client;

import com.github.NGoedix.watchvideo.block.entity.custom.HandRadioBlockEntity;
import com.github.NGoedix.watchvideo.block.entity.custom.RadioBlockEntity;
import com.github.NGoedix.watchvideo.block.entity.custom.TVBlockEntity;
import com.github.NGoedix.watchvideo.client.gui.RadioScreen;
import com.github.NGoedix.watchvideo.client.gui.TVVideoScreen;
import com.github.NGoedix.watchvideo.client.gui.VideoScreen;
import com.github.NGoedix.watchvideo.item.custom.HandRadioItem;
import me.srrapero720.watermedia.api.player.SyncMusicPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.List;

public class ClientHandler {

    private static final List<SyncMusicPlayer> musicPlayers = new ArrayList<>();

    public static void openVideo(String url, int volume, boolean isControlBlocked, boolean canSkip) {
        Minecraft.getInstance().setScreen(new VideoScreen(url, volume, isControlBlocked, canSkip, false));
    }

    public static void openVideo(String url, int volume, boolean isControlBlocked, boolean canSkip, int optionInMode, int optionInSecs, int optionOutMode, int optionOutSecs) {
        Minecraft.getInstance().setScreen(new VideoScreen(url, volume, isControlBlocked, canSkip, optionInMode, optionInSecs, optionOutMode, optionOutSecs));
    }

    public static void playMusic(String url, int volume) {
        // Until any callback in SyncMusicPlayer I will check if the music is playing when added other music player
        for (SyncMusicPlayer musicPlayer : musicPlayers) {
            if (musicPlayer.isPlaying()) {
                musicPlayer.stop();
                musicPlayer.release();
                musicPlayers.remove(musicPlayer);
            }
        }

        // Add the new player
        SyncMusicPlayer musicPlayer = new SyncMusicPlayer();
        musicPlayers.add(musicPlayer);
        musicPlayer.setVolume(volume);
        musicPlayer.start(url);
    }

    public static void stopMusicIfPlaying() {
        for (SyncMusicPlayer musicPlayer : musicPlayers) {
            musicPlayer.stop();
            musicPlayer.release();
        }
        musicPlayers.clear();
    }

    public static void stopVideoIfExists() {
        if (Minecraft.getInstance().screen instanceof VideoScreen) {
            VideoScreen screen = (VideoScreen) Minecraft.getInstance().screen;
            screen.onClose();
        }
    }

    public static void manageRadio(String url, BlockPos pos, boolean playing) {
        BlockEntity be = Minecraft.getInstance().level.getBlockEntity(pos);
        if (be instanceof RadioBlockEntity) {
            RadioBlockEntity tv = (RadioBlockEntity) be;
            tv.setUrl(url);
            tv.setPlaying(playing);

            tv.notifyPlayer();
        }

        if (be instanceof HandRadioBlockEntity) {
            HandRadioBlockEntity tv = (HandRadioBlockEntity) be;
            tv.setUrl(url);
            tv.setPlaying(playing);

            tv.notifyPlayer();
        }
    }

    public static void manageVideo(String url, BlockPos pos, boolean playing, int tick) {
        BlockEntity be = Minecraft.getInstance().level.getBlockEntity(pos);
        if (be instanceof TVBlockEntity) {
            TVBlockEntity tv = (TVBlockEntity) be;
            tv.setUrl(url);
            tv.setPlaying(playing);
            if (tv.getTick() - 40 > tick || tv.getTick() + 40 < tick)
                tv.setTick(tick);
            if (tv.requestDisplay() != null) {
                if (playing)
                    tv.requestDisplay().resume(tv.getTick());
                else
                    tv.requestDisplay().pause(tv.getTick());
            }
        }
    }

    public static void openVideoGUI(BlockPos pos, String url, int volume, int tick, boolean isPlaying) {
        BlockEntity be = Minecraft.getInstance().level.getBlockEntity(pos);
        if (be instanceof TVBlockEntity) {
            TVBlockEntity tv = (TVBlockEntity) be;
            tv.setUrl(url);
            tv.setTick(tick);
            tv.setVolume(volume);
            tv.setPlaying(isPlaying);
            Minecraft.getInstance().setScreen(new TVVideoScreen(be));
        }
    }

    public static void openRadioGUI(BlockPos pos, String url, int volume, boolean isPlaying) {
        BlockEntity be = Minecraft.getInstance().level.getBlockEntity(pos);
        if (be instanceof RadioBlockEntity) {
            RadioBlockEntity tv = (RadioBlockEntity) be;
            tv.setUrl(url);
            tv.setVolume(volume);
            tv.setPlaying(isPlaying);
            Minecraft.getInstance().setScreen(new RadioScreen(be));
        }
    }

    public static void openRadioGUI(ItemStack stack, String url, int volume, boolean isPlaying) {
        if (stack.getItem() instanceof HandRadioItem) {
            Minecraft.getInstance().setScreen(new RadioScreen(stack));
        }
    }
}
