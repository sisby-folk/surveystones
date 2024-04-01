package folk.sisby.antique_fwaystones;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.text.Text;

public class CodecUtil {
	public static final Codec<JsonElement> JSON_ELEMENT = Codec.PASSTHROUGH
		.xmap(dynamic -> dynamic.convert(JsonOps.INSTANCE).getValue(), element -> new Dynamic<>(JsonOps.INSTANCE, element));
	public static final Codec<Text> TEXT_CODEC = JSON_ELEMENT.flatXmap(element -> {
		try {
			return DataResult.success(Text.Serializer.fromJson(element));
		} catch (JsonParseException e) {
			return DataResult.error(e.getMessage());
		}
	}, text -> {
		try {
			return DataResult.success(Text.Serializer.toJsonTree(text));
		} catch (IllegalArgumentException e) {
			return DataResult.error(e.getMessage());
		}
	});
}
