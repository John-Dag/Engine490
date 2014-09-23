package com.gdx.engine;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;

public class MeshCreatorTest implements ApplicationListener{
	public PerspectiveCamera cam;
	public CameraInputController camController;
	public Shader shader;
	public RenderContext renderContext;
	public Model model;
	public Environment environment;
	public Renderable renderable;

	@Override
	public void create() {
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
		
		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(2f,2f,2f);
		cam.lookAt(0,0,0);
		cam.near = 1f;
		cam.far = 300f;
		cam.update();
		
		camController = new CameraInputController(cam);
		Gdx.input.setInputProcessor(camController);
		
		renderable = new Renderable();
		//renderable.mesh = genCube();
		renderable.mesh = genRect();
		renderable.meshPartOffset = 0;
		renderable.meshPartSize = renderable.mesh.getNumIndices();
		renderable.primitiveType = GL20.GL_TRIANGLES;
		renderable.material = new Material();
		renderable.environment = environment;
		
		renderContext = new RenderContext(new DefaultTextureBinder(DefaultTextureBinder.WEIGHTED, 1));
		shader = new DefaultShader(renderable);
		shader.init();
	}

	@Override
	public void render() {
		camController.update();
		
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		renderContext.begin();
		shader.begin(cam, renderContext);
		shader.render(renderable);
		shader.end();
		renderContext.end();
	}
	
	@Override
	public void dispose() {
		shader.dispose();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}
	
	public Mesh genRect(){
		Mesh mesh = new Mesh(true, 4, 6,
				new VertexAttribute(Usage.Position, 3, "a_position"), 
				new VertexAttribute(Usage.Normal, 3, "a_normal"), 
				new VertexAttribute(Usage.TextureCoordinates, 2, "a_texcoords"));
		float[] rectVerts = { 	-0.5f, -0.5f, 0f,  0.5f, -0.5f, 0f,
								 0.5f,  0.5f, 0f, -0.5f,  0.5f, 0f, };
		
		float[] rectNormals = {	0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
								0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, };
		
		float[] rectTex = { 0.0f,  0.0f,  0.0f,  1.0f,  1.0f,  1.0f,  1.0f,  0.0f,};
		
		float[] vertices = new float[4 * 8];
		int pIdx = 0;
		int nIdx = 0;
		int tIdx = 0;
		for (int i = 0; i < vertices.length;) {
			vertices[i++] = rectVerts[pIdx++];
			vertices[i++] = rectVerts[pIdx++];
			vertices[i++] = rectVerts[pIdx++];
			vertices[i++] = rectNormals[nIdx++];
			vertices[i++] = rectNormals[nIdx++];
			vertices[i++] = rectNormals[nIdx++];
			vertices[i++] = rectTex[tIdx++];
			vertices[i++] = rectTex[tIdx++];
		}
		
		short[] indices = {0, 1, 3, 1, 2, 3};

		mesh.setVertices(vertices);
		mesh.setIndices(indices);
		
		return mesh;
	}
	
	public Mesh genCube(){
		Mesh mesh = new Mesh(true, 24, 36, 
				new VertexAttribute(Usage.Position, 3, "a_position"), 
				new VertexAttribute(Usage.Normal, 3, "a_normal"), 
				new VertexAttribute(Usage.TextureCoordinates, 2, "a_texcoords"));

		float[] cubeVerts = {	-0.5f, -0.5f, -0.5f, -0.5f, -0.5f,  0.5f,  0.5f, -0.5f,  0.5f,  0.5f, -0.5f, -0.5f, 
				-0.5f,  0.5f, -0.5f, -0.5f,  0.5f,  0.5f,  0.5f,  0.5f,  0.5f,  0.5f,  0.5f, -0.5f,
				-0.5f, -0.5f, -0.5f, -0.5f,  0.5f, -0.5f,  0.5f,  0.5f, -0.5f,  0.5f, -0.5f, -0.5f,
				-0.5f, -0.5f,  0.5f, -0.5f,  0.5f,  0.5f,  0.5f,  0.5f,  0.5f,  0.5f, -0.5f,  0.5f,
				-0.5f, -0.5f, -0.5f, -0.5f, -0.5f,  0.5f, -0.5f,  0.5f,  0.5f, -0.5f,  0.5f, -0.5f,
				0.5f, -0.5f, -0.5f,  0.5f, -0.5f,  0.5f,  0.5f,  0.5f,  0.5f,  0.5f,  0.5f, -0.5f,};

		float[] cubeNormals = {	 0.0f, -1.0f,  0.0f,  0.0f, -1.0f,  0.0f,  0.0f, -1.0f,  0.0f,  0.0f, -1.0f,  0.0f,
				0.0f,  1.0f,  0.0f,  0.0f,  1.0f,  0.0f,  0.0f,  1.0f,  0.0f,  0.0f,  1.0f,  0.0f,
				0.0f,  0.0f, -1.0f,  0.0f,  0.0f, -1.0f,  0.0f,  0.0f, -1.0f,  0.0f,  0.0f, -1.0f,
				0.0f,  0.0f,  1.0f,  0.0f,  0.0f,  1.0f,  0.0f,  0.0f,  1.0f,  0.0f,  0.0f,  1.0f,
				-1.0f,  0.0f,  0.0f, -1.0f,  0.0f,  0.0f, -1.0f,  0.0f,  0.0f, -1.0f,  0.0f,  0.0f,
				1.0f,  0.0f,  0.0f,  1.0f,  0.0f,  0.0f,  1.0f,  0.0f,  0.0f,  1.0f,  0.0f,  0.0f,};

		float[] cubeTex = {		 0.0f,  0.0f,  0.0f,  1.0f,  1.0f,  1.0f,  1.0f,  0.0f,
				1.0f,  0.0f,  1.0f,  1.0f,  0.0f,  1.0f,  0.0f,  0.0f,
				0.0f,  0.0f,  0.0f,  1.0f,  1.0f,  1.0f,  1.0f,  0.0f,
				0.0f,  0.0f,  0.0f,  1.0f,  1.0f,  1.0f,  1.0f,  0.0f,
				0.0f,  0.0f,  0.0f,  1.0f,  1.0f,  1.0f,  1.0f,  0.0f,
				0.0f,  0.0f,  0.0f,  1.0f,  1.0f,  1.0f,  1.0f,  0.0f,};

		float[] vertices = new float[24 * 8];
		int pIdx = 0;
		int nIdx = 0;
		int tIdx = 0;
		for (int i = 0; i < vertices.length;) {
			vertices[i++] = cubeVerts[pIdx++];
			vertices[i++] = cubeVerts[pIdx++];
			vertices[i++] = cubeVerts[pIdx++];
			vertices[i++] = cubeNormals[nIdx++];
			vertices[i++] = cubeNormals[nIdx++];
			vertices[i++] = cubeNormals[nIdx++];
			vertices[i++] = cubeTex[tIdx++];
			vertices[i++] = cubeTex[tIdx++];
		}

		short[] indices = {0, 2, 1, 0, 3, 2, 4, 5, 6, 4, 6, 7, 8, 9, 10, 8, 10, 11, 12, 15, 14, 12, 14, 13, 16, 17, 18, 16, 18, 19,
				20, 23, 22, 20, 22, 21};

		mesh.setVertices(vertices);
		mesh.setIndices(indices);

		return mesh;
	}

}