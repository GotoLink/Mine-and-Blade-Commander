package mab.common.commander.utils;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import mab.common.commander.CommanderPacketHandeler;
import mab.common.commander.EnumTeam;
import mab.common.commander.npc.EntityMBUnit;
import mab.common.commander.npc.ai.MBEntityAIAttackMelee;
import mab.common.commander.utils.TeamMap;
import mab.common.commander.utils.TeamPacketHandeler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringTranslate;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.SaveHandler;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.world.WorldEvent;

public class MBCommanderForgeEvents {
	//private static MBNearestAttackableSorter a;
	
	@SubscribeEvent
	public void livingSpawnEvent(EntityJoinWorldEvent event){
		
		if(event.entity instanceof IMob){
			EntityLiving living = (EntityLiving)event.entity;
			
			if(living != null){
				
				if(living.isAIEnabled()){
					if(living instanceof IRangedAttackMob){
						living.targetTasks.addTask(2, new EntityAINearestAttackableTarget(living, EntityMBUnit.class, 16.0F, 0, true));
					}else if(living instanceof EntityCreeper){
						
					}else{
						living.tasks.addTask(2, new MBEntityAIAttackMelee(living, .3F, true));
						living.targetTasks.addTask(2, new EntityAINearestAttackableTarget(living, EntityMBUnit.class, 16.0F, 0, true));
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void livingUpdateEvent(LivingUpdateEvent event){
		
		if(event.entity != null && event.entity instanceof IMob && event.entity instanceof EntityCreature){
			EntityCreature living = (EntityCreature)event.entity;
			if(! living.isAIEnabled() && living.getEntityToAttack() == null){
				Entity attackTarget = findUnitToAttack(living, 16);
				living.setTarget(attackTarget);
		        if (living.getEntityToAttack() != null)
		        {
		        	living.setPathToEntity(living.worldObj.getPathEntityToEntity(
		          			living, living.getEntityToAttack(), 16.0F, true, false, false, true));
		        }
			}
		}else if(event.entity != null && event.entity instanceof EntityPlayer && !event.entity.worldObj.isRemote){
			if(event.entity.ticksExisted % 20*2 == 0){ //only every 2 seconds
				
				if(TeamMap.getInstance().isGameActive()){
					EnumTeam team = TeamMap.getInstance().getTeamForPlayer((EntityPlayer) event.entity);
					if(team != null){
						ByteBuf buffer = Unpooled.buffer();
						buffer.writeByte((byte)TeamPacketHandeler.TeamPacketType.SendTeam.ordinal());
						buffer.writeInt(event.entity.entityId);
						
						buffer.writeByte((byte)team.ordinal());
						
						FMLProxyPacket packet = new FMLProxyPacket(buffer, CommanderPacketHandeler.teamPacket);
						
						//Send to eveyone tracking player & to player
						((WorldServer)event.entity.worldObj).getEntityTracker().sendPacketToAllPlayersTrackingEntity(event.entity, packet);

                        if(event.entity instanceof EntityPlayerMP){
                            CommanderPacketHandeler.sendPacketToPlayer(packet, (EntityPlayerMP)event.entity);
                        }
	
					}
				}
			}
		}
	}

    @SubscribeEvent
    public void ServerLoad(WorldEvent.Load event){
        if(!event.world.isRemote && event.world.provider.dimensionId == 0){//|| TeamMap.isNotLoaded()){
            TeamMap.load(event.world.getSaveHandler().getWorldDirectory());
        }
    }

    @SubscribeEvent
    public void ServerSave(WorldEvent.Save event){
        if(!event.world.isRemote){
            TeamMap.save(event.world.getSaveHandler().getWorldDirectory());
        }
    }

    @SubscribeEvent
    public void clientChat(ClientChatReceivedEvent event) {
        if(event.message.message.startsWith("mb.")){

            if(event.message.message.equals("mb.gameEnd") || event.message.message.startsWith("mb.gameRestart"))
                TeamMap.resetInstance(-1, new EnumTeam[0]);

            if(event.message.message.contains("-")){
                String[] split = event.message.message.split("-");
                event.message.message = StatCollector.translateToLocal(split[0]);
                event.message.message = event.message.message.replace("@team@", StatCollector.translateToLocal(split[1]));
            }else{
                event.message.message = StatCollector.translateToLocal(event.message.message);
            }
        }
    }

	private EntityLiving findUnitToAttack(EntityLiving e, float dist) {
		
		//From EntitAINearestAttackableTarget
		List var5 = e.worldObj.selectEntitiesWithinAABB(EntityMBUnit.class, e.boundingBox.expand((double)dist, 4.0D, (double)dist), null);
		Collections.sort(var5, new MBNearestAttackableSorter(e));
        Iterator var2 = var5.iterator();
        while (var2.hasNext())
        {
            Entity var3 = (Entity)var2.next();
            EntityLiving var4 = (EntityLiving)var3;

            if (var4 instanceof EntityMBUnit && e.getEntitySenses().canSee(var4)){
            	return var4;
            }
        }
		
		return null;
	}
	
	private class MBNearestAttackableSorter implements Comparator{
		
		private Entity theEntity;
		
		public MBNearestAttackableSorter(Entity theEntity){
			this.theEntity=theEntity;
		}
	
		public int compareDistanceSq(Entity par1Entity, Entity par2Entity)
	    {
	        double var3 = this.theEntity.getDistanceSqToEntity(par1Entity);
	        double var5 = this.theEntity.getDistanceSqToEntity(par2Entity);
	        return var3 < var5 ? -1 : (var3 > var5 ? 1 : 0);
	    }
	
	    public int compare(Object par1Obj, Object par2Obj)
	    {
	        return this.compareDistanceSq((Entity)par1Obj, (Entity)par2Obj);
	    }
	}
	
}
