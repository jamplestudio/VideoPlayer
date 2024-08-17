package com.github.NGoedix.watchvideo.client.gui.components;

import com.github.NGoedix.watchvideo.client.gui.components.slider.AbstractSelectionList;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

public class ScrollingList<E extends AbstractSelectionList.Entry<E>> extends AbstractSelectionList<E> {

    public ScrollingList(int x, int y, int width, int height, int slotHeightIn) {
        super(Minecraft.getInstance(), width, height, y - (height / 2), (y - (height / 2)) + height, slotHeightIn);
        this.setLeftPos(x - (width / 2));
        this.setRenderBackground(false);
        this.setRenderTopAndBottom(false); // removes background
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        double scale = Minecraft.getInstance().getWindow().getGuiScale();

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor((int)(this.x0  * scale), (int)(Minecraft.getInstance().getWindow().getHeight() - ((this.y0 + this.height) * scale)),
                (int)(this.width * scale), (int)(this.height * scale));

        super.render(stack, mouseX, mouseY, partialTicks);

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    @Override // @mcp: getScrollbarPosition = getScrollbarPosition
    protected int getScrollbarPosition() {
        return (this.x0 + this.width) - 6;
    }
}
