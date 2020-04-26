package uk.co.harryyoud.biospheres;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;

public class Sphere {
	private final IWorld world;
	private final BlockPos centre;
	private final ChunkPos centreChunk;
	private final Random rnd;
	private final Biome biome;
	private final int minRadius = 40;
	private final int maxRadius = 100;
	public final int radius;
	public static final int gridSize = 15;
	public static final int midY = 100;
	private HashMap<Direction, BlockPos> bridgeJoin = new HashMap<Direction, BlockPos>();
	private ArrayList<MutableBoundingBox> domeCutouts = new ArrayList<MutableBoundingBox>();

	@SuppressWarnings("serial")
	public static Map<BlockPos, Sphere> sphereCache = new LinkedHashMap<BlockPos, Sphere>(500, 0.7f, true) {

		@Override
		protected boolean removeEldestEntry(Map.Entry<BlockPos, Sphere> eldest) {
			return size() > 500;
		}
	};

	private Sphere(IWorld world, BlockPos centrePos) {
		this.world = world;
		this.centre = centrePos;
		this.centreChunk = new ChunkPos(centrePos);
		this.rnd = new Random(centrePos.hashCode() * world.getSeed());
		this.biome = this.getRandomBiome(this.rnd);
		this.radius = this.rnd.nextInt((maxRadius - minRadius) + 1) + minRadius;
	}

	public static Sphere fromCentre(IWorld worldIn, BlockPos centrePos) {
		if (sphereCache.containsKey(centrePos)) {
			return sphereCache.get(centrePos);
		}
		Sphere sphere = new Sphere(worldIn, centrePos);
		sphereCache.put(centrePos, sphere);
		return sphere;
	}

	public static Sphere fromCentreChunk(IWorld worldIn, ChunkPos chunkPos) {
		BlockPos pos = chunkPos.getBlock(8, Sphere.midY, 8);
		return Sphere.fromCentre(worldIn, pos);
	}

	public static Sphere getClosest(IWorld worldIn, BlockPos pos) {
		ChunkPos chunkPos = new ChunkPos(pos);
		int chunkOffsetX = (int) Math.floor(Math.IEEEremainder(chunkPos.x, Sphere.gridSize));
		int chunkOffsetZ = (int) Math.floor(Math.IEEEremainder(chunkPos.z, Sphere.gridSize));
		return Sphere.fromCentreChunk(worldIn, new ChunkPos(chunkPos.x - chunkOffsetX, chunkPos.z - chunkOffsetZ));
	}

	private Biome getRandomBiome(Random rnd) {
		Biome biome = BiosphereBiomeProvider.biomesArray[(rnd.nextInt(BiosphereBiomeProvider.biomesArray.length))];
		return biome;
	}

	public int getDistanceToCenter(BlockPos pos) {
		return (int) Math.floor(Math.sqrt(this.getCentre().distanceSq(pos.getX(), pos.getY(), pos.getZ(), false)));
	}

	public Random getRandom() {
		return this.rnd;
	}

	public ChunkPos getCentreChunk() {
		return this.centreChunk;
	}

	public BlockPos getCentre() {
		return this.centre;
	}

	public Biome getBiome() {
		return this.biome;
	}

	public BlockPos computeBridgeJoin(BiosphereChunkGenerator<?> chunkGen, Direction dir) {
		BlockPos fromCache = this.bridgeJoin.get(dir);
		if (fromCache != null) {
			return fromCache;
		}

		BlockPos.Mutable join = new BlockPos.Mutable(this.getCentre()).move(dir, this.radius);
		int domeHeight;

		do {
			join.move(dir.getOpposite(), 1);
			ChunkPos joinChunk = new ChunkPos(join);
			double[][][] noisesFrom = chunkGen.getNoiseForChunk(this.world, joinChunk);
			// height of dome is pythag, where radius is hyp, and dome height and horizontal
			// distance from centre are sides
			domeHeight = Sphere.midY + (int) Math.round(Math.sqrt(Math.abs(Math.pow(this.radius, 2) - Math
					.pow(Utils.getCoord(this.getCentre(), dir.getAxis()) - Utils.getCoord(join, dir.getAxis()), 2))));
			join.setY(Utils.topBlockFromNoise(noisesFrom, Math.abs(join.getX()) % 16, Math.abs(join.getZ()) % 16,
					domeHeight, chunkGen.getSeaLevel(), (i) -> chunkGen.correctYValue(i)));
			if (Utils.getCoord(this.getCentre(), dir.getAxis()) == Utils.getCoord(join, dir.getAxis())) {
				break;
			}
		} while (join.getY() >= domeHeight);

		join.move(dir, 1);
		ChunkPos joinChunk = new ChunkPos(join);
		double[][][] noisesFrom = chunkGen.getNoiseForChunk(this.world, joinChunk);
		domeHeight = Sphere.midY + (int) Math.round(Math.sqrt(Math.abs(Math.pow(this.radius, 2)
				- Math.pow(Utils.getCoord(this.getCentre(), dir.getAxis()) - Utils.getCoord(join, dir.getAxis()), 2))));
		if (domeHeight < Sphere.midY + 3) {
			domeHeight = Sphere.midY + 10;
		}
		join.setY(Utils.topBlockFromNoise(noisesFrom, Math.abs(join.getX()) % 16, Math.abs(join.getZ()) % 16,
				domeHeight, chunkGen.getSeaLevel(), (i) -> chunkGen.correctYValue(i)));

		BlockPos joinIm = join.toImmutable();
		this.bridgeJoin.put(dir, joinIm);

		BlockPos.Mutable one = new BlockPos.Mutable(joinIm);
		BlockPos.Mutable two = new BlockPos.Mutable(joinIm);

		// This will be to the side of the middle block of the bridge
		one.move(dir.rotateY(), chunkGen.BRIDGE_WIDTH).move(Direction.UP);
		two.move(dir.rotateYCCW(), chunkGen.BRIDGE_WIDTH);
		two.move(dir.getOpposite(),
				Math.abs(Utils.getCoord(joinIm, dir.getAxis()) - Utils.getCoord(this.getCentre(), dir.getAxis())));
		two.move(Direction.UP, chunkGen.BRIDGE_HEIGHT);

		this.domeCutouts.add(new MutableBoundingBox(one, two));

		return joinIm;
	}

	public HashMap<Direction, BlockPos> getBridgeJoins() {
		return this.getBridgeJoins();
	}

	public ArrayList<MutableBoundingBox> getCutouts() {
		return this.domeCutouts;
	}
}
