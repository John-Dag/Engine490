package com.gdx.engine;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.gdx.DynamicEntities.Enemy;

import javax.management.BadAttributeValueExpException;
import java.util.*;

/**
 * Created by Austin on 10/18/2014.
 * create obj
 * add adj obj to
 */
public class DistanceTrackerMap {
    private MeshLevel meshLevel;
    private DistanceFromPlayer[][] distanceMap;
    private int width;
    private int height;
    private List<Integer> tilesAlreadyChecked;
    private ArrayList<DistanceFromPlayer> lookingAt;
    private ArrayList<DistanceFromPlayer> toBeLookedAt;
    private int defaultTileNumber;
    private int maxLayerHeight = 6;

    public DistanceTrackerMap(MeshLevel meshLevel, int playerPos) {
    	this.meshLevel = meshLevel;
    	width = meshLevel.getMapXDimension();
    	height = meshLevel.getMapYDimension();
        int totalLayers = 2;
        distanceMap = new DistanceFromPlayer[width * height][totalLayers];
        defaultTileNumber = width * height * (totalLayers + 1);
    	tilesAlreadyChecked = new ArrayList<Integer>(width * height);
        //set default values in distance map
        for (int fill = 0; fill < width * height - 1; fill++)
            for (int fillTileLayers = 0; fillTileLayers < totalLayers; fillTileLayers++)
                distanceMap[fill][fillTileLayers] = new DistanceFromPlayer(defaultTileNumber, defaultTileNumber, defaultTileNumber);

    	buildMap(playerPos);
    }

    private int unreachableSpotResolution(int pos, int startLayerHeight) {
        if (distanceMap[pos][startLayerHeight].getTileNumber() == -1) {//temp fix to erroring out if player goes to spot unreachable by enemy
            if (distanceMap[pos][startLayerHeight].getTileNumber() == -1)
                return pos + 1;
            else if (distanceMap[pos][startLayerHeight].getTileNumber() == -1)
                return pos - 1;
            else if (distanceMap[pos][startLayerHeight].getTileNumber() == -1)
                return pos + width;
            else if (distanceMap[pos][startLayerHeight].getTileNumber() == -1)
                return pos - width;
            else if (distanceMap[pos][startLayerHeight].getTileNumber() == -1)
                return pos + width + 1;
            else if (distanceMap[pos][startLayerHeight].getTileNumber() == -1)
                return pos + width - 1;
            else if (distanceMap[pos][startLayerHeight].getTileNumber() == -1)
                return pos - width + 1;
            else if (distanceMap[pos][startLayerHeight].getTileNumber() == -1)
                return pos - width - 1;
            else
                return pos;
        }
        return pos;
    }

    public void addDistances(int playerPos) {
        //if (playerPos == )
		int pos = playerPos;
        int startLayerHeight = pos / (width * height);
        DistanceFromPlayer start;
        if (startLayerHeight > 0)
            pos = pos - (startLayerHeight * width * height);
        if (playerPos > width * width)//debug purposes
            playerPos = playerPos + 1 - 1;
        pos = unreachableSpotResolution(pos, startLayerHeight);

        start = distanceMap[pos][startLayerHeight];
        for (int checkLayer = startLayerHeight; checkLayer > -1; checkLayer--){
            start = distanceMap[pos][checkLayer];
            if (start.getTileNumber() != defaultTileNumber)
                break;
        }
        //try {
            //start = distanceMap[pos][startLayerHeight];
        //} catch (Exception e) {
        //    return;
        //}
        lookingAt = new ArrayList<DistanceFromPlayer>(1);
        toBeLookedAt = new ArrayList<DistanceFromPlayer>(1);
        DistanceFromPlayer toCheck;
        toBeLookedAt.add(start);
        int distFromPlayer = 0;
        int layerHeight;
        do {
            distFromPlayer++;
            lookingAt = new ArrayList<DistanceFromPlayer>(1);
            lookingAt = new ArrayList<DistanceFromPlayer>(toBeLookedAt);
            toBeLookedAt = new ArrayList<DistanceFromPlayer>(1);
            for (DistanceFromPlayer distance : lookingAt) {
                if (distance.getTileNumber() == 1832)//debug purposes
                    distance.setTileNumber(1832);
                for (int num : distance.getSpotToMoveIndex()) {
                    layerHeight = num / (width * height);
                    if (layerHeight > 0)
                        num = num - (layerHeight * width * height);
                    toCheck = distanceMap[num][layerHeight];
                    if (toCheck.getDistFromPlayer() == defaultTileNumber && distanceMap[num][layerHeight].getSpotToMoveIndex().contains(distance.getTileNumber())) {
                        distanceMap[num][layerHeight].setDistFromPlayer(distFromPlayer);
                        toBeLookedAt.add(toCheck);
                    }
                }
            }
        } while (toBeLookedAt.size() != 0);

        return;
    }

    public void resetDistances() {
        for (DistanceFromPlayer[] distances : distanceMap){
            for (DistanceFromPlayer dist : distances)
            if (dist != null)
                dist.setDistFromPlayer(defaultTileNumber);
        }
    }

    public ArrayList<Integer> shortestPath (int start, int finish) {//enemytile, playertile
        int layerHeight, adjLayerHeight;
        ArrayList<Integer> intPath = new ArrayList<Integer>(1);
        //DistanceFromPlayer Pos = distanceMap.get(/*tilesAlreadyChecked.indexOf(*/start/*)*/);
        layerHeight = start / (width * height);
        if (layerHeight > 0)
            start = start - (layerHeight * width * height);
        DistanceFromPlayer leastDistance = new DistanceFromPlayer(-1, -1, width);//initial distance placeholder
        DistanceFromPlayer toCheck;
        DistanceFromPlayer Pos = distanceMap[start][layerHeight];
        /*for (int checkLayer = layerHeight; checkLayer > -1; checkLayer--){
            Pos = distanceMap[start][checkLayer];
            if (Pos.getTileNumber() != defaultTileNumber)
                break;
        }*/
        leastDistance.setDistFromPlayer(width * height + 1);
        while (Pos.getTileNumber() != finish) {
            if (Pos.getTileNumber() == 776)//debug purposes
                Pos.setTileNumber(776);
            for (int adjIndex : Pos.getSpotToMoveIndex()) {
                //toCheck = distanceMap.get(adjIndex);
                adjLayerHeight = adjIndex / (width * height);
                if (adjLayerHeight > 0)
                    adjIndex = adjIndex - (adjLayerHeight * width * height);
                toCheck = distanceMap[adjIndex][adjLayerHeight];
                if (toCheck.getTileNumber() == finish) {
                    leastDistance = toCheck;
                    break;
                }

                if (leastDistance.getDistFromPlayer() > toCheck.getDistFromPlayer() && toCheck.getDistFromPlayer() != defaultTileNumber){
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
        return intPath;
    }

    private void buildMap(int playerPos) {
        DistanceFromPlayer start = new DistanceFromPlayer(playerPos, 0, width);
        int layerHeight, adjLayerHeight, adjTilePos, tilePos;
        lookingAt = new ArrayList<DistanceFromPlayer>();
        toBeLookedAt = new ArrayList<DistanceFromPlayer>();
        toBeLookedAt.add(start);
        ArrayList<DistanceFromPlayer> adjs;
        do {
            lookingAt = new ArrayList<DistanceFromPlayer>(toBeLookedAt);
            toBeLookedAt = new ArrayList<DistanceFromPlayer>();
            for (DistanceFromPlayer mapObject : lookingAt) {
                if (mapObject.getTileNumber() == 776)//debug purposes
                    mapObject.setTileNumber(776);
                layerHeight = mapObject.getTileNumber() / (width * height);
                tilePos = mapObject.getTileNumber();
                if (layerHeight > 0)
                    tilePos = tilePos - (layerHeight * width * height);
                adjs = new ArrayList<DistanceFromPlayer>(FindAdjLocations(mapObject));
                for (DistanceFromPlayer adjPos : adjs){
                    adjLayerHeight = adjPos.getTileNumber() / (width * height);
                    adjTilePos = adjPos.getTileNumber();
                    if (adjLayerHeight > 0)
                        adjTilePos = adjPos.getTileNumber() - (adjLayerHeight * width * height);
                    if (distanceMap[adjTilePos][adjLayerHeight].getTileNumber() == defaultTileNumber && !tilesAlreadyChecked.contains(adjPos.getTileNumber())) {
                        tilesAlreadyChecked.add(adjPos.getTileNumber());
                        toBeLookedAt.add(adjPos);
                    }
                    mapObject.addSpotToMoveIndex(adjPos.getTileNumber());
                }
                distanceMap[tilePos][layerHeight] = mapObject;
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


    //start of new methods



    private boolean notAWall(DistanceFromPlayer currentLoc,  int adjTileNum, int currentTileHeight, int rampDirection, int adjTileLayer) {
    	if (adjTileNum == defaultTileNumber || adjTileNum >= height * width)//invalid tile
    		return false;
        int x = getXPos(adjTileNum);
        int y = getYPos(adjTileNum);
    	MapTile adjTile = meshLevel.getMapTile(x, y, adjTileLayer);
    	MapTile currentTile = meshLevel.getMapTile(getXPos(currentLoc.getTileNumber()), getYPos(currentLoc.getTileNumber()), currentLoc.getLayer());

    	int adjHeight = adjTile.getHeight();

    	boolean hasHeight = adjHeight > -1;

        if (adjTileLayer == 1)
            adjHeight = adjHeight + maxLayerHeight;
        if (currentLoc.getLayer() == 1)
            currentTileHeight = currentTileHeight + maxLayerHeight;
        if (adjTileLayer == 1 && currentLoc.getLayer() == 0){
            if (adjHeight > currentTileHeight + 1)//b/c layer.get(1) is object layer
                return false;
        }

    	if (!meshLevel.outOfBounds(new GridPoint2(x, y)) //contains a tile
    			&& hasHeight
    			&& (
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

    private DistanceFromPlayer checkNonDiagAdjTile(int currentAdjTile, int adjTileLayer, int currentHeight, DistanceFromPlayer mapObject, int rampDir) {
        MapTile checkTile;
        int currentLocHeight;
        for (int tileLayer = adjTileLayer; tileLayer > -1; tileLayer--)
            if (notAWall(mapObject, currentAdjTile, currentHeight, rampDir, tileLayer)
                    && !rampIsWall(mapObject, currentAdjTile, rampDir, tileLayer)) { //not a wall or wrong ramp direction
                if (tileLayer == 1)
                    currentAdjTile = currentAdjTile + width * height;
                else {
                    if (adjTileLayer == 1) {
                        checkTile = meshLevel.getMapTile(getXPos(currentAdjTile) - (width * tileLayer), getYPos(currentAdjTile), 1);
                        currentLocHeight = meshLevel.getMapTile(getXPos(mapObject.getTileNumber()), getYPos(mapObject.getTileNumber()), mapObject.getLayer()).getHeight();
                        if(checkTile.getHeight() != -1 || (checkTile.getHeight() > currentLocHeight))
                            return null;
                    }
                }
                return new DistanceFromPlayer(currentAdjTile, tileLayer, width);
            }
        return null;
    }

    private DistanceFromPlayer checkDiagAdjTile( int adjTileLayer, int currentHeight, DistanceFromPlayer mapObject, ArrayList<DistanceFromPlayer> spotNums,
                                                 int XAdjTile, int YAdjTile, int currentAdjTile, int tileLocation, int YDistFromTile){
        int currentLocHeight;
        MapTile checkTile;
        int adjDiagonalRamp = -1;//comparing to ramp direction will always return false
        for (int tileLayer = adjTileLayer; tileLayer > -1; tileLayer--)
            if ((isMoveable(spotNums, /*top*/YAdjTile) && isMoveable(spotNums, /*left*/XAdjTile))
                    && notAWall(mapObject, currentAdjTile, currentHeight, adjDiagonalRamp, tileLayer)
                    && getYPos(currentAdjTile) == getYPos(tileLocation) + YDistFromTile
                    && meshLevel.getMapTile(getXPos(currentAdjTile), getYPos(currentAdjTile), tileLayer).getRampDirection() == -1
                    && notAdjToRamp(XAdjTile, YAdjTile, tileLayer)
                    ) {
                if (tileLayer == 1)
                    if ((YAdjTile > width * width && XAdjTile > width * width) || (YAdjTile < width * width && XAdjTile < width * width))
                        currentAdjTile = currentAdjTile + width * height;
                    else
                        continue;
                else {
                    if (adjTileLayer == 1) {
                        checkTile = meshLevel.getMapTile(getXPos(currentAdjTile) - (width * tileLayer), getYPos(currentAdjTile), 1);
                        currentLocHeight = meshLevel.getMapTile(getXPos(mapObject.getTileNumber()), getYPos(mapObject.getTileNumber()), mapObject.getLayer()).getHeight();
                        if(checkTile.getHeight() != -1 || (checkTile.getHeight() > currentLocHeight))
                            return null;
                    }
                }
                return new DistanceFromPlayer(currentAdjTile, tileLayer, width);
            }
        return null;
    }

    private ArrayList<DistanceFromPlayer> FindAdjLocations(DistanceFromPlayer mapObject) {
        int adjTileLayer = 0;
        MapTile tile;
        int tileLocation = mapObject.getTileNumber();
        int currentHeight;

        if (mapObject.getTileNumber() == 1224)//debug purposes
            mapObject.setTileNumber(1224);

        if (mapObject.getTileNumber() == 232)//debug purposes
            mapObject.setTileNumber(232);

        if (mapObject.getLayer() == 1){
            mapObject.setTileNumber(mapObject.getTileNumber() - width * height);//temp change to layer 2 tile pos
            adjTileLayer = 1;
            tile = meshLevel.getMapTile(getXPos(mapObject.getTileNumber()), getYPos(mapObject.getTileNumber()), 1);
        }
        else//layer == 0
            tile = meshLevel.getMapTile(getXPos(mapObject.getTileNumber()), getYPos(mapObject.getTileNumber()), 0);

        tileLocation = mapObject.getTileNumber();
        currentHeight = tile.getHeight();

        if (currentHeight == 5) {
            adjTileLayer = 1;
        }
        //calculate adj rooms
        ArrayList<DistanceFromPlayer> spotNums = new ArrayList<DistanceFromPlayer>(1);
        int bottom, top, left, right, botLeft, botRight, topLeft, topRight;

        DistanceFromPlayer testVal = null;
        //left tile
        left = tileLocation - width;
        testVal = checkNonDiagAdjTile(left, adjTileLayer, currentHeight, mapObject, MeshLevel.LEFT);
        if (testVal != null) {
            left = testVal.getTileNumber();
            spotNums.add(testVal);
            testVal = null;
        }

        //right tile
        right = tileLocation + width;
        testVal = checkNonDiagAdjTile(right, adjTileLayer, currentHeight, mapObject, MeshLevel.RIGHT);
        if (testVal != null) {
            right = testVal.getTileNumber();
            spotNums.add(testVal);
            testVal = null;
        }

        //bottom tile
        bottom = tileLocation - 1;
        testVal = checkNonDiagAdjTile(bottom, adjTileLayer, currentHeight, mapObject, MeshLevel.DOWN);
        if (testVal != null) {
            bottom = testVal.getTileNumber();
            spotNums.add(testVal);
            testVal = null;
        }

        //top tile
        top = tileLocation + 1;
        testVal = checkNonDiagAdjTile(top, adjTileLayer, currentHeight, mapObject, MeshLevel.UP);
        if (testVal != null) {
            top = testVal.getTileNumber();
            spotNums.add(testVal);
            testVal = null;
        }

        //top left tile
        topLeft = tileLocation - width + 1;
        testVal = checkDiagAdjTile(adjTileLayer, currentHeight, mapObject, spotNums, top, left, topLeft, tileLocation, 1);
        if (testVal != null) {
            topLeft = testVal.getTileNumber();
            spotNums.add(testVal);
            testVal = null;
        }

        //bottom left tile
        botLeft = tileLocation - width - 1;
        testVal = checkDiagAdjTile(adjTileLayer, currentHeight, mapObject, spotNums, bottom, left, botLeft, tileLocation, -1);
        if (testVal != null) {
            botLeft = testVal.getTileNumber();
            spotNums.add(testVal);
            testVal = null;
        }

        //bottom right tile
        botRight = tileLocation + width - 1;
        testVal = checkDiagAdjTile(adjTileLayer, currentHeight, mapObject, spotNums, bottom, right, botRight, tileLocation, -1);
        if (testVal != null) {
            botRight = testVal.getTileNumber();
            spotNums.add(testVal);
            testVal = null;
        }

        //top right tile
        topRight = tileLocation + width + 1;
        testVal = checkDiagAdjTile(adjTileLayer, currentHeight, mapObject, spotNums, top, right, topRight, tileLocation, 1);
        if (testVal != null) {
            topRight = testVal.getTileNumber();
            spotNums.add(testVal);
            testVal = null;
        }

        if (mapObject.getLayer() == 1)
            mapObject.setTileNumber(mapObject.getTileNumber() + width * height);

        return spotNums;
    }

    private boolean notAdjToRamp(int adjXDirTileNum, int adjYDirTileNum, int tileLayer) {
        int adjXTileLayer = (int)adjXDirTileNum / (width * height);
        int adjYTileLayer = (int)adjYDirTileNum / (width * height);
        adjXDirTileNum = adjXDirTileNum - (width * width * adjXTileLayer);
        adjYDirTileNum = adjYDirTileNum - (width * width * adjYTileLayer);
        if (meshLevel.getMapTile(getXPos(adjXDirTileNum), getYPos(adjXDirTileNum), adjXTileLayer).getRampDirection() == -1
                && meshLevel.getMapTile(getXPos(adjYDirTileNum), getYPos(adjYDirTileNum), adjYTileLayer).getRampDirection() == -1
                && adjXDirTileNum == adjYDirTileNum)
            return true;

        return false;
    }


    private boolean isMoveable( ArrayList<DistanceFromPlayer> spotNums, int locationNum ) {
        for (DistanceFromPlayer spotNum : spotNums) {
            if (spotNum.getTileNumber() == locationNum)
                return true;
        }
        return false;
    }

    public int getNewPath(int start, int finish, Enemy enemy) {
        //DistanceFromPlayer enemyPos = distanceMap.get(/*tilesAlreadyChecked.indexOf(*/start/*)*/);
        int tilePos = start;
        int layerHeight = start / (width * height);
        if (layerHeight > 0)
            tilePos = tilePos - (layerHeight * width * height);
        DistanceFromPlayer enemyPos = distanceMap[tilePos][layerHeight];
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

    public ArrayList<int[]> getCheckPoints(){
        ArrayList<int[]> checkPoints = new ArrayList<int[]>();
        checkPoints = meshLevel.getEnemyWayPoints();
        for (int[] wpPos : checkPoints){

        }

        return checkPoints;
    }

    private int getXPos(int tileNumber) {
        return tileNumber /width;
    }

    private int getYPos(int tileNumber) {
        return tileNumber % width;
    }

    public boolean moveableAdjTile(int currentTileNum, int adjTileNum){
        DistanceFromPlayer currentTile = distanceMap[currentTileNum][0];
        for (int adjNum : currentTile.getSpotToMoveIndex())
            if (adjNum == adjTileNum)
                return true;
        return false;
    }

}

