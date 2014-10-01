package com.gdx.engine;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector3;
import java.util.ArrayList;

/**
 * Created by Austin on 9/21/2014.
 */

public class Enemy extends Entity {
    private TiledMap tiledMap;
    private  ArrayList<String> showXY = new ArrayList<String>();

    public Enemy (Vector3 position, Vector3 rotation, Vector3 scale, boolean active, 
		      int index, boolean collision, ModelInstance model) {
    	super(position, rotation, scale, active, index, collision, model);
    }

    public ArrayList<Integer> shortestPath(int startLoc, int endLoc, TiledMapTileLayer layer) {
        int width = layer.getWidth();
        int height = layer.getHeight();
        ArrayList<TilePathTile> tilePath = new ArrayList<TilePathTile>();
        ArrayList<TilePathTile> movablePlaces = new ArrayList<TilePathTile>();
        ArrayList<Integer> adjLocs = new ArrayList<Integer>();
        ArrayList<Integer> intPath = new ArrayList<Integer>();
        ArrayList<String> showMoveable = new ArrayList<String>();
        showXY = new ArrayList<String>();
        //int currentLoc = startLoc;//current enemy location
        TilePathTile currentLoc = new TilePathTile(startLoc);
        int workToFinish;
        int workToStart;
        int minWorkToFinish = -1;
        TilePathTile adjTile;
        TilePathTile removePlace = new TilePathTile(-1);

        movablePlaces.add(currentLoc);
        do {
            currentLoc = locationWithLowestWork(startLoc, endLoc, width, height, movablePlaces);
            tilePath.add(currentLoc);
            showXY.add("(" + (currentLoc.getNumber() / width) + ", " + (currentLoc.getNumber() % width) + ")" + currentLoc);
            for (TilePathTile movablePlace : movablePlaces) {
                if (movablePlace.getNumber() == currentLoc.getNumber()) {
                    removePlace = movablePlace;
                }
            }
            movablePlaces.remove(movablePlaces.indexOf(removePlace));
            //showMoveable.remove(showMoveable.indexOf("(" + (currentLoc.getNumber() / width) + ", " + (currentLoc.getNumber() % width) + ")" + currentLoc));
            if (currentLoc.getNumber() == endLoc) break;

            adjLocs = FindAdjLocations(currentLoc.getNumber(), width, height, layer);

            int lowestWorkLocation = 0;
            int minWork = 100;

            for (int adjLoc : adjLocs) {
                if (inArray(tilePath, adjLoc)) continue;
                if (!inArray(tilePath, adjLoc) && !inArray(movablePlaces, adjLoc)){
                    adjTile = new TilePathTile(adjLoc);
                    adjTile.setParent(currentLoc);
                    movablePlaces.add(adjTile);
                    showMoveable.add("(" + (currentLoc.getNumber() / width) + ", " + (currentLoc.getNumber() % width) + ")" + currentLoc);
                }
                workToFinish = Math.abs((endLoc / width) - (adjLoc / width))
                        + Math.abs((endLoc % height) - (adjLoc % height));
                workToStart = Math.abs((startLoc / width) - (adjLoc / width))
                        + Math.abs((startLoc % height) - (adjLoc % height));
                if (minWork > workToFinish + workToStart /* moved farther away from start?*/) {
                    minWork = workToFinish + workToStart;
                    minWorkToFinish = workToFinish;
                    lowestWorkLocation = adjLoc;
                }
                else if (minWork == workToFinish + workToStart)
                    if (workToFinish <= minWorkToFinish) {
                        minWork = workToFinish + workToStart;
                        minWorkToFinish = workToFinish;
                        lowestWorkLocation = adjLoc;
                    }
            }
        } while (movablePlaces.size() != 0);

        while (currentLoc.getNumber() != startLoc) {
            intPath.add(currentLoc.getNumber());
            currentLoc = currentLoc.getParent();
        }
        intPath.add(startLoc);

        return intPath;
    }

    private boolean inArray(ArrayList<TilePathTile> array, int adjLoc) {
        for (TilePathTile tile : array) {
            if (tile.getNumber() == adjLoc)
                return true;
        }
        return false;
    }

    private boolean notAWall(int currentLoc, int width,  int distFromCurrentLoc, TiledMapTileLayer layer) {
    	String height = new String();
    	int value;
    	TiledMapTile tile = layer.getCell((currentLoc + distFromCurrentLoc) / width, (currentLoc + distFromCurrentLoc) % width).getTile();
    	height = tile.getProperties().get("height").toString();
    	value = Integer.parseInt(height);
    	
        if (layer.getCell((currentLoc + distFromCurrentLoc) / width, (currentLoc + distFromCurrentLoc) % width) != null //contains a tile
                && layer.getCell((currentLoc + distFromCurrentLoc) / width, (currentLoc + distFromCurrentLoc) % width).getTile().getProperties().containsKey("height")
                && value == 1) //not a wall)
            return true;
        else
            return false;
    }

    private ArrayList<Integer> FindAdjLocations(int currentLoc, int width, int height,  TiledMapTileLayer layer) {
        //calculate adj rooms
        ArrayList<Integer> adjLocs = new ArrayList<Integer>();

        //bottom tile
        if (0 < currentLoc - width && notAWall(currentLoc, width, -width, layer) == true) //not a wall
            adjLocs.add(currentLoc - width);
        //top tile
        if (width * height > currentLoc + width && notAWall(currentLoc, width, width, layer) == true)
            adjLocs.add(currentLoc + width);
        //right tile
        if (currentLoc / width == (currentLoc - 1) / width && currentLoc - 1 >= 0 && notAWall(currentLoc, width, -1, layer) == true)//if in the same row
            adjLocs.add(currentLoc - 1);
        //left tile
        if (((currentLoc / width == (currentLoc + 1) / width || (currentLoc == 0 && width > 1 && height > 1)))
                && width * height >= currentLoc + 1 && notAWall(currentLoc, width, 1, layer) == true)//if in the same row
            adjLocs.add(currentLoc + 1);

        //bottom left tile
        if (adjLocs.indexOf(currentLoc - width) != -1 && adjLocs.indexOf(currentLoc + 1) != -1 && notAWall(currentLoc, width, -width + 1, layer) == true)
            adjLocs.add(currentLoc - width + 1);

        //bottom right tile
        if (adjLocs.indexOf(currentLoc - width) != -1 && adjLocs.indexOf(currentLoc - 1) != -1 && notAWall(currentLoc, width, -width - 1, layer) == true)
            adjLocs.add(currentLoc - width - 1);

        //top right tile
        if (adjLocs.indexOf(currentLoc + width) != -1 && adjLocs.indexOf(currentLoc - 1) != -1 && notAWall(currentLoc, width, width - 1, layer) == true)
            adjLocs.add(currentLoc + width - 1);

        //top left tile
        if (adjLocs.indexOf(currentLoc + width) != -1 && adjLocs.indexOf(currentLoc + 1) != -1 && notAWall(currentLoc, width, width + 1, layer) == true)
            adjLocs.add(currentLoc + width + 1);

        return adjLocs;
    }

    private TilePathTile locationWithLowestWork( int startLoc, int endLoc, int width, int height, ArrayList<TilePathTile> adjLocs) {
        TilePathTile lowestWorkLocation = null;
        int minWork = 100;
        int workToFinish;
        int workToStart;
        int minWorkToFinish = -1;

        for (TilePathTile adjLocTile : adjLocs) {

            workToFinish = Math.abs((endLoc / width) - (adjLocTile.getNumber() / width))
                    + Math.abs((endLoc % height) - (adjLocTile.getNumber() % height));
            workToStart = Math.abs((startLoc / width) - (adjLocTile.getNumber() / width))
                    + Math.abs((startLoc % height) - (adjLocTile.getNumber() % height));
            if (minWork > workToFinish + workToStart) {
                minWork = workToFinish + workToStart;
                minWorkToFinish = workToFinish;
                lowestWorkLocation = adjLocTile;
            }
            else if (minWork == workToFinish + workToStart)
                if (workToFinish < minWorkToFinish) {
                    minWork = workToFinish + workToStart;
                    minWorkToFinish = workToFinish;
                    lowestWorkLocation = adjLocTile;
                }
        }
        return lowestWorkLocation;
    }
}

class TilePathTile {
    TilePathTile parent = null;
    int number;

    public TilePathTile(int number) {
        this.number = number;
    }

    public TilePathTile getParent() {
        return parent;
    }

    public void setParent(TilePathTile parent) {
        this.parent = parent;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}