package com.stebars.moreobserversmod.blocks;

import net.minecraft.block.BlockObserver;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;


public class SurveyorBlock extends BlockObserver {

	public static final PropertyInteger POWER = PropertyInteger.create("power", 0, 15);

	public static final int FREQUENCY_TICKS = 20;
	// How frequently to re-check

	public static final int FORWARD_RANGE = 15;
	// How far forward the detection range extends
	
	public static final int REDSTONE_MAX = 15;

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, POWERED, POWER);
	}

	public SurveyorBlock() {
		this.setDefaultState(this.blockState.getBaseState()
				.withProperty(FACING, EnumFacing.SOUTH)
				.withProperty(POWERED, Boolean.FALSE)
				.withProperty(POWER, 0));
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		int distanceToFirstBlock = -1;
		boolean seen = false;
		EnumFacing facing = state.getValue(FACING);
		for (int distance = 1; distance <= FORWARD_RANGE; distance++) {
			if (!world.getBlockState(pos.offset(facing, distance)).getBlock().equals(Blocks.AIR)) {
				seen = true;
				distanceToFirstBlock = distance;
				break;
			}
		}

		int powerNew = seen ? REDSTONE_MAX - distanceToFirstBlock + 1 : 0;

		return state.withProperty(POWER, powerNew);
	}

	@SuppressWarnings("deprecation") // .isAir() is deprecated, says to use .isAir(world, pos) which is also deprecated?? 
	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		int distanceToFirstBlock = -1;
		boolean seen = false;
		EnumFacing facing = state.getValue(FACING);
		for (int distance = 1; distance <= FORWARD_RANGE; distance++) {
			if (!world.getBlockState(pos.offset(facing, distance)).getBlock().equals(Blocks.AIR)) {
				seen = true;
				distanceToFirstBlock = distance;
				break;
			}
		}

		int powerOld = state.getValue(POWER);
		int powerNew = seen ? REDSTONE_MAX - distanceToFirstBlock + 1 : 0;

		if (powerOld != powerNew) {
			IBlockState updatedState = state.withProperty(POWERED, powerNew > 0).withProperty(POWER, powerNew);
			world.setBlockState(pos, updatedState, 2);
			this.updateNeighborsInFront(world, pos, state);	
		}
		world.scheduleUpdate(pos, this, FREQUENCY_TICKS); // Schedule next check
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		if (!state.getBlock().equals(this)) {
			if (!world.isRemote && state.getValue(POWERED) && !world.isUpdateScheduled(pos, this)) {
				IBlockState blockstate = state.withProperty(POWERED, Boolean.FALSE).withProperty(POWER, 0);
				world.setBlockState(pos, blockstate, 18);
				this.updateNeighborsInFront(world, pos, blockstate);
			}
		}
		
		// Schedule update tick to get initial value
		world.scheduleUpdate(pos, this, 2);
	}


	@Override
	public int getWeakPower(IBlockState state, IBlockAccess reader, BlockPos pos, EnumFacing direction) {
		return state.getValue(FACING) == direction ? state.getValue(POWER) : 0;
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
