package extendedGUI;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;

public class GUIAltScroll extends GuiButton {
	
	 /** The size in pixels */
    protected int size;
    
    /** Weather the control is horizontal or vertical*/
    protected boolean horizonatal;
    
    /** The current position of the slider */
    public int current;
    
    /** The slider value 0<=x=<1 */
    public float sliderValue;
    
    /** Is this slider control being dragged. */
    public boolean dragging = false;
    
    protected int min;
    protected int max;
	
	public GUIAltScroll(int id, int xPos, int yPos, int size, boolean horizontal, int min, int max){
		super(id, xPos, yPos, size, 14, "");
		
		if(max > min){
			this.max = max;
			this.min = min;
		}else{
			this.min = max;
			this.max = min;
		}
		
		this.max++;
		
		this.horizonatal = horizontal;
		
		if(!horizontal){
			width = 14;
			height = size;
		}
		
		this.current = min;
	}

	@Override
	public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
		 if (this.visible)
	     {
			 minecraft.renderEngine.bindTexture(new ResourceLocation("commander","gui/GUI Controls.png"));
			 GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			 
			 if(horizonatal){
				 this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 108, this.width / 2, this.height);
		         this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 108, this.width / 2, this.height);
		         this.drawTexturedModalRect(this.xPosition + (int)(sliderValue * (float)(this.width - 15)), this.yPosition+1, 39, 147, 15, 12);
			 }else{
	            	this.drawTexturedModalRect(this.yPosition+1, this.yPosition + (int)(this.sliderValue * (float)(this.width - 15)), 39, 147, 15, 12);
			 }
			 
			 this.mouseDragged(minecraft, mouseX, mouseY);
			 
			 if(this.displayString != null && this.displayString.length()>0){
				 
			 }
	     }
	}

    @Override
    protected void mouseDragged(Minecraft minecraft, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            if (this.dragging)
            {
            	if(horizonatal)
            		this.sliderValue = (float)(mouseX - (this.xPosition + 8)) / (float)(this.width - 15);
            	else
            		this.sliderValue = (float)(mouseY -(this.yPosition + 8)) / (float)(this.height - 15);

            	
                if (this.sliderValue < 0.0F)
                {
                    this.sliderValue = 0.0F;
                }

                if (this.sliderValue > 0.9999F)
                {
                    this.sliderValue = 0.9999F;
                }
                
                current = MathHelper.floor_float(sliderValue * (max - min) + min);
                
            }
        }
    }

    @Override
    public boolean mousePressed(Minecraft par1Minecraft, int mouseX, int mouseY)
    {
        if (super.mousePressed(par1Minecraft,mouseX, mouseY))
        {
        	if(horizonatal)
        		this.sliderValue = (float)(mouseX - (this.xPosition + 15)) / (float)(this.width - 15);
        	else
        		this.sliderValue = (float)(mouseY -(this.yPosition + 15)) / (float)(this.height - 15);

            if (this.sliderValue < 0.0F)
            {
                this.sliderValue = 0.0F;
            }

            if (this.sliderValue > 0.9999F)
            {
                this.sliderValue = 0.9999F;
            }
           
            this.dragging = true;
            return true;
        }
        else
        {
            return false;
        }
    }
	
	@Override
    public void mouseReleased(int par1, int par2)
    {
        this.dragging = false;
    }
	

}
