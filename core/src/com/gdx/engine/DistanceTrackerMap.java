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
        DistanceFromPlayer start = distanceMap.get(tilesAlreadyChecked.indexOf(playerPos));
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
                    if (toCheck.getDistFromPlayer() == -1){
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
        do {
            lookingAt = new ArrayList<DistanceFromPlayer>(toBeLookedAt);
            toBeLookedAt = new ArrayList<DistanceFromPlayer>();
            for (DistanceFromPlayer mapObject : lookingAt) {
                for (int ajdPos : FindAdjLocations(mapObject)){
                    adjSpot = new DistanceFromPlayer(ajdPos);
                    if (!tilesAlreadyChecked.contains(ajdPos)) {
                        tilesAlreadyChecked.add(ajdPos);
                        toBeLookedAt.add(adjSpot);
                    }
                    mapObject.addSpotToMoveIndex(tilesAlreadyChecked.indexOf(ajdPos));
                }
                distanceMap.add(tilesAlreadyChecked.indexOf(mapObject.getTileNumber()), mapObject);
            }
        } while (toBeLookedAt.size() != 0);

        return;
    }

    private boolean notAWall(int currentLoc,  int adjTileNum, int currentTileHeight, String rampDirection) {
        TiledMapTile adjTile = layer.getCell(getXPos(adjTileNum), getYPos(adjTileNum)).getTile();
        TiledMapTile currentTile = layer.getCell(getXPos(currentLoc), getYPos(currentLoc)).getTile();
        int adjHeight = Integer.parseInt(adjTile.getProperties().get("height").toString());
        int x = getXPos(adjTileNum);
        int y = getYPos(adjTileNum);

        if (layer.getCell(x, y) != null //contains a tile
                && layer.getCell(x, y).getTile().getProperties().containsKey("height")
                && (((adjHeight == currentTileHeight + 1 && currentTile.getProperties().containsKey("ramp")) ||  adjHeight <= currentTileHeight))
                && rampCorrectDirection(adjTile, rampDirection))
            //(( currentTile.getProperties().containsKey("ramp") &&  (Integer.parseInt(height) + curretHeight == currentHeight + 1 ||  curretHeight - Integer.parseInt(height) = currentHeight - 1)))
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

        //bottom tile
        bottom = currentLoc - width;
        if (0 < bottom
                && notAWall(currentLoc, bottom, currentHeight, "down")) //not a wall
            spotNums.add(bottom);
        //top tile
        top = currentLoc + width;
        if (width * height > top
                && notAWall(currentLoc, top, currentHeight, "up"))
            spotNums.add(top);
        //right tile
        right = currentLoc - 1;
        if (getXPos(currentLoc) == getXPos(right) && right >= 0
                && notAWall(currentLoc, right, currentHeight, "right"))
            spotNums.add(right);
        //left tile
        left = currentLoc + 1;
        if (((getXPos(currentLoc) == getXPos(left) || (currentLoc == 0 && width > 1 && height > 1)))
                && width * height >= left
                && notAWall(currentLoc, left, currentHeight, "left"))
            spotNums.add(left);

        //bottom left tile
        botLeft = currentLoc - width + 1;
        if (spotNums.indexOf(bottom) != -1 && spotNums.indexOf(left) != -1
                && notAWall(currentLoc, botLeft, currentHeight, adjDiagonalRamp))
            spotNums.add(botLeft);

        //bottom right tile
        botRight = currentLoc - width - 1;
        if (spotNums.indexOf(bottom) != -1 && spotNums.indexOf(right) != -1
                && notAWall(currentLoc, botRight, currentHeight, adjDiagonalRamp))
            spotNums.add(botRight);

        //top right tile
        topRight = currentLoc + width - 1;
        if (spotNums.indexOf(top) != -1 && spotNums.indexOf(right) != -1
                && notAWall(currentLoc, topRight, currentHeight, adjDiagonalRamp))
            spotNums.add(topRight);

        //top left tile
        topLeft = currentLoc + width + 1;
        if (spotNums.indexOf(top) != -1 && spotNums.indexOf(left) != -1
                && notAWall(currentLoc, topLeft, currentHeight, adjDiagonalRamp))
            spotNums.add(topLeft);

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