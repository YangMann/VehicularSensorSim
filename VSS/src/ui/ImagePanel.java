package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 *
 * Created by Yang ZHANG on 2014/10/26.
 */
public class ImagePanel extends JPanel {

    private final BufferedImage image;
    private final Graphics2D imageGraphics;
    private double scale = 1.0;

    public ImagePanel(BufferedImage image) {
        super();
        this.image = image;
        this.imageGraphics = image.createGraphics();
        setOpaque(false);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.drawImage(image, 0, 0, this);
    }

    public BufferedImage getImage() {
        return image;
    }

    public Graphics2D getImageGraphics() {
        return imageGraphics;
    }
}
