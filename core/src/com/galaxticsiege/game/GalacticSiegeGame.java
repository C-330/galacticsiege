package com.galaxticsiege.game;

import com.badlogic.gdx.Game;

import java.util.Random;

public class GalacticSiegeGame extends Game {

	GameScreen gameScreen;
	GameOverScreen gameOverScreen;

	public static Random random = new Random();

	@Override
	public void dispose() {
		super.dispose();
		gameScreen.dispose();

	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void resize(int width, int height) {
		gameScreen.resize(width, height);

	}

	@Override
	public void create() {
		gameScreen = new GameScreen(this,0);
		setScreen(gameScreen);

	}
}
