package mab.common.commander.commands;

import java.util.Iterator;
import java.util.Random;

import mab.common.commander.EnumTeam;
import mab.common.commander.utils.TeamMap;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;

public class MBStartGame extends CommandBase {

	@Override
	public String getCommandName() {
		return "mb-start";
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		if(shouldStart()){
			long id = System.currentTimeMillis();
			EnumTeam[] teams = new EnumTeam[]{EnumTeam.red, EnumTeam.yellow};
			TeamMap.resetInstance(id, teams);
			TeamMap teamMap = TeamMap.getInstance();
			
			Iterator<EntityPlayer> it = MinecraftServer.getServer().getConfigurationManager().playerEntityList.iterator();
			System.out.println("Mine & Blade: "+MinecraftServer.getServer().getConfigurationManager().getCurrentPlayerCount()+" players in "+teams.length+" teams");
			int teamCounter = new Random().nextInt(2);
			while(it.hasNext()){
				EntityPlayer player = it.next();
				System.out.println(player.getCommandSenderName()+": "+teams[teamCounter]);
				teamMap.addPlayerToTeam(player, teams[teamCounter]);
				player.addChatMessage(new ChatComponentTranslation("mb.gameStart-" + teams[teamCounter].name()));
				teamCounter++;
				if(teamCounter == teams.length)
					teamCounter=0;
			}
			
			
		}else{
			var1.addChatMessage(new ChatComponentTranslation("mb.gameFailStart"));
		}
	}
	
	protected void sendNotificationToPlayer(EntityPlayer player, EnumTeam team){
		player.addChatMessage(new ChatComponentTranslation("mb.gameStart-" + team.toString()));
	}
	
	protected boolean shouldStart(){
		return (!TeamMap.getInstance().isGameActive());	}
	
    @Override
    public int getRequiredPermissionLevel(){
    	//only alow OPs to use command on servers
        return 3;
    }

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender par1iCommandSender) {
		if(MinecraftServer.getServer().isDedicatedServer())
			return super.canCommandSenderUseCommand(par1iCommandSender);
		else
			return true;
	}
    
    

    
}
