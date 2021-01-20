package lightning3d.Engine;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class SoundManager {
	private Array<SoundEffect> environmentSounds;
	private Array<Long> activeSoundIds;
	
	public SoundManager() {
		setSounds(new Array<SoundEffect>());
		activeSoundIds = new Array<Long>();
	}

	public Array<SoundEffect> getSounds() {
		return environmentSounds;
	}

	public void setSounds(Array<SoundEffect> sounds) {
		this.environmentSounds = sounds;
	}
	
	public void updateEnvironmentSounds(Vector3 playerPos, Vector3 playerDirection) {
		SoundEffect sound = findClosestEnvironSound(playerPos);
		if (sound == null)
			return;
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
				Vector2 v = new Vector2(sound.getPosition().x - playerPos.x, sound.getPosition().z - playerPos.z);
				Vector2 u = new Vector2(playerDirection.x, playerDirection.z);
				v.nor();
				u.nor();
				Vector3 up = new Vector3(0f, 1f, 0f);
				Vector3 u3 = new Vector3(u.x, 0, u.y);
				Vector3 leftvec = u3.crs(up);
				leftvec.nor();
				float pan = Vector2.dot(leftvec.x, leftvec.z, v.x, v.y);
				sound.getSound().setPan(sound.getId(), pan, sound.getVolume());
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
		if (environmentSounds.size == 0)
			return null;
		SoundEffect temp = environmentSounds.first();
		
		for (SoundEffect sound : environmentSounds) {
			if (sound.getPosition().dst(playerPos) < temp.getPosition().dst(playerPos)) {
				temp = sound;
			}
		}
		
		return temp;
	}
}
