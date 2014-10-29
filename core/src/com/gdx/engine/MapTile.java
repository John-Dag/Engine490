package com.gdx.engine;

public class MapTile {
	private static int NULL_VALUE = -1;
	private int rampDirection;
	private int height;
	
	public MapTile(){
		rampDirection = NULL_VALUE;
		height = NULL_VALUE;
	}
	
	public MapTile(int rampDirection, int height){
		this.rampDirection = rampDirection;
		this.height = height;
	}
	
	public void printTileInfo(){
		System.out.print("["+rampDirection+","+height+"]");
	}

	public int getRampDirection() {
		return rampDirection;
	}

	public void setRampDirection(int rampDirection) {
		this.rampDirection = rampDirection;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
}