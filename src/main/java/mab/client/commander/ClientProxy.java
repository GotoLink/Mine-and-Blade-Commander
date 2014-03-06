package mab.client.commander;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import mab.client.commander.block.TileEntityBannerRenderer;
import mab.client.commander.gui.GuiSpawnUpgrade;
import mab.client.commander.gui.order.GUIOrderMenu;
import mab.client.commander.npc.ModelNBUnitBody;
import mab.client.commander.npc.RendererMBUnit;
import mab.client.commander.utils.EntityGoToWaypoint;
import mab.client.commander.utils.ModelWaypoint;
import mab.client.commander.utils.RenderWaypoint;
import mab.common.commander.CommonProxy;
import mab.common.commander.block.TileEntityBanner;
import mab.common.commander.npc.EntityMBUnit;
import mab.common.commander.npc.ai.EnumOrder;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class ClientProxy extends CommonProxy{
	
	public static GUIOrderMenu orderMenu;
	
	private static List<EntityMBUnit> selectedUnits = new ArrayList<EntityMBUnit>();
	
	public static EntityGoToWaypoint waypoint;
	
	@Override
	public void resetSelectedUnits(){
		selectedUnits = new ArrayList<EntityMBUnit>();
		reparseOrderGUIOptions();
	}
	
	@Override
	public void reparseOrderGUIOptions() {
		if(orderMenu != null){
			if(orderMenu.isMainMenu()){
				if(selectedUnits.size() == 1){
					if(selectedUnits.get(0).getUpgrades().length > 0 && 
							(selectedUnits.get(0).getCurrentExperience() == 100 
										|| FMLClientHandler.instance().getClient().playerController.isInCreativeMode()))
						orderMenu.setOrders(new EnumOrder[]{EnumOrder.Follow, EnumOrder.StandGuard, EnumOrder.GoTo, EnumOrder.Upgrade});
					else
						orderMenu.setOrders(new EnumOrder[]{EnumOrder.Follow, EnumOrder.StandGuard, EnumOrder.GoTo});
				}else{
					orderMenu.setOrders(new EnumOrder[]{EnumOrder.Follow, EnumOrder.StandGuard, EnumOrder.GoTo});
				}
			}
		}
	}



	@Override
	public List<EntityMBUnit> getSelectedUnits() {
		return selectedUnits;
	}
	
	@Override
	public void registerRenderInformation(){
		
		RenderingRegistry.registerEntityRenderingHandler(EntityMBUnit.class, new RendererMBUnit(new ModelNBUnitBody(.1F), 0));
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBanner.class, new TileEntityBannerRenderer());
		
		
		RenderingRegistry.registerEntityRenderingHandler(EntityGoToWaypoint.class, new RenderWaypoint(new ModelWaypoint()));

        FMLCommonHandler.instance().bus().register(new MBRnderHandeler());
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		switch(ID){
			case 0:
				TileEntity tile = world.getTileEntity(x, y, z);
				if(tile != null && tile instanceof TileEntityBanner)
					return new GuiSpawnUpgrade((TileEntityBanner) tile, player);
				else
					return null;
			case 1:
				Entity unit = world.getEntityByID(x);
				if(unit != null && unit instanceof EntityMBUnit){
					return new GuiSpawnUpgrade((EntityMBUnit)unit, player);
				}
				
				return null;
			default:
				return null;
		}
	}

	public void removeWaypoint() {
		if(waypoint != null){
			waypoint.setDead();
			waypoint.worldObj.updateEntity(waypoint);
			waypoint = null;
			
		}
	}

	public void spawnAndMoveWaypoint() {
		MovingObjectPosition mouseOver = FMLClientHandler.instance().getClient().renderViewEntity.rayTrace(50, 0);
		
		if(mouseOver != null && mouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK){
			int[] point = findPoints(mouseOver, FMLClientHandler.instance().getClient().theWorld );
			if(point==null){
				removeWaypoint();
				return;
			}
			
			if(waypoint == null){
				waypoint = new EntityGoToWaypoint(FMLClientHandler.instance().getClient().theWorld);
				FMLClientHandler.instance().getClient().theWorld.spawnEntityInWorld(waypoint);
			}
			
			waypoint.setPosition(point[0]+.5F,point[1], point[2]+.5F);
			
		}else
			removeWaypoint();

	}

	private int[] findPoints(MovingObjectPosition mouseOver, World world) {
		for(int i = 0; i < 20; i++){
			if(
				!world.blockExists(mouseOver.blockX, mouseOver.blockY+i, mouseOver.blockZ) || 
					world.isAirBlock(mouseOver.blockX, mouseOver.blockY+i, mouseOver.blockZ)||
					world.getBlock(mouseOver.blockX, mouseOver.blockY+i, mouseOver.blockZ)
						.getCollisionBoundingBoxFromPool(world, mouseOver.blockX, mouseOver.blockY+1+i, mouseOver.blockZ) == null){
				
				if(!world.blockExists(mouseOver.blockX, mouseOver.blockY+1+i, mouseOver.blockZ) || 
						world.isAirBlock(mouseOver.blockX, mouseOver.blockY+1+i, mouseOver.blockZ) ||
						world.getBlock(mouseOver.blockX, mouseOver.blockY+1+i, mouseOver.blockZ)
						.getCollisionBoundingBoxFromPool(world, mouseOver.blockX, mouseOver.blockY+1+i, mouseOver.blockZ) == null){
						return new int[]{mouseOver.blockX, mouseOver.blockY+i, mouseOver.blockZ};
					}
			}
			
			
		}
		return null;
	}

	@Override
	public void selectAllUnitsAround(List<EntityMBUnit> units, EntityPlayer player) {
		for (EntityMBUnit entityMBUnit : units) {
			if(! selectedUnits.contains(entityMBUnit) && !entityMBUnit.isEnemy(player, false))
				selectedUnits.add(entityMBUnit);
		}
		
		if(orderMenu == null)
			orderMenu = new GUIOrderMenu(FMLClientHandler.instance().getClient());
		orderMenu.show();
	}

}
