package com.galaxticsiege.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;


public class Explosion {
    private Animation<TextureRegion> explotionAnimation;
    private float explosionTimer;

    private Rectangle boundingBox;

    public Explosion(Texture texture,Rectangle boudingBox, float totalAnimationTime) {
        this.boundingBox = boudingBox;

        //split 4x4
        TextureRegion[][] textureRegions2D =
                TextureRegion.split(texture,64,64);

        TextureRegion[] textureRegions1D = new TextureRegion[16];
        int index = 0;

        // 4x4 tile dari texture jadi i= 4, j= 4
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                textureRegions1D[index] = textureRegions2D[i][j];
                index++;
            }
        }
        explotionAnimation = new Animation<TextureRegion>(totalAnimationTime/16,textureRegions1D);
        explosionTimer = 0;
    }

    public void update(float deltaTime){
        explosionTimer += deltaTime;
    }

    public void draw(SpriteBatch batch){
        batch.draw(explotionAnimation.getKeyFrame(explosionTimer),
                boundingBox.x,boundingBox.y,
                boundingBox.width,boundingBox.height);

    }

    public boolean isFinished(){
        return explotionAnimation.isAnimationFinished(explosionTimer);
    }
}
