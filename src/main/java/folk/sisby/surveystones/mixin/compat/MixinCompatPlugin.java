package folk.sisby.surveystones.mixin.compat;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class MixinCompatPlugin implements IMixinConfigPlugin {
	@Override
	public void onLoad(String mixinPackage) {
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		if (mixinClassName.startsWith("folk.sisby.surveystones.mixin.compat.")) {
			int startModID = mixinClassName.indexOf(".compat.") + ".compat.".length();
			int endModID  = mixinClassName.indexOf('.', startModID);
			String modID = mixinClassName.substring(startModID, endModID);
			if (FabricLoader.getInstance().isModLoaded(modID)) {
				System.out.println("[Surveystones] Enabling mixin " + mixinClassName);
				return true;
			} else {
				System.out.println("[Surveystones] Disabling mixin " + mixinClassName);
				return false;
			}
		}
		return true;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
	}

	@Override
	public List<String> getMixins() {
		return null;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}
}
