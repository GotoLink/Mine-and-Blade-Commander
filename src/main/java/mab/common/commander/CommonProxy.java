package mab.common.commander;

import java.util.List;

import cpw.mods.fml.common.FMLCommonHandler;
import mab.common.commander.npc.EntityMBUnit;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class CommonProxy implements IGuiHandler{
	
	public void registerRenderInformation(){}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		return null;
	}
	
	
	public void scanForGold(InventoryPlayer inv, int required){
		
		//loop 1 - gold nuggets
		required = scanForNuggets(inv, required);
		
		//loop2 - goldIngots
		if(required > 0){
			expandIngots(inv, required / 9 + 1);
			required = scanForNuggets(inv, required);
			
			if(required > 0){
				expandBlocks(inv, required / (9*9) + 1);
				expandIngots(inv, required / 9 + 1);
				required = scanForNuggets(inv, required);
			}
		}
		
	}
	
	private void expandBlocks(InventoryPlayer inv, int expands) {
		int expandsNeeded = expands;
		for(int i = 0; i < inv.mainInventory.length && expandsNeeded > 0; i++){
			ItemStack stack = inv.mainInventory[i];
			if(stack != null && stack.getItem() == Item.getItemFromBlock(Blocks.gold_block)){
				if(stack.stackSize > expandsNeeded){
					stack.stackSize = stack.stackSize - expandsNeeded;
					inv.addItemStackToInventory(new ItemStack(Items.gold_ingot, expandsNeeded*9));
					expandsNeeded = 0;
				}else if(stack.stackSize == expandsNeeded){
					inv.addItemStackToInventory(new ItemStack(Items.gold_ingot, expandsNeeded*9));
					expandsNeeded = 0;
					inv.mainInventory[i] = null;
				}else{
					inv.addItemStackToInventory(new ItemStack(Items.gold_ingot, stack.stackSize*9));
					expandsNeeded = expandsNeeded - stack.stackSize;
					inv.mainInventory[i] = null;
				}
			}
		}
	}

	private void expandIngots(InventoryPlayer inv, int expands) {
		int expandsNeeded = expands;
		for(int i = 0; i < inv.mainInventory.length && expandsNeeded > 0; i++){
			ItemStack stack = inv.mainInventory[i];
			if(stack != null && stack.getItem() == Items.gold_ingot){
				if(stack.stackSize > expandsNeeded){
					stack.stackSize = stack.stackSize - expandsNeeded;
					inv.addItemStackToInventory(new ItemStack(Items.gold_nugget, expandsNeeded*9));
					expandsNeeded = 0;
				}else if(stack.stackSize == expandsNeeded){
					inv.addItemStackToInventory(new ItemStack(Items.gold_nugget, expandsNeeded*9));
					expandsNeeded = 0;
					inv.mainInventory[i] = null;
				}else{
					inv.addItemStackToInventory(new ItemStack(Items.gold_nugget, stack.stackSize*9));
					expandsNeeded = expandsNeeded - stack.stackSize;
					inv.mainInventory[i] = null;
				}
			}
		}
		
	}

	private int scanForNuggets(InventoryPlayer inv, int required){
		for(int i = 0; i < inv.mainInventory.length && required > 0; i++){
			ItemStack stack = inv.mainInventory[i];
			if(stack != null && stack.getItem() == Items.gold_nugget){
				if(stack.stackSize > required){
					stack.stackSize = stack.stackSize - required;
					required = 0;
				}else if (stack.stackSize == required){
					required = 0;
					inv.mainInventory[i] = null;
				}else{
					required = required - stack.stackSize;
					inv.mainInventory[i] = null;
				}
			}
		}
		return required;
	}

	public void resetSelectedUnits() {}

	public List<EntityMBUnit> getSelectedUnits() {
		return null;
	}

	public void reparseOrderGUIOptions() {
		// TODO Auto-generated method stub
		
	}

	public void selectAllUnitsAround(List<EntityMBUnit> surrounded, EntityPlayer player) {}

	public void registerPlayerTracker() {
		FMLCommonHandler.instance().bus().register(new MBPlayerTracker());
	}

}

