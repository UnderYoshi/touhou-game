package game.obj;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

import javax.swing.ImageIcon;

/**
 *
 * @author under
 */
public class Bullet {

    // How many pixels out of bounds bullets have to be before they despawn:
    private final int DESPAWN_RADIUS = 200;
    /*
     * Behaviour types:
     * 1: Constant motion
     * 2: Speeding up constantly
     * 3: Speeding up constantly, but slower
     */
    private int behaviourType;
    private int imageType;
    private double x;
    private double y;
    private float angle;
    private float speed;

    private double width;
    private double height;
    private Image image;
    private Area hitboxArea;
    private Shape hitboxShape;

    public Bullet(int behaviourType, int imageType, double x, double y, float angle, float speed) {
        this.behaviourType = behaviourType;
        this.imageType = imageType;
        this.angle = angle;
        this.speed = speed;

        this.image = new ImageIcon(getClass().getResource("/game/image/bullets/enemyBullet" + imageType + ".png")).getImage();
        width = image.getWidth(null);
        height = image.getHeight(null);
        x -= (width / 2);
        y -= (height / 2);
        this.x = x;
        this.y = y;

        // Changes hitbox based on used bullet image
        double offset;
        switch(this.imageType) {
            case 1:
                offset = 3;
                hitboxShape = new Ellipse2D.Double(offset, offset, width - (2*offset), height - (2*offset));

            break;
            case 2:
                offset = 7;
                hitboxShape = new Ellipse2D.Double(offset, offset, width - 2*offset, height - 2*offset);

            break;
            case 3:
                offset = 3;
                hitboxShape = new Ellipse2D.Double(offset, offset, width - (2*offset), height - (2*offset));

            break;
        }

        hitboxArea = new Area(hitboxShape);
    }


    public void changeLocation(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void update() {
        switch (behaviourType) {
            case 2:
                speed += 0.001;
            break;
            case 3:
            speed += 0.0005;
            break;
        };
        x += Math.cos(Math.toRadians(angle)) * speed;
        y += Math.sin(Math.toRadians(angle)) * speed;
    }
    public void changeAngle(float angle) {
        if (angle < 0 ) {
            angle = 359;
        } else if (angle > 359) {
            angle = 0;
        }
        this.angle = angle;
    }

    // Checks if bullet it still on screen
    public boolean check(int screenWidth, int screenHeight) {
        if (y < -height - DESPAWN_RADIUS || y > screenHeight + DESPAWN_RADIUS || x < -width - DESPAWN_RADIUS || x > screenWidth + DESPAWN_RADIUS) {
            return false;
        } else {
            return true;
        }
    }

    public void draw(Graphics2D g2) {
        AffineTransform oldTransform = g2.getTransform();
        g2.translate(x, y);
        AffineTransform transform = new AffineTransform();
        transform.rotate(Math.toRadians(angle + 90), width / 2, height / 2);
        g2.drawImage(image, transform, null);

        //Shape hitbox = getShape();
        g2.setTransform(oldTransform);
        //g2.setColor(new Color(36,214, 63));
        //g2.draw(hitbox);
    }

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public float getAngle() {
        return angle;
    }
    public double getWidth() {
        return width;
    }
    public double getHeight() {
        return height;
    }
    public double getCenterX() {
        return x + (width / 2);
    }
    public double getCenterY() {
        return y + (height / 2);
    }
    public Area getShape() {
        AffineTransform afx = new AffineTransform();
        afx.translate(x, y);
        afx.rotate(Math.toRadians(angle + 90), width / 2, height / 2);
        return new Area(afx.createTransformedShape(hitboxArea));
    }
}
