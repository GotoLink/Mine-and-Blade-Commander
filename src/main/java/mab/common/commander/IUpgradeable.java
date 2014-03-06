package mab.common.commander;

import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import mab.common.commander.npc.EntityMBUnit;
import mab.common.commander.npc.EnumUnits;
import net.minecraft.entity.player.EntityPlayer;

public interface IUpgradeable {
	public EnumUnits[] getUpgrades();
	
	public EnumTeam getTeam();
	
	public int getFirstEditableOptionIndex();
	
	public FMLProxyPacket generatePacket(EntityMBUnit unit, EntityPlayer player);
	
	public EntityMBUnit getDefaltUnit();
	
	public byte getDefaultOption(int i);
}
