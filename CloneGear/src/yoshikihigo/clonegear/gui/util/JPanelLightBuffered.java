/*
 * Created on 2004/04/22
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package yoshikihigo.clonegear.gui.util;


import javax.swing.JPanel;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Dimension;


/**
 * @author kamiya
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class JPanelLightBuffered extends JPanel {
    private BufferedImage backBuffer = null;

    @Override
    public void paintComponent(Graphics g) {
        final Dimension dim = getSize();
        if (this.backBuffer == null || this.backBuffer.getHeight() != dim.height
                || this.backBuffer.getWidth() != dim.width) {
            this.backBuffer = new BufferedImage(dim.width, dim.height,
                    BufferedImage.TYPE_USHORT_555_RGB);
        }
        Graphics gBackBuffer = this.backBuffer.getGraphics();

        super.paintComponent(gBackBuffer);

        g.drawImage(this.backBuffer, 0, 0, this);
    }
}
