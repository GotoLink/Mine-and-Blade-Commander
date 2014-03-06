package mab.common.commander.items;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mab.common.commander.MBCommander;
import mab.common.commander.npc.EntityMBUnit;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemTrumpet extends Item {

    private IIcon[] icons;
	public ItemTrumpet() {
		super();
		this.setCreativeTab(CreativeTabs.tabMisc);
		this.setUnlocalizedName("commander:trumpet");
		this.setTextureName("commander:trumpet");
		this.setFull3D();
	}

	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World,
			EntityPlayer par3EntityPlayer) {

		List<EntityMBUnit> surrounded = par2World.getEntitiesWithinAABB(EntityMBUnit.class, 
				AxisAlignedBB.getBoundingBox(par3EntityPlayer.posX - 15, par3EntityPlayer.posY - 15, par3EntityPlayer.posZ - 15,
                        par3EntityPlayer.posX + 15, par3EntityPlayer.posY + 15, par3EntityPlayer.posZ + 15));
		
		MBCommander.PROXY.selectAllUnitsAround(surrounded, par3EntityPlayer);
		MBCommander.PROXY.reparseOrderGUIOptions();
		
		return par1ItemStack;
	}
	
	@Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg){
        icons = new IIcon[8];
        for(int i = 0; i<icons.length; i++){
            icons[i] = reg.registerIcon(getIconString()+"-"+i);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int i){
        return icons[i];
    }

}
