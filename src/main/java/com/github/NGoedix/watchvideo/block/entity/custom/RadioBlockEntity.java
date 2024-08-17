package com.github.NGoedix.watchvideo.block.entity.custom;

import com.github.NGoedix.watchvideo.block.entity.ModBlockEntities;
import com.github.NGoedix.watchvideo.network.PacketHandler;
import com.github.NGoedix.watchvideo.network.message.OpenRadioManagerScreen;
import com.github.NGoedix.watchvideo.network.message.RadioMessage;
import me.lib720.caprica.vlcj.player.component.CallbackMediaPlayerComponent;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

public class RadioBlockEntity extends VideoPlayerBlockEntity {

    private UUID playerUsing;

    public RadioBlockEntity() {
        super(ModBlockEntities.RADIO_BLOCK_ENTITY.get(), true);
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
        if (!this.level.isClientSide)
            PacketHandler.sendToClient(new RadioMessage(worldPosition, isPlaying()), level, worldPosition);
    }

    @Override
    public void tick() {
        super.tick();
        if (level != null && isPlaying() && getTick() % 10 == 0)
            level.addParticle(ParticleTypes.NOTE, (double)getBlockPos().getX() + 0.5D, (double)getBlockPos().getY() + 0.5D, (double)getBlockPos().getZ() + 0.5D, 1.0f, 0.0D, 0.0D);
    }
}
