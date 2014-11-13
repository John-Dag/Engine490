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
import com.gdx.engine.Condition;
import com.gdx.engine.DistanceTrackerMap;
import com.gdx.engine.State;
import com.gdx.engine.StateMachine;
import com.gdx.engine.World;

import java.util.ArrayList;

public class Enemy extends DynamicEntity {
	private int health, damage;
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
		//TiledMapTileLayer layer = (TiledMapTileLayer)world.getMeshLevel().getTiledMap().getLayers().get(0);
		int width = world.getMeshLevel().getMapXDimension();
		ArrayList<Integer> path;
		
		if (this.getStateMachine().Current == this.idle) {
			this.getAnimation().setAnimation("Idle", -1);
            this.getVelocity().set(0,0,0);
		}

		else if (this.getStateMachine().Current == this.moving) {
            try {
                path = this.shortestPath(thisPosition.x + width
                        * thisPosition.y, playerPosition.x + width
                        * playerPosition.y, world.getDistanceMap());
            } catch (Exception ex) {
                path = new ArrayList<Integer>();
            }
            if (path.size() == 0) 
                return;

            this.getAnimation().setAnimation("Walking", -1);
            Vector3 vel = new Vector3();
            int y = path.get(0) / width;
            int x = path.get(0) % width;
            vel.x = x - thisPosition.x;
            vel.z = y - thisPosition.y;

            if (vel.x == 0 && vel.z == 0 && path.size() > 1) {
                y = path.get(1) / width;
                x = path.get(1 ) % width;
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

            float heightValueLvl1 = world.getMeshLevel().mapHeight(
            	     this.getPosition().x, this.getPosition().z, 1);
            float heightValueLvl2 = 6 + world.getMeshLevel().mapHeight(
            	     this.getPosition().x, this.getPosition().z, 2);
            if (this.getPosition().y >= 6) {
            	this.getPosition().y = heightValueLvl2;
            }
            if (this.getPosition().y < 6 + 0.5f) {
            	this.getPosition().y = heightValueLvl1;
            }
            
//            float targetHeight = world.getMeshLevel().getHeightOffset()
//                    + world.getMeshLevel().mapHeight(
//                    this.getPosition().x, this.getPosition().z, 1);
//            if (this.getPosition().y > targetHeight + 30 * delta) {
//                this.getPosition().y -= 30 * delta;
//
//            } else if (this.getPosition().y < targetHeight) {
//                this.getPosition().y = targetHeight;
//
//            } else {
//                this.getPosition().y = world.getMeshLevel()
//                        .getHeightOffset()
//                        + world.getMeshLevel().mapHeight(
//                        this.getPosition().x,
//                        this.getPosition().z, 1);
//            }
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
			this.getAnimation().setAnimation("Attacking", -1, new AnimationListener() {
			
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
		return this.getBoundingBox().set(new Vector3(this.getPosition().x - 0.5f, this.getPosition().y - 0f, this.getPosition().z - 0.5f),
			    						 new Vector3(this.getPosition().x + 0.5f, this.getPosition().y + 1f, this.getPosition().z + 0.5f));
	}
	
	public BoundingBox getTransformedEnemyDetectionBoundingBox() {
		return this.getBoundingBox().set(new Vector3(this.getPosition().x - 15f, this.getPosition().y - 15f, this.getPosition().z - 15f),
			    						 new Vector3(this.getPosition().x + 15f, this.getPosition().y + 15f, this.getPosition().z + 15f));
	}
	
	public BoundingBox getTransformedEnemyAttackBoundingBox() {
		return this.getBoundingBox().set(new Vector3(this.getPosition().x - 1f, this.getPosition().y - 0f, this.getPosition().z - 1f),
			    						 new Vector3(this.getPosition().x + 1f, this.getPosition().y + 1f, this.getPosition().z + 1f));
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

    private int getYPos (int tileNumber, TiledMapTileLayer layer) {
        return tileNumber % layer.getHeight();
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