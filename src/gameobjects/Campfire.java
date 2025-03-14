package gameobjects;

import utils.Vector;

import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Random;

public class Campfire extends GameObject implements Serializable {

    private final double lightRadius;
    private int currentFrame;

    public static int FIRE_RENDER_SOUND_DISTANCE = 800;

    public Campfire(Campfire campfire) {
        super(campfire);
        this.lightRadius = campfire.lightRadius;
        this.currentFrame = campfire.currentFrame;
    }

    public Campfire(Vector pos, double lightRadius) {
        super(pos, 50);
        this.lightRadius = lightRadius;
        this.collisionBox = new Rectangle2D.Double(pos.getX(), pos.getY(), 50, 50);
        currentFrame = new Random().nextInt(0, 4);

        imageRef = "./assets/fire/fire_" + currentFrame + ".png";
    }



    @Override
    public void animate() {
        currentFrame = (currentFrame + 1) % 4;
        imageRef = "./assets/fire/fire_" + currentFrame + ".png";
    }

    public double getLightRadius() {
        return lightRadius;
    }

    public Area getLightArea() {
        return new Area(new Ellipse2D.Double(getX() + (getSize() - lightRadius) / 2,
                getY() + (getSize() - lightRadius) / 2,
                lightRadius, lightRadius));
    }
}
