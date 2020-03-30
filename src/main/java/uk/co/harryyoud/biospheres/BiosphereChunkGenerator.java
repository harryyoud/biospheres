package uk.co.harryyoud.biospheres;

import net.minecraft.block.BlockState;
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

	private static int BRIDGE_WIDTH = 2;
	private static int BRIDGE_HEIGHT = 6;

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
		SphereChunk sphereChunk = SphereChunk.get((World) world, chunkIn.getPos());

		for (BlockPos pos : new BlockPosIterator(chunkIn)) {
			BlockState blockState = getBlockStateFor(sphereChunk, pos);
			chunkIn.setBlockState(pos, blockState, false);
		}
	}

	private BlockState getBlockStateFor(SphereChunk sphereChunk, BlockPos pos) {
		BlockState blockState = Blocks.AIR.getDefaultState();
		double sphereDistance = sphereChunk.getDistance(pos);
		if (pos.getY() > sphereChunk.seaLevel) {
			// Create the glass half of the sphere and the walkway cut outs
			if (sphereDistance == sphereChunk.radius) {
				if (pos.getY() >= sphereChunk.seaLevel + BRIDGE_HEIGHT
						|| Math.abs(pos.getX() - sphereChunk.sphereCenter.getX()) > BRIDGE_WIDTH
								&& Math.abs(pos.getZ() - sphereChunk.sphereCenter.getZ()) > BRIDGE_WIDTH) {
					blockState = Blocks.GLASS.getDefaultState();
				}
			}
		} else if (sphereDistance == sphereChunk.radius) {
			blockState = Blocks.STONE.getDefaultState();
		} else if (pos.getY() == sphereChunk.seaLevel && sphereDistance > sphereChunk.radius
				&& (Math.abs(pos.getX() - sphereChunk.sphereCenter.getX()) < BRIDGE_WIDTH + 1
						|| Math.abs(pos.getZ() - sphereChunk.sphereCenter.getZ()) < BRIDGE_WIDTH + 1)) {
			blockState = Blocks.OAK_PLANKS.getDefaultState();
		}
		return blockState;
	}

	// generateNoiseRegion
	@Override
	public int func_222529_a(int p_222529_1_, int p_222529_2_, Type heightmapType) {
		// TODO Auto-generated method stub
		return 0;
	}
}
