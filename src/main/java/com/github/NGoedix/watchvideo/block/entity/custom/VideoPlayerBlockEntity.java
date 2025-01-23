package com.github.NGoedix.watchvideo.block.entity.custom;

import com.github.NGoedix.watchvideo.util.displayers.Display;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.watermedia.api.image.ImageAPI;
import org.watermedia.api.image.ImageCache;

import javax.annotation.Nullable;
import java.net.URI;

public abstract class VideoPlayerBlockEntity extends TileEntity implements ITickableTileEntity {

    // VIDEO / MUSIC  PROPERTIES
    private String url = "";
    private boolean playing = false;
    private boolean stopped = false;

    private int volume = 100;
    private int tick = 0;

    @OnlyIn(Dist.CLIENT)
    public Display display;

    @OnlyIn(Dist.CLIENT)
    public ImageCache imageCache;

    private final Display.DisplayType displayMode;

    public VideoPlayerBlockEntity(TileEntityType<?> tileEntity, Display.DisplayType displayMode) {
        super(tileEntity);
        this.displayMode = displayMode;
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

    public Display requestDisplay() {
        if (isURLEmpty()) return null;

        if (imageCache == null || (!isURLEmpty() && !imageCache.uri.equals(URI.create(url)))) {
            imageCache = ImageAPI.getCache(URI.create(url), Minecraft.getInstance());
            releaseDisplay();
        }

        switch (imageCache.getStatus()) {
            case LOADING:
            case FAILED:
            case READY:
                if (this.display != null) return this.display;
                return this.display = new Display(this, URI.create(url), displayMode);

            case WAITING:
                this.releaseDisplay();
                this.imageCache.load();
                return this.display;

            case FORGOTTEN:
                this.imageCache = null;
                return null;

            default:
                return null;
        }
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
        if (isClient()) releaseDisplay();
    }

    @Override
    public void onChunkUnloaded() {
        if (isClient()) releaseDisplay();
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
            Display display = be.requestDisplay();
            if (display != null) {
                if (stopped) {
                    display.stop();
                }
                stopped = false;
                display.tick(be.tick);
            }
        }
        if (be.playing)
            be.tick++;
    }

    public void releaseDisplay() {
        if (display != null) {
            display.release();
            display = null;
        }
    }
}
