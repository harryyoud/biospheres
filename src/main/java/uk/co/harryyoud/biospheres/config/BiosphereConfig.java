package uk.co.harryyoud.biospheres.config;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import uk.co.harryyoud.biospheres.Biospheres;
import uk.co.harryyoud.biospheres.config.serializers.BlockStateSerializer;

@EventBusSubscriber(modid = Biospheres.MODID, bus = EventBusSubscriber.Bus.MOD)
public class BiosphereConfig {
	public static final ForgeConfigSpec GENERAL_SPEC;
	public static final GeneralConfig GENERAL;
	public static final ForgeConfigSpec DEFAULT_PER_WORLD_SPEC;
	public static final DefaultPerWorldConfig DEFAULT_PER_WORLD;
	public static final ForgeConfigSpec CLIENT_SPEC;
	public static final ClientConfig CLIENT;

	static {
		final Pair<GeneralConfig, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder()
				.configure(GeneralConfig::new);
		GENERAL = commonSpecPair.getLeft();
		GENERAL_SPEC = commonSpecPair.getRight();
		final Pair<ClientConfig, ForgeConfigSpec> clientSpecPair = new ForgeConfigSpec.Builder()
				.configure(ClientConfig::new);
		CLIENT = clientSpecPair.getLeft();
		CLIENT_SPEC = clientSpecPair.getRight();
		final Pair<DefaultPerWorldConfig, ForgeConfigSpec> worldGenSpecPair = new ForgeConfigSpec.Builder()
				.configure(DefaultPerWorldConfig::new);
		DEFAULT_PER_WORLD = worldGenSpecPair.getLeft();
		DEFAULT_PER_WORLD_SPEC = worldGenSpecPair.getRight();
	}

	public static boolean shouldInjectWorldType;
	public static float cloudHeight;
	public static List<String> bannedBiomes;
	public static List<Biome.Category> bannedBiomeCategories;
	public static List<BlockState> bannedBlocks;

	public static void setup() {
		Path configPath = FMLPaths.CONFIGDIR.get();
		Path ourConfigPath = Paths.get(configPath.toAbsolutePath().toString(), "biospheres");

		try {
			Files.createDirectory(ourConfigPath);
		} catch (FileAlreadyExistsException e) {
			// Do nothing
		} catch (IOException e) {
			System.out.println("Failed to create biospheres config directory");
		}
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON,
				BiosphereConfig.GENERAL_SPEC,
				"biospheres/common.toml");
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, BiosphereConfig.DEFAULT_PER_WORLD_SPEC,
				"biospheres/world-generation-defaults.toml");
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, BiosphereConfig.CLIENT_SPEC,
				"biospheres/client.toml");
	}

	@SubscribeEvent
	public static void onModConfigEvent(final ModConfig.ModConfigEvent configEvent) {
		if (configEvent.getConfig().getSpec() == BiosphereConfig.GENERAL_SPEC) {
			bakeGeneralConfig();
		}
		if (configEvent.getConfig().getSpec() == BiosphereConfig.CLIENT_SPEC) {
			bakeClientConfig();
		}
	}

	@SuppressWarnings("unchecked")
	public static void bakeGeneralConfig() {
		shouldInjectWorldType = GENERAL.shouldInjectWorldType.get();
		bannedBiomes = (List<String>) GENERAL.bannedBiomes.get();
		bannedBiomeCategories = GENERAL.bannedBiomeCategories
				.get()
				.stream()
				.map((cat) -> Biome.Category.valueOf(cat)).collect(Collectors.toList());
		// We lazy calculate bannedBlocks later, once modded blocks have loaded
	}

	public static void bakeClientConfig() {
		cloudHeight = CLIENT.cloudHeight.get();
	}

	public static class DefaultPerWorldConfig {
		public final IntValue sphereMidY;
		public final IntValue sphereMinRadius;
		public final IntValue sphereMaxRadius;
		public final ConfigValue<String> domeBlock;
		public final ConfigValue<String> outsideFillerBlock;
		public final ConfigValue<String> bridgeBlock;
		public final ConfigValue<List<? extends String>> bannedBlocks;

		public DefaultPerWorldConfig(ForgeConfigSpec.Builder builder) {
			//@formatter:off
			builder.push("spheres");
			this.sphereMidY = builder
					.comment("Sea level (aka vertical midpoint of spheres)\n"
							+ "This should be between sphereMaxRadius and (256 - sphereMaxRadius)")
					.defineInRange("sphereMidY", 100, 0, 255);
			this.sphereMinRadius = builder
					.comment("Minimum sphere radius")
					.defineInRange("sphereMinRadius", 40, 15, 100);
			this.sphereMaxRadius = builder
					.comment("Maximum sphere radius")
					.defineInRange("sphereMaxRadius", 100, 15, 100);
			builder.pop();


			builder.push("blocks");
			this.domeBlock = builder
					.comment("Block to use for the dome")
					.define("domeBlock",
							(new BlockStateSerializer()).serialize(Blocks.WHITE_STAINED_GLASS.getDefaultState()),
							(new BlockStateSerializer())::validate);
			this.outsideFillerBlock = builder
					.comment("Block to use for outside the spheres")
					.define("outsideFillerBlock",
							(new BlockStateSerializer()).serialize(Blocks.AIR.getDefaultState()),
							(new BlockStateSerializer())::validate);
			this.bridgeBlock = builder
					.comment("Block to use for the bridges between spheres")
					.define("bridgeBlock",
							(new BlockStateSerializer()).serialize(Blocks.OAK_PLANKS.getDefaultState()),
							(new BlockStateSerializer())::validate);
			this.bannedBlocks = builder
					.comment("Blocks to always replace with dome blocks if at the radius")
					.defineList("bannedBlocks", Arrays.asList(
								(new BlockStateSerializer()).serialize(Blocks.AIR.getDefaultState()),
								(new BlockStateSerializer()).serialize(Blocks.WATER.getDefaultState()),
								(new BlockStateSerializer()).serialize(Blocks.LAVA.getDefaultState())
							),
							(new BlockStateSerializer())::validate);
			//@formatter:on
			builder.pop();
		}
	}

	public static class GeneralConfig {
		public final BooleanValue shouldInjectWorldType;
		public final ConfigValue<List<? extends String>> bannedBiomes;
		public final ConfigValue<List<? extends String>> bannedBiomeCategories;

		public GeneralConfig(ForgeConfigSpec.Builder builder) {
			//@formatter:off
			this.shouldInjectWorldType = builder
					.comment("Inject level-type on dedicated server startup. Won't do anything on a client.")
					.translation(Biospheres.MODID + ".config." + "shouldInjectWorldType")
					.define("shouldInjectWorldType", true);


			builder.push("biomes");
			this.bannedBiomes = builder
					.comment("Don't use these biomes")
					.defineList("bannedBiomes", Arrays.asList(Biomes.THE_VOID.getRegistryName().toString()),
							(s) -> ForgeRegistries.BIOMES.containsKey(new ResourceLocation((String) s)));
			this.bannedBiomeCategories = builder
					.comment("Don't use these biome categories")
					.defineList("bannedBiomeCategories", Arrays.asList(
								Biome.Category.THEEND.toString(),
								Biome.Category.NETHER.toString()
							),
							this::validateBiomeCategory);
			builder.pop();

			//@formatter:on
		}

		private boolean validateBiomeCategory(Object obj) {
			try {
				String s = (String) obj;
				Biome.Category.valueOf(s);
				return true;
			} catch (IllegalArgumentException e) {
				return false;
			}
		}
	}

	public static class ClientConfig {
		public final IntValue cloudHeight;

		public ClientConfig(ForgeConfigSpec.Builder builder) {
			this.cloudHeight = builder.comment("Cloud height\n"
					+ "Useful for when spheres are high up and/or big")
					.translation(Biospheres.MODID + ".config." + "cloudHeight")
					.defineInRange("cloudHeight", 255, 0, 256);
		}
	}
}
