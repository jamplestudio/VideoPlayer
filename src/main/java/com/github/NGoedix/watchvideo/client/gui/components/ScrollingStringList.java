package com.github.NGoedix.watchvideo.client.gui.components;

import com.github.NGoedix.watchvideo.client.gui.components.slider.AbstractSelectionList;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.FontRenderer;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

public class ScrollingStringList extends ScrollingList<ScrollingStringList.PlayerSlot> {
    private static final int SLOT_HEIGHT = 30;

    public interface PlayerSlotClickListener {
        void onClick(String text);
    }

    private PlayerSlotClickListener playerSlotClickListener;

    public ScrollingStringList(int x, int y, int width, int height, List<String> text) {
        super(x, y, width, height, SLOT_HEIGHT);
        this.updateEntries(text);
    }

    public void setPlayerSlotClickListener(PlayerSlotClickListener playerSlotClickListener) {
        this.playerSlotClickListener = playerSlotClickListener;
    }

    public String getSelectedText() {
        return this.getSelected().getText();
    }

    public void setSelected(@Nullable String entry) {
        for (int i = 0; i < this.children().size(); i++) {
            PlayerSlot slot = (PlayerSlot) this.children().get(i);
            if (slot.getText().equals(entry)) {
                this.setSelected(slot);
                break;
            }
        }
    }

    @Override
    public void setSelected(@Nullable PlayerSlot entry) {
        super.setSelected(entry);
        if (entry != null && this.playerSlotClickListener != null) {
            this.playerSlotClickListener.onClick(entry.getText());
        }
    }

    public void updateEntries(List<String> texts) {
        this.clearEntries();
        texts.forEach(text -> this.addEntry(new PlayerSlot(text, this)));
    }

    @Override
    protected void renderBackground(MatrixStack pPoseStack) {
        // Render background of the list
        super.renderBackground(pPoseStack);

        // Render background of the slots
        int i = this.getRowLeft();
        int j = this.getRowTop(this.getItemCount());
        int k = this.getRowTop(0);

        // Render container background color gray
        fillGradient(pPoseStack, i, k - 4, i + this.getRowWidth(), j + 4, -1072689136, -804253680);

        // Render container border color black
        fillGradient(pPoseStack, i, k - 4, i + 1, j + 4, -804253680, -804253680);
    }

    public class PlayerSlot extends AbstractSelectionList.Entry<PlayerSlot> {

        private final String text;
        private final ScrollingStringList parent;

        PlayerSlot(String text, ScrollingStringList parent) {
            this.text = text;
            this.parent = parent;
        }

        public String getText() {
            return text;
        }

        @Override
        public void render(MatrixStack stack, int entryIdx, int top, int left, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean p_194999_5_, float partialTicks) {
            FontRenderer font = this.parent.minecraft.font;

            fillGradient(stack, left, top, left + entryWidth, top + entryHeight, -435154928, -435154928);

            // If the mouse is hovering over the slot, render the background
            if (mouseX >= parent.getLeft() && mouseX <= parent.getRight() && mouseY >= top && mouseY <= top + entryHeight) {
                fillGradient(stack, left, top, left + entryWidth, top + entryHeight, -1072689136, -804253680);
            }

            font.draw(stack, this.text, left + 65, top + 10, Color.WHITE.getRGB());
        }

        @Override
        public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
            this.parent.setSelected(this);
            return false;
        }
    }
}