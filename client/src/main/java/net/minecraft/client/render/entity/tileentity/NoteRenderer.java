package net.minecraft.client.render.entity.tileentity;

import net.minecraft.client.localization.Font;
import net.minecraft.game.block.tileentity.other.NoteBlockEntity;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class NoteRenderer extends TileEntityRenderer<NoteBlockEntity> {
    private final int[] colorArray = new int[]{
        7853824,
        9814016,
        11707648,
        13403648,
        14836992,
        15941888,
        16522752,
        16646159,
        16187443,
        15204442,
        13566083,
        11403433,
        8782028,
        5964007,
        2949369,
        133886,
        14326,
        26848,
        39612,
        50829,
        59736,
        64545,
        2096128,
        5892096,
        9748736
    };
    private final String[] noteArray = new String[]{"F#", "G", "G#", "A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#"};

    public void render(NoteBlockEntity tile, double x, double y, double z, float delta) {
        GL11.glPushMatrix();
        GL11.glDisable(2896);
        GL13.glMultiTexCoord2f(33985, 240.0F, 240.0F);
        GL13.glClientActiveTexture(33985);
        GL13.glActiveTexture(33985);
        GL11.glDisable(3553);
        GL13.glClientActiveTexture(33984);
        GL13.glActiveTexture(33984);
        byte note = tile.note;
        String noteText = "" + note;
        Font font = this.renderManager.getFontRenderer();
        float offset = 0.011111112F;
        GL11.glPushMatrix();
        GL11.glDepthMask(false);
        GL11.glNormal3f(0.0F, 0.0F, -1.0F * offset);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y + 0.58, z + 1.0);
        GL11.glTranslatef(0.01F, 0.0F, 0.01F);
        GL11.glScalef(2.0F, 2.0F, 2.0F);
        GL11.glScalef(offset, -offset, offset);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        font.drawString(noteText, -font.getStringWidth(noteText) / 2 + 8, 0, this.colorArray[tile.note]);
        if (note >= 12) {
            note = (byte)(note - 12);
        }

        font.drawString("|", -font.getStringWidth("|") / 2, 0, 16777215);
        font.drawString(this.noteArray[note], -font.getStringWidth(this.noteArray[note]) / 2 - 8, 0, this.colorArray[tile.note]);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(x + 1.0, y + 0.58, z + 0.5);
        GL11.glTranslatef(0.01F, 0.0F, -0.01F);
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glScalef(2.0F, 2.0F, 2.0F);
        GL11.glScalef(offset, -offset, offset);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        font.drawString(noteText, -font.getStringWidth(noteText) / 2 + 8, 0, this.colorArray[tile.note]);
        if (note >= 12) {
            note = (byte)(note - 12);
        }

        font.drawString("|", -font.getStringWidth("|") / 2, 0, 16777215);
        font.drawString(this.noteArray[note], -font.getStringWidth(this.noteArray[note]) / 2 - 8, 0, this.colorArray[tile.note]);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(x, y + 0.58, z + 0.5);
        GL11.glTranslatef(-0.01F, 0.0F, 0.01F);
        GL11.glRotatef(270.0F, 0.0F, 1.0F, 0.0F);
        GL11.glScalef(2.0F, 2.0F, 2.0F);
        GL11.glScalef(offset, -offset, offset);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        font.drawString(noteText, -font.getStringWidth(noteText) / 2 + 8, 0, this.colorArray[tile.note]);
        if (note >= 12) {
            note = (byte)(note - 12);
        }

        font.drawString("|", -font.getStringWidth("|") / 2, 0, 16777215);
        font.drawString(this.noteArray[note], -font.getStringWidth(this.noteArray[note]) / 2 - 8, 0, this.colorArray[tile.note]);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y + 0.58, z);
        GL11.glTranslatef(-0.01F, 0.0F, -0.01F);
        GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
        GL11.glScalef(2.0F, 2.0F, 2.0F);
        GL11.glScalef(offset, -offset, offset);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        font.drawString(noteText, -font.getStringWidth(noteText) / 2 + 8, 0, this.colorArray[tile.note]);
        if (note >= 12) {
            note = (byte)(note - 12);
        }

        font.drawString("|", -font.getStringWidth("|") / 2, 0, 16777215);
        font.drawString(this.noteArray[note], -font.getStringWidth(this.noteArray[note]) / 2 - 8, 0, this.colorArray[tile.note]);
        GL11.glPopMatrix();
        GL11.glDepthMask(true);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
        GL13.glClientActiveTexture(33985);
        GL13.glActiveTexture(33985);
        GL11.glEnable(3553);
        GL13.glClientActiveTexture(33984);
        GL13.glActiveTexture(33984);
        GL11.glEnable(2896);
        GL11.glPopMatrix();
    }
}
