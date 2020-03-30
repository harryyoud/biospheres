package uk.co.harryyoud.biospheres;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public class SphereChunk {
	@SuppressWarnings("unused")
	private final World world;
	private final ChunkPos chunkPos;
	final BlockPos sphereCenter;
	private final int gridSize = 9;
	final int seaLevel = 64;
	public final int radius = 40;

	public SphereChunk(World world, ChunkPos pos) {
		this.world = world;
		this.chunkPos = pos;
		this.sphereCenter = this.getSphereCenter();
	}

	public static SphereChunk get(World world, ChunkPos pos) {
		return new SphereChunk(world, pos);
	}

	private BlockPos getSphereCenter() {
		int chunkOffsetX = -(int) Math.floor(Math.IEEEremainder((double) this.chunkPos.x, this.gridSize));
		int chunkOffsetZ = -(int) Math.floor(Math.IEEEremainder((double) this.chunkPos.z, this.gridSize));
		ChunkPos centerChunk = new ChunkPos(this.chunkPos.x + chunkOffsetX, this.chunkPos.z + chunkOffsetZ);
		return centerChunk.getBlock(8, this.seaLevel, 8);
	}

	public int getDistance(BlockPos pos) {
		double sq = Math.pow((pos.getX() - this.sphereCenter.getX()), 2.0D)
				+ Math.pow((pos.getY() - this.sphereCenter.getY()), 2.0D)
				+ Math.pow((pos.getZ() - this.sphereCenter.getZ()), 2.0D);
		return (int) Math.floor(Math.sqrt(sq));
	}
}
