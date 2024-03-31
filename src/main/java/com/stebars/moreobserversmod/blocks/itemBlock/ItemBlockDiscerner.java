package com.stebars.moreobserversmod.blocks.itemBlock;

import com.stebars.moreobserversmod.MoreObserversMod;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.time.Month;

public class ItemBlockDiscerner extends ItemBlock {
    public ItemBlockDiscerner() {
        super(MoreObserversMod.DISCERNER_BLOCK);

        this.addPropertyOverride(new ResourceLocation("special"), new IItemPropertyGetter() {
            @Override
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
                return  LocalDateTime.now().getMonth() == Month.APRIL && LocalDateTime.now().getDayOfMonth() == 1 ? 1 : 0;
            }
        });
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getItemStackDisplayName(ItemStack stack) {
        if(LocalDateTime.now().getMonth() == Month.APRIL && LocalDateTime.now().getDayOfMonth() == 1)
            return I18n.format("tile.discerner.special");
        else
            return I18n.format("tile.discerner.name");
    }
}
