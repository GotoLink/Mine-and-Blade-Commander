package mab.common.commander;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import mab.common.commander.block.TileEntityBanner;
import mab.common.commander.npc.EntityMBUnit;
import mab.common.commander.npc.EnumUnits;
import mab.common.commander.npc.ai.EnumOrder;
import mab.common.commander.utils.CommonHelper;
import mab.common.commander.utils.TeamPacketHandeler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;

import java.util.HashMap;

public class CommanderPacketHandeler {
    public static String BannerPacket = "MBc|Banner";
    public static String spawnPacket = "MBc|Spawn";
    public static String upgradePacket = "MBc|Upgrade";
    public static String orderPacket = "MBc|Order";
    public static String particlePacket = "MBc|Particle";
    public static String teamPacket = "MBc|Team";
	public static String[] CHANNELS = {BannerPacket, spawnPacket, upgradePacket, orderPacket, particlePacket, teamPacket};
    public static HashMap<String, FMLEventChannel> eventChannel = new HashMap<String, FMLEventChannel>();

	@SubscribeEvent
	public void onPacketData( FMLProxyPacket packet) {
		
		EntityPlayer entityPlayer = ((EntityPlayer)player);
	
		if(packet.channel().equals(BannerPacket)){
			
			ByteBuf buf = packet.payload();
			try {
				entityPlayer.worldObj.setTileEntity(
                        buf.readInt(), buf.readInt(), buf.readInt(),
                        new TileEntityBanner(buf.readByte(), EnumTeam.values()[buf.readByte()]));
				
			}catch(Exception e){
				e.printStackTrace();
			}
			
			
		}else if (packet.channel().equals(spawnPacket)){
			
			EntityMBUnit unit = readUnitFromPacket(packet, entityPlayer.worldObj, entityPlayer);
			entityPlayer.worldObj.spawnEntityInWorld(unit);

			
		}else if (packet.channel().equals(upgradePacket)){
			readAndProcessUpgradePacket(packet, entityPlayer.worldObj, entityPlayer);
		}else if(packet.channel().equals(orderPacket)){
			
			ByteBuf buf = packet.payload();
			try {
				
				int playerID = buf.readInt();
				EnumOrder order = EnumOrder.values()[buf.readByte()];
				byte size = buf.readByte();
				
				
				int[] data = new int[3];
				String stringData = "";
				
				EntityMBUnit[] units = new EntityMBUnit[size];
				for(int i = 0; i < size; i++){
					
					Entity e = entityPlayer.worldObj.getEntityByID(buf.readInt());
					
					if(e != null && e instanceof EntityMBUnit){
						units[i] = (EntityMBUnit)e;
					}else{
						System.out.println("Mine & Blade: Unexpected Entity ID in Order Packet");
					}
					
				}
				
				if(order == EnumOrder.GoTo){
					
					for(int i = 0; i < 3; i++)
						data[i] = buf.readInt();
					CommonHelper.findAndSetGotoPos(units, data);

					units[0].setOrder(EnumOrder.StandGuard, data, "");
					
				}else if(order == EnumOrder.TargetDist){
					for(int i = 0; i < size; i++){
						units[i].setTargetDistance(data[0]);
						units[i].setAttackTarget(null);
					}
				}else{
					for(int i = 0; i < size; i++){
						
						order.setOrderFromPacketData(units[i], entityPlayer);
					}
				}
				
				
			}catch(Exception e){
				e.printStackTrace();
			}			
		}else if (packet.channel().equals(particlePacket)){
			readAndProcessParticlerPacket(packet, entityPlayer, entityPlayer.worldObj);
		}else if(packet.channel().equals(teamPacket)){
			TeamPacketHandeler.readAndProcessTeamPacket(packet, entityPlayer);
		}
		
	}
	
	private void readAndProcessParticlerPacket(FMLProxyPacket p,
			EntityPlayer entityPlayer, World world) {
		ByteBuf buf = p.payload();
		try{
			
			byte type = buf.readByte();
			
			switch (type) {
			case 0: //spawn effect
				Entity e = world.getEntityByID(buf.readInt());
				if(e != null){
					 for (int var1 = 0; var1 < 20; ++var1)
			            {
			                double var8 = world.rand.nextGaussian() * 0.02D;
			                double var4 = world.rand.nextGaussian() * 0.02D;
			                double var6 = world.rand.nextGaussian() * 0.02D;
			                world.spawnParticle("explode", 
			                		e.posX + (double)(world.rand.nextFloat() * e.width * 2.0F) - 
			                		(double)e.width, e.posY + (double)(world.rand.nextFloat() * e.height),
			                		e.posZ + (double)(world.rand.nextFloat() * e.width * 2.0F) - (double)e.width, var8, var4, var6);
			            }
				}
				 
				break;
			default:
				break;
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	public static FMLProxyPacket generateUpgradePacket(EntityMBUnit previousUnit, EntityMBUnit unit, EntityPlayer player){
		ByteBuf buf = Unpooled.buffer();
		try {

            buf.writeInt(previousUnit.entityId);
            buf.writeByte((byte)unit.getUnitType().ordinal());
			for(int i = 0; i < 6; i++){
                buf.writeByte(unit.getOption(i));
			}
            buf.writeInt(player.entityId);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	
		return new FMLProxyPacket(buf, upgradePacket);
	}
	
	public static void readAndProcessUpgradePacket(FMLProxyPacket p, World world, EntityPlayer player){
		if(p.channel().equals(upgradePacket)){

			ByteBuf buf = p.payload();
			try{
				
				Entity e = world.getEntityByID(buf.readInt());
				if(e != null && e instanceof EntityMBUnit){
					EntityMBUnit previous = (EntityMBUnit)e;
					
					EntityMBUnit unit = EntityMBUnit.generateUnit(world,previous.getTeam(), EnumUnits.values()[buf.readByte()]);
					
					unit.setLocationAndAngles(
							previous.posX,
							previous.posY, 
							previous.posZ, 
							previous.rotationYaw,
							previous.rotationPitch);
					
					unit.setOrder(previous.getOrder(), previous.getOrderData(), previous.getOrderStringData());
					
					unit.setBaseMorale(previous.getBaseMorale());
					
					previous.setDead();
					
					sendPacketToAllAround(unit.posX, unit.posY, unit.posZ, 30, unit.dimension, generateParticleEffectPacket((byte) 0, previous));

					for(int i = 0; i < 6; i++){
						unit.setOption(i, buf.readByte());
					}
					
					
					e = world.getEntityByID(buf.readInt());
					if(e instanceof EntityPlayer && !((EntityPlayer)e).capabilities.isCreativeMode){
						InventoryPlayer inv = ((EntityPlayer)e).inventory;
						int required = unit.getCost();
						MBCommander.PROXY.scanForGold(inv,required);
					}
					
					unit.setOwner(previous.getOwner());
					
					world.spawnEntityInWorld(unit);
					
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	
	private static FMLProxyPacket generateParticleEffectPacket(byte i, Entity centerEntity) {
		ByteBuf buf = Unpooled.buffer();
		try {
            buf.writeByte(i);
            buf.writeInt(centerEntity.entityId);
			
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return new FMLProxyPacket(buf, particlePacket);
	}

	public static FMLProxyPacket generateSpawnPacket(EntityMBUnit unit, int x, int y, int z, EntityPlayer player){
		
		ByteBuf buf = Unpooled.buffer();
		try {
            buf.writeInt(unit.entityId);
            buf.writeInt(x);
            buf.writeInt(y);
            buf.writeInt(z);

            buf.writeByte((byte)((int)((player.rotationYaw * 256.0F) / 360.0F)));
            buf.writeByte((byte)((int)(unit.rotationPitch * 256.0F / 360.0F)));

            buf.writeByte((byte)unit.getTeam().ordinal());
            buf.writeByte((byte)unit.getUnitType().ordinal());
			
			for(int i = 0; i < 6; i++){
                buf.writeByte(unit.getOption(i));
			}
            buf.writeInt(player.entityId);
		
		}catch (Exception e) {
			e.printStackTrace();
		}
	
		return new FMLProxyPacket(buf, spawnPacket);
	}
	
	public static EntityMBUnit readUnitFromPacket(FMLProxyPacket p, World world, EntityPlayer player){
		if(p.channel().equals(spawnPacket)){

			ByteBuf buf = p.payload();
			
			try{
				int id = buf.readInt();
				int x = buf.readInt();
				int y = buf.readInt();
				int z = buf.readInt();
				byte pitch = buf.readByte();
				byte yaw = buf.readByte();

				world.removeTileEntity(x, y, z);
				world.removeTileEntity(x, y + 1, z);
				
				world.setBlockToAir(x, y, z);
				world.setBlockToAir(x, y + 1, z);
				
				EntityMBUnit unit = EntityMBUnit.generateUnit(world, EnumTeam.values()[buf.readByte()], EnumUnits.values()[buf.readByte()]);
				
				unit.setLocationAndAngles(
						(double)x+.5F,
						(double)y, 
						(double)z+.5F, 
						player.rotationYaw,
						player.rotationPitch);
				
				unit.setOrder(EnumOrder.StandGuard, new int[]{x,y,z}, "");
				
				for(int i = 0; i < 6; i++){
					unit.setOption(i, buf.readByte());
				}
				
				Entity e = world.getEntityByID(buf.readInt());
				if(e instanceof EntityPlayer && !((EntityPlayer)e).capabilities.isCreativeMode){
					InventoryPlayer inv = ((EntityPlayer)e).inventory;
					int required = unit.getCost();
					MBCommander.PROXY.scanForGold(inv,required);
				}
				unit.setOwner(player.getCommandSenderName());
				
				return unit;
			}catch(Exception e){
				e.printStackTrace();
				return null;
			}
		}else
			return null;
		
	}

    public static void sendPacketToAllAround(double x, double y, double z, int range, int dim, FMLProxyPacket packet){
        eventChannel.get(packet.channel()).sendToAllAround(packet, new NetworkRegistry.TargetPoint(dim, x, y, z, range));
    }

    public static void sendPacketToServer(FMLProxyPacket packet){
        eventChannel.get(packet.channel()).sendToServer(packet);
    }

    public static void sendPacketToPlayer(FMLProxyPacket packet, EntityPlayerMP entity){
        eventChannel.get(packet.channel()).sendTo(packet, entity);
    }
}
