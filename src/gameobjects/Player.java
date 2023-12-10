package gameobjects;

import utils.Vector3d;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

public class Player extends GameObject {

    public int jumpToken = 0;
    public final double jumpSpeed = 35;

    public Player(int x, int y, int size) {
        super(new Vector3d(x, y,0), size);
        pos = new Vector3d(x, y, 0);
        vel = new Vector3d(0,0,0);
        collisionBox = new Rectangle2D.Double(pos.x, pos.y, size, size);
        this.size = size;
        accX = 0.5;

        try {
            // TODO find image
            image = ImageIO.read(new File("./assets/player.png"))
                    .getScaledInstance((size), size, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void update() {
        // TODO fix value for better movement
        vel.x = (Math.abs(vel.x) < 10) ? (vel.x) * 0.95 : 10 * vel.x / Math.abs(vel.x) * 0.98;
        vel.x = (Math.abs(vel.x) > 0.3) ? vel.x: 0;
        vel.y -= (vel.y > 3) ? 3 - jumpToken * jumpSpeed : accY - jumpToken * jumpSpeed;

        pos.x += vel.x;
        pos.y += vel.y;
        jumpToken = (jumpToken != 0) ? ((jumpToken > 0) ? jumpToken - 1 : jumpToken + 1) : 0;
        collisionBox = new Rectangle2D.Double(pos.x - (double) size / 2, pos.y - (double) size / 2, size, size);
    }


    public void accelerate() {
        vel.x += 3;
    }

    public void decelerate() {
        vel.x -= 3;
    }

    public void jump() {
        // TODO fix jump for better movement
        jumpToken++;
    }

}
