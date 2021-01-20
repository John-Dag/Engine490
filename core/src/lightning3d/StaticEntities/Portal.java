package lightning3d.StaticEntities;

import lightning3d.Engine.World;

import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Vector3;

public class Portal extends StaticEntity {
	public Portal() {
		super();
	}
	
	public Portal(Vector3 position, int id, boolean isActive, boolean isRenderable, PointLight light) {
		super(position, id, isActive, isRenderable, false);
		this.setEffect(World.particleManager.portalPool.obtain());
		this.getBoundingBox().set(new Vector3(this.getPosition().x - 0.5f, this.getPosition().y - 1f, this.getPosition().z - 0.5f),
			    				  new Vector3(this.getPosition().x + 0.5f, this.getPosition().y + 1f, this.getPosition().z + 0.5f));
	}
	
	@Override
	public void update(float delta, World world) {
		if (this.getBoundingBox().intersects(world.getPlayer().getTransformedBoundingBox())) {
			world.enterDungeon();
		}
	}
}
