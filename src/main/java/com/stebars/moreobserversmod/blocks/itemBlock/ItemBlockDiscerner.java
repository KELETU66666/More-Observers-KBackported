package com.stebars.moreobserversmod.blocks.itemBlock;

import com.stebars.moreobserversmod.MoreObserversMod;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.time.LocalDateTime;
import java.time.Month;

public class ItemBlockDiscerner extends ItemBlock {
    public ItemBlockDiscerner() {
        super(MoreObserversMod.DISCERNER_BLOCK);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getItemStackDisplayName(ItemStack stack) {
        if(LocalDateTime.now().getMonth() == Month.APRIL && LocalDateTime.now().getDayOfMonth() == 1)
            return "RuiXuqi";
        else
            return I18n.format("tile.discerner.name");
    }
}
