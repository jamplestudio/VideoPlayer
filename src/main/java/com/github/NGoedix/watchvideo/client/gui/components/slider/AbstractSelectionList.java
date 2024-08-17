package com.github.NGoedix.watchvideo.client.gui.components.slider;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.AbstractList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractSelectionList<E extends AbstractSelectionList.Entry<E>> extends AbstractContainerEventHandler {
   protected final Minecraft minecraft;
   protected final int itemHeight;
   private final List<E> children = new AbstractSelectionList.TrackedList();
   protected int width;
   protected int height;
   protected int y0;
   protected int y1;
   protected int x1;
   protected int x0;
   private double scrollAmount;
   private boolean renderSelection = true;
   protected int headerHeight;
   private boolean scrolling;
   @Nullable
   private E selected;
   private boolean renderBackground = true;
   private boolean renderTopAndBottom = true;
   @Nullable
   private E hovered;

   public AbstractSelectionList(Minecraft pMinecraft, int pWidth, int pHeight, int pY0, int pY1, int pItemHeight) {
      super(pY0, pY1, pWidth, pHeight, null);
      this.minecraft = pMinecraft;
      this.width = pWidth;
      this.height = pHeight;
      this.y0 = pY0;
      this.y1 = pY1;
      this.itemHeight = pItemHeight;
      this.x0 = 0;
      this.x1 = pWidth;
   }

   public int getRowWidth() {
      return 220;
   }

   @Nullable
   public E getSelected() {
      return this.selected;
   }

   public void setSelected(@Nullable E pSelected) {
      this.selected = pSelected;
   }

   public void setRenderBackground(boolean pRenderBackground) {
      this.renderBackground = pRenderBackground;
   }

   public void setRenderTopAndBottom(boolean pRenderTopAndButton) {
      this.renderTopAndBottom = pRenderTopAndButton;
   }

   @Nullable
   public IGuiEventListener getFocused() {
      return super.getFocused();
   }

   public final List<? extends IGuiEventListener> children() {
      return this.children;
   }

   protected final void clearEntries() {
      this.children.clear();
   }

   protected E getEntry(int pIndex) {
      return (E) this.children().get(pIndex);
   }

   protected int addEntry(E pEntry) {
      this.children.add(pEntry);
      return this.children.size() - 1;
   }

   protected int getItemCount() {
      return this.children().size();
   }

   protected boolean isSelectedItem(int pIndex) {
      return Objects.equals(this.getSelected(), this.children().get(pIndex));
   }

   @Nullable
   protected final E getEntryAtPosition(double pMouseX, double pMouseY) {
      int i = this.getRowWidth() / 2;
      int j = this.x0 + this.width / 2;
      int k = j - i;
      int l = j + i;
      int i1 = MathHelper.floor(pMouseY - (double)this.y0) - this.headerHeight + (int)this.getScrollAmount() - 4;
      int j1 = i1 / this.itemHeight;
      return (E)(pMouseX < (double)this.getScrollbarPosition() && pMouseX >= (double)k && pMouseX <= (double)l && j1 >= 0 && i1 >= 0 && j1 < this.getItemCount() ? this.children().get(j1) : null);
   }

   public void setLeftPos(int pX0) {
      this.x0 = pX0;
      this.x1 = pX0 + this.width;
   }

   protected int getMaxPosition() {
      return this.getItemCount() * this.itemHeight + this.headerHeight;
   }

   protected void clickedHeader(int pMouseX, int pMouseY) {
   }

   protected void renderBackground(MatrixStack pPoseStack) {
   }

   protected void renderDecorations(MatrixStack pPoseStack, int pMouseX, int pMouseY) {
   }

   public void render(MatrixStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
      this.renderBackground(pPoseStack);
      int i = this.getScrollbarPosition();
      int j = i + 6;
      Tessellator tesselator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tesselator.getBuilder();
      this.hovered = this.isMouseOver((double)pMouseX, (double)pMouseY) ? this.getEntryAtPosition((double)pMouseX, (double)pMouseY) : null;
      if (this.renderBackground) {
         this.minecraft.getTextureManager().bind(BACKGROUND_LOCATION);
         GlStateManager._blendColor(1.0F, 1.0F, 1.0F, 1.0F);
         float f = 32.0F;
         bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
         bufferbuilder.vertex(this.x0, this.y1, 0.0D).uv((float)this.x0 / 32.0F, (float)(this.y1 + (int)this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
         bufferbuilder.vertex(this.x1, this.y1, 0.0D).uv((float)this.x1 / 32.0F, (float)(this.y1 + (int)this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
         bufferbuilder.vertex(this.x1, this.y0, 0.0D).uv((float)this.x1 / 32.0F, (float)(this.y0 + (int)this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
         bufferbuilder.vertex(this.x0, this.y0, 0.0D).uv((float)this.x0 / 32.0F, (float)(this.y0 + (int)this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
         tesselator.end();
      }

      int j1 = this.getRowLeft();
      int k = this.y0 + 4 - (int)this.getScrollAmount();

      this.renderList(pPoseStack, j1, k, pMouseX, pMouseY, pPartialTick);
      if (this.renderTopAndBottom) {
         this.minecraft.getTextureManager().bind(BACKGROUND_LOCATION);
         RenderSystem.enableDepthTest();
         RenderSystem.depthFunc(519);
         float f1 = 32.0F;
         int l = -100;
         bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
         bufferbuilder.vertex((double)this.x0, (double)this.y0, -100.0D).uv(0.0F, (float)this.y0 / 32.0F).color(64, 64, 64, 255).endVertex();
         bufferbuilder.vertex((double)(this.x0 + this.width), (double)this.y0, -100.0D).uv((float)this.width / 32.0F, (float)this.y0 / 32.0F).color(64, 64, 64, 255).endVertex();
         bufferbuilder.vertex((double)(this.x0 + this.width), 0.0D, -100.0D).uv((float)this.width / 32.0F, 0.0F).color(64, 64, 64, 255).endVertex();
         bufferbuilder.vertex((double)this.x0, 0.0D, -100.0D).uv(0.0F, 0.0F).color(64, 64, 64, 255).endVertex();
         bufferbuilder.vertex((double)this.x0, (double)this.height, -100.0D).uv(0.0F, (float)this.height / 32.0F).color(64, 64, 64, 255).endVertex();
         bufferbuilder.vertex((double)(this.x0 + this.width), (double)this.height, -100.0D).uv((float)this.width / 32.0F, (float)this.height / 32.0F).color(64, 64, 64, 255).endVertex();
         bufferbuilder.vertex((double)(this.x0 + this.width), (double)this.y1, -100.0D).uv((float)this.width / 32.0F, (float)this.y1 / 32.0F).color(64, 64, 64, 255).endVertex();
         bufferbuilder.vertex((double)this.x0, (double)this.y1, -100.0D).uv(0.0F, (float)this.y1 / 32.0F).color(64, 64, 64, 255).endVertex();
         tesselator.end();
         RenderSystem.depthFunc(515);
         RenderSystem.disableDepthTest();
         RenderSystem.enableBlend();
         RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
         RenderSystem.disableTexture();
         int i1 = 4;
         bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
         bufferbuilder.vertex((double)this.x0, (double)(this.y0 + 4), 0.0D).color(0, 0, 0, 0).endVertex();
         bufferbuilder.vertex((double)this.x1, (double)(this.y0 + 4), 0.0D).color(0, 0, 0, 0).endVertex();
         bufferbuilder.vertex((double)this.x1, (double)this.y0, 0.0D).color(0, 0, 0, 255).endVertex();
         bufferbuilder.vertex((double)this.x0, (double)this.y0, 0.0D).color(0, 0, 0, 255).endVertex();
         bufferbuilder.vertex((double)this.x0, (double)this.y1, 0.0D).color(0, 0, 0, 255).endVertex();
         bufferbuilder.vertex((double)this.x1, (double)this.y1, 0.0D).color(0, 0, 0, 255).endVertex();
         bufferbuilder.vertex((double)this.x1, (double)(this.y1 - 4), 0.0D).color(0, 0, 0, 0).endVertex();
         bufferbuilder.vertex((double)this.x0, (double)(this.y1 - 4), 0.0D).color(0, 0, 0, 0).endVertex();
         tesselator.end();
      }

      int k1 = this.getMaxScroll();
      if (k1 > 0) {
         RenderSystem.disableTexture();
         int l1 = (int)((float)((this.y1 - this.y0) * (this.y1 - this.y0)) / (float)this.getMaxPosition());
         l1 = MathHelper.clamp(l1, 32, this.y1 - this.y0 - 8);
         int i2 = (int)this.getScrollAmount() * (this.y1 - this.y0 - l1) / k1 + this.y0;
         if (i2 < this.y0) {
            i2 = this.y0;
         }

         bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
         bufferbuilder.vertex((double)i, (double)this.y1, 0.0D).color(0, 0, 0, 255).endVertex();
         bufferbuilder.vertex((double)j, (double)this.y1, 0.0D).color(0, 0, 0, 255).endVertex();
         bufferbuilder.vertex((double)j, (double)this.y0, 0.0D).color(0, 0, 0, 255).endVertex();
         bufferbuilder.vertex((double)i, (double)this.y0, 0.0D).color(0, 0, 0, 255).endVertex();
         bufferbuilder.vertex((double)i, (double)(i2 + l1), 0.0D).color(128, 128, 128, 255).endVertex();
         bufferbuilder.vertex((double)j, (double)(i2 + l1), 0.0D).color(128, 128, 128, 255).endVertex();
         bufferbuilder.vertex((double)j, (double)i2, 0.0D).color(128, 128, 128, 255).endVertex();
         bufferbuilder.vertex((double)i, (double)i2, 0.0D).color(128, 128, 128, 255).endVertex();
         bufferbuilder.vertex((double)i, (double)(i2 + l1 - 1), 0.0D).color(192, 192, 192, 255).endVertex();
         bufferbuilder.vertex((double)(j - 1), (double)(i2 + l1 - 1), 0.0D).color(192, 192, 192, 255).endVertex();
         bufferbuilder.vertex((double)(j - 1), (double)i2, 0.0D).color(192, 192, 192, 255).endVertex();
         bufferbuilder.vertex((double)i, (double)i2, 0.0D).color(192, 192, 192, 255).endVertex();
         tesselator.end();
      }

      this.renderDecorations(pPoseStack, pMouseX, pMouseY);
      RenderSystem.enableTexture();
      RenderSystem.disableBlend();
   }

   protected void ensureVisible(E pEntry) {
      int i = this.getRowTop(this.children().indexOf(pEntry));
      int j = i - this.y0 - 4 - this.itemHeight;
      if (j < 0) {
         this.scroll(j);
      }

      int k = this.y1 - i - this.itemHeight - this.itemHeight;
      if (k < 0) {
         this.scroll(-k);
      }

   }

   private void scroll(int pScroll) {
      this.setScrollAmount(this.getScrollAmount() + (double)pScroll);
   }

   public double getScrollAmount() {
      return this.scrollAmount;
   }

   public void setScrollAmount(double pScroll) {
      this.scrollAmount = MathHelper.clamp(pScroll, 0.0D, (double)this.getMaxScroll());
   }

   public int getMaxScroll() {
      return Math.max(0, this.getMaxPosition() - (this.y1 - this.y0 - 4));
   }

   protected void updateScrollingState(double pMouseX, double pMouseY, int pButton) {
      this.scrolling = pButton == 0 && pMouseX >= (double)this.getScrollbarPosition() && pMouseX < (double)(this.getScrollbarPosition() + 6);
   }

   protected int getScrollbarPosition() {
      return this.width / 2 + 124;
   }

   public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
      this.updateScrollingState(pMouseX, pMouseY, pButton);
      if (!this.isMouseOver(pMouseX, pMouseY)) {
         return false;
      } else {
         E e = this.getEntryAtPosition(pMouseX, pMouseY);
         if (e != null) {
            if (e.mouseClicked(pMouseX, pMouseY, pButton)) {
               this.setFocused(e);
               this.setDragging(true);
               return true;
            }
         } else if (pButton == 0) {
            this.clickedHeader((int)(pMouseX - (double)(this.x0 + this.width / 2 - this.getRowWidth() / 2)), (int)(pMouseY - (double)this.y0) + (int)this.getScrollAmount() - 4);
            return true;
         }

         return this.scrolling;
      }
   }

   public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
      if (this.getFocused() != null) {
         this.getFocused().mouseReleased(pMouseX, pMouseY, pButton);
      }

      return false;
   }

   public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
      if (super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY)) {
         return true;
      } else if (pButton == 0 && this.scrolling) {
         if (pMouseY < (double)this.y0) {
            this.setScrollAmount(0.0D);
         } else if (pMouseY > (double)this.y1) {
            this.setScrollAmount((double)this.getMaxScroll());
         } else {
            double d0 = (double)Math.max(1, this.getMaxScroll());
            int i = this.y1 - this.y0;
            int j = MathHelper.clamp((int)((float)(i * i) / (float)this.getMaxPosition()), 32, i - 8);
            double d1 = Math.max(1.0D, d0 / (double)(i - j));
            this.setScrollAmount(this.getScrollAmount() + pDragY * d1);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
      this.setScrollAmount(this.getScrollAmount() - pDelta * (double)this.itemHeight / 2.0D);
      return true;
   }

   public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
      if (super.keyPressed(pKeyCode, pScanCode, pModifiers)) {
         return true;
      } else if (pKeyCode == 264) {
         this.moveSelection(AbstractSelectionList.SelectionDirection.DOWN);
         return true;
      } else if (pKeyCode == 265) {
         this.moveSelection(AbstractSelectionList.SelectionDirection.UP);
         return true;
      } else {
         return false;
      }
   }

   protected void moveSelection(AbstractSelectionList.SelectionDirection pOrdering) {
      this.moveSelection(pOrdering, (p_93510_) -> {
         return true;
      });
   }

   protected void moveSelection(AbstractSelectionList.SelectionDirection pOrdering, Predicate<E> pFilter) {
      int i = pOrdering == AbstractSelectionList.SelectionDirection.UP ? -1 : 1;
      if (!this.children().isEmpty()) {
         int j = this.children().indexOf(this.getSelected());

         while(true) {
            int k = MathHelper.clamp(j + i, 0, this.getItemCount() - 1);
            if (j == k) {
               break;
            }

            E e = (E) this.children().get(k);
            if (pFilter.test(e)) {
               this.setSelected(e);
               this.ensureVisible(e);
               break;
            }

            j = k;
         }
      }

   }

   public boolean isMouseOver(double pMouseX, double pMouseY) {
      return pMouseY >= (double)this.y0 && pMouseY <= (double)this.y1 && pMouseX >= (double)this.x0 && pMouseX <= (double)this.x1;
   }

   protected void renderList(MatrixStack pPoseStack, int pX, int pY, int pMouseX, int pMouseY, float pPartialTick) {
      int i = this.getItemCount();
      Tessellator tesselator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tesselator.getBuilder();

      for(int j = 0; j < i; ++j) {
         int k = this.getRowTop(j);
         int l = this.getRowBottom(j);
         if (l >= this.y0 && k <= this.y1) {
            int i1 = pY + j * this.itemHeight + this.headerHeight;
            int j1 = this.itemHeight - 4;
            E e = this.getEntry(j);
            int k1 = this.getRowWidth();
            if (this.renderSelection && this.isSelectedItem(j)) {
               int l1 = this.x0 + this.width / 2 - k1 / 2;
               int i2 = this.x0 + this.width / 2 + k1 / 2;
               RenderSystem.disableTexture();
               float f = this.isFocused() ? 1.0F : 0.5F;
               GlStateManager._blendColor(f, f, f, 1.0F);
               bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
               bufferbuilder.vertex((double)l1, (double)(i1 + j1 + 2), 0.0D).endVertex();
               bufferbuilder.vertex((double)i2, (double)(i1 + j1 + 2), 0.0D).endVertex();
               bufferbuilder.vertex((double)i2, (double)(i1 - 2), 0.0D).endVertex();
               bufferbuilder.vertex((double)l1, (double)(i1 - 2), 0.0D).endVertex();
               tesselator.end();
               GlStateManager._blendColor(0.0F, 0.0F, 0.0F, 1.0F);
               bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
               bufferbuilder.vertex((double)(l1 + 1), (double)(i1 + j1 + 1), 0.0D).endVertex();
               bufferbuilder.vertex((double)(i2 - 1), (double)(i1 + j1 + 1), 0.0D).endVertex();
               bufferbuilder.vertex((double)(i2 - 1), (double)(i1 - 1), 0.0D).endVertex();
               bufferbuilder.vertex((double)(l1 + 1), (double)(i1 - 1), 0.0D).endVertex();
               tesselator.end();
               RenderSystem.enableTexture();
            }

            int j2 = this.getRowLeft();
            e.render(pPoseStack, j, k, j2, k1, j1, pMouseX, pMouseY, Objects.equals(this.hovered, e), pPartialTick);
         }
      }

   }

   public int getRowLeft() {
      return this.x0 + this.width / 2 - this.getRowWidth() / 2 + 2;
   }

   protected int getRowTop(int pIndex) {
      return this.y0 + 4 - (int)this.getScrollAmount() + pIndex * this.itemHeight + this.headerHeight;
   }

   private int getRowBottom(int pIndex) {
      return this.getRowTop(pIndex) + this.itemHeight;
   }

   void bindEntryToSelf(AbstractSelectionList.Entry<E> pEntry) {
      pEntry.list = this;
   }

   public int getWidth() { return this.width; }
   public int getHeight() { return this.height; }
   public int getLeft() { return this.x0; }
   public int getRight() { return this.x1; }

   @OnlyIn(Dist.CLIENT)
   public abstract static class Entry<E extends AbstractSelectionList.Entry<E>> implements IGuiEventListener {
      /** @deprecated */
      @Deprecated
      protected AbstractSelectionList<E> list;

      public abstract void render(MatrixStack pPoseStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick);

      public boolean isMouseOver(double pMouseX, double pMouseY) {
         return Objects.equals(this.list.getEntryAtPosition(pMouseX, pMouseY), this);
      }
   }

   @OnlyIn(Dist.CLIENT)
   protected enum SelectionDirection {
      UP,
      DOWN;
   }

   @OnlyIn(Dist.CLIENT)
   class TrackedList extends AbstractList<E> {
      private final List<E> delegate = Lists.newArrayList();

      public E get(int p_93557_) {
         return this.delegate.get(p_93557_);
      }

      public int size() {
         return this.delegate.size();
      }

      public E set(int p_93559_, E p_93560_) {
         E e = this.delegate.set(p_93559_, p_93560_);
         AbstractSelectionList.this.bindEntryToSelf(p_93560_);
         return e;
      }

      public void add(int p_93567_, E p_93568_) {
         this.delegate.add(p_93567_, p_93568_);
         AbstractSelectionList.this.bindEntryToSelf(p_93568_);
      }

      public E remove(int p_93565_) {
         return this.delegate.remove(p_93565_);
      }
   }
}
