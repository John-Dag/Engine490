package lightning3d.Engine;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector3;

public class SoundEffect {
	private Vector3 position;
	private Sound sound;
	private boolean isPlaying, isPaused, isAmbient, isDirectional;
	private long id;
	private float distanceTrigger, volume, volumeScale;
	
	public SoundEffect(Vector3 position, Sound sound, float distanceTrigger, float volumeScale, boolean isAmbient, boolean isDirectional) {
		this.setPosition(position);
		this.setSound(sound);
		this.isPlaying = false;
		this.isPaused = false;
		this.isAmbient = isAmbient;
		this.isDirectional = isDirectional;
		this.setVolume(0);
		this.setDistanceTrigger(distanceTrigger);
		this.volumeScale = volumeScale;
	}

	public Vector3 getPosition() {
		return position;
	}

	public void setPosition(Vector3 position) {
		this.position = position;
	}

	public Sound getSound() {
		return sound;
	}

	public void setSound(Sound sound) {
		this.sound = sound;
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	public void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public float getVolume() {
		return volume;
	}

	public void setVolume(float volume) {
		this.volume = volume;
	}

	public boolean isAmbient() {
		return isAmbient;
	}

	public void setAmbient(boolean isAmbient) {
		this.isAmbient = isAmbient;
	}

	public boolean isDirectional() {
		return isDirectional;
	}

	public void setDirectional(boolean isDirectional) {
		this.isDirectional = isDirectional;
	}

	public float getDistanceTrigger() {
		return distanceTrigger;
	}

	public void setDistanceTrigger(float distanceTrigger) {
		this.distanceTrigger = distanceTrigger;
	}

	public float getVolumeScale() {
		return volumeScale;
	}

	public void setVolumeScale(float volumeScale) {
		this.volumeScale = volumeScale;
	}

	public boolean isPaused() {
		return isPaused;
	}

	public void setPaused(boolean isPaused) {
		this.isPaused = isPaused;
	}
}
