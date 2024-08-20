package com.github.NGoedix.watchvideo.client;

import com.github.NGoedix.watchvideo.block.entity.custom.HandRadioBlockEntity;
import com.github.NGoedix.watchvideo.block.entity.custom.RadioBlockEntity;
import com.github.NGoedix.watchvideo.block.entity.custom.TVBlockEntity;
import com.github.NGoedix.watchvideo.client.gui.OverlayVideo;
import com.github.NGoedix.watchvideo.client.gui.RadioScreen;
import com.github.NGoedix.watchvideo.client.gui.TVVideoScreen;
import com.github.NGoedix.watchvideo.client.gui.VideoScreen;
import com.github.NGoedix.watchvideo.item.custom.HandRadioItem;
import me.lib720.caprica.vlcj.media.MediaRef;
import me.lib720.caprica.vlcj.player.base.MediaPlayer;
import me.lib720.caprica.vlcj.player.base.MediaPlayerEventListener;
import me.srrapero720.watermedia.api.player.SyncMusicPlayer;
import net.minecraft.client.Minecraft;
import me.lib720.caprica.vlcj.media.TrackType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

public class ClientHandler {

    private static final List<SyncMusicPlayer> musicPlayers = new ArrayList<>();

    @OnlyIn(Dist.CLIENT)
    public static final OverlayVideo gui = new OverlayVideo();

    public static void openVideo(String url, int volume, boolean isControlBlocked, boolean canSkip) {
        Minecraft.getInstance().setScreen(new VideoScreen(url, volume, isControlBlocked, canSkip, false));
    }

    public static void openVideo(String url, int volume, boolean isControlBlocked, boolean canSkip, int optionInMode, int optionInSecs, int optionOutMode, int optionOutSecs) {
        Minecraft.getInstance().setScreen(new VideoScreen(url, volume, isControlBlocked, canSkip, optionInMode, optionInSecs, optionOutMode, optionOutSecs));
    }

    public static void playMusic(String url, int volume) {
        SyncMusicPlayer musicPlayer = new SyncMusicPlayer();
        musicPlayers.add(musicPlayer);
        musicPlayer.setVolume(volume);
        musicPlayer.start(url);
        musicPlayer.raw().mediaPlayer().events().addMediaPlayerEventListener(new MediaPlayerEventListener() {
            @Override
            public void mediaChanged(MediaPlayer mediaPlayer, MediaRef mediaRef) {}
            @Override
            public void opening(MediaPlayer mediaPlayer) {}
            @Override
            public void buffering(MediaPlayer mediaPlayer, float v) {}
            @Override
            public void playing(MediaPlayer mediaPlayer) {}
            @Override
            public void paused(MediaPlayer mediaPlayer) {}
            @Override
            public void stopped(MediaPlayer mediaPlayer) {}
            @Override
            public void forward(MediaPlayer mediaPlayer) {}
            @Override
            public void backward(MediaPlayer mediaPlayer) {}
            @Override
            public void finished(MediaPlayer mediaPlayer) {
                musicPlayers.remove(musicPlayer);
            }
            @Override
            public void timeChanged(MediaPlayer mediaPlayer, long l) {}
            @Override
            public void positionChanged(MediaPlayer mediaPlayer, float v) {}
            @Override
            public void seekableChanged(MediaPlayer mediaPlayer, int i) {}
            @Override
            public void pausableChanged(MediaPlayer mediaPlayer, int i) {}
            @Override
            public void titleChanged(MediaPlayer mediaPlayer, int i) {}
            @Override
            public void snapshotTaken(MediaPlayer mediaPlayer, String s) {}
            @Override
            public void lengthChanged(MediaPlayer mediaPlayer, long l) {}
            @Override
            public void videoOutput(MediaPlayer mediaPlayer, int i) {}
            @Override
            public void scrambledChanged(MediaPlayer mediaPlayer, int i) {}
            @Override
            public void elementaryStreamAdded(MediaPlayer mediaPlayer, TrackType trackType, int i) {}
            @Override
            public void elementaryStreamDeleted(MediaPlayer mediaPlayer, TrackType trackType, int i) {}
            @Override
            public void elementaryStreamSelected(MediaPlayer mediaPlayer, TrackType trackType, int i) {}
            @Override
            public void corked(MediaPlayer mediaPlayer, boolean b) {}
            @Override
            public void muted(MediaPlayer mediaPlayer, boolean b) {}
            @Override
            public void volumeChanged(MediaPlayer mediaPlayer, float v) {}
            @Override
            public void audioDeviceChanged(MediaPlayer mediaPlayer, String s) {}
            @Override
            public void chapterChanged(MediaPlayer mediaPlayer, int i) {}
            @Override
            public void error(MediaPlayer mediaPlayer) {}
            @Override
            public void mediaPlayerReady(MediaPlayer mediaPlayer) {}
        });
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
