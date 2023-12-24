package com.art.fallball.engine;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import lombok.Getter;


public class World {

    @Getter
    private Array<Ball> balls;

    private Array<Vector2> mouseRecordPoints;


    //=====================================================
    public World () {
        balls = new Array<>();
        mouseRecordPoints = new Array<>();
    }

    public void update (float deltaTime) {
        for (Ball ball : balls) {
            ball.update(deltaTime);
            collisionCheck();
        }
    }

    public void render (ShapeRenderer renderer) {
        renderer.setColor(Color.GREEN);
        for (Ball ball : balls) {
            ball.draw(renderer);
        }
    }

    public void renderMisc (ShapeRenderer renderer) {
        renderer.setColor(Color.RED);
        for (Vector2 mouseRecordPoint : mouseRecordPoints) {
            renderer.circle(mouseRecordPoint.x, mouseRecordPoint.y, 1);
        }
    }
    //=====================================================

    //input related
    public void recordMousePos (float x, float y) {
        Vector2 pool = Pools.obtain(Vector2.class);
        pool.set(x, y);
        this.mouseRecordPoints.add(pool);
    }

    public void flushMouseRecording () {
        for (Vector2 mouseRecordPoint : mouseRecordPoints) {
            Pools.free(mouseRecordPoint);
        }
        this.mouseRecordPoints.clear();
    }

//    public void throwBall (V)


    public void spawnBallOnClick (float x, float y) {
        Ball ball = Pools.obtain(Ball.class);
        ball.setPosition(x, y);
        this.balls.add(ball);
    }
    //=====================================================

    //Collision checking
    public void collisionCheck () {
        for (int i = 0; i < balls.size; i++) {
            for (int j = i + 1; j < balls.size; j++) {
                Ball ball1 = balls.get(i);
                Ball ball2 = balls.get(j);

                if (Intersector.overlaps(ball1.getCollider(), ball2.getCollider())) {
                    resolveStaticCollision(ball1, ball2);
//                    inelasticCollision(ball1, ball2, 0.9f);
                    resolveDynamicCollision(ball1, ball2);
                }
            }
        }
    }

    private void resolveDynamicCollision (Ball ball1, Ball ball2) {
        Vector2 pos1 = ball1.getPos();
        Vector2 pos2 = ball2.getPos();

        Vector2 v1 = ball1.getVelocity();
        Vector2 v2 = ball2.getVelocity();

        float m1 = ball1.getMass();
        float m2 = ball2.getMass();


        float distance = pos1.dst(pos2);

        float nx = (pos2.x - pos1.x) / distance;
        float ny = (pos2.y - pos1.y) / distance;


        //tangent
        float tx = -ny;
        float ty = nx;

        // dot product tangent
        float dpTan1 = v1.x * tx + v1.y * ty;
        float dpTan2 = v2.x * tx + v2.y * ty;

        // dot product normalized
        float u1 = v1.x * nx + v1.y * ny;
        float u2 = v2.x * nx + v2.y * ny;


        //momentum conservation
        float resultV1 = (u1 * (m1 - m2) + 2f * m2 * u2) / (m1 + m2);
        float resultV2 = (u2 * (m2 - m1) + 2f * m1 * u1) / (m1 + m2);

        ball1.setVelocity(tx * dpTan1 + nx * resultV1, ty * dpTan1 + ny * resultV1);
        ball2.setVelocity(tx * dpTan2 + nx * resultV2, ty * dpTan2 + ny * resultV2);
    }
//
//    private void inelasticCollision (Ballzy ball1, Ballzy ball2, float restitution) {
//        Vector2 pos1 = ball1.getPos();
//        Vector2 pos2 = ball2.getPos();
//
//        Vector2 v1 = ball1.getVelocity();
//        Vector2 v2 = ball2.getVelocity();
//
//        float m1 = ball1.getMass();
//        float m2 = ball2.getMass();
//
//
//        float distance = pos1.dst(pos2);
//
//        float norX = (pos2.x - pos1.x) / distance;
//        float norY = (pos2.y - pos1.y) / distance;
//
//
//        //tangent
//        float tanX = -norY;
//        float tanY = norX;
//
//        // dot product tangent
//        float dpTan1 = v1.x * tanX + v1.y * tanY;
//        float dpTan2 = v2.x * tanX + v2.y * tanY;
//
//        // dot product normalized
//        float u1 = v1.x * norX + v1.y * norY;
//        float u2 = v2.x * norX + v2.y * norY;
//
//        float resultV1 = (restitution * m2 * (u2 - u1) + m1*u1 + m2*u2) / (m1 + m2);
//        float resultV2 = (restitution * m1 * (u1 - u2) + m1*u1 + m2*u2) / (m1 + m2);
//
//        ball1.setVelocity(tanX * dpTan1 + norX * resultV1, tanY * dpTan1 + norY * resultV1);
//        ball2.setVelocity(tanX * dpTan2 + norX * resultV2, tanY * dpTan2 + norY * resultV2);
//    }

    private void resolveStaticCollision (Ball ball1, Ball ball2) {
        Vector2 pos1 = ball1.getPos();
        Vector2 pos2 = ball2.getPos();

        float distanceBetweenCenters = pos1.dst(pos2);

        float overlap = 0.5f * (distanceBetweenCenters - ball1.getRadius() - ball2.getRadius());

        //displace
        pos1.x -= overlap * (pos1.x - pos2.x) / distanceBetweenCenters;
        pos1.y -= overlap * (pos1.y - pos2.y) / distanceBetweenCenters;

        pos2.x += overlap * (pos1.x - pos2.x) / distanceBetweenCenters;
        pos2.y += overlap * (pos1.y - pos2.y) / distanceBetweenCenters;
    }
    //=====================================================

}
