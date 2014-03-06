package mab.client.commander.utils;

import mab.common.commander.MBCommander;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderWaypoint extends Render
{
    public static ResourceLocation loc = new ResourceLocation(MBCommander.IMAGE_FOLDER+"Waypoint.png");
    public final ModelWaypoint waypointModel;

    public RenderWaypoint(ModelWaypoint dummy)
    {
        this.waypointModel = dummy;
    }

    @Override
    public void doRender(Entity var1, double d, double d1, double d2, float f, float f1)
    {
        GL11.glPushMatrix();
        f1 = 0.6666667F;

        GL11.glTranslatef((float)d, (float)d1, (float)d2);

        GL11.glTranslatef(0.0F, 1.51F, 0.0F);

        GL11.glRotatef(0.0F, 0.0F, 1.0F, 0.0F);

        GL11.glPushMatrix();
        GL11.glScalef(f1, -f1, -f1);

        bindEntityTexture(var1);
        this.waypointModel.renderBase(var1, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
        GL11.glColor4f(1F, 1F, 1F, .75F);
        GL11.glEnable(GL11.GL_BLEND);
        this.waypointModel.renderArrow(var1, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
        GL11.glDisable(GL11.GL_BLEND);

        GL11.glPopMatrix();

        GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity var1) {
        return loc;
    }

}