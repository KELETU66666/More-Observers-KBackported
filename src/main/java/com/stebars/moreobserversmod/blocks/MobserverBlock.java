package com.stebars.moreobserversmod.blocks;

import akka.japi.Pair;
import net.minecraft.block.BlockObserver;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;


public class MobserverBlock extends BlockObserver {

	public static final int FREQUENCY_TICKS = 20;
	// How frequently to re-check for mobs

	public static final int FORWARD_RANGE = 5;
	// How far forward the detection range extends

	public static final int SIDE_RANGE = 2;
	// How far out the detection zone extends in each direction from forward direction
	// Actual bounding box has side length SIDE_RANGE * 2 + 1


	public MobserverBlock() {
		this.setDefaultState(this.blockState.getBaseState()
				.withProperty(FACING, EnumFacing.SOUTH)
				.withProperty(POWERED, Boolean.FALSE));
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		List<Entity> mobs = world.getEntitiesWithinAABB(EntityLivingBase.class, getZone(state, pos));

		boolean poweredOld = state.getValue(POWERED);
		boolean poweredNew = !mobs.isEmpty();

		if (poweredOld != poweredNew) {
			IBlockState updatedState = state.withProperty(POWERED, poweredNew);
			Blocks.REDSTONE_WIRE.getItem(world, pos, state);
			world.setBlockState(pos, updatedState, 2);
			this.updateNeighborsInFront(world, pos, state);
		}
		world.scheduleUpdate(pos, this, FREQUENCY_TICKS); // Schedule next check
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		if (!state.getBlock().equals(this)) {
			if (!world.isRemote && state.getValue(POWERED) && !world.isUpdateScheduled(pos, this)) {
				IBlockState blockstate = state.withProperty(POWERED, Boolean.FALSE);
				world.setBlockState(pos, blockstate, 18);
				this.updateNeighborsInFront(world, pos, blockstate);
			}
		}

		// Schedule update tick to get initial value
		world.scheduleUpdate(pos, this, 2);
	}

	public AxisAlignedBB getZone(IBlockState state, BlockPos pos) {
		// We recompute the zone on every update. Could do it faster by caching on block placement, but would
		// need to make tile entity (or they'd all share the same zone), I think
		EnumFacing facing = state.getValue(FACING);
		BlockPos farPos = pos.offset(facing, FORWARD_RANGE);
		Pair<EnumFacing, EnumFacing> otherDirs = getPerpendicularDirections(facing);
		BlockPos bound1 = farPos
				.offset(otherDirs.first(), SIDE_RANGE)
				.offset(otherDirs.second(), SIDE_RANGE);
		BlockPos bound2 = pos //.relative(facing, 1) -- make it a half-block too big, not half-block gap from front
				.offset(otherDirs.first(), -SIDE_RANGE)
				.offset(otherDirs.second(), -SIDE_RANGE);

		return new AxisAlignedBB(
				bound1,
				bound2
				);
	}

	public Pair<EnumFacing, EnumFacing> getPerpendicularDirections(EnumFacing dir) {
		if (dir == EnumFacing.DOWN || dir == EnumFacing.UP)
			return new Pair<EnumFacing, EnumFacing>(EnumFacing.EAST, EnumFacing.NORTH);
		else if (dir == EnumFacing.WEST || dir == EnumFacing.EAST)
			return new Pair<EnumFacing, EnumFacing>(EnumFacing.NORTH, EnumFacing.UP);
		else //if (dir == EnumFacing.NORTH || dir == EnumFacing.SOUTH)
			return new Pair<EnumFacing, EnumFacing>(EnumFacing.UP, EnumFacing.WEST);
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		return state.getValue(POWERED) ? 10 : super.getLightValue(state, world, pos);
	}

	@Override
	public int getWeakPower(IBlockState p_180656_1_, IBlockAccess p_180656_2_, BlockPos p_180656_3_, EnumFacing dir) {
		// Send both forward and backward
		EnumFacing facing = p_180656_1_.getValue(FACING);
		return p_180656_1_.getValue(POWERED) && (facing == dir || facing.getOpposite() == dir) ? 15 : 0;
	}

	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(FACING, EnumFacing.byIndex(meta & 7)).withProperty(POWERED, (meta & 8) > 0);

	}

	public int getMetaFromState(IBlockState state)
	{
		int i = 0;
		i = i | state.getValue(FACING).getIndex();

		if (state.getValue(POWERED))
		{
			i |= 8;
		}

		return i;
	}

	public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EnumFacing side)
	{
		return side == state.getValue(BlockObserver.FACING);
	}
}
