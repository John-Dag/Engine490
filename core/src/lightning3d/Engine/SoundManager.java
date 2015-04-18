package lightning3d.Engine;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class SoundManager {
	private Array<SoundEffect> environmentSounds;
	private Array<Long> activeSoundIds;
	
	public SoundManager(float distanceTrigger) {
		setSounds(new Array<SoundEffect>());
		activeSoundIds = new Array<Long>();
	}

	public Array<SoundEffect> getSounds() {
		return environmentSounds;
	}

	public void setSounds(Array<SoundEffect> sounds) {
		this.environmentSounds = sounds;
	}
	
	public void updateEnvironmentSounds(Vector3 playerPos) {
		SoundEffect sound = findClosestEnvironSound(playerPos);
		float distance = sound.getPosition().dst(playerPos);
		
		if (distance < sound.getDistanceTrigger()) {
			boolean isActive = false;
			
			for (Long id : activeSoundIds) {
				if (id == sound.getId())
					isActive = true;
			}
			
			if (!sound.isPlaying() && !isActive) {
				sound.setId(sound.getSound().play());
				activeSoundIds.add(sound.getId());
				sound.getSound().setLooping(sound.getId(), true);
				sound.setPlaying(true);
			}
			
			else {
				sound.setVolume(1 - (sound.getPosition().dst(playerPos) / sound.getDistanceTrigger()));
				sound.getSound().setVolume(sound.getId(), sound.getVolume());
			}
			
			if (sound.isPaused()) {
				sound.getSound().resume();
				sound.setPaused(false);
			}
		}
		
		if (distance > sound.getDistanceTrigger() && !sound.isPaused()) {
			sound.getSound().pause();
			sound.setPaused(true);
		}
	}
	
	public void pauseAllSounds() {
		for (SoundEffect sound : environmentSounds) {
			if (sound.isPlaying())
				sound.getSound().pause(sound.getId());
		}
	}
	
	public SoundEffect findClosestEnvironSound(Vector3 playerPos) {
		SoundEffect temp = environmentSounds.first();
		
		for (SoundEffect sound : environmentSounds) {
			if (sound.getPosition().dst(playerPos) < temp.getPosition().dst(playerPos)) {
				temp = sound;
			}
		}
		
		return temp;
	}
}
