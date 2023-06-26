package com.galaxticsiege.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class EnemyShip extends Ship{

    Vector2 directionVector;
    float timeSinceLastDirectionChange = 0;
    float directionChangeFreq = 0.75f;
    public EnemyShip(float movementSpeed, int shield, float xCentre, float yCentre,
                     float width, float height, TextureRegion shipTexture,
                     TextureRegion shieldTexture, TextureRegion laserTextureRegion,
                     float laserWidth, float laserHeight,
                     float laserMovementSpeed, float timeBetweenShots) {
        super(movementSpeed, shield, xCentre, yCentre, width, height,
                shipTexture, shieldTexture,
                laserTextureRegion, laserWidth, laserHeight, laserMovementSpeed, timeBetweenShots);

        directionVector = new Vector2(0,-1);
    }

    public Vector2 getDirectionVector() {
        return directionVector;
    }

    private void randomizeDirectionVector() {
        double bearing = GalacticSiegeGame.random.nextDouble()*6.283185; // karena pakai rad jadi 0 sampai 2*pi
        directionVector.x = (float)Math.sin(bearing);
        directionVector.y = (float)Math.cos(bearing);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        timeSinceLastDirectionChange += deltaTime;
        if (timeSinceLastDirectionChange > directionChangeFreq){
            randomizeDirectionVector();
            timeSinceLastDirectionChange -= directionChangeFreq;
        }
    }

    @Override
    public Laser[] fireLasers() {
        Laser[] lasers = new Laser[2];
        lasers[0] = new Laser(boundingBox.x +boundingBox.width *0.18f,boundingBox.y - laserHeight
                ,laserWidth,laserHeight
                ,laserMovementSpeed,laserTextureRegion);
        lasers[1] = new Laser(boundingBox.x +boundingBox.width *0.82f,boundingBox.y - laserHeight
                ,laserWidth,laserHeight
                ,laserMovementSpeed,laserTextureRegion);

        timeSinceLastShot = 0;

        return lasers;
    }
    @Override
    public void draw(Batch batch){
        batch.draw(shipTextureRegion,boundingBox.x,boundingBox.y,boundingBox.width,boundingBox.height);
        if (shield > 0 ){
            batch.draw(shieldTextureRegion,boundingBox.x,boundingBox.y-boundingBox.height*0.2f,boundingBox.width,boundingBox.height);
        }
    }
}
