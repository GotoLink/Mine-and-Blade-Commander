package mab.common.commander.npc.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.src.*;
import mab.common.commander.ConfigHelper;
import mab.common.commander.MBCommander;
import mab.common.commander.npc.EntityMBUnit;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class MBEntityAIAttackMelee extends EntityAIBase
{
    World worldObj;
    EntityLiving attacker;
    EntityLivingBase entityTarget;

    /**
     * An amount of decrementing ticks that allows the entity to attack once the tick reaches 0.
     */
    int attackTick;
    float field_75440_e;
    boolean field_75437_f;

    /** The PathEntity of our entity. */
    PathEntity entityPathEntity;
    private int field_75445_i;
    
    private int attackTime;
    private float reach;

    public MBEntityAIAttackMelee(EntityLiving par1EntityLiving, float par2, boolean par3, int attackTime, float reach)
    {
        this.attackTick = 0;
        this.attacker = par1EntityLiving;
        this.worldObj = par1EntityLiving.worldObj;
        this.field_75440_e = par2;
        this.field_75437_f = par3;
        this.setMutexBits(3);
        
        this.attackTime = attackTime;
        this.reach = reach;
    }
    
    public MBEntityAIAttackMelee(EntityLiving par1EntityLiving, float par2, boolean par3)
    {
        this(par1EntityLiving,par2, par3, 25, 0F);
    }

    @Override
    public boolean shouldExecute()
    {
        EntityLivingBase var1 = this.attacker.getAttackTarget();
        if (var1 == null)
        {
            return false;
        }
        else //if (var1 instanceof IMob || 
        	//	(var1 instanceof EntityMBUnit && MBCommander.config.get(ConfigHelper.CAT_UNITS, ConfigHelper.UNIT_ATTACK_TEAM, false).getBoolean(false)))
        {
        	this.entityTarget = var1;
            this.entityPathEntity = this.attacker.getNavigator().getPathToEntityLiving(this.entityTarget);
            return this.entityPathEntity != null;
        }
       // else
       // {
        	 
       //     return false;
       // }
    }

    @Override
    public boolean continueExecuting()
    {
        EntityLivingBase var1 = this.attacker.getAttackTarget();
        return var1 != null && this.entityTarget.isEntityAlive() && (!this.field_75437_f ?
        		!this.attacker.getNavigator().noPath() || isCloseEnough(): 
        			this.attacker.isWithinHomeDistance(MathHelper.floor_double(this.entityTarget.posX), MathHelper.floor_double(this.entityTarget.posY), MathHelper.floor_double(this.entityTarget.posZ)));
    }

    @Override
    public void startExecuting()
    {
        this.attacker.getNavigator().setPath(this.entityPathEntity, this.field_75440_e);
        this.field_75445_i = 0;
    }

    @Override
    public void resetTask()
    {
        this.entityTarget = null;
        this.attacker.getNavigator().clearPathEntity();
    }

    @Override
    public void updateTask()
    {
        this.attacker.getLookHelper().setLookPositionWithEntity(this.entityTarget, 30.0F, 30.0F);
        
        if ((this.field_75437_f || this.attacker.getEntitySenses().canSee(this.entityTarget)) && --this.field_75445_i <= 0)
        {
            this.field_75445_i = 4 + this.attacker.getRNG().nextInt(7);
            this.attacker.getNavigator().tryMoveToEntityLiving(this.entityTarget, this.field_75440_e);
        }
        
        if(isCloseEnough()){
        	this.attacker.getNavigator().clearPathEntity();
        }

        this.attackTick = Math.max(this.attackTick - 1, 0);
        double var1 = (double)((reach + this.attacker.width * 2.0F) * (reach + this.attacker.width * 2.0F));
        double targetSize = (double)(this.entityTarget.width * 2.0F * this.entityTarget.width * 2.0F);
        if (this.attacker.getDistanceSq(this.entityTarget.posX, this.entityTarget.boundingBox.minY, this.entityTarget.posZ) <= 0.5*(var1+targetSize))
        {
            if (this.attackTick <= 0)
            {
                this.attackTick = attackTime;

                this.attacker.swingItem();

                this.attacker.attackEntityAsMob(this.entityTarget);
            }
        }
    }

	private boolean isCloseEnough() {
		 double var1 = (double)((reach + this.attacker.width * 2.0F) * (reach + this.attacker.width * 2.0F));
	        double targetSize = (double)(this.entityTarget.width * 2.0F * this.entityTarget.width * 2.0F);
	        return this.attacker.getDistanceSq(this.entityTarget.posX, this.entityTarget.boundingBox.minY, this.entityTarget.posZ) <= 0.5*(var1+targetSize);
	}
}
