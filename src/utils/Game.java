package utils;

import gameobjects.*;

import java.net.Socket;
import java.util.*;

public class Game {

    protected Player player;
    protected HashMap<Socket, Player> players = new HashMap<>();
    protected ArrayList<GameObject> entities = new ArrayList<>();
    protected Vector spawnPoint = new Vector(0,0,0);

    private Random random = new Random();

    public Game() {
        player = new Player(new Vector(0,0,0), 0);
    }

    public void init() {

        entities.add(new Campfire(new Vector(0, -130, 0), 150));
        entities.add(new Campfire(new Vector(100 + Platform.IMG_LENGTH * 3, -30, 0), 350));
        entities.add(new Tent(new Vector(150 + Platform.IMG_LENGTH * 3, -30, 0), 80));
        entities.add(new Campfire(new Vector(100 + Platform.IMG_LENGTH * 7, -230, 0), 150));

        entities.add(new Platform(new Vector(0, -200, 0), Platform.IMG_LENGTH * 2));
        entities.add(new Platform(new Vector(Platform.IMG_LENGTH * 3, -100, 0), Platform.IMG_LENGTH * 4));
        entities.add(new Platform(new Vector(Platform.IMG_LENGTH * 7, -300, 0), Platform.IMG_LENGTH * 7));

    }

    public Player getPlayer() {
        return players.getOrDefault(null, player);
    }


    public Player getPlayer(Socket client) {
        return players.get(client);
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void addPlayer(Socket client) {
        Player play = new Player(new Vector(0,0,0), 50);
        entities.add(play);
        players.put(client, play);
    }

    public void removePlayer(Socket socket) {
        removeEntity(players.remove(socket));
    }

    public ArrayList<GameObject> getEntities() {
        return entities;
    }

    public void setEntities(ArrayList<GameObject> entities) {
        this.entities = entities;
    }

    public void addEntity(GameObject gameObject) {
        entities.add(gameObject);
    }

    public void removeEntity(GameObject gameObject) {
        entities.remove(gameObject);
    }

    public Vector getSpawnPoint() {
        return spawnPoint;
    }

    public void setSpawnPoint(Vector spawnPoint) {
        this.spawnPoint = new Vector(spawnPoint.getX(), spawnPoint.getY() + 50, 0);
    }

    public void update() {
        synchronized (entities) {

            entities.sort(Comparator.comparingDouble(GameObject::getZ));

            entities.forEach(gameObject -> {
            if (gameObject instanceof Ghost) {
                ((Ghost) gameObject).followPlayer();
            }
                synchronized (this) {
                    gameObject.update();
                }
            });

            checkPlayerDeath();
            checkToDestroyed();
        }
    }

    public void updateImages() {
        synchronized (entities) {
            entities.forEach(GameObject::animate);
        }
    }

    public void checkCollisions() {
        Set<GameObject> toDestroy = new HashSet<>();

        synchronized (entities) {
            for (GameObject playerToBeChecked: getEntities()) {

                if (playerToBeChecked instanceof Player) {
                    ((Player) playerToBeChecked).setTouchingFloor(false);
                    for (GameObject go: getEntities()) {
                        //TODO collision left right
                        if (go instanceof Platform && playerToBeChecked.getCollisionBox().intersects(go.getCollisionBox())) {

                            if (playerToBeChecked.getVelY() <= 0) {
                                playerToBeChecked.setVelY(0);
                                ((Player) playerToBeChecked).setTouchingFloor(true);
                            } else {
                                playerToBeChecked.setY(playerToBeChecked.getY() - playerToBeChecked.getVelY());
                            }
                        }
                        if (go instanceof  Tent && playerToBeChecked.getCollisionBox().intersects(go.getCollisionBox())) {
                            setSpawnPoint(go.getPos());
                        }
                        if (go instanceof Ghost && playerToBeChecked.getCollisionBox().intersects(go.getCollisionBox())) {
                            ((Player) playerToBeChecked).die(spawnPoint);
                            toDestroy.add(go);
                        }
                    }
                }
            }

            for (GameObject go: toDestroy) {
                entities.remove(go);
            }
        }
    }

    private void checkPlayerDeath() {
        synchronized (this) {
            for (GameObject go: entities) {
                if (go instanceof Player) {
                    if (go.getY() < -600) {
                        go.setVelY(0);
                        ((Player) go).die(spawnPoint);
                    }
                }
            }
        }
    }

    private void checkToDestroyed() {
        Set<Bullet> toBeDestroyed = new HashSet<>();
        for (GameObject go: entities) {
            if (go instanceof Bullet && ((Bullet) go).timeOut()) {
                toBeDestroyed.add((Bullet) go);
            }
        }

        for (Bullet bullet: toBeDestroyed) {
            entities.remove(bullet);
        }

    }
}
