package com.art.fallball.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import lombok.Getter;
import lombok.Setter;

public class Ball implements Pool.Poolable {

    @Getter
    private Vector2 pos;

    @Getter
    private Vector2 velocity;

    @Getter
    private final float radius = 30;

    @Getter
    private float mass;

    @Getter
    @Setter
    private Circle collider;

    @Getter
    private int id;
    private boolean manualControl = false;



    public Ball () {
        pos = new Vector2();

        collider = new Circle();
        collider.setRadius(radius);

        velocity = new Vector2();

        mass = radius * 0.5f;

        id = Params.ballCounter++;
    }



    public void update (float deltaTime) {
        if (Params.GRAVITY_ENABLED) {
            velocity.y += Params.GRAV * deltaTime;
        }
        checkNearZero();
        pos.add(velocity.x, velocity.y);
        collider.setPosition(pos.x, pos.y);

        if (pos.y - radius <= 0) {
            pos.y = radius;

            velocity.y *= -1;
            applyBouncePenaltyY();
        }

        int height = Gdx.graphics.getHeight();
        if (pos.y + radius >= height) {
            pos.y = height - radius;
            velocity.y *= -1;
            applyBouncePenaltyY();
        }

        if (pos.x - radius <= 0) {
            pos.x = radius;
            velocity.x *= -1f;
            applyBouncePenaltyX();
        }

        int width = Gdx.graphics.getWidth();
        if (pos.x + radius >= width) {
            pos.x = width - radius;
            velocity.x *= -1f;
            applyBouncePenaltyX();
        }

        //damp
        velocity.scl(Params.DAMPING_FACTOR);
    }


    public void setPosition (float x, float y) {
        this.pos.set(x, y);
        this.collider.setPosition(x, y);
    }


    public void applyBouncePenaltyY() {
        velocity.y *= Params.BOUNCE_PENALTY;
    }

    public void applyBouncePenaltyX () {
        velocity.x *= Params.BOUNCE_PENALTY;
    }


    private void checkNearZero() {
//        if (velocity.x + velocity.y <= Params.LOWEST_SPEED) {
//            velocity.setZero();
//        }
    }

    public void setManualControl (boolean manualControl) {
        this.manualControl = manualControl;
        velocity.setZero();
    }


    public void draw (ShapeRenderer renderer) {
        renderer.setColor(Color.GREEN);
        renderer.circle(pos.x, pos.y, radius);
    }

    public void setVelocity (float x, float y) {
        this.velocity.set(x, y);
    }

    @Override
    public void reset() {
        velocity.setZero();
        pos.setZero();
    }

}
