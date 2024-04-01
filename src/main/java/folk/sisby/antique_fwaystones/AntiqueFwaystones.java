package folk.sisby.antique_fwaystones;

import folk.sisby.surveyor.WorldSummary;
import folk.sisby.surveyor.landmark.Landmarks;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wraith.fwaystones.FabricWaystones;
import wraith.fwaystones.access.WaystoneValue;
import wraith.fwaystones.block.WaystoneBlockEntity;
import wraith.fwaystones.integration.event.WaystoneEvents;

public class AntiqueFwaystones implements ModInitializer {
	public static final String ID = "antique_fwaystones";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	@Override
	public void onInitialize() {
		WaystoneEvents.DISCOVER_WAYSTONE_EVENT.register(AntiqueFwaystones::discoverWaystone);
		WaystoneEvents.REMOVE_WAYSTONE_EVENT.register(AntiqueFwaystones::removeWaystone);
		WaystoneEvents.RENAME_WAYSTONE_EVENT.register(AntiqueFwaystones::renameWaystone);
		Landmarks.register(WaystoneLandmark.TYPE);
		LOGGER.info("[Antique Fwaystones] Initialized.");
	}

	public static void discoverWaystone(String hash) {
		WaystoneValue waystone = FabricWaystones.WAYSTONE_STORAGE.getWaystoneData(hash);
		if (waystone != null) {
			WaystoneBlockEntity waystoneEntity = waystone.getEntity();
            if (waystoneEntity != null && waystoneEntity.getWorld() instanceof ServerWorld sw) {
                removeWaystone(hash);
                WorldSummary.of(sw).landmarks().put(sw, new WaystoneLandmark(waystone.way_getPos(), hash, waystone.getColor(), Text.of(waystone.getWaystoneName())));
            }
		}
	}

	public static void renameWaystone(String hash) {
		removeWaystone(hash);
		discoverWaystone(hash);
	}

	public static void removeWaystone(String hash) {
		WaystoneValue waystone = FabricWaystones.WAYSTONE_STORAGE.getWaystoneData(hash);
		if (waystone != null) {
			WaystoneBlockEntity waystoneEntity = waystone.getEntity();
			if (waystoneEntity != null) {
				if (waystoneEntity.getWorld() instanceof ServerWorld sw) {
					WorldSummary.of(sw).landmarks().remove(sw, WaystoneLandmark.TYPE, waystone.way_getPos());
				}
			}
		}
	}
}
