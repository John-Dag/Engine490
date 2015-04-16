package lightning3d.StaticEntities;

import lightning3d.Engine.Entity;
import lightning3d.Engine.Render;
import lightning3d.Engine.World;
import lightning3d.Shaders.EntityShader;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class StaticEntity extends Entity {
	private Vector3 position;
	private Decal decal;
	private Color color;
	private float intensity;
	private PointLight pointLight;
	private boolean isDecalFacing;
	private ParticleEffect effect;
	private ModelInstance model;
	private BoundingBox boundingBox = new BoundingBox();

	public StaticEntity() {
		super();
		decal = new Decal();
		color = new Color();
		intensity = 0f;
		pointLight = new PointLight();
		effect = new ParticleEffect();
	}

	public StaticEntity(Vector3 position, int id, boolean isActive, boolean isRenderable, boolean isDecalFacing) {
		super(id, isActive, isRenderable);
		this.position = position;
		this.isDecalFacing = isDecalFacing;
		this.effect = new ParticleEffect();
	}

	public StaticEntity(Vector3 position, int id, boolean isActive, boolean isRenderable, boolean isDecalFacing, Model model) {
		super(id, isActive, isRenderable);
		this.position = position;
		this.isDecalFacing = isDecalFacing;
		this.effect = new ParticleEffect();
		this.setModel(new ModelInstance(model));
	}
	
	@Override
	public void update(float delta, World world) {

	}
	
	@Override 
	public void render(ModelBatch modelBatch, DecalBatch decalBatch, ModelBatch shadowBatch) {
		if (this.decal != null)
			decalBatch.add(this.decal);
		if (this.model != null) {
			//this.model.transform.setToTranslation(this.position);
			//this.model.transform.scale(0.005f, 0.005f, 0.005f);
			shadowBatch.render(this.model);
			modelBatch.render(this.model);
		}
	}
	
	@Override
	public void initialize(World world) {
		super.initialize(world);
		if (this.isRenderable() && this.isActive()) {
			if (this.getPointLight() != null)
				Render.environment.add(this.getPointLight());
			if (this.getEffect() != null) {
				this.getEffect().init();
				this.getEffect().start();
				this.getEffect().translate(this.getPosition());
				World.particleManager.system.add(this.getEffect());
			}
		}
	}
	
	public BoundingBox getTransformedBoundingBox(){
		return new BoundingBox(this.boundingBox).mul(this.model.transform);
	}

	public BoundingBox getBoundingBox() {
		return boundingBox;
	}

	public void setBoundingBox(BoundingBox boundingBox) {
		this.boundingBox = boundingBox;
	}
	
	public ModelInstance getModel() {
		return model;
	}

	public void setModel(ModelInstance model) {
		this.model = model;
	}
	
	public ParticleEffect getEffect() {
		return effect;
	}

	public void setEffect(ParticleEffect effect) {
		this.effect = effect;
	}

	public Decal getDecal() {
		return decal;
	}

	public Color getColor() {
		return color;
	}

	public float getIntensity() {
		return intensity;
	}

	public PointLight getPointLight() {
		return pointLight;
	}

	public void setDecal(Decal decal) {
		this.decal = decal;
		this.decal.setPosition(this.position);
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}

	public void setPointLight(PointLight pointLight) {
		this.pointLight = pointLight;
	}

	public Vector3 getPosition() {
		return position;
	}

	public boolean isDecalFacing() {
		return isDecalFacing;
	}

	public void setPosition(Vector3 position) {
		this.position = position;
	}

	public void setDecalFacing(boolean isDecalFacing) {
		this.isDecalFacing = isDecalFacing;
	}
	
	public void setShader(EntityShader shader) {
		super.setShader(shader);
		if(this.model!=null)
		{
			this.model.userData=shader;
		}
	}
}
