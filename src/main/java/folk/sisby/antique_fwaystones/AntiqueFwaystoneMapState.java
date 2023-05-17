package folk.sisby.antique_fwaystones;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.PersistentState;

import java.util.HashMap;
import java.util.Map;

public class AntiqueFwaystoneMapState extends PersistentState {
	public final Map<String, Integer> waystoneMarkers;

	public AntiqueFwaystoneMapState(Map<String, Integer> waystoneMarkers) {
		this.waystoneMarkers = waystoneMarkers;
	}

	public static AntiqueFwaystoneMapState readNbt(NbtCompound compound) {
		Map<String, Integer> outMap = new HashMap<>();
		for (String key : compound.getKeys()) {
			outMap.put(key, compound.getInt(key));
		}
		return new AntiqueFwaystoneMapState(outMap);
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		waystoneMarkers.forEach(nbt::putInt);
		return nbt;
	}
}
