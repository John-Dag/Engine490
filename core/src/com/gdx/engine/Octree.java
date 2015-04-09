package com.gdx.engine;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;

public class Octree {
	
	private Octree[] children;
	private BoundingBox octBox;
	private Octree parent;
	private int nodeId;
	private Array<Integer> modelIndices;
	//private Array<ModelInstance> modelInstances;
	private World world;
	private Vector3 xSplitVec, ySplitVec, zSplitVec;
	private static int nodeCount = 0;
	
	public Octree(Octree parent, BoundingBox octBox, World world) {
		this.parent = parent;
		this.octBox = octBox;
		this.nodeId = nodeCount;
		this.modelIndices = new Array<Integer>();
		this.world = world;
		this.children = new Octree[8];
		this.xSplitVec = new Vector3((octBox.max.x - octBox.min.x) / 2, 0, 0);
		this.ySplitVec = new Vector3(0, (octBox.max.y - octBox.min.y) / 2, 0);
		this.zSplitVec = new Vector3(0, 0, (octBox.max.z - octBox.min.z) / 2);
		nodeCount++;
		create();
	}
	
	public void create() {
		// this is where we create the octree
		
		// check each modelInstance to see if it is in the octree
		//System.out.println("Size of worldBoundingBoxes");
		//System.out.println(world.getBoundingBoxes().size);
		for (int i = 0; i < world.getBoundingBoxes().size; i++) {
			if (octBox.intersects(world.getBoundingBoxes().get(i))) {
				// populate the list of modelIndices
				modelIndices.add(i);
			}
		}
		
		// only split if octBox dimension is greater than or equal to 2
		if ((octBox.max.x - octBox.min.x) >= 2) {
			
//			System.out.println("Splitting octBox: min(" + octBox.min.x + "," + octBox.min.y + "," + octBox.min.z + ")"
//					+ "max(" + octBox.max.x + "," + octBox.max.y + "," + octBox.max.z + ")");
			
			//System.out.print
			
			// divide this into 8 children, and call recursively on them, passing in list of modelIndices			
			Vector3 minVector = new Vector3();
			Vector3 maxVector = new Vector3();
			
			int index = 0;
			
			minVector.set(octBox.min);
			maxVector.set(minVector.cpy().add(xSplitVec.x, ySplitVec.y, zSplitVec.z));
			children[index] = new Octree(this, new BoundingBox(minVector, maxVector), world);
//			System.out.println("New Octree, minVec: (" + minVector.x + "," + minVector.y + "," + minVector.z + "), maxVec: ("
//					 + maxVector.x + "," + maxVector.y + "," + maxVector.z + "), nodeId: " + children[index].nodeId);
			index++;
			
			minVector.set(octBox.min.cpy().add(xSplitVec));
			maxVector.set(minVector.cpy().add(xSplitVec.x, ySplitVec.y, zSplitVec.z));
			children[index] = new Octree(this, new BoundingBox(minVector, maxVector), world);
//			System.out.println("New Octree, minVec: (" + minVector.x + "," + minVector.y + "," + minVector.z + "), maxVec: ("
//					 + maxVector.x + "," + maxVector.y + "," + maxVector.z + "), nodeId: " + children[index].nodeId);
			index++;
			
			minVector.set(octBox.min.cpy().add(zSplitVec));
			maxVector.set(minVector.cpy().add(xSplitVec.x, ySplitVec.y, zSplitVec.z));
			children[index] = new Octree(this, new BoundingBox(minVector, maxVector), world);
//			System.out.println("New Octree, minVec: (" + minVector.x + "," + minVector.y + "," + minVector.z + "), maxVec: ("
//					 + maxVector.x + "," + maxVector.y + "," + maxVector.z + "), nodeId: " + children[index].nodeId);
			index++;
			
			minVector.set(octBox.min.cpy().add(xSplitVec).add(zSplitVec));
			maxVector.set(minVector.cpy().add(xSplitVec.x, ySplitVec.y, zSplitVec.z));
			children[index] = new Octree(this, new BoundingBox(minVector, maxVector), world);
//			System.out.println("New Octree, minVec: (" + minVector.x + "," + minVector.y + "," + minVector.z + "), maxVec: ("
//					 + maxVector.x + "," + maxVector.y + "," + maxVector.z + "), nodeId: " + children[index].nodeId);
			index++;
			
			minVector.set(octBox.min.cpy().add(ySplitVec));
			maxVector.set(minVector.cpy().add(xSplitVec.x, ySplitVec.y, zSplitVec.z));
			children[index] = new Octree(this, new BoundingBox(minVector, maxVector), world);
//			System.out.println("New Octree, minVec: (" + minVector.x + "," + minVector.y + "," + minVector.z + "), maxVec: ("
//					 + maxVector.x + "," + maxVector.y + "," + maxVector.z + "), nodeId: " + children[index].nodeId);
			index++;
			
			minVector.set(octBox.min.cpy().add(xSplitVec).add(ySplitVec));
			maxVector.set(minVector.cpy().add(xSplitVec.x, ySplitVec.y, zSplitVec.z));
			children[index] = new Octree(this, new BoundingBox(minVector, maxVector), world);
//			System.out.println("New Octree, minVec: (" + minVector.x + "," + minVector.y + "," + minVector.z + "), maxVec: ("
//					 + maxVector.x + "," + maxVector.y + "," + maxVector.z + "), nodeId: " + children[index].nodeId);
			index++;
			
			minVector.set(octBox.min.cpy().add(zSplitVec).add(ySplitVec));
			maxVector.set(minVector.cpy().add(xSplitVec.x, ySplitVec.y, zSplitVec.z));
			children[index] = new Octree(this, new BoundingBox(minVector, maxVector), world);
//			System.out.println("New Octree, minVec: (" + minVector.x + "," + minVector.y + "," + minVector.z + "), maxVec: ("
//					 + maxVector.x + "," + maxVector.y + "," + maxVector.z + "), nodeId: " + children[index].nodeId);
			index++;
			
			minVector.set(octBox.min.cpy().add(xSplitVec).add(zSplitVec).add(ySplitVec));
			maxVector.set(minVector.cpy().add(xSplitVec.x, ySplitVec.y, zSplitVec.z));
			children[index] = new Octree(this, new BoundingBox(minVector, maxVector), world);
//			System.out.println("New Octree, minVec: (" + minVector.x + "," + minVector.y + "," + minVector.z + "), maxVec: ("
//					 + maxVector.x + "," + maxVector.y + "," + maxVector.z + "), nodeId: " + children[index].nodeId);
			index++;
		}
	}
	
	public boolean isVisible() {
		// need to check if it is visible using ray picking
		return true;
	}
	
	public Octree getParent() {
		return parent;
	}
	
	public void setParent(Octree octree) {
		parent = octree;
	}
	
	public Octree getChild(int index) {
		if(index < 8 && index >= 0){
			return children[index];
		}
		else {
			return null;
		}
	}
	
	public Array<Integer> getModelIndices() {
		return modelIndices;
	}
	
	public BoundingBox getBoundingBox() {
		return octBox;
	}
}