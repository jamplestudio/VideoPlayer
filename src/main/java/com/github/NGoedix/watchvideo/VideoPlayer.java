package com.github.NGoedix.watchvideo;

import com.github.NGoedix.watchvideo.block.ModBlocks;
import com.github.NGoedix.watchvideo.block.entity.ModBlockEntities;
import com.github.NGoedix.watchvideo.client.gui.OverlayVideo;
import com.github.NGoedix.watchvideo.client.render.TVBlockRenderer;
import com.github.NGoedix.watchvideo.commands.RegisterCommands;
import com.github.NGoedix.watchvideo.commands.arguments.SymbolStringArgumentSerializer;
import com.github.NGoedix.watchvideo.commands.arguments.SymbolStringArgumentType;
import com.github.NGoedix.watchvideo.common.CommonHandler;
import com.github.NGoedix.watchvideo.item.ModItems;
import com.github.NGoedix.watchvideo.util.RadioStreams;
import com.github.NGoedix.watchvideo.util.cache.TextureCache;
import com.github.NGoedix.watchvideo.util.displayers.VideoDisplayer;
import com.mojang.logging.LogUtils;
import me.srrapero720.watermedia.api.image.ImageAPI;
import me.srrapero720.watermedia.api.image.ImageRenderer;
import me.srrapero720.watermedia.core.tools.JarTool;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Reference.MOD_ID)
public class VideoPlayer
{

    @OnlyIn(Dist.CLIENT)
    private static final OverlayVideo gui = new OverlayVideo();

    @OnlyIn(Dist.CLIENT)
    private static ImageRenderer IMG_PAUSED;

    @OnlyIn(Dist.CLIENT)
    private static ImageRenderer IMG_STEP30;

    @OnlyIn(Dist.CLIENT)
    private static ImageRenderer IMG_STEP10;

    @OnlyIn(Dist.CLIENT)
    public static ImageRenderer pausedImage() { return IMG_PAUSED; }

    @OnlyIn(Dist.CLIENT)
    public static ImageRenderer step30Image() { return IMG_STEP30; }

    @OnlyIn(Dist.CLIENT)
    public static ImageRenderer step10Image() { return IMG_STEP10; }

    public static CreativeModeTab videoPlayerTab;
    private static final ResourceLocation VIDEO_PLAYER_TAB_ID = new ResourceLocation(Reference.MOD_ID, "video_player_tab");

    public VideoPlayer()
    {
        Reference.LOGGER.info("Initializing mod...");
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        MinecraftForge.EVENT_BUS.register(RegisterCommands.class);

        ModBlocks.register(eventBus);
        ModBlockEntities.register(eventBus);
        ModItems.register(eventBus);

        eventBus.addListener(this::onCommonSetup);
        eventBus.addListener(this::onClientSetup);
        eventBus.addListener(this::onCreativeTabRegistration);
        eventBus.addListener(this::addCreative);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void onCommonSetup(final FMLCommonSetupEvent event)
    {
        ArgumentTypeInfos.registerByClass(SymbolStringArgumentType.class, new SymbolStringArgumentSerializer());
        event.enqueueWork(CommonHandler::setup);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        RadioStreams.prepareRadios();

        ItemBlockRenderTypes.setRenderLayer(ModBlocks.TV_BLOCK.get(), RenderType.cutout());
        BlockEntityRenderers.register(ModBlockEntities.TV_BLOCK_ENTITY.get(), TVBlockRenderer::new);

        loadImages();
    }

    @OnlyIn(Dist.CLIENT)
    private void loadImages() {
        IMG_PAUSED = ImageAPI.renderer(JarTool.readImage("/pictures/paused.png"), true);
        IMG_STEP30 = ImageAPI.renderer(JarTool.readImage("/pictures/step30.png"), true);
        IMG_STEP10 = ImageAPI.renderer(JarTool.readImage("/pictures/step10.png"), true);
    }

    private void onCreativeTabRegistration(CreativeModeTabEvent.Register event) {
        Reference.LOGGER.info("Registering creative tab...");
        videoPlayerTab = event.registerCreativeModeTab(VIDEO_PLAYER_TAB_ID, builder -> builder.icon(() -> new ItemStack(ModBlocks.TV_BLOCK.get())).title(Component.translatable("itemGroup.videoplayer.video_player_tab")));
    }

    private void addCreative(CreativeModeTabEvent.BuildContents event) {
        Reference.LOGGER.info("Adding items to creative tab...");
        event.accept(ModBlocks.TV_BLOCK);
        event.accept(ModBlocks.RADIO_BLOCK);
    }

    @Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static final class Events {

        @SubscribeEvent
        public static void onRenderTickEvent(TickEvent.RenderTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                TextureCache.renderTick();
            }
        }

        @SubscribeEvent
        public static void onClientTickEvent(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                TextureCache.clientTick();
                VideoDisplayer.tick();
            }
        }

        @SubscribeEvent
        public static void onUnloadingLevel(LevelEvent.Unload unload) {
            if (unload.getLevel() != null && unload.getLevel().isClientSide()) {
                TextureCache.unload();
                VideoDisplayer.unload();
            }
        }
    }
}
