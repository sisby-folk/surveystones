package folk.sisby.surveystones;

import folk.sisby.surveyor.landmark.Landmark;
import folk.sisby.surveyor.landmark.WorldLandmarks;
import folk.sisby.surveyor.landmark.component.LandmarkComponentTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import wraith.fwaystones.FabricWaystones;
import wraith.fwaystones.access.WaystoneValue;
import wraith.fwaystones.block.WaystoneBlock;
import wraith.fwaystones.block.WaystoneBlockEntity;
import wraith.fwaystones.integration.event.WaystoneEvents;

import java.util.List;

public class FwaystoneEvents {
	public static void init() {
		WaystoneEvents.DISCOVER_WAYSTONE_EVENT.register(FwaystoneEvents::discoverWaystone);
		WaystoneEvents.REMOVE_WAYSTONE_EVENT.register(FwaystoneEvents::removeWaystone);
		WaystoneEvents.RENAME_WAYSTONE_EVENT.register(FwaystoneEvents::discoverWaystone);
	}

	public static Identifier getId(ServerWorld sw, WaystoneValue waystone) {
		BlockState state = sw.getBlockState(waystone.way_getPos());
		BlockPos pos = state.contains(WaystoneBlock.HALF) && state.get(WaystoneBlock.HALF) == DoubleBlockHalf.UPPER ? waystone.way_getPos().down() : waystone.way_getPos();
		String shortType = "waystone";
		String variant = sw.getRegistryManager().get(RegistryKeys.ITEM).getId(state.getBlock().getPickStack(sw, pos, state).getItem()).getPath().replace("_" + shortType, "");
		return Identifier.of("fwaystones", shortType + "/" + variant + "/" + pos.toShortString().replace(", ", "/"));
	}

	public static void discoverWaystone(String hash) {
		WaystoneValue waystone = FabricWaystones.WAYSTONE_STORAGE.getWaystoneData(hash);
		if (waystone != null) {
			WaystoneBlockEntity waystoneEntity = waystone.getEntity();
			if (waystoneEntity != null && waystoneEntity.getWorld() instanceof ServerWorld world) {
				WorldLandmarks landmarks = WorldLandmarks.of(world);
				if (landmarks == null) return;
				landmarks.put(Landmark.create(WorldLandmarks.GLOBAL, getId(world, waystone), b -> LandmarkComponentTypes.forBlock(b, world, waystone.way_getPos())
					.add(LandmarkComponentTypes.COLOR, waystone.getColor())
					.add(LandmarkComponentTypes.NAME, Text.of(waystone.getWaystoneName()))
					.add(LandmarkComponentTypes.LORE, List.of(world.getBlockState(waystone.way_getPos()).getBlock().getPickStack(world, waystone.way_getPos(), world.getBlockState(waystone.way_getPos())).getItem().getName().copy().setStyle(Style.EMPTY.withItalic(true).withColor(Formatting.GRAY))))
				));
			}
		}
	}

	public static void removeWaystone(String hash) {
		WaystoneValue waystone = FabricWaystones.WAYSTONE_STORAGE.getWaystoneData(hash);
		if (waystone != null) {
			WaystoneBlockEntity waystoneEntity = waystone.getEntity();
			if (waystoneEntity != null) {
				if (waystoneEntity.getWorld() instanceof ServerWorld sw) {
					WorldLandmarks landmarks = WorldLandmarks.of(sw);
					if (landmarks == null) return;
					landmarks.remove(WorldLandmarks.GLOBAL, getId(sw, waystone));
				}
			}
		}
	}
}
