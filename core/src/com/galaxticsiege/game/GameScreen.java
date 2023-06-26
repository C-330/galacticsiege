package com.galaxticsiege.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Locale;

public class GameScreen implements Screen {

    private Game game;

    // bisa dijadikan generic
    // Screen
    private Camera camera;
    private Viewport viewport;

    // Graphics
    private SpriteBatch batch;
    private TextureAtlas textureAtlas;
    private Texture explosionTexture;

    private TextureRegion[] backgrounds;
    private TextureRegion playerShipTextureRegion,playerLaserTextureRegion,playerShieldTextureRegion
            ,enemyShipTextureRegion,enemyLaserTextureRegion,enemyShieldTextureRegion;


    // timing

    private float[] backgroundOffsets = {0,0,0,0};
    private float backgroundMaxScrollingSpeed;
    private float timeBetweenEnemySpawn = 3f;
    private float enemySpawnTimer = 0;


    // world parameters
    private final float WORLD_WIDTH = 72;
    private final float WORLD_HEIGHT = 128;
    private final float TOUCH_MOVEMENT_TRESHOLD = 0.5f;

    // Game objek
    private PlayerShip playerShip;
    private LinkedList<EnemyShip> enemyShipList;

    private LinkedList<Laser> playerLaserList;
    private LinkedList<Laser> enemyLaserList;
    private LinkedList<Explosion> explosionList;

    private  int score = 0;

    //HUD
    BitmapFont font;
    float hudVerticalMargin, hudLeftX,hudRightx,
            hudCentrX, hudRow1Y,hudRow2Y, hudSectionWidth;


    public GameScreen(Game game, int score) {
        this.game = game;
        this.score = score;
        camera = new OrthographicCamera();
        viewport = new StretchViewport(WORLD_WIDTH,WORLD_HEIGHT,camera);

        //texture atlas
        textureAtlas = new TextureAtlas("image.atlas");

        // bg
        backgrounds = new TextureRegion[4];
        backgrounds[0] = textureAtlas.findRegion("Starscape00");
        backgrounds[1] = textureAtlas.findRegion("Starscape01");
        backgrounds[2] = textureAtlas.findRegion("Starscape02");
        backgrounds[3] = textureAtlas.findRegion("Starscape03");

        backgroundMaxScrollingSpeed = (float) WORLD_HEIGHT / 4;

        // initialize texture region
        playerShipTextureRegion = textureAtlas.findRegion("playerShip3_blue");
        playerShieldTextureRegion = textureAtlas.findRegion("shield1");
        playerLaserTextureRegion = textureAtlas.findRegion("laserBlue04");
        enemyShipTextureRegion = textureAtlas.findRegion("enemyGreen2");
        enemyShieldTextureRegion = textureAtlas.findRegion("shield2");
        enemyShieldTextureRegion.flip(false,true);
        enemyLaserTextureRegion = textureAtlas.findRegion("laserGreen11");

        explosionTexture = new Texture("explosion.png");


        playerShip = new PlayerShip(47,10,WORLD_WIDTH/2,WORLD_HEIGHT/4,10,10,
                playerShipTextureRegion,playerShieldTextureRegion,playerLaserTextureRegion,0.4f,4,45,0.5f);

        playerLaserList = new LinkedList<>();
        enemyLaserList = new LinkedList<>();
        enemyShipList = new LinkedList<>();
        explosionList = new LinkedList<>();


        batch = new SpriteBatch();

        prepareHUD();

    }

    private void prepareHUD(){
        FreeTypeFontGenerator fontGenerator =
                new FreeTypeFontGenerator(Gdx.files.internal("EdgeOfTheGalaxy.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter =
                new FreeTypeFontGenerator.FreeTypeFontParameter();

        fontParameter.size = 72;
        fontParameter.borderWidth = 3.6f;
        fontParameter.color = new Color(1,1,1,0.3f);
        fontParameter.borderColor = new Color(0,0,0,0.3f);

        font = fontGenerator.generateFont(fontParameter);

        font.getData().setScale(0.08f);

        hudVerticalMargin = font.getCapHeight()/2;
        hudLeftX = hudVerticalMargin;
        hudRightx = WORLD_WIDTH * 2/3 - hudLeftX;
        hudCentrX = WORLD_WIDTH / 3;
        hudRow1Y = WORLD_HEIGHT - hudVerticalMargin;
        hudRow2Y = hudRow1Y - hudVerticalMargin - font.getCapHeight();
        hudSectionWidth = WORLD_WIDTH / 3;

    }

    @Override
    public void render(float deltaTime) {
        batch.begin();

        renderBackground(deltaTime);

        detectInput(deltaTime);
        playerShip.update(deltaTime);

        spawnEnemyShips(deltaTime);

        ListIterator<EnemyShip> enemyShipListIterator = enemyShipList.listIterator();
        while (enemyShipListIterator.hasNext()){
            EnemyShip enemyShip = enemyShipListIterator.next();
            moveEnemy(enemyShip,deltaTime);
            enemyShip.update(deltaTime);
            enemyShip.draw(batch);

        }


        playerShip.draw(batch);

        renderLasers(deltaTime);

        detectCollisions();

        renderExplotions(deltaTime);

        updateAndRenderHUD();

        batch.end();
    }

    private void updateAndRenderHUD(){
        // Baris hud atas
        font.draw(batch,"SCORE", hudLeftX,hudRow1Y,hudSectionWidth, Align.left,false);
        font.draw(batch, "SHIELD", hudCentrX,hudRow1Y,hudSectionWidth,Align.center,false);
        font.draw(batch,"LIVES", hudRightx,hudRow1Y,hudSectionWidth,Align.right,false);
        //
        font.draw(batch,String.format(Locale.getDefault(),"%06d",score),
                hudLeftX,hudRow2Y,hudSectionWidth, Align.left,false);
        font.draw(batch,String.format(Locale.getDefault(),"%02d", playerShip.shield),
                hudCentrX,hudRow2Y,hudSectionWidth, Align.center,false);
        font.draw(batch,String.format(Locale.getDefault(),"%02d", playerShip.getLives()),
                hudRightx,hudRow2Y,hudSectionWidth, Align.right,false);
    }

    private void spawnEnemyShips(float deltaTime){
        enemySpawnTimer += deltaTime;

        if (enemySpawnTimer > timeBetweenEnemySpawn){
            enemyShipList.add(new EnemyShip(45,2,
                    GalacticSiegeGame.random.nextFloat()*(WORLD_WIDTH-10)+5,WORLD_HEIGHT-5,
                    10,10,
                    enemyShipTextureRegion,enemyShieldTextureRegion,enemyLaserTextureRegion,
                    0.3f,5,50,0.8f));
            enemySpawnTimer -= timeBetweenEnemySpawn;
        }
    }

    private void detectInput(float deltaTime){
        // kbd
        float leftLimit,rightLimit,upLimit,downLimit;
        leftLimit = -playerShip.boundingBox.x;
        downLimit = -playerShip.boundingBox.y;
        rightLimit = WORLD_WIDTH - playerShip.boundingBox.x - playerShip.boundingBox.width;
        upLimit = (float) WORLD_HEIGHT/2 - playerShip.boundingBox.y - playerShip.boundingBox.height;

        if (Gdx.input.isKeyPressed(Input.Keys.D) && rightLimit > 0){
            // float deltaX = playerShip.movementSpeed*deltaTime;
            // deltaX = Math.min(deltaX, rightLimit);
            // playerShip.translate(deltaX, 0f);
            playerShip.translate(Math.min(playerShip.movementSpeed*deltaTime, rightLimit),0f);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W) && upLimit > 0){
            playerShip.translate(0f,Math.min(playerShip.movementSpeed*deltaTime, upLimit));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) && leftLimit < 0){
            playerShip.translate(Math.max(-playerShip.movementSpeed*deltaTime, leftLimit),0f);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) && downLimit < 0){
            playerShip.translate(0f,Math.max(-playerShip.movementSpeed*deltaTime, downLimit));
        }

        if (Gdx.input.isTouched()){
            float xTouchPixel = Gdx.input.getX();
            float yTouchPixel = Gdx.input.getY();

            Vector2 touchPoint = new Vector2(xTouchPixel,yTouchPixel);
            touchPoint = viewport.unproject(touchPoint);

            Vector2 playerShipCentre = new Vector2(
                    playerShip.boundingBox.x + playerShip.boundingBox.width/2,
                    playerShip.boundingBox.y + playerShip.boundingBox.height/2);

            float touchDistance = touchPoint.dst(playerShipCentre);

            if (touchDistance > TOUCH_MOVEMENT_TRESHOLD){
                float xTouchDifference = touchPoint.x - playerShipCentre.x;
                float yTouchDifference = touchPoint.y - playerShipCentre.y;

                float xMove = xTouchDifference / touchDistance
                        * playerShip.movementSpeed * deltaTime;
                float yMove = yTouchDifference / touchDistance
                        * playerShip.movementSpeed * deltaTime;

                if (xMove > 0 ){
                    xMove = Math.min(xMove,rightLimit);
                } else {
                    xMove = Math.max(xMove, leftLimit);
                }
                if (yMove > 0 ){
                    yMove = Math.min(yMove,upLimit);
                } else {
                    yMove = Math.max(yMove, downLimit);
                }

                playerShip.translate(xMove,yMove);
            }
        }
    }

    private void moveEnemy(EnemyShip enemyShip,float deltaTime){

        float leftLimit,rightLimit,upLimit,downLimit;
        leftLimit = -enemyShip.boundingBox.x;
        downLimit = (float) WORLD_HEIGHT/2 - enemyShip.boundingBox.y;
        rightLimit = WORLD_WIDTH - enemyShip.boundingBox.x - enemyShip.boundingBox.width;
        upLimit = WORLD_HEIGHT - enemyShip.boundingBox.y - enemyShip.boundingBox.height;


        float xMove = enemyShip.getDirectionVector().x
                * enemyShip.movementSpeed * deltaTime;
        float yMove = enemyShip.getDirectionVector().y
                * enemyShip.movementSpeed * deltaTime;

        if (xMove > 0 ){
            xMove = Math.min(xMove,rightLimit);
        } else {
            xMove = Math.max(xMove, leftLimit);
        }

        if (yMove > 0 ){
            yMove = Math.min(yMove,upLimit);
        } else {
            yMove = Math.max(yMove, downLimit);
        }
        enemyShip.translate(xMove,yMove);
    }

    private void detectCollisions() {
        ListIterator<Laser> laserListIterator = playerLaserList.listIterator();
        while (laserListIterator.hasNext()) {
            Laser laser = laserListIterator.next();
            ListIterator<EnemyShip> enemyShipListIterator = enemyShipList.listIterator();
            while (enemyShipListIterator.hasNext()){
                EnemyShip enemyShip = enemyShipListIterator.next();

                if (enemyShip.intersects(laser.boundingBox)){
                    if (enemyShip.hitAndCheckDestroyed(laser)){
                        enemyShipListIterator.remove();
                        explosionList.add(
                                new Explosion(explosionTexture,
                                new Rectangle(enemyShip.boundingBox),
                                0.7f));
                        score += 100;
                    }

                    laserListIterator.remove();
                    break;
                }
            }
        }

        laserListIterator = enemyLaserList.listIterator();
        while (laserListIterator.hasNext()) {
            Laser laser = laserListIterator.next();
            if (playerShip.intersects(laser.boundingBox)) {
                if(playerShip.hitAndCheckDestroyed(laser)){
                    explosionList.add(
                            new Explosion(explosionTexture,
                                    new Rectangle(playerShip.boundingBox),
                                    0.7f));
                    playerShip.shield =10;
                    playerShip.minusLive();
                    if (playerShip.isDead()){
                        game.setScreen(new GameOverScreen(game,score));
                    }
                }
                laserListIterator.remove();
            }
        }
    }


    private void renderExplotions(float deltaTime){
        ListIterator<Explosion> explosionListIterator = explosionList.listIterator();
        while (explosionListIterator.hasNext()){
            Explosion explosion = explosionListIterator.next();
            explosion.update(deltaTime);
            if (explosion.isFinished()){
                explosionListIterator.remove();
            } else {
                explosion.draw(batch);
            }
        }
    }

    private void renderLasers(float deltaTime){
        if (playerShip.canFireLaser()){
            Laser[] lasers = playerShip.fireLasers();
            for (Laser laser: lasers){
                playerLaserList.add(laser);
            }
        }

        ListIterator<EnemyShip> enemyShipListIterator = enemyShipList.listIterator();
        while (enemyShipListIterator.hasNext()) {
            EnemyShip enemyShip = enemyShipListIterator.next();
            if (enemyShip.canFireLaser()) {
                Laser[] lasers = enemyShip.fireLasers();
                enemyLaserList.addAll(Arrays.asList(lasers));
            }
        }
        ListIterator<Laser> iterator = playerLaserList.listIterator();
        while (iterator.hasNext()){
            Laser laser = iterator.next();
            laser.draw(batch);
            laser.boundingBox.y += laser.movementSpeed * deltaTime;
            if (laser.boundingBox.y > WORLD_HEIGHT){
                iterator.remove();
            }
        }
        iterator = enemyLaserList.listIterator();
        while (iterator.hasNext()){
            Laser laser = iterator.next();
            laser.draw(batch);
            laser.boundingBox.y -= laser.movementSpeed * deltaTime;
            if (laser.boundingBox.y + laser.boundingBox.height <= 0){
                iterator.remove();
            }
        }
    }

    private void renderBackground(float deltaTime){

        backgroundOffsets[0] += deltaTime * backgroundMaxScrollingSpeed / 8;
        backgroundOffsets[1] += deltaTime * backgroundMaxScrollingSpeed / 4;
        backgroundOffsets[2] += deltaTime * backgroundMaxScrollingSpeed / 2;
        backgroundOffsets[3] += deltaTime * backgroundMaxScrollingSpeed;

        for (int layer = 0; layer < backgroundOffsets.length; layer++){
            if (backgroundOffsets[layer] > WORLD_HEIGHT) {
                backgroundOffsets[layer] = 0;
            }
            batch.draw(backgrounds[layer],0,-backgroundOffsets[layer],
                    WORLD_WIDTH,WORLD_HEIGHT);
            batch.draw(backgrounds[layer],0,-backgroundOffsets[layer] + WORLD_HEIGHT,
                    WORLD_WIDTH,WORLD_HEIGHT);

        }

    }


    @Override
    public void resize(int width, int height) {
        viewport.update(width, height,true);
        batch.setProjectionMatrix(camera.combined);

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void show() {

    }
}
