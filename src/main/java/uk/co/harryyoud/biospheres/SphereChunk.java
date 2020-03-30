package uk.co.harryyoud.biospheres;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;

public class SphereChunk {
	private final ChunkPos chunkPos;
	private final IWorld world;
	final Sphere closestSphere;

	public SphereChunk(IWorld worldIn, ChunkPos pos) {
		this.world = worldIn;
		this.chunkPos = pos;
		this.closestSphere = this.getClosestSphere();
	}

	public static SphereChunk get(IWorld worldIn, ChunkPos pos) {
		return new SphereChunk(worldIn, pos);
	}

	private Sphere getClosestSphere() {
		int chunkOffsetX = -(int) Math.floor(Math.IEEEremainder(this.chunkPos.x, Sphere.gridSize));
		int chunkOffsetZ = -(int) Math.floor(Math.IEEEremainder(this.chunkPos.z, Sphere.gridSize));
		return Sphere.get(this.world, new ChunkPos(this.chunkPos.x + chunkOffsetX, this.chunkPos.z + chunkOffsetZ));
	}
}