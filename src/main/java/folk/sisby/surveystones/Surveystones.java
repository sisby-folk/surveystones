package folk.sisby.surveystones;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Surveystones implements ModInitializer {
	public static final String ID = "surveystones";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	public static Identifier id(String path) {
		return Identifier.of(ID, path);
	}

	@Override
	public void onInitialize() {
		if (FabricLoader.getInstance().isModLoaded("fwaystones")) FwaystoneEvents.init();
		if (FabricLoader.getInstance().isModLoaded("waystones")) BlaystoneEvents.init();
		LOGGER.info("[Surveystones] Initialized.");
	}
}
