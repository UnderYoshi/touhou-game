package game.obj;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

import javax.swing.ImageIcon;

import game.component.PanelGame;
import game.main.Main;

/**
 *
 * @author under
 */
public class Enemy {
    // How many pixels out of bounds enemies have to be before they despawn:
    private final int DESPAWN_RADIUS = 200;
    public boolean attacking = false;
    /*
     * Enemy types:
     * 1:   Comes down from top of the screen, goes back up and despawns;
     *      Attacks when reaching most bottom position
     * 2:   Comes from the side, stays in the middle to attack, then leaves again
     */
    private int enemyType;
    /*
    * Attacks themselves are coded in PanelGame.java
    * Attack types:
    * 1: Sprays bullets in half circle under enemy
    * 2: Clockwise bullet spray
    * 3: Counterclockwise bullet spray
    */
    private int attackType;
    private double x;
    private double y;
    private float angle;
    private float speed;
    private int attackAmount;
    private int attackCooldown = 0;
    // Tells enemy type 2 where to stop moving on the screen, default is in the middle
    private double stopWidth;
    private int health;
    public int attackTimer = 0;


    private double width;
    private double height;
    private final Image image;
    private final Area hitboxArea;
    private Shape hitboxShape;

    public Enemy(int type, int attackType, double x, double y, float speed, float angle, int attackAmount, double stopWidth) {
        this.enemyType = type;
        this.image = new ImageIcon(getClass().getResource("/game/image/enemies/enemy" + enemyType + ".png")).getImage();

        this.attackAmount = attackAmount;
        this.attackType = attackType;
        width = image.getWidth(null);
        height = image.getHeight(null);
        x -= (width / 2);
        y -= (height / 2);
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.angle = angle;
        this.stopWidth = stopWidth;

        // Special properties per enemy type
        switch(type) {
            case 1:
                this.health = 2;
            break;
            case 2:
                this.x = -50;
                this.health = 30;
            break;
        }
        hitboxShape = new Ellipse2D.Double(0, 0, width, height);
        hitboxArea = new Area(hitboxShape);
    }
    public Enemy(int type, int attackType, double x, double y, float speed, float angle) {
        this(type, attackType, x, y, speed, angle, 1, PanelGame.GAME_WIDTH / 2);
    }
    public Enemy(int type, int attackType, double x, double y, float speed, float angle, int attackAmount) {
        this(type, attackType, x, y, speed, angle, attackAmount, PanelGame.GAME_WIDTH / 2);
    }

    public void changeLocation(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void doDamage(int damage) {
        health -= damage;
    }

    public void update() {
        switch (enemyType) {
            case 1:
                speed -= 0.3;
                if(attackAmount > 0 && attackCooldown == 0 && speed < 0) {
                    attacking = true;
                    attackAmount--;
                    attackCooldown = 10;
                } else if(attackCooldown > 0) {
                    attackCooldown--;
                }
            break;
            case 2:
                if (x + width >= stopWidth && attackAmount > 0 && attackCooldown <= 0) {
                    speed = 0;
                    angle = 0;
                    attacking = true;
                    attackAmount--;
                    attackCooldown = 5;
                } else if (x + width >= stopWidth && attackAmount > 0 && attackCooldown > 0){
                    attackCooldown--;
                } else if (attackAmount == 0 && attacking == false) {
                    speed += 0.1;
                    angle -= 1;
                } else {
                    speed = speed * 0.999f;
                    angle = angle * 0.99f;
                }
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

    public void draw(Graphics2D g2) {
        AffineTransform oldTransform = g2.getTransform();
        g2.translate(x, y);
        g2.drawImage(image, 0, 0, null);
        g2.setTransform(oldTransform);
    }

    // Checks if enemy it still on screen
    public boolean check(int screenWidth, int screenHeight) {
        if (y < -height - DESPAWN_RADIUS || y > screenHeight + DESPAWN_RADIUS || x < -width - DESPAWN_RADIUS || x > screenWidth + DESPAWN_RADIUS) {
            return false;
        } else {
            return true;
        }
    }

    public int getAttackType() {
        return attackType;
    }
    public int getAttackAmount() {
        return attackAmount;
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
        afx.rotate(Math.toRadians(angle), width / 2, height / 2);
        return new Area(afx.createTransformedShape(hitboxArea));
    }
    public int getHealth() {
        return health;
    }
    
}
