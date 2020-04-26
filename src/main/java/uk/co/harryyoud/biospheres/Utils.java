package uk.co.harryyoud.biospheres;

import java.util.function.IntFunction;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public class Utils {
	public static int topBlockFromNoise(double[][][] noise, int x, int z, int topLimit, int bottomLimit,
			IntFunction<Integer> yCorrection) {
		for (int y = topLimit; y >= bottomLimit; y--) {
			if (noise[x][yCorrection.apply(y)][z] > 0.0D) {
				return y;
			}
		}
		return bottomLimit - 1;
	}

	public static int getCoord(BlockPos pos, Direction.Axis axis) {
		return axis.getCoordinate(pos.getX(), pos.getY(), pos.getZ());
	}

	public static ChunkPos moveChunk(ChunkPos chunkPos, Direction dir, int amount) {
		switch (dir) {
		case NORTH:
			return new ChunkPos(chunkPos.x, chunkPos.z - amount);
		case SOUTH:
			return new ChunkPos(chunkPos.x, chunkPos.z + amount);
		case WEST:
			return new ChunkPos(chunkPos.x - amount, chunkPos.z);
		case EAST:
			return new ChunkPos(chunkPos.x + amount, chunkPos.z);
		default:
			throw new IllegalStateException("Unable to get offset chunk in Y direction");
		}
	}

	public static boolean inIncRange(int num, int range1, int range2) {
		return Math.min(range1, range2) <= num && num <= Math.max(range1, range2);
	}
}
