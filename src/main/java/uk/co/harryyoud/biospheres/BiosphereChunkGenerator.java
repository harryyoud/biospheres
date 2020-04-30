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
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.INoiseGenerator;
import net.minecraft.world.gen.NoiseChunkGenerator;
import net.minecraft.world.gen.OverworldChunkGenerator;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraftforge.registries.ForgeRegistries;
import uk.co.harryyoud.biospheres.config.BiosphereGenSettingsSerializer.BiosphereGenSettings;
import uk.co.harryyoud.biospheres.wrappers.IChunkWrapper;
import uk.co.harryyoud.biospheres.wrappers.IWorldWrapper;

public class BiosphereChunkGenerator extends OverworldChunkGenerator {
	private final BlockState DOME_BLOCK;
	private final BlockState AIR = Blocks.AIR.getDefaultState();
	private final BlockState OUTSIDE_FILLER_BLOCK;
	private final BlockState BRIDGE_BLOCK;
	private final BlockState FENCE_BLOCK = Blocks.OAK_FENCE.getDefaultState();
	private static final ArrayList<Block> BANNED_BLOCKS = new ArrayList<Block>(
			Arrays.asList(Blocks.AIR, Blocks.WATER, Blocks.LAVA));
	private final ArrayList<Block> ALLOWED_BLOCKS = new ArrayList<Block>(
			Arrays.asList(AIR.getBlock(), FENCE_BLOCK.getBlock()));
	public final int BRIDGE_WIDTH = 2;
	public final int BRIDGE_HEIGHT = 4;
	private final INoiseGenerator surfaceDepthNoise2;
	private final BiosphereGenSettings genSettings;

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

	public BiosphereChunkGenerator(IWorld worldIn, BiomeProvider biomeProviderIn, BiosphereGenSettings genSettings) {
		super(worldIn, biomeProviderIn, genSettings);

		this.genSettings = genSettings;

		this.DOME_BLOCK = this.genSettings.domeBlock;
		this.BRIDGE_BLOCK = this.genSettings.bridgeBlock;
		this.OUTSIDE_FILLER_BLOCK = this.genSettings.outsideFillerBlock;
		this.ALLOWED_BLOCKS.add(DOME_BLOCK.getBlock());
		this.ALLOWED_BLOCKS.add(BRIDGE_BLOCK.getBlock());
		this.ALLOWED_BLOCKS.add(OUTSIDE_FILLER_BLOCK.getBlock());

		Sphere.minRadius = this.genSettings.sphereMinRadius;
		Sphere.maxRadius = this.genSettings.sphereMaxRadius;
		Sphere.midY = this.genSettings.sphereMidY;

		try {
			@SuppressWarnings("rawtypes")
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
						this.getSurfaceDepthNoise(pos), this.genSettings.getDefaultBlock(),
						this.genSettings.getDefaultFluid(), this.getSeaLevel(), region.getSeed());
			}
		}
	}

	@Override
	public void decorate(WorldGenRegion region) {
		IWorld worldWrapper = new IWorldWrapper(region, this.getSeaLevel());

		// COPY PASTED FROM PARENT, with region replaced with worldWrapper
		int i = region.getMainChunkX();
		int j = region.getMainChunkZ();
		int k = i * 16;
		int l = j * 16;
		BlockPos blockpos = new BlockPos(k, 0, l);
		Biome biome = this.getBiome(region.getBiomeManager(), blockpos.add(8, 8, 8));
		SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
		long i1 = sharedseedrandom.setDecorationSeed(region.getSeed(), k, l);

		for (GenerationStage.Decoration generationstage$decoration : GenerationStage.Decoration.values()) {
			try {
				biome.decorate(generationstage$decoration, this, worldWrapper, i1, sharedseedrandom, blockpos);
			} catch (Exception exception) {
				CrashReport crashreport = CrashReport.makeCrashReport(exception, "Biome decoration");
				crashreport.makeCategory("Generation").addDetail("CenterX", i).addDetail("CenterZ", j)
						.addDetail("Step", generationstage$decoration).addDetail("Seed", i1)
						.addDetail("Biome", ForgeRegistries.BIOMES.getKey(biome));
				throw new ReportedException(crashreport);
			}
		}
		// END COPY-PASTE

		IChunk chunkIn = region.getChunk(region.getMainChunkX(), region.getMainChunkZ());
		Sphere sphere = Sphere.getClosest(region, chunkIn.getPos().asBlockPos());
		for (BlockPos pos : new BlockPosIterator(chunkIn.getPos())) {
			double sphereDistance = sphere.getDistanceToCenter(pos);
			BlockState prevState = region.getBlockState(pos);
			BlockState state = null;

			for (int d = 0; d < 4; d++) {
				sphere.computeBridgeJoin(this, Direction.byHorizontalIndex(d));
			}

			if (sphereDistance == sphere.radius) {
				if (BANNED_BLOCKS.contains(prevState.getBlock()) || prevState.isAir(region, pos)
						|| prevState.isFoliage(region, pos) || prevState.getBlock() instanceof BushBlock
						|| prevState.getBlock().isIn(BlockTags.LEAVES) || !prevState.isSolid()
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

	@SuppressWarnings("deprecation")
	@Override
	public void makeBase(IWorld worldIn, IChunk chunkIn) {
		double[][][] noises = this.getNoiseForChunk(worldIn, chunkIn.getPos());
		Sphere sphere = Sphere.getClosest(worldIn, chunkIn.getPos().asBlockPos());

		for (BlockPos pos : new BlockPosIterator(chunkIn.getPos())) {
			BlockState state = null;
			int sphereDistance = sphere.getDistanceToCenter(pos);
			if (pos.getY() < this.getSeaLevel() && sphereDistance < sphere.radius) {
				state = this.genSettings.getDefaultFluid();
			}
			if (noises[Math.abs(pos.getX()) % 16][this.correctYValue(pos.getY())][Math.abs(pos.getZ()) % 16] > 0.0D) {
				if (sphereDistance <= sphere.radius) {
					state = this.genSettings.getDefaultBlock();
				}
			}
			if (state != null && !state.isAir()) {
				chunkIn.setBlockState(pos, state, false);
			}

			if (sphereDistance > sphere.radius) {
				chunkIn.setBlockState(pos, OUTSIDE_FILLER_BLOCK, false);
			}

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

	public double getSurfaceDepthNoise(BlockPos pos) {
		double scale = 0.0625D;
		return this.surfaceDepthNoise2.noiseAt(pos.getX() * scale, pos.getZ() * scale, scale, pos.getY() * scale)
				* 15.0D;
	}

	@Override
	public int getGroundHeight() {
		return this.getSeaLevel() + 1;
	}

	@Override
	public int getSeaLevel() {
		return this.genSettings.sphereMidY;
	}

	public int correctYValue(int yIn) {
		// The overworld noise generator uses loads of magic numbers, which assume the
		// sea level is 63, so if we want to raise the sea level, then we need to use
		// the noise values from a different height in order to raise the ground level
		// up from the sea
		int seaLevelDiff = this.getSeaLevel() - 63;
		int newY = yIn - seaLevelDiff;
		return Math.max(0, newY);
	}

	@Override
	// carve
	public void func_225550_a_(BiomeManager biomeManager, IChunk chunkIn, GenerationStage.Carving genStage) {
		// I can't influence where caves are going to be on a block by block basis, so
		// just ignore block changes that fall outside of the spheres
		Sphere sphere = Sphere.getClosest(this.world, chunkIn.getPos().asBlockPos());
		IChunk newChunk = new IChunkWrapper(chunkIn, (pos) -> sphere.getDistanceToCenter(pos) > sphere.radius);
		super.func_225550_a_(biomeManager, newChunk, genStage);
	}
}