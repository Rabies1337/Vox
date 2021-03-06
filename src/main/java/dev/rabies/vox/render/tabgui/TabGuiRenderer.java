package dev.rabies.vox.render.tabgui;

import dev.rabies.vox.VoxMod;
import dev.rabies.vox.cheats.Category;
import dev.rabies.vox.render.RenderHook;
import dev.rabies.vox.render.font.SystemFontRenderer;
import dev.rabies.vox.utils.render.DrawUtils;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TabGuiRenderer implements TabActionListener {

    private final SystemFontRenderer customLabelFont = VoxMod.get().newSystemFont("NotoSansJP-Medium", 19);
    private final FontRenderer defaultLabelFont = Minecraft.getMinecraft().fontRenderer;
    @Getter
    private final List<TabCategoryComponent> categoryTabs = new ArrayList<>();
    @Getter
    private int selectedIndex;
    @Getter
    private int selectedCheatIndex;

    public TabGuiRenderer() {
        Arrays.stream(Category.values()).forEach(cat -> {
            TabCategoryComponent tab = new TabCategoryComponent(cat);
            VoxMod.get().getCheatsByCategory(cat).forEach(ch -> tab.registerTab(new TabCheatComponent(ch)));
            categoryTabs.add(tab);
        });
        selectedIndex = 0;
        selectedCheatIndex = -1;
    }

    public void render(RenderHook hook) {
        int offset = 6;
        double height = ((defaultLabelFont.FONT_HEIGHT / 1.2) + offset - 1) * categoryTabs.size() + 2;
        double width = 0;
        for (TabCategoryComponent tab : categoryTabs) {
            if (defaultLabelFont.getStringWidth(tab.getLabel()) > width) {
                width = defaultLabelFont.getStringWidth(tab.getLabel());
            }
        }
        width += offset * 3;

        int bg = new Color(20, 20, 20, 200).getRGB();
        int theme = new Color(110, 255, 60).getRGB();

        GlStateManager.translate(5, hook.getHudCheat().isEnabled() ? 20 : 5, 0);
        DrawUtils.drawRect(0, 0, width, height, bg);
        GlStateManager.glLineWidth(1.2F);
        DrawUtils.drawRect(GL11.GL_LINE_LOOP, 0, 0, width, height, theme);

        int subOffset = offset - 1;
        int offsetY = subOffset - 1;
        for (int i = 0; i < categoryTabs.size(); i++) {
            TabCategoryComponent tab = categoryTabs.get(i);
            Color col = new Color(255, 255, 255).darker();
            if (selectedIndex == i) {
                col = col.brighter().brighter();

                if (selectedCheatIndex != -1) {
                	col = new Color(110, 255, 60);
                    tab.renderCheatsTab(width + 3, offsetY - 4, selectedCheatIndex);
                }
            }

            defaultLabelFont.drawStringWithShadow(tab.getLabel(), subOffset, offsetY, col.getRGB());
            offsetY += defaultLabelFont.FONT_HEIGHT / 1.2;
            offsetY += subOffset;
        }

        GlStateManager.translate(-5, hook.getHudCheat().isEnabled() ? -20 : -5, 0);
    }

    @Override
    public void input(int keyCode, boolean state) {
    	if (!state) return;

        switch (keyCode) {
            case Keyboard.KEY_DOWN:
                if (selectedCheatIndex != -1) {
                	selectedCheatIndex++;
                	if (selectedCheatIndex >= categoryTabs.get(selectedIndex)
            				.getCheatTabs().size()) {
                		selectedCheatIndex = 0;
                	}
                	return;
                }
                
                selectedIndex++;
                if (selectedIndex >= categoryTabs.size()) {
                    selectedIndex = 0;
                }
                break;

            case Keyboard.KEY_UP:
                if (selectedCheatIndex != -1) {
                	selectedCheatIndex--;
                	if (selectedCheatIndex < 0) {
                		selectedCheatIndex = categoryTabs.get(selectedIndex)
                				.getCheatTabs().size() - 1;
                	}
                	return;
                }
                
                selectedIndex--;
                if (selectedIndex < 0) {
                    selectedIndex = categoryTabs.size() - 1;
                }
                break;

            case Keyboard.KEY_RIGHT:
            case Keyboard.KEY_RETURN: // enter
            	TabCategoryComponent tab = categoryTabs.get(selectedIndex);
                if (selectedCheatIndex != -1) {
                	tab.getCheatTabs().get(selectedCheatIndex).input(keyCode, true);
                    return;
                }
                
                if (tab.getCheatTabs().isEmpty()) return;
                selectedCheatIndex = 0;
                break;

            case Keyboard.KEY_LEFT:
                if (selectedCheatIndex == -1) return;
                selectedCheatIndex = -1;
                break;
        }
    }
}
