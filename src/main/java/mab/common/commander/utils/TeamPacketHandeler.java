package mab.common.commander.utils;

import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import mab.common.commander.CommanderPacketHandeler;
import mab.common.commander.EnumTeam;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class TeamPacketHandeler {
	
	public enum TeamPacketType{
		SendTeam,
		SelectTeamReply,
		UpdateListRequest,
		UpdateListReply
	}
	
	public static class PlayerIDTeam{
		int id;
		byte teamId;
		public PlayerIDTeam(EntityPlayer player, EnumTeam team){
			id = player.entityId;
			if(team == null)
				teamId = -1;
			else
				teamId = (byte)team.ordinal();
		}
		public Entity getPlayer(World world){
			return world.getEntityByID(id);
		}
		public EnumTeam getTeam(){
			if(teamId == -1)
				return null;
			else
				return EnumTeam.values()[teamId];
		}
	}
	
	public static void readAndProcessTeamPacket(FMLProxyPacket packet, EntityPlayer entityPlayer){
		
		ByteBuf buf = packet.payload();
		try {
			
			TeamPacketType type = TeamPacketType.values()[buf.readByte()];
			
			switch(type){
			case SendTeam:
				if(entityPlayer.worldObj.isRemote){
					Entity e = entityPlayer.worldObj.getEntityByID(buf.readInt());
					if(e instanceof EntityPlayer){
						TeamMap.getInstance().setTeamForPlayer(((EntityPlayer)e), EnumTeam.values()[buf.readByte()]);
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static FMLProxyPacket generateListPacket(World world) {
		
		FMLProxyPacket packet = null;
		
		PlayerIDTeam[] list = TeamMap.getInstance().generateList(world);
		if(list.length > 0){
			
			ByteBuf buf = Unpooled.buffer();
			
			try {
                buf.writeByte((byte)TeamPacketType.UpdateListReply.ordinal());
				for (PlayerIDTeam playerIDTeam : list) {
                    buf.writeInt(playerIDTeam.id);
                    buf.writeByte(playerIDTeam.teamId);
				}
				packet = new FMLProxyPacket(buf, CommanderPacketHandeler.teamPacket);
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		return packet;
	}

	
}
