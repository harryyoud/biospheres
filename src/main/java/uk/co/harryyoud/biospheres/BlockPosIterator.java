package uk.co.harryyoud.biospheres;

import java.util.Iterator;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunk;

public class BlockPosIterator implements Iterable<BlockPos>, Iterator<BlockPos> {

	private final IChunk chunk;
	private int currentX = 0;
	private int currentY = 0;
	private int currentZ = 0;

	public BlockPosIterator(IChunk chunkIn) {
		this.chunk = chunkIn;
	}

	@Override
	public boolean hasNext() {
		boolean xIsMax = this.currentX == 15;
		boolean yIsMax = this.currentY == 255;
		boolean zIsMax = this.currentZ == 15;
		return !(xIsMax && yIsMax && zIsMax);
	}

	@Override
	public BlockPos next() {
		if (this.currentZ < 15) {
			this.currentZ += 1;
			return this.chunk.getPos().getBlock(this.currentX, this.currentY, this.currentZ);
		}
		if (this.currentY < 255) {
			this.currentY += 1;
			this.currentZ = 0;
			return this.chunk.getPos().getBlock(this.currentX, this.currentY, this.currentZ);
		}
		if (this.currentX < 15) {
			this.currentX += 1;
			this.currentY = 0;
			this.currentZ = 0;
			return this.chunk.getPos().getBlock(this.currentX, this.currentY, this.currentZ);
		}
		return null;
	}

	@Override
	public Iterator<BlockPos> iterator() {
		return this;
	}
}
