package mab.client.commander;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.input.Keyboard;

import mab.client.commander.gui.order.GUIOrderMenu;
import mab.client.commander.utils.MBClientHelper;
import mab.common.commander.EnumTeam;
import mab.common.commander.MBCommander;
import mab.common.commander.npc.EntityMBUnit;
import mab.common.commander.npc.ai.EnumOrder;
import mab.common.commander.utils.TeamMap;
import net.minecraft.client.Minecraft;

import cpw.mods.fml.client.FMLClientHandler;

import java.util.List;

public class MBRnderHandeler {

    public static final KeyBinding selectUnit = new KeyBinding("Select Unit", Keyboard.KEY_X, "commander.menu");
    public static final KeyBinding up = new KeyBinding("Menu Up", Keyboard.KEY_UP, "commander.menu");
    public static final KeyBinding altDown = new KeyBinding("Order Menu Navigate", Keyboard.KEY_Z, "commander.menu");
    public static final KeyBinding down = new KeyBinding("Menu Down", Keyboard.KEY_DOWN, "commander.menu");
    public static final KeyBinding cancel = new KeyBinding("Cancel", Keyboard.KEY_C, "commander.menu");
    public static final KeyBinding selectOrder = new KeyBinding("Select Order", Keyboard.KEY_RETURN, "commander.menu");
    public static Minecraft mc;

    public MBRnderHandeler(){
        for(KeyBinding key:new KeyBinding[]{selectUnit, up, down, altDown, cancel, selectOrder}){
            ClientRegistry.registerKeyBinding(key);
        }
    }

	@SubscribeEvent
	public void tickEnd(TickEvent.ClientTickEvent event) {
        if(event.phase == TickEvent.Phase.END){
            if(mc==null)
                mc = FMLClientHandler.instance().getClient();
            if(mc.currentScreen != null)
                MBCommander.PROXY.resetSelectedUnits();

            GUIOrderMenu menu = getOrderMenu();
            if(selectUnit.getIsKeyPressed()){
                List<EntityMBUnit> selected = MBCommander.PROXY.getSelectedUnits();
                EntityMBUnit unit = MBClientHelper.getUnitMouseOver(5, 0, mc.thePlayer);

                if(unit != null){
                    if(!selected.contains(unit)){
                        selected.add(unit);
                        MBCommander.PROXY.reparseOrderGUIOptions();
                        menu.show();
                    }else{
                        selected.remove(unit);
                        MBCommander.PROXY.reparseOrderGUIOptions();
                        if(selected.isEmpty())
                            menu.hide();
                    }
                }else{
                    //MBCommander.PROXY.resetSelectedUnits();
                    //menu.hide();
                }
            }else if (up.getIsKeyPressed()){
                if(menu.isDisplayed() || menu.isShowing()){
                    menu.moveSelectionUp();
                }
            }else if (down.getIsKeyPressed() || altDown.getIsKeyPressed()){
                if(menu.isDisplayed() || menu.isShowing()){
                    menu.moveSelectionDown();
                }
            }else if (selectOrder.getIsKeyPressed()){
                menu.applySelectedOrder(MBCommander.PROXY.getSelectedUnits(), mc.thePlayer);
            }else if (cancel.getIsKeyPressed()){
                MBCommander.PROXY.resetSelectedUnits();
                menu.hide();
            }
        }
    }

    @SubscribeEvent
    public void tickEnd(TickEvent.RenderTickEvent event) {
		if(event.phase == TickEvent.Phase.END){
			GUIOrderMenu orderMenu = ((ClientProxy)MBCommander.PROXY).orderMenu;
            if(mc==null)
                mc = FMLClientHandler.instance().getClient();
			if(orderMenu != null && mc.currentScreen == null){
				orderMenu.renderOverlayRightBox();
				if(! orderMenu.isMainMenu() && orderMenu.getCurrentMenu()[0].equals(EnumOrder.GoToSelect)){
					((ClientProxy)MBCommander.PROXY).spawnAndMoveWaypoint();
				}else{
					((ClientProxy)MBCommander.PROXY).removeWaypoint();
					orderMenu.setSubMenu(null);
				}
			}else{
				((ClientProxy)MBCommander.PROXY).removeWaypoint();
				if(orderMenu!=null)
					orderMenu.setSubMenu(null);
			}
			
			MovingObjectPosition mouseOver = MBClientHelper.getMouseOver(5, 0);
			if(mouseOver != null && mouseOver.entityHit instanceof EntityMBUnit &&
					 FMLClientHandler.instance().getClient().currentScreen == null &&
						!((EntityMBUnit)mouseOver.entityHit).isEnemy(mc.thePlayer, false)){
				String message = StatCollector.translateToLocal("gui.screen.select");
				message = message.replaceAll("@key@", Keyboard.getKeyName(selectUnit.getKeyCode()));
				message = message.replaceAll("@unit@", StatCollector.translateToLocal(((EntityMBUnit) mouseOver.entityHit).getUnitName()));

				ScaledResolution sr = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
	            int width = sr.getScaledWidth();
	            int height = sr.getScaledHeight();
				mc.fontRenderer.drawStringWithShadow(message, width/2 - mc.fontRenderer.getStringWidth(message) / 2, height/2 + 30, 0xFFFFFF);
			}
			
			if(mc.currentScreen == null){
				ScaledResolution sr = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
	            int width = sr.getScaledWidth();
	            int height = sr.getScaledHeight();
	            
	            EnumTeam team = TeamMap.getInstance().getTeamForPlayer(mc.thePlayer);
	            if(team != null){
		            int index = TeamMap.getInstance().getTeamForPlayer(mc.thePlayer).ordinal();
		            
		            int x = index%8*32;
		            int y = index/8*32 + 32*6;
		            
		            mc.renderEngine.bindTexture(new ResourceLocation(MBCommander.IMAGE_FOLDER + "BigItemSheet.png"));
		            FMLClientHandler.instance().getClient().ingameGUI.drawTexturedModalRect(width - 5- 32, height-5-32, x, y, 32, 32);
	            }
			}
			
		}
	}

    private void handelSelect() {
        GUIOrderMenu orderMenu = getOrderMenu();
    }

    private GUIOrderMenu getOrderMenu(){
        if(((ClientProxy)MBCommander.PROXY).orderMenu == null){
            ((ClientProxy)MBCommander.PROXY).orderMenu = new GUIOrderMenu(mc);
        }
        return ((ClientProxy)MBCommander.PROXY).orderMenu;
    }

}
