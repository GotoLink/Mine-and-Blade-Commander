package mab.common.commander.block;

import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import mab.common.commander.CommanderPacketHandeler;
import mab.common.commander.EnumTeam;
import mab.common.commander.IUpgradeable;
import mab.common.commander.npc.EntityMBUnit;
import mab.common.commander.npc.EnumUnits;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;

public class TileEntityBanner extends TileEntity implements IUpgradeable{
	
	//0-7 (on ground top)
	//8-15 (on ground base)
	//16-19 (on wall top)
	//20-24 (on wall base)
	private byte state;
	private EnumTeam team;
	
	public TileEntityBanner(){
		state = 0;
		team = EnumTeam.black;
	}

	public TileEntityBanner(byte state, EnumTeam team) {
		super();
		this.state = state;
		this.team = team;
	}

	public boolean isBase() {
		return state < 8 || (state>15 && state< 20);
	}
	
	public boolean isTop(){
		return !(isBase());
	}
	
	public boolean isOnGround(){
		return state < 16;
	}
	public boolean isOnWall(){
		return state > 15;
	}

	public int getState() {
		return state;
	}

	public void setState(byte state) {
		this.state = state;
	}

	public EnumTeam getTeam() {
		return team;
	}

	public void setTeam(EnumTeam team) {
		this.team = team;
	}
	
	public void setTeam(byte teamIndex) {
		this.team = EnumTeam.values()[teamIndex];
	}

	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		super.readFromNBT(nbtTagCompound);
		state = nbtTagCompound.getByte("state");
		setTeam(nbtTagCompound.getByte("team"));
	}

	@Override
	public void writeToNBT(NBTTagCompound nbtTagCompound) {
		super.writeToNBT(nbtTagCompound);
		nbtTagCompound.setByte("state", state);
		nbtTagCompound.setByte("team", (byte)team.ordinal());
		
		
	}
	
	@Override
	public Packet getDescriptionPacket() {
		ByteBuf buf = Unpooled.buffer();
		try {
			buf.writeInt(xCoord);
            buf.writeInt(yCoord);
            buf.writeInt(zCoord);
            buf.writeByte(state);
            buf.writeByte((byte) team.ordinal());
			return new FMLProxyPacket(buf, CommanderPacketHandeler.BannerPacket);
			
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}

	@Override
	public EnumUnits[] getUpgrades() {
		return new EnumUnits[]{EnumUnits.Militia};
	}

	@Override
	public int getFirstEditableOptionIndex() {
		return 0;
	}

	@Override
	public FMLProxyPacket generatePacket(EntityMBUnit unit, EntityPlayer player) {
		if(isBase()){
			return CommanderPacketHandeler.generateSpawnPacket(unit, xCoord, yCoord, zCoord, player);
		}else{
			return CommanderPacketHandeler.generateSpawnPacket(unit, xCoord, yCoord - 1, zCoord, player);
		}
		
	}

	@Override
	public EntityMBUnit getDefaltUnit() {
		return null;
	}

	@Override
	public byte getDefaultOption(int i) {
		return 0;
	}

}
