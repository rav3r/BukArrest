package com.raver.buka;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.raver.buka.BukArrestGame.MapField;

public class Buka extends Gridable {
	Sprite sprite;
	
	int[][] distance = new int[BukArrestGame.MAP_HEIGHT][BukArrestGame.MAP_WIDTH];
	
	class MapDist
	{
		int row;
		int col;
		int dist;
		boolean visited;
	}
	MapDist[] fields;
	
	Buka(MapField[][] map, int row, int col)
	{
		super(map, row, col);
		
		speed = 2.0f;
		
		Texture playerTex = new Texture(Gdx.files.internal("data/buka.png"));
		playerTex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		sprite = new Sprite(playerTex);
		sprite.setSize(40, 40);
		
		fields = new MapDist[BukArrestGame.MAP_WIDTH*BukArrestGame.MAP_HEIGHT];
		for(int i=0; i<fields.length; i++)
			fields[i] = new MapDist();
	}

	public void draw(SpriteBatch batch) {
		sprite.setPosition(getX() - BukArrestGame.HSCREEN_W, getY() - BukArrestGame.HSCREEN_H + 10);
		sprite.draw(batch);
	}
	
	public void onStop()
	{			
		if(map[row][col].ice < 0)
			map[row][col].ice = 1;
		else if(map[row][col].ice < 0.1f)
			map[row][col].ice = 1.0f;
		else
			map[row][col].ice = 0.9f;
		if(map[row][col].fire)
			BukArrestGame.self.burnBuka();
		
		for(int i=0; i<fields.length; i++)
			fields[i].visited = false;
		
		int fieldsCount = 1;
		fields[0].row = BukArrestGame.self.player.row;
		fields[0].col = BukArrestGame.self.player.col;
		fields[0].dist = 0;
		
		final int fireDist = BukArrestGame.MAP_HEIGHT*BukArrestGame.MAP_WIDTH;
		
		final int inf = 9999999;
		
		for(int i=0; i<distance.length; i++)
			for(int j=0; j<distance[i].length; j++)
			{
				distance[i][j] = inf;
			}
		
		distance[fields[0].row][fields[0].col] = 0;
		
		while(fieldsCount > 0)
		{
			MapDist f;
			int minDist = inf+inf;
			int id = 0;
			
			for(int i=0; i<fieldsCount; i++)
			{
				if(fields[i].visited == false && fields[i].dist < minDist)
				{
					id = i;
					minDist = fields[i].dist;
				}
			}
			
			if(minDist == inf+inf)
				break;
			
			f = fields[id];
			f.visited = true;
			
			if(f.row > 0 && distance[f.row-1][f.col] == inf && distance[f.row-1][f.col] > f.dist+1 && map[f.row-1][f.col].w == false) 
			{ 
				fields[fieldsCount].row = f.row-1; fields[fieldsCount].col = f.col;
				fields[fieldsCount].dist = f.dist + 1;
				MapDist c = fields[fieldsCount];
				if(map[c.row][c.col].fire) c.dist += fireDist;
				distance[c.row][c.col] = c.dist;
				fieldsCount++;
			}
			if(f.col > 0 && distance[f.row][f.col-1] == inf && distance[f.row][f.col-1] > f.dist+1 && map[f.row][f.col-1].w == false) 
			{ 
				fields[fieldsCount].row = f.row; fields[fieldsCount].col = f.col-1;
				fields[fieldsCount].dist = f.dist + 1;
				MapDist c = fields[fieldsCount];
				if(map[c.row][c.col].fire) c.dist += fireDist;
				distance[c.row][c.col] = c.dist;
				fieldsCount++;
			}
			if(f.row + 1 < BukArrestGame.MAP_HEIGHT && distance[f.row+1][f.col] == inf && distance[f.row+1][f.col] > f.dist+1 && map[f.row+1][f.col].w == false) 
			{ 
				fields[fieldsCount].row = f.row + 1; fields[fieldsCount].col = f.col;
				fields[fieldsCount].dist = f.dist + 1;
				MapDist c = fields[fieldsCount];
				if(map[c.row][c.col].fire) c.dist += fireDist;
				distance[c.row][c.col] = c.dist;
				fieldsCount++;
			}
			if(f.col + 1 < BukArrestGame.MAP_WIDTH && distance[f.row][f.col+1] == inf && distance[f.row][f.col+1] > f.dist+1 && map[f.row][f.col+1].w == false) 
			{ 
				fields[fieldsCount].row = f.row; fields[fieldsCount].col = f.col+1;
				fields[fieldsCount].dist = f.dist + 1;
				MapDist c = fields[fieldsCount];
				if(map[c.row][c.col].fire) c.dist += fireDist;
				distance[c.row][c.col] = c.dist;
				fieldsCount++;
			}
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
