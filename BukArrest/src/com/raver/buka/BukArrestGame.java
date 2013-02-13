package com.raver.buka;

import java.io.IOException;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.XmlReader;

public class BukArrestGame implements ApplicationListener {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Texture texture;
	private Sprite sprite;
	
	private Texture emptyTexture;
	private Sprite emptySprite;
	
	private Texture wallTexture;
	private Sprite wallSprite;
	
	private final int MAP_WIDTH = 800/40;
	private final int MAP_HEIGHT = 600/40;
	
	private final float HSCREEN_W = 400;
	private final float HSCREEN_H = 300;
	
	private boolean[][] wallsMap = new boolean[MAP_HEIGHT][MAP_WIDTH];
	
	@Override
	public void create() {		
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera(800, 600);
		batch = new SpriteBatch();
		
		texture = new Texture(Gdx.files.internal("data/libgdx.png"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		TextureRegion region = new TextureRegion(texture, 0, 0, 512, 275);
		
		sprite = new Sprite(region);
		sprite.setSize(100, 100);
		sprite.setOrigin(sprite.getWidth()/2, sprite.getHeight()/2);
		sprite.setPosition(-sprite.getWidth()/2, -sprite.getHeight()/2);
		
		emptyTexture = new Texture(Gdx.files.internal("data/empty.png"));
		emptyTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		emptySprite = new Sprite(emptyTexture);
		emptySprite.setSize(40, 40);
		
		wallTexture = new Texture(Gdx.files.internal("data/wall.png"));
		wallTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		wallSprite = new Sprite(wallTexture);
		wallSprite.setSize(40, 40);
		
		XmlReader xmlReader = new XmlReader();
		
		try {
			XmlReader.Element element = xmlReader.parse(Gdx.files.internal("data/level.oel"));
			
			
			XmlReader.Element walls = element.getChildByName("walls");
			String wtable = walls.getText();
			
			int col = 0;
			int row = 0;
			for(int i=0; i<wtable.length(); i++)
			{
				if(wtable.charAt(i) == '\n')
				{
					col = 0;
					row++;
					continue;
				}
				wallsMap[MAP_HEIGHT - row - 1][col] = wtable.charAt(i) == '1';
				col++;
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void dispose() {
		batch.dispose();
		texture.dispose();
	}

	@Override
	public void render() {		
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		sprite.draw(batch);
		
		for(int row = 0; row < MAP_HEIGHT; row++)
			for(int col = 0; col < MAP_WIDTH; col++)
			{
				Sprite spr = wallsMap[row][col] ? wallSprite : emptySprite;
				spr.setPosition(col*40-HSCREEN_W, row*40-HSCREEN_H);
				spr.draw(batch);
			}
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
