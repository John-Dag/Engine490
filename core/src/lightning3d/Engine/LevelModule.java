package lightning3d.Engine;

import java.util.ArrayList;
import com.badlogic.gdx.math.Vector3;

// This is a room in a procedurally generated dungeon. Level modules are connected by level module connectors.
public class LevelModule {
	private int length;
	private int width;
	private int height;	// number of layers
	private Vector3 offset;
	private ArrayList<LevelModuleConnectionPoint> connectionPoints;
	private MapTile[][][] levelArray;
	
	// constructor
	public LevelModule(){
		this.length = 16;
		this.width = 16;
		this.height = 1;
		this.offset = new Vector3(0,0,0);
		
		generateModule();
	}
	
	// constructor
	public LevelModule(int length, int width, int height, Vector3 offset){
		this.length = length;
		this.width = width;
		this.height = height;
		this.offset = offset;
		
		generateConnectionPoints();
		generateModule();
	}
	
	private void generateConnectionPoints() {
		
	}
	
	private void generateModule() {
		
	}
}