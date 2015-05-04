package lightning3d.Shaders;

import lightning3d.Engine.Entity;

import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader.Setter;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader.Uniform;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader.Config;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader.Inputs;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader.Setters;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class WiznerdShader extends DefaultShader {

	
	public final static Vector3 colorMult=new Vector3(1,1,1);
	public final static Vector2 resolution=new Vector2(0,0);
	public final static float time=0;
	
	public static class Config extends DefaultShader.Config{

		public boolean enableColorMultiplier=false;
		
		public boolean enableFireBall=false;
		
		public boolean enableFract=false;
		
		public boolean enableRainbow=false;
		
		public boolean enableBlackHole=false;
		
		public boolean enableLaser=false;
		
		public Config(String vertexShader, String fragmentShader) {
			super(vertexShader,fragmentShader);
		}

		public Config() {
			super();
		}
		
		
	
	}
	
	public static class Inputs extends DefaultShader.Inputs {
		public final static Uniform colorMultiplier = new Uniform("u_colorMultiplier");
		public final static Uniform time = new Uniform("time");
		public final static Uniform resolution = new Uniform("resolution");
	}
	
	public static class Setters extends DefaultShader.Setters {
		public final static Setter colorMultiplier = new Setter() {
			@Override
			public boolean isGlobal (BaseShader shader, int inputID) {
				return true;
			}

			@Override
			public void set (BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
				shader.set(inputID, colorMult);
			}
		};
		public final static Setter resolution = new Setter() {
			@Override
			public boolean isGlobal (BaseShader shader, int inputID) {
				return true;
			}

			@Override
			public void set (BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
				shader.set(inputID, WiznerdShader.resolution);
			}
		};
		public final static Setter time = new Setter() {
			@Override
			public boolean isGlobal (BaseShader shader, int inputID) {
				return true;
			}

			@Override
			public void set (BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
				shader.set(inputID, colorMult);
			}
		};
	}
	
	private EntityShader wiznerdEntityShader;
	
	public WiznerdShader (final Renderable renderable) {
		this(renderable, new WiznerdShader.Config());
	}

	public WiznerdShader (final Renderable renderable, final Config config) {
		this(renderable, config, createPrefix(renderable, config));
	}

	public WiznerdShader (final Renderable renderable, final Config config, final String prefix) {

		this(renderable, config, prefix, config.vertexShader != null ? config.vertexShader : getDefaultVertexShader(),
				config.fragmentShader != null ? config.fragmentShader : getDefaultFragmentShader());
	}
	
	
	public WiznerdShader (final Renderable renderable, final Config config, final ShaderProgram shaderProgram)
	{
		
		super(renderable,config,shaderProgram);
		//u_colorMultiplier=register(new Uniform("u_colorMultiplier"));
		//if(config.enableColorMultiplier)
		{
			u_colorMultiplier=register(Inputs.colorMultiplier, Setters.colorMultiplier);
		}
		u_time=register(Inputs.time,Setters.time);
		u_resolution=register(Inputs.resolution,Setters.resolution);
		
		//init();
		//set(u_colorMultiplier, colorMult);
		
	}
	
	@Override
	public void render(Renderable renderable) {
		if(wiznerdEntityShader!=null)
			wiznerdEntityShader.begin(this);
		
		super.render(renderable);
	}
	
	@Override
	public void end() {
		// TODO Auto-generated method stub
		super.end();
		if(wiznerdEntityShader!=null)
			wiznerdEntityShader.end(this);
	}

	public WiznerdShader (final Renderable renderable, final Config config, final String prefix, final String vertexShader,
		final String fragmentShader) {
		this(renderable, config, new ShaderProgram(prefix + vertexShader, prefix + fragmentShader));
	}
	
	public int u_colorMultiplier;//=register(new Uniform("u_colorMultiplier"));
	public int u_time;
	public int u_resolution;
	
	public static String createPrefix (final Renderable renderable, final Config config) {
		String prefix=DefaultShader.createPrefix(renderable,config);
		
		//Add custom setters here
		if(config.enableColorMultiplier)
		{
			prefix += "#define ColorMultiplier\n";
		}
		
		if(config.enableFireBall)
		{
			prefix += "#define FireBall\n";
		}
		
		if(config.enableFract)
		{
			prefix += "#define Fract\n";
		}
		
		if(config.enableRainbow)
		{
			prefix += "#define Rainbow\n";
		}
		
		if(config.enableBlackHole)
		{
			prefix += "#define BlackHole\n";
		}
		
		if(config.enableLaser)
		{
			prefix += "#define Laser\n";
		}
		
		
		
		return prefix;
	}

	public EntityShader getWiznerdEntityShader() {
		return wiznerdEntityShader;
	}

	public void setWiznerdEntityShader(EntityShader wiznerdEntityShader) {
		this.wiznerdEntityShader = wiznerdEntityShader;
	}
	

}
