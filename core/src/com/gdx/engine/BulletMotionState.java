package com.gdx.engine;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;

public class BulletMotionState extends btMotionState {
    public Matrix4 transform = new Matrix4();
    
    @Override
    public void getWorldTransform (Matrix4 worldTrans) {
        worldTrans.set(transform);
    }
    
    @Override
    public void setWorldTransform (Matrix4 worldTrans) {
        transform.set(worldTrans);
    }
}
