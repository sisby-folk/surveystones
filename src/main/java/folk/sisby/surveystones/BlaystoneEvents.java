package folk.sisby.surveystones;

import folk.sisby.surveyor.WorldSummary;
import folk.sisby.surveyor.landmark.Landmarks;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.api.WaystoneActivatedEvent;
import net.blay09.mods.waystones.api.WaystoneUpdateReceivedEvent;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayDeque;
import java.util.Deque;

public class BlaystoneEvents {
	public static Deque<IWaystone> updates = new ArrayDeque<>();
	public static Deque<Pair<RegistryKey<World>, BlockPos>> removals = new ArrayDeque<>();

	public static void init() {
		Balm.getEvents().onEvent(WaystoneActivatedEvent.class, BlaystoneEvents::activateWaystone);
		Balm.getEvents().onEvent(WaystoneUpdateReceivedEvent.class, e -> updates.addLast(e.getWaystone()));
		ServerTickEvents.END_SERVER_TICK.register(BlaystoneEvents::tick);
		Landmarks.register(BlaystoneLandmark.TYPE);
	}

	private static void updateWaystone(ServerWorld world, IWaystone waystone) {
		if (waystone.isValidInLevel(world)) {
			Surveystones.LOGGER.info("Trying to add blaystone at {} with name {}", waystone.getPos(), waystone.getName());
			WorldSummary.of(world).landmarks().put(world, new BlaystoneLandmark(waystone.getPos(), waystone.getName()));
		} else {
			Surveystones.LOGGER.info("Trying to remove blaystone at {} with name {}", waystone.getPos(), waystone.getName());
			WorldSummary.of(world).landmarks().remove(world, BlaystoneLandmark.TYPE, waystone.getPos());
		}
	}

	private static void removeWaystone(ServerWorld world, BlockPos pos) {
		Surveystones.LOGGER.info("Trying to remove blaystone at {}", pos);
		WorldSummary.of(world).landmarks().remove(world, BlaystoneLandmark.TYPE, pos);
	}

	private static void activateWaystone(WaystoneActivatedEvent event) {
		World world = event.getPlayer().getWorld();
		if (!world.isClient()) updateWaystone((ServerWorld) world, event.getWaystone());
	}

	public static void removeWaystone(IWaystone waystone) {
		updates.addLast(waystone);
	}

	private static void tick(MinecraftServer server) {
		while (!updates.isEmpty()) {
			IWaystone waystone = updates.pop();
			ServerWorld world = server.getWorld(waystone.getDimension());
			if (world != null && !world.isClient()) updateWaystone(world, waystone);
		}
		while (!removals.isEmpty()) {
			Pair<RegistryKey<World>, BlockPos> waystone = removals.pop();
			ServerWorld world = server.getWorld(waystone.getLeft());
			// if (world != null && !world.isClient()) removeWaystone(world, waystone.getRight());
		}
	}
}
