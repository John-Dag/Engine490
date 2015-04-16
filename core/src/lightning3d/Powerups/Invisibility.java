package lightning3d.Powerups;

import lightning3d.Engine.World;
import lightning3d.StaticEntities.PowerUp;
import lightning3d.StaticEntities.PowerUp.powerUpTypeEnum;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class Invisibility extends PowerUp{
	private Model model;
	private float duration;
	private Vector3 xVec = new Vector3(1,0,0);
	
	public Invisibility () {
		
	}
	
	public Invisibility(Vector3 position, int id, boolean isActive, boolean isRenderable, Model model, float duration, powerUpTypeEnum type) {
		super(position, id, isActive, isRenderable, model, duration, type);
		this.model = model;
		this.duration = duration;
		
		BoundingBox temp = new BoundingBox();
		this.getModel().calculateBoundingBox(temp);
		this.setBoundingBox(temp);
		this.getModel().transform.setToTranslation(this.getPosition());
		this.getModel().transform.scale(0.05f, 0.05f, 0.05f);
		this.getModel().transform.rotate(xVec, 90);
	}
	
	@Override
	public void effect() {
		System.out.println("You are now invisible.");
	}
	
	@Override
	public PowerUp spawn() {
		Invisibility invis = new Invisibility(this.getPosition().cpy(), 2, true, true, model, duration, powerUpTypeEnum.healthPot);
		BoundingBox temp = new BoundingBox();
		invis.getModel().calculateBoundingBox(temp);
		invis.setBoundingBox(temp);
		invis.getModel().transform.setToTranslation(invis.getPosition());
		invis.getModel().transform.scale(0.05f, 0.05f, 0.05f);
		invis.getModel().transform.rotate(xVec, 90);
		return invis;
	}
}
