package dev.rabies.vox.cheats.cheats;

import dev.rabies.vox.cheats.Category;
import dev.rabies.vox.cheats.CheatWrapper;
import dev.rabies.vox.cheats.setting.BoolSetting;
import dev.rabies.vox.cheats.setting.NumberSetting;
import dev.rabies.vox.events.game.UpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ReachCheat extends CheatWrapper {

	public final BoolSetting likeLegitSetting = registerBoolSetting("Like legit", false);
	public final NumberSetting reachSetting = registerNumberSetting("Reach", 3.8, 3.0, 10.0, 0.1);

	public ReachCheat() {
		super("Reach", Category.LEGIT);
	}

	@SubscribeEvent
	public void onUpdate(UpdateEvent event) {
		setSuffix(reachSetting.getValue());
	}
}
