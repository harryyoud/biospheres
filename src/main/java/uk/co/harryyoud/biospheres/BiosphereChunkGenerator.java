package uk.co.harryyoud.biospheres;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.gen.WorldGenRegion;

public class BiosphereChunkGenerator<C extends GenerationSettings> extends ChunkGenerator<C> {

	public BiosphereChunkGenerator(IWorld worldIn, BiomeProvider biomeProviderIn, C generationSettingsIn) {
		super(worldIn, biomeProviderIn, generationSettingsIn);
	}

	// carve
	@Override
	public void func_225551_a_(WorldGenRegion p_225551_1_, IChunk p_225551_2_) {
		// TODO Auto-generated method stub
	}

	@Override
	public int getGroundHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void makeBase(IWorld worldIn, IChunk chunkIn) {
		int midY = 64;
		int worldMaxY = 256;
		SphereChunk chunk = SphereChunk.get((World)world, chunkIn.getPos());
		int rawX = chunkIn.getPos().x << 4;
		int rawZ = chunkIn.getPos().z << 4;
		int BRIDGE_SIZE = 2;
		
		for (int zo = 0; zo < 16; ++zo) {
			for (int xo = 0; xo < 16; ++xo) {
				for (int rawY = worldMaxY; rawY >= 0; --rawY) {
					Block block = Blocks.AIR;
					double sphereDistance = chunk.getDistance(new BlockPos(rawX + xo, rawY, rawZ + zo));
					if (rawY > midY) {
						if (sphereDistance == chunk.RADIUS) {
							if (rawY >= midY + 4 || Math.abs(rawX + xo - chunk.sphereCenter.getX()) > BRIDGE_SIZE
									&& Math.abs(rawZ + zo - chunk.sphereCenter.getZ()) > BRIDGE_SIZE) {
								block = Blocks.GLASS;
							}
						}
					} else if (rawY == midY
							&& sphereDistance > chunk.RADIUS
							&& (Math.abs(rawX + xo - chunk.sphereCenter.getX()) < BRIDGE_SIZE + 1 || Math.abs(rawZ + zo
									- chunk.sphereCenter.getZ()) < BRIDGE_SIZE + 1)) {
						block = Blocks.OAK_PLANKS;
					}
					chunkIn.setBlockState(new BlockPos(rawX + xo, rawY, rawZ + zo), block.getDefaultState(), false);
				}
			}
		}
	}
	
	// generateNoiseRegion
	@Override
	public int func_222529_a(int p_222529_1_, int p_222529_2_, Type heightmapType) {
		// TODO Auto-generated method stub
		return 0;
	}
}
