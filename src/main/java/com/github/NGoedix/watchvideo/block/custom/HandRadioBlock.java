package com.github.NGoedix.watchvideo.block.custom;

import com.github.NGoedix.watchvideo.block.entity.custom.HandRadioBlockEntity;
import com.github.NGoedix.watchvideo.block.entity.custom.VideoPlayerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class HandRadioBlock extends Block implements EntityBlock {

    public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    private static final VoxelShape SHAPE_EAST_WEST = Block.box(7, 0, 6, 9, 11, 10);
    private static final VoxelShape SHAPE_NORTH_SOUTH = Block.box(6, 0, 7, 10, 11, 9);

    public HandRadioBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);

        if (pLevel.getBlockEntity(pPos) instanceof HandRadioBlockEntity) {
            HandRadioBlockEntity radioBlockEntity = (HandRadioBlockEntity) pLevel.getBlockEntity(pPos);
            CompoundTag tag = pStack.getOrCreateTag();

            if (tag.contains("url")) {
                radioBlockEntity.setUrl(tag.getString("url"));
            }
            if (tag.contains("volume")) {
                radioBlockEntity.setVolume(tag.getInt("volume"));
            }
            if (tag.contains("isPlaying")) {
                radioBlockEntity.setPlaying(tag.getBoolean("isPlaying"));
            }
            if (tag.contains("tick")) {
                radioBlockEntity.setTick(tag.getInt("tick"));
            }
            radioBlockEntity.setBeingUsed(null);
        }
    }


    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return pState.getValue(FACING).getAxis() == Direction.Axis.X ? SHAPE_EAST_WEST : SHAPE_NORTH_SOUTH;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.getStateDefinition().any().setValue(LIT, false).setValue(FACING, context.getHorizontalDirection() == Direction.WEST ? Direction.EAST : (context.getHorizontalDirection() == Direction.EAST ? Direction.WEST : context.getHorizontalDirection()));
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

    @Override
    public boolean isValidSpawn(BlockState state, BlockGetter world, BlockPos pos, SpawnPlacements.Type type, EntityType<?> entityType) {
        return false;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (!pLevel.isClientSide) {
            if (blockEntity instanceof HandRadioBlockEntity handBlockEntity) {
                handBlockEntity.tryOpen(pLevel, pPos, pPlayer);
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState pState) {
        return PushReaction.DESTROY;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return VideoPlayerBlockEntity::tick;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new HandRadioBlockEntity(pPos, pState);
    }
}
