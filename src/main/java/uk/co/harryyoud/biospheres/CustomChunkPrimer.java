package uk.co.harryyoud.biospheres;

import java.util.BitSet;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.palette.UpgradeData;
import net.minecraft.world.ITickList;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.GenerationStage.Carving;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.gen.feature.structure.StructureStart;

public class CustomChunkPrimer implements IChunk {

	private IChunk chunk;

	private Predicate<BlockPos> filter;

	public CustomChunkPrimer(IChunk chunkIn, Predicate<BlockPos> filter) {
		this.chunk = chunkIn;
		this.filter = filter;
	}

	@Override
	public BlockState setBlockState(BlockPos pos, BlockState state, boolean isMoving) {
		if (this.filter.test(pos)) {
			return Blocks.VOID_AIR.getDefaultState();
		}
		return chunk.setBlockState(pos, state, isMoving);
	}

	public IChunk getInnerIChunk() {
		return this.chunk;
	}

	@Override
	public boolean equals(Object objIn) {
		if (objIn instanceof CustomChunkPrimer) {
			return this.chunk == ((CustomChunkPrimer) objIn).getInnerIChunk();
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.chunk.hashCode();
	}

	// AUTO GENERATED ECLIPSE DELEGATED METHODS

	@Override
	public void addEntity(Entity entityIn) {
		chunk.addEntity(entityIn);
	}

	@Override
	public void addStructureReference(String arg0, long arg1) {
		chunk.addStructureReference(arg0, arg1);
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		return chunk.getBlockState(pos);
	}

	@Override
	public IFluidState getFluidState(BlockPos pos) {
		return chunk.getFluidState(pos);
	}

	@Override
	public int getLightValue(BlockPos pos) {
		return chunk.getLightValue(pos);
	}

	@Override
	public int getMaxLightLevel() {
		return chunk.getMaxLightLevel();
	}

	@Override
	public int getHeight() {
		return chunk.getHeight();
	}

	@Override
	public void addTileEntity(BlockPos pos, TileEntity tileEntityIn) {
		chunk.addTileEntity(pos, tileEntityIn);
	}

	@Override
	public ChunkSection getLastExtendedBlockStorage() {
		return chunk.getLastExtendedBlockStorage();
	}

	@Override
	public ChunkSection[] getSections() {
		return chunk.getSections();
	}

	@Override
	public Collection<Entry<Type, Heightmap>> getHeightmaps() {
		return chunk.getHeightmaps();
	}

	@Override
	public Heightmap getHeightmap(Type typeIn) {
		return chunk.getHeightmap(typeIn);
	}

	@Override
	public ChunkPos getPos() {
		return chunk.getPos();
	}

	@Override
	public BiomeContainer getBiomes() {
		return chunk.getBiomes();
	}

	@Override
	public ChunkStatus getStatus() {
		return chunk.getStatus();
	}

	@Override
	public ShortList[] getPackedPositions() {
		return chunk.getPackedPositions();
	}

	@Override
	public void func_201636_b(short packedPosition, int index) {
		chunk.func_201636_b(packedPosition, index);
	}

	@Override
	public void addTileEntity(CompoundNBT nbt) {
		chunk.addTileEntity(nbt);
	}

	@Override
	public CompoundNBT getDeferredTileEntity(BlockPos pos) {
		return chunk.getDeferredTileEntity(pos);
	}

	@Override
	public Stream<BlockPos> getLightSources() {
		return chunk.getLightSources();
	}

	@Override
	public ITickList<Block> getBlocksToBeTicked() {
		return chunk.getBlocksToBeTicked();
	}

	@Override
	public ITickList<Fluid> getFluidsToBeTicked() {
		return chunk.getFluidsToBeTicked();
	}

	@Override
	public BitSet getCarvingMask(Carving type) {
		return chunk.getCarvingMask(type);
	}

	@Override
	public long getInhabitedTime() {
		return chunk.getInhabitedTime();
	}

	@Override
	public Map<String, LongSet> getStructureReferences() {
		return chunk.getStructureReferences();
	}

	@Override
	public LongSet getStructureReferences(String arg0) {
		return chunk.getStructureReferences(arg0);
	}

	@Override
	public StructureStart getStructureStart(String arg0) {
		return chunk.getStructureStart(arg0);
	}

	@Override
	public TileEntity getTileEntity(BlockPos pos) {
		return chunk.getTileEntity(pos);
	}

	@Override
	public BlockRayTraceResult rayTraceBlocks(RayTraceContext context) {
		return chunk.rayTraceBlocks(context);
	}

	@Override
	public int getTopFilledSegment() {
		return chunk.getTopFilledSegment();
	}

	@Override
	public Set<BlockPos> getTileEntitiesPos() {
		return chunk.getTileEntitiesPos();
	}

	@Override
	public void setHeightmap(Type type, long[] data) {
		chunk.setHeightmap(type, data);
	}

	@Override
	public int getTopBlockY(Type heightmapType, int x, int z) {
		return chunk.getTopBlockY(heightmapType, x, z);
	}

	@Override
	public BlockRayTraceResult rayTraceBlocks(Vec3d p_217296_1_, Vec3d p_217296_2_, BlockPos p_217296_3_,
			VoxelShape p_217296_4_, BlockState p_217296_5_) {
		return chunk.rayTraceBlocks(p_217296_1_, p_217296_2_, p_217296_3_, p_217296_4_, p_217296_5_);
	}

	@Override
	public void setLastSaveTime(long saveTime) {
		chunk.setLastSaveTime(saveTime);
	}

	@Override
	public Map<String, StructureStart> getStructureStarts() {
		return chunk.getStructureStarts();
	}

	@Override
	public void setStructureStarts(Map<String, StructureStart> structureStartsIn) {
		chunk.setStructureStarts(structureStartsIn);
	}

	@Override
	public boolean isEmptyBetween(int startY, int endY) {
		return chunk.isEmptyBetween(startY, endY);
	}

	@Override
	public void setModified(boolean modified) {
		chunk.setModified(modified);
	}

	@Override
	public boolean isModified() {
		return chunk.isModified();
	}

	@Override
	public void removeTileEntity(BlockPos pos) {
		chunk.removeTileEntity(pos);
	}

	@Override
	public void markBlockForPostprocessing(BlockPos pos) {
		chunk.markBlockForPostprocessing(pos);
	}

	@Override
	public CompoundNBT getTileEntityNBT(BlockPos pos) {
		return chunk.getTileEntityNBT(pos);
	}

	@Override
	public UpgradeData getUpgradeData() {
		return chunk.getUpgradeData();
	}

	@Override
	public void setInhabitedTime(long newInhabitedTime) {
		chunk.setInhabitedTime(newInhabitedTime);
	}

	@Override
	public boolean hasLight() {
		return chunk.hasLight();
	}

	@Override
	public void setLight(boolean lightCorrectIn) {
		chunk.setLight(lightCorrectIn);
	}

	@Override
	public IWorld getWorldForge() {
		return chunk.getWorldForge();
	}

	@Override
	public void putStructureStart(String arg0, StructureStart arg1) {
		chunk.putStructureStart(arg0, arg1);
	}

	@Override
	public void setStructureReferences(Map<String, LongSet> arg0) {
		chunk.setStructureReferences(arg0);
	}
}