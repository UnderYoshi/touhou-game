package game.component;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

import game.obj.PlayerBullet;
import game.main.Main;
import game.obj.Bullet;
import game.obj.Enemy;
import game.obj.Player;

/**
 *
 * @author under
 */

public class PanelGame extends JComponent{
    
    Random random = new Random();
    private Area tempArea;
    private Graphics2D g2;
    private BufferedImage image;
    private int width;
    private int height;
    private Thread mainThread;
    private boolean start = true;
    private Key key;
    private int shotTime;
    public static int GAME_WIDTH;
    public static int GAME_HEIGHT;

    // Player speed
    final double SPEED = 2;
    final double FOCUSED_SPEED = 1;

    // Game FPS
    private final int FPS = 60;
    private final int TARGET_TIME = 1000000000 / FPS;
    private static int frame = 0;
    // Game objects
    private Player player;
    private List<PlayerBullet> playerBullets;
    private List<Enemy> enemies;
    private List<Bullet> bullets;

    public void start() {
        width = getWidth();
        height = getHeight();
        GAME_WIDTH = width;
        GAME_HEIGHT = height;
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        mainThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (start) {
                    long startTime = System.nanoTime();
                    frame ++;
                    drawBackground();
                    drawGame();
                    render();

                    for (int i = 0; i < bullets.size(); i++) {
                        Bullet bullet = bullets.get(i);
                        if (bullet != null && Math.abs(bullet.getCenterX() - player.getCenterX()) < 20 && Math.abs(bullet.getCenterY() - player.getCenterY()) < 20) {
                            checkBulletCollision(bullet);
                        }
                    }
                    for(int i = 0; i < enemies.size(); i++) {
                        Enemy enemy = enemies.get(i);
                        if (enemy != null); {
                            enemy.update();
                            if (!enemy.check(width, height)) {
                                enemies.remove(enemy);
                            } else if (enemy.attacking) {
                                attack(enemy, enemy.getAttackType(), enemy.getAttackAmount(), enemy.getCenterX(), enemy.getCenterY());
                            }
                        }
                    }
                    for (int i = 0; i < playerBullets.size(); i++) {
                        PlayerBullet playerBullet = playerBullets.get(i);
                        if (playerBullet != null) {
                            checkPlayerBulletCollision(playerBullet);
                        }
                    }
                    long time = System.nanoTime() - startTime;
                    if (time < TARGET_TIME) {
                        long sleep = (TARGET_TIME - time) / 1000000;
                        sleep(sleep);
                    }
                }
            }
        });
        initObjectGame();
        initKeyboard();
        initPlayerBullets();
        initBullets();
        mainThread.start();
    }

    private void checkPlayerBulletCollision(PlayerBullet playerBullet) {
        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            if (enemy != null && Math.abs(playerBullet.getCenterX() - enemy.getCenterX()) < 20 && Math.abs(playerBullet.getCenterY() - enemy.getCenterY()) < 20) {
                Area area = new Area(playerBullet.getShape());
                area.intersect(enemy.getShape());
                if (!area.isEmpty()) {
                    enemy.doDamage(playerBullet.getDamage());
                    playerBullets.remove(playerBullet);
                }
                if (enemy.getHealth() <= 0) {
                    enemies.remove(enemy);
                }
            }
        }

    }

    private void checkBulletCollision(Bullet bullet) {
        if (player != null) {
            tempArea =  bullet.getShape();
            tempArea.intersect(player.getShape());
            if (!tempArea.isEmpty()) {
                System.out.println("ouch");
            }
        }
    }

    private void addEnemy(int type, int attackType, double x, double y, float speed, float angle) {
        Enemy enemy = new Enemy(type, attackType, x, y, speed, angle);
        enemies.add(enemy);
    }
    private void addEnemy(int type, int attackType, double x, double y, float speed, float angle, int attackAmount) {
        Enemy enemy = new Enemy(type, attackType, x, y, speed, angle, attackAmount);
        enemies.add(enemy);
    }
    private void addEnemy(int type, int attackType, double x, double y, float speed, float angle, int attackAmount, double stopWidth) {
        Enemy enemy = new Enemy(type, attackType, x, y, speed, angle, attackAmount, stopWidth);
        enemies.add(enemy);
    }
    /*
    * Attack types:
    * 1: Sprays bullets in half circle under enemy
    * 2: Clockwise bullet spray
    * 3: Counterclockwise bullet spray
    * 4: Closer together clockwise bullet spray
    * 5: Closer together counterclockwise bullet spray
    */
    private void attack(Enemy enemy, int attackType, int attackAmount, double x, double y) {
        switch(attackType) {
            case 1:
                for (int th = 0; th <= 180; th = th + 10) {
                    addBullet(2, 3, x, y, th, 0.01f);
                    enemy.attacking = false;
                }
            break;
            case 2:
                if(enemy.attackTimer % 4 == 0) {
                    addBullet(3, 2, x, y, enemy.attackTimer, 0.01f);
                }
                if (enemy.attackTimer >= 3600) {
                    enemy.attacking = false;
                }
                enemy.attackTimer += 6;
            break;
            case 3:
                if(-enemy.attackTimer % 4 == 0) {
                    addBullet(3, 2, x, y, enemy.attackTimer + 180, 0.01f);
                }
                if (enemy.attackTimer <= -3600) {
                    enemy.attacking = false;
                }
                enemy.attackTimer -= 6;
            break;
            case 4:
                if(enemy.attackTimer % 8 == 0) {
                    addBullet(2, 2, x, y, enemy.attackTimer, 0.01f);
                }
                if (enemy.attackTimer >= 1800) {
                    enemy.attacking = false;
                }
                enemy.attackTimer += 4;
            break;
            case 5:
                if(-enemy.attackTimer % 8 == 0) {
                    addBullet(2, 2, x, y, enemy.attackTimer + 180, 0.01f);
                }
                if (enemy.attackTimer <= -1800) {
                    enemy.attacking = false;
                }
                enemy.attackTimer -= 4;
            break;
        }
    }

    private void addBullet(int behaviourType, int imageType, double x, double y, float angle, float speed) {
        Bullet bullet = new Bullet(behaviourType, imageType, x, y, angle, speed);
        bullets.add(bullet);
    }

    private void initObjectGame() {
        player = new Player();
        player.changeLocation((width / 2), (height / 2 + 300));
        enemies = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (start) {
                    // // Spawining enemies
                    addEnemy(2, 1, -50, 0, 5, 45, 60, width * 0.5);
                    sleep(50000);
                }
            }
        }).start();
    }

    private void initKeyboard() {
        key = new Key();
        requestFocus();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    key.setKey_left(true);
                } if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    key.setKey_right(true);
                } if (e.getKeyCode() == KeyEvent.VK_UP) {
                    key.setKey_up(true);
                } if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    key.setKey_down(true);
                } if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    key.setKey_shift(true);
                } if (e.getKeyCode() == KeyEvent.VK_Z) {
                    key.setKey_z(true);
                } if (e.getKeyCode() == KeyEvent.VK_X) {
                    key.setKey_x(true);
                } if (e.getKeyCode() == KeyEvent.VK_C) {
                    key.setKey_c(true);
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    key.setKey_left(false);
                } if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    key.setKey_right(false);
                } if (e.getKeyCode() == KeyEvent.VK_UP) {
                    key.setKey_up(false);
                } if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    key.setKey_down(false);
                } if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    key.setKey_shift(false);
                } if (e.getKeyCode() == KeyEvent.VK_Z) {
                    key.setKey_z(false);
                } if (e.getKeyCode() == KeyEvent.VK_X) {
                    key.setKey_x(false);
                } if (e.getKeyCode() == KeyEvent.VK_C) {
                    key.setKey_c(false);
                }
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (start) {
                    // Moving left/right
                    double playerX = player.getX();
                    double playerY = player.getY();
                    if (!key.isKey_shift()) {
                        if (key.isKey_right() && player.getCenterX() < width) {
                            playerX += SPEED;
                        } if (key.isKey_left() && player.getCenterX() > 0) {
                            playerX -= SPEED;
                        } if (key.isKey_up() && player.getCenterY() > 0) {
                            playerY -= SPEED;
                        } if (key.isKey_down() && player.getCenterY() < height) {
                            playerY += SPEED;
                        }
                    }
                    if (key.isKey_shift()) {
                        if (key.isKey_right() && player.getCenterX() < width) {
                            playerX += FOCUSED_SPEED;
                        } if (key.isKey_left() && player.getCenterX() > 0) {
                            playerX -= FOCUSED_SPEED;
                        } if (key.isKey_up() && player.getCenterY() > 0) {
                            playerY -= FOCUSED_SPEED;
                        } if (key.isKey_down() && player.getCenterY() < height) {
                            playerY += FOCUSED_SPEED;
                        }
                    }
                    player.changeLocation(playerX, playerY);
                    // Going in focus mode
                    if (key.isKey_shift()) {
                        player.focus();
                    } if (!key.isKey_shift()) {
                        player.unfocus();
                    }
                    // Shooting bullets
                    if (key.isKey_z()) {
                        if (shotTime == 0) {
                            playerBullets.add(0, new PlayerBullet(1, player.getX(), player.getY(), 1));
                        }
                        shotTime++;
                        if (shotTime >= 15) {
                            shotTime = 0;
                        }
                    } else {
                        shotTime = 0;
                    }

                    // test TODO:remove this
                    if (key.isKey_x()) {
                        for(int i = 0; i < enemies.size(); i++) {
                            Enemy enemy = enemies.get(i);
                            enemies.remove(enemy);
                        }
                    }
                    sleep(5);
                }
            }
        }).start();
    }

    private void initPlayerBullets() {
        playerBullets = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(start) {
                    for (int i = 0; i < playerBullets.size(); i++) {
                        PlayerBullet playerBullet = playerBullets.get(i);
                        if (playerBullet != null) {
                            playerBullet.update();
                            if(!playerBullet.check()) {
                                playerBullets.remove(playerBullet);
                            }
                        } else {
                            playerBullets.remove(playerBullet);
                        }
                    }
                    sleep(1);
                }
            }
        }).start();
    }

    private void initBullets() {
        bullets = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(start) {
                    for (int i = 0; i < bullets.size(); i++) {
                        Bullet bullet = bullets.get(i);
                        if (bullet != null) {
                            bullet.update();
                            if(!bullet.check(width, height)) {
                                bullets.remove(bullet);
                            }
                        } else {
                            bullets.remove(bullet);
                        }
                    }
                    sleep(1);
                }
            }
        }).start();
    }

    private void drawBackground() {
        g2.setColor(new Color(30, 30, 30));
        g2.fillRect(0, 0, width, height);
    }

    private void drawGame() {
        for(int i = 0; i < playerBullets.size(); i++) {
            PlayerBullet playerBullet = playerBullets.get(i);
            if (playerBullet != null) {
                playerBullet.draw(g2);
            }
        }
        player.draw(g2);
        for(int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);
            if (bullet != null) {
                bullet.draw(g2);
            }
        }
        for(int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            if (enemy != null) {
                enemy.draw(g2);
            }
        }
    }

    private void render() {
        Graphics g = getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();

    }

    private void sleep(long speed) {
        try {
            Thread.sleep(speed);
        } catch (InterruptedException ex) {
            System.err.println(ex);
        }
    }

    public static int getFrame() {
        return frame;
    }
}
