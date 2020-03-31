package uk.co.harryyoud.biospheres;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;

public class Sphere {
	final BlockPos centre;
	private final ChunkPos centreChunk;
	private final Random rnd;
	public final Biome biome;
	public static final int gridSize = 9;
	public static final int radius = 40;
	public static final int seaLevel = 64;

	@SuppressWarnings("serial")
	public static Map<BlockPos, Sphere> sphereCache = new LinkedHashMap<BlockPos, Sphere>(200, 0.7f, true) {
		@Override
		protected boolean removeEldestEntry(Map.Entry<BlockPos, Sphere> eldest) {
			return size() > 200;
		}
	};

	public Sphere(IWorld world, BlockPos centrePos) {
		this.centre = centrePos;
		this.centreChunk = new ChunkPos(centrePos);
		this.rnd = new Random(centrePos.hashCode() * world.getSeed());
		this.biome = this.getRandomBiome(this.rnd);
	}

	public static Sphere get(IWorld worldIn, BlockPos centrePos) {
		if (sphereCache.containsKey(centrePos)) {
			return sphereCache.get(centrePos);
		}
		Sphere sphere = new Sphere(worldIn, centrePos);
		sphereCache.put(centrePos, sphere);
		return sphere;
	}

	public static Sphere get(IWorld worldIn, ChunkPos chunkPos) {
		BlockPos pos = chunkPos.getBlock(8, Sphere.seaLevel, 8);
		return Sphere.get(worldIn, pos);
	}

	private Biome getRandomBiome(Random rnd) {
		Biome biome = BiosphereBiomeProvider.biomesArray[(rnd.nextInt(BiosphereBiomeProvider.biomesArray.length))];
//		System.out.println("Sphere(" + this.centre.toString() + ") = " + biome.toString());
		return biome;
	}

	private BlockPos getSphereCenter() {
		int chunkOffsetX = -(int) Math.floor(Math.IEEEremainder(this.centreChunk.x, Sphere.gridSize));
		int chunkOffsetZ = -(int) Math.floor(Math.IEEEremainder(this.centreChunk.z, Sphere.gridSize));
		ChunkPos centerChunk = new ChunkPos(this.centreChunk.x + chunkOffsetX, this.centreChunk.z + chunkOffsetZ);
		return centerChunk.getBlock(8, Sphere.seaLevel, 8);
	}

	public int getDistanceToCenter(BlockPos pos) {
		double sq = Math.pow((pos.getX() - this.centre.getX()), 2.0D)
				+ Math.pow((pos.getY() - this.centre.getY()), 2.0D) + Math.pow((pos.getZ() - this.centre.getZ()), 2.0D);
		return (int) Math.floor(Math.sqrt(sq));
	}

	public Random getRandom() {
		return this.rnd;
	}

}
