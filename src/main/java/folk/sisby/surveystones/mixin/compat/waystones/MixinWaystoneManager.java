package folk.sisby.surveystones.mixin.compat.waystones;

import folk.sisby.surveystones.BlaystoneEvents;
import folk.sisby.surveystones.Surveystones;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.core.WaystoneManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WaystoneManager.class)
public class MixinWaystoneManager {
	@Inject(method = "removeWaystone", at = @At("TAIL"), remap = false)
	private void removeWaystone(IWaystone waystone, CallbackInfo ci) {
		Surveystones.LOGGER.info("Aa! removing a waystone!");
		BlaystoneEvents.removeWaystone(waystone);
	}
}
