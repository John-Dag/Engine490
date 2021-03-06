package lightning3d.UI;

import lightning3d.Network.NetStatField;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Array;

public class UIForm extends UIBase {
	private Skin skin;
	private Array<TextField> fields;
	private Array<NetStatField> statFields;

	public UIForm(Stage stage, Skin skin, String name) {
		super(stage);
		this.setWindow(new Window(name, skin));
		fields = new Array<TextField>();
		setStatFields(new Array<NetStatField>());
		this.skin = skin;
	}

	public void generateWindow(float x, float y, int width, int height, boolean moveable) {
		getWindow().setPosition(x, y);
		getWindow().setSize(width, height);
		getWindow().setMovable(moveable);
		this.getStage().addActor(getWindow());
	}
	
	/***
	 * Adds a text field to the form window
	 * @param title
	 * @param x position x
	 * @param y position y
	 * @param width width of getWindow()
	 * @param height height of getWindow()
	 */
	
	public void addTextField(String title, float x, float y, int width, int height) {
		try {
			TextField field = new TextField(title, skin);
			field.setPosition(x, y);
			field.setSize(width, height);
			fields.add(field);
			getWindow().addActor(field);
		}
		catch (Exception e) {
			System.err.println(e);
		}
	}
	
	/***
	 * Adds a NetStat field to the form window
	 * @param field
	 * @param posX
	 * @param posY
	 * @param width
	 * @param height
	 */
	
	public void addNetStatField(NetStatField field, float posX, float posY, int width, int height) {
		try {
			field.setPosition(posX, posY);
			field.setWidth(width);
			field.setHeight(height);
			statFields.add(field);
			getWindow().addActor(field);
		}
		catch (Exception e) {
			System.err.println(e);
		}
	}
	
	public Array<TextField> getFields() {
		return fields;
	}

	public void setFields(Array<TextField> fields) {
		this.fields = fields;
	}

	public Array<NetStatField> getStatFields() {
		return statFields;
	}

	public void setStatFields(Array<NetStatField> statFields) {
		this.statFields = statFields;
	}
}
