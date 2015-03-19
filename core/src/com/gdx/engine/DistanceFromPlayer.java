package com.gdx.engine;

import java.util.ArrayList;
import java.util.List;

public class DistanceFromPlayer {
    List<Integer> spotsToMoveIndex = new ArrayList<Integer>();
    int distFromPlayer = -1;
    int tileNumber;
    int layer;
    int right = -1;
    int left = -1;
    int top = -1;
    int bottom = -1;
    int mapWidth;
    int topRight = -1;
    int topLeft = -1;
    int botRight = -1;
    int botLeft = -1;

    public DistanceFromPlayer(int number, int layer, int width) {
        this.layer = layer;
        this.tileNumber = number;
        this.mapWidth = width;
        this.distFromPlayer = width * width + 2;
    }

    public int getTileNumber() {
        return tileNumber;
    }

    public void setTileNumber(int number) {
        this.tileNumber = number;
    }

    public int getDistFromPlayer() {
        return distFromPlayer;
    }

    public void setDistFromPlayer(int distFromPlayer) {
        this.distFromPlayer = distFromPlayer;
    }

    public void addSpotToMoveIndex(int adjTileNum){
        spotsToMoveIndex.add(adjTileNum);
        if (adjTileNum + 1 == tileNumber)
            top = adjTileNum;
        else if (adjTileNum - 1 == tileNumber)
            bottom = adjTileNum;
        else if (adjTileNum + mapWidth == tileNumber)
            right = adjTileNum;
        else if (adjTileNum - mapWidth == tileNumber)
            left = adjTileNum;
        else if (adjTileNum + 1 + mapWidth == tileNumber)
            topRight = adjTileNum;
        else if (adjTileNum - 1 + mapWidth == tileNumber)
            botRight = adjTileNum;
        else if (adjTileNum + 1 - mapWidth == tileNumber)
            topLeft = adjTileNum;
        else if (adjTileNum - 1 - mapWidth == tileNumber)
            botLeft = adjTileNum;
    }

    public List<Integer> getSpotToMoveIndex(){
        return spotsToMoveIndex;
    }

    public void setLayer (int layer) { this.layer = layer; }

    public int getLayer () { return layer; }

    public int getTop(){
        return top;
    }

    public int getBottom(){
        return bottom;
    }

    public int getRight(){
        return right;
    }

    public int getLeft(){
        return left;
    }

    public int getTopRight(){
        return topRight;
    }

    public int getBotRight(){
        return botRight;
    }

    public int getTopLeft(){
        return topLeft;
    }

    public int getBotLeft(){
        return botLeft;
    }
}
