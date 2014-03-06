package extendedGUI;

import java.awt.Color;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;

public class RadioButtonControl extends GuiButton {

	String[] options;
	public int selected = -1;
	
	public RadioButtonControl(int id, int xPos, int yPos, String[] options, FontRenderer font) {
		super(id, xPos, yPos, "");
		
		int max = 0;
		for (String s : options) {
			int length = font.getStringWidth(s);
			if(length>max)
				max = length;
		}
		
		this.height = 12*options.length;
		this.width = max + 14;
		this.options = options;
	}
	
	public RadioButtonControl(int id, int xPos, int yPos, int width, int height,String[] options) {
		super(id, xPos, yPos, width, height, "");
		
		
		
		this.height = 12*options.length;
		this.options = options;
	}
	
	@Override
    public void drawButton(Minecraft minecraft, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            FontRenderer var4 = minecraft.fontRenderer;
            boolean mouseOver = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int hoverState = this.getHoverState(mouseX, mouseY);
                        
            minecraft.renderEngine.bindTexture(new ResourceLocation("commander","gui/GUI Controls.png"));
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            
            
            for(int i = 0; i < options.length; i++){
            	if(i == selected){
            		this.drawTexturedModalRect(this.xPosition, this.yPosition+12*i, 44, 123, 8, 8);
            	}else{
            		this.drawTexturedModalRect(this.xPosition, this.yPosition + 12*i, 31, 123, 8, 8);
            	}
            }
            
            for(int i = 0; i < options.length; i++){
            	if(hoverState == i){
            		this.drawString(var4, options[i],this.xPosition+10 , this.yPosition+12*i, Color.YELLOW.getRGB());
            	}else{
            		this.drawString(var4, options[i],this.xPosition+10 , this.yPosition+12*i, Color.WHITE.getRGB());
            	}
            }
        }
    }

    protected int getHoverState(int mouseX, int mouseY) {
    	if(!enabled)
    		return -1;
    	else{
    		//if within bounds
    		if(mouseX >= this.xPosition &&  
    				mouseY >= this.yPosition && 
    				mouseX < this.xPosition + this.width && 
    				mouseY < this.yPosition + this.height ){
    			
    			return (mouseY-this.yPosition) / 12;
    			
    		}else
    			return -1;
    	}
    }

	@Override
	public boolean mousePressed(Minecraft par1Minecraft, int mouseX, int mouseY) {
		int newSelect = getHoverState(mouseX, mouseY);
		
		if(newSelect != -1){
			if(newSelect != selected){
				selected = newSelect;
				return true;
			}
		}
		
		return false;
	}
}
