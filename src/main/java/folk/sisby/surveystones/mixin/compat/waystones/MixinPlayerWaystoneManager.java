package folk.sisby.surveystones.mixin.compat.waystones;

import folk.sisby.surveystones.BlaystoneEvents;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerWaystoneManager.class)
public class MixinPlayerWaystoneManager {
	@Inject(method = "removeKnownWaystone", at = @At("HEAD"), remap = false)
	private static void removeWaystoneEvent(MinecraftServer server, IWaystone waystone, CallbackInfo ci) {
		if (server != null) {
			BlaystoneEvents.removeWaystone(server.getWorld(waystone.getDimension()), waystone.getPos(), waystone.getName());
		}
	}
}
