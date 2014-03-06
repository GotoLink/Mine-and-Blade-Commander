package mab.common.commander;

import net.minecraft.entity.Entity;
import net.minecraft.util.EntityDamageSource;

public class DamageSourceArmourPenatrate extends EntityDamageSource {

	public DamageSourceArmourPenatrate(Entity par2Entity) {
		super("mob", par2Entity);
		this.setDamageBypassesArmor();
	}


}
