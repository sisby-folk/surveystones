package folk.sisby.surveystones;

import folk.sisby.surveyor.WorldSummary;
import folk.sisby.surveyor.landmark.Landmarks;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.event.WaystoneActivatedEvent;
import net.blay09.mods.waystones.api.event.WaystoneRemovedEvent;
import net.blay09.mods.waystones.api.event.WaystoneUpdateReceivedEvent;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayDeque;
import java.util.Deque;

public class BlaystoneEvents {
	public static Deque<Waystone> updates = new ArrayDeque<>();
	public static Deque<Waystone> removals = new ArrayDeque<>();

	public static void init() {
		Balm.getEvents().onEvent(WaystoneActivatedEvent.class, BlaystoneEvents::activateWaystone);
		Balm.getEvents().onEvent(WaystoneUpdateReceivedEvent.class, e -> updates.addLast(e.getWaystone()));
		Balm.getEvents().onEvent(WaystoneRemovedEvent.class, e -> removals.addLast(e.getWaystone()));
		ServerTickEvents.END_SERVER_TICK.register(BlaystoneEvents::tick);
		Landmarks.register(BlaystoneLandmark.TYPE);
	}

	public static void updateWaystone(World world, Waystone waystone) {
		addWaystone(world, waystone.getPos(), waystone.getName());
	}

	public static void removeWaystone(World world, Waystone waystone) {
		removeWaystone(world, waystone.getPos(), waystone.getName());
	}

	public static void addWaystone(World world, BlockPos pos, Text name) {
		WorldSummary.of(world).landmarks().put(world, new BlaystoneLandmark(pos, name));
	}

	public static void removeWaystone(World world, BlockPos pos, Text name) {
		WorldSummary.of(world).landmarks().remove(world, BlaystoneLandmark.TYPE, pos);
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
