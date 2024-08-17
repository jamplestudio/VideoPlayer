package com.github.NGoedix.watchvideo.block.entity.custom;

import com.github.NGoedix.watchvideo.util.cache.TextureCache;
import com.github.NGoedix.watchvideo.util.config.TVConfig;
import com.github.NGoedix.watchvideo.util.displayers.IDisplay;
import com.github.NGoedix.watchvideo.util.math.geo.Vec3d;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public abstract class VideoPlayerBlockEntity extends TileEntity implements ITickableTileEntity {

    // VIDEO / MUSIC  PROPERTIES
    private String url = "";
    private boolean playing = false;
    private boolean stopped = false;

    private int volume = 100;
    private int tick = 0;

    private final boolean loop = true;

    @OnlyIn(Dist.CLIENT)
    public IDisplay display;

    @OnlyIn(Dist.CLIENT)
    public TextureCache cache;

    private boolean isOnlyMusic;

    public VideoPlayerBlockEntity(TileEntityType<?> tileEntity, boolean isOnlyMusic) {
        super(tileEntity);
        this.isOnlyMusic = isOnlyMusic;
    }

    public boolean isURLEmpty() {
        return url.isEmpty();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        if (this.level == null) return;
        this.level.blockEntityChanged(this.worldPosition, this);
        this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
    }

    public void setVolume(int volume) {
        this.volume = volume;
        if (this.level == null) return;
        this.level.blockEntityChanged(this.worldPosition, this);
        this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
    }

    public int getVolume() {
        return volume;
    }

    public boolean isPlaying() {
        return playing && !stopped;
    }

    public int getTick() {
        return tick;
    }

    public void setTick(int tick) {
        this.tick = tick;
        if (this.level == null) return;
        this.level.blockEntityChanged(this.worldPosition, this);
        this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public void stop() {
        stopped = true;
    }

    public IDisplay requestDisplay() {
        String url = getUrl();
        if (isURLEmpty()) return null;
        if (cache == null || !cache.url.equals(url)) {
            cache = TextureCache.get(url);
            if (display != null)
                display.release();
            display = null;
        }
        if (!cache.isVideo() && (!cache.ready() || cache.getError() != null))
            return null;
        if (display != null)
            return display;

        return display = cache.createDisplay(new Vec3d(worldPosition), url, volume, TVConfig.MIN_DISTANCE, TVConfig.MAX_DISTANCE, loop, playing, isOnlyMusic);
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(getBlockPos(), Registry.BLOCK_ENTITY_TYPE.getId(getType()), getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.handleUpdateTag(getBlockState(), pkt.getTag());
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT nbt) {
        loadFromNBTInternal(nbt);
        if (this.level == null) return;
        this.level.blockEntityChanged(this.worldPosition, this);
        this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.save(new CompoundNBT());
    }

    @Override
    public void setRemoved() {
        if (isClient() && display != null)
            display.release();
    }

    @Override
    public void onChunkUnloaded() {
        if (isClient() && display != null)
            display.release();
    }

    public boolean isClient() {
        return this.level != null && this.level.isClientSide;
    }

    @Override
    public CompoundNBT save(CompoundNBT pTag) {
        super.save(pTag);

        pTag.putString("url", url == null ? "" : url);
        pTag.putBoolean("playing", playing);
        pTag.putInt("tick", tick);
        pTag.putInt("volume", volume);
        return pTag;
    }

    @Override
    public void load(BlockState state, CompoundNBT pTag) {
        super.load(state, pTag);
        loadFromNBTInternal(pTag);
    }

    protected abstract void loadFromNBT(CompoundNBT nbt);

    public void loadFromNBTInternal(CompoundNBT nbt) {
        loadFromNBT(nbt);

        url = nbt.getString("url");
        playing = nbt.getBoolean("playing");
        tick = nbt.getInt("tick");
        volume = nbt.getInt("volume");
    }

    @Override
    public void tick() {
        VideoPlayerBlockEntity be = this;
        if (this.level == null) return;
        if (level.isClientSide) {
            IDisplay display = be.requestDisplay();
            if (display != null) {
                if (stopped)
                    display.stop();
                stopped = false;
                display.tick(be.url, be.volume, TVConfig.MIN_DISTANCE, TVConfig.MAX_DISTANCE, be.playing, be.loop, isOnlyMusic ? 0 : be.tick);
            }
        }
        if (be.playing)
            be.tick++;
    }
}
