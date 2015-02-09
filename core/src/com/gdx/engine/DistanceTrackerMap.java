package com.gdx.engine;

import com.badlogic.gdx.math.GridPoint2;
import com.gdx.DynamicEntities.Enemy;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Austin on 10/18/2014.
 * create obj
 * add adj obj to
 */
public class DistanceTrackerMap {
    //private TiledMapTileLayer layer;
    private MeshLevel meshLevel;
    //private DistanceFromPlayer playerDistanceMap[][];
    private List<DistanceFromPlayer> distanceMap;
    private int width;
    private int height;
    private List<Integer> tilesAlreadyChecked;
    ArrayList<DistanceFromPlayer> lookingAt;
    ArrayList<DistanceFromPlayer> toBeLookedAt;

    //  public DistanceTrackerMap(TiledMapTileLayer layer, int playerPos) {
    //		this.layer = layer;
    //		width = layer.getWidth();
    //		height = layer.getHeight();
    //		distanceMap = new ArrayList<DistanceFromPlayer>(width * height);
    //		tilesAlreadyChecked = new ArrayList<Integer>(width * height);
    //		buildMap(playerPos);
    //  }

    public DistanceTrackerMap(MeshLevel meshLevel, int playerPos) {
    	this.meshLevel = meshLevel;
    	width = meshLevel.getMapXDimension();
    	height = meshLevel.getMapYDimension();
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
        DistanceFromPlayer start;
		try {
            start = distanceMap.get(tilesAlreadyChecked.indexOf(pos));
        }catch (Exception e){
            return;
        }
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
        DistanceFromPlayer leastDistance = new DistanceFromPlayer(-1, -1, width);//initial distance placeholder
        DistanceFromPlayer toCheck;
        leastDistance.setDistFromPlayer(width * height + 1);
        while (Pos.getTileNumber() != finish) {
            for (int adjIndex : Pos.getSpotToMoveIndex()) {
                toCheck = distanceMap.get(adjIndex);
                if (toCheck.getTileNumber() == finish) {
                    leastDistance = toCheck;
                    break;
                }

                if (leastDistance.getDistFromPlayer() > toCheck.getDistFromPlayer() && toCheck.getDistFromPlayer() != -1){
                    leastDistance = toCheck;
                }
                if (intPath.size() == 5)//2)//temp fix to memory leak
                    return intPath;
            }
            Pos = leastDistance;
            intPath.add(leastDistance.getTileNumber());
            if (intPath.size() > width * width)
                break;
        }
        boolean test = true;
        if (intPath.contains(1192))
            test = false;
        return intPath;
    }

    private void buildMap(int playerPos) {
        DistanceFromPlayer start = new DistanceFromPlayer(playerPos, 0, width);
        lookingAt = new ArrayList<DistanceFromPlayer>();
        toBeLookedAt = new ArrayList<DistanceFromPlayer>();
        toBeLookedAt.add(start);
        tilesAlreadyChecked.add(start.getTileNumber());
        DistanceFromPlayer adjSpot;
        ArrayList<DistanceFromPlayer> adjs;
        do {
            lookingAt = new ArrayList<DistanceFromPlayer>(toBeLookedAt);
            toBeLookedAt = new ArrayList<DistanceFromPlayer>();
            for (DistanceFromPlayer mapObject : lookingAt) {
                if (mapObject.getTileNumber() == 1224)
                    mapObject.setTileNumber(mapObject.getTileNumber());
                adjs = new ArrayList<DistanceFromPlayer>(FindAdjLocations(mapObject));
                for (DistanceFromPlayer ajdPos : adjs){
                    if (!tilesAlreadyChecked.contains(ajdPos.getTileNumber())) {
                        tilesAlreadyChecked.add(ajdPos.getTileNumber());
                        toBeLookedAt.add(ajdPos);
                    }
                    mapObject.addSpotToMoveIndex(tilesAlreadyChecked.indexOf(ajdPos.getTileNumber()), ajdPos.getTileNumber());
                }
                int tileIndex = tilesAlreadyChecked.indexOf(mapObject.getTileNumber());
                distanceMap.add(tileIndex, mapObject);
            }
        } while (toBeLookedAt.size() != 0);

        return;
    }

    private boolean rampIsWall(DistanceFromPlayer currentLoc, int adjTileNum, int rampDirection, int adjTileLayer){
        /*MapTile adjTile = meshLevel.getMapTile(getXPos(adjTileNum), getYPos(adjTileNum), adjTileLayer);
        MapTile currentTile = meshLevel.getMapTile(getXPos(currentLoc.getTileNumber()), getYPos(currentLoc.getTileNumber()), currentLoc.getLayer());

        if (adjTile.getRampDirection() != -1)
            if (adjTile.getRampDirection() != rampDirection)
                return true;
        return false;*/
        return false;
    }

    private boolean notAWall(DistanceFromPlayer currentLoc,  int adjTileNum, int currentTileHeight, int rampDirection, int adjTileLayer) {
    	if (adjTileNum < 0 || adjTileNum >= height * width)
    		return false;
    	MapTile adjTile = meshLevel.getMapTile(getXPos(adjTileNum), getYPos(adjTileNum), adjTileLayer);
    	MapTile currentTile = meshLevel.getMapTile(getXPos(currentLoc.getTileNumber()), getYPos(currentLoc.getTileNumber()), currentLoc.getLayer());

    	int adjHeight = adjTile.getHeight();
    	int x = getXPos(adjTileNum);
    	int y = getYPos(adjTileNum);
    	boolean hasHeight = adjHeight > -1;

        if (adjTileLayer == 1)
            adjHeight = adjHeight + 6;
        if (currentLoc.getLayer() == 1)
            currentTileHeight = currentTileHeight + 6;
        if (adjTileLayer == 1 && currentLoc.getLayer() == 0){
            if (adjHeight > currentTileHeight + 1)//b/c layer.get(1) is object layer
                return false;
        }

    	//if (layer.getCell(x, y) != null //contains a tile
    	if (!meshLevel.outOfBounds(new GridPoint2(x, y)) //contains a tile
    			&& hasHeight
    			&& (
    					//(adjHeight == currentTileHeight + 1 && currentTile.getProperties().containsKey("ramp") && rampCorrectDirection(currentTile, rampDirection))
    					(adjHeight == currentTileHeight + 1 && currentTile.getRampDirection() != -1 && rampCorrectDirection(currentTile, rampDirection))
    					||  (adjHeight <= currentTileHeight) /*&& !adjTile.getProperties().containsKey("ramp"))*/
    					)
    			)
    		return true;
    	else
    		return false;
    }

    private boolean rampCorrectDirection(MapTile adjTile, int rampDirection) {
    	if (adjTile.getRampDirection() != -1) {
    		if (adjTile.getRampDirection() == rampDirection)
    			return true;//right direction
    		else
    			return false;//wrong direction
    	}
    	else
    		return true;//no ramp
    }

    private ArrayList<DistanceFromPlayer> FindAdjLocations(DistanceFromPlayer mapObject) {
        //int mapObjectNum = mapObject.getTileNumber();
        int currentLoc = mapObject.getTileNumber();
        int adjTileLayer = 0;
        if (mapObject.getLayer() == 1){
            mapObject.setTileNumber(mapObject.getTileNumber() - width * height);//temp change to layer 2 tile pos
            adjTileLayer = 1;
        }
        int tileLocation = mapObject.getTileNumber();

        MapTile tile = meshLevel.getMapTile(getXPos(mapObject.getTileNumber()), getYPos(mapObject.getTileNumber()), 0);
        int currentHeight = tile.getHeight();

        if (currentHeight == 5) {
            adjTileLayer = 1;
        }
        //calculate adj rooms
        ArrayList<DistanceFromPlayer> spotNums = new ArrayList<DistanceFromPlayer>(1);
        int bottom, top, left, right, botLeft, botRight, topLeft, topRight;
        int adjDiagonalRamp = -1;//comparing to ramp direction will always return falsew

        //left tile
        left = tileLocation - width;
        for (int tileLayer = adjTileLayer; tileLayer > -1; tileLayer--)
            if (notAWall(mapObject, left, currentHeight, MeshLevel.LEFT, tileLayer)
                    && !rampIsWall(mapObject, left, MeshLevel.LEFT, tileLayer)) { //not a wall or wrong ramp direction
                if (tileLayer == 1)
                    left = left + width * height;
                spotNums.add(new DistanceFromPlayer(left, tileLayer, width));
                break;
            }
        //right tile
        right = tileLocation + width;
        for (int tileLayer = adjTileLayer; tileLayer > -1; tileLayer--)
            if (notAWall(mapObject, right, currentHeight, MeshLevel.RIGHT, tileLayer)
                    && !rampIsWall(mapObject, right, MeshLevel.RIGHT, tileLayer)){
                if (tileLayer == 1)
                    right = right + width * height;
                spotNums.add(new DistanceFromPlayer(right, tileLayer, width));
                break;
            }
        //bottom tile
        bottom = tileLocation - 1;
        for (int tileLayer = adjTileLayer; tileLayer > -1; tileLayer--)
            if (notAWall(mapObject, bottom, currentHeight, MeshLevel.DOWN, tileLayer) && getXPos(mapObject.getTileNumber()) == getXPos(bottom)
                    && !rampIsWall(mapObject, bottom, MeshLevel.DOWN, tileLayer)) {
                if (tileLayer == 1)
                    bottom = bottom + width * height;
                spotNums.add(new DistanceFromPlayer(bottom, tileLayer, width));
                break;
            }

        //top tile
        top = tileLocation + 1;
        for (int tileLayer = adjTileLayer; tileLayer > -1; tileLayer--)
            if (notAWall(mapObject, top, currentHeight, MeshLevel.UP, tileLayer) && getXPos(mapObject.getTileNumber()) == getXPos(top)
                    && !rampIsWall(mapObject, top, MeshLevel.UP, tileLayer)){
                if (tileLayer == 1)
                    top = top + width * height;
                spotNums.add(new DistanceFromPlayer(top, tileLayer, width));
                break;
            }


        //top left tile
        topLeft = tileLocation - width + 1;
        for (int tileLayer = adjTileLayer; tileLayer > -1; tileLayer--)
            if ((isMoveable(spotNums, top) && isMoveable(spotNums, right))
                    && notAWall(mapObject, topLeft, currentHeight, adjDiagonalRamp, tileLayer)
                    && getXPos(topLeft) == getXPos(top)
                    && meshLevel.getMapTile(getXPos(topLeft), getYPos(topLeft), adjTileLayer).getRampDirection() == -1) {
                if (tileLayer == 1)
                    topLeft = topLeft + width * height;
                spotNums.add(new DistanceFromPlayer(topLeft, tileLayer, width));
                break;
            }


        //bottom left tile
        botLeft = tileLocation - width - 1;
        for (int tileLayer = adjTileLayer; tileLayer > -1; tileLayer--)
            if ((isMoveable(spotNums, bottom) && isMoveable(spotNums, left))
                    && notAWall(mapObject, botLeft, currentHeight, adjDiagonalRamp, tileLayer)
                    && getXPos(left) == getXPos(botLeft)
                    && meshLevel.getMapTile(getXPos(botLeft), getYPos(botLeft), adjTileLayer).getRampDirection() == -1) {
                if (tileLayer == 1)
                    botLeft = botLeft + width * height;
                spotNums.add(new DistanceFromPlayer(botLeft, tileLayer, width));
                break;
            }


        //bottom right tile
        botRight = tileLocation + width - 1;
        for (int tileLayer = adjTileLayer; tileLayer > -1; tileLayer--)
            if ((isMoveable(spotNums,bottom) && isMoveable(spotNums, right))
                    && notAWall(mapObject, botRight, currentHeight, adjDiagonalRamp, tileLayer)
                    && getXPos(botRight) == getXPos(right)
                    && meshLevel.getMapTile(getXPos(botRight), getYPos(botRight), adjTileLayer).getRampDirection() == -1) {
                if (tileLayer == 2)
                    botRight = botRight + width * height;
                spotNums.add(new DistanceFromPlayer(botRight, tileLayer, width));
                break;
            }


        //top right tile
        topRight = tileLocation + width + 1;
        for (int tileLayer = adjTileLayer; tileLayer > -1; tileLayer--)
            if ((isMoveable(spotNums,top) && isMoveable(spotNums,right))
                    && notAWall(mapObject, topRight, currentHeight, adjDiagonalRamp, tileLayer)
                    && getXPos(topRight) == getXPos(right)
                    && meshLevel.getMapTile(getXPos(topRight), getYPos(topRight), adjTileLayer).getRampDirection() == -1){
                if (tileLayer == 2)
                    topRight = topRight + width * height;
                spotNums.add(new DistanceFromPlayer(topRight, tileLayer, width));
                break;
            }

        if (mapObject.getLayer() == 1)
            mapObject.setTileNumber(mapObject.getTileNumber() + width * height);

        return spotNums;
    }

    private boolean isMoveable( ArrayList<DistanceFromPlayer> spotNums, int locationNum ) {
        for (DistanceFromPlayer spotNum : spotNums) {
            if (spotNum.getTileNumber() == locationNum)
                return true;
        }
        return false;
    }

    public int getNewPath(int start, int finish, Enemy enemy) {
        DistanceFromPlayer enemyPos = distanceMap.get(tilesAlreadyChecked.indexOf(start));
        if (enemy.getRotation().x == 180) {//top
            if (enemyPos.getLeft() != -1)
                return enemyPos.getLeft();
            else if (enemyPos.getRight() != -1)
                return enemyPos.getRight();
            else if (enemyPos.getBottom() != -1)
                return enemyPos.getBottom();
            else
                return start;
        }
        else if (enemy.getRotation().x == 270) {//bottom
            if (enemyPos.getRight() != -1)
                return enemyPos.getRight();
            else if (enemyPos.getLeft() != -1)
                return enemyPos.getLeft();
            else if (enemyPos.getTop() != -1)
                return enemyPos.getTop();
            else
                return start;
        }
        else if (enemy.getRotation().x == 0) {//right
            if (enemyPos.getTop() != -1)
                return enemyPos.getTop();
            else if (enemyPos.getBottom() != -1)
                return enemyPos.getBottom();
            else if (enemyPos.getLeft() != -1)
                return enemyPos.getLeft();
            else
                return start;
        }
        else if (enemy.getRotation().x == 90) {//left
            if (enemyPos.getBottom() != -1)
                return enemyPos.getBottom();
            else if (enemyPos.getTop() != -1)
                return enemyPos.getTop();
            else if (enemyPos.getRight() != -1)
                return enemyPos.getRight();
            else
                return start;
        }
        else if (enemy.getRotation().x > 0 && enemy.getRotation().x < 90) {//top right
            if (enemyPos.getBottom() != -1)//bottom
                return enemyPos.getBottom();
            else if (enemyPos.getLeft() != -1)//left
                return enemyPos.getLeft();
            else return start;
        }
        else if (enemy.getRotation().x > 90 && enemy.getRotation().x < 180) {//top left
            if (enemyPos.getRight() != -1)
                return enemyPos.getRight();
            if (enemyPos.getBottom() != -1)//bottom
                return enemyPos.getBottom();
            else return start;
        }
        else if (enemy.getRotation().x > 180 && enemy.getRotation().x < 270) {//bot left
            if (enemyPos.getRight() != -1)
                return enemyPos.getRight();
            if (enemyPos.getTop() != -1)//bottom
                return enemyPos.getTop();
            else return start;
        }
        else { // enemy.getRotation().x > 270 && enemy.getRotation().x < 360 // bot right
            if (enemyPos.getLeft() != -1)
                return enemyPos.getLeft();
            if (enemyPos.getTop() != -1)//bottom
                return enemyPos.getTop();
            else return start;
        }
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

    public void addSpotToMoveIndex(int spot, int adjTileNum){
        spotsToMoveIndex.add(spot);
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