package com.github.NGoedix.watchvideo.client.gui.components.slider;

import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractContainerEventHandler extends Widget implements ContainerEventHandler {

   @Nullable
   private IGuiEventListener focused;
   private boolean isDragging;

   public AbstractContainerEventHandler(int pX, int pY, int pWidth, int pHeight, ITextComponent pMessage) {
      super(pX, pY, pWidth, pHeight, pMessage);
   }

   public final boolean isDragging() {
      return this.isDragging;
   }

   public final void setDragging(boolean pDragging) {
      this.isDragging = pDragging;
   }

   @Nullable
   public IGuiEventListener getFocused() {
      return this.focused;
   }

   public void setFocused(@Nullable IGuiEventListener pListener) {
      this.focused = pListener;
   }
}