package mab.common.commander.npc.melee;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mab.common.commander.EnumTeam;
import mab.common.commander.MBCommander;
import mab.common.commander.npc.EntityMBUnit;
import mab.common.commander.npc.EnumUnitItems;
import mab.common.commander.npc.EnumUnits;
import net.minecraft.world.World;

public class EntityMBKnight extends EntityMBUnit{
	
	@Override
	public byte getCost() {
		return 9;
	}

	public EntityMBKnight(World par1World) {
		this(par1World, EnumTeam.values()[par1World.rand.nextInt(16)], EnumUnits.KnightShield);
	}
	
	public EntityMBKnight(World par1World, EnumTeam team, EnumUnits type) {
		super(par1World, team, type);		
	}	

	@Override
	public int getAttackStrength() {
		return 10;
	}
	
	@Override
	public int getMaxHealth() {
		return 10;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public String getTexture() {
		return MBCommander.IMAGE_FOLDER+"skins/units/knight/Knight-"+getTeam().ordinal()+".png";
	}

	@Override
	public EnumUnitItems getWeaponOption() {
		switch(getUnitType()){
		case KnightShield:
		case KnightDuel:
			switch(getOption(3)){
			case 0:
				return EnumUnitItems.IronSword;
			case 1:
				return EnumUnitItems.IronBattleaxe;
			case 2:
				return EnumUnitItems.IronMace;
			default:
					return null;
			}
		case KnightSpear:
			switch(getOption(3)){
			case 0:
				return EnumUnitItems.IronSpear;
			case 1:
				return EnumUnitItems.IronHalberard;
			}
		}
		
		return null;
	}

	@Override
	public EnumUnitItems getWeaponOffHandOption() {
		switch(getUnitType()){
		case KnightShield:
		case KnightSpear:
			switch(getOption(4)){
			case 0:
				return EnumUnitItems.IronShield;
			case 1:
				return EnumUnitItems.DiamondShield;
			case 2:
				return EnumUnitItems.GoldShield;
				default:
					return null;
			}
		case KnightDuel:
			switch(getOption(4)){
			case 0:
				return EnumUnitItems.IronSword;
			case 1:
				return EnumUnitItems.IronBattleaxe;
			case 2:
				return EnumUnitItems.IronMace;
			default:
					return null;
			}
			
		default:
			return null;
		}

	}

	@Override
	public int getHelmNumber() {
		return getOption(5)-1;
	}

	@Override
	protected byte getExperiencePerHit() {
		return 0;
	}
	
	

}
