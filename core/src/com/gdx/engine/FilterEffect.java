package com.gdx.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;

public abstract class FilterEffect implements Disposable {
	protected ShaderProgram shader;
	protected String effectName;
	protected String vertexShader;
	protected String fragmentShader;
	private FrameBuffer filterEffectBuffer;
	public abstract void Update(World world, float delta);
	
	public void begin() {
		shader.begin();
		setShaderParams();
	}
	
	public void end() {
		shader.end();
	}
	
	protected void loadShaderProgram() {
		vertexShader = Gdx.files.internal("shaders/filtereffects/"+effectName+".vertex.glsl.txt").readString();
        fragmentShader = Gdx.files.internal("shaders/filtereffects/"+effectName+".fragment.glsl.txt").readString();
        shader = new ShaderProgram(vertexShader, fragmentShader);
	}
	
	protected void setShaderParams() {
		shader.setUniformi("u_texture",0);
	}
	
	protected void initializeFrameBuffer() {
		filterEffectBuffer= new FrameBuffer( Format.RGBA8888
			    , Gdx.graphics.getWidth()
			    , Gdx.graphics.getHeight()
			    , true);
	}
	
	public ShaderProgram getShader() {
		return shader;
	}

	public void setShader(ShaderProgram shader) {
		this.shader = shader;
	}
	
	public String getVertexShader() {
		return vertexShader;
	}

	public void setVertexShader(String vertexShader) {
		this.vertexShader = vertexShader;
	}

	public String getFragmentShader() {
		return fragmentShader;
	}

	public void setFragmentShader(String fragmentShader) {
		this.fragmentShader = fragmentShader;
	}
	
	public String getEffectName() {
		return effectName;
	}

	public void setEffectName(String effectName) {
		this.effectName = effectName;
	}
	
	public FrameBuffer getFilterEffectBuffer() {
		return filterEffectBuffer;
	}


	public void setFilterEffectBuffer(FrameBuffer filterEffectBuffer) {
		this.filterEffectBuffer = filterEffectBuffer;
	}


	public void dispose()
	{
		if (shader!=null) {
			shader.dispose();
		}
		if (filterEffectBuffer != null) {
			filterEffectBuffer.dispose();
		}
	}
}
