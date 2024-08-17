package com.github.NGoedix.watchvideo.client.gui;

import com.github.NGoedix.watchvideo.Reference;
import com.github.NGoedix.watchvideo.block.entity.custom.RadioBlockEntity;
import com.github.NGoedix.watchvideo.client.gui.components.CustomSlider;
import com.github.NGoedix.watchvideo.client.gui.components.ImageButtonHoverable;
import com.github.NGoedix.watchvideo.client.gui.components.ScrollingStringList;
import com.github.NGoedix.watchvideo.item.custom.HandRadioItem;
import com.github.NGoedix.watchvideo.network.PacketHandler;
import com.github.NGoedix.watchvideo.network.message.UploadRadioUpdateMessage;
import com.github.NGoedix.watchvideo.util.RadioStreams;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.*;

public class RadioScreen extends Screen {

    // Textures
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/gui/background.png");

    private static final ResourceLocation PLAY_BUTTON_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/gui/play_button.png");
    private static final ResourceLocation PLAY_HOVER_BUTTON_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/gui/play_button_hover.png");

    private static final ResourceLocation PAUSE_BUTTON_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/gui/pause_button.png");
    private static final ResourceLocation PAUSE_HOVER_BUTTON_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/gui/pause_button_hover.png");

    private ScrollingStringList countryList, stationList;
    private TextFieldWidget urlField;
    private CustomSlider volumeSlider;
    private ImageButtonHoverable playButton;
    private ImageButtonHoverable pauseButton;

    // Control
    private final RadioBlockEntity be;
    private final ItemStack item;
    private String url;
    private int volume;
    private boolean ready = false;

    // GUI
    private final int imageWidth = 256;
    private final int imageHeight = 256;
    private int leftPos;
    private int topPos;

    public RadioScreen(TileEntity be) {
        super(new TranslationTextComponent("gui.radio_screen.title"));
        this.be = (RadioBlockEntity) be;
        this.item = null;
        this.url = this.be.getUrl();
        this.volume = this.be.getVolume();
    }

    public RadioScreen(ItemStack item) {
        super(new TranslationTextComponent("gui.radio_screen.title"));
        this.be = null;
        this.item = item;

        this.url = HandRadioItem.getUrl(item);
        this.volume = HandRadioItem.getVolume(item);
    }

    @Override
    protected void init() {
        super.init();

        leftPos = (width - imageWidth) / 2;
        topPos = (height - imageHeight) / 2;

        Minecraft.getInstance().keyboardHandler.setSendRepeatsToGui(true);

        List<String> sortedCountries = new ArrayList<>(RadioStreams.getRadioStreams().keySet());
        Collections.sort(sortedCountries);

        addButton(countryList = new ScrollingStringList((width / 2) - 177, height / 2 - 3, 100, 248, sortedCountries));
        countryList.setSelected("Custom");
        countryList.setPlayerSlotClickListener(text -> {
            if (urlField != null)
                urlField.setEditable(text.equals("Custom"));
            if (stationList != null)
                stationList.updateEntries(getStationNamesForCountry(text, RadioStreams.getRadioStreams()));
        });
        countryList.setSelected(getCountryFromStationUrl(url));

        // Expresión regular para validar una URL
        String urlPattern = "(http|https)://(www\\.)?([\\w]+\\.)+[\\w]{2,63}/?[\\w\\-\\?\\=\\&\\%\\.\\/]*/?";

        addButton(urlField = new TextFieldWidget(font, leftPos + 12, height / 2 - 30, imageWidth - 28, 20, new StringTextComponent("")));
        urlField.setResponder(text -> {
            if (!ready) return;
            if (countryList.getSelectedText().equals("Custom")) {
                if (text.matches(urlPattern))
                    sendUpdate(urlField.getValue(), volume, true, 0, false);
            }
        });
        urlField.setEditable(countryList.getSelectedText().equals("Custom"));
        urlField.setMaxLength(32767);
        urlField.setValue(url);

        addButton(stationList = new ScrollingStringList((width / 2) + 172, height / 2 - 3, 100, 248, getStationNamesForCountry(countryList.getSelectedText(), RadioStreams.getRadioStreams())));
        stationList.setPlayerSlotClickListener(text -> {
            if (!ready) return;

            if (text != null && !text.isEmpty()) {
                urlField.setValue(getStationUrlFromStation(text));
                sendUpdate(urlField.getValue(), volume, pauseButton.visible, 0, false);
            }
        });
        stationList.setSelected(getStationFromStationUrl(url));
        stationList.updateEntries(getStationNamesForCountry(countryList.getSelectedText(), RadioStreams.getRadioStreams()));

        // Volume slider
        addButton(volumeSlider = new CustomSlider(leftPos + 10, height / 2 - 5, imageWidth - 24, 20, new TranslationTextComponent("gui.tv_video_screen.volume"), volume / 100f, false));
        volumeSlider.setOnSlideListener(value -> {
            if (!ready) return;

            if (be != null)
                be.setVolume((int) value);
            else
                HandRadioItem.setVolume(item, (int) value);
            volume = (int) volumeSlider.getValue();

            sendUpdate(urlField.getValue(), volume, pauseButton.visible, -1, false);
        });
        volumeSlider.setValue(volume / 100f);

        // Buttons
        addButton(playButton = new ImageButtonHoverable(width / 2 - 10, topPos + 150, 20, 20, 0, 0, 0, PLAY_BUTTON_TEXTURE, PLAY_HOVER_BUTTON_TEXTURE, 20, 20, button -> {
            if (!ready) return;

            if (!urlField.getValue().isEmpty()) {
                playButton.visible = false;
                pauseButton.visible = true;

                if (be != null) {
                    if (be.requestDisplay() == null) return;
                    be.requestDisplay().resume(be.getTick());
                } else {
                    if (HandRadioItem.requestDisplay(item) == null) return;
                    HandRadioItem.requestDisplay(item).resume(-1);
                }

                sendUpdate(urlField.getValue(), volume, true, 0, false);
            }
        }));
        playButton.visible = be != null ? !be.isPlaying() : url.isEmpty();

        addButton(pauseButton = new ImageButtonHoverable(width / 2 - 10, topPos + 150, 20, 20, 0, 0, 0, PAUSE_BUTTON_TEXTURE, PAUSE_HOVER_BUTTON_TEXTURE, 20, 20, button -> {
            if (!ready) return;

            if (!urlField.getValue().isEmpty()) {
                playButton.visible = true;
                pauseButton.visible = false;

                if (be != null) {
                    if (be.requestDisplay() == null) return;
                    be.requestDisplay().pause(be.getTick());
                } else {
                    if (HandRadioItem.requestDisplay(item) == null) return;
                    HandRadioItem.requestDisplay(item).pause(-1);
                }
                sendUpdate(urlField.getValue(), volume, false, 0, false);
            }
        }));
        pauseButton.visible = be != null ? be.isPlaying() : !url.isEmpty();
    }

    private String getCountryFromStationUrl(String url) {
        for (Map.Entry<String, List<RadioStreams.RadioStream>> entry : RadioStreams.getRadioStreams().entrySet()) {
            for (RadioStreams.RadioStream station : entry.getValue()) {
                if (station.getStreamLink().equals(url)) {
                    return entry.getKey();
                }
            }
        }
        return "Custom";
    }

    private String getStationFromStationUrl(String url) {
        for (Map.Entry<String, List<RadioStreams.RadioStream>> entry : RadioStreams.getRadioStreams().entrySet()) {
            for (RadioStreams.RadioStream station : entry.getValue()) {
                if (station.getStreamLink().equals(url)) {
                    return station.getRadioName();
                }
            }
        }
        return null;
    }

    private List<String> getStationNamesForCountry(String country, Map<String, List<RadioStreams.RadioStream>> radioStreamsByCountry) {
        List<String> stationNames = new ArrayList<>(9);

        if (country == null) {
            for (int i = 0; i < 8; i++)
                stationNames.add("");
            return stationNames;
        }

        List<RadioStreams.RadioStream> stations = radioStreamsByCountry.get(country);
        if (stations != null) {
            for (RadioStreams.RadioStream station : stations) {
                stationNames.add(station.getRadioName());
            }
        }

        while (stationNames.size() < 8)
            stationNames.add("");

        return stationNames;
    }

    private String getStationUrlFromStation(String stationUrl) {
        for (Map.Entry<String, List<RadioStreams.RadioStream>> entry : RadioStreams.getRadioStreams().entrySet()) {
            for (RadioStreams.RadioStream station : entry.getValue()) {
                if (station.getRadioName().equals(stationUrl)) {
                    return station.getStreamLink();
                }
            }
        }
        return "";
    }

    @Override
    public void render(MatrixStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        if (!ready) ready = true;
        renderBackground(pPoseStack);

        RenderSystem.clearColor(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getInstance().getTextureManager().bind(TEXTURE);
        blit(pPoseStack, leftPos, topPos, 320, 320, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);

        font.draw(pPoseStack, "Radio Player (by Goedix)", (width / 2f) - 62, height / 2f - 100, 0xFFFFFF);

        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }

    private void sendUpdate(String url, int volume, boolean isPlaying, int tick, boolean exit) {
        if (be != null) {
            PacketHandler.sendToServer(new UploadRadioUpdateMessage(be.getBlockPos(), url, volume, tick == -1 ? -1 : be.getTick(), isPlaying, exit));
        } else {
            PacketHandler.sendToServer(new UploadRadioUpdateMessage(item, url, volume, -1, isPlaying, exit));
        }
    }

    @Override
    public void removed() {
        sendUpdate(urlField.getValue(), volume, pauseButton.visible, -1, true);
        Minecraft.getInstance().keyboardHandler.setSendRepeatsToGui(false);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
