package uk.co.harryyoud.biospheres;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.WorldGenRegion;

public class BiosphereChunkGenerator<C extends GenerationSettings> extends ChunkGenerator<C> {

	private static final BlockState DOME_BLOCK = Blocks.GLASS.getDefaultState();
	private static final BlockState OUTSIDE_FILLER_BLOCK = Blocks.AIR.getDefaultState();
	private static final BlockState INSIDE_FILLER_BLOCK = Blocks.STONE.getDefaultState();
	private static final BlockState BRIDGE_BLOCK = Blocks.OAK_PLANKS.getDefaultState();
	private static final BlockState FALLBACK_BLOCK = Blocks.AIR.getDefaultState();
	private static final BlockState LAKE_FLUID_BLOCK = Blocks.WATER.getDefaultState();
	private static final int BRIDGE_WIDTH = 2;
	private static final int BRIDGE_HEIGHT = 6;

	public BiosphereChunkGenerator(IWorld worldIn, BiomeProvider biomeProviderIn, C generationSettingsIn) {
		super(worldIn, biomeProviderIn, generationSettingsIn);
	}

	// carve
	@Override
	public void func_225551_a_(WorldGenRegion region, IChunk chunkIn) {
		SphereChunk sphereChunk = SphereChunk.get(region, chunkIn.getPos());
		Sphere sphere = sphereChunk.closestSphere;

		for (BlockPos pos : new BlockPosIterator(chunkIn)) {
			if (pos.getY() == sphere.centre.getY() && sphere.getDistanceToCenter(pos) <= sphere.radius) {
				// Only run surface builder for each x, z once
				sphere.biome.buildSurface(sphere.getRandom(), chunkIn, pos.getX(), pos.getZ(), this.getGroundHeight(),
						sphere.getRandom().nextDouble() * 5, INSIDE_FILLER_BLOCK, LAKE_FLUID_BLOCK, this.getSeaLevel(),
						region.getSeed());
			}
		}
	}

	@Override
	public int getSeaLevel() {
		return 63;
	}

	@Override
	public int getGroundHeight() {
		return this.world.getSeaLevel() + 1;
	}

	@Override
	public void makeBase(IWorld worldIn, IChunk chunkIn) {
		SphereChunk sphereChunk = SphereChunk.get(worldIn, chunkIn.getPos());

		for (BlockPos pos : new BlockPosIterator(chunkIn)) {
			BlockState blockState = getBlockStateAt(sphereChunk, pos);
			chunkIn.setBlockState(pos, blockState, false);
		}
	}

	private BlockState getBlockStateAt(SphereChunk sphereChunk, BlockPos pos) {
		double sphereDistance = sphereChunk.closestSphere.getDistanceToCenter(pos);
		if (sphereDistance == sphereChunk.closestSphere.radius) {
			return this.getShellBlockStateAt(sphereChunk, pos);
		}
		if (sphereDistance > sphereChunk.closestSphere.radius) {
			return this.getOutsideBlockStateAt(sphereChunk, pos);
		}
		if (sphereDistance < sphereChunk.closestSphere.radius) {
			if (pos.getY() < sphereChunk.closestSphere.seaLevel) {
				return INSIDE_FILLER_BLOCK;
			}
		}
		return FALLBACK_BLOCK;
	}

	private BlockState getOutsideBlockStateAt(SphereChunk sphereChunk, BlockPos pos) {
		if (pos.getY() == sphereChunk.closestSphere.seaLevel - 1
				&& (Math.abs(pos.getX() - sphereChunk.closestSphere.centre.getX()) < BRIDGE_WIDTH + 1
						|| Math.abs(pos.getZ() - sphereChunk.closestSphere.centre.getZ()) < BRIDGE_WIDTH + 1))

		{
			return BRIDGE_BLOCK;
		}
		return OUTSIDE_FILLER_BLOCK;
	}

	private BlockState getShellBlockStateAt(SphereChunk sphereChunk, BlockPos pos) {
		if (pos.getY() < sphereChunk.closestSphere.seaLevel) { // Below the vertical centre
			return INSIDE_FILLER_BLOCK;
		}
		if (pos.getY() >= sphereChunk.closestSphere.seaLevel + BRIDGE_HEIGHT
				|| Math.abs(pos.getX() - sphereChunk.closestSphere.centre.getX()) > BRIDGE_WIDTH
						&& Math.abs(pos.getZ() - sphereChunk.closestSphere.centre.getZ()) > BRIDGE_WIDTH) {
			return DOME_BLOCK;
		}
		return FALLBACK_BLOCK;
	}

	// generateNoiseRegion
	@Override
	public int func_222529_a(int p_222529_1_, int p_222529_2_, Heightmap.Type heightmapType) {
		// TODO Auto-generated method stub
		return 0;
	}
}
