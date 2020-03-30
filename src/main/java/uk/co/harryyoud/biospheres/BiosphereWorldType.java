package uk.co.harryyoud.biospheres;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.gen.ChunkGenerator;

public class BiosphereWorldType extends WorldType {

	public BiosphereWorldType() {
		super("biospheres");
	}

	@Override
	public ChunkGenerator<?> createChunkGenerator(World world) {
		BiosphereBiomeProviderSettings bbps = new BiosphereBiomeProviderSettings(world.getWorldInfo());
		return new BiosphereChunkGenerator<BiosphereBiomeProviderSettings>(world,
				new BiosphereBiomeProvider(world, bbps), bbps);
	}
}
