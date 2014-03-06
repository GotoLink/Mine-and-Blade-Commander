package mab.common.commander.commands;

import java.util.Iterator;

import mab.common.commander.EnumTeam;
import mab.common.commander.npc.EntityMBUnit;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;

public class MBRestartGame extends MBStartGame{

	@Override
	public String getCommandName() {
		return "mb-restart";
	}
	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		for(int i = 0; i < MinecraftServer.getServer().worldServers.length; i++){
			Iterator<Entity> it = MinecraftServer.getServer().worldServers[i].loadedEntityList.iterator();
			while(it.hasNext()){
				Entity next = it.next();
				if(next instanceof EntityMBUnit){
					((EntityMBUnit)next).setAttackTarget(null);
				}
			}
		}
		super.processCommand(var1, var2);
	}

	@Override
	protected boolean shouldStart() {
		return true;
	}
	
	protected void sendNotificationToPlayer(EntityPlayer player, EnumTeam team){
		player.addChatMessage(new ChatComponentTranslation("mb.gameRestart-"+team.toString()));
	}


}
