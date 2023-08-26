package folk.sisby.antique_fwaystones;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("deprecation")
public class AntiqueFwaystonesClient implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("antique_fwaystones_client");

	@Override
	public void onInitializeClient() {
		LOGGER.info("[Antique Fwaystones] Client Initialized.");
	}
}
