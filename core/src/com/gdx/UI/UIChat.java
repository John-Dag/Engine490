package com.gdx.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.gdx.Network.Net;
import com.gdx.engine.GameScreen;
import com.gdx.engine.World;

public class UIChat extends UIBase {
	private Window window;
	private final int ENTER = 13;
	private final int T = 84;
	private TextField textfield;
	private String fieldValue;
	private Table textAreaTable;
	private Label textArea;
	private StringBuilder chatLog;
	private ScrollPane scrollPane;
	
	public UIChat(Stage stage, Skin skin, String name) {
		super(stage);
		window = new Window(name, skin);
		textfield = new TextField("", skin);
		fieldValue = new String();
		textAreaTable = new Table();
		textAreaTable.setFillParent(false);
		chatLog = new StringBuilder();
		textArea = new Label("", skin);
	}
	
	/***
	 * Adds a chat widget to the UI
	 * @param width Main window
	 * @param height Main window
	 * @param posX xCoord of the main window
	 * @param posY xCoord of the main window
	 * @param fontScale Size of chat window font
	 */

	public void addChatWidget(float width, float height, float posX, float posY, float fontScale) {
		window.setSize(width, height);
		window.setPosition(posX, posY);
		window.setColor(Color.BLUE);
		textAreaTable.setSize(window.getWidth(), window.getHeight());
		textfield.setSize(textAreaTable.getWidth(), 30);
		textfield.setDisabled(true);
		textAreaTable.addActor(textfield);
		textArea.setSize(textAreaTable.getWidth(), textAreaTable.getHeight() - 50f);
		textArea.setPosition(20, 30);
		textArea.setWrap(true);
		textArea.setFontScale(fontScale);
		scrollPane = new ScrollPane(textArea);
		scrollPane.setColor(Color.BLUE);
		scrollPane.setForceScroll(false, true);
		scrollPane.setFlickScroll(true);
		scrollPane.setOverscroll(false, false);
		scrollPane.layout();
		textAreaTable.add(scrollPane).width(width - 10).height(height - 55);
		window.addActor(textAreaTable);

		this.getStage().addActor(window);
	}
	
	/***
	 * Adds default listeners for basic functionality
	 */
	
	public void addChatWidgetListeners() {
		final Stage stage = this.getStage();
		
		stage.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				boolean textfieldClicked = stage.hit(x, y, true) == textfield;
				boolean textAreaClicked = stage.hit(x, y, true) == textArea;
				
				if (textfieldClicked) {
					textfield.setDisabled(false);
					stage.unfocusAll();
					stage.setKeyboardFocus(textfield);
					UIBase.uiSelected = true;
				}
				
				else {
					textfield.setDisabled(true);
					UIBase.uiSelected = false;
				}
				
				return textfieldClicked;
			}
		});

		textfield.setTextFieldListener(new TextFieldListener() {
			public void keyTyped (TextField textField, char key) {
				if (key == ENTER) { //Enter key ASCII value is 13
					if (textfield.getText().isEmpty()) {
						textfield.setDisabled(true);
						UIBase.uiSelected = false;
						return;
					}
					
					fieldValue = textfield.getText();
					textField.setText("");
					chatLog.append(Net.name + ": " + fieldValue + "\n");
					textArea.setText(chatLog.toString());
					scrollPane.setScrollPercentY(scrollPane.getScrollPercentY());
					
					if (GameScreen.mode == GameScreen.state.Client) {
						sendMessage(fieldValue);
					}
				}
			}
		});
	}
	
	public void sendMessage(String message) {
		try {
			Net.chatMessage packet = new Net.chatMessage();
			packet.message = Net.name + ": " + fieldValue;
			GameScreen.client.sendChatMessage(packet);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addMessage(Net.chatMessage packet) {
		fieldValue = textfield.getText();
		chatLog.append(packet.message + "\n");
		textArea.setText(chatLog.toString());
		scrollPane.setScrollPercentY(scrollPane.getScrollPercentY());
	}
	
	public void activateChatField() {
		if (textfield.isDisabled()) {
			textfield.setDisabled(false);
			this.getStage().unfocusAll();
			this.getStage().setKeyboardFocus(textfield);
			UIBase.uiSelected = true;
		}
	}

	public Window getWindow() {
		return window;
	}

	public TextField getTextfield() {
		return textfield;
	}

	public String getFieldValue() {
		return fieldValue;
	}

	public Table getTextAreaTable() {
		return textAreaTable;
	}

	public StringBuilder getChatLog() {
		return chatLog;
	}

	public ScrollPane getScrollPane() {
		return scrollPane;
	}

	public void setWindow(Window window) {
		this.window = window;
	}

	public void setTextfield(TextField textfield) {
		this.textfield = textfield;
	}

	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}

	public void setTextAreaTable(Table textAreaTable) {
		this.textAreaTable = textAreaTable;
	}

	public void setChatLog(StringBuilder chatLog) {
		this.chatLog = chatLog;
	}

	public void setScrollPane(ScrollPane scrollPane) {
		this.scrollPane = scrollPane;
	}
}
