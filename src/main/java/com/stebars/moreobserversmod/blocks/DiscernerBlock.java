package com.stebars.moreobserversmod.blocks;

import com.stebars.moreobserversmod.utils.PropertyIntegerDetails;
import net.minecraft.block.*;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class DiscernerBlock extends BlockObserver{

	public static final PropertyInteger POWER = PropertyInteger.create("power", 0, 15);

	public static final PropertyBool FOOL = PropertyBool.create("fool");

	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, FACING, POWERED, POWER, FOOL);
	}
	
	// List of properties it'll check, in order, stopping at first one that's present
	public static List<PropertyIntegerDetails> propertyList = new ArrayList<PropertyIntegerDetails>(Arrays.asList(
			new PropertyIntegerDetails(BlockCrops.AGE, 7, 1),
			//new PropertyIntegerDetails(PropertyInteger.AGE_15, 15),
			//new PropertyIntegerDetails(PropertyInteger.AGE_25, 25),
			new PropertyIntegerDetails(PropertyInteger.create("power", 0, 15), 15), // redstone power
			//new PropertyIntegerDetails(PropertyInteger.LEVEL_HONEY, 5),
			new PropertyIntegerDetails(BlockSapling.STAGE, 1), // bamboo and sapling stage
			new PropertyIntegerDetails(BlockCauldron.LEVEL, 3),
			//new PropertyIntegerDetails(Block.LEVEL_COMPOSTER, 8),
			new PropertyIntegerDetails(BlockFluidBase.LEVEL, 8), // level of flowing fluid; actual max is 15, but only goes up to 7 for flowing blocks
			//new PropertyIntegerDetails(BlockStateProperties.LEVEL_FLOWING, 8, 1),
			new PropertyIntegerDetails(BlockFarmland.MOISTURE, 7),
			new PropertyIntegerDetails(BlockCake.BITES, 6), // cake bites
			new PropertyIntegerDetails(BlockRedstoneRepeater.DELAY, 4, 1) // repeater delay
			//new PropertyIntegerDetails(BlockStateProperties.HATCH, 2),
			//new PropertyIntegerDetails(BlockStateProperties.EGGS, 4, 1), // turtle eggs
			//new PropertyIntegerDetails(BlockStateProperties.PICKLES, 4, 1) // sea pickles
			));

	public DiscernerBlock() {
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.SOUTH).withProperty(POWERED, Boolean.FALSE).withProperty(POWER, 0).withProperty(FOOL, false));
	}

	private int detectSignal(IBlockState state, IBlockAccess world, BlockPos pos){
		IBlockState observedState = world.getBlockState(pos.offset(state.getValue(FACING)));

		int seen0to11 = -1;
		for (PropertyIntegerDetails details : propertyList) {
			int propertyVal = details.toBracket(observedState);
			if (propertyVal != -1) {
				seen0to11 = propertyVal;
				break;
			}
		}
		// Some boolean properties:
		// Piston blocks
		if (seen0to11 == -1 && state.getProperties().containsKey(BlockPistonBase.EXTENDED))
			seen0to11 = state.getValue(BlockPistonBase.EXTENDED) ? 11 : 0;
		// Whether objects are powered (anything, eg doors, bells, note blocks)
		// Do not enable this - for some reason it thinks everything is powered, even empty air
		//else if (seen0to11 == -1 && state.hasProperty(BlockStateProperties.POWERED))
		//	seen0to11 = state.getValue(BlockStateProperties.POWERED) ? 11 : 0;

		// Given seen value, we want to output 0 if no property, 5-10 for intermediate values, 15 if at max
		int outputSignal;
		if (seen0to11 == -1) // no properties detected
			outputSignal = 0;
		else if (seen0to11 == 11) // property is at max
			outputSignal = 15;
		else
			outputSignal = 5 + (seen0to11 / 2);

		return outputSignal;
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		return state.withProperty(POWER, detectSignal(state, world, pos)).withProperty(FOOL, LocalDateTime.now().getMonth() == Month.APRIL && LocalDateTime.now().getDayOfMonth() == 1);
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		world.scheduleUpdate(pos, this, 2);

		IBlockState updatedState = state
				.withProperty(POWERED, detectSignal(state, world, pos) > 0)
				.withProperty(POWER, detectSignal(state, world, pos))
				.withProperty(FOOL, LocalDateTime.now().getMonth() == Month.APRIL && LocalDateTime.now().getDayOfMonth() == 1);

		//((TileEntityWithPowered) world.getTileEntity(pos)).setPower(outputSignal);
		world.setBlockState(pos, updatedState, 2);

		this.updateNeighborsInFront(world, pos, state);
	}

	@Override
	public int getWeakPower(IBlockState state, IBlockAccess reader, BlockPos pos, EnumFacing direction) {
		return state.getValue(FACING) == direction? state.getValue(POWER) : 0;
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
		if (!p_220082_2_.isRemote && p_220082_1_.getValue(POWERED) && !p_220082_2_.isUpdateScheduled(p_220082_3_, this)) {
			IBlockState blockstate = p_220082_1_.withProperty(POWERED, Boolean.FALSE).withProperty(POWER, 0).withProperty(FOOL, Boolean.FALSE);
			p_220082_2_.setBlockState(p_220082_3_, blockstate, 18);
			this.updateNeighborsInFront(p_220082_2_, p_220082_3_, blockstate);
		}
		// schedule update tick to get initial value
		p_220082_2_.scheduleUpdate(p_220082_3_, this, 2);
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

