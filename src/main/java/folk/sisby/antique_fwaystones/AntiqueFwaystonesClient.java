package folk.sisby.antique_fwaystones;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AntiqueFwaystonesClient implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("antique_fwaystones_client");

	@Override
	public void onInitializeClient(ModContainer mod) {
		LOGGER.info("[Antique Fwaystones] Client Initialized.");
	}
}
