package com.galaxticsiege.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import com.badlogic.gdx.math.Rectangle;

public abstract class Ship {

    float movementSpeed; //world unit per sec
    int shield;



    Rectangle boundingBox;

    float laserWidth,laserHeight;
    float laserMovementSpeed;
    float timeBetweenShots;
    float timeSinceLastShot =0;

    TextureRegion shipTextureRegion;
    TextureRegion shieldTextureRegion;
    TextureRegion laserTextureRegion;


    public Ship(float movementSpeed, int shield,
                float xCentre, float yCentre,
                float width, float height,
                TextureRegion shipTexture, TextureRegion shieldTexture
                ,TextureRegion laserTextureRegion, float laserWidth, float laserHeight,
                float laserMovementSpeed, float timeBetweenShots) {
        this.movementSpeed = movementSpeed;
        this.shield = shield;
        this.boundingBox = new Rectangle(xCentre - width/2,yCentre - width/2,width,height);
        this.laserWidth = laserWidth;
        this.laserHeight = laserHeight;
        this.timeBetweenShots = timeBetweenShots;
        this.laserMovementSpeed =laserMovementSpeed;
        this.shipTextureRegion = shipTexture;
        this.shieldTextureRegion = shieldTexture;
        this.laserTextureRegion = laserTextureRegion;

    }

    public void update(float deltaTime){
        timeSinceLastShot += deltaTime;
    }

    public boolean canFireLaser(){
        return (timeSinceLastShot - timeBetweenShots >=0);
    }

    public abstract Laser[] fireLasers();
    public boolean hitAndCheckDestroyed(Laser laser){
        if (shield >0){
            shield--;
            return false;
        }
        return true;
    }

    public boolean intersects(Rectangle otherRectangle){
        return (boundingBox.overlaps(otherRectangle));
    }

    public void translate(float deltaX, float deltaY){
        boundingBox.setPosition(boundingBox.x + deltaX, boundingBox.y + deltaY);
    }

    public void draw(Batch batch){
        batch.draw(shipTextureRegion,boundingBox.x,boundingBox.y,boundingBox.width,boundingBox.height);
        if (shield > 0 ){
            batch.draw(shieldTextureRegion,boundingBox.x,boundingBox.y,boundingBox.width,boundingBox.height);
        }
    }
}
