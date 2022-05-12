package dev.rabies.vox.render.hud.elements;

import dev.rabies.vox.VoxMod;
import dev.rabies.vox.cheats.CheatWrapper;
import dev.rabies.vox.events.render.Render2DEvent;
import dev.rabies.vox.render.RenderHook;
import dev.rabies.vox.render.font.SystemFontRenderer;
import dev.rabies.vox.render.hud.HudElement;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ArrayListHud extends HudElement {

    private final SystemFontRenderer cheatInfoFont = VoxMod.get().newSystemFont("Mukta-SemiBold", 20);

    public ArrayListHud() {
        super("ArrayList");
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public void render(RenderHook hook, Render2DEvent event) {
        List<CheatWrapper> sorted = VoxMod.get().getCheats().stream()
                .filter(CheatWrapper::isEnabled)
                .sorted(Comparator.comparingDouble(it -> {
                    String label = it.getName();
                    if (it.getSuffix() != null && it.getSuffix().toString().length() > 0 &&
                            VoxMod.get().isDebugMode()) {
                        label += " \2477" + it.getSuffix().toString();
                    }
                    return -cheatInfoFont.getStringWidth(label);
                })).collect(Collectors.toList());

        double offsetY = hook.getTabGuiCheat().isEnabled() ? 68 : cheatInfoFont.getHeight() + 5;
        for (CheatWrapper cheat : sorted) {
            String label = cheat.getName();
            if (cheat.getSuffix() != null && cheat.getSuffix().toString().length() > 0 &&
                    VoxMod.get().isDebugMode()) {
                label += " \2477" + cheat.getSuffix().toString();
            }

            cheatInfoFont.drawStringWithShadow(label, 5, offsetY, -1);
            offsetY += cheatInfoFont.getHeight();
        }
    }
}