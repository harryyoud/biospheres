package uk.co.harryyoud.biospheres;

import com.mojang.datafixers.Dynamic;

import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.biome.provider.SingleBiomeProvider;
import net.minecraft.world.biome.provider.SingleBiomeProviderSettings;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.FlatGenerationSettings;

public class BiosphereWorldType extends WorldType {

	public BiosphereWorldType() {
		super("biospheres");
	}

	@Override
	public ChunkGenerator<?> createChunkGenerator(World world) {
		BiomeProviderType<SingleBiomeProviderSettings, SingleBiomeProvider> biomeprovidertype = BiomeProviderType.FIXED;
		FlatGenerationSettings flatgenerationsettings = FlatGenerationSettings
				.createFlatGenerator(new Dynamic<>(NBTDynamicOps.INSTANCE, world.getWorldInfo().getGeneratorOptions()));
		SingleBiomeProviderSettings singlebiomeprovidersettings1 = biomeprovidertype
				.func_226840_a_(world.getWorldInfo()).setBiome(flatgenerationsettings.getBiome());
		return new BiosphereChunkGenerator<FlatGenerationSettings>(world,
				biomeprovidertype.create(singlebiomeprovidersettings1), flatgenerationsettings);
	}
}
