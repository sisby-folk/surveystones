package folk.sisby.surveystones;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import folk.sisby.surveyor.landmark.Landmark;
import folk.sisby.surveyor.landmark.LandmarkType;
import folk.sisby.surveyor.landmark.SimpleLandmarkType;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public record BlaystoneLandmark(BlockPos pos, Text waystoneName) implements Landmark<BlaystoneLandmark> {
	public static LandmarkType<BlaystoneLandmark> TYPE = new SimpleLandmarkType<>(Surveystones.id("blaystone"), pos -> RecordCodecBuilder.create(instance -> instance.group(
		TextCodecs.CODEC.fieldOf("waystoneName").orElse(null).forGetter(BlaystoneLandmark::waystoneName)
	).apply(instance, (name) -> new BlaystoneLandmark(pos, name))));

	@Override
	public LandmarkType<BlaystoneLandmark> type() {
		return TYPE;
	}

	@Override
	public @Nullable Text name() {
		return waystoneName;
	}

	@Override
	public DyeColor color() {
		return DyeColor.WHITE;
	}
}
