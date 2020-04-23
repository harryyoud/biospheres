package uk.co.harryyoud.biospheres;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.OverworldBiomeProviderSettings;
import net.minecraftforge.registries.ForgeRegistries;

public class BiosphereBiomeProvider extends BiomeProvider {

	public final IWorld world;
	public static final Set<Biome> biomes;
	public static final Biome[] biomesArray;

	static {
		biomes = new HashSet<Biome>(ForgeRegistries.BIOMES.getValues());
		biomesArray = biomes.toArray(new Biome[biomes.size()]);
	}

	protected BiosphereBiomeProvider(IWorld worldIn, OverworldBiomeProviderSettings settingsProvider) {
		super(biomes);
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
		return sphere.getBiome();
	}
}
