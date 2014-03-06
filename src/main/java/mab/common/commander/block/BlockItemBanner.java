package mab.common.commander.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mab.common.commander.EnumTeam;
import mab.common.commander.MBCommander;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockItemBanner extends ItemBlock {
	
	 public BlockItemBanner(Block block)
	 {
		 super(block);
		 this.setCreativeTab(CreativeTabs.tabDecorations);
		 this.setHasSubtypes(true);
	 }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int par1)
    {
        return MBCommander.INSTANCE.banner.getIcon(2, par1);
    }

	public int getPlacedBlockMetadata(int i){
		return i;
	}
	    
	@Override
	public String getUnlocalizedName(ItemStack itemstack){
		return (new StringBuilder()).append(super.getUnlocalizedName())
				.append(".").
				append(EnumTeam.values()[itemstack.getItemDamage()].name()).toString();
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z,
			int side, float hitX, float hitY, float hitZ, int i) {
		
		if(side == 1){ // on top of a block
			if(world.isAirBlock(x, y+1, z))
			{
				boolean placed = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ,  i);
				
				world.setBlock(x, y + 1, z, MBCommander.INSTANCE.banner, stack.getItemDamage(), 2);
				
				float angle = player.rotationYaw + 45+180;
				while(angle < 0)
					angle =angle+360;
				while (angle>360)
					angle = angle-360;
				
				byte state = (byte) (angle / 45);
				world.setTileEntity(x, y, z, new TileEntityBanner(state, EnumTeam.values()[stack.getItemDamage()]));
				world.setTileEntity(x, y + 1, z, new TileEntityBanner((byte) (state + 8), EnumTeam.values()[stack.getItemDamage()]));

				return placed;
			}else
				return false;
		}else if (side != 0){
			
		}
		return false;
		
	}

	@Override
	public int getMetadata(int par1) {
		return par1;
	}
}
