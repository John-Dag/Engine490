package com.gdx.DynamicEntities;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController.AnimationDesc;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController.AnimationListener;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.gdx.engine.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Enemy extends DynamicEntity {
	private int health, damage;
	private Vector3 boundingBoxMinimum;
	private Vector3 boundingBoxMaximum;
	public State idle;
	public State moving;
	public State dead;
	public State spawn;
	public State attack;
	public StateMachine stateMachine;
	public boolean isSpawned, isAttacking;
	public Vector3 spawnPos;

	public Enemy() {
		super();
	}

	public Enemy(int id, boolean isActive, boolean isRenderable, Vector3 position, Vector3 rotation,
				 Vector3 scale, Vector3 velocity, Vector3 acceleration, ModelInstance model) {
		super(id, isActive, isRenderable, position, rotation,
			  scale, velocity, acceleration, model);
		this.isSpawned = false;
		this.isAttacking = false;
		this.spawnPos = position.cpy();
		boundingBoxMinimum = new Vector3();
		boundingBoxMaximum = new Vector3();
		idle = new State();
		moving = new State();
		dead = new State();
		spawn = new State();
		attack = new State();
		stateMachine = new StateMachine();
		this.StateMachineUsage(this);
	}

	@Override
	public void update(float delta, World world) {
		this.updatePosition(delta);
		this.updateInstanceTransform();
		this.getAnimation().update(delta);
		this.stateMachine.UpdateStates(this);
		
		GridPoint2 thisPosition = new GridPoint2((int)this.getPosition().x, (int)this.getPosition().z);
		GridPoint2 playerPosition = new GridPoint2((int)world.getPlayer().camera.position.x, (int)world.getPlayer().camera.position.z);
		int meshLevelHeight = 0;
        int[] directionVals = new int[3];
        int x; int y; int yKeep;
		int width = world.getMeshLevel().getMapXDimension();
		String adjPos = "";
		ArrayList<Integer> path;
		Vector3 checkPos = this.getPosition();
		float heightValueLvl1 = world.getMeshLevel().mapHeight(
				this.getPosition().x, this.getPosition().z, 1);
		float heightValueLvl2 = 6 + world.getMeshLevel().mapHeight(
				this.getPosition().x, this.getPosition().z, 2);
		float targetHeight = world.getMeshLevel().getHeightOffset()
				+ world.getMeshLevel().mapHeight(
				this.getPosition().x, this.getPosition().z, 1);

		if (this.getStateMachine().Current == this.idle) {
			this.getAnimation().setAnimation("Idle", -1);
            this.getVelocity().set(0,0,0);
		}

		else if (this.getStateMachine().Current == this.moving) {
            int playerTile = playerPosition.x + width
                    * playerPosition.y;
            if (world.getPlayer().camera.position.y > 7)
                playerTile = playerTile + width * width;
             try {
                path = this.shortestPath(thisPosition.x + width
                        * thisPosition.y, playerTile, world.getDistanceMap());
            } catch (Exception ex) {
                path = new ArrayList<Integer>();
            }
            if (path.size() == 0) 
                return;

           //calc vel
            directionVals = calcVel(path, delta, width, thisPosition, world);
            
            //Check collision with other enemies

            World.enemyInstances.sort(new Comparator<Enemy>() {
				@Override
				public int compare(Enemy arg0, Enemy arg1) {
					if(arg0.getPosition().dst(World.player.getPosition()) > arg1.getPosition().dst(World.player.getPosition()))
					    return 1;
					return -1;
				}
			});



            if (enemyEnemyCollision(delta)) {
               ArrayList<Integer> newPath = new ArrayList<Integer>();
                newPath.add(world.getDistanceMap().getNewPath(thisPosition.x + width * thisPosition.y, playerTile, this));
                if (newPath.get(0) == thisPosition.x + width * thisPosition.y)
                    this.getVelocity().set(0, 0, 0);
                else
                    directionVals = calcVel(newPath, delta, width, thisPosition, world);
            }

			//this.getVelocity().set(0, 0, 0);
            //set tile
            //reget vel and dir

            if(this.getVelocity().len() > 0)
            	this.getAnimation().setAnimation("Walking", -1);
            else
            	this.getAnimation().setAnimation("Idle", -1);

            x = directionVals[0];
            y = directionVals[1];
            yKeep = directionVals[2];

			if (this.getPosition().x == 7 && (int)this.getPosition().z == 8 )
				x = x + 1 - 1;

            if (this.getPosition().y >= 6) {//6?
                checkPos.y = heightValueLvl2;
            }
            else if (this.getPosition().y < 6) {
                checkPos.y = heightValueLvl1;
            }
            this.setPosition(checkPos);
			//adjusts enemy position to center of tile
			if (this.getPosition().y >= 6)
				meshLevelHeight = (int)this.getPosition().y / 6;
            if (x >= 0 && y >= 0 && world.getMeshLevel().getMapTile(x, y, meshLevelHeight) != null
                    && this.getPosition().x + 2 < width && this.getPosition().x - 2 > 0
                    && this.getPosition().z + 2 < width && this.getPosition().z - 2 > 0)
                if ( world.getMeshLevel().getMapTile(x, y, meshLevelHeight).getRampDirection() != -1 || !(adjPos = adjToWall((y * width) + x, world, width)).equals("")) {
                    if (this.getPosition().z < y + .5f && this.getPosition().z > y - .5f && (this.getRotation().x == 90 || this.getRotation().x == 270))
                        this.getPosition().z = y + .5f;
                    if (this.getPosition().z > y + .5f && this.getPosition().z < y + 1.5f && (this.getRotation().x == 90 || this.getRotation().x == 270))
                        this.getPosition().z = y + .5f;
                    if (this.getPosition().x < x + .5f && this.getPosition().x > x - .5f && (this.getRotation().x == 0 || this.getRotation().x == 180))
                        this.getPosition().x = x + .5f;
                    if (this.getPosition().x > x + .5f && this.getPosition().x < x + 1.5f && (this.getRotation().x == 0 || this.getRotation().x == 180))
                        this.getPosition().x = x + .5f;
                }

			//adjust enemy pos to 2nd layer?
            if (this.getPosition().y >= 6)
                targetHeight = 6
                        + world.getMeshLevel().mapHeight(
                        this.getPosition().x, this.getPosition().z, 2);
            if (this.getPosition().y > targetHeight + 30 * delta) {
                this.getPosition().y -= 30 * delta;

            } else if (this.getPosition().y < targetHeight) {
                this.getPosition().y = targetHeight;

            } else {
                this.getPosition().y = world.getMeshLevel()
                        .getHeightOffset()
                        + world.getMeshLevel().mapHeight(
                        this.getPosition().x,
                        this.getPosition().z, 1);
            }

		}
		
		else if (this.getStateMachine().Current == this.spawn) {
			this.getAnimation().setAnimation("Reincarnating", 1, new AnimationListener() {
			
			@Override
			public void onLoop(AnimationDesc animation) {
					// TODO Auto-generated method stub
			}
				
			@Override
			public void onEnd(AnimationDesc animation) {
				setSpawned(true);
				getStateMachine().Current = idle;
			}
		});
		}
		
		else if (this.getStateMachine().Current == this.attack){
            this.getVelocity().set(0,0,0); 
			this.getAnimation().setAnimation("Attacking", -1, 2.0f, new AnimationListener() {
			
				@Override
			public void onLoop(AnimationDesc animation) {
				dealDamage();
			}
				
			@Override
			public void onEnd(AnimationDesc animation) {
				
			}
		});
		}
		
		else if (this.getStateMachine().Current == this.dead) {
			this.getVelocity().set(0, 0, 0);
			World.enemyInstances.removeValue(this, true);
			this.getAnimation().setAnimation("Dying", 1, new AnimationListener() {
					
					@Override
				public void onLoop(AnimationDesc animation) {
						// TODO Auto-generated method stub
						
				}
					
				@Override
				public void onEnd(AnimationDesc animation) {
					setIsActive(false);
				}
			});
		}
	}

    private int[] calcVel(ArrayList<Integer> path, float delta, int width, GridPoint2 thisPosition, World world){
        int[] result = new int[3];
        Vector3 vel = new Vector3();
        int yKeep = path.get(0) / width;
        int y;
        if (path.get(0) > width * width)
            y = (path.get(0) - width * width) / width;
        else
            y = path.get(0) / width;
        int x = path.get(0) % width;

        vel.x = x - thisPosition.x;
        vel.z = y - thisPosition.y;

        if (vel.x == 0 && vel.z == 0 && path.size() > 1) {
            yKeep = path.get(1) / width;
            if (path.get(1) > width * width)
                y = (path.get(1) - width * width) / width;
            else
                y = path.get(1) / width;
            x = path.get(1) % width;
            vel.x = x - thisPosition.x;
            vel.z = y - thisPosition.y;
        }

        vel.y = 0;
        vel.nor();
        vel.scl(2f);
        Vector2 angleVector = new Vector2(vel.z, vel.x);
        this.getRotation().x = angleVector.angle();// +90 because
        // model is
        // originally 90
        // degrees off
        // when loaded
        this.getVelocity().set(vel);
        Vector3 collisionVector = world.getMeshLevel()
                .checkCollision(this.getPosition(),
                        this.getNewPosition(delta), 1.6f, 1.6f,
                        1.6f);
        this.getVelocity().set(this.getVelocity().x
                * collisionVector.x, this.getVelocity().y
                * collisionVector.y, this.getVelocity().z
                * collisionVector.z);
        result[0] = x;
        result[1] = y;
        result[2] = yKeep;
        return result;
     }

    public boolean enemyEnemyCollision(float delta) {
        for(Enemy enemy:World.enemyInstances)
        {
            if(enemy==this)
                continue;
            if(this.getPosition().dst(enemy.getPosition()) > 4)
                continue;

            if(this.getPosition().dst(enemy.getPosition()) < 1)
            {
                if(enemy.getPosition().dst(this.getNewPosition(delta)) < enemy.getPosition().dst(this.getPosition()))
                {
                    return true;
                }
            }
        }
        return false;
    }
	
    private String adjToWall(int tileNumber, World world, int width){
        HashMap<String, Integer> adjTiles = new HashMap<String, Integer>();
        int currentTileHeight = world.getMeshLevel().getMapTile(getXPos(tileNumber, width), getYPos(tileNumber, width), 0).getHeight();
        //if (this.getRotation().x == 180 || this.getRotation().x == 270) {
            adjTiles.put("top", tileNumber + 1);
            adjTiles.put("bot", tileNumber - 1);
        //}
        //if (this.getRotation().x == 0 || this.getRotation().x == 90) {
            adjTiles.put("right", tileNumber + width);
            adjTiles.put("left", tileNumber - width);
        //}
        for (Map.Entry<String, Integer> adjTile : adjTiles.entrySet()) {
            if (world.getMeshLevel().getMapTile(getXPos(adjTile.getValue(), width), getYPos(adjTile.getValue(), width), 0).getHeight() == -1)
                return adjTile.getKey();
            if (world.getMeshLevel().getMapTile(getXPos(adjTile.getValue(), width), getYPos(adjTile.getValue(), width), 0).getHeight() != currentTileHeight)
                return adjTile.getKey();
        }
        return "";
    }
	
	private void StateMachineUsage(Enemy enemy){
		Condition enemyDead = new Condition() {
			@Override
			public boolean IsSatisfied(Enemy enemy) {
				if (enemy.health <= 0) {
					return true;
				}
				else
					return false;
			}
		};
		
//		Condition idleCondition = new Condition() {
//			@Override
//			public boolean IsSatisfied(Enemy enemy) {
//				
//				GridPoint2 enemyPosition = new GridPoint2((int)enemy.getPosition().x, (int)enemy.getPosition().z);
//				GridPoint2 playerPosition = new GridPoint2((int)World.player.camera.position.x, (int)World.player.camera.position.z);
//				TiledMapTileLayer layer = (TiledMapTileLayer)Assets.castle.getLayers().get(0);
//				int width = layer.getWidth();
//                TiledMapTileLayer firstLayer = (TiledMapTileLayer)Assets.castle.getLayers().get(0);
//                TiledMapTile playerTile = firstLayer.getCell((int)playerPosition.x, (int)playerPosition.y).getTile();
//                int playerTileHeight = Integer.parseInt(playerTile.getProperties().get("height").toString());
//
//                if (!enemy.seesPlayer(enemyPosition.x + width * enemyPosition.y, 
//                					  playerPosition.x + width * playerPosition.y, 
//                					  playerTileHeight, layer)) {
//                	return true;
//                }
//                else
//                	return false;
//
//				if (!enemy.getTransformedEnemyDetectionBoundingBox().intersects(World.player.getTransformedBoundingBox()))
//					return true;
//				else
//					return false;
//
//			}
//		};
		
		final Condition inAggroRange = new Condition() {
			@Override
			public boolean IsSatisfied(Enemy enemy) {
//				GridPoint2 enemyPosition = new GridPoint2((int)enemy.getPosition().x, (int)enemy.getPosition().z);
//				GridPoint2 playerPosition = new GridPoint2((int)World.player.camera.position.x, (int)World.player.camera.position.z);
//				TiledMapTileLayer layer = (TiledMapTileLayer)Assets.castle.getLayers().get(0);
//				int width = layer.getWidth();
//                TiledMapTileLayer firstLayer = (TiledMapTileLayer)Assets.castle.getLayers().get(0);
//                TiledMapTile playerTile = firstLayer.getCell((int)playerPosition.x, (int)playerPosition.y).getTile();
//                int playerTileHeight = Integer.parseInt(playerTile.getProperties().get("height").toString());
//
//                if (enemy.seesPlayer(enemyPosition.x + width * enemyPosition.y, 
//                					 playerPosition.x + width * playerPosition.y, 
//                					 playerTileHeight, layer)) {
//                	return true;
//                }
//                else
//                	return false;
      
				if (enemy.getTransformedEnemyDetectionBoundingBox().intersects(World.player.getTransformedBoundingBox()))
					return true;
				else 
					return false;
			}
		};
		
		Condition outOfAggroRange = new Condition() {
			@Override
			public boolean IsSatisfied(Enemy enemy) {
//				GridPoint2 enemyPosition = new GridPoint2((int)enemy.getPosition().x, (int)enemy.getPosition().z);
//				GridPoint2 playerPosition = new GridPoint2((int)World.player.camera.position.x, (int)World.player.camera.position.z);
//				TiledMapTileLayer layer = (TiledMapTileLayer)Assets.castle.getLayers().get(0);
//				int width = layer.getWidth();
//                TiledMapTileLayer firstLayer = (TiledMapTileLayer)Assets.castle.getLayers().get(0);
//                TiledMapTile playerTile = firstLayer.getCell((int)playerPosition.x, (int)playerPosition.y).getTile();
//                int playerTileHeight = Integer.parseInt(playerTile.getProperties().get("height").toString());
//
//                if (enemy.seesPlayer(enemyPosition.x + width * enemyPosition.y, 
//                					 playerPosition.x + width * playerPosition.y, 
//                					 playerTileHeight, layer)) {
//                	return true;
//                }
//                else
//                	return false;
      
				return !inAggroRange.IsSatisfied(enemy);
			}
		};
		
		Condition playerDead = new Condition() {
			@Override
			public boolean IsSatisfied(Enemy enemy) {
				if (!World.player.isActive())
					return true;
				else
					return false;
			}
		};
	
//		Condition spawnCondition = new Condition() {
//			@Override
//			public boolean IsSatisfied(Enemy enemy) {
//				if (!enemy.isSpawned)
//					return true;
//				else
//					return false;
//			}
//		};
		
		final Condition inAttackRange = new Condition() {
			@Override
			public boolean IsSatisfied(Enemy enemy) {
				if (enemy.getTransformedEnemyAttackBoundingBox().intersects(World.player.getTransformedBoundingBox())) {
					return true;
				}
				else {
					return false;
				}
			}
		};
		
//		Condition outOfAttackRange = new Condition() {
//			@Override
//			public boolean IsSatisfied(Enemy enemy) {
//				return !inAttackRange.IsSatisfied(enemy);
//			}
//		};
		
		idle.LinkedStates.put(inAggroRange, moving);
		idle.LinkedStates.put(enemyDead, dead);
		moving.LinkedStates.put(outOfAggroRange, idle);
		moving.LinkedStates.put(inAttackRange, attack);
		moving.LinkedStates.put(enemyDead, dead);
		moving.LinkedStates.put(playerDead, idle);
		attack.LinkedStates.put(playerDead, idle);
		attack.LinkedStates.put(enemyDead, dead);
		//attack.LinkedStates.put(outOfAttackRange, moving);
		//attack.LinkedStates.put(outOfAggroRange, idle);
		
		stateMachine.States.add(idle);
		stateMachine.States.add(moving);
		stateMachine.States.add(dead);
		stateMachine.States.add(spawn);
		stateMachine.States.add(attack);
		
		stateMachine.Current = idle; //Set initial state
	}
	
	public void dealDamage() {
		World.player.takeDamage(this.getDamage());
	}
	
	public Enemy copyEnemy() {
		Enemy enemy = new Enemy(this.getId(), this.isActive(), this.isRenderable(), this.getPosition().cpy(), this.getRotation().cpy(),
			     				this.getScale().cpy(), this.getVelocity().cpy(), this.getAcceleration().cpy(), this.getModel());
		enemy.initializeEnemy();
		return enemy;
	}
	
	public void initializeEnemy() {
		this.setAnimation(new AnimationController(this.getModel()));
		this.getStateMachine().Current = this.spawn;
		this.setInCollision(true);
		this.setIsActive(true);
	}
	
	@Override
	public BoundingBox getTransformedBoundingBox() {
		boundingBoxMinimum.set(this.getPosition().x - 0.5f, this.getPosition().y - 0f, this.getPosition().z - 0.5f);
		boundingBoxMaximum.set(this.getPosition().x + 0.5f, this.getPosition().y + 1f, this.getPosition().z + 0.5f);
		return this.getBoundingBox().set(boundingBoxMinimum,	boundingBoxMaximum);
	}
	
	public BoundingBox getTransformedEnemyDetectionBoundingBox() {
		boundingBoxMinimum.set(this.getPosition().x - 15f, this.getPosition().y - 15f, this.getPosition().z - 15f);
		boundingBoxMaximum.set(this.getPosition().x + 15f, this.getPosition().y + 15f, this.getPosition().z + 15f);
		return this.getBoundingBox().set(boundingBoxMinimum,boundingBoxMaximum);
	}
	
	public BoundingBox getTransformedEnemyAttackBoundingBox() {
		boundingBoxMinimum.set(this.getPosition().x - 1f, this.getPosition().y - 0f, this.getPosition().z - 1f);
		boundingBoxMaximum.set(this.getPosition().x + 1f, this.getPosition().y + 1f, this.getPosition().z + 1f);
		return this.getBoundingBox().set(boundingBoxMinimum,boundingBoxMaximum);
	}

	public boolean isAttacking() {
		return isAttacking;
	}

	public void setAttacking(boolean isAttacking) {
		this.isAttacking = isAttacking;
	}
	
	public void takeDamage(int damage) {
		this.health -= damage;
	}

	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public boolean isSpawned() {
		return isSpawned;
	}

	public void setSpawned(boolean isSpawned) {
		this.isSpawned = isSpawned;
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

    private int getXPos(int tileNumber, int width){
        return tileNumber / width;
    }

    private int getYPos (int tileNumber, TiledMapTileLayer layer) {
        return tileNumber % layer.getHeight();
    }

    private int getYPos (int tileNumber, int height) {
        return tileNumber % height;
    }
    
    public ArrayList<Integer> shortestPath(int startLoc, int endLoc, DistanceTrackerMap distanceMap) {
        //distanceMap.resetDistances();
        //distanceMap.addDistances(endLoc);
        return distanceMap.shortestPath(startLoc, endLoc);
    }
    
    public boolean seesPlayer(int playerPos, int currentPos, int playerHeight, TiledMapTileLayer layer) {
        Vector2 start = new Vector2(getXPos(playerPos, layer), getYPos(playerPos, layer));
        Vector2 end = new Vector2(getXPos(currentPos, layer), getYPos(currentPos, layer));
        Polygon polygon;
        Rectangle bounds = new Rectangle(0,0, 1, 1);
        for(int i = 0; i < layer.getWidth(); i++) {
            for (int j = 0; j < layer.getHeight(); j++) {
                if (layer.getCell(i,j).getTile().getProperties().containsKey("height"))
                    if (Integer.parseInt(layer.getCell(i,j).getTile().getProperties().get("height").toString()) > playerHeight) {
                        polygon = new Polygon(new float[]{0,0,bounds.width,0,bounds.width,bounds.height,0,bounds.height,0,0});
                        polygon.setPosition(i, j);
                       if (Intersector.intersectSegmentPolygon(start, end, polygon)){
                           return false;
                       }
                }
            }
        }
        
        return true;
    }
}