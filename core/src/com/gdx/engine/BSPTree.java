package com.gdx.engine;

import java.util.ArrayList;

public class BSPTree {
	//private ArrayList<Room> rooms;
	public int x1, x2, y1, y2;	// the coordinates of this space
	private BSPTree parent, leftChild, rightChild;
	
	public BSPTree(BSPTree parent, int x1, int x2, int y1, int y2){
		this.parent = parent;
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		this.leftChild = null;
		this.rightChild = null;
	}
	
	public void setLeftChild(BSPTree leftChild){
		this.leftChild = leftChild;
	}
	
	public void setRightChild(BSPTree rightChild){
		this.rightChild = rightChild;
	}
	
	public BSPTree getLeftChild(){
		return leftChild;
	}
	
	public BSPTree getRightChild(){
		return rightChild;
	}
	
	public BSPTree getParent(){
		return parent;
	}
}