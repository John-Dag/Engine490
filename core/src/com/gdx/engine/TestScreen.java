package com.gdx.engine;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class TestScreen implements Screen{
	public Game game;
	public Level level;
	public PerspectiveCamera camera;
	public CameraInputController cameraController;
	public ModelBatch modelBatch;
	public ModelBuilder modelBuilder;
	public Model box;
	public Renderable ramp;
	public Shader shader;
	public AssetManager assets;
	public Environment environment;
	public Array<ModelInstance> instances = new Array<ModelInstance>();
	public boolean loading;
	
	public TestScreen(Game game) {
		this.game = game;
		//this.level = new Level();
		ramp = new Renderable();
		modelBatch = new ModelBatch();
		modelBuilder = new ModelBuilder();
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
		
		camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(7f, 7f, 7f);
		camera.lookAt(0, 0, 0);
		camera.near = 0.5f;
		camera.far = 100f;
		camera.update();
	
		cameraController = new CameraInputController(camera);
		Gdx.input.setInputProcessor(cameraController);

		assets = new AssetManager();
		assets.load("data/ramp.g3db", Model.class);
		
		loading = true;
	}
	
	private void doneLoading() {
		Mesh rampMesh = genCube();
		ramp.mesh = rampMesh;
		ramp.material = new Material();
		ramp.environment = environment;
		ramp.meshPartOffset = 0;
		ramp.meshPartSize = rampMesh.getNumIndices();
		ramp.primitiveType = GL20.GL_TRIANGLES;
		
		shader = new DefaultShader(ramp);
		shader.init();
		//Model ramp = assets.get("data/ramp.g3db", Model.class);
		
		//Model ramp = new Model();
		//Mesh rampMesh = new Mesh(loading, 0, 0, null);
		//ramp.meshes.add(rampMesh);
		
		//ModelInstance modelInstance = new ModelInstance(ramp);
		//instances.add(modelInstance);
		
		
		
		loading = false;
	}

	@Override
	public void render(float delta) {
		if (loading && assets.update()) {
			System.out.println("Assets are loaded.");
			doneLoading();
		}
		cameraController.update();
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		modelBatch.begin(camera);
		//modelBatch.render(instances, environment);
		modelBatch.render(ramp);
		modelBatch.end();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
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
	public void dispose() {
		// TODO Auto-generated method stub
		modelBatch.dispose();
	}
	
	public Mesh genCube(){
		Mesh mesh = new Mesh(true, 24, 36, 
				new VertexAttribute(Usage.Position, 3, "a_position"), 
				new VertexAttribute(Usage.Normal, 3, "a_normal"));

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

		float[] vertices = new float[24 * 6];
		int pIdx = 0;
		int nIdx = 0;
		for (int i = 0; i < vertices.length;) {
			vertices[i++] = cubeVerts[pIdx++];
			vertices[i++] = cubeVerts[pIdx++];
			vertices[i++] = cubeVerts[pIdx++];
			vertices[i++] = cubeNormals[nIdx++];
			vertices[i++] = cubeNormals[nIdx++];
			vertices[i++] = cubeNormals[nIdx++];
		}

		short[] indices = {0, 2, 1, 0, 3, 2, 4, 5, 6, 4, 6, 7, 8, 9, 10, 8, 10, 11, 12, 15, 14, 12, 14, 13, 16, 17, 18, 16, 18, 19,
				20, 23, 22, 20, 22, 21};

		mesh.setVertices(vertices);
		mesh.setIndices(indices);

		return mesh;
	}
	
//	public Mesh genCube(){
//		Mesh mesh = new Mesh(true, 24, 36, 
//				new VertexAttribute(Usage.Position, 3, "a_position"), 
//				new VertexAttribute(Usage.Normal, 3, "a_normal"), 
//				new VertexAttribute(Usage.TextureCoordinates, 2, "a_texcoords"));
//
//		float[] cubeVerts = {	-0.5f, -0.5f, -0.5f, -0.5f, -0.5f,  0.5f,  0.5f, -0.5f,  0.5f,  0.5f, -0.5f, -0.5f, 
//								-0.5f,  0.5f, -0.5f, -0.5f,  0.5f,  0.5f,  0.5f,  0.5f,  0.5f,  0.5f,  0.5f, -0.5f,
//								-0.5f, -0.5f, -0.5f, -0.5f,  0.5f, -0.5f,  0.5f,  0.5f, -0.5f,  0.5f, -0.5f, -0.5f,
//								-0.5f, -0.5f,  0.5f, -0.5f,  0.5f,  0.5f,  0.5f,  0.5f,  0.5f,  0.5f, -0.5f,  0.5f,
//								-0.5f, -0.5f, -0.5f, -0.5f, -0.5f,  0.5f, -0.5f,  0.5f,  0.5f, -0.5f,  0.5f, -0.5f,
//								 0.5f, -0.5f, -0.5f,  0.5f, -0.5f,  0.5f,  0.5f,  0.5f,  0.5f,  0.5f,  0.5f, -0.5f,};
//
//		float[] cubeNormals = {	 0.0f, -1.0f,  0.0f,  0.0f, -1.0f,  0.0f,  0.0f, -1.0f,  0.0f,  0.0f, -1.0f,  0.0f,
//								 0.0f,  1.0f,  0.0f,  0.0f,  1.0f,  0.0f,  0.0f,  1.0f,  0.0f,  0.0f,  1.0f,  0.0f,
//								 0.0f,  0.0f, -1.0f,  0.0f,  0.0f, -1.0f,  0.0f,  0.0f, -1.0f,  0.0f,  0.0f, -1.0f,
//								 0.0f,  0.0f,  1.0f,  0.0f,  0.0f,  1.0f,  0.0f,  0.0f,  1.0f,  0.0f,  0.0f,  1.0f,
//								-1.0f,  0.0f,  0.0f, -1.0f,  0.0f,  0.0f, -1.0f,  0.0f,  0.0f, -1.0f,  0.0f,  0.0f,
//								 1.0f,  0.0f,  0.0f,  1.0f,  0.0f,  0.0f,  1.0f,  0.0f,  0.0f,  1.0f,  0.0f,  0.0f,};
//
//		float[] cubeTex = {		 0.0f,  0.0f,  0.0f,  1.0f,  1.0f,  1.0f,  1.0f,  0.0f,
//								 1.0f,  0.0f,  1.0f,  1.0f,  0.0f,  1.0f,  0.0f,  0.0f,
//								 0.0f,  0.0f,  0.0f,  1.0f,  1.0f,  1.0f,  1.0f,  0.0f,
//								 0.0f,  0.0f,  0.0f,  1.0f,  1.0f,  1.0f,  1.0f,  0.0f,
//								 0.0f,  0.0f,  0.0f,  1.0f,  1.0f,  1.0f,  1.0f,  0.0f,
//								 0.0f,  0.0f,  0.0f,  1.0f,  1.0f,  1.0f,  1.0f,  0.0f,};
//
//		float[] vertices = new float[24 * 8];
//		int pIdx = 0;
//		int nIdx = 0;
//		int tIdx = 0;
//		for (int i = 0; i < vertices.length;) {
//			vertices[i++] = cubeVerts[pIdx++];
//			vertices[i++] = cubeVerts[pIdx++];
//			vertices[i++] = cubeVerts[pIdx++];
//			vertices[i++] = cubeNormals[nIdx++];
//			vertices[i++] = cubeNormals[nIdx++];
//			vertices[i++] = cubeNormals[nIdx++];
//			vertices[i++] = cubeTex[tIdx++];
//			vertices[i++] = cubeTex[tIdx++];
//		}
//
//		short[] indices = {0, 2, 1, 0, 3, 2, 4, 5, 6, 4, 6, 7, 8, 9, 10, 8, 10, 11, 12, 15, 14, 12, 14, 13, 16, 17, 18, 16, 18, 19,
//				20, 23, 22, 20, 22, 21};
//
//		mesh.setVertices(vertices);
//		mesh.setIndices(indices);
//
//		return mesh;
//	}

}
