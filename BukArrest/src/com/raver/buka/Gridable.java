package com.raver.buka;

import com.raver.buka.BukArrestGame.MapField;


public class Gridable {
	MapField[][] map;
	int row;
	int col;
	int nextRow;
	int nextCol;
	float progress = 1.0f;
	float speed = 5.0f;
	
	Gridable(MapField[][] map, int row, int col)
	{
		this.map = map;
		
		this.col = this.nextCol = col;
		this.row = this.nextRow = row;
	}
	
	public void onStop()
	{
	}
	
	void update(float delta)
	{
		progress += delta*speed;
		
		if(progress >= 1.0f)
		{
			col = nextCol;
			row = nextRow;
			
			onStop();
		}
	}
	
	boolean canMoveUp()
	{
		return row + 1 < map.length && !map[row + 1][col].w; 
	}
	
	void moveUp()
	{
		if(isMoving() == false && canMoveUp())
		{
			progress = 0;
			nextRow = row + 1;
			nextCol = col;
		}
	}
	
	boolean canMoveDown()
	{
		return row > 0 && !map[row - 1][col].w; 
	}
	
	void moveDown()
	{
		if(isMoving() == false && canMoveDown())
		{
			progress = 0;
			nextRow = row - 1;
			nextCol = col;
		}
	}
	
	boolean canMoveRight()
	{
		return col + 1 < map[0].length && !map[row][col+1].w; 
	}
	
	void moveRight()
	{
		if(isMoving() == false && canMoveRight())
		{
			progress = 0;
			nextRow = row;
			nextCol = col + 1;
		}
	}
	
	boolean canMoveLeft()
	{
		return col > 0 && !map[row][col-1].w; 
	}
	
	void moveLeft()
	{
		if(isMoving() == false && canMoveLeft())
		{
			progress = 0;
			nextRow = row;
			nextCol = col - 1;
		}
	}
	
	boolean isMoving()
	{
		return progress < 1.0f;
	}
	
	float getX()
	{
		float k = progress;
		k = k > 1 ? 1 : k;
		return (col*(1-k) + nextCol*k)*40.0f;
	}
	
	float getY()
	{
		float k = progress;
		k = k > 1 ? 1 : k;
		return (row*(1-k) + nextRow*k)*40.0f;
	}
}
