package com.raver.buka;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class Buka extends Gridable {
	Sprite sprite;
	
	boolean[][] visited = new boolean[BukArrestGame.MAP_HEIGHT][BukArrestGame.MAP_WIDTH];
	int[][] distance = new int[BukArrestGame.MAP_HEIGHT][BukArrestGame.MAP_WIDTH];
	
	class MapField
	{
		int row;
		int col;
		int dist;
	}
	MapField[] fields;
	
	Buka(boolean[][] map, int row, int col)
	{
		super(map, row, col);
		
		Texture playerTex = new Texture(Gdx.files.internal("data/buka.png"));
		playerTex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		sprite = new Sprite(playerTex);
		sprite.setSize(40, 40);
		
		fields = new MapField[BukArrestGame.MAP_WIDTH*BukArrestGame.MAP_HEIGHT];
		for(int i=0; i<fields.length; i++)
			fields[i] = new MapField();
	}

	public void draw(SpriteBatch batch) {
		sprite.setPosition(getX() - BukArrestGame.HSCREEN_W, getY() - BukArrestGame.HSCREEN_H);
		sprite.draw(batch);
	}
	
	public void onStop()
	{		
		for(int i=0; i<visited.length; i++)
			for(int j=0; j<visited[i].length; j++)
				visited[i][j] = false;
	
		int fieldsCount = 1;
		fields[0].row = BukArrestGame.self.player.row;
		fields[0].col = BukArrestGame.self.player.col;
		fields[0].dist = 0;
		
		visited[row][col] = true;
		
		for(int i=0; i<fieldsCount; i++)
		{
			MapField f = fields[i];
			
			if(f.row > 0 && visited[f.row-1][f.col] == false && map[f.row-1][f.col] == false) 
			{ 
				fields[fieldsCount].row = f.row-1; fields[fieldsCount].col = f.col;
				fields[fieldsCount].dist = f.dist + 1;
				visited[f.row-1][f.col] = true;
				fieldsCount++;
			}
			if(f.col > 0 && visited[f.row][f.col-1] == false && map[f.row][f.col-1] == false) 
			{ 
				fields[fieldsCount].row = f.row; fields[fieldsCount].col = f.col-1;
				fields[fieldsCount].dist = f.dist + 1;
				visited[f.row][f.col-1] = true;
				fieldsCount++;
			}
			if(f.row + 1 < BukArrestGame.MAP_HEIGHT && visited[f.row+1][f.col] == false && map[f.row+1][f.col] == false) 
			{ 
				fields[fieldsCount].row = f.row + 1; fields[fieldsCount].col = f.col;
				fields[fieldsCount].dist = f.dist + 1;
				visited[f.row+1][f.col] = true;
				fieldsCount++;
			}
			if(f.col + 1 < BukArrestGame.MAP_WIDTH && visited[f.row][f.col+1] == false && map[f.row][f.col+1] == false) 
			{ 
				fields[fieldsCount].row = f.row; fields[fieldsCount].col = f.col+1;
				fields[fieldsCount].dist = f.dist + 1;
				visited[f.row][f.col+1] = true;
				fieldsCount++;
			}
		}
		
		final int inf = 9999999;
		
		for(int i=0; i<visited.length; i++)
			for(int j=0; j<visited[i].length; j++)
				distance[i][j] = inf;
		
		for(int i=0; i<fieldsCount; i++)
		{
			MapField f = fields[i];
			distance[f.row][f.col] = f.dist;
		}
		
		int minDist = inf;
		
		if(row > 0) minDist = Math.min(minDist, distance[row-1][col]);
		if(col > 0) minDist = Math.min(minDist, distance[row][col-1]);
		if(row + 1 < BukArrestGame.MAP_HEIGHT) minDist = Math.min(minDist, distance[row+1][col]);
		if(col + 1 < BukArrestGame.MAP_WIDTH) minDist = Math.min(minDist, distance[row][col+1]);
		
		int[] tab = {0, 0, 0, 0};
		int tabSize = 0;
		
		if(row > 0 && distance[row-1][col] == minDist) tab[tabSize++] = 0;
		if(col > 0 && distance[row][col-1] == minDist) tab[tabSize++] = 1;
		if(row + 1 < BukArrestGame.MAP_HEIGHT && distance[row+1][col] == minDist) tab[tabSize++] = 2;
		if(col + 1 < BukArrestGame.MAP_WIDTH && distance[row][col+1] == minDist) tab[tabSize++] = 3;
		
		int dir = tab[com.badlogic.gdx.math.MathUtils.random(tabSize-1)];
		
		if(dir == 0) moveDown();
		if(dir == 1) moveLeft();
		if(dir == 2) moveUp();
		if(dir == 3) moveRight();
	}
}
