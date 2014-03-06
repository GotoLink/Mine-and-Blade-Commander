package mab.common.commander.npc.ai;

import mab.common.commander.npc.EntityMBUnit;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;

public enum EnumOrder {
	
	StandGuard("order.stand_guard"),
	Follow("order.follow"), 
	Details("order.details"), 
	Upgrade("order.upgrade"),
	GoTo("order.goto"),
	GoToSelect("order.goto.select"),
	GoToCancel("order.goto.cancel"),
	TargetDist("order.targetDist"),
	TargetNormal("order.target.normal"),
	TargetNear("order.tagert.near"),
	TargetNone("order.tagert.none");
	
	private String label;
	
	private EnumOrder(String label){
		this.label = label;
	}
	
	public String getLabel(){
		return label;
	}
	
	public String getTranslatedLabel(String postfix){
		if(postfix == null)
			return StatCollector.translateToLocal(label);
		else
			return StatCollector.translateToLocal(label)+" ("+postfix+")";
	}
	
	
	public void setOrderFromPacketData(EntityMBUnit unit, EntityPlayer player){
		switch (this) {
		case Follow:
			unit.setOrder(this, new int[]{-1, -1, -1}, player.username);
			break;
		case StandGuard:
			unit.setOrder(this, new int[]{
					(int)Math.floor(unit.posX),
					(int)Math.floor(unit.posY),
					(int)Math.floor(unit.posZ)
				}, "");
			break;
		default:
			break;
		}
	}

}
