package com.gdx.engine;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Austin on 10/18/2014.
 * create obj
 * add adj obj to
 */
public class DistanceTrackerMap {
    private TiledMapTileLayer layer;
    //private DistanceFromPlayer playerDistanceMap[][];
    private List<DistanceFromPlayer> distanceMap;
    private int width;
    private int height;
    private List<Integer> tilesAlreadyChecked;
    ArrayList<DistanceFromPlayer> lookingAt;
    ArrayList<DistanceFromPlayer> toBeLookedAt;

    public DistanceTrackerMap(TiledMapTileLayer layer, int playerPos) {
		this.layer = layer;
		width = layer.getWidth();
		height = layer.getHeight();
		distanceMap = new ArrayList<DistanceFromPlayer>(width * height);
		tilesAlreadyChecked = new ArrayList<Integer>(width * height);
		buildMap(playerPos);
    }

    public void addDistances(int playerPos) {
		int pos = playerPos;
		if (tilesAlreadyChecked.indexOf(playerPos) == -1) {//temp fix to erroring out if player goes to spot unreachable by enemy
		    if (tilesAlreadyChecked.indexOf(playerPos + 1) != -1)
		        pos = playerPos + 1;
		    else if (tilesAlreadyChecked.indexOf(playerPos - 1) != -1)
		        pos = playerPos - 1;
		    else if (tilesAlreadyChecked.indexOf(playerPos + width) != -1)
		        pos = playerPos + width;
		    else if (tilesAlreadyChecked.indexOf(playerPos - width) != -1)
	            pos = playerPos - width;
		    else if (tilesAlreadyChecked.indexOf(playerPos + width + 1) != -1)
		        pos = playerPos + width + 1;
		    else if (tilesAlreadyChecked.indexOf(playerPos + width - 1) != -1)
		        pos = playerPos + width - 1;
		    else if (tilesAlreadyChecked.indexOf(playerPos - width + 1) != -1)
		        pos = playerPos - width + 1;
		    else if (tilesAlreadyChecked.indexOf(playerPos - width - 1) != -1)
		        pos = playerPos - width - 1;
		}
		
        DistanceFromPlayer start = distanceMap.get(tilesAlreadyChecked.indexOf(pos));
        lookingAt = new ArrayList<DistanceFromPlayer>(1);
        toBeLookedAt = new ArrayList<DistanceFromPlayer>(1);
        DistanceFromPlayer toCheck;
        toBeLookedAt.add(start);
        int distFromPlayer = 0;
        do {
            distFromPlayer++;
            lookingAt = new ArrayList<DistanceFromPlayer>(1);
            lookingAt = new ArrayList<DistanceFromPlayer>(toBeLookedAt);
            toBeLookedAt = new ArrayList<DistanceFromPlayer>(1);
            for (DistanceFromPlayer distance : lookingAt) {
                for (int index : distance.getSpotToMoveIndex()) {
                    toCheck = distanceMap.get(index);
                    if (toCheck.getDistFromPlayer() == -1 && distanceMap.get(index).getSpotToMoveIndex().contains(tilesAlreadyChecked.indexOf(distance.getTileNumber()))){
                        distanceMap.get(index).setDistFromPlayer(distFromPlayer);
                        toBeLookedAt.add(toCheck);
                    }
                }
            }
        } while (toBeLookedAt.size() != 0);

        return;
    }

    public void resetDistances() {
        for (DistanceFromPlayer distance : distanceMap){
            distance.setDistFromPlayer(-1);
        }
    }

    public ArrayList<Integer> shortestPath (int start, int finish) {
        ArrayList<Integer> intPath = new ArrayList<Integer>(1);
        DistanceFromPlayer Pos = distanceMap.get(tilesAlreadyChecked.indexOf(start));
        DistanceFromPlayer leastDistance = new DistanceFromPlayer(-1);//initial distance placeholder
        DistanceFromPlayer toCheck;
        leastDistance.setDistFromPlayer(width * height + 1);
        while (Pos.getTileNumber() != finish) {
            for (int adjIndex : Pos.getSpotToMoveIndex()) {
                toCheck = distanceMap.get(adjIndex);
                if (toCheck.getTileNumber() == finish) {
                    leastDistance = toCheck;
                    break;
                }

                if (leastDistance.getDistFromPlayer() > toCheck.getDistFromPlayer()){
                    leastDistance = toCheck;
                }
                if (intPath.size() == 2)//temp fix to memory leak
                    return intPath;
            }
            Pos = leastDistance;
            intPath.add(leastDistance.getTileNumber());
        }
        
        return intPath;
    }

    private void buildMap(int playerPos) {
        DistanceFromPlayer start = new DistanceFromPlayer(playerPos);
        lookingAt = new ArrayList<DistanceFromPlayer>();
        toBeLookedAt = new ArrayList<DistanceFromPlayer>();
        toBeLookedAt.add(start);
        tilesAlreadyChecked.add(start.getTileNumber());
        DistanceFromPlayer adjSpot;
        ArrayList<Integer> adjs;
        do {
            lookingAt = new ArrayList<DistanceFromPlayer>(toBeLookedAt);
            toBeLookedAt = new ArrayList<DistanceFromPlayer>();
            for (DistanceFromPlayer mapObject : lookingAt) {
                adjs = new ArrayList<Integer>(FindAdjLocations(mapObject));
                for (int ajdPos : adjs){
                    adjSpot = new DistanceFromPlayer(ajdPos);
                    if (!tilesAlreadyChecked.contains(ajdPos)) {
                        tilesAlreadyChecked.add(ajdPos);
                        toBeLookedAt.add(adjSpot);
                    }
                    mapObject.addSpotToMoveIndex(tilesAlreadyChecked.indexOf(ajdPos));
                }
                distanceMap.add(tilesAlreadyChecked.indexOf(mapObject.getTileNumber()), mapObject);
            }
        }while (toBeLookedAt.size() != 0);

        return;
    }

    private boolean notAWall(int currentLoc,  int adjTileNum, int currentTileHeight, String rampDirection) {
        if (adjTileNum < 0 || adjTileNum >= height * width)
            return false;

        TiledMapTile adjTile = layer.getCell(getXPos(adjTileNum), getYPos(adjTileNum)).getTile();
        TiledMapTile currentTile = layer.getCell(getXPos(currentLoc), getYPos(currentLoc)).getTile();
        int adjHeight = Integer.parseInt(adjTile.getProperties().get("height").toString());
        int x = getXPos(adjTileNum);
        int y = getYPos(adjTileNum);
        boolean hasHeight = adjTile.getProperties().containsKey("height");

        if (layer.getCell(x, y) != null //contains a tile
                && hasHeight
                && (
                    (adjHeight == currentTileHeight + 1 && currentTile.getProperties().containsKey("ramp") && rampCorrectDirection(currentTile, rampDirection))
                        ||  (adjHeight <= currentTileHeight) /*&& !adjTile.getProperties().containsKey("ramp"))*/
                )
            )
            return true;
        else
            return false;
    }

    private boolean rampCorrectDirection(TiledMapTile adjTile, String rampDirection) {
        if (adjTile.getProperties().containsKey("ramp")) {
            if (adjTile.getProperties().get("ramp").toString().equals(rampDirection))
                return true;//right direction
            else
                return false;//wrong direction
        }
        else
            return true;//no ramp
    }

    private ArrayList<Integer> FindAdjLocations(DistanceFromPlayer mapObject) {
        TiledMapTile tile = layer.getCell(getXPos(mapObject.getTileNumber()), getYPos(mapObject.getTileNumber())).getTile();
        int currentHeight = Integer.parseInt(tile.getProperties().get("height").toString());
        int currentLoc = mapObject.getTileNumber();
        //calculate adj rooms
        ArrayList<Integer> spotNums = new ArrayList<Integer>(1);
        int bottom, top, left, right, botLeft, botRight, topLeft, topRight;
        String adjDiagonalRamp = "can't go up a ramp from a diagonal tile";//comparing to ramp direction will always return false

        //left tile
        left = currentLoc - width;
        if (notAWall(currentLoc, left, currentHeight, "left")) //not a wall or wrong ramp direction
            spotNums.add(left);
        //right tile
        right = currentLoc + width;
        if (notAWall(currentLoc, right, currentHeight, "right"))
            spotNums.add(right);
        //bottom tile
        bottom = currentLoc - 1;
        if (notAWall(currentLoc, bottom, currentHeight, "down") && getXPos(mapObject.getTileNumber()) == getXPos(bottom))
            spotNums.add(bottom);
        //top tile
        top = currentLoc + 1;
        if (notAWall(currentLoc, top, currentHeight, "up") && getXPos(mapObject.getTileNumber()) == getXPos(top))
            spotNums.add(top);

        //top left tile
        topLeft = currentLoc - width + 1;
        if ((spotNums.indexOf(top) != -1 && spotNums.indexOf(right) != -1)
                && notAWall(currentLoc, topLeft, currentHeight, adjDiagonalRamp)
                && getXPos(topLeft) == getXPos(top))
            spotNums.add(topLeft);

        //bottom left tile
        botLeft = currentLoc - width - 1;
        if ((spotNums.indexOf(bottom) != -1 && spotNums.indexOf(left) != -1)
                && notAWall(currentLoc, botLeft, currentHeight, adjDiagonalRamp)
                && getXPos(left) == getXPos(botLeft))
            spotNums.add(botLeft);

        //bottom right tile
        botRight = currentLoc + width - 1;
        if ((spotNums.indexOf(bottom) != -1 && spotNums.indexOf(right) != -1)
                && notAWall(currentLoc, botRight, currentHeight, adjDiagonalRamp)
                && getXPos(botRight) == getXPos(right))
            spotNums.add(botRight);

        //top right tile
        topRight = currentLoc + width + 1;
        if ((spotNums.indexOf(top) != -1 && spotNums.indexOf(right) != -1)
                && notAWall(currentLoc, topRight, currentHeight, adjDiagonalRamp)
                && getXPos(topRight) == getXPos(right))
            spotNums.add(topRight);

        return spotNums;
    }

    private int getXPos(int tileNumber) {
        return tileNumber /width;
    }

    private int getYPos(int tileNumber) {
        return tileNumber % width;
    }

}
class DistanceFromPlayer {
    List<Integer> spotsToMoveIndex = new ArrayList<Integer>();
    int distFromPlayer = -1;
    int tileNumber;

    public DistanceFromPlayer(int number) {
        this.tileNumber = number;
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

    public void addSpotToMoveIndex(int spot){
        spotsToMoveIndex.add(spot);
    }

    public List<Integer> getSpotToMoveIndex(){
        return spotsToMoveIndex;
    }
}