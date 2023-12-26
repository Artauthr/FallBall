package com.art.fallball.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import lombok.Setter;

public class WorldInteraction implements InputProcessor {
    @Setter
    private World worldRef;

    private Ball currentSelected;
    private boolean dragging; //dragging these ballz across your face

    private float lastMouseX;
    private float lastMouseY;
    private long lastTime;
    private float velocityX, velocityY;
    private final float THROW_MULTIPLIER = 6.4f;

    private Ball detectPressedBall (float x, float y) {
        for (Ball ball : worldRef.getBalls()) {
            if (ball.getCollider().contains(x, y)) {
                return ball;
            }
        }
        return null;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            dragging = true;
            Ball ball = detectPressedBall(screenX, Gdx.graphics.getHeight() - screenY);
            if (ball != null) {
                currentSelected = ball;
                currentSelected.setManualControl(true);
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            dragging = false;
            if (currentSelected != null) {
                currentSelected.setManualControl(false);
                currentSelected.setVelocity(velocityX, velocityY);
                currentSelected.getVelocity().scl(THROW_MULTIPLIER);
            }
            worldRef.flushMouseRecording();
            currentSelected = null;
        }

        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (dragging && currentSelected != null) {

            worldRef.recordMousePos(screenX, Gdx.graphics.getHeight() - screenY);
            currentSelected.setPosition(screenX, Gdx.graphics.getHeight() - screenY);

            long currentTime = System.currentTimeMillis();
            long timeDelta = currentTime - lastTime;

            if (timeDelta > 0) {
                velocityX = (screenX - lastMouseX) / timeDelta;
                velocityY = (lastMouseY - screenY) / timeDelta; // Invert the Y-axis velocity

                currentSelected.setVelocity(velocityX, velocityY);
                System.out.println("currentSelected = " + currentSelected.getVelocity());

                // Update last positions and time
                lastMouseX = screenX;
                lastMouseY = screenY;

                lastTime = currentTime;
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
