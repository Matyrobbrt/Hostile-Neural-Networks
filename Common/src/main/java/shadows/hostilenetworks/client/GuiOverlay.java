package shadows.hostilenetworks.client;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;

public interface GuiOverlay {
    void render(Gui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight);
}
