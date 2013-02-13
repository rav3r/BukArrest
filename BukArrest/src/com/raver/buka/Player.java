package com.raver.buka;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.raver.buka.BukArrestGame.MapField;

public class Player extends Gridable {
	Sprite sprite;

	Player(MapField[][] map, int row, int col)
	{
		super(map, row, col);

		Texture playerTex = new Texture(Gdx.files.internal("data/player.png"));
		playerTex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		sprite = new Sprite(playerTex);
		sprite.setSize(40, 40);
	}

	public void draw(SpriteBatch batch) {
		sprite.setPosition(getX() - BukArrestGame.HSCREEN_W, getY() - BukArrestGame.HSCREEN_H);
		sprite.draw(batch);
	}
	
	void update(float delta)
	{
		float slowing = map[row][col].ice;
		if(slowing < 0) slowing = 0;
		speed = 5.0f * (1 - slowing);
		super.update(delta);
	}
}
