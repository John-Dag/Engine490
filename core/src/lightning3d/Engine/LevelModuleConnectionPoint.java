package lightning3d.Engine;

// These keep track of where the level module can connect to other level modules through connectors
// Their frame of reference is with respect to the level module that owns it, so offsets are applied at a higher level
public class LevelModuleConnectionPoint {
	private int x,y,z;
	
	public LevelModuleConnectionPoint(){
		this.x = -1;
		this.y = -1;
		this.z = -1;
	}
	
	public LevelModuleConnectionPoint(int x, int y, int z){
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}
}