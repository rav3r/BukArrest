package com.raver.buka;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Player {
	Sprite sprite;
	int col;
	int row;
	
	Player(int row, int col)
	{
		this.col = col;
		this.row = row;
		
		Texture playerTex = new Texture(Gdx.files.internal("data/player.png"));
		playerTex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		sprite = new Sprite(playerTex);
		sprite.setSize(40, 40);
	}

	public void draw(SpriteBatch batch) {
		sprite.setPosition(col*40 - BukArrestGame.HSCREEN_W, row*40 - BukArrestGame.HSCREEN_H);
		sprite.draw(batch);
	}
}
