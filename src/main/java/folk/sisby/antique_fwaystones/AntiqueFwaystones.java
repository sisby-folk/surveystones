package folk.sisby.antique_fwaystones;

import hunternif.mc.api.AtlasAPI;
import hunternif.mc.impl.atlas.marker.Marker;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.lifecycle.api.event.ServerWorldLoadEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wraith.fwaystones.FabricWaystones;
import wraith.fwaystones.access.WaystoneValue;
import wraith.fwaystones.block.WaystoneBlockEntity;
import wraith.fwaystones.integration.event.WaystoneEvents;

import java.util.HashMap;

public class AntiqueFwaystones implements ModInitializer {
	public static final String ID = "antique_fwaystones";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);
	private static final String DATA_KEY = "aFwaystonesMarkerMap";

	public static final Identifier WAYSTONE_MARKER_ID = new Identifier(ID, "waystone");

	public static AntiqueFwaystoneMapState MAP_STATE;

	@Override
	public void onInitialize(ModContainer mod) {
		WaystoneEvents.DISCOVER_WAYSTONE_EVENT.register(AntiqueFwaystones::discoverWaystone);
		WaystoneEvents.REMOVE_WAYSTONE_EVENT.register(AntiqueFwaystones::removeWaystone);
		WaystoneEvents.RENAME_WAYSTONE_EVENT.register(AntiqueFwaystones::renameWaystone);
		ServerWorldLoadEvents.LOAD.register(((server, world) -> {
			if (world.getRegistryKey() == World.OVERWORLD) {
				MAP_STATE = world.getPersistentStateManager().getOrCreate(AntiqueFwaystoneMapState::readNbt, () -> {
					AntiqueFwaystoneMapState state = new AntiqueFwaystoneMapState(new HashMap<>());
					state.markDirty();
					return state;
				}, DATA_KEY);
			}
		}
		));
		LOGGER.info("[Antique Fwaystones] Initialized.");
	}

	public static void discoverWaystone(String hash) {
		if (MAP_STATE != null && !MAP_STATE.waystoneMarkers.containsKey(hash)) {
			WaystoneValue waystone = FabricWaystones.WAYSTONE_STORAGE.getWaystoneData(hash);
			if (waystone != null) {
				WaystoneBlockEntity waystoneEntity = waystone.getEntity();
				if (waystoneEntity != null) {
					World waystoneWorld = waystoneEntity.getWorld();
					if (waystoneWorld != null) {
						removeWaystone(hash);
						Marker marker = AtlasAPI.getMarkerAPI().putGlobalMarker(
							waystoneWorld,
							false,
							WAYSTONE_MARKER_ID,
							Text.literal(waystone.getWaystoneName()),
							waystone.way_getPos().getX(),
							waystone.way_getPos().getZ()
						);
						if (marker != null) {
							MAP_STATE.waystoneMarkers.put(hash, marker.getId());
							MAP_STATE.markDirty();
						}
					}
				}
			}
		}
	}

	public static void renameWaystone(String hash) {
		removeWaystone(hash);
		discoverWaystone(hash);
	}

	public static void removeWaystone(String hash) {
		if (MAP_STATE != null && MAP_STATE.waystoneMarkers.containsKey(hash)) {
			WaystoneValue waystone = FabricWaystones.WAYSTONE_STORAGE.getWaystoneData(hash);
			if (waystone != null) {
				WaystoneBlockEntity waystoneEntity = waystone.getEntity();
				if (waystoneEntity != null) {
					World waystoneWorld = waystoneEntity.getWorld();
					if (waystoneWorld != null) {
						AtlasAPI.getMarkerAPI().deleteGlobalMarker(waystoneWorld, MAP_STATE.waystoneMarkers.get(hash));
						MAP_STATE.waystoneMarkers.remove(hash);
						MAP_STATE.markDirty();
					}
				}
			}
		}
	}
}
