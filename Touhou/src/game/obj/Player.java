package game.obj;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

import game.component.PanelGame;

/**
 *
 * @author under
 */

public class Player {

    public Player() {
        this.imageFrame1 = new ImageIcon(getClass().getResource("/game/image/playerFrame1.png")).getImage();
        this.imageFrame2 = new ImageIcon(getClass().getResource("/game/image/playerFrame2.png")).getImage();
        this.imageFrame3 = new ImageIcon(getClass().getResource("/game/image/playerFrame3.png")).getImage();
        this.imageFrame4 = new ImageIcon(getClass().getResource("/game/image/playerFrame4.png")).getImage();
        this.hitbox = new ImageIcon(getClass().getResource("/game/image/hitbox.png")).getImage();
        // Hitbox offset
        double hO = 4;
        hitboxShape = new Ellipse2D.Double(24 + hO, 40 + hO, 16 - (hO*2) - 1, 16 - (hO*2) - 1);
        hitboxArea = new Area(hitboxShape);
    }

    public static final double PLAYER_WIDTH = 32 * 2;
    public static final double PLAYER_HEIGHT = 48 * 2;
    private double x;
    private double y;
    public float hitboxOpacity;
    private boolean inFocusMode;

    private final Image imageFrame1;
    private final Image imageFrame2;
    private final Image imageFrame3;
    private final Image imageFrame4;
    private final Image hitbox;
    private Image image;
    private Area hitboxArea;
    private Shape hitboxShape;

    public void changeLocation(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics2D g2) {
        switch((PanelGame.getFrame() / 12) % 4) {
            case 0:
                image = imageFrame1;
            break;
            case 1:
                image = imageFrame2;
            break;
            case 2:
                image = imageFrame3;
            break;
            case 3:
                image = imageFrame4;
            break;
        }

        AffineTransform oldTransform = g2.getTransform();
        g2.translate(x, y);
        g2.drawImage(image, 0, 0, null);

        // if (inFocusMode)
        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, hitboxOpacity);
        g2.setComposite(ac);
        g2.drawImage(hitbox, 0, 0, null);
        ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1);
        g2.setComposite(ac);

        Shape hitbox = getShape();
        g2.setTransform(oldTransform);
        g2.setColor(new Color(36,214, 63));
        g2.draw(hitbox);
    }

    public void focus() {
        inFocusMode = true;
        if (this.hitboxOpacity < 1) {
            this.hitboxOpacity += 0.02;
        } if (this.hitboxOpacity > 1) {
            this.hitboxOpacity = 1;
        }
    }

    public void unfocus() {
        inFocusMode = false;
        this.hitboxOpacity = 0;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getCenterX() {
        return x + (PLAYER_WIDTH / 2);
    }

    public double getCenterY() {
        return y + (PLAYER_HEIGHT / 2);
    }
    public Area getShape() {
        AffineTransform afx = new AffineTransform();
        afx.translate(x, y);
        return new Area(afx.createTransformedShape(hitboxArea));
    }
}
