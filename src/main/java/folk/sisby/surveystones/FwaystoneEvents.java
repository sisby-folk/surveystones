package folk.sisby.surveystones;

import folk.sisby.surveyor.WorldSummary;
import folk.sisby.surveyor.landmark.Landmarks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import wraith.fwaystones.FabricWaystones;
import wraith.fwaystones.access.WaystoneValue;
import wraith.fwaystones.block.WaystoneBlockEntity;
import wraith.fwaystones.integration.event.WaystoneEvents;

public class FwaystoneEvents {
	public static void init() {
		WaystoneEvents.DISCOVER_WAYSTONE_EVENT.register(FwaystoneEvents::discoverWaystone);
		WaystoneEvents.REMOVE_WAYSTONE_EVENT.register(FwaystoneEvents::removeWaystone);
		WaystoneEvents.RENAME_WAYSTONE_EVENT.register(FwaystoneEvents::discoverWaystone);
		Landmarks.register(FwaystoneLandmark.TYPE);
	}

	public static void discoverWaystone(String hash) {
		WaystoneValue waystone = FabricWaystones.WAYSTONE_STORAGE.getWaystoneData(hash);
		if (waystone != null) {
			WaystoneBlockEntity waystoneEntity = waystone.getEntity();
			if (waystoneEntity != null && waystoneEntity.getWorld() instanceof ServerWorld sw) {
				removeWaystone(hash);
				WorldSummary.of(sw).landmarks().put(sw, new FwaystoneLandmark(waystone.way_getPos(), hash, waystone.getColor(), Text.of(waystone.getWaystoneName())));
			}
		}
	}

	public static void removeWaystone(String hash) {
		WaystoneValue waystone = FabricWaystones.WAYSTONE_STORAGE.getWaystoneData(hash);
		if (waystone != null) {
			WaystoneBlockEntity waystoneEntity = waystone.getEntity();
			if (waystoneEntity != null) {
				if (waystoneEntity.getWorld() instanceof ServerWorld sw) {
					WorldSummary.of(sw).landmarks().remove(sw, FwaystoneLandmark.TYPE, waystone.way_getPos());
				}
			}
		}
	}
}
