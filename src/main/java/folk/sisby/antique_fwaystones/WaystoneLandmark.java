package folk.sisby.antique_fwaystones;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import folk.sisby.surveyor.landmark.Landmark;
import folk.sisby.surveyor.landmark.LandmarkType;
import folk.sisby.surveyor.landmark.SimpleLandmarkType;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record WaystoneLandmark(BlockPos pos, String hash, int fullColor, Text name) implements Landmark<WaystoneLandmark> {
	public static LandmarkType<WaystoneLandmark> TYPE = new SimpleLandmarkType<>(new Identifier("antique_fwaystones:waystone"), pos -> RecordCodecBuilder.create(instance -> instance.group(
		Codec.STRING.fieldOf("hash").forGetter(WaystoneLandmark::hash),
		Codec.INT.fieldOf("fullColor").orElse(null).forGetter(WaystoneLandmark::fullColor),
		CodecUtil.TEXT_CODEC.fieldOf("name").orElse(null).forGetter(WaystoneLandmark::name)
	).apply(instance, (hash, fullColor, name) -> new WaystoneLandmark(pos, hash, fullColor, name))));

	@Override
	public LandmarkType<WaystoneLandmark> type() {
		return TYPE;
	}

	@Override
	public DyeColor color() {
		return DyeColor.GREEN; // Replace this with a DyeColor guesser
	}
}
