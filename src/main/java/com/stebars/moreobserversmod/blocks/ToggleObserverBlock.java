package com.stebars.moreobserversmod.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockObserver;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

// Most code here taken from ObserverBlock, with POWERED changed to TRIGGERED except in some places

public class ToggleObserverBlock extends BlockObserver {

	public static final PropertyBool TRIGGERED = PropertyBool.create("triggered");
	// "Triggered" holds the value the vanilla observer would have, i.e. it pulses for 2 ticks when observed state changes
	// Then we flip the value of "powered" whenever "triggered" goes high.

	public static final int IMMUNITY_TICKS = 25;
	// After changing powered state, waits this number of ticks before being able to change again


	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, POWERED, TRIGGERED);
	}

	@Override
	public void updateTick(World p_225534_2_, BlockPos p_225534_3_, IBlockState state, Random p_225534_4_) {
		if (state.getValue(TRIGGERED)) {
			p_225534_2_.setBlockState(p_225534_3_, state.withProperty(TRIGGERED, Boolean.FALSE), 2);
		} else {
			p_225534_2_.setBlockState(p_225534_3_, state.withProperty(TRIGGERED, Boolean.TRUE)
					.withProperty(POWERED, !state.getValue(POWERED)), 2);
			p_225534_2_.scheduleUpdate(p_225534_3_, this, IMMUNITY_TICKS);
		}

		this.updateNeighborsInFront(p_225534_2_, p_225534_3_, state);
	}

	@Override
	public void observedNeighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos)
	{
		if (!world.isRemote && pos.offset(state.getValue(FACING)).equals(fromPos)){
			this.startSignal(world, pos);
		}
	}

	private void startSignal(World p_203420_1_, BlockPos p_203420_2_) {
		if (!p_203420_1_.isRemote && !p_203420_1_.isUpdateScheduled(p_203420_2_, this)) {
			p_203420_1_.scheduleUpdate(p_203420_2_, this, 2);
		}
	}

	@Override
	public void onBlockAdded(World p_220082_2_, BlockPos p_220082_3_, IBlockState p_220082_1_) {
		if (!p_220082_2_.isRemote && p_220082_1_.getValue(TRIGGERED) && !p_220082_2_.isUpdateScheduled(p_220082_3_, this)) {
			IBlockState blockstate = p_220082_1_.withProperty(TRIGGERED, Boolean.FALSE);
			p_220082_2_.setBlockState(p_220082_3_, blockstate, 18);
			this.updateNeighborsInFront(p_220082_2_, p_220082_3_, blockstate);
		}
	}

	public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
	{
		if (state.getValue(TRIGGERED) && worldIn.isUpdateScheduled(pos, this))
		{
			this.updateNeighborsInFront(worldIn, pos, state.withProperty(TRIGGERED, Boolean.FALSE));
		}
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		return state.getValue(POWERED) ? 10 : super.getLightValue(state, world, pos);
	}

	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(FACING, EnumFacing.byIndex(meta & 7)).withProperty(POWERED, (meta & 8) > 0).withProperty(TRIGGERED, false);

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
	// Don't want to send signal in other directions, e.g. forward (because the nose), because that would make it harder to detect
	// redstone signal changes from e.g. buttons.
}
