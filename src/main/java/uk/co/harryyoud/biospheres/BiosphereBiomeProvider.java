package uk.co.harryyoud.biospheres;

import java.util.Random;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProvider;

public class BiosphereBiomeProvider extends BiomeProvider {

	private final World world;
	// List of biomes we will use
	private static final Set<Biome> biomes = ImmutableSet.of(Biomes.PLAINS, Biomes.DESERT, Biomes.MOUNTAINS,
			Biomes.FOREST, Biomes.TAIGA, Biomes.SWAMP, Biomes.RIVER, Biomes.SNOWY_TUNDRA, Biomes.SNOWY_MOUNTAINS,
			Biomes.MUSHROOM_FIELDS, Biomes.MUSHROOM_FIELD_SHORE, Biomes.DESERT_HILLS, Biomes.WOODED_HILLS,
			Biomes.TAIGA_HILLS, Biomes.MOUNTAIN_EDGE, Biomes.JUNGLE, Biomes.JUNGLE_HILLS, Biomes.JUNGLE_EDGE,
			Biomes.DEEP_OCEAN, Biomes.STONE_SHORE, Biomes.SNOWY_BEACH, Biomes.BIRCH_FOREST, Biomes.BIRCH_FOREST_HILLS,
			Biomes.DARK_FOREST, Biomes.SNOWY_TAIGA, Biomes.SNOWY_TAIGA_HILLS, Biomes.GIANT_TREE_TAIGA,
			Biomes.GIANT_TREE_TAIGA_HILLS, Biomes.WOODED_MOUNTAINS, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU,
			Biomes.BADLANDS, Biomes.WOODED_BADLANDS_PLATEAU, Biomes.BADLANDS_PLATEAU, Biomes.SUNFLOWER_PLAINS,
			Biomes.DESERT_LAKES, Biomes.GRAVELLY_MOUNTAINS, Biomes.FLOWER_FOREST, Biomes.TAIGA_MOUNTAINS,
			Biomes.SWAMP_HILLS, Biomes.ICE_SPIKES, Biomes.MODIFIED_JUNGLE, Biomes.MODIFIED_JUNGLE_EDGE,
			Biomes.TALL_BIRCH_FOREST, Biomes.TALL_BIRCH_HILLS, Biomes.DARK_FOREST_HILLS, Biomes.SNOWY_TAIGA_MOUNTAINS,
			Biomes.GIANT_SPRUCE_TAIGA, Biomes.GIANT_SPRUCE_TAIGA_HILLS, Biomes.MODIFIED_GRAVELLY_MOUNTAINS,
			Biomes.SHATTERED_SAVANNA, Biomes.SHATTERED_SAVANNA_PLATEAU, Biomes.ERODED_BADLANDS,
			Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU, Biomes.MODIFIED_BADLANDS_PLATEAU);
	public static final Biome[] biomesArray = biomes.toArray(new Biome[biomes.size()]);
	private final Random rnd = new Random();

	protected BiosphereBiomeProvider(World worldIn, BiosphereBiomeProviderSettings settingsProvider) {
		super(BiosphereBiomeProvider.biomes);
		this.world = worldIn;
	}

	@Override
	public Biome getNoiseBiome(int x, int y, int z) {
		// We get passed a biome coordinate, which is bitshifted to create our
		// approximate block coordinate
		x = (x << 2);
		z = (z << 2);
		BlockPos pos = new BlockPos(x, y, z);
		ChunkPos chunkPos = new ChunkPos(pos);
		SphereChunk sphereChunk = SphereChunk.get(this.world, chunkPos);
		Sphere sphere = sphereChunk.closestSphere;
		return sphere.biome;
	}
}
