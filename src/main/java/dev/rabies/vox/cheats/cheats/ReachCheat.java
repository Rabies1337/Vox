package dev.rabies.vox.cheats.cheats;

import dev.rabies.vox.cheats.Category;
import dev.rabies.vox.cheats.Cheat;
import dev.rabies.vox.cheats.setting.BoolSetting;
import dev.rabies.vox.cheats.setting.NumberSetting;
import dev.rabies.vox.events.UpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ReachCheat extends Cheat {

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