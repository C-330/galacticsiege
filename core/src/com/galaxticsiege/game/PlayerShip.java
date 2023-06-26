package com.galaxticsiege.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class PlayerShip extends Ship{

    private int lives;
    public PlayerShip(float movementSpeed, int shield, float xCentre, float yCentre,
                      float width, float height, TextureRegion shipTexture,
                      TextureRegion shieldTexture, TextureRegion laserTextureRegion,
                      float laserWidth, float laserHeight,
                      float laserMovementSpeed, float timeBetweenShots) {
        super(movementSpeed, shield, xCentre, yCentre, width, height,
                shipTexture, shieldTexture,
                laserTextureRegion, laserWidth, laserHeight, laserMovementSpeed, timeBetweenShots);
        lives = 4;
    }

    @Override
    public Laser[] fireLasers() {
        Laser[] lasers = new Laser[2];
        lasers[0] = new Laser(boundingBox.x +boundingBox.width *0.1f, boundingBox.y+ boundingBox.height*0.45f
                                ,laserWidth,laserHeight
                                ,laserMovementSpeed,laserTextureRegion);
        lasers[1] = new Laser(boundingBox.x +boundingBox.width*0.9f,boundingBox.y+ boundingBox.height*0.45f
                ,laserWidth,laserHeight
                ,laserMovementSpeed,laserTextureRegion);

        timeSinceLastShot = 0;

        return lasers;
    }
    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public void minusLive(){
        lives--;
    }
    public boolean isDead(){
        return lives <= 0;
    }

}
