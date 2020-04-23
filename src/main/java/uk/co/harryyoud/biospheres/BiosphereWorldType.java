package uk.co.harryyoud.biospheres;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.provider.OverworldBiomeProvider;
import net.minecraft.world.biome.provider.OverworldBiomeProviderSettings;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.OverworldGenSettings;

public class BiosphereWorldType extends WorldType {

	public BiosphereWorldType() {
		super("biospheres");
	}

	@Override
	public ChunkGenerator<?> createChunkGenerator(World world) {
		OverworldBiomeProviderSettings biomeProvSettings = new OverworldBiomeProviderSettings(world.getWorldInfo());
		BiosphereBiomeProvider biomeProv = new BiosphereBiomeProvider(world, biomeProvSettings);
		OverworldGenSettings settings = new OverworldGenSettings();

		return new BiosphereChunkGenerator<OverworldGenSettings>(world, biomeProv, settings);
	}
}
