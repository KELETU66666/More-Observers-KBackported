package com.stebars.moreobserversmod;

import com.stebars.moreobserversmod.blocks.DiscernerBlock;
import com.stebars.moreobserversmod.blocks.MobserverBlock;
import com.stebars.moreobserversmod.blocks.SurveyorBlock;
import com.stebars.moreobserversmod.blocks.ToggleObserverBlock;
import com.stebars.moreobserversmod.blocks.itemBlock.ItemBlockDiscerner;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@Mod(modid = MoreObserversMod.MOD_ID, name = MoreObserversMod.MOD_NAME)
@Mod.EventBusSubscriber(modid = MoreObserversMod.MOD_ID)
public class MoreObserversMod {
	public final static String MOD_ID = "moreobservers";
	public final static String MOD_NAME = "More Observers Kedition";

	public static final Block DISCERNER_BLOCK = new DiscernerBlock().setHardness(3.0F).setRegistryName(new ResourceLocation(MOD_ID, "discerner")).setTranslationKey("discerner").setTickRandomly(true);
	public static final Item DISCERNER_ITEM = new ItemBlockDiscerner().setCreativeTab(CreativeTabs.REDSTONE).setRegistryName(new ResourceLocation(MOD_ID, "discerner"));

	public static final Block TOGGLE_OBSERVER_BLOCK = new ToggleObserverBlock().setHardness(3.0F).setRegistryName(new ResourceLocation(MOD_ID, "toggle_observer")).setTranslationKey("toggle_observer");
	public static final Item TOGGLE_OBSERVER_ITEM = new ItemBlock(TOGGLE_OBSERVER_BLOCK).setCreativeTab(CreativeTabs.REDSTONE).setRegistryName(new ResourceLocation(MOD_ID, "toggle_observer"));

	public static final Block MOBSERVER_BLOCK = new MobserverBlock().setHardness(3.0F).setRegistryName(new ResourceLocation(MOD_ID, "mobserver")).setTranslationKey("mobserver");
	public static final Item MOBSERVER_ITEM = new ItemBlock(MOBSERVER_BLOCK).setCreativeTab(CreativeTabs.REDSTONE).setRegistryName(new ResourceLocation(MOD_ID, "mobserver"));

	public static final Block SURVEYOR_BLOCK = new SurveyorBlock().setHardness(3.0F).setRegistryName(new ResourceLocation(MOD_ID, "surveyor")).setTranslationKey("surveyor").setTickRandomly(true);
	public static final Item SURVEYOR_ITEM = new ItemBlock(SURVEYOR_BLOCK).setCreativeTab(CreativeTabs.REDSTONE).setRegistryName(new ResourceLocation(MOD_ID, "surveyor"));


	public MoreObserversMod() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public static void onBlocksRegistry(final RegistryEvent.Register<Block> event) {
		event.getRegistry().registerAll(DISCERNER_BLOCK, TOGGLE_OBSERVER_BLOCK, MOBSERVER_BLOCK, SURVEYOR_BLOCK);
	}

	@SubscribeEvent
	public static void onItemsRegistry(final RegistryEvent.Register<Item> event) {
		event.getRegistry().registerAll(DISCERNER_ITEM, TOGGLE_OBSERVER_ITEM, MOBSERVER_ITEM, SURVEYOR_ITEM);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void modelRegistryEvent(ModelRegistryEvent event) {
		ModelLoader.setCustomStateMapper(DISCERNER_BLOCK, new StateMap.Builder().ignore(DiscernerBlock.POWER).build());
		ModelLoader.setCustomStateMapper(TOGGLE_OBSERVER_BLOCK, new StateMap.Builder().ignore(ToggleObserverBlock.TRIGGERED).build());
		ModelLoader.setCustomStateMapper(SURVEYOR_BLOCK, new StateMap.Builder().ignore(SurveyorBlock.POWER).build());

		ModelLoader.setCustomModelResourceLocation(DISCERNER_ITEM, 0, new ModelResourceLocation(DISCERNER_ITEM.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(TOGGLE_OBSERVER_ITEM, 0, new ModelResourceLocation(TOGGLE_OBSERVER_ITEM.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(MOBSERVER_ITEM, 0, new ModelResourceLocation(MOBSERVER_ITEM.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(SURVEYOR_ITEM, 0, new ModelResourceLocation(SURVEYOR_ITEM.getRegistryName(), "inventory"));
	}
}
