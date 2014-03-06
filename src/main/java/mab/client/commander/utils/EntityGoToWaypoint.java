package mab.client.commander.utils;

import java.util.List;

import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import mab.common.commander.CommanderPacketHandeler;
import mab.common.commander.npc.EntityMBUnit;
import mab.common.commander.npc.ai.EnumOrder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityGoToWaypoint extends Entity {

	public EntityGoToWaypoint(World par1World) {
		super(par1World);
	}

	@Override
	protected void entityInit() {
		
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound var1) {
		
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound var1) {
		
	}
	
	public FMLProxyPacket setGoToOrder(List<EntityMBUnit> units, EntityPlayer player){
		
		ByteBuf buf = Unpooled.buffer();
		try{

            buf.writeInt(player.entityId);
            buf.writeByte((byte)EnumOrder.GoTo.ordinal());
            buf.writeByte((byte)units.size());
			for (EntityMBUnit entityMBUnit : units) {
                buf.writeInt(entityMBUnit.entityId);
			}
            buf.writeInt(MathHelper.floor_double(posX));
            buf.writeInt(MathHelper.floor_double(posY));
            buf.writeInt(MathHelper.floor_double(posZ));
			
			return new FMLProxyPacket(buf, CommanderPacketHandeler.orderPacket);

		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
