package folk.sisby.surveystones;

import folk.sisby.surveyor.WorldSummary;
import folk.sisby.surveyor.landmark.Landmark;
import folk.sisby.surveyor.landmark.WorldLandmarks;
import folk.sisby.surveyor.landmark.component.LandmarkComponentTypes;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.event.WaystoneActivatedEvent;
import net.blay09.mods.waystones.api.event.WaystoneRemovedEvent;
import net.blay09.mods.waystones.api.event.WaystoneUpdateReceivedEvent;
import net.blay09.mods.waystones.block.WaystoneBlockBase;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class BlaystoneEvents {
	public static Deque<Waystone> updates = new ArrayDeque<>();
	public static Deque<Waystone> removals = new ArrayDeque<>();

	public static void init() {
		Balm.getEvents().onEvent(WaystoneActivatedEvent.class, BlaystoneEvents::activateWaystone);
		Balm.getEvents().onEvent(WaystoneUpdateReceivedEvent.class, e -> updates.addLast(e.getWaystone()));
		Balm.getEvents().onEvent(WaystoneRemovedEvent.class, e -> removals.addLast(e.getWaystone()));
		ServerTickEvents.END_SERVER_TICK.register(BlaystoneEvents::tick);
	}

	public static Identifier getId(World sw, Waystone waystone) {
		BlockState state = sw.getBlockState(waystone.getPos());
		BlockPos pos = state.contains(WaystoneBlockBase.HALF) && state.get(WaystoneBlockBase.HALF) == DoubleBlockHalf.UPPER ? waystone.getPos().down() : waystone.getPos();
		String shortType = waystone.getWaystoneType().getPath();
		String variant = sw.getRegistryManager().get(RegistryKeys.ITEM).getId(state.getBlock().getPickStack(sw, pos, state).getItem()).getPath().replace("_" + shortType, "");
		return Identifier.of("waystones", shortType + "/" + variant + "/" + pos.toShortString().replace(", ", "/"));
	}

	public static void updateWaystone(World world, Waystone waystone) {
		WorldLandmarks landmarks = WorldSummary.of(world).landmarks();
		if (landmarks == null) return;
		landmarks.put(world, Landmark.create(WorldLandmarks.GLOBAL, getId(world, waystone), b -> LandmarkComponentTypes.forBlock(b, world, waystone.getPos())
			.add(LandmarkComponentTypes.NAME, Text.of(waystone.getName()))
			.add(LandmarkComponentTypes.LORE, List.of(world.getBlockState(waystone.getPos()).getBlock().getPickStack(world, waystone.getPos(), world.getBlockState(waystone.getPos())).getItem().getName().copy().setStyle(Style.EMPTY.withItalic(true).withColor(Formatting.GRAY))))
		));
	}

	public static void removeWaystone(World world, Waystone waystone) {
		WorldLandmarks landmarks = WorldSummary.of(world).landmarks();
		if (landmarks == null) return;
		landmarks.remove(world, WorldLandmarks.GLOBAL, getId(world, waystone));
	}

	public static void activateWaystone(WaystoneActivatedEvent event) {
		World world = event.getPlayer().getWorld();
		updateWaystone(world, event.getWaystone());
	}

	private static void tick(MinecraftServer server) {
		while (!updates.isEmpty()) {
			Waystone waystone = updates.pop();
			ServerWorld world = server.getWorld(waystone.getDimension());
			if (world != null) updateWaystone(world, waystone);
		}
		while (!removals.isEmpty()) {
			Waystone waystone = removals.pop();
			ServerWorld world = server.getWorld(waystone.getDimension());
			if (world != null) removeWaystone(world, waystone);
		}
	}
}
