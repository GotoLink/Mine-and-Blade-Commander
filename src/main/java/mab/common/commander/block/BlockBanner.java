package mab.common.commander.block;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mab.common.commander.EnumTeam;
import mab.common.commander.MBCommander;
import mab.common.commander.utils.CommonHelper;
import mab.common.commander.utils.TeamMap;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockBanner extends Block {
    private IIcon[] icons;
	public BlockBanner() {
		super(Material.cloth);
		this.setBlockName("commander:banner");
	    this.setBlockTextureName("commander:banner");
	    this.setCreativeTab(CreativeTabs.tabDecorations);
	}

    @Override
    public boolean hasTileEntity(int meta){
        return meta < EnumTeam.values().length;
    }
	
	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TileEntityBanner((byte)0, EnumTeam.values()[meta]);
	}
	
	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

    @Override
     public int getRenderType(){
            return -1;
     }

	@Override
	public boolean isOpaqueCube(){
		return false;
	}
	
	@Override
	public void getSubBlocks(Item itemID, CreativeTabs creativeTabs, List list){
		for (int i = 0; i < 16; ++i){
			list.add(new ItemStack(itemID, 1, i));
		}
	}
	
	/**
	  * For the banner only the metadata is used. Furthermore the image is only used for
	  * the image that appears in the inventory as the banners have a custom renderer & model
	  */
	 @Override
     @SideOnly(Side.CLIENT)
	 public IIcon getIcon(int side, int meta) {
		 return icons[meta];
	 }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        icons = new IIcon[16];
        for(int i = 0; i<icons.length;i++){
            icons[i] = reg.registerIcon(getTextureName()+"-"+i);
        }
    }
	 
	 @Override
	 public boolean canPlaceBlockAt(World world, int x, int y, int z)
	 {
		 if (y >= 255)
		 {
			 return false;
		 }
		    
		 return (world.getBlock(x, y - 1, z) != this && world.getBlock(x, y + 1, z) != this)
				 && (world.isAirBlock(x, y+1, z) || world.isAirBlock(x, y-1, z));
	 }
	 
	 /**
	  * Prevents creatures from spawning on top of banners
	  */
	 @Override
	 public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {
        return false;
	 }
	 

	@Override
	public int damageDropped(int i) {
		return i;
	}

	/**
	  * Will remove the other section of the banner when it is destroyed
	  */
	 @Override
	 public void breakBlock(World world, int x, int y, int z, Block p, int a){
		 
		 TileEntity tile = world.getTileEntity(x, y, z);

		 if(tile != null && tile instanceof TileEntityBanner){
			TileEntityBanner banner = (TileEntityBanner)tile;
			
			//if it is a base, remove the top
			if(banner.isBase()){
				world.setBlockToAir(x, y + 1, z);
				world.removeTileEntity(x, y + 1, z);
			}else{ // if not remove the base
				world.setBlockToAir(x, y - 1, z);
				world.removeTileEntity(x, y - 1, z);
			}
			
		 }
		 
		//remove self
		world.removeTileEntity(x, y, z);
		
		super.breakBlock(world, x, y, z, p, a);
	 }

	@Override
	public boolean onBlockActivated(World par1World, int x, int y,
			int z, EntityPlayer par5EntityPlayer, int par6, float par7,
			float par8, float par9) {
		
		if(par1World.isRemote){
			if(CommonHelper.isTeamGame())
				if(TeamMap.getInstance().isOnSameTeam(par5EntityPlayer, EnumTeam.values()[par1World.getBlockMetadata(x, y, z)]))
					par5EntityPlayer.openGui(MBCommander.INSTANCE, 0, par1World, x, y, z);
				else
					return false;
			else
				par5EntityPlayer.openGui(MBCommander.INSTANCE, 0, par1World, x, y, z);
		}
		
		return true;
	}

}
