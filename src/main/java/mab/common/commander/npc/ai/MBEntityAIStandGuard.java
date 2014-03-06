package mab.common.commander.npc.ai;

import mab.common.commander.npc.EntityMBUnit;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.world.World;

public class MBEntityAIStandGuard extends EntityAIBase {

    private EntityMBUnit unit;
	private World theWorld;
	private float warpDist;
	private PathNavigate petPathfinder;
	private int timer;
	private boolean avoidWater;

	public MBEntityAIStandGuard(EntityMBUnit par1EntityTameable, float par2)
    {
        this.unit = par1EntityTameable;
        this.theWorld = par1EntityTameable.worldObj;
        this.warpDist = 1F;
        this.petPathfinder = par1EntityTameable.getNavigator();
    }
	 
	@Override
	public boolean shouldExecute() {
		return unit.getOrder() == EnumOrder.StandGuard 
				&& distSqToTarget() >= .1F
				&& unit.getAttackTarget() == null;
				
	}
	
	@Override
    public boolean continueExecuting()
    {
        return !this.petPathfinder.noPath() &&
        		distSqToTarget() > (double)(this.warpDist * this.warpDist) 
        		&& unit.getOrder() == EnumOrder.StandGuard && unit.getAttackTarget() == null && unit.hurtTime == 0;
    }
	
    @Override
    public void startExecuting()
    {
        this.timer = 0;
        int[] data = unit.getOrderData();
        this.petPathfinder.tryMoveToXYZ(data[0]+.5F, data[1], data[2]+.5F,.3F);
        this.avoidWater = unit.getNavigator().getAvoidsWater();
        unit.getNavigator().setAvoidsWater(false);
    }
    
    @Override
    public void resetTask()
    {
        this.petPathfinder.clearPathEntity();
        unit.getNavigator().setAvoidsWater(avoidWater);
        double dist = distSqToTarget();
        if(unit.getAttackTarget() == null && unit.hurtTime == 0 
        		&& dist < (double)(this.warpDist * this.warpDist)){
	        int[] data = unit.getOrderData();
	        unit.setPositionAndUpdate(data[0]+.5F, data[1], data[2]+.5F);
        }
    }
	
	
	private double distSqToTarget(){
		int[] data = unit.getOrderData();
		return unit.getDistanceSq(data[0]+.5F, data[1], data[2]+.5F);
	}
	
	@Override
    public void updateTask()
    {
        //unit.getLookHelper().setLookPositionWithEntity(owner, 10.0F, (float)unit.getVerticalFaceSpeed());

        if (unit.getOrder() == EnumOrder.StandGuard)
        {
            if (--this.timer <= 0)
            {
                this.timer = 10;

                int[] data = unit.getOrderData();
               
                this.petPathfinder.tryMoveToXYZ(data[0]+.5F, data[1], data[2]+.5F,.3F);
            }
            
            if(distSqToTarget() < 1){
            	 int[] data = unit.getOrderData();
     	        unit.setPositionAndUpdate(data[0]+.5F, data[1], data[2]+.5F);
            }
        }
    }

}
