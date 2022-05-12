package dev.rabies.vox.render.tabgui;

import dev.rabies.vox.VoxMod;
import dev.rabies.vox.cheats.Category;
import dev.rabies.vox.render.font.SystemFontRenderer;
import dev.rabies.vox.utils.DrawUtils;
import lombok.Getter;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TabCategoryComponent {

    private final SystemFontRenderer labelFont = VoxMod.get().newSystemFont("Mukta-Regular", 20);
    @Getter
    private final List<TabCheatComponent> cheatTabs = new ArrayList<>();
    @Getter
    private final Category category;
    @Getter
    private final String label;

    public TabCategoryComponent(Category category) {
        this.category = category;
        this.label = category.getLabel();
    }

    public void registerTab(TabCheatComponent tab) {
        cheatTabs.add(tab);
    }

    public void renderCheatsTab(double parentOffsetX, double parentOffsetY, int selectedIndex) {
        int offset = 6;
        double height = ((labelFont.getHeight() / 1.3) + offset - 1) * cheatTabs.size();
        double width = 0;
        for (TabCheatComponent tab : cheatTabs) {
            if (labelFont.getStringWidth(tab.getLabel()) > width) {
                width = labelFont.getStringWidth(tab.getLabel());
            }
        }
        width += offset * 3;

        int bg = new Color(20, 20, 20, 200).getRGB();
        int theme = new Color(110, 255, 60).getRGB();

        GlStateManager.translate(parentOffsetX, parentOffsetY, 0);
        DrawUtils.drawRect(0, 0, width, height, bg);
        GlStateManager.glLineWidth(1.2F);
        DrawUtils.drawRect(GL11.GL_LINE_LOOP, 0, 0, width, height, theme);

        int subOffset = offset - 1;
        int offsetY = subOffset - 1;
        for (int i = 0; i < cheatTabs.size(); i++) {
            TabCheatComponent tab = cheatTabs.get(i);
            Color col = new Color(255, 255, 255).darker();
            if (tab.getCheat().isEnabled()) {
            	col = new Color(110, 255, 60).darker();
            }
            
            if (selectedIndex == i) {
                col = col.brighter();
            }

            labelFont.drawStringWithShadow(tab.getLabel(), subOffset, offsetY - 2, col.getRGB());
            offsetY += labelFont.getHeight() / 1.3;
            offsetY += subOffset;
        }

        GlStateManager.translate(-parentOffsetX, -parentOffsetY, 0);
    }
}