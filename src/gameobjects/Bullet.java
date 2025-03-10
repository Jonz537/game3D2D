package gameobjects;

import utils.Vector;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;

public class Bullet extends GameObject implements Serializable {

    private int ttl = 1200;

    public Bullet(Bullet bullet) {
        super(bullet);
        this.ttl = bullet.ttl;
    }

    public Bullet(Vector pos, int size, double vecX, double vecY, double accY) {
        super(new Vector(pos), size, 0, accY);
        vel = new Vector(vecX, vecY, 0);
        collisionBox = new Rectangle2D.Double(pos.getX(), pos.getY(), size, size);

        //TODO find an image
        imageRef = "./assets/ghost.png";
    }

    public void update() {
        vel.addY(-((vel.getY() > 3) ? 3 : accY));
        pos.addX(vel.getX());
        pos.addY(vel.getY());
        collisionBox = new Rectangle2D.Double(pos.getX(), pos.getY(), size, size);
        ttl--;
    }

    public boolean timeOut() {
        return ttl <= 0;
    }
}
