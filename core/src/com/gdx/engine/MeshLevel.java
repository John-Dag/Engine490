package com.gdx.engine;

import java.lang.Object;
import java.util.*;

import java.nio.FloatBuffer;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btConvexHullShape;
import com.badlogic.gdx.physics.bullet.collision.btStaticPlaneShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.badlogic.gdx.utils.Array;
import com.gdx.DynamicEntities.Weapon;
import com.gdx.Enemies.Zombie;
import com.gdx.StaticEntities.EnemySpawner;
import com.gdx.StaticEntities.HealthPowerUp;
import com.gdx.StaticEntities.Light;
import com.gdx.StaticEntities.Mist;
import com.gdx.StaticEntities.Portal;
import com.gdx.StaticEntities.PowerUp;
import com.gdx.StaticEntities.PowerUpSpawner;
import com.gdx.StaticEntities.RocketLauncherSpawn;
import com.gdx.StaticEntities.SpeedPowerUp;
import com.gdx.StaticEntities.SwordSpawn;
import com.gdx.StaticEntities.Torch;
import com.gdx.StaticEntities.WeaponSpawn;
import com.gdx.StaticEntities.WeaponSpawn.weaponSpawnTypeEnum;
import com.gdx.StaticEntities.WeaponSpawner;
import com.gdx.StaticEntities.PowerUp.powerUpTypeEnum;


// Important note to the team: (this can be confusing)
// World coordinates have x, y, z, with +x pointing East, and +z pointing South
// Tiled coordinates have x, y with +x pointing South and +y pointing East
// Many methods convert between these two coordinate systems, so its important to know which variables are in which coordinate system.

public class MeshLevel {
	private World world;
	public static Color skyColor = Color.BLACK;
	public static final float ROOT_PT5 = 0.70710678f;
	public static final int NORTH = 1;
	public static final int SOUTH = 2;
	public static final int EAST = 3;
	public static final int WEST = 4;
	
	// the direction of the ramp gradient (left = north, etc)
	public static final int LEFT = 1;
	public static final int RIGHT = 2;
	public static final int UP = 3;
	public static final int DOWN = 4;
	
	public MeshPartBuilder meshPartBuilder;
	public ModelBuilder modelBuilder;
	public ModelInstance instance;
	public Model model;
	
	// the length and width of one tile
	private static final float SPOT_WIDTH = 1;
	private static final float SPOT_LENGTH = 1;
	
	// colors of point lights
	private final int RED = 0;
	private final int WHITE = 1;
	private final int GREEN = 2;
	private final int BLUE = 3;
	private final int PURPLE = 4;
	
	private TiledMap tiledMap;
	private Array<ModelInstance> instances;
	private Array<PowerUp> powerUpInstances;
	private Array<WeaponSpawn> weaponInstances;
	private Model skySphere;
	private int triCount = 0;
	private boolean isSkySphereActive;
	//private TiledMapTileLayer tiledMapLayer0;
	private TiledMapTileLayer currentLayer;
	private int currentLayerNumber;
	private TiledMapTileLayer layer1;
	private TiledMapTileLayer layer2;
	private float heightOffset;
	//private float levelHeight;
	
	private MapTile[][][] levelArray;
	private int tileLayerWidth;
	private int tileLayerHeight;
	private int tileLayerCount;
	
	// These vectors and vertex info objects are used in the generation of the level mesh
	private Vector3 p1 = new Vector3();
	private Vector3 p2 = new Vector3();
	private Vector3 p3 = new Vector3();
	private Vector3 p4 = new Vector3();
	private VertexInfo v1 = new VertexInfo();
	private VertexInfo v2 = new VertexInfo();
	private VertexInfo v3 = new VertexInfo();
	private VertexInfo v4 = new VertexInfo();
	private Vector3 normal = new Vector3();
	
	private boolean combinedWalls;
	private boolean ceiling;
	
	private Random rand = new Random();
	
	//private int numLevel = 1;
	
	//Texture stuff
	public Map<Integer,Material> MapMaterials=new HashMap<Integer,Material>();	//Maps texture Ids to Materials
	public Map<String,Integer> MaterialIds=new HashMap<String,Integer>();		//Maps texture filenames to texture Ids
	//End Texture stuff

	ArrayList<DistanceFromPlayer> patrolPath = new ArrayList<DistanceFromPlayer>();
	
	//Bullet physics stuff
	public btCollisionShape unitBoxShape;
	public btCollisionShape unitFloorTileShape;
	public btConvexHullShape bulletUnitRampShapeUp;
	public btConvexHullShape bulletUnitRampShapeDown;
	public btConvexHullShape bulletUnitRampShapeLeft;
	public btConvexHullShape bulletUnitRampShapeRight;
	public Array<btCollisionObject> bulletObjects;
	//
	
	public MeshLevel(boolean isSkySphereActive, World world){
		this.world = world;
		modelBuilder = new ModelBuilder();
		instances = new Array<ModelInstance>();
		powerUpInstances = new Array<PowerUp>();
		weaponInstances = new Array<WeaponSpawn>();
		currentLayerNumber = 0;
		this.heightOffset = 0f;
		combinedWalls = true;
		ceiling = false;
		tileLayerCount = 2;
		tileLayerWidth = 64;
		tileLayerHeight = 64;
		levelArray = new MapTile[tileLayerWidth][tileLayerHeight][tileLayerCount];
		for(int i = 0; i < levelArray.length; i++){
			for(int j = 0; j < levelArray[i].length; j++){
				for(int k = 0; k < levelArray[i][j].length; k++){
					levelArray[i][j][k] = new MapTile();
				}
			}
		}
		initializeLevelArray();
		generateBSPDungeonArray();
		Assets.loadMeshLevelTextures(tiledMap, levelArray, MapMaterials, MaterialIds);
		initializeBulletPhysics();
		generateLevelMesh();
		bindBulletPhysics();
		
	}
	
	public MeshLevel(TiledMap tiledMap, boolean isSkySphereActive, World world) {
		this.world = world;
		modelBuilder = new ModelBuilder();
		instances = new Array<ModelInstance>();
		powerUpInstances = new Array<PowerUp>();
		weaponInstances = new Array<WeaponSpawn>();
		this.tiledMap = tiledMap;
		this.isSkySphereActive = isSkySphereActive;
		//tiledMapLayer0 = (TiledMapTileLayer) tiledMap.getLayers().get(0);
		this.currentLayer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
		this.layer1 = (TiledMapTileLayer) tiledMap.getLayers().get(0);
		this.layer2 = (TiledMapTileLayer) tiledMap.getLayers().get(2);
		this.heightOffset = 0f;
		//this.levelHeight = 0f;
		combinedWalls = true;
		
		// count the tile layers
		tileLayerCount = 0;
		for(int k = 0; k < tiledMap.getLayers().getCount(); k++){
			if(tiledMap.getLayers().get(k).getName().startsWith("Tile Layer")){
				tileLayerCount++;
			}
		}
		tileLayerWidth = this.layer1.getWidth();
		tileLayerHeight = this.layer1.getHeight();
		// initialize the levelArray
		levelArray = new MapTile[tileLayerWidth][tileLayerHeight][tileLayerCount];
		for(int i = 0; i < levelArray.length; i++){
			for(int j = 0; j < levelArray[i].length; j++){
				for(int k = 0; k < levelArray[i][j].length; k++){
					levelArray[i][j][k] = new MapTile();
				}
			}
		}
		generateLevelArray();
		Assets.loadMeshLevelTextures(tiledMap, levelArray, MapMaterials, MaterialIds);
		initializeBulletPhysics();
		generateLevelMesh();
		bindBulletPhysics();
		
	}
	
	// print levelArray
	public void printLevelArray(){
		for(int i = 0; i < levelArray.length; i++){
			for(int j = 0; j < levelArray[i].length; j++){
				System.out.print("(");
				for(int k = 0; k < levelArray[i][j].length; k++){
					levelArray[i][j][k].printTileInfo();
				}
				System.out.print("), ");
			}
			System.out.println("");
		}
	}
	
	private void initializeLevelArray(){
		for (int i = 0; i < levelArray.length; i++){
			for(int j = 0; j < levelArray[i].length; j++){
				levelArray[i][j][0].setHeight(-1);
				if(ceiling){
					levelArray[i][j][1].setHeight(0);
				}else{
					levelArray[i][j][1].setHeight(-1);
				}
				levelArray[i][j][0].setRampDirection(-1);
				levelArray[i][j][1].setRampDirection(-1);
			}
		}
	}
	
	private void initializeBulletPhysics()
	{
		unitBoxShape=new btBoxShape(new Vector3(.5f, .5f, .5f));
		//unitFloorTileShape=new btBoxShape(new Vector3(.5f, 0.01f, .5f));
		unitFloorTileShape=new btConvexHullShape();
		((btConvexHullShape)unitFloorTileShape).addPoint(new Vector3(-.5f, 0, -.5f));
		((btConvexHullShape)unitFloorTileShape).addPoint(new Vector3(-.5f, 0, .5f));
		((btConvexHullShape)unitFloorTileShape).addPoint(new Vector3(.5f, 0,  .5f));
		((btConvexHullShape)unitFloorTileShape).addPoint(new Vector3(.5f, 0, -.5f));
		
		bulletUnitRampShapeUp=new btConvexHullShape();
		bulletUnitRampShapeUp.addPoint(new Vector3(0,0,1));
		bulletUnitRampShapeUp.addPoint(new Vector3(1,1,1));
		bulletUnitRampShapeUp.addPoint(new Vector3(1,1,0));
		bulletUnitRampShapeUp.addPoint(new Vector3(0,0,0));
		

			bulletUnitRampShapeDown=new btConvexHullShape();
			bulletUnitRampShapeDown.addPoint(new Vector3(0,1,1));
			bulletUnitRampShapeDown.addPoint(new Vector3(1,0,1));
			bulletUnitRampShapeDown.addPoint(new Vector3(1,0,0));
			bulletUnitRampShapeDown.addPoint(new Vector3(0,1,0));
			
			bulletUnitRampShapeLeft=new btConvexHullShape();
			bulletUnitRampShapeLeft.addPoint(new Vector3(0,0,1));
			bulletUnitRampShapeLeft.addPoint(new Vector3(1,0,1));
			bulletUnitRampShapeLeft.addPoint(new Vector3(1,1,0));
			bulletUnitRampShapeLeft.addPoint(new Vector3(0,1,0));
			
			bulletUnitRampShapeRight=new btConvexHullShape();
			bulletUnitRampShapeRight.addPoint(new Vector3(0,1,1));
			bulletUnitRampShapeRight.addPoint(new Vector3(1,1,1));
			bulletUnitRampShapeRight.addPoint(new Vector3(1,0,0));
			bulletUnitRampShapeRight.addPoint(new Vector3(0,0,0));
		
		bulletObjects=new Array<btCollisionObject>(); 
	}
	
	private void bindBulletPhysics()
	{
		for(btCollisionObject bulletObject:bulletObjects)
		{
			//World.dynamicsWorld.addRigidBody((btRigidBody)bulletObject);
			World.dynamicsWorld.addCollisionObject(bulletObject);
		}
		System.out.println(bulletObjects.size + "level bullet objects added to dynamic sim");
		bulletObjects.clear();
	}
	
	private btCollisionObject addBoxObject()
	{
		//Vector3 inertia=new Vector3();
		//bulletShape.calculateLocalInertia(0, inertia);
		//btCollisionObject btObj=new btRigidBody(0,new BulletMotionState(),bulletShape,inertia);
		btCollisionObject btObj=new btCollisionObject();
		btObj.setCollisionShape(unitBoxShape);
		bulletObjects.add(btObj);
		btObj.userData=1;
		//btObj.activate();
		return btObj;
	}
	
	private btCollisionObject addBoxObject(float dx,float dy, float dz)
	{
		//Vector3 inertia=new Vector3();
		//bulletShape.calculateLocalInertia(0, inertia);
		//btCollisionObject btObj=new btRigidBody(0,new BulletMotionState(),bulletShape,inertia);
		btCollisionObject btObj=new btCollisionObject();
		btObj.setCollisionShape(new btBoxShape(new Vector3(dx,dy,dz)));
		bulletObjects.add(btObj);
		btObj.userData=1;
		//btObj.activate();
		return btObj;
	}
	
	private btCollisionObject addFloorObject()
	{
	
		btCollisionObject btObj=new btCollisionObject();
		btObj.setCollisionShape(unitFloorTileShape);
		bulletObjects.add(btObj);
		btObj.userData=1;
		return btObj;
	}
	
	private btCollisionObject addRampBulletShape()
	{
	
		btCollisionObject btObj=new btCollisionObject();
		btObj.setCollisionShape(unitFloorTileShape);
		bulletObjects.add(btObj);
		btObj.userData=1;
		return btObj;
	}
	
	private btCollisionObject addCustomShapeObject(btCollisionShape shape)
	{
	
		btCollisionObject btObj=new btCollisionObject();
		btObj.setCollisionShape(shape);
		bulletObjects.add(btObj);
		btObj.userData=1;
		return btObj;
	}
	
	
	private btCollisionObject addPlane()
	{
		btCollisionObject btObj=new btCollisionObject();
		btCollisionShape btShape=new btStaticPlaneShape(new Vector3(0,1,0),1f);
		btObj.setCollisionShape(btShape);
		bulletObjects.add(btObj);
		btObj.userData=1;
		//btObj.activate();
		return btObj;
	}
	
	private void createDungeonRoom(int x1, int x2, int y1, int y2) {
		x1 += rand.nextInt(2) + 1;
		x2 -= rand.nextInt(2) + 1;
		y1 += rand.nextInt(2) + 1;
		y2 -= rand.nextInt(2) + 1;

		Color color = new Color();
		Assets.loadModels();
		for(int i = y1; i < y2; i++){
			for(int j = x1; j < x2; j++){
				levelArray[j][i][0].setHeight(1);
				// chance to create an enemy
				if(rand.nextInt(1000) > 995){
					createEnemySpawner(j, i, 50);
				}
				// chance to create a point light
				if(rand.nextInt(1000) > 997){
					color.set(0f, 1f, 68f/255f, 1f);
					createPointLight(j, i, color);
				}
				if(rand.nextInt(1000) > 997){
					color.set(0f, 0f, 1f, 1f);
					createPointLight(j, i, color);
				}
				if(rand.nextInt(1000) > 997){
					color.set(127f/255f, 0f, 1f, 1f);
					createPointLight(j, i, color);
				}
				// chance to create mist
				if(rand.nextInt(1000) > 997){
					color.set(0f, 1f, 68f/255f, 1f);
					createMist(j, i, color);
				}
				// chance to create weapons
				if(rand.nextInt(1000) > 995){
					color.set(0f, 1f, 68f/255f, 1f);
					createRocket(j, i, 40);
				}
				if(rand.nextInt(1000) > 997){
					color.set(0f, 1f, 68f/255f, 1f);
					createSword(j, i, 40);
				}
			}
		}
	}
	
	// returns wall facing direction if the tile is next to a wall, -1 if not next to a wall
	// Note: if the tile is next to more than one wall, this function will only return one wall direction
	private int nextToWall(int i, int j){
		if(!outOfBounds(i+1, j)){
			if(levelArray[i+1][j][0].getHeight() != 1){
				return NORTH;
			}
		}
		if(!outOfBounds(i-1, j)){
			if(levelArray[i-1][j][0].getHeight() != 1){
				return SOUTH;
			}
		}
		if(!outOfBounds(i, j+1)){
			if(levelArray[i][j+1][0].getHeight() != 1){
				return EAST;
			}
		}
		if(!outOfBounds(i, j-1)){
			if(levelArray[i][j-1][0].getHeight() != 1){
				return WEST;
			}
		}
		return -1;
	}
	
	private void generateBSPDungeonArray(){
		splitBSP(new BSPTree(null, 0, tileLayerWidth - 1, 0, tileLayerHeight - 1));
		// set walls
		for (int i = 0; i < levelArray.length; i++){
			for(int j = 0; j < levelArray[i].length; j++){
				for(int k = 0; k < levelArray[i][j].length; k++){
					setWalls(i,j,k);
				}
			}
		}
	}
	
	public GridPoint2 getStartingPoint(){
		for (int i = 0; i < levelArray.length; i++){
			for(int j = 0; j < levelArray[i].length; j++){
				for(int k = 0; k < levelArray[i][j].length; k++){
					if(levelArray[i][j][k].getHeight() == 1){
						return new GridPoint2(j,i);
					}
				}
			}
		}
		return null;
	}
	
	private void setWalls(int i, int j, int k){
		if(levelArray[i][j][k].getHeight() == -1){
			// check out of bounds
			if(!outOfBounds(i+1, j)){
				if(levelArray[i+1][j][k].getHeight() == 1){
					levelArray[i][j][k].setHeight(5);
					if((rand.nextInt(1000)> 970)){
						createTorch(i+1, j, SOUTH);
					}
					return;
				}
			}
			if(!outOfBounds(i-1, j)){
				if(levelArray[i-1][j][k].getHeight() == 1){
					levelArray[i][j][k].setHeight(5);
					if((rand.nextInt(1000)> 970)){
						createTorch(i-1, j, NORTH);
					}
					return;
				}
			}
			if(!outOfBounds(i, j+1)){
				if(levelArray[i][j+1][k].getHeight() == 1){
					levelArray[i][j][k].setHeight(5);
					if((rand.nextInt(1000)> 970)){
						createTorch(i, j+1, EAST);
					}
					return;
				}
			}
			if(!outOfBounds(i, j-1)){
				if(levelArray[i][j-1][k].getHeight() == 1){
					levelArray[i][j][k].setHeight(5);
					if((rand.nextInt(1000)> 970)){
						createTorch(i, j-1, WEST);
					}
					return;
				}
			}
		}
	}
	
	private void splitBSP(BSPTree tree){
		int splitSpot = -1;
		if (rand.nextInt(2) == 0) {
			if (tree.x2 - tree.x1 > 16) {
				// split vertically (x) with a padding of 4 tiles on either side
				splitSpot = tree.x1 + 8 + rand.nextInt((tree.x2-8) - (tree.x1+8));
				tree.setLeftChild(new BSPTree(tree, tree.x1, splitSpot-1, tree.y1, tree.y2));
				tree.setRightChild(new BSPTree(tree, splitSpot, tree.x2, tree.y1, tree.y2));
				splitBSP(tree.getLeftChild());
				splitBSP(tree.getRightChild());
			} else if (tree.y2 - tree.y1 > 32) {
				// split horizontally (y) with a padding of 4 tiles on either side
				splitSpot = tree.y1 + 8 + rand.nextInt((tree.y2-8) - (tree.y1+8));
				tree.setLeftChild(new BSPTree(tree, tree.x1, tree.x2, tree.y1, splitSpot-1));
				tree.setRightChild(new BSPTree(tree, tree.x1, tree.x2, splitSpot, tree.y2));
				splitBSP(tree.getLeftChild());
				splitBSP(tree.getRightChild());
			} else {
				// done splitting, create room  from (tree.x1+2, tree.y1+2) to (tree.x2-2, tree.y2-2)
				createDungeonRoom(tree.x1, tree.x2, tree.y1, tree.y2);
			}
		} else {
			if (tree.y2 - tree.y1 > 16) {
				// split horizontally (y)
				splitSpot = tree.y1 + 8 + rand.nextInt((tree.y2-8) - (tree.y1+8));
				tree.setLeftChild(new BSPTree(tree, tree.x1, tree.x2, tree.y1, splitSpot-1));
				tree.setRightChild(new BSPTree(tree, tree.x1, tree.x2, splitSpot, tree.y2));
				splitBSP(tree.getLeftChild());
				splitBSP(tree.getRightChild());
			} else if (tree.x2 - tree.x1 > 32) {
				// split vertically (x) with a padding of 4 tiles on either side
				splitSpot = tree.x1 + 8 + rand.nextInt((tree.x2-8) - (tree.x1+8));
				tree.setLeftChild(new BSPTree(tree, tree.x1, splitSpot-1, tree.y1, tree.y2));
				tree.setRightChild(new BSPTree(tree, splitSpot, tree.x2, tree.y1, tree.y2));
				splitBSP(tree.getLeftChild());
				splitBSP(tree.getRightChild());
			} else {
				// done splitting, create room from (tree.x1+2, tree.y1+2) to (tree.x2-2, tree.y2-2)
				createDungeonRoom(tree.x1, tree.x2, tree.y1, tree.y2);
			}
		}
		// connect the two children
		if (tree.getLeftChild() == null || tree.getRightChild() == null) {
			connectDungeonRooms(tree.getParent());
		}
	}
	
	private void connectDungeonRooms(BSPTree parent){
		BSPTree left = parent.getLeftChild();
		BSPTree right = parent.getRightChild();

		GridPoint2 leftCenter = new GridPoint2((left.x1 + left.x2)/2, (left.y1 + left.y2)/2);
		GridPoint2 rightCenter = new GridPoint2((right.x1 + right.x2)/2, (right.y1 + right.y2)/2);

		if (left.x2 < right.x1) {
			// vertical split
			for (int i = leftCenter.x; i < rightCenter.x; i++) {
				if (!outOfBounds(i, leftCenter.y)) {
					levelArray[i][leftCenter.y][0].setHeight(1);
				}
			}
		} else if (left.y2 < right.y1) {
			// horizontal split
			for (int i = leftCenter.y; i < rightCenter.y; i++) {
				if (!outOfBounds(leftCenter.x, i)) {
					levelArray[leftCenter.x][i][0].setHeight(1);
				}
			}
		}
		if (parent.getParent()!= null) {
			connectDungeonRooms(parent.getParent());
		}
	}
	
	public void generateLevelArray() {
		TiledMapTile tile = null;
		int layerNumber = 0;
		
		for (int k = 0; k < tiledMap.getLayers().getCount(); k++) {
			if (tiledMap.getLayers().get(k).getName().startsWith("Tile Layer")) {
				currentLayer = (TiledMapTileLayer) tiledMap.getLayers().get(k);
				if (currentLayer.getName().equals("Tile Layer 1")) {
					heightOffset = 0;
					layerNumber = 0;
				} else if (currentLayer.getName().equals("Tile Layer 2")) {
					heightOffset = 6;
					layerNumber = 1;
				}
				
				// on each cell
				for (int i = 0; i < currentLayer.getWidth(); i++) {
					for (int j = 0; j < currentLayer.getHeight(); j++) {
						tile = currentLayer.getCell(i, j).getTile();
						// place blocks
						if (tile.getProperties().containsKey("height")) {
							levelArray[i][j][layerNumber].setHeight(getHeight(tile));
						}
						// place ramps
						if (tile.getProperties().containsKey("ramp")) {
							levelArray[i][j][layerNumber].setRampDirection(getIntRampDirection(tile));
						}
					}
				}
			}
		}
	}
	
	public Array<ModelInstance> generateLevelMesh() {
		if (isSkySphereActive) {
			skySphere = modelBuilder.createSphere(tileLayerWidth*2, tileLayerHeight*2, tileLayerWidth*2, 20, 20, new Material(ColorAttribute.createDiffuse(Color.BLUE)), Usage.Position | Usage.Normal);
			skySphere.materials.get(0).set(new IntAttribute(IntAttribute.CullFace, 0));
			instance = new ModelInstance(skySphere);
			instance.transform.setToTranslation(tileLayerHeight/2, 0, tileLayerWidth/2);
			instances.add(instance);
		}
		//addPlane();
		//modelBuilder.begin();

		MapTile tile1 = null;
		MapTile tile2 = null;
		//TiledMapTile currentTile = null;
		
		for(int k = 0; k < tileLayerCount; k++){

			if(k == 0){
				heightOffset = 0;
			}else if(k == 1){
				heightOffset = 6;
			}
			//modelBuilder.begin();

			// on each cell
			for (int i = 0; i < tileLayerWidth; i++) {
				for (int j = 0; j < tileLayerHeight; j++) {

					if (levelArray[i][j][0] != null) {
						tile1 = levelArray[i][j][0];
					}
					if (tileLayerCount > 1) {
						if (levelArray[i][j][1] != null) {
							tile2 = levelArray[i][j][1];
						}
					}

					MapTile currentTile = null;
					if (levelArray[i][j][k] != null) {
						currentTile = levelArray[i][j][k];
					}

					// make lvl2 ceilings
					if (tile2 != null && tile2.getHeight() != -1
							&& tile1 != null && tile1.getHeight() != 5) {
						modelBuilder.begin();
						Node node = modelBuilder.node();
						node.translation.set(j,5,i);
						meshPartBuilder = modelBuilder.part("floor" + i + "_" + j, 
								GL20.GL_TRIANGLES,
								Usage.Position | Usage.Normal | Usage.TextureCoordinates, 
								MapMaterials.get(currentTile.getWallTextureId()));

						meshPartBuilder.rect(0,0,0, 1,0,0, 1,0,1, 0,0,1, 0,-1,0);
						model = modelBuilder.end();
						instance = new ModelInstance(model);
						instances.add(instance);
						addFloorObject().setWorldTransform(new Matrix4().idt().translate(node.translation).translate(.5f, 0, .5f));
					}

					// make the floor tiles
					if (currentTile.getHeight() != -1) {
						// if ramp
						if (currentTile.getRampDirection() != -1) {
							modelBuilder.begin();
							Node node = modelBuilder.node();
							node.translation.set(j, currentTile.getHeight()+heightOffset, i);
							meshPartBuilder = modelBuilder.part("floor" + i + "_" + j, 
									GL20.GL_TRIANGLES, 
									Usage.Position | Usage.Normal | Usage.TextureCoordinates,
									MapMaterials.get(currentTile.getTextureId()));

							if (currentTile.getRampDirection() == UP)	{ // -x direction
								meshPartBuilder.rect(0,0,1, 1,1,1, 1,1,0, 0,0,0, -ROOT_PT5,ROOT_PT5,0);
								addCustomShapeObject(bulletUnitRampShapeUp).setWorldTransform(new Matrix4().idt().translate(node.translation));
							}	
							else if (currentTile.getRampDirection() == DOWN) { // +x direction
								meshPartBuilder.rect(0,1,1, 1,0,1, 1,0,0, 0,1,0, ROOT_PT5,ROOT_PT5,0);
								addCustomShapeObject(bulletUnitRampShapeDown).setWorldTransform(new Matrix4().idt().translate(node.translation));
							}	
							else if (currentTile.getRampDirection() == LEFT) { // +z direction
								meshPartBuilder.rect(0,0,1, 1,0,1, 1,1,0, 0,1,0, 0,ROOT_PT5,ROOT_PT5);
								addCustomShapeObject(bulletUnitRampShapeLeft).setWorldTransform(new Matrix4().idt().translate(node.translation));
							}	
							else if (currentTile.getRampDirection() == RIGHT)	{ // -z direction
								meshPartBuilder.rect(0,1,1, 1,1,1, 1,0,0, 0,0,0, 0,ROOT_PT5,-ROOT_PT5);
								addCustomShapeObject(bulletUnitRampShapeRight).setWorldTransform(new Matrix4().idt().translate(node.translation));
							}	
							else {
								System.err.println("generateLevel(): Direction not recognized");
							}
							model = modelBuilder.end();
							instance = new ModelInstance(model);
							instances.add(instance);
							

						}
						else if (k == 0 && currentTile.getHeight() == 5 && tile2.getHeight() != -1) {
							// don't make this polygon because it is hidden by level2
						}
						// else not a ramp
						else {
							modelBuilder.begin();
							Node node = modelBuilder.node();
							node.translation.set(j, currentTile.getHeight()+heightOffset, i);
							meshPartBuilder = modelBuilder.part("floor" + i + "_" + j, 
									GL20.GL_TRIANGLES,
									Usage.Position | Usage.Normal | Usage.TextureCoordinates, 
									MapMaterials.get(currentTile.getTextureId()));

							meshPartBuilder.rect(0,0,1, 1,0,1, 1,0,0, 0,0,0, 0,1,0);
							model = modelBuilder.end();
							instance = new ModelInstance(model);
							instances.add(instance);
							//addBoxObject().setWorldTransform(new Matrix4().idt().translate(node.translation));
							addFloorObject().setWorldTransform(new Matrix4().idt().translate(node.translation).translate(.5f, 0, .5f));
						}
					}
					//				Node node = modelBuilder.node();
					//				node.translation.set(0,0,0);

					// make any north-facing walls (look south)
					makeWalls(i,j,k,NORTH);

					// make any south-facing walls (look north)
					makeWalls(i,j,k,SOUTH);

					// make any east-facing walls
					makeWalls(i,j,k,EAST);

					// make any west-facing walls
					makeWalls(i,j,k,WEST);
				}
			}
			//		model = modelBuilder.end();
			//		instance = new ModelInstance(model);
			//		instances.add(instance);

		}
		
		heightOffset = 0;
		
		if (tiledMap != null){
			if (tiledMap.getLayers().get("objects") != null){
				initializeObjectInstances();
			}
			else {
				System.err.println("TileMap - No object layer in current map");
			}
		
		
			currentLayer = (TiledMapTileLayer) tiledMap.getLayers().get("Tile Layer 1");
			//currentLayerNumber = 0;
		}
		
		return instances;
	}
	public static Mesh mergeMeshes(AbstractList<Mesh> meshes, AbstractList<Matrix4> transformations)
	{
	    if(meshes.size() == 0) return null;

	    int vertexArrayTotalSize = 0;
	    int indexArrayTotalSize = 0;

	    VertexAttributes va = meshes.get(0).getVertexAttributes();
	    int vaA[] = new int [va.size()];
	    for(int i=0; i<va.size(); i++)
	    {
	        vaA[i] = va.get(i).usage;
	    }

	    for(int i=0; i<meshes.size(); i++)
	    {
	        Mesh mesh = meshes.get(i);
	        if(mesh.getVertexAttributes().size() != va.size()) 
	        {
	            meshes.set(i, copyMesh(mesh, true, false, vaA));
	        }

	        vertexArrayTotalSize += mesh.getNumVertices() * mesh.getVertexSize() / 4;
	        indexArrayTotalSize += mesh.getNumIndices();
	    }

	    final float vertices[] = new float[vertexArrayTotalSize];
	    final short indices[] = new short[indexArrayTotalSize];

	    int indexOffset = 0;
	    int vertexOffset = 0;
	    int vertexSizeOffset = 0;
	    int vertexSize = 0;

	    for(int i=0; i<meshes.size(); i++)
	    {
	        Mesh mesh = meshes.get(i);

	        int numIndices = mesh.getNumIndices();
	        int numVertices = mesh.getNumVertices();
	        vertexSize = mesh.getVertexSize() / 4;
	        int baseSize = numVertices * vertexSize;
	        VertexAttribute posAttr = mesh.getVertexAttribute(Usage.Position);
	        int offset = posAttr.offset / 4;
	        int numComponents = posAttr.numComponents;

	        { //uzupelnianie tablicy indeksow
	            mesh.getIndices(indices, indexOffset);
	            for(int c = indexOffset; c < (indexOffset + numIndices); c++)
	            {
	                indices[c] += vertexOffset;
	            }
	            indexOffset += numIndices;
	        }

	        mesh.getVertices(0, baseSize, vertices, vertexSizeOffset);
	        Mesh.transform(transformations.get(i), vertices, vertexSize, offset, numComponents, vertexOffset, numVertices);
	        vertexOffset += numVertices;
	        vertexSizeOffset += baseSize;
	    }

	    Mesh result = new Mesh(true, vertexOffset, indices.length, meshes.get(0).getVertexAttributes());
	    result.setVertices(vertices);
	    result.setIndices(indices);
	    return result;
	} 

	    public static Mesh copyMesh(Mesh meshToCopy, boolean isStatic, boolean removeDuplicates, final int[] usage) {
	    // TODO move this to a copy constructor?
	    // TODO duplicate the buffers without double copying the data if possible.
	    // TODO perhaps move this code to JNI if it turns out being too slow.
	    final int vertexSize = meshToCopy.getVertexSize() / 4;
	    int numVertices = meshToCopy.getNumVertices();
	    float[] vertices = new float[numVertices * vertexSize];
	    meshToCopy.getVertices(0, vertices.length, vertices);
	    short[] checks = null;
	    VertexAttribute[] attrs = null;
	    int newVertexSize = 0;
	    if (usage != null) {
	        int size = 0;
	        int as = 0;
	        for (int i = 0; i < usage.length; i++)
	            if (meshToCopy.getVertexAttribute(usage[i]) != null) {
	                size += meshToCopy.getVertexAttribute(usage[i]).numComponents;
	                as++;
	            }
	        if (size > 0) {
	            attrs = new VertexAttribute[as];
	            checks = new short[size];
	            int idx = -1;
	            int ai = -1;
	            for (int i = 0; i < usage.length; i++) {
	                VertexAttribute a = meshToCopy.getVertexAttribute(usage[i]);
	                if (a == null)
	                    continue;
	                for (int j = 0; j < a.numComponents; j++)
	                    checks[++idx] = (short)(a.offset/4 + j);
	                attrs[++ai] = new VertexAttribute(a.usage, a.numComponents, a.alias);
	                newVertexSize += a.numComponents;
	            }
	        }
	    }
	    if (checks == null) {
	        checks = new short[vertexSize];
	        for (short i = 0; i < vertexSize; i++)
	            checks[i] = i;
	        newVertexSize = vertexSize;
	    }

	    int numIndices = meshToCopy.getNumIndices();
	    short[] indices = null; 
	    if (numIndices > 0) {
	        indices = new short[numIndices];
	        meshToCopy.getIndices(indices);
	        if (removeDuplicates || newVertexSize != vertexSize) {
	            float[] tmp = new float[vertices.length];
	            int size = 0;
	            for (int i = 0; i < numIndices; i++) {
	                final int idx1 = indices[i] * vertexSize;
	                short newIndex = -1;
	                if (removeDuplicates) {
	                    for (short j = 0; j < size && newIndex < 0; j++) {
	                        final int idx2 = j*newVertexSize;
	                        boolean found = true;
	                        for (int k = 0; k < checks.length && found; k++) {
	                            if (tmp[idx2+k] != vertices[idx1+checks[k]])
	                                found = false;
	                        }
	                        if (found)
	                            newIndex = j;
	                    }
	                }
	                if (newIndex > 0)
	                    indices[i] = newIndex;
	                else {
	                    final int idx = size * newVertexSize;
	                    for (int j = 0; j < checks.length; j++)
	                        tmp[idx+j] = vertices[idx1+checks[j]];
	                    indices[i] = (short)size;
	                    size++;
	                }
	            }
	            vertices = tmp;
	            numVertices = size;
	        }
	    }

	    Mesh result;
	    if (attrs == null)
	        result = new Mesh(isStatic, numVertices, indices == null ? 0 : indices.length, meshToCopy.getVertexAttributes());
	    else
	        result = new Mesh(isStatic, numVertices, indices == null ? 0 : indices.length, attrs);
	    result.setVertices(vertices, 0, numVertices * newVertexSize);
	    result.setIndices(indices);
	    return result;
	}
	
	private void makeWalls(int i, int j, int k, int direction) {
		MapTile tile = levelArray[i][j][k];
		int looki = 0;
		int lookj = 0;
		// direction: which direction the wall is facing
		switch(direction) {
			case NORTH:	// look south to check for north-facing wall
				looki = i+1;
				lookj = j;
				break;
			case SOUTH:	// look north to check for south-facing wall
				looki = i-1;
				lookj = j;
				break;
			case EAST:	// look west to check for east-facing wall
				looki = i;
				lookj = j-1;
				break;
			case WEST:	// look east to check for west-facing wall
				looki = i;
				lookj = j+1;
				break;
			default:
				System.err.println("makeWalls: Direction not recognized");
		}
		
		MapTile adj = null;
		if (looki >= 0 && looki < tileLayerWidth && lookj >= 0 && lookj < tileLayerHeight) {
			adj = levelArray[looki][lookj][k];
		}
		
		// case where current tile and adjacent tile are not ramps
		if(adj!=null &&
				adj.getHeight() != -1 &&
				adj.getRampDirection() == -1 &&
				tile.getRampDirection() == -1 &&
				adj.getHeight() > tile.getHeight()){
			int bottom = tile.getHeight();
			if( k == 1	// layer 2
					&& levelArray[i][j][0].getRampDirection() != -1
					&& levelArray[i][j][0].getHeight() == 5){
				bottom++;
			}
			if (!combinedWalls) {
				for(float b1 = bottom; b1 < adj.getHeight(); b1++){
					genWall(i, j, b1, direction);
				}
			} else {
				genCombinedWall(i, j, bottom, adj.getHeight(), direction);
			}
		}
		// case where current tile is a ramp, but the adjacent tile is not
		if(adj!=null &&
				adj.getHeight() != -1 &&
				tile.getRampDirection() != -1 &&
				adj.getRampDirection() == -1 &&
				adj.getHeight() > tile.getHeight()){
			if (!combinedWalls) {
				for (float b1 = tile.getHeight()+1; b1 < adj.getHeight(); b1++) {
					genWall(i, j, b1, direction);
				}
			} else {
				genCombinedWall(i, j, tile.getHeight()+1, adj.getHeight(), direction);
			}
			// generate the walls at the bottom of ramp slopes (if any)
			if (tile.getRampDirection() == direction) {
				genWall(i, j, tile.getHeight(), direction);
			}
			
			
			// generates the triangle segments and puts them in the right spot
			makeTriangleSegments(tile.getRampDirection(), tile.getHeight(),
					adj.getRampDirection(), adj.getHeight(), direction, j, i);
			
		}
		// case where the current tile is not a ramp, but the adjacent tile is
		if(adj!=null &&
				adj.getHeight() != -1 &&
				adj.getRampDirection() != -1 &&
				tile.getRampDirection() == -1 && 
				adj.getHeight() >= tile.getHeight()){
			int bottom = tile.getHeight();
			if( k == 1	// layer 2
					&& levelArray[i][j][0].getRampDirection() != -1
					&& levelArray[i][j][0].getHeight() == 5){
				bottom++;
			}
			if (!combinedWalls) {
				for (int b1 = bottom; b1 < adj.getHeight(); b1++) {
					genWall(i, j, b1, direction);
				}
			} else {
				genCombinedWall(i, j, bottom, adj.getHeight(), direction);
			}

			// generates the triangle segments and puts them in the right spot
			makeTriangleSegments(tile.getRampDirection(), tile.getHeight(),
					adj.getRampDirection(), adj.getHeight(), direction, j, i);
			
		}
		// case where both the current tile and the adjacent tile are ramps
		if(adj!=null &&
				adj.getHeight() != -1 &&
				adj.getRampDirection() != -1 &&
				tile.getRampDirection() != -1 &&
				adj.getHeight() > tile.getHeight()){
			if (!combinedWalls) {
				for (float b1 = tile.getHeight()+1; b1 < adj.getHeight(); b1++) {
					genWall(i, j, b1, direction);
				}
			} else {
				genCombinedWall(i, j, tile.getHeight()+1, adj.getHeight(), direction);
			}

			// generates the triangle segments and puts them in the right spot
			makeTriangleSegments(tile.getRampDirection(), tile.getHeight(),
					adj.getRampDirection(), adj.getHeight(), direction, j, i);
			
		}
	}
	
	private void makeTriangleSegments(int currRampDir, int currHeight, int adjRampDir, int adjHeight, 
									  int wallFaceDir, int j, int i){
		// case where the adjacent tile is a ramp
		if(adjRampDir > 0){
			switch(wallFaceDir){
			case(NORTH):
				if (adjRampDir == LEFT) {
					// need to make a rectangle
					genWall(i, j, adjHeight, wallFaceDir);
				}
				if (adjRampDir == UP) {
					// need to make a triangle
					p1.set(0,0,1);
					p2.set(1,1,1);
					p3.set(1,0,1);
					normal.set(0,0,-1);
					v1.setPos(p1).setNor(normal).setUV(1.0f, 1.0f);
					v2.setPos(p2).setNor(normal).setUV(0.0f, 0.0f);
					v3.setPos(p3).setNor(normal).setUV(0.0f, 1.0f);
					genTriangle(v1, v2, v3, j, adjHeight, i);
				}
				if (adjRampDir == DOWN) {
					// need to make a triangle
					p1.set(0,1,1);
					p2.set(1,0,1);
					p3.set(0,0,1);
					normal.set(0,0,-1);
					v1.setPos(p1).setNor(normal).setUV(1.0f, 0.0f);
					v2.setPos(p2).setNor(normal).setUV(0.0f, 1.0f);
					v3.setPos(p3).setNor(normal).setUV(1.0f, 1.0f);
					genTriangle(v1, v2, v3, j, adjHeight, i);
				}
				break;
			case(SOUTH):
				if (adjRampDir == RIGHT) {
					// need to make a rectangle
					genWall(i, j, adjHeight, wallFaceDir);
				}
				if (adjRampDir == UP) {
					// need to make a triangle
					p1.set(1,1,0);
					p2.set(0,0,0);
					p3.set(1,0,0);
					normal.set(0,0,1);
					v1.setPos(p1).setNor(normal).setUV(1.0f, 0.0f);
					v2.setPos(p2).setNor(normal).setUV(0.0f, 1.0f);
					v3.setPos(p3).setNor(normal).setUV(1.0f, 1.0f);
					genTriangle(v1, v2, v3, j, adjHeight, i);
				}
				if (adjRampDir == DOWN) {
					// need to make a triangle
					p1.set(1,0,0);
					p2.set(0,1,0);
					p3.set(0,0,0);
					normal.set(0,0,1);
					v1.setPos(p1).setNor(normal).setUV(1.0f, 1.0f);
					v2.setPos(p2).setNor(normal).setUV(0.0f, 0.0f);
					v3.setPos(p3).setNor(normal).setUV(0.0f, 1.0f);
					genTriangle(v1, v2, v3, j, adjHeight, i);
				}
				break;
			case(EAST):
				if (adjRampDir == UP) {
					// need to make a rectangle
					genWall(i, j, adjHeight, wallFaceDir);
				}
				if (adjRampDir == LEFT) {
					// need to make a triangle
					p1.set(0,1,0);
					p2.set(0,0,1);
					p3.set(0,0,0);
					normal.set(1,0,0);
					v1.setPos(p1).setNor(normal).setUV(1.0f, 0.0f);
					v2.setPos(p2).setNor(normal).setUV(0.0f, 1.0f);
					v3.setPos(p3).setNor(normal).setUV(1.0f, 1.0f);
					genTriangle(v1, v2, v3, j, adjHeight, i);
				}
				if (adjRampDir == RIGHT) {
					// need to make a triangle
					p1.set(0,0,0);
					p2.set(0,1,1);
					p3.set(0,0,1);
					normal.set(1,0,0);
					v1.setPos(p1).setNor(normal).setUV(1.0f, 1.0f);
					v2.setPos(p2).setNor(normal).setUV(0.0f, 0.0f);
					v3.setPos(p3).setNor(normal).setUV(0.0f, 1.0f);
					genTriangle(v1, v2, v3, j, adjHeight, i);
				}
				break;
			case(WEST):
				if (adjRampDir == DOWN) {
					// need to make a rectangle
					genWall(i, j, adjHeight, wallFaceDir);
				}
				if (adjRampDir == LEFT) {
					// need to make a triangle
					p1.set(1,0,1);
					p2.set(1,1,0);
					p3.set(1,0,0);
					normal.set(-1,0,0);
					v1.setPos(p1).setNor(normal).setUV(1.0f, 1.0f);
					v2.setPos(p2).setNor(normal).setUV(0.0f, 0.0f);
					v3.setPos(p3).setNor(normal).setUV(0.0f, 1.0f);
					genTriangle(v1, v2, v3, j, adjHeight, i);
				}
				if (adjRampDir == RIGHT) {
					// need to make a triangle
					p1.set(1,1,1);
					p2.set(1,0,0);
					p3.set(1,0,1);
					normal.set(-1,0,0);
					v1.setPos(p1).setNor(normal).setUV(1.0f, 0.0f);
					v2.setPos(p2).setNor(normal).setUV(0.0f, 1.0f);
					v3.setPos(p3).setNor(normal).setUV(1.0f, 1.0f);
					genTriangle(v1, v2, v3, j, adjHeight, i);
				}
				break;
			default:
				break;
			}
		}
		// case where the current tile is a ramp
		if (currRampDir > 0) {
			switch(currRampDir){
			case(UP):
				//
				if (wallFaceDir == NORTH) {
					p1.set(0,0,1);
					p2.set(0,1,1);
					p3.set(1,1,1);
					normal.set(0,0,-1);
					v1.setPos(p1).setNor(normal).setUV(1.0f, 1.0f);
					v2.setPos(p2).setNor(normal).setUV(1.0f, 0.0f);
					v3.setPos(p3).setNor(normal).setUV(0.0f, 0.0f);
					genTriangle(v1, v2, v3, j, currHeight, i);
				}
				if (wallFaceDir == SOUTH) {
					p1.set(1,1,0);
					p2.set(0,1,0);
					p3.set(0,0,0);
					normal.set(0,0,1);
					v1.setPos(p1).setNor(normal).setUV(1.0f, 0.0f);
					v2.setPos(p2).setNor(normal).setUV(0.0f, 0.0f);
					v3.setPos(p3).setNor(normal).setUV(0.0f, 1.0f);
					genTriangle(v1, v2, v3, j, currHeight, i);
				}
			break;
			case(DOWN):
				//
				if (wallFaceDir == NORTH) {
					p1.set(0,1,1);
					p2.set(1,1,1);
					p3.set(1,0,1);
					normal.set(0,0,-1);
					v1.setPos(p1).setNor(normal).setUV(1.0f, 0.0f);
					v2.setPos(p2).setNor(normal).setUV(0.0f, 0.0f);
					v3.setPos(p3).setNor(normal).setUV(0.0f, 1.0f);
					genTriangle(v1, v2, v3, j, currHeight, i);
				}
				if (wallFaceDir == SOUTH) {
					p1.set(1,0,0);
					p2.set(1,1,0);
					p3.set(0,1,0);
					normal.set(0,0,1);
					v1.setPos(p1).setNor(normal).setUV(1.0f, 1.0f);
					v2.setPos(p2).setNor(normal).setUV(1.0f, 0.0f);
					v3.setPos(p3).setNor(normal).setUV(0.0f, 0.0f);
					genTriangle(v1, v2, v3, j, currHeight, i);
				}
			break;
			case(LEFT):
				//
				if (wallFaceDir == WEST) {
					p1.set(1,0,1);
					p2.set(1,1,1);
					p3.set(1,1,0);
					normal.set(-1,0,0);
					v1.setPos(p1).setNor(normal).setUV(1.0f, 1.0f);
					v2.setPos(p2).setNor(normal).setUV(1.0f, 0.0f);
					v3.setPos(p3).setNor(normal).setUV(0.0f, 0.0f);
					genTriangle(v1, v2, v3, j, currHeight, i);
				}
				if (wallFaceDir == EAST) {
					p1.set(0,1,0);
					p2.set(0,1,1);
					p3.set(0,0,1);
					normal.set(1,0,0);
					v1.setPos(p1).setNor(normal).setUV(1.0f, 0.0f);
					v2.setPos(p2).setNor(normal).setUV(0.0f, 0.0f);
					v3.setPos(p3).setNor(normal).setUV(0.0f, 1.0f);
					genTriangle(v1, v2, v3, j, currHeight, i);
				}
			break;
			case(RIGHT):
				//
				if (wallFaceDir == WEST) {
					p1.set(1,1,1);
					p2.set(1,1,0);
					p3.set(1,0,0);
					normal.set(-1,0,0);
					v1.setPos(p1).setNor(normal).setUV(1.0f, 0.0f);
					v2.setPos(p2).setNor(normal).setUV(0.0f, 0.0f);
					v3.setPos(p3).setNor(normal).setUV(0.0f, 1.0f);
					genTriangle(v1, v2, v3, j, currHeight, i);
				}
				if (wallFaceDir == EAST) {
					p1.set(0,0,0);
					p2.set(0,1,0);
					p3.set(0,1,1);
					normal.set(1,0,0);
					v1.setPos(p1).setNor(normal).setUV(1.0f, 1.0f);
					v2.setPos(p2).setNor(normal).setUV(1.0f, 0.0f);
					v3.setPos(p3).setNor(normal).setUV(0.0f, 0.0f);
					genTriangle(v1, v2, v3, j, currHeight, i);
				}
			break;
			default:
				System.err.println("makeWalls(): Ramp direction not recognized");
				break;
			}
		}
	}
	
	// TODO: genTriangle
	// Generates a triangle given the VertexInfo (these include normals, and UV texture coordinates)
	private void genTriangle(VertexInfo v1, VertexInfo v2, VertexInfo v3, int offset1, int offset2, int offset3){
		modelBuilder.begin();
		Node node = modelBuilder.node();
		node.translation.set(offset1, offset2+heightOffset, offset3);
		meshPartBuilder = modelBuilder.part("triangle" + triCount++, 
				GL20.GL_TRIANGLES, 

				Usage.Position
				| Usage.Normal
				| Usage.TextureCoordinates
				, MapMaterials.get(levelArray[offset3][offset1][0].getWallTextureId())
				);

		meshPartBuilder.triangle(v1, v2, v3);
		model = modelBuilder.end();
		instance = new ModelInstance(model);
		instances.add(instance);
		
		btConvexHullShape chshape=new btConvexHullShape();
		chshape.addPoint(v1.position);
		chshape.addPoint(v2.position);
		chshape.addPoint(v3.position);

		addCustomShapeObject(chshape).setWorldTransform(new Matrix4().idt().translate(node.translation));
	}
	
	// TODO: genWall
	// Generates a wall segment
	private void genWall(float cellj, float celli, float bottom, int direction){
		String dirString;
		
		switch(direction) {
		case NORTH:
			dirString = "North";
			
			p1.set(1, 0, 1);
			p2.set(0, 0, 1);
			p3.set(0, 1, 1);
			p4.set(1, 1, 1);
			normal.set(0f,0f,-1f);
			
			break;
		case SOUTH:
			dirString = "South";
			
			p1.set(0, 0, 0);
			p2.set(1, 0, 0);
			p3.set(1, 1, 0);
			p4.set(0, 1, 0);
			normal.set(0f,0f,1f);
			
			break;
		case EAST:
			dirString = "East";
		
			p1.set(0, 0, 1);
			p2.set(0, 0, 0);
			p3.set(0, 1, 0);
			p4.set(0, 1, 1);
			normal.set(1f,0f,0f);
			
			break;
		case WEST:
			dirString = "West";
		
			p1.set(1, 0, 0);
			p2.set(1, 0, 1);
			p3.set(1, 1, 1);
			p4.set(1, 1, 0);
			normal.set(-1f,0f,0f);
			
			break;
		default:
			dirString = "Error";
			System.err.println("Error: direction not recognized");
		}
		
		modelBuilder.begin();
		Node node = modelBuilder.node();
		node.translation.set(celli,bottom+heightOffset,cellj);
		meshPartBuilder = modelBuilder.part(dirString + "_wall" + celli + "_" + cellj, 
				GL20.GL_TRIANGLES, 
				Usage.Position | Usage.Normal | Usage.TextureCoordinates, 
				MapMaterials.get(levelArray[(int)cellj][(int)celli][0].getWallTextureId()));

		meshPartBuilder.rect(p1, p2, p3, p4, normal);
		model = modelBuilder.end();
		instance = new ModelInstance(model);
		instances.add(instance);
		btConvexHullShape chshape=new btConvexHullShape();
		chshape.addPoint(v1.position);
		chshape.addPoint(v2.position);
		chshape.addPoint(v3.position);
		chshape.addPoint(v4.position);

		addCustomShapeObject(chshape).setWorldTransform(new Matrix4().idt().translate(node.translation));
	}
	
	private void genCombinedWall(float cellj, float celli, float bottom, float top, int direction){
		String dirString;
		float height = top - bottom;
		
		switch(direction) {
			case NORTH:
				dirString = "North";
				
				p1.set(1, 0, 1);
				p2.set(0, 0, 1);
				p3.set(0, height, 1);
				p4.set(1, height, 1);
				normal.set(0f,0f,-1f);
				
				break;
			case SOUTH:
				dirString = "South";
				
				p1.set(0, 0, 0);
				p2.set(1, 0, 0);
				p3.set(1, height, 0);
				p4.set(0, height, 0);
				normal.set(0f,0f,1f);
				
				break;
			case EAST:
				dirString = "East";
			
				p1.set(0, 0, 1);
				p2.set(0, 0, 0);
				p3.set(0, height, 0);
				p4.set(0, height, 1);
				normal.set(1f,0f,0f);
				
				break;
			case WEST:
				dirString = "West";
			
				p1.set(1, 0, 0);
				p2.set(1, 0, 1);
				p3.set(1, height, 1);
				p4.set(1, height, 0);
				normal.set(-1f,0f,0f);
				
				break;
			default:
				dirString = "Error";
				System.err.println("Error: direction not recognized");
		}

		v1.setPos(p1).setNor(normal).setUV(0.0f, height);
		v2.setPos(p2).setNor(null).setUV(1.0f, height);
		v3.setPos(p3).setNor(null).setUV(1.0f, 0.0f);
		v4.setPos(p4).setNor(null).setUV(0.0f, 0.0f);
		
		modelBuilder.begin();
		Node node = modelBuilder.node();
		node.translation.set(celli,bottom+heightOffset,cellj);
		meshPartBuilder = modelBuilder.part(dirString + "_wall" + celli + "_" + cellj, 
				GL20.GL_TRIANGLES, 
				Usage.Position | Usage.Normal | Usage.TextureCoordinates, 
				MapMaterials.get(levelArray[(int)cellj][(int)celli][0].getWallTextureId()));

		meshPartBuilder.rect(v1, v2, v3, v4);
		model = modelBuilder.end();
		instance = new ModelInstance(model);
		instances.add(instance);
		btConvexHullShape chshape=new btConvexHullShape();
		chshape.addPoint(v1.position);
		chshape.addPoint(v2.position);
		chshape.addPoint(v3.position);
		chshape.addPoint(v4.position);

		addCustomShapeObject(chshape).setWorldTransform(new Matrix4().idt().translate(node.translation));
	}

	public ArrayList<int[]> getEnemyWayPoints(){

		MapObjects objects = tiledMap.getLayers().get("objects").getObjects();
		int[] posArray = new int[2];
		ArrayList<int[]> wpPos = new ArrayList<int[]>();
		//int wpCount = 0;
		for (MapObject obj : objects){
			if (obj.getName().equalsIgnoreCase("Spawn")){
				posArray[0] = Integer.parseInt(obj.getProperties().get("X").toString());
				posArray[1] = Integer.parseInt(obj.getProperties().get("Y").toString());
				wpPos.add(posArray);
				//wpCount++;
			}
		}
		return wpPos;
	}

	public void generatePatrolPath(){
		//ArrayList<DistanceFromPlayer> patrolPath = new ArrayList<DistanceFromPlayer>();
		//TiledMapTileLayer patrolLayer = (TiledMapTileLayer) tiledMap.getLayers().get(6);//map from android assets
		DistanceFromPlayer newTile;
		TiledMapTileLayer patrolLayer;
		try {
			patrolLayer = (TiledMapTileLayer) tiledMap.getLayers().get("Patrol Path Tile Layer 1");
		} catch (Exception ex) {
			return;
		}
		int tileNum = 0;
		for (int tileX = 0; tileX < tileLayerWidth; tileX++)
			for (int tileY = 0; tileY < tileLayerWidth; tileY++) {
				tileNum = (tileX * tileLayerWidth) + tileY;
				if (patrolLayer.getCell(tileX, tileY) != null){
					newTile = new DistanceFromPlayer(tileNum, 1, tileLayerWidth);
					for (DistanceFromPlayer partOfPath : patrolPath){
						partOfPath.addSpotToMoveIndex(tileNum);//checks to see if tileNum is adjacent to prev. added tiles
						newTile.addSpotToMoveIndex(partOfPath.getTileNumber());
					}
					patrolPath.add(newTile);
				}

			}
	}
	
	//Objects are read from the "objects" layer in the tile map
	private void initializeObjectInstances() {
		Vector3 objPosition;
		MapObjects objects = tiledMap.getLayers().get("objects").getObjects();
		Assets.loadModels();

		for (int i = 0; i < objects.getCount(); i++) {
			RectangleMapObject rectObj = (RectangleMapObject) objects.get(i);
			//decal, color, intensity, pointLight, id, isActive, isRenderable
			if (rectObj.getName().contains("Torch")) {
				objPosition = new Vector3();
				int height = getObjectHeight(rectObj);
				objPosition.set(rectObj.getRectangle().getY() / 32, height, rectObj.getRectangle().getX() / 32);
				Torch torch = new Torch(objPosition, 'W', 1, true, true);

				if (rectObj.getProperties().containsKey("N"))
					torch.setDirection('N');
				else if (rectObj.getProperties().containsKey("S"))
					torch.setDirection('S');
				else if (rectObj.getProperties().containsKey("E")) 
					torch.setDirection('E');
				else if (rectObj.getProperties().containsKey("W"))
					torch.setDirection('W');

				torch.setDecal(Decal.newDecal(Assets.torch, true));
				torch.setColor(getLightColor(rectObj));
				torch.getDecal().setScale(0.003f);
				torch.setRotations(torch.getDirection());
				PointLight light = new PointLight();
				torch.setPointLight(light.set(getLightColor(rectObj), objPosition, 2f));
				Entity.entityInstances.add(torch);
			}

			else if (rectObj.getName().contains("Light")) {
				int height = getObjectHeight(rectObj);
				objPosition = new Vector3();
				objPosition.set(rectObj.getRectangle().getY() / 32, height, rectObj.getRectangle().getX() / 32);
				PointLight pointLight = new PointLight();
				pointLight.set(getLightColor(rectObj), objPosition, 20f);
				Light light = new Light(objPosition, 2, true, true, pointLight);
				Entity.entityInstances.add(light);
			} 

			else if (rectObj.getName().contains("Mist")) {
				int height = getObjectHeight(rectObj);
				objPosition = new Vector3();
				objPosition.set(rectObj.getRectangle().getY() / 32, height, rectObj.getRectangle().getX() / 32);
				PointLight pointLight = new PointLight();
				pointLight.set(getLightColor(rectObj), objPosition, 20f);
				Mist mist = new Mist(objPosition, 2, true, true, pointLight);
				Entity.entityInstances.add(mist);
			}

			else if (rectObj.getName().contains("Spawn")) {
				int height = getObjectHeight(rectObj);
				objPosition = new Vector3();
				objPosition.set(rectObj.getRectangle().getY() / 32, height, rectObj.getRectangle().getX() / 32);
				Assets.loadModels();
				Zombie zombie = new Zombie(9, false, true, objPosition, new Vector3(0, 0, 0), 
										   new Vector3(0.8f, 0.8f, 0.8f), new Vector3(0, 0, 0), new Vector3(0, 0, 0), 
										   new ModelInstance(Assets.manager.get("zombie_fast.g3db", Model.class)));
				EnemySpawner spawn = new EnemySpawner(objPosition, 8, true, false, false, 1f, getSpawnTime(rectObj), zombie);
				Entity.entityInstances.add(spawn);
			}
			
			else if (rectObj.getName().contains("Portal")) {
				int height = getObjectHeight(rectObj);
				objPosition = new Vector3();
				objPosition.set(rectObj.getRectangle().getY() / 32, height, rectObj.getRectangle().getX() / 32);
				PointLight pointLight = new PointLight();
				pointLight.set(getLightColor(rectObj), objPosition, 20f);
				Portal portal = new Portal(objPosition, 2, true, true, pointLight);
				Entity.entityInstances.add(portal);
			}
			
			else if (rectObj.getName().contains("Rocket")) {
				int height = getObjectHeight(rectObj);
				objPosition = new Vector3();
				objPosition.set(rectObj.getRectangle().getY() / 32, height + .5f, rectObj.getRectangle().getX() / 32);
				//Note: Here I set is renderable to false.
				// need to send the newWeapon packet to the player in order to set it to true for client
				boolean renderable = true;
				if(GameScreen.mode == GameScreen.mode.Client) {
					renderable = false;
				}
				RocketLauncherSpawn launcherSpawn = new RocketLauncherSpawn(objPosition, 8, true, renderable, Assets.manager.get("GUNFBX.g3db", Model.class), weaponSpawnTypeEnum.rocketLauncher);
				WeaponSpawner spawner = new WeaponSpawner(objPosition, 8, true, true, false, getSpawnTime(rectObj), getLightColor(rectObj), launcherSpawn, world);
				launcherSpawn.setSpawner(spawner);
				weaponInstances.add(launcherSpawn);
				Entity.entityInstances.add(spawner);
			}
			
			else if (rectObj.getName().contains("Sword")) {
				int height = getObjectHeight(rectObj);
				objPosition = new Vector3();
				objPosition.set(rectObj.getRectangle().getY() / 32, height + .5f, rectObj.getRectangle().getX() / 32);
				//Note: Here I set is renderable to false.
				// need to send the newWeapon packet to the player in order to set it to true for client
				boolean renderable = true;
				if(GameScreen.mode == GameScreen.mode.Client) {
					renderable = false;
				}
				SwordSpawn swordSpawn = new SwordSpawn(objPosition, 8, true, renderable, Assets.manager.get("sword2.g3db", Model.class), weaponSpawnTypeEnum.sword);
				WeaponSpawner spawner = new WeaponSpawner(objPosition, 8, true, true, false, getSpawnTime(rectObj), getLightColor(rectObj), swordSpawn, world);
				swordSpawn.setSpawner(spawner);
				weaponInstances.add(swordSpawn);
				Entity.entityInstances.add(spawner);
			}
			
			else if (rectObj.getName().contains("SpeedBoost")) {
				objPosition = new Vector3();
				int height = getObjectHeight(rectObj);
				float duration = getPowerUpDuration(rectObj);
				objPosition.set(rectObj.getRectangle().getY() / 32, height + .5f, rectObj.getRectangle().getX() / 32);
				//Note: Here I set is renderable to false.
				// need to send the newPowerUp packet to the player in order to set it to true for client
				boolean renderable = true;
				if(GameScreen.mode == GameScreen.mode.Client) {
					renderable = false;
				}
				SpeedPowerUp speedBoost = new SpeedPowerUp(objPosition, 9, true, renderable, Assets.manager.get("FireFlower.g3db", Model.class), duration, powerUpTypeEnum.speedBoost);
				PowerUpSpawner spawner = new PowerUpSpawner(objPosition, 9, true, true, false, getSpawnTime(rectObj), getLightColor(rectObj), speedBoost, world);
				speedBoost.setSpawner(spawner);
				powerUpInstances.add(speedBoost);
				Entity.entityInstances.add(spawner);
			}
			
			else if (rectObj.getName().contains("HealthPot")) {
				objPosition = new Vector3();
				int height = getObjectHeight(rectObj);
				float duration = getPowerUpDuration(rectObj);
				objPosition.set(rectObj.getRectangle().getY() / 32, height + .5f, rectObj.getRectangle().getX() / 32);
				//Note: Here I set is renderable to false if client, true if server.
				// need to send the newPowerUp packet to the player in order to set it to true for client
				boolean renderable = true;
				if(GameScreen.mode == GameScreen.mode.Client) {
					renderable = false;
				}
				HealthPowerUp healthPot = new HealthPowerUp(objPosition, 9, true, renderable, Assets.manager.get("FireFlower.g3db", Model.class), duration, powerUpTypeEnum.healthPot);
				PowerUpSpawner spawner = new PowerUpSpawner(objPosition, 9, true, true, false, getSpawnTime(rectObj), getLightColor(rectObj), healthPot, world);
				healthPot.setSpawner(spawner);
				powerUpInstances.add(healthPot);
				Entity.entityInstances.add(spawner);
			}

			else {
				System.err.println("initializeObjectInstances(): Object does not exist " + rectObj.getName());
			}
		}
	}
	
	private void createRocket(int xPos, int yPos, float spawnTime){
		if(!outOfBounds(xPos, yPos)){
			int height = levelArray[xPos][yPos][currentLayerNumber].getHeight();
			Vector3 objPosition = new Vector3(yPos+0.5f, height+0.5f, xPos+0.5f);
			boolean renderable = false;
			if(GameScreen.mode == GameScreen.mode.Server) {
				renderable = true;
			}
			RocketLauncherSpawn launcherSpawn = new RocketLauncherSpawn(objPosition, 8, true, renderable, Assets.manager.get("GUNFBX.g3db", Model.class), weaponSpawnTypeEnum.rocketLauncher);
			WeaponSpawner spawner = new WeaponSpawner(objPosition, 8, true, true, false, spawnTime, Color.CYAN, launcherSpawn, world);
			launcherSpawn.setSpawner(spawner);
			weaponInstances.add(launcherSpawn);
			Entity.entityInstances.add(spawner);
		}
	}
	
	private void createSword(int xPos, int yPos, float spawnTime){
		if(!outOfBounds(xPos, yPos)){
			int height = levelArray[xPos][yPos][currentLayerNumber].getHeight();
			Vector3 objPosition = new Vector3(yPos+0.5f, height+0.5f, xPos+0.5f);
			boolean renderable = false;
			if(GameScreen.mode == GameScreen.mode.Server) {
				renderable = true;
			}
			SwordSpawn swordSpawn = new SwordSpawn(objPosition, 8, true, renderable, Assets.manager.get("sword2.g3db", Model.class), weaponSpawnTypeEnum.sword);
			WeaponSpawner spawner = new WeaponSpawner(objPosition, 8, true, true, false, spawnTime, Color.CYAN, swordSpawn, world);
			swordSpawn.setSpawner(spawner);
			weaponInstances.add(swordSpawn);
			Entity.entityInstances.add(spawner);
		}
	}
	
	private void createTorch(int xPos, int yPos, int direction){
		if(!outOfBounds(xPos, yPos)){
			int height = levelArray[xPos][yPos][currentLayerNumber].getHeight()+1;
			Vector3 objPosition = new Vector3(yPos+0.5f, height, xPos+0.5f);
			Torch torch = new Torch(objPosition, 'W', 1, true, true);

			if (direction == NORTH){
				torch.setDirection('N');
				objPosition.z += 0.32f;
				torch.setPosition(objPosition);
			}
			else if (direction == SOUTH){
				objPosition.z -= 0.32f;
				torch.setDirection('S');
			}
			else if (direction == EAST){
				objPosition.x -= 0.32f;
				torch.setDirection('E');
			}
			else if (direction == WEST){
				objPosition.x += 0.32f;
				torch.setDirection('W');
			}

			torch.setDecal(Decal.newDecal(Assets.torch, true));
			Color color = new Color();
			color.set(240f/255f, 1f, 186f/255f, 1f);
			torch.setColor(color);
			torch.getDecal().setScale(0.003f);
			torch.setRotations(torch.getDirection());
			PointLight light = new PointLight();
			torch.setPointLight(light.set(torch.getColor(), objPosition, 2f));
			Entity.entityInstances.add(torch);
		}
	}
	
	private void createMist(int xPos, int yPos, Color color){
		if(!outOfBounds(xPos, yPos)){
			int height = levelArray[xPos][yPos][currentLayerNumber].getHeight();
			Vector3 objPosition = new Vector3(yPos+0.5f, height, xPos+0.5f);
			PointLight pointLight = new PointLight();
			pointLight.set(color, objPosition, 20f);
			Mist mist = new Mist(objPosition, 2, true, true, pointLight);
			Entity.entityInstances.add(mist);
		}
	}
	
	// TODO: I need to add these creators for the other types of objects
	private void createPointLight(int xPos, int yPos, Color color){
		if(!outOfBounds(xPos, yPos)){
			int height = levelArray[xPos][yPos][currentLayerNumber].getHeight() + 3;
			Vector3 objPosition = new Vector3(yPos+0.5f, height, xPos+0.5f);
			PointLight pointLight = new PointLight();
			pointLight.set(color, objPosition, 20f);
			Light light = new Light(objPosition, 2, true, true, pointLight);
			Entity.entityInstances.add(light);
		}
	}

	private void createEnemySpawner(int xPos, int yPos, float spawnTime){
		if(!outOfBounds(xPos, yPos)){
			int height = levelArray[xPos][yPos][currentLayerNumber].getHeight();
			Assets.loadModels();
			Vector3 objPosition = new Vector3(yPos+0.5f, height, xPos+0.5f);
			Zombie zombie = new Zombie(9, false, true, objPosition, new Vector3(0, 0, 0), 
					new Vector3(0.8f, 0.8f, 0.8f), new Vector3(0, 0, 0), new Vector3(0, 0, 0), 
					new ModelInstance(Assets.manager.get("zombie_fast.g3db", Model.class)));
			EnemySpawner spawn = new EnemySpawner(objPosition, 8, true, false, false, 1f, spawnTime, zombie);
			Entity.entityInstances.add(spawn);
		}
	}

	// returns the tile height at the given position
	public int getHeight(Vector3 position){
		TiledMapTile tile = getTileAt(getTileCoords(position));
		if(tile != null){
			return getHeight(tile);
		}else{
			return -1;
		}
	}
	
	// returns the height of the given tile
	public int getHeight(TiledMapTile tile) {
		String height = "-1";
		if (tile.getProperties().containsKey("height")){
			height = tile.getProperties().get("height").toString();
		}
		return Integer.parseInt(height);
	}
	
	// returns the tile object at the given position
	public TiledMapTile getTileAt(GridPoint2 position){
		if(position.x >= 0 && position.x < currentLayer.getWidth() && position.y >= 0 && position.y < currentLayer.getHeight()){
			return currentLayer.getCell(position.x, position.y).getTile();
		}
		else{
			return null;
		}
	}
	
	public float getSpawnTime(RectangleMapObject object) {
		String time = "0";
		
		if (object.getProperties().containsKey("time")) {
			time = object.getProperties().get("time").toString();
		}
		
		return Float.parseFloat(time);
	}
	
	// returns how long the powerUp lasts for
	public float getPowerUpDuration(RectangleMapObject object) {
		String duration = "0";
		
		if (object.getProperties().containsKey("duration")) {
			duration = object.getProperties().get("duration").toString();
		}
		
		return Float.parseFloat(duration);
	}
	
	// returns the height of the given map object
	public int getObjectHeight(RectangleMapObject object) {
		String height = "0";
		
		if (object.getProperties().containsKey("height")) {
			height = object.getProperties().get("height").toString();
		}
		
		return Integer.parseInt(height) + (int)heightOffset;
	}
	
	// returns the color of a light map object
	private Color getLightColor(RectangleMapObject object) {
		Color color = new Color();
		int temp;
		
		if (object.getProperties().containsKey("color")) {
			String type = object.getProperties().get("color").toString();
			temp = Integer.parseInt(type);
			
			switch(temp) {
				case(RED):
					color.set(1f, 0f, 0f, 1f);
					break;
				case(WHITE):
					color.set(240f/255f, 1f, 186f/255f, 1f);
					break;
				case(GREEN):
					color.set(0f, 1f, 68f/255f, 1f);
					break;
				case(BLUE):
					color.set(0f, 0f, 1f, 1f);
					break;
				case(PURPLE):
					color.set(127f/255f, 0f, 1f, 1f);
					break;
				default:
					color.set(0f, 0f, 0f, 1f);
					break;
			}
		}
		else {
			System.err.println("Tile color not set. Default color used");
			color.set(0f, 0f, 0f, 1f);
		}
		
		return color;
	}
	
	// returns the direction of the upward slope. Up = East, Down = West, Left = North, Right = South, NAR = not a ramp
	private String getRampDirection(TiledMapTile tile) {
		if(tile.getProperties().containsKey("ramp")){
			String direction = tile.getProperties().get("ramp").toString();
			return direction;
		} else {
			return "NAR"; // not a ramp
		}
	}
	
	// returns the direction of the upward slope
	private int getIntRampDirection(TiledMapTile tile) {
		String direction = getRampDirection(tile);
		int intDir;
		if (direction.equals("up"))	{ // -x direction
			intDir = UP;
		}	
		else if (direction.equals("down")) { // +x direction
			intDir = DOWN;
		}	
		else if(direction.equals("left")) { // +z direction
			intDir = LEFT;
		}	
		else if (direction.equals("right"))	{ // -z direction
			intDir = RIGHT;
		}
		else{
			// not a ramp
			intDir = -1;
		}
		return intDir;
	}
	
	public TiledMap getTiledMap() {
		return tiledMap;
	}
	
	public Array<ModelInstance> getInstances() {
		return instances;
	}
	
	// TODO: Check Collision
	// This returns a Vector3. A '0' in that vector represents collision on the corresponding axis,
	// and a '1' represents no collision on the corresponding axis
	// The vector returned is then multiplied component by component with the movement vector in Player.update()
	public Vector3 checkCollision(Vector3 oldPos, Vector3 newPos, float objectWidth, float objectHeight, float objectLength){
		Vector3 collisionVector = new Vector3(1,1,1);
		Vector3 movementVector = new Vector3(newPos.x - oldPos.x, newPos.y - oldPos.y, newPos.z - oldPos.z);

		//TiledMapTileLayer oldPosLayer, newPosLayer;
		int oldPosLayer = 0;
		int newPosLayer = 0;
		int oldHeightOffset, newHeightOffset;
		if(oldPos.y >= 6.5f){ 
			oldPosLayer = 1;
			oldHeightOffset = 6;
		}
		else {
			oldPosLayer = 0;
			oldHeightOffset = 0;
		}
		if(newPos.y >= 6.5f) {
			newPosLayer = 1;
			newHeightOffset = 6;
		}
		else { 
			newPosLayer = 0;
			newHeightOffset = 0;
		}

		if(movementVector.len() > 0){
			Vector3 blockSize = new Vector3(SPOT_WIDTH, 0, SPOT_LENGTH);
			Vector3 objectSize = new Vector3(objectWidth, objectHeight, objectLength);

			// returns tiled coords (as opposed to world coords)
			GridPoint2 tileCoords = getTileCoords(oldPos);

			int startX, endX, startY, endY;

			// This checks only the spaces in the direction of motion
			if(movementVector.x > 0){
				startY = tileCoords.y;
				endY = tileCoords.y + 1;
			}else if(movementVector.x < 0){
				startY = tileCoords.y - 1;
				endY = tileCoords.y;
			}else{
				startY = tileCoords.y;
				endY = tileCoords.y;
			}
			if(movementVector.z > 0){
				startX = tileCoords.x;
				endX = tileCoords.x + 1;
			}else if(movementVector.z < 0){
				startX = tileCoords.x - 1;
				endX = tileCoords.x;
			}else{
				startX = tileCoords.x;
				endX = tileCoords.x;
			}

			// iterate through certain tiles in front of the player, depending on direction
			for(int i = startX; i <= endX; i++) {

				// don't check tiles outside the map which don't exist
				if(i < 0 || i >= tileLayerWidth) {continue;}

				for(int j = startY; j <= endY; j++) {

					// don't check tiles outside the map which don't exist
					if (j<0 || j >= tileLayerHeight) { continue; }

					// TODO: Fix known bug: Player can access a ramp from the side, but should not be able to.

					// if oldPos tile is a ramp, it can lead us up one space
					if (!outOfBounds(tileCoords)) {
						if (levelArray[tileCoords.x][tileCoords.y][oldPosLayer].getRampDirection() != -1) {

							if (levelArray[i][j][newPosLayer].getHeight() + newHeightOffset > levelArray[tileCoords.x][tileCoords.y][oldPosLayer].getHeight() + oldHeightOffset + 1) {
								//check collision
								//Note: Switched i,j        here                             and                                 here (translating tiled coords to world coords)
								Vector3 tilePos = new Vector3(j * blockSize.x, levelArray[i][j][newPosLayer].getHeight() + newHeightOffset, i * blockSize.z);
								Vector3 rectCollideVec = rectCollide(oldPos, newPos, objectSize, tilePos, blockSize);

								collisionVector.set(collisionVector.x * rectCollideVec.x, collisionVector.y * rectCollideVec.y, collisionVector.z * rectCollideVec.z);
							}
						}
						else if (levelArray[i][j][newPosLayer].getHeight() + newHeightOffset > levelArray[tileCoords.x][tileCoords.y][oldPosLayer].getHeight() + oldHeightOffset) {
							//check collision
							//Note: Switched i,j        here                             and                                 here (translating tiled coords to world coords)
							Vector3 tilePos = new Vector3(j * blockSize.x, levelArray[i][j][newPosLayer].getHeight() + newHeightOffset, i * blockSize.z);
							Vector3 rectCollideVec = rectCollide(oldPos, newPos, objectSize, tilePos, blockSize);

							collisionVector.set(collisionVector.x * rectCollideVec.x, collisionVector.y * rectCollideVec.y, collisionVector.z * rectCollideVec.z);
						}
					}
				}
			}
		}

		// This prevents the player from going out of bounds of the level
		if (newPos.x - objectWidth < 0 || newPos.x + objectWidth > tileLayerHeight) {
			collisionVector.x = 0;
		}
		if (newPos.z - objectLength < 0 || newPos.z + objectLength > tileLayerWidth) {
			collisionVector.z = 0;
		}

		return new Vector3(collisionVector.x, collisionVector.y, collisionVector.z);
	}
	
	// collision of 1 rectangular object with another
	// called by checkCollision, this takes the old and new positions of the moving object (maybe a player)
	// and the position of the object to test collision with, as well as the size of both objects
	// This returns a Vector3. A '0' in that vector represents collision on the corresponding axis,
	// and a '1' represents no collision on the corresponding axis
	private Vector3 rectCollide(Vector3 oldPos, Vector3 newPos, Vector3 size1, Vector3 pos2, Vector3 size2) {
		Vector3 result = new Vector3(0,0,0);
	
		if(     newPos.x + size1.x < pos2.x ||
				newPos.x - size1.x > pos2.x + size2.x * size2.x ||
				oldPos.y - size1.y > pos2.y ||
                oldPos.z + size1.z < pos2.z ||
                oldPos.z - size1.z > pos2.z + size2.z * size2.z) {
			result.x = 1;
        }
		if(     oldPos.x + size1.x < pos2.x ||
				oldPos.x - size1.x > pos2.x + size2.x * size2.x ||
                newPos.y - size1.y > pos2.y ||
                oldPos.z + size1.z < pos2.z ||
                oldPos.z - size1.z > pos2.z + size2.z * size2.z) {
			result.y = 1;
        } 
        if(     oldPos.x + size1.x < pos2.x ||
        		oldPos.x - size1.x > pos2.x + size2.x * size2.x ||
        		oldPos.y - size1.y > pos2.y ||
                newPos.z + size1.z < pos2.z ||
                newPos.z - size1.z > pos2.z + size2.z * size2.z) {

            result.z = 1;
        }
        
        // This is useful if you want to do debugging on collision. Simply un-comment it in that case.
//        if(result.x == 0 || result.y == 0 || result.z == 0){
//        	System.out.println("Collision ("+result.x+", "+result.y+", "+result.z+")" + ", oldPos ("+oldPos.x+", "+oldPos.y+", "+oldPos.z+")"
//        			+", newPos ("+newPos.x+", "+newPos.y+", "+newPos.z+")"
//        			+", pos2 ("+pos2.x+", "+pos2.y+", "+pos2.z+")");
//        }
        
		return result;
	}
	
	public boolean outOfBounds(GridPoint2 point){
		return outOfBounds(point.x, point.y);
	}
	
	public boolean outOfBounds(int x, int y){
		if (x < 0 || x >= tileLayerWidth || y < 0 || y >= tileLayerHeight){
			return true;
		} else {
			return false;
		}
	}
	
	public float mapHeight(float x, float z, int heightLevel) {
		float height = 0;
		GridPoint2 tileCoords = getTileCoords(x, z);
		if(outOfBounds(tileCoords)){
			System.err.println("mapHeight call to out of bounds location");
			return -1f;
		}

		//TiledMapTile tile;
		MapTile tile;
		if (heightLevel == 1){
			//tile = layer1.getCell(tileCoords.x, tileCoords.y).getTile();
			tile = getMapTile(tileCoords.x, tileCoords.y, 0);
		} else {
			//tile = layer2.getCell(tileCoords.x, tileCoords.y).getTile();
			tile = getMapTile(tileCoords.x, tileCoords.y, 1);
		}

		// this is in case the player is on level 2 but there is no floor there, so level 1 is used
		//height = (float)getHeight(tile);
		height = (float)tile.getHeight();
		if (height == -1f) {
			//currentLayer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
			heightOffset = 0;
		}


		//if (tile.getProperties().containsKey("ramp")) {
		if (tile.getRampDirection() > 0) {
			//height = (float)getHeight(tile);
			int temp = 0;
			//String direction = getRampDirection(tile);
			int direction = tile.getRampDirection();

			if (direction == UP)	{ // -x direction
				temp = (int)x;
				height += x - temp;
			}
			else if (direction == DOWN) { // +x direction
				temp = (int)x;
				height += 1 - (x - temp);
			}
			else if(direction == LEFT) { // -z direction
				temp = (int)z;
				height += 1 - (z - temp);
			}
			else if (direction == RIGHT)	{ // +z direction
				temp = (int)z;
				height += z - temp;
			}
			else {
				System.err.println("MeshLevel.rampHeight() - Direction not recognized");
			}
		}
		return height;
	}

	// gets world coords of the center of the tile at world coords (x, y, z)
	public Vector2 getTileCenter(float x, float z){
		Vector2 returnVec = new Vector2(0,0);
		GridPoint2 tileCoords = getTileCoords(x, z);
		returnVec.set(tileCoords.y + 0.5f, tileCoords.x + 0.5f);
		return returnVec;
	}
	
	// input Vector3 position (world coordinates) and output tile coordinates
	private GridPoint2 getTileCoords(Vector3 position) {
		return getTileCoords(position.x, position.z);
	}
	
	// input x,z position (world coordinates) and output tile coordinates
	private GridPoint2 getTileCoords(float x, float z) {
		int tileX = (int)z;
		int tileY = (int)x;
		return new GridPoint2(tileX, tileY);
	}
	
	public int getHeightOffset(){
		return (int)heightOffset;
	}
	
	public int getHeightOffset(Vector3 position){
		if(position.y > 6){ 
			return 6;
		}
		else {
			return 0;
		}
	}
	
	public MapTile[][][] getLevelArray() {
		return levelArray;
	}

	public void setLevelArray(MapTile[][][] levelArray) {
		this.levelArray = levelArray;
	}

	public int getMapXDimension(){
		return tileLayerWidth;
	}
	
	public int getMapYDimension(){
		return tileLayerHeight;
	}

	public ArrayList<DistanceFromPlayer> getPatrolPath(){
		return patrolPath;
	}
	
	public Map<String, Integer> getMaterialIds() {
		return MaterialIds;
	}

	public void setMaterialIds(Map<String, Integer> materialIds) {
		MaterialIds = materialIds;
	}

	public Map<Integer, Material> getMapMaterials() {
		return MapMaterials;
	}

	public void setMapMaterials(Map<Integer, Material> mapMaterials) {
		MapMaterials = mapMaterials;
	}

	public MapTile getMapTile(int x, int y, int z){
		try {
			return levelArray[x][y][z];
		}catch (Exception e) {
			return levelArray[x][y][0];
		}
	}

	public Array<PowerUp> getPowerUpInstances() {
		return powerUpInstances;
	}
	
	public Array<WeaponSpawn> getWeaponInstances() {
		return weaponInstances;
	}
}