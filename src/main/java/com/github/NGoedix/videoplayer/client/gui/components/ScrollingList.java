package com.github.NGoedix.videoplayer.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import org.lwjgl.opengl.GL11;

public class ScrollingList<E extends AbstractSelectionList.Entry<E>> extends AbstractSelectionList<E> {

    public ScrollingList(int x, int y, int width, int height, int slotHeightIn) {
        super(Minecraft.getInstance(), width, height, y - (height / 2), (y - (height / 2)) + height, slotHeightIn);
        this.setLeftPos(x - (width / 2));
        this.setRenderBackground(false);
        this.setRenderTopAndBottom(false); // removes background
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        double scale = Minecraft.getInstance().getWindow().getGuiScale();

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor((int)(this.x0  * scale), (int)(Minecraft.getInstance().getWindow().getHeight() - ((this.y0 + this.height) * scale)),
                (int)(this.width * scale), (int)(this.height * scale));

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    @Override // @mcp: getScrollbarPosition = getScrollbarPosition
    protected int getScrollbarPosition() {
        return (this.x0 + this.width) - 6;
    }

    @Override
    public void updateNarration(NarrationElementOutput p_169152_) {

    }
}
