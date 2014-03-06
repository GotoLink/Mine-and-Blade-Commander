package mab.common.commander.npc;

import net.minecraft.util.StatCollector;

public enum EnumAbilities {

	ExtendedReach("special.extend_Reach", 0),
	SlowAttack("special.slow_attack", 1),
	
	ArmourPenatrate("special.penatrate", 0),
	ArrowBlock("special.arrow_block", 0), 
	Leap("special.leap", 0);
	
	private String name;
	private int type;
	
	private EnumAbilities(String name, int type){
		this.name = name;
		this.type = type;
	} 
	private EnumAbilities(String name){
		this(name, 0);
	}
	
	public String getTranslatedName(int level){
		if(level > 0)
			return StatCollector.translateToLocal(name) + "("+level+")";
		else
			return StatCollector.translateToLocal(name);
	}
	
	public String getTranslatedDecription(int level, EntityMBUnit unit){
		
		String desc = StatCollector.translateToLocal(name+".decription");
		
		//desc = desc.replaceAll("@name@", StringTranslate.getInstance().translateKey(name));
		desc = desc.replaceAll("@unit@", StatCollector.translateToLocal(unit.getUnitName()));
		//desc = desc.replaceAll("@level@", String.valueOf(level));
		
		return desc;
		
	}
	
	public boolean isPositive(){
		return type == 0;
	}
}
