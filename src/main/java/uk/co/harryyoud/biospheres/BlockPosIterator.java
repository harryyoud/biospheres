package uk.co.harryyoud.biospheres;

import java.util.Iterator;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.SectionPos;

public class BlockPosIterator implements Iterable<BlockPos>, Iterator<BlockPos> {

	private final BlockPos.Mutable pos;

	private final int minX;
	private final int minY;
	private final int minZ;
	private final int maxX;
	private final int maxY;
	private final int maxZ;

	public BlockPosIterator(ChunkPos chunkPos) {
		this.minX = chunkPos.getXStart();
		this.minY = 0;
		this.minZ = chunkPos.getZStart();
		this.maxX = chunkPos.getXEnd();
		this.maxY = 255;
		this.maxZ = chunkPos.getZEnd();
		this.pos = new BlockPos.Mutable(this.minX, this.minY, this.minZ);
	}

	public BlockPosIterator(SectionPos secPos) {
		this.minX = secPos.getWorldStartX();
		this.minY = secPos.getWorldStartY();
		this.minZ = secPos.getWorldStartZ();
		this.maxX = secPos.getWorldEndX();
		this.maxY = secPos.getWorldEndY();
		this.maxZ = secPos.getWorldEndZ();
		this.pos = new BlockPos.Mutable(this.minX, this.minY, this.minZ);
	}

	@Override
	public boolean hasNext() {
		boolean xIsMax = this.pos.getX() == this.maxX;
		boolean yIsMax = this.pos.getY() == this.maxY;
		boolean zIsMax = this.pos.getZ() == this.maxZ;
		return !(xIsMax && yIsMax && zIsMax);
	}

	@Override
	public BlockPos next() {
		BlockPos ret = this.pos.toImmutable();
		if (this.pos.getZ() < this.maxZ) {
			this.pos.setZ(this.pos.getZ() + 1);
			return ret;
		}
		if (this.pos.getY() < this.maxY) {
			this.pos.setY(this.pos.getY() + 1);
			this.pos.setZ(this.minZ);
			return ret;
		}
		if (this.pos.getX() < this.maxX) {
			this.pos.setX(this.pos.getX() + 1);
			this.pos.setY(this.minY);
			this.pos.setZ(this.minZ);
			return ret;
		}
		return null;
	}

	@Override
	public Iterator<BlockPos> iterator() {
		return this;
	}
}
