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
	public static final int MAX_HEALTH = 100;
	public static final int DAMAGE = 10;
	private int health, damage;
	public State idle;
	public State moving;
	public State dead;
	public State spawn;
	public State attack;
	StateMachine stateMachine;
	boolean isSpawned, isAttacking;

	public Enemy() {
		super();
	}

	public Enemy(int id, boolean isActive, boolean isRenderable, Vector3 position, Vector3 rotation,
				 Vector3 scale, Vector3 velocity, Vector3 acceleration, ModelInstance model) {
		super(id, isActive, isRenderable, position, rotation,
			  scale, velocity, acceleration, model);
		this.health = MAX_HEALTH;
		this.damage = DAMAGE;
		this.isSpawned = false;
		this.isAttacking = false;
		BoundingBox box = new BoundingBox();
		box.set(new Vector3(this.getPosition().x - 1, this.getPosition().y - 1, this.getPosition().z - 1),
			    new Vector3(this.getPosition().x + 1, this.getPosition().y + 1, this.getPosition().z + 1));
		System.out.println(this.getPosition().x + " " + this.getPosition().z);
		this.getBoundingBox().set(box);
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
		TiledMapTileLayer layer = (TiledMapTileLayer)world.getMeshLevel().getTiledMap().getLayers().get(0);
		int width = layer.getWidth();
		ArrayList<Integer> path;
		
		if (this.getStateMachine().Current == this.idle) {
			this.getAnimation().setAnimation("Idle", -1);
            this.getVelocity().set(0,0,0);
		}

		else if (this.getStateMachine().Current == this.moving) {
            try {
                path = this.shortestPath(thisPosition.x + width
                        * thisPosition.y, playerPosition.x + width
                        * playerPosition.y, layer, world.getDistanceMap());
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


            float targetHeight = world.getMeshLevel().getHeightOffset()
                    + world.getMeshLevel().mapHeight(
                    this.getPosition().x, this.getPosition().z);
            if (this.getPosition().y > targetHeight + 30 * delta) {
                this.getPosition().y -= 30 * delta;

            } else if (this.getPosition().y < targetHeight) {
                this.getPosition().y = targetHeight;

            } else {
                this.getPosition().y = world.getMeshLevel()
                        .getHeightOffset()
                        + world.getMeshLevel().mapHeight(
                        this.getPosition().x,
                        this.getPosition().z);
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
			this.getAnimation().setAnimation("Attacking", 1, new AnimationListener() {
				
				@Override
			public void onLoop(AnimationDesc animation) {
					// TODO Auto-generated method stub
			}
				
			@Override
			public void onEnd(AnimationDesc animation) {
				doDamage();
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
		Condition idleCondition=new Condition() {
			@Override
			public boolean IsSatisfied(Enemy enemy) {
				/*
				GridPoint2 enemyPosition = new GridPoint2((int)enemy.getPosition().x, (int)enemy.getPosition().z);
				GridPoint2 playerPosition = new GridPoint2((int)World.player.camera.position.x, (int)World.player.camera.position.z);
				TiledMapTileLayer layer = (TiledMapTileLayer)Assets.castle.getLayers().get(0);
				int width = layer.getWidth();
                TiledMapTileLayer firstLayer = (TiledMapTileLayer)Assets.castle.getLayers().get(0);
                TiledMapTile playerTile = firstLayer.getCell((int)playerPosition.x, (int)playerPosition.y).getTile();
                int playerTileHeight = Integer.parseInt(playerTile.getProperties().get("height").toString());

                if (!enemy.seesPlayer(enemyPosition.x + width * enemyPosition.y, 
                					  playerPosition.x + width * playerPosition.y, 
                					  playerTileHeight, layer)) {
                	return true;
                }
                else
                	return false;
*/
				if (!enemy.getTransformedEnemyDetectionBoundingBox().intersects(World.player.getTransformedBoundingBox()))
					return true;
				else
					return false;

			}
		};
		
		Condition movingCondition=new Condition() {
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
      
				if (enemy.getTransformedEnemyDetectionBoundingBox().intersects(World.player.getTransformedBoundingBox()) && !enemy.isAttacking)
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
				if (!enemy.isSpawned)
					return true;
				else
					return false;
			}
		};
		
		Condition attackCondition = new Condition() {
			@Override
			public boolean IsSatisfied(Enemy enemy) {
				if (enemy.getTransformedEnemyAttackBoundingBox().intersects(World.player.getTransformedBoundingBox()) && getStateMachine().Current != dead) {
					return true;
				}
				else {
					return false;
				}
			}
		};

		/*
		idle.LinkedStates.put(movingCondition, moving);
		idle.LinkedStates.put(deadCondition, dead);
		idle.LinkedStates.put(spawnCondition, spawn);
		moving.LinkedStates.put(idleCondition, idle);
		moving.LinkedStates.put(deadCondition, dead);
		moving.LinkedStates.put(attackCondition, attack);
		*/
		idle.LinkedStates.put(idleCondition, idle);
		moving.LinkedStates.put(movingCondition, moving);
		dead.LinkedStates.put(deadCondition, dead);
		spawn.LinkedStates.put(spawnCondition, spawn);
		attack.LinkedStates.put(attackCondition, attack);
		
		stateMachine.States.add(idle);
		stateMachine.States.add(moving);
		stateMachine.States.add(dead);
		stateMachine.States.add(spawn);
		stateMachine.States.add(attack);
		
		stateMachine.Current=idle; //Set initial state
	}
	
	public void doDamage() {
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
	
	public BoundingBox getTransformedEnemyBoundingBox() {
		return this.getBoundingBox().set(new Vector3(this.getPosition().x - 0.5f, this.getPosition().y - 1f, this.getPosition().z - 0.5f),
			    						 new Vector3(this.getPosition().x + 0.5f, this.getPosition().y + 1f, this.getPosition().z + 0.5f));
	}
	
	public BoundingBox getTransformedEnemyDetectionBoundingBox() {
		return this.getBoundingBox().set(new Vector3(this.getPosition().x - 15f, this.getPosition().y - 15f, this.getPosition().z - 15f),
			    						 new Vector3(this.getPosition().x + 15f, this.getPosition().y + 15f, this.getPosition().z + 15f));
	}
	
	public BoundingBox getTransformedEnemyAttackBoundingBox() {
		return this.getBoundingBox().set(new Vector3(this.getPosition().x - 2f, this.getPosition().y - 2f, this.getPosition().z - 2f),
			    						 new Vector3(this.getPosition().x + 2f, this.getPosition().y + 2f, this.getPosition().z + 2f));
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
    
    public ArrayList<Integer> shortestPath(int startLoc, int endLoc, TiledMapTileLayer layer, DistanceTrackerMap distanceMap) {
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