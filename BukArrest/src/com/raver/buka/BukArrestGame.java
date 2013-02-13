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
	
	private Texture tempTexture;
	private Sprite tempSprite;
	
	private Texture bukaBurntTexture;
	private Sprite bukaBurntSprite;
	private Texture happyEndTexture;
	private Sprite happyEndSprite;
	private Texture deadTexture;
	private Sprite deadSprite;
	
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
	
	enum GameState
	{
		Gameplay,
		Frozen,
		BukaBurnt,
		HappyEnd
	}
	GameState gameState = GameState.Gameplay;
	
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
		
		tempTexture = new Texture(Gdx.files.internal("data/tempBar.png"));
		tempTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		tempSprite = new Sprite(tempTexture);
		tempSprite.setSize(40*6, 40);
		
		bukaBurntTexture = new Texture(Gdx.files.internal("data/bukaBurnt.png"));
		bukaBurntTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		bukaBurntSprite = new Sprite(bukaBurntTexture);
		bukaBurntSprite.setSize(256, 256);
		bukaBurntSprite.setPosition(-128, -128);
		
		happyEndTexture = new Texture(Gdx.files.internal("data/happyEnd.png"));
		happyEndTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		happyEndSprite = new Sprite(happyEndTexture);
		happyEndSprite.setSize(256, 256);
		happyEndSprite.setPosition(-128, -128);
		
		deadTexture = new Texture(Gdx.files.internal("data/frozen.png"));
		deadTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		deadSprite = new Sprite(deadTexture);
		deadSprite.setSize(256, 256);
		deadSprite.setPosition(-128, -128);
		
		restartGame();
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
		
		float tempFactor = bukaTemp/100.0f;
		tempSprite.setSize(tempFactor*6*40, tempSprite.getHeight());
		tempSprite.setPosition(-tempSprite.getWidth()/2.0f, 600-40-HSCREEN_H);
		tempSprite.setRegion(0, 0, (int)(512*tempFactor), 64);
		tempSprite.draw(batch);
		
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
		
		if(gameState == GameState.Frozen) deadSprite.draw(batch);
		if(gameState == GameState.BukaBurnt) bukaBurntSprite.draw(batch);
		if(gameState == GameState.HappyEnd) happyEndSprite.draw(batch);
		
		batch.end();
		
		if(gameState == GameState.Gameplay)
		{
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
		} else if(Gdx.input.isKeyPressed(Input.Keys.R))
		{
			gameState = GameState.Gameplay;
			restartGame();
			return;
		}
		
		for(int row = 0; row < MAP_HEIGHT; row++)
			for(int col = 0; col < MAP_WIDTH; col++)
				wallsMap[row][col].ice -= 0.1f*Gdx.graphics.getDeltaTime();
		
		{
			int row = buka.row;
			int col = buka.col;
			
			float fireSpeed = 20.0f;
			
			if(row > 0 && wallsMap[row-1][col].fire) bukaTemp -= Gdx.graphics.getDeltaTime()*fireSpeed;
			if(col > 0 && wallsMap[row][col-1].fire) bukaTemp -= Gdx.graphics.getDeltaTime()*fireSpeed;
			if(row + 1 < MAP_HEIGHT && wallsMap[row+1][col].fire) bukaTemp -= Gdx.graphics.getDeltaTime()*fireSpeed;
			if(col + 1 < MAP_WIDTH  && wallsMap[row][col+1].fire) bukaTemp -= Gdx.graphics.getDeltaTime()*fireSpeed;
			
			if(bukaTemp < 0)
			{
				if(gameState == GameState.Gameplay)
					gameState = GameState.HappyEnd;
				bukaTemp = 0;
			}
		}
		
		player.update(Gdx.graphics.getDeltaTime());
		if(gameState != GameState.BukaBurnt)
			buka.update(Gdx.graphics.getDeltaTime());
		
		if(gameState == GameState.Gameplay)
		{
			if(Math.abs(player.getX() - buka.getX()) < 40 &&
					Math.abs(player.getY() - buka.getY()) < 40)
				gameState = GameState.Frozen;
				
		}
	}

	public void restartGame()
	{
		bukaLifes = 3;
		fires = 0;
		bukaTemp = 100.0f;
		
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
			e.printStackTrace();
		}
	}
	
	public void burnBuka()
	{
		bukaLifes--;
		if(bukaLifes == 0)
		{
			gameState = GameState.BukaBurnt;
		} else
		{
			bukaTemp = 100.0f;
			fires = 0;
			for(int row = 0; row < MAP_HEIGHT; row++)
				for(int col = 0; col < MAP_WIDTH; col++)
				{
					wallsMap[row][col].ice = 1;
					wallsMap[row][col].fire = false;
				}
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
