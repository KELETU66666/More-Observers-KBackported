package com.stebars.moreobserversmod.utils;

import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;

public class PropertyIntegerDetails {
	public PropertyInteger property;
	public int max;
	public int min = 0;

	public PropertyIntegerDetails(PropertyInteger property, int max, int min) {
		this.property = property;
		this.max = max;
		this.min = min;
	}

	public PropertyIntegerDetails(PropertyInteger property, int max) {
		this.property = property;
		this.max = max;
	}

	public int toBracket(IBlockState state) {
		// Returns -1 if property not present, 0 if at min, 11 if at max, 1-10 if intermediate
		if (!state.getProperties().containsKey(property))
			return -1;

		int val = state.getValue(property);
		if (val == min) return 0;
		if (val == max) return 11;
		if(max - min - 1 != 0)
			return 1 + ((val - min - 1) * 10) / (max - min - 1);
		return 0;
	}
}
