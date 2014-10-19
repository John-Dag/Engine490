package com.gdx.DynamicEntities;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector3;
import com.gdx.engine.Condition;
import com.gdx.engine.State;
import com.gdx.engine.StateMachine;
import com.gdx.engine.World;

import java.util.ArrayList;

public class Enemy extends DynamicEntity {
	public static final int MAX_HEALTH = 100;
	public static final int DAMAGE = 1;
	private int health, damage;
    private TiledMap tiledMap;
    private ArrayList<String> showXY = new ArrayList<String>();
    private int currentHeight = 1;
	public State idle;
	public State moving;
	public State dead;
	public State spawn;
	StateMachine stateMachine;

	public Enemy() {
		super();
	}

	public Enemy(int id, boolean isActive, boolean isRenderable, Vector3 position, Vector3 rotation,
				 Vector3 scale, Vector3 velocity, Vector3 acceleration, ModelInstance model) {
		super(id, isActive, isRenderable, position, rotation,
			  scale, velocity, acceleration, model);
		this.health = MAX_HEALTH;
		this.damage = DAMAGE;
		idle = new State();
		moving = new State();
		dead = new State();
		spawn = new State();
		stateMachine=new StateMachine();
		this.StateMachineUsage(this);
	}

	@Override
	public void update(float delta) {
		this.updatePosition(delta);
		this.updateInstanceTransform();
		this.getAnimation().update(delta);
		this.stateMachine.UpdateStates(this);
	}
	
	private void StateMachineUsage(Enemy enemy){
		Condition idleCondition=new Condition() {
			@Override
			public boolean IsSatisfied(Enemy enemy) {
				if (!enemy.getTransformedDetectionBoundingBox().intersects(World.player.getTransformedBoundingBox()))
					return true;
				else
					return false;
			}
		};
		
		Condition movingCondition=new Condition() {
			@Override
			public boolean IsSatisfied(Enemy enemy) {
				if (enemy.getTransformedDetectionBoundingBox().intersects(World.player.getTransformedBoundingBox()))
					return true;
				else
					return false;
			}
		};
		
		Condition deadCondition=new Condition() {
			@Override
			public boolean IsSatisfied(Enemy enemy) {
				if (enemy.health <= 0) {
					return true;
				}
				else
					return false;
			}
		};
		
		Condition spawnCondition = new Condition() {
			@Override
			public boolean IsSatisfied(Enemy enemy) {
				return false;
			}
		};

		idle.LinkedStates.put(movingCondition, moving);
		idle.LinkedStates.put(deadCondition, dead);
		moving.LinkedStates.put(idleCondition, idle);
		moving.LinkedStates.put(deadCondition, dead);
		
		stateMachine.States.add(idle);
		stateMachine.States.add(moving);
		stateMachine.States.add(dead);
		stateMachine.States.add(spawn);
		
		stateMachine.Current=idle; //Set initial state
	}
	
	public StateMachine getStateMachine() {
		return stateMachine;
	}

	public void setStateMachine(StateMachine stateMachine) {
		this.stateMachine = stateMachine;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}
	
    private int getXPos(int tileNumber, TiledMapTileLayer layer) {
        return tileNumber / layer.getWidth();
    }

    private int getYPos (int tileNumber, TiledMapTileLayer layer) {
        return tileNumber % layer.getHeight();
    }

    public ArrayList<Integer> shortestPath(int startLoc, int endLoc, TiledMapTileLayer layer, int currentHeight) {
        this.currentHeight = currentHeight;
        int width = layer.getWidth();
        int height = layer.getHeight();
        ArrayList<TilePathTile> tilePath = new ArrayList<TilePathTile>();
        ArrayList<TilePathTile> movablePlaces = new ArrayList<TilePathTile>();
        ArrayList<Integer> adjLocs;
        ArrayList<Integer> intPath = new ArrayList<Integer>();
        ArrayList<String> showMoveable = new ArrayList<String>();
        showXY = new ArrayList<String>();
        TilePathTile currentLoc = new TilePathTile(startLoc);
        int workToFinish;
        int workToStart;
        int minWorkToFinish = -1;
        TilePathTile adjTile;
        TilePathTile removePlace = new TilePathTile(-1);
        int x; int y; int prevX; int prevY;

        movablePlaces.add(currentLoc);
        do {
            /*if (layer.getCell(x, y).getTile().getProperties().containsKey("ramp"))
                prevIsRamp = true;
            else
                prevIsRamp = false;*/
            currentLoc = locationWithLowestWork(startLoc, endLoc, layer, movablePlaces);
            tilePath.add(currentLoc);
            x = getXPos(currentLoc.getNumber(), layer);
            y = getYPos(currentLoc.getNumber(), layer);
            if (currentLoc.getParent() != null) {
                prevX = getXPos(currentLoc.getNumber(), layer);
                prevY = getYPos(currentLoc.getNumber(), layer);
                //enemyheight = currentlocation.getheight(); note
                if (layer.getCell(x, y).getTile().getProperties().containsKey("ramp"))//if ramp increase height by 1
                    if (Integer.parseInt(layer.getCell(prevX, prevY).getTile().getProperties().get("height").toString())
                            == Integer.parseInt(layer.getCell(x, y).getTile().getProperties().get("height").toString())
                            && layer.getCell(prevX, prevY).getTile().getProperties().containsKey("ramp")) {//moved from ramp to ramp with same height
                        //may cause problem with consecutive up/down or up/up or down/down ramps
                    }
                    else if (Integer.parseInt(layer.getCell(prevX, prevY).getTile().getProperties().get("height").toString())
                            == Integer.parseInt(layer.getCell(x, y).getTile().getProperties().get("height").toString())
                            && !layer.getCell(prevX, prevY).getTile().getProperties().containsKey("ramp"))//moved from ground to ramp
                        currentHeight = currentHeight + 1;
                    else
                        currentHeight = Integer.parseInt(layer.getCell(x, y).getTile().getProperties().get("height").toString());
                else//get height of current block
                    currentHeight = Integer.parseInt(layer.getCell(x, y).getTile().getProperties().get("height").toString());
            }
            showXY.add("(" + (x) + ", " + (y) + ")" + currentLoc);
            for (TilePathTile movablePlace : movablePlaces) {
                if (movablePlace.getNumber() == currentLoc.getNumber()) {
                    removePlace = movablePlace;
                }
            }
            movablePlaces.remove(movablePlaces.indexOf(removePlace));
            //showMoveable.remove(showMoveable.indexOf("(" + getXPos(currentLoc.getNumber(), layer) + ", " + getYPos(currentLoc.getNumber(), layer) + ")" + currentLoc));
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
                    showMoveable.add("(" + getXPos(currentLoc.getNumber(), layer) + ", " + getYPos(currentLoc.getNumber(), layer) + ")" + currentLoc);
                }
                workToFinish = Math.abs((getXPos(endLoc, layer) - getXPos(adjLoc, layer)))
                        + Math.abs(getYPos(endLoc, layer) - getYPos(adjLoc, layer));
                workToStart = Math.abs(getXPos(startLoc, layer) - getXPos(adjLoc, layer))
                        + Math.abs(getYPos(startLoc, layer) - getYPos(adjLoc, layer));
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
        TiledMapTile tile = layer.getCell(getXPos(currentLoc + distFromCurrentLoc, layer), getYPos(currentLoc + distFromCurrentLoc, layer)).getTile();
        TiledMapTile currentTile = layer.getCell(getXPos(currentLoc, layer), getYPos(currentLoc, layer)).getTile();
        height = tile.getProperties().get("height").toString();
        value = Integer.parseInt(height);
        int x = getXPos(currentLoc + distFromCurrentLoc, layer);
        int y = getYPos(currentLoc + distFromCurrentLoc, layer);

        if (layer.getCell(x, y) != null //contains a tile
                && layer.getCell(x, y).getTile().getProperties().containsKey("height")
                && ((Integer.parseInt(height) == currentHeight + 1 && currentTile.getProperties().containsKey("ramp")) ||  Integer.parseInt(height) <= currentHeight)
            //(( currentTile.getProperties().containsKey("ramp") &&  (Integer.parseInt(height) + curretHeight == currentHeight + 1 ||  curretHeight - Integer.parseInt(height) = currentHeight - 1)))
                )
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
        if (getXPos(currentLoc, layer) == getXPos(currentLoc - 1, layer) && currentLoc - 1 >= 0 && notAWall(currentLoc, width, -1, layer) == true)//if in the same row
            adjLocs.add(currentLoc - 1);
        //left tile
        if (((getXPos(currentLoc, layer) == getXPos(currentLoc + 1, layer) || (currentLoc == 0 && width > 1 && height > 1)))
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

    private TilePathTile locationWithLowestWork( int startLoc, int endLoc, TiledMapTileLayer layer, ArrayList<TilePathTile> adjLocs) {
        TilePathTile lowestWorkLocation = null;
        int minWork = 100;
        int workToFinish;
        int workToStart;
        int minWorkToFinish = -1;

        for (TilePathTile adjLocTile : adjLocs) {

            workToFinish = Math.abs(getXPos(endLoc, layer) - getYPos(adjLocTile.getNumber(), layer))
                    + Math.abs(getYPos(endLoc, layer) - getYPos(adjLocTile.getNumber(), layer));
            workToStart = Math.abs(getXPos(startLoc, layer) - getXPos(adjLocTile.getNumber(), layer))
                    + Math.abs(getYPos(startLoc, layer) - getYPos(adjLocTile.getNumber(), layer));
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

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public ArrayList<Integer> closestCoverPath(int position, int playerPosition, TiledMapTileLayer mapLayer) {
        int closestCover = findClosestCover(position, playerPosition, mapLayer);
        return shortestPath(position, closestCover, mapLayer, 1/*currentheight*/);
    }

    private int findClosestCover(int position, int playerPos, TiledMapTileLayer mapLayer) {
        int mapWidth = mapLayer.getWidth(); int mapHeight = mapLayer.getHeight();
        ArrayList<TilePathTile> coverLocs;
        ArrayList<Integer> adjToCovers;
        int coverLocation = -1;
        int distFromPos = 0;
        int topRight, bottomRight, topLeft, bottomLeft;
        do {
            distFromPos++;
            topRight = position + distFromPos + (mapWidth * distFromPos);
            bottomRight = position - distFromPos + (mapWidth * distFromPos);
            topLeft = position + distFromPos - (mapWidth * distFromPos);
            bottomLeft = position - distFromPos - (mapWidth * distFromPos);
            coverLocs = findPotentialCover(topRight, bottomRight, topLeft, bottomLeft, mapLayer);
            for (TilePathTile coverLoc : coverLocs) {
                adjToCovers = movableAdjLocs(coverLoc.getNumber(), mapLayer);
                coverLocation = farthestFromPlayer(adjToCovers, playerPos, mapLayer);
                if (coverLocation != -1)
                    break;
                //get covers valid adj locations(moveable places)
                //check if adj location is in view of player
                //if multiple hiding places for one cover, choose closest one to enemy
                //test
            }
            if (coverLocation == -1)
                coverLocs = new ArrayList<TilePathTile>();
        }while (coverLocs.size() != 0);
        return coverLocation;
    }

    private int farthestFromPlayer (  ArrayList<Integer> adjToCoverTiles, int playerPos, TiledMapTileLayer mapLayer) {
        int workToPlayer;
        int highestWorkLocation = -1;
        int highestWork = -1;

        for (int adjTile : adjToCoverTiles) {

            workToPlayer = Math.abs(getXPos(adjTile,  mapLayer) - getXPos(playerPos, mapLayer))
                    + Math.abs(getYPos(adjTile, mapLayer) - getYPos(playerPos, mapLayer));
            if (highestWork < workToPlayer) {
                highestWork = workToPlayer;
                highestWorkLocation = adjTile;
            }
        }
        return highestWorkLocation;
    }

    private ArrayList<Integer> movableAdjLocs(int position, TiledMapTileLayer mapLayer) {
        ArrayList<Integer> openTiles = new ArrayList<Integer>();
        if (!adjtTileInDiffRow(position, position + 1, mapLayer) && !isCover(position + 1, mapLayer))
            openTiles.add(position + 1);
        if (!adjtTileInDiffRow(position, position - 1, mapLayer) && !isCover(position - 1, mapLayer))
            openTiles.add(position - 1);
        if (!outsideMap(position + mapLayer.getWidth(), mapLayer) && !isCover(position + mapLayer.getWidth(), mapLayer))
            openTiles.add(position + mapLayer.getWidth());
        if (!outsideMap(position - mapLayer.getWidth(), mapLayer) && !isCover(position - mapLayer.getWidth(), mapLayer))
            openTiles.add(position - mapLayer.getWidth());
        return openTiles;
    }

    private boolean outsideMap(int position, TiledMapTileLayer mapLayer) {
        if (position < 0 || position > mapLayer.getHeight() * mapLayer.getWidth())
            return true;
        else
            return false;
    }

    private boolean adjtTileInDiffRow(int startPos, int position, TiledMapTileLayer mapLayer) {
        if (getXPos(startPos, mapLayer) != getXPos(position, mapLayer))
            return true;
        else
            return false;
    }

    private ArrayList<TilePathTile> findPotentialCover(int topRight, int bottomRight, int topLeft, int bottomLeft, TiledMapTileLayer mapLayer) {
        //assuming square map
        int mapWidth = mapLayer.getWidth(); int mapHeight = mapLayer.getHeight();
        ArrayList<TilePathTile> cover = new ArrayList<TilePathTile>();
        for (int currentPos = topRight; currentPos > bottomRight; currentPos = currentPos - 1){//right side
            if (getYPos(topRight, mapLayer) != getYPos(currentPos, mapLayer) || currentPos < 0)//this may not be right//
                break;
            if (isCover(currentPos, mapLayer))
                cover.add(new TilePathTile(currentPos));
        }

        for (int currentPos = bottomRight; currentPos > bottomLeft; currentPos = currentPos - mapWidth){//bottom side
            if (getXPos(topRight, mapLayer) != getXPos(currentPos, mapLayer) || currentPos < 0)//not on same row or not in map
                break;
            if (isCover(currentPos, mapLayer))
                cover.add(new TilePathTile(currentPos));
        }

        for (int currentPos = bottomLeft; currentPos > topLeft; currentPos = currentPos + 1){//left side
            if (getYPos(topRight, mapLayer) != getYPos(currentPos, mapLayer) || currentPos > mapHeight * mapWidth)//may not be right//
                break;
            if (isCover(currentPos, mapLayer))
                cover.add(new TilePathTile(currentPos));
        }

        for (int currentPos = topLeft; currentPos > topRight; currentPos = currentPos + mapWidth){//top side
            if (getXPos(topRight, mapLayer) != getXPos(currentPos, mapLayer) || currentPos > mapHeight * mapWidth)//not on same row or not in map
                break;
            if (isCover(currentPos, mapLayer))
                cover.add(new TilePathTile(currentPos));
        }

        return cover;
    }

    private boolean isCover(int location, TiledMapTileLayer mapLayer) {
        if (mapLayer.getCell(getXPos(location, mapLayer), getYPos(location, mapLayer)).getTile().getProperties().containsKey("ramp")
                || mapLayer.getCell(getXPos(location, mapLayer), getYPos(location, mapLayer)).getTile().getProperties().containsKey("height"))
            return true;
        else
            return false;
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
