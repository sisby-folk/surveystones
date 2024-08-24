package folk.sisby.surveystones;

import folk.sisby.surveyor.WorldSummary;
import folk.sisby.surveyor.landmark.Landmarks;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.api.KnownWaystonesEvent;
import net.blay09.mods.waystones.api.WaystoneActivatedEvent;
import net.blay09.mods.waystones.api.WaystoneUpdateReceivedEvent;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayDeque;
import java.util.Deque;

public class BlaystoneEvents {
	public static Deque<IWaystone> updates = new ArrayDeque<>();

	public static void init() {
		Balm.getEvents().onEvent(WaystoneActivatedEvent.class, BlaystoneEvents::activateWaystone);
		Balm.getEvents().onEvent(WaystoneUpdateReceivedEvent.class, e -> updates.addLast(e.getWaystone()));
		Balm.getEvents().onEvent(KnownWaystonesEvent.class, e -> updates.addAll(e.getWaystones()));
		ServerTickEvents.END_SERVER_TICK.register(BlaystoneEvents::tick);
		Landmarks.register(BlaystoneLandmark.TYPE);
	}

	public static void updateWaystone(World world, IWaystone waystone) {
		addWaystone(world, waystone.getPos(), waystone.getName());
	}

	public static void addWaystone(World world, BlockPos pos, String name) {
		WorldSummary.of(world).landmarks().put(world, new BlaystoneLandmark(pos, name));
	}

	public static void removeWaystone(World world, BlockPos pos, String name) {
		WorldSummary.of(world).landmarks().remove(world, BlaystoneLandmark.TYPE, pos);
	}

	public static void activateWaystone(WaystoneActivatedEvent event) {
		World world = event.getPlayer().getWorld();
		updateWaystone(world, event.getWaystone());
	}

	private static void tick(MinecraftServer server) {
		while (!updates.isEmpty()) {
			IWaystone waystone = updates.pop();
			ServerWorld world = server.getWorld(waystone.getDimension());
			if (world != null) updateWaystone(world, waystone);
		}
	}
}
