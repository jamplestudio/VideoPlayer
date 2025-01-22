package com.github.NGoedix.watchvideo.block.entity.custom;

import com.github.NGoedix.watchvideo.block.entity.ModBlockEntities;
import com.github.NGoedix.watchvideo.network.PacketHandler;
import com.github.NGoedix.watchvideo.network.message.OpenRadioManagerScreen;
import com.github.NGoedix.watchvideo.network.message.RadioMessage;
import com.github.NGoedix.watchvideo.util.displayers.Display;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

public class HandRadioBlockEntity extends VideoPlayerBlockEntity {

    private UUID playerUsing;

    public HandRadioBlockEntity() {
        super(ModBlockEntities.RADIO_BLOCK_ENTITY.get(), Display.DisplayType.MUSIC);
    }

    public void tryOpen(World level, BlockPos blockPos, PlayerEntity player) {
        // If none is using the block, open the GUI
        if (playerUsing == null) {
            setBeingUsed(player.getUUID());
            openRadioManagerGUI(blockPos, player);
            return;
        }

        // If the player that use the block is connected, don't open the GUI
        for (PlayerEntity p : level.players())
            if (p.getUUID() == playerUsing)
                return;

        // Open the GUI
        openRadioManagerGUI(blockPos, player);
    }

    public void openRadioManagerGUI(BlockPos blockPos, PlayerEntity player) {
        setBeingUsed(player.getUUID());
        PacketHandler.sendTo(new OpenRadioManagerScreen(blockPos, getUrl(), getVolume(), isPlaying()), player);
    }

    public void setBeingUsed(UUID player) {
        this.playerUsing = player;
        setChanged();
    }

    @Override
    public CompoundNBT save(CompoundNBT pTag) {
        super.save(pTag);
        pTag.putUUID("beingUsed", playerUsing == null ? new UUID(0, 0) : playerUsing);
        return pTag;
    }

    @Override
    public void load(BlockState state, CompoundNBT pTag) {
        super.load(state, pTag);
        loadFromNBT(pTag);
    }

    @Override
    protected void loadFromNBT(CompoundNBT nbt) {
        playerUsing = nbt.getUUID("beingUsed");
    }

    public void notifyPlayer() {
        if (this.level == null) return;
        PacketHandler.sendToClient(new RadioMessage(getUrl(), worldPosition, isPlaying()), level, worldPosition);
    }
}
