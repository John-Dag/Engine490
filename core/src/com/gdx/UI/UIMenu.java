package com.gdx.UI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;

public class UIMenu extends UIBase {
	private Array<TextButton> buttonInstances;
	private Color color;
	private Table table;
	private Label title;
	
	public UIMenu(Stage stage, Skin skin, Array<TextButton> buttonInstances, String menuTitle, float posX, float posY) {
		super(stage);
		setButtonInstances(buttonInstances);
		table = new Table();
		table.setFillParent(true);
		title = new Label(menuTitle, skin);
		table.setPosition(posX, posY);
		table.setVisible(false);
	}
	
	/***
	 * Generates a menu with a vertical layout using buttons (TextButton). Specify button ClickListeners
	 * for each button before generating a menu.
	 * @param padding Value that specifies the amount of space between buttons.
	 */

	public void generateVerticalMenu(int padding) {
		table.add(title).padBottom(padding).row();
		for (TextButton button : buttonInstances) {
			if (button == null)
				System.err.println("generateMenu(): Button " + button);
			else {
				table.add(button).padBottom(padding).row();
			}
		}
		
		this.getStage().addActor(table);
	}
	
	/***
	 * Generates a menu with a horizontal layout using buttons (TextButton). Specify button ClickListeners
	 * for each button before generating a menu.
	 * @param padding Value that specifies the amount of space between buttons.
	 */
	
	public void generateHorizontalMenu(int padding) {
		table.add(title).padRight(padding).getColumn();
		for (TextButton button : buttonInstances) {
			if (button == null)
				System.err.println("generateMenu(): Button " + button);
			else {
				table.add(button).padRight(padding).getColumn();
			}
		}

		this.getStage().addActor(table);
	}
	
	public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Array<TextButton> getButtonInstances() {
		return buttonInstances;
	}

	public void setButtonInstances(Array<TextButton> buttonInstances) {
		this.buttonInstances = buttonInstances;
	}
}
