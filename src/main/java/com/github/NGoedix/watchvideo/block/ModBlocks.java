package com.github.NGoedix.watchvideo.block;

import com.github.NGoedix.watchvideo.Reference;
import com.github.NGoedix.watchvideo.VideoPlayerModTab;
import com.github.NGoedix.watchvideo.block.custom.HandRadioBlock;
import com.github.NGoedix.watchvideo.block.custom.RadioBlock;
import com.github.NGoedix.watchvideo.block.custom.TVBlock;
import com.github.NGoedix.watchvideo.item.ModItems;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Reference.MOD_ID);

    public static final RegistryObject<Block> TV_BLOCK = registerBlock("tv_block",
            () -> new TVBlock(AbstractBlock.Properties.of(Material.HEAVY_METAL).noOcclusion().requiresCorrectToolForDrops().sound(SoundType.METAL).lightLevel(litBlockEmission(12)).strength(3.5F, 6.0F)), VideoPlayerModTab.ALL);

    public static final RegistryObject<Block> RADIO_BLOCK = registerBlock("radio_block",
            () -> new RadioBlock(AbstractBlock.Properties.of(Material.METAL).noOcclusion().requiresCorrectToolForDrops().sound(SoundType.METAL).strength(3.5f, 6.0f)), VideoPlayerModTab.ALL);

//    public static final RegistryObject<Block> HAND_RADIO_BLOCK = registerBlockWithoutBlockItem("hand_radio_block",
//            () -> new HandRadioBlock(AbstractBlock.Properties.of(Material.METAL).noOcclusion().requiresCorrectToolForDrops().sound(SoundType.METAL).strength(3.5f, 6.0f)));

    private static ToIntFunction<BlockState> litBlockEmission(int pLightValue) {
        return (blockstate) -> blockstate.getValue(BlockStateProperties.LIT) ? pLightValue : 0;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block,
                                                                            ItemGroup tab) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(),
                new Item.Properties().tab(tab)));
    }

    private static <T extends Block> RegistryObject<T> registerBlockWithoutBlockItem(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block,
                                                                     ItemGroup tab) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn, tab);
        return toReturn;
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
