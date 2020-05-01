package uk.co.harryyoud.biospheres.wrappers;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.particles.IParticleData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.ITickList;
import net.minecraft.world.IWorld;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraft.world.storage.WorldInfo;

@SuppressWarnings("deprecation")
public class IWorldWrapper implements IWorld {

	public final IWorld world;
	public final int seaLevel;
	private Predicate<BlockPos> blockStatePredicate;

	public IWorldWrapper(IWorld world, int seaLevel) {
		this.world = world;
		this.seaLevel = seaLevel;
	}

	@Override
	public int getSeaLevel() {
		return this.seaLevel;
	}

	public void setBlockStatePredicate(Predicate<BlockPos> f) {
		this.blockStatePredicate = f;
	}

	@Override
	public boolean setBlockState(BlockPos pos, BlockState newState, int flags) {
		if (!this.blockStatePredicate.test(pos)) {
			return false;
		}
		return world.setBlockState(pos, newState, flags);
	}

	// ECLIPSE AUTO-GENERATE DELEGATE METHODS

	@Override
	public boolean hasBlockState(BlockPos p_217375_1_, Predicate<BlockState> p_217375_2_) {
		return world.hasBlockState(p_217375_1_, p_217375_2_);
	}

	@Override
	public WorldLightManager getLightManager() {
		return world.getLightManager();
	}

	@Override
	public int getMaxHeight() {
		return world.getMaxHeight();
	}

	@Override
	public int getLightFor(LightType lightTypeIn, BlockPos blockPosIn) {
		return world.getLightFor(lightTypeIn, blockPosIn);
	}

	@Override
	public TileEntity getTileEntity(BlockPos pos) {
		return world.getTileEntity(pos);
	}

	@Override
	public int getLightSubtracted(BlockPos blockPosIn, int amount) {
		return world.getLightSubtracted(blockPosIn, amount);
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		return world.getBlockState(pos);
	}

	@Override
	public IFluidState getFluidState(BlockPos pos) {
		return world.getFluidState(pos);
	}

	@Override
	public List<Entity> getEntitiesInAABBexcluding(Entity entityIn, AxisAlignedBB boundingBox,
			Predicate<? super Entity> predicate) {
		return world.getEntitiesInAABBexcluding(entityIn, boundingBox, predicate);
	}

	@Override
	public int getLightValue(BlockPos pos) {
		return world.getLightValue(pos);
	}

	@Override
	public boolean canSeeSky(BlockPos blockPosIn) {
		return world.canSeeSky(blockPosIn);
	}

	@Override
	public boolean removeBlock(BlockPos pos, boolean isMoving) {
		return world.removeBlock(pos, isMoving);
	}

	@Override
	public IChunk getChunk(int x, int z, ChunkStatus requiredStatus, boolean nonnull) {
		return world.getChunk(x, z, requiredStatus, nonnull);
	}

	@Override
	public boolean destroyBlock(BlockPos pos, boolean dropBlock) {
		return world.destroyBlock(pos, dropBlock);
	}

	@Override
	public int getMaxLightLevel() {
		return world.getMaxLightLevel();
	}

	@Override
	public int getHeight() {
		return world.getHeight();
	}

	@Override
	public WorldBorder getWorldBorder() {
		return world.getWorldBorder();
	}

	@Override
	public <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> clazz, AxisAlignedBB aabb,
			Predicate<? super T> filter) {
		return world.getEntitiesWithinAABB(clazz, aabb, filter);
	}

	@Override
	public int getHeight(Type heightmapType, int x, int z) {
		return world.getHeight(heightmapType, x, z);
	}

	@Override
	public BlockRayTraceResult rayTraceBlocks(RayTraceContext context) {
		return world.rayTraceBlocks(context);
	}

	@Override
	public long getSeed() {
		return world.getSeed();
	}

	@Override
	public int getSkylightSubtracted() {
		return world.getSkylightSubtracted();
	}

	@Override
	public float getCurrentMoonPhaseFactor() {
		return world.getCurrentMoonPhaseFactor();
	}

	@Override
	public BiomeManager getBiomeManager() {
		return world.getBiomeManager();
	}

	@Override
	public boolean destroyBlock(BlockPos p_225521_1_, boolean p_225521_2_, Entity p_225521_3_) {
		return world.destroyBlock(p_225521_1_, p_225521_2_, p_225521_3_);
	}

	@Override
	public Biome getBiome(BlockPos p_226691_1_) {
		return world.getBiome(p_226691_1_);
	}

	@Override
	public <T extends Entity> List<T> getLoadedEntitiesWithinAABB(Class<? extends T> p_225316_1_,
			AxisAlignedBB p_225316_2_, Predicate<? super T> p_225316_3_) {
		return world.getLoadedEntitiesWithinAABB(p_225316_1_, p_225316_2_, p_225316_3_);
	}

	@Override
	public boolean func_226663_a_(BlockState p_226663_1_, BlockPos p_226663_2_, ISelectionContext p_226663_3_) {
		return world.func_226663_a_(p_226663_1_, p_226663_2_, p_226663_3_);
	}

	@Override
	public boolean addEntity(Entity entityIn) {
		return world.addEntity(entityIn);
	}

	@Override
	public int getBlockColor(BlockPos blockPosIn, ColorResolver colorResolverIn) {
		return world.getBlockColor(blockPosIn, colorResolverIn);
	}

	@Override
	public float getCelestialAngle(float partialTicks) {
		return world.getCelestialAngle(partialTicks);
	}

	@Override
	public List<? extends PlayerEntity> getPlayers() {
		return world.getPlayers();
	}

	@Override
	public List<Entity> getEntitiesWithinAABBExcludingEntity(Entity entityIn, AxisAlignedBB bb) {
		return world.getEntitiesWithinAABBExcludingEntity(entityIn, bb);
	}

	@Override
	public Biome getNoiseBiome(int x, int y, int z) {
		return world.getNoiseBiome(x, y, z);
	}

	@Override
	public int getMoonPhase() {
		return world.getMoonPhase();
	}

	@Override
	public boolean checkNoEntityCollision(Entity p_226668_1_) {
		return world.checkNoEntityCollision(p_226668_1_);
	}

	@Override
	public ITickList<Block> getPendingBlockTicks() {
		return world.getPendingBlockTicks();
	}

	@Override
	public ITickList<Fluid> getPendingFluidTicks() {
		return world.getPendingFluidTicks();
	}

	@Override
	public boolean hasNoCollisions(AxisAlignedBB p_226664_1_) {
		return world.hasNoCollisions(p_226664_1_);
	}

	@Override
	public Biome getNoiseBiomeRaw(int x, int y, int z) {
		return world.getNoiseBiomeRaw(x, y, z);
	}

	@Override
	public World getWorld() {
		return world.getWorld();
	}

	@Override
	public boolean isRemote() {
		return world.isRemote();
	}

	@Override
	public WorldInfo getWorldInfo() {
		return world.getWorldInfo();
	}

	@Override
	public Dimension getDimension() {
		return world.getDimension();
	}

	@Override
	public DifficultyInstance getDifficultyForLocation(BlockPos pos) {
		return world.getDifficultyForLocation(pos);
	}

	@Override
	public boolean hasNoCollisions(Entity p_226669_1_) {
		return world.hasNoCollisions(p_226669_1_);
	}

	@Override
	public Difficulty getDifficulty() {
		return world.getDifficulty();
	}

	@Override
	public AbstractChunkProvider getChunkProvider() {
		return world.getChunkProvider();
	}

	@Override
	public boolean isAirBlock(BlockPos pos) {
		return world.isAirBlock(pos);
	}

	@Override
	public boolean hasNoCollisions(Entity p_226665_1_, AxisAlignedBB p_226665_2_) {
		return world.hasNoCollisions(p_226665_1_, p_226665_2_);
	}

	@Override
	public boolean chunkExists(int chunkX, int chunkZ) {
		return world.chunkExists(chunkX, chunkZ);
	}

	@Override
	public boolean hasNoCollisions(Entity p_226662_1_, AxisAlignedBB p_226662_2_, Set<Entity> p_226662_3_) {
		return world.hasNoCollisions(p_226662_1_, p_226662_2_, p_226662_3_);
	}

	@Override
	public Random getRandom() {
		return world.getRandom();
	}

	@Override
	public void notifyNeighbors(BlockPos pos, Block blockIn) {
		world.notifyNeighbors(pos, blockIn);
	}

	@Override
	public <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> p_217357_1_, AxisAlignedBB p_217357_2_) {
		return world.getEntitiesWithinAABB(p_217357_1_, p_217357_2_);
	}

	@Override
	public BlockPos getSpawnPoint() {
		return world.getSpawnPoint();
	}

	@Override
	public boolean canBlockSeeSky(BlockPos pos) {
		return world.canBlockSeeSky(pos);
	}

	@Override
	public void playSound(PlayerEntity player, BlockPos pos, SoundEvent soundIn, SoundCategory category, float volume,
			float pitch) {
		world.playSound(player, pos, soundIn, category, volume, pitch);
	}

	@Override
	public BlockRayTraceResult rayTraceBlocks(Vec3d p_217296_1_, Vec3d p_217296_2_, BlockPos p_217296_3_,
			VoxelShape p_217296_4_, BlockState p_217296_5_) {
		return world.rayTraceBlocks(p_217296_1_, p_217296_2_, p_217296_3_, p_217296_4_, p_217296_5_);
	}

	@Override
	public <T extends Entity> List<T> func_225317_b(Class<? extends T> p_225317_1_, AxisAlignedBB p_225317_2_) {
		return world.func_225317_b(p_225317_1_, p_225317_2_);
	}

	@Override
	public Stream<VoxelShape> getCollisionShapes(Entity p_226667_1_, AxisAlignedBB p_226667_2_,
			Set<Entity> p_226667_3_) {
		return world.getCollisionShapes(p_226667_1_, p_226667_2_, p_226667_3_);
	}

	@Override
	public Stream<VoxelShape> getCollisionShapes(Entity p_226666_1_, AxisAlignedBB p_226666_2_) {
		return world.getCollisionShapes(p_226666_1_, p_226666_2_);
	}

	@Override
	public void addParticle(IParticleData particleData, double x, double y, double z, double xSpeed, double ySpeed,
			double zSpeed) {
		world.addParticle(particleData, x, y, z, xSpeed, ySpeed, zSpeed);
	}

	@Override
	public void playEvent(PlayerEntity player, int type, BlockPos pos, int data) {
		world.playEvent(player, type, pos, data);
	}

	@Override
	public float getBrightness(BlockPos pos) {
		return world.getBrightness(pos);
	}

	@Override
	public void playEvent(int type, BlockPos pos, int data) {
		world.playEvent(type, pos, data);
	}

	@Override
	public int getStrongPower(BlockPos pos, Direction direction) {
		return world.getStrongPower(pos, direction);
	}

	@Override
	public Stream<VoxelShape> getEmptyCollisionShapes(Entity entityIn, AxisAlignedBB aabb,
			Set<Entity> entitiesToIgnore) {
		return world.getEmptyCollisionShapes(entityIn, aabb, entitiesToIgnore);
	}

	@Override
	public IChunk getChunk(BlockPos pos) {
		return world.getChunk(pos);
	}

	@Override
	public boolean checkNoEntityCollision(Entity entityIn, VoxelShape shape) {
		return world.checkNoEntityCollision(entityIn, shape);
	}

	@Override
	public IChunk getChunk(int chunkX, int chunkZ) {
		return world.getChunk(chunkX, chunkZ);
	}

	@Override
	public PlayerEntity getClosestPlayer(double x, double y, double z, double distance, Predicate<Entity> predicate) {
		return world.getClosestPlayer(x, y, z, distance, predicate);
	}

	@Override
	public IChunk getChunk(int chunkX, int chunkZ, ChunkStatus requiredStatus) {
		return world.getChunk(chunkX, chunkZ, requiredStatus);
	}

	@Override
	public BlockPos getHeight(Type heightmapType, BlockPos pos) {
		return world.getHeight(heightmapType, pos);
	}

	@Override
	public IBlockReader getBlockReader(int chunkX, int chunkZ) {
		return world.getBlockReader(chunkX, chunkZ);
	}

	@Override
	public boolean hasWater(BlockPos pos) {
		return world.hasWater(pos);
	}

	@Override
	public boolean containsAnyLiquid(AxisAlignedBB bb) {
		return world.containsAnyLiquid(bb);
	}

	@Override
	public PlayerEntity getClosestPlayer(Entity entityIn, double distance) {
		return world.getClosestPlayer(entityIn, distance);
	}

	@Override
	public PlayerEntity getClosestPlayer(double x, double y, double z, double distance, boolean creativePlayers) {
		return world.getClosestPlayer(x, y, z, distance, creativePlayers);
	}

	@Override
	public PlayerEntity getClosestPlayer(double x, double y, double z) {
		return world.getClosestPlayer(x, y, z);
	}

	@Override
	public int getLight(BlockPos pos) {
		return world.getLight(pos);
	}

	@Override
	public int getNeighborAwareLightSubtracted(BlockPos pos, int amount) {
		return world.getNeighborAwareLightSubtracted(pos, amount);
	}

	@Override
	public boolean isPlayerWithin(double x, double y, double z, double distance) {
		return world.isPlayerWithin(x, y, z, distance);
	}

	@Override
	public boolean isBlockLoaded(BlockPos pos) {
		return world.isBlockLoaded(pos);
	}

	@Override
	public boolean isAreaLoaded(BlockPos center, int range) {
		return world.isAreaLoaded(center, range);
	}

	@Override
	public boolean isAreaLoaded(BlockPos from, BlockPos to) {
		return world.isAreaLoaded(from, to);
	}

	@Override
	public PlayerEntity getClosestPlayer(EntityPredicate predicate, LivingEntity target) {
		return world.getClosestPlayer(predicate, target);
	}

	@Override
	public boolean isAreaLoaded(int fromX, int fromY, int fromZ, int toX, int toY, int toZ) {
		return world.isAreaLoaded(fromX, fromY, fromZ, toX, toY, toZ);
	}

	@Override
	public PlayerEntity getClosestPlayer(EntityPredicate predicate, LivingEntity target, double p_217372_3_,
			double p_217372_5_, double p_217372_7_) {
		return world.getClosestPlayer(predicate, target, p_217372_3_, p_217372_5_, p_217372_7_);
	}

	@Override
	public PlayerEntity getClosestPlayer(EntityPredicate predicate, double x, double y, double z) {
		return world.getClosestPlayer(predicate, x, y, z);
	}

	@Override
	public <T extends LivingEntity> T getClosestEntityWithinAABB(Class<? extends T> entityClazz,
			EntityPredicate p_217360_2_, LivingEntity target, double x, double y, double z, AxisAlignedBB boundingBox) {
		return world.getClosestEntityWithinAABB(entityClazz, p_217360_2_, target, x, y, z, boundingBox);
	}

	@Override
	public <T extends LivingEntity> T func_225318_b(Class<? extends T> p_225318_1_, EntityPredicate p_225318_2_,
			LivingEntity p_225318_3_, double p_225318_4_, double p_225318_6_, double p_225318_8_,
			AxisAlignedBB p_225318_10_) {
		return world.func_225318_b(p_225318_1_, p_225318_2_, p_225318_3_, p_225318_4_, p_225318_6_, p_225318_8_,
				p_225318_10_);
	}

	@Override
	public <T extends LivingEntity> T getClosestEntity(List<? extends T> entities, EntityPredicate predicate,
			LivingEntity target, double x, double y, double z) {
		return world.getClosestEntity(entities, predicate, target, x, y, z);
	}

	@Override
	public List<PlayerEntity> getTargettablePlayersWithinAABB(EntityPredicate predicate, LivingEntity target,
			AxisAlignedBB box) {
		return world.getTargettablePlayersWithinAABB(predicate, target, box);
	}

	@Override
	public <T extends LivingEntity> List<T> getTargettableEntitiesWithinAABB(Class<? extends T> p_217374_1_,
			EntityPredicate p_217374_2_, LivingEntity p_217374_3_, AxisAlignedBB p_217374_4_) {
		return world.getTargettableEntitiesWithinAABB(p_217374_1_, p_217374_2_, p_217374_3_, p_217374_4_);
	}

	@Override
	public PlayerEntity getPlayerByUuid(UUID uniqueIdIn) {
		return world.getPlayerByUuid(uniqueIdIn);
	}
}
