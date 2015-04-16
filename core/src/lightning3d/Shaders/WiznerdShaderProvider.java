package lightning3d.Shaders;

import lightning3d.FilterEffects.Rainbow;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;

public class WiznerdShaderProvider extends DefaultShaderProvider {
	public final WiznerdShader.Config config;

	public EntityShader entityShader;
	
	public WiznerdShaderProvider (final WiznerdShader.Config config) {
		this.config = (config == null) ? new WiznerdShader.Config() : config;
	}

	public WiznerdShaderProvider (final String vertexShader, final String fragmentShader) {
		this(new WiznerdShader.Config(vertexShader, fragmentShader));
	}

	public WiznerdShaderProvider (final FileHandle vertexShader, final FileHandle fragmentShader) {
		this(vertexShader.readString(), fragmentShader.readString());
	}

	public WiznerdShaderProvider () {
		this(null);
	}

	@Override
	protected Shader createShader (final Renderable renderable) {
		
		if(renderable.userData instanceof EntityShader)
		{
			entityShader=(EntityShader)renderable.userData;
		}else {entityShader=null;return new DefaultShader(renderable,config);}
		
		if(entityShader!=null){
			config.enableColorMultiplier=false;
			config.enableRainbow=false;
			config.enableFract=false;
			config.enableFireBall=false;
			config.enableBlackHole=false;
			
			if(entityShader instanceof ColorMultiplierEntityShader){
				config.enableColorMultiplier=true;
				config.enableRainbow=false;
			}
			else if(entityShader instanceof FireBallShader)
			{
				config.enableFireBall=true;
			}
			else if(entityShader instanceof LavaShader)
			{
				config.enableFract=true;
				config.enableColorMultiplier=true;
			}
			else if(entityShader instanceof EntityRainbow)
			{
				config.enableRainbow=true;
				config.enableColorMultiplier=false;
			}
			else if(entityShader instanceof BlackHole)
			{
				config.enableBlackHole=true;
				config.enableColorMultiplier=false;
				config.enableRainbow=false;
				config.enableFract=false;
				config.enableFireBall=false;
				
			}
			return createShader ( renderable, entityShader);
		}
		//config.enableColorMultiplier=false;
		return new WiznerdShader(renderable, config);
	}
	
	@Override
	public Shader getShader(Renderable renderable) {
		// TODO Auto-generated method stub
		//Shader shader= super.getShader(renderable);
		

		
		Shader suggestedShader = renderable.shader;
		if (suggestedShader != null && suggestedShader.canRender(renderable)) 
			return suggestedShader;
		
		if(renderable.userData instanceof EntityShader)
		{
			entityShader=(EntityShader)renderable.userData;
		}
		else entityShader=null;
		
		if(entityShader!=null)
		for (Shader shader : shaders) {
			if (shader.canRender(renderable))
				if(((WiznerdShader)shader).getWiznerdEntityShader()==entityShader)
				{
					renderable.shader=shader;
					return shader;
				}
		}
		else
			for (Shader shader : shaders) {
				if (shader.canRender(renderable))
					//if(((WiznerdShader)shader).getWiznerdEntityShader()==entityShader)
					{
						renderable.shader=shader;
						return shader;
					}
			}

		
		
		final Shader shader = createShader(renderable);
		shader.init();
		shaders.add(shader);
		//return shader;
		
		
		if(entityShader!=null&& shader instanceof WiznerdShader)
		{
			WiznerdShader wss=(WiznerdShader)shader;
			wss.setWiznerdEntityShader(entityShader);
			return wss;
		}
		return shader;
	}
	
	protected Shader createShader (final Renderable renderable,EntityShader es) {
		WiznerdShader ws= new WiznerdShader(renderable, config);
		ws.setWiznerdEntityShader(es);
		return ws;
		
	}
}
