package com.raver.buka;

import java.io.IOException;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
	
	private Texture emptyTexture;
	private Sprite emptySprite;
	
	private Texture wallTexture;
	private Sprite wallSprite;
	
	private Texture fireTexture;
	private Sprite fireSprite;
	
	private Texture iceTexture;
	private Sprite iceSprite;
	
	static final int MAP_WIDTH = 800/40;
	static final int MAP_HEIGHT = 600/40 - 1; // 40px for hud
	
	static final float HSCREEN_W = 400;
	static final float HSCREEN_H = 300;
	
	boolean lastSpace = false;
	
	float bukaTemp = 100.0f;
	
	class MapField
	{
		boolean w;
		boolean fire;
		float ice;
	}
	
	private MapField[][] wallsMap = new MapField[MAP_HEIGHT][MAP_WIDTH];
	
	public Player player;
	public Buka buka;
	
	public int maxFires = 5;
	public int fires = 0;
	
	public int bukaLifes = 3;
	
	static public BukArrestGame self;
	
	@Override
	public void create() {
		self = this;
		
		for(int i=0; i<wallsMap.length; i++)
			for(int j=0; j<wallsMap[i].length; j++)
				wallsMap[i][j] = new MapField();
		
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera(800, 600);
		batch = new SpriteBatch();
		
		emptyTexture = new Texture(Gdx.files.internal("data/empty.png"));
		emptyTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		emptySprite = new Sprite(emptyTexture);
		emptySprite.setSize(40, 40);
		
		wallTexture = new Texture(Gdx.files.internal("data/wall.png"));
		wallTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		wallSprite = new Sprite(wallTexture);
		wallSprite.setSize(40, 40);
		
		fireTexture = new Texture(Gdx.files.internal("data/fire.png"));
		fireTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		fireSprite = new Sprite(fireTexture);
		fireSprite.setSize(40, 40);
		
		iceTexture = new Texture(Gdx.files.internal("data/ice.png"));
		iceTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		iceSprite = new Sprite(iceTexture);
		iceSprite.setSize(40, 40);
		
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
				wallsMap[MAP_HEIGHT - row - 1][col].w = wtable.charAt(i) == '1';
				wallsMap[MAP_HEIGHT - row - 1][col].fire = false;
				wallsMap[MAP_HEIGHT - row - 1][col].ice = 0;
				col++;
			}
			
			XmlReader.Element actors = element.getChildByName("actors");
			XmlReader.Element bukaEl = actors.getChildByName("buka");
			XmlReader.Element playerEl = actors.getChildByName("you");
			
			player = new Player(wallsMap, MAP_HEIGHT - playerEl.getInt("y")/40 - 1, playerEl.getInt("x")/40);
			buka = new Buka(wallsMap, MAP_HEIGHT - bukaEl.getInt("y")/40 - 1, bukaEl.getInt("x")/40);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void dispose() {
		batch.dispose();
	}

	@Override
	public void render() {		
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		// hud
		for(int i=0; i<maxFires-fires; i++)
		{
			fireSprite.setPosition(i*40-HSCREEN_W, 600-40-HSCREEN_H);
			fireSprite.draw(batch);
		}
		for(int i=0; i<bukaLifes; i++)
		{
			buka.sprite.setPosition(HSCREEN_W-i*40-40, 600-40-HSCREEN_H);
			buka.sprite.draw(batch);
		}
		
		for(int row = 0; row < MAP_HEIGHT; row++)
			for(int col = 0; col < MAP_WIDTH; col++)
			{
				Sprite spr = wallsMap[row][col].w ? wallSprite : emptySprite;
				spr.setPosition(col*40-HSCREEN_W, row*40-HSCREEN_H);
				spr.draw(batch);
				
				if(wallsMap[row][col].ice > 0)
				{
					float alpha = wallsMap[row][col].ice;
					if(alpha > 0.9f) alpha = 1.0f - (alpha - 0.9f)*10.0f;
					else alpha /= 0.9f;
					
					iceSprite.setColor(1,1,1,alpha);
					iceSprite.setPosition(col*40-HSCREEN_W, row*40-HSCREEN_H);
					iceSprite.draw(batch);
				}
				
				if(wallsMap[row][col].fire)
				{
					fireSprite.setPosition(col*40-HSCREEN_W, row*40-HSCREEN_H);
					fireSprite.draw(batch);
				}
			}
		
		player.draw(batch);
		buka.draw(batch);
		batch.end();
		
		if(Gdx.input.isKeyPressed(Input.Keys.UP)) player.moveUp();
		if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) player.moveDown();
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) player.moveLeft();
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) player.moveRight();
		
		boolean space = Gdx.input.isKeyPressed(Input.Keys.SPACE);
		if(space && !lastSpace)
		{
			if(wallsMap[player.row][player.col].fire == true)
			{
				wallsMap[player.row][player.col].fire = false;
				fires--;
			} else if(fires < maxFires)
			{
				wallsMap[player.row][player.col].fire = true;
				fires++;
			}
		}
		lastSpace = space;
		
		for(int row = 0; row < MAP_HEIGHT; row++)
			for(int col = 0; col < MAP_WIDTH; col++)
				wallsMap[row][col].ice -= 0.1f*Gdx.graphics.getDeltaTime();
		
		player.update(Gdx.graphics.getDeltaTime());
		buka.update(Gdx.graphics.getDeltaTime());
	}

	public void burnBuka()
	{
		
		fires = 0;
		for(int row = 0; row < MAP_HEIGHT; row++)
			for(int col = 0; col < MAP_WIDTH; col++)
			{
				wallsMap[row][col].ice = 1;
				wallsMap[row][col].fire = false;
			}
		bukaLifes--;
		if(bukaLifes == 0)
		{
			
		}
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
