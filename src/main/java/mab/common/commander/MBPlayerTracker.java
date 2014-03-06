package mab.common.commander;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import mab.common.commander.block.BlockItemBanner;
import mab.common.commander.utils.CommonHelper;
import mab.common.commander.utils.TeamMap;
import net.minecraft.util.ChatComponentTranslation;

public class MBPlayerTracker {

	@SubscribeEvent
	public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
		if(!event.player.worldObj.isRemote){
			
			TeamMap teamMap = TeamMap.getInstance();
			
			if(teamMap.isGameActive() && teamMap.getTeams().length > 0){
				System.out.println("Game Active");
				EnumTeam playerTeam = teamMap.getTeamForPlayer(event.player);
				if(playerTeam == null){
					playerTeam = teamMap.getTeamWithMinPlayers();
					teamMap.addPlayerToTeam(event.player, playerTeam);
					
					System.out.println(event.player.getCommandSenderName()+" entered world, assigning "+playerTeam.name());
                    event.player.addChatMessage(new ChatComponentTranslation("mb.gameJoin-" + playerTeam.name()));
				}
				
			}
		}
	}

    @SubscribeEvent
    public void onCrafting(PlayerEvent.ItemCraftedEvent event) {
        if(event.crafting.getItem() instanceof BlockItemBanner && CommonHelper.isTeamGame()){
            event.crafting.setItemDamage(TeamMap.getInstance().getTeamForPlayer(event.player).ordinal());
        }
    }
}
