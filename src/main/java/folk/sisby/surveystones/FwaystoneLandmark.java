package folk.sisby.surveystones;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import folk.sisby.surveyor.landmark.Landmark;
import folk.sisby.surveyor.landmark.LandmarkType;
import folk.sisby.surveyor.landmark.SimpleLandmarkType;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record FwaystoneLandmark(BlockPos pos, String hash, int fullColor, Text name) implements Landmark<FwaystoneLandmark> {
	public static LandmarkType<FwaystoneLandmark> TYPE = new SimpleLandmarkType<>(new Identifier("surveystones:fwaystone"), pos -> RecordCodecBuilder.create(instance -> instance.group(
		Codec.STRING.fieldOf("hash").forGetter(FwaystoneLandmark::hash),
		Codec.INT.fieldOf("fullColor").orElse(null).forGetter(FwaystoneLandmark::fullColor),
		CodecUtil.TEXT_CODEC.fieldOf("name").orElse(null).forGetter(FwaystoneLandmark::name)
	).apply(instance, (hash, fullColor, name) -> new FwaystoneLandmark(pos, hash, fullColor, name))));

	@Override
	public LandmarkType<FwaystoneLandmark> type() {
		return TYPE;
	}

	@Override
	public DyeColor color() {
		return DyeColor.GREEN; // Replace this with a DyeColor guesser
	}
}
