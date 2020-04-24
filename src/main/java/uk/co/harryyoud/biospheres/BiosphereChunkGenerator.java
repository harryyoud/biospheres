package uk.co.harryyoud.biospheres;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BushBlock;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.INoiseGenerator;
import net.minecraft.world.gen.NoiseChunkGenerator;
import net.minecraft.world.gen.OverworldChunkGenerator;
import net.minecraft.world.gen.OverworldGenSettings;
import net.minecraft.world.gen.WorldGenRegion;

public class BiosphereChunkGenerator<C extends GenerationSettings> extends OverworldChunkGenerator {
	private final BlockState DOME_BLOCK = Blocks.WHITE_STAINED_GLASS.getDefaultState();
	private final BlockState AIR = Blocks.AIR.getDefaultState();
	private final BlockState OUTSIDE_FILLER_BLOCK = Blocks.AIR.getDefaultState();
	private final BlockState INSIDE_FILLER_BLOCK = Blocks.STONE.getDefaultState();
	private final BlockState BRIDGE_BLOCK = Blocks.OAK_PLANKS.getDefaultState();
	private final BlockState FENCE_BLOCK = Blocks.OAK_FENCE.getDefaultState();
	private final BlockState LAKE_FLUID_BLOCK = Blocks.WATER.getDefaultState();
	private static final ArrayList<Block> BANNED_BLOCKS = new ArrayList<Block>(
			Arrays.asList(Blocks.AIR, Blocks.WATER, Blocks.LAVA));
	private final ArrayList<Block> ALLOWED_BLOCKS = new ArrayList<Block>(Arrays.asList(OUTSIDE_FILLER_BLOCK.getBlock(),
			AIR.getBlock(), BRIDGE_BLOCK.getBlock(), FENCE_BLOCK.getBlock(), DOME_BLOCK.getBlock()));
	public final int BRIDGE_WIDTH = 2;
	public final int BRIDGE_HEIGHT = 4;
	private final INoiseGenerator surfaceDepthNoise2;

	private static final int noiseSizeX = 4;
	private static final int noiseSizeY = 32;
	private static final int noiseSizeZ = 4;
	private static final int verticalNoiseGranularity = 8;
	private static final int horizontalNoiseGranularity = 4;

	@SuppressWarnings("serial")
	private final Map<ChunkPos, double[][][]> depthNoiseCache = new LinkedHashMap<ChunkPos, double[][][]>(500, 0.7f,
			true) {
		@Override
		protected boolean removeEldestEntry(Map.Entry<ChunkPos, double[][][]> eldest) {
			return size() > 500;
		}
	};

	public BiosphereChunkGenerator(IWorld worldIn, BiomeProvider biomeProviderIn,
			OverworldGenSettings generationSettingsIn) {
		super(worldIn, biomeProviderIn, generationSettingsIn);
		try {
			Class clz = NoiseChunkGenerator.class;
			Field f = clz.getDeclaredField("surfaceDepthNoise");
			f.setAccessible(true);
			this.surfaceDepthNoise2 = (INoiseGenerator) f.get(this);
		} catch (Exception e) {
			throw new Error();
		}
	}

	@Override
	// build surface;
	public void func_225551_a_(WorldGenRegion region, IChunk chunkIn) {
		Sphere sphere = Sphere.getClosest(region, chunkIn.getPos().asBlockPos());

		for (BlockPos pos : new BlockPosIterator(chunkIn.getPos())) {
			double sphereDistance = sphere.getDistanceToCenter(pos);
			if (pos.getY() == sphere.getCentre().getY() && sphereDistance <= sphere.radius) {
				// Only run surface builder for each x, z once (one y value)
				Biome biome = region.getBiome(pos);
				biome.buildSurface(sphere.getRandom(), chunkIn, pos.getX(), pos.getZ(),
						chunkIn.getTopBlockY(Heightmap.Type.WORLD_SURFACE_WG, pos.getX(), pos.getZ()),
						this.getSurfaceDepthNoise(pos), INSIDE_FILLER_BLOCK, LAKE_FLUID_BLOCK, this.getSeaLevel(),
						region.getSeed());
			}
		}
	}

	@Override
	public void decorate(WorldGenRegion region) {
		super.decorate(region);

		IChunk chunkIn = region.getChunk(region.getMainChunkX(), region.getMainChunkZ());
		Sphere sphere = Sphere.getClosest(region, chunkIn.getPos().asBlockPos());
		for (BlockPos pos : new BlockPosIterator(chunkIn.getPos())) {
			double sphereDistance = sphere.getDistanceToCenter(pos);
			BlockState prevState = region.getBlockState(pos);
			BlockState state = null;

			for (int j = 0; j < 4; j++) {
				sphere.computeBridgeJoin(this, Direction.byHorizontalIndex(j));
			}

			if (sphereDistance == sphere.radius) {
				if (BANNED_BLOCKS.contains(prevState.getBlock()) || prevState.isAir(region, pos)
						|| prevState.isFoliage(region, pos) || prevState.getBlock() instanceof BushBlock
						|| prevState.getBlock().isIn(BlockTags.LEAVES)
						|| (chunkIn.getTopBlockY(Heightmap.Type.WORLD_SURFACE_WG, pos.getX(), pos.getZ()) == pos.getY()
								- 1) && !ALLOWED_BLOCKS.contains(region.getBlockState(pos).getBlock())) {
					state = DOME_BLOCK;
				}
				for (MutableBoundingBox box : sphere.getCutouts()) {
					if (box.isVecInside(pos) && state != null && state.getBlock() == DOME_BLOCK.getBlock()) {
						state = AIR;
						break;
					}
				}
			}
			if (sphereDistance > sphere.radius) {
				// Don't remove blocks that have light values > 0, since the light engine gets
				// annoyed
				if (!ALLOWED_BLOCKS.contains(prevState.getBlock()) && !region.isAirBlock(pos)
						&& prevState.getLightValue(region, pos) == 0) {
					state = AIR; // Clean up anything outside the spheres that isn't meant to be there
				}
			}
			if (state != null) {
				if (state == AIR) {
					region.removeBlock(pos, false);
				} else {
					region.setBlockState(pos, state, 3);
				}
			}
		}
	}

	@Override
	public void makeBase(IWorld worldIn, IChunk chunkIn) {
		double[][][] noises = this.getNoiseForChunk(worldIn, chunkIn.getPos());
		Sphere sphere = Sphere.getClosest(worldIn, chunkIn.getPos().asBlockPos());

		for (BlockPos pos : new BlockPosIterator(chunkIn.getPos())) {
			BlockState state = this.getBlock(worldIn, chunkIn, pos,
					noises[Math.abs(pos.getX()) % 16][pos.getY()][Math.abs(pos.getZ()) % 16]);
			chunkIn.setBlockState(pos, state, false);

			double sphereDistance = sphere.getDistanceToCenter(pos);
			if (sphereDistance >= sphere.radius) {
				Direction dir = null;

				if (Utils.inIncRange(pos.getX(), sphere.getCentre().getX() + BRIDGE_WIDTH,
						sphere.getCentre().getX() - BRIDGE_WIDTH)) {
					if (pos.getZ() < sphere.getCentre().getZ()) {
						dir = Direction.NORTH;
					} else if (pos.getZ() > sphere.getCentre().getZ()) {
						dir = Direction.SOUTH;
					}
				}

				if (Utils.inIncRange(pos.getZ(), sphere.getCentre().getZ() + BRIDGE_WIDTH,
						sphere.getCentre().getZ() - BRIDGE_WIDTH)) {
					if (pos.getX() < sphere.getCentre().getX()) {
						dir = Direction.WEST;
					} else if (pos.getX() > sphere.getCentre().getX()) {
						dir = Direction.EAST;
					}
				}

				if (dir == null) {
					continue;
				}

				Direction.Axis axis = dir.getAxis();
				Direction.Axis otherAxis = dir.rotateY().getAxis();
				Sphere nextClosestSphere = Sphere.getClosest(worldIn,
						Utils.moveChunk(sphere.getCentreChunk(), dir, Sphere.gridSize).asBlockPos());
				BlockPos aimFor = nextClosestSphere.computeBridgeJoin(this, dir.getOpposite());
				BlockPos aimFrom = sphere.computeBridgeJoin(this, dir);

				int diffDenom = Utils.getCoord(aimFrom, axis) - Utils.getCoord(aimFor, axis);
				if (diffDenom == 0) {
					diffDenom = 1;
				}

				// m = Δy/Δx
				double gradient = ((double) (aimFrom.getY() - aimFor.getY())) / (diffDenom);
				// y = mx + c, where x is the distance from "0" (aimFrom)
				double newY = Math.round(
						(gradient * (Utils.getCoord(pos, axis) - Utils.getCoord(aimFrom, axis))) + aimFrom.getY());

				if (Utils.inIncRange(Utils.getCoord(pos, axis), Utils.getCoord(aimFrom, axis),
						Utils.getCoord(aimFor, axis))) {
					if (pos.getY() == newY) {
						chunkIn.setBlockState(pos, BRIDGE_BLOCK, false);
					}
					if (pos.getY() == newY + 1 && (Utils.getCoord(pos,
							otherAxis) == Utils.getCoord(sphere.getCentre(), otherAxis) + BRIDGE_WIDTH
							|| Utils.getCoord(pos, otherAxis) == Utils.getCoord(sphere.getCentre(), otherAxis)
									- BRIDGE_WIDTH)) {
						chunkIn.setBlockState(pos, FENCE_BLOCK, false);
					}
				}
			}
		}
	}

	// Fetch noises from the cache or calculate if not there
	double[][][] getNoiseForChunk(IWorld worldIn, ChunkPos chunkPos) {
		if (this.depthNoiseCache.containsKey(chunkPos)) {
			return depthNoiseCache.get(chunkPos);
		}
		double[][][] depthNoise = this.calcNoiseForChunk(worldIn, chunkPos);
		depthNoiseCache.put(chunkPos, depthNoise);
		return depthNoise;
	}

	// This is essentially lifted from OverworldChunkGenerator, but modified to
	// return a 3d matrix of noise values so we can cache them for repeated use
	private double[][][] calcNoiseForChunk(IWorld worldIn, ChunkPos chunkPos) {
		int chunkXPos = chunkPos.x;
		int chunkXStart = chunkPos.getXStart();
		int chunkZPos = chunkPos.z;
		int chunkZStart = chunkPos.getZStart();
		double[][][] noises = new double[2][noiseSizeZ + 1][noiseSizeY + 1];
		double[][][] finalNoises = new double[16][256][16];

		for (int zNoiseOffset = 0; zNoiseOffset < noiseSizeZ + 1; ++zNoiseOffset) {
			noises[0][zNoiseOffset] = new double[noiseSizeY + 1];
			this.fillNoiseColumn(noises[0][zNoiseOffset], chunkXPos * noiseSizeX,
					chunkZPos * noiseSizeZ + zNoiseOffset);
			noises[1][zNoiseOffset] = new double[noiseSizeY + 1];
		}

		for (int xNoiseOffset = 0; xNoiseOffset < noiseSizeX; ++xNoiseOffset) {
			for (int zNoiseOffset = 0; zNoiseOffset < noiseSizeZ + 1; ++zNoiseOffset) {
				this.fillNoiseColumn(noises[1][zNoiseOffset], chunkXPos * noiseSizeX + xNoiseOffset + 1,
						chunkZPos * noiseSizeZ + zNoiseOffset);
			}

			for (int zNoiseOffset = 0; zNoiseOffset < noiseSizeZ; ++zNoiseOffset) {

				for (int yNoiseOffset = noiseSizeY - 1; yNoiseOffset >= 0; --yNoiseOffset) {
					double noise000 = noises[0][zNoiseOffset][yNoiseOffset];
					double noise010 = noises[0][zNoiseOffset + 1][yNoiseOffset];
					double noise100 = noises[1][zNoiseOffset][yNoiseOffset];
					double noise110 = noises[1][zNoiseOffset + 1][yNoiseOffset];
					double noise001 = noises[0][zNoiseOffset][yNoiseOffset + 1];
					double noise011 = noises[0][zNoiseOffset + 1][yNoiseOffset + 1];
					double noise101 = noises[1][zNoiseOffset][yNoiseOffset + 1];
					double noise111 = noises[1][zNoiseOffset + 1][yNoiseOffset + 1];

					for (int yGranularityOffset = verticalNoiseGranularity
							- 1; yGranularityOffset >= 0; --yGranularityOffset) {
						int worldY = yNoiseOffset * verticalNoiseGranularity + yGranularityOffset;

						double yNoiseScale = (double) yGranularityOffset / (double) verticalNoiseGranularity;
						double d6 = MathHelper.lerp(yNoiseScale, noise000, noise001);
						double d7 = MathHelper.lerp(yNoiseScale, noise100, noise101);
						double d8 = MathHelper.lerp(yNoiseScale, noise010, noise011);
						double d9 = MathHelper.lerp(yNoiseScale, noise110, noise111);

						for (int XGranularityOffset = 0; XGranularityOffset < horizontalNoiseGranularity; ++XGranularityOffset) {
							int worldX = chunkXStart + xNoiseOffset * horizontalNoiseGranularity + XGranularityOffset;
							double xNoiseScale = (double) XGranularityOffset / (double) horizontalNoiseGranularity;
							double d11 = MathHelper.lerp(xNoiseScale, d6, d7);
							double d12 = MathHelper.lerp(xNoiseScale, d8, d9);

							for (int zGranularityOffset = 0; zGranularityOffset < horizontalNoiseGranularity; ++zGranularityOffset) {
								int worldZ = chunkZStart + zNoiseOffset * horizontalNoiseGranularity
										+ zGranularityOffset;
								double zNoiseScale = (double) zGranularityOffset / (double) horizontalNoiseGranularity;
								double finalNoise = MathHelper.lerp(zNoiseScale, d11, d12);
								double finalNoiseClamped = MathHelper.clamp(finalNoise / 200.0D, -1.0D, 1.0D);
								finalNoises[Math.abs(worldX) % 16][worldY][Math.abs(worldZ) % 16] = finalNoiseClamped;
							}
						}
					}
				}
			}
			double[][] temp = noises[0];
			noises[0] = noises[1];
			noises[1] = temp;
		}
		return finalNoises;
	}

	private BlockState getBlock(IWorld worldIn, IChunk chunkIn, BlockPos pos, double finalNoiseClamped) {
		Sphere sphere = Sphere.getClosest(worldIn, chunkIn.getPos().asBlockPos());
		double sphereDistance = sphere.getDistanceToCenter(pos);
		if (finalNoiseClamped > 0.0D) {
			if (sphereDistance <= sphere.radius) {
				return INSIDE_FILLER_BLOCK;
			}
		}
		if (pos.getY() < this.getSeaLevel() && sphereDistance < sphere.radius) {
			return LAKE_FLUID_BLOCK;
		}
		if (sphereDistance > sphere.radius) {
			return OUTSIDE_FILLER_BLOCK;
		}
		return AIR;
	}

	public double getSurfaceDepthNoise(BlockPos pos) {
		double scale = 0.0625D;
		return this.surfaceDepthNoise2.noiseAt(pos.getX() * scale, pos.getZ() * scale, scale, pos.getY() * scale)
				* 15.0D;
	}

	@Override
	// carve
	public void func_225550_a_(BiomeManager biomeManager, IChunk chunkIn, GenerationStage.Carving genStage) {
		// I can't influence where caves are going to be on a block by block basis, so
		// just ignore block changes that fall outside of the spheres
		Sphere sphere = Sphere.getClosest(this.world, chunkIn.getPos().asBlockPos());
		IChunk newChunk = new CustomChunkPrimer(chunkIn, (pos) -> sphere.getDistanceToCenter(pos) > sphere.radius);
		super.func_225550_a_(biomeManager, newChunk, genStage);
	}
}