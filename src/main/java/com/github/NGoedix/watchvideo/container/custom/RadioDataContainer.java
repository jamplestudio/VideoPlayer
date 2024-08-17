package com.github.NGoedix.watchvideo.container.custom;

import com.github.NGoedix.watchvideo.container.ModContainerTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nullable;

public class RadioDataContainer extends Container {
    private final ItemStack stack;
    private String url;
    private int volume;
    private boolean isPlaying;
    private int tick;

    public RadioDataContainer(@Nullable ContainerType<?> pMenuType, int pContainerId) {
        super(pMenuType, pContainerId);
        this.stack = ItemStack.EMPTY;
    }

    public RadioDataContainer(int windowId, PlayerInventory inv, PacketBuffer data) {
//        super(ModContainerTypes.RADIO_CONTAINER.get(), windowId);
        super(null, windowId);
        this.stack = data.readItem();
        this.url = stack.getOrCreateTag().getString("url");
        this.volume = stack.getOrCreateTag().getInt("volume");
        this.isPlaying = stack.getOrCreateTag().getBoolean("isPlaying");
        this.tick = stack.getOrCreateTag().getInt("tick");
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return true;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        stack.getOrCreateTag().putString("url", url);
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
        stack.getOrCreateTag().putInt("volume", volume);
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
        stack.getOrCreateTag().putBoolean("isPlaying", isPlaying);
    }

    public int getTick() {
        return tick;
    }

    public void setTick(int tick) {
        this.tick = tick;
        stack.getOrCreateTag().putInt("tick", tick);
    }

    public ItemStack getStack() {
        return stack;
    }
}
