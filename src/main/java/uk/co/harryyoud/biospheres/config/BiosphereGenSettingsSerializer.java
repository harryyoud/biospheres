package uk.co.harryyoud.biospheres.config;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.block.BlockState;
import net.minecraft.world.gen.OverworldGenSettings;
import uk.co.harryyoud.biospheres.config.serializers.BlockStateSerializer;
import uk.co.harryyoud.biospheres.config.serializers.ISerializer;
import uk.co.harryyoud.biospheres.config.serializers.IntegerSerializer;

public class BiosphereGenSettingsSerializer {
	//@formatter:off
	private static Map<String, ISerializer<?>> OPTIONS = ImmutableMap.<String, ISerializer<?>>builder()
			.put("domeBlock", new BlockStateSerializer())
			.put("bridgeBlock", new BlockStateSerializer())
			.put("outsideFillerBlock", new BlockStateSerializer())
			.put("sphereMidY", (new IntegerSerializer()).addInRange(0, 255))
			.put("sphereMinRadius", (new IntegerSerializer()).addInRange(15, 100))
			.put("sphereMaxRadius", (new IntegerSerializer()).addInRange(15, 100))
			.build();
	//@formatter:on
	private Map<String, String> values = new HashMap<>();

	public BiosphereGenSettingsSerializer() {
		values.put("domeBlock", BiosphereConfig.DEFAULT_PER_WORLD.domeBlock.get());
		values.put("bridgeBlock", BiosphereConfig.DEFAULT_PER_WORLD.bridgeBlock.get());
		values.put("outsideFillerBlock", BiosphereConfig.DEFAULT_PER_WORLD.outsideFillerBlock.get());
		values.put("sphereMidY", BiosphereConfig.DEFAULT_PER_WORLD.sphereMidY.get().toString());
		values.put("sphereMinRadius", BiosphereConfig.DEFAULT_PER_WORLD.sphereMinRadius.get().toString());
		values.put("sphereMaxRadius", BiosphereConfig.DEFAULT_PER_WORLD.sphereMaxRadius.get().toString());
	}

	public static BiosphereGenSettingsSerializer fromNBT(Dynamic<?> in) {
		BiosphereGenSettingsSerializer settings = new BiosphereGenSettingsSerializer();

		for (Map.Entry<String, ISerializer<?>> entry : OPTIONS.entrySet()) {
			String val;
			String key = entry.getKey();
			ISerializer<?> serializer = entry.getValue();
			if (serializer instanceof IntegerSerializer) {
				val = String.valueOf(in.get(key).asInt(-1));
			} else {
				val = in.get(key).asString("");
			}
			if (serializer.validate(val)) {
				settings.set(key, val);
			}
		}
		return settings;
	}

	public <T> Dynamic<T> toNBT(DynamicOps<T> in) {
		HashMap<T, T> map = new HashMap<>();
		for (Map.Entry<String, ISerializer<?>> entry : OPTIONS.entrySet()) {
			T val;
			if (entry.getValue() instanceof IntegerSerializer) {
				val = in.createInt(Integer.valueOf(this.get(entry.getKey())));
			} else {
				val = in.createString(this.get(entry.getKey()));
			}
			map.put(in.createString(entry.getKey()), val);
		}
		return new Dynamic<>(in, in.createMap(map));
	}

	public String get(String key) {
		return this.values.get(key);
	}

	public Predicate<String> getValidator(String key) {
		return OPTIONS.get(key)::validate;
	}

	public ISerializer<?> getSerializer(String key) {
		return OPTIONS.get(key);
	}

	public Object getValue(String key) {
		return this.getSerializer(key).deserialize(this.get(key));
	}

	public Map<String, ISerializer<?>> getSerializers() {
		return OPTIONS;
	}

	public void set(String key, String value) {
		if (!this.values.containsKey(key)) {
			return;
		}
		this.values.put(key, value);
	}

	public static BiosphereGenSettings get(Dynamic<?> in) {
		BiosphereGenSettingsSerializer serializer = BiosphereGenSettingsSerializer.fromNBT(in);
		return serializer.new BiosphereGenSettings();
	}

	public class BiosphereGenSettings extends OverworldGenSettings {
		public final BlockState domeBlock;
		public final BlockState bridgeBlock;
		public final BlockState outsideFillerBlock;
		public final int sphereMidY;
		public final int sphereMinRadius;
		public final int sphereMaxRadius;

		private BiosphereGenSettings() {
			this.domeBlock = (BlockState) BiosphereGenSettingsSerializer.this.getValue("domeBlock");
			this.bridgeBlock = (BlockState) BiosphereGenSettingsSerializer.this.getValue("bridgeBlock");
			this.outsideFillerBlock = (BlockState) BiosphereGenSettingsSerializer.this.getValue("outsideFillerBlock");
			this.sphereMidY = (Integer) BiosphereGenSettingsSerializer.this.getValue("sphereMidY");
			this.sphereMinRadius = (Integer) BiosphereGenSettingsSerializer.this.getValue("sphereMinRadius");
			this.sphereMaxRadius = (Integer) BiosphereGenSettingsSerializer.this.getValue("sphereMaxRadius");
		}
	}
}
