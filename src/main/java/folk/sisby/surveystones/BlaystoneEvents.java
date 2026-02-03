package folk.sisby.surveystones;

import folk.sisby.surveyor.landmark.Landmark;
import folk.sisby.surveyor.landmark.WorldLandmarks;
import folk.sisby.surveyor.landmark.component.LandmarkComponentTypes;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.api.KnownWaystonesEvent;
import net.blay09.mods.waystones.api.WaystoneActivatedEvent;
import net.blay09.mods.waystones.api.WaystoneUpdateReceivedEvent;
import net.blay09.mods.waystones.block.WaystoneBlockBase;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class BlaystoneEvents {
	public static Deque<IWaystone> updates = new ArrayDeque<>();

	public static void init() {
		Balm.getEvents().onEvent(WaystoneActivatedEvent.class, BlaystoneEvents::activateWaystone);
		Balm.getEvents().onEvent(WaystoneUpdateReceivedEvent.class, e -> updates.addLast(e.getWaystone()));
		Balm.getEvents().onEvent(KnownWaystonesEvent.class, e -> updates.addAll(e.getWaystones()));
		ServerTickEvents.END_SERVER_TICK.register(BlaystoneEvents::tick);
	}

	public static Identifier getId(World sw, IWaystone waystone) {
		BlockState state = sw.getBlockState(waystone.getPos());
		BlockPos pos = state.contains(WaystoneBlockBase.HALF) && state.get(WaystoneBlockBase.HALF) == DoubleBlockHalf.UPPER ? waystone.getPos().down() : waystone.getPos();
		String shortType = waystone.getWaystoneType().getPath();
		String variant = sw.getRegistryManager().get(RegistryKeys.ITEM).getId(state.getBlock().getPickStack(sw, pos, state).getItem()).getPath().replace("_" + shortType, "");
		return Identifier.of("waystones", shortType + "/" + variant + "/" + pos.toShortString().replace(", ", "/"));
	}

	public static void updateWaystone(World world, IWaystone waystone) {
		WorldLandmarks landmarks = WorldLandmarks.of(world);
		if (landmarks == null) return;
		landmarks.put(Landmark.create(WorldLandmarks.GLOBAL, getId(world, waystone), b -> LandmarkComponentTypes.forBlock(b, world, waystone.getPos())
			.add(LandmarkComponentTypes.NAME, Text.of(waystone.getName()))
			.add(LandmarkComponentTypes.LORE, List.of(world.getBlockState(waystone.getPos()).getBlock().getPickStack(world, waystone.getPos(), world.getBlockState(waystone.getPos())).getItem().getName().copy().setStyle(Style.EMPTY.withItalic(true).withColor(Formatting.GRAY))))
		));
	}

	public static void removeWaystone(World world, IWaystone waystone) {
		WorldLandmarks landmarks = WorldLandmarks.of(world);
		if (landmarks == null) return;
		landmarks.removeAll(l ->
			l.id().toString().startsWith("waystones:" + waystone.getWaystoneType().getPath()) &&
			l.contains(LandmarkComponentTypes.POS) &&
			(l.get(LandmarkComponentTypes.POS).equals(waystone.getPos()) || l.get(LandmarkComponentTypes.POS).equals(waystone.getPos().down()))
		);
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
