
import acm.graphics.GOval;
import acm.util.RandomGenerator;
import com.shpp.cs.a.graphics.WindowProgram;

import java.util.*;

/**
 * Program implements animation. randomly bouncing balls in window.
 * Balls are repelled from the edges of the window and from each other.
 * Balls are created in random places in window with random direction and acceleration.
 */
public class BollBouncing extends WindowProgram {

    /**
     * Window main menu height param set in pixels
     **/
    public static final int MENU_HEIGHT = 23;
    /**
     * Window size params for resizing
     **/
    public static final int APPLICATION_WIDTH = 600;
    public static final int APPLICATION_HEIGHT = 600 + MENU_HEIGHT; // adding menu height for set correct canvas size

    /* Value of balls count on canvas */
    private static final int BALLS_COUNT = 10; // can't be 0. Minimum 1 ball should be on canvas.
    /* The size of each ball. */
    private static final double BALL_SIZE = 50;

    /* The amount of time to pause between frames. Depends on balls count.
     * The more balls the less pause time. */
    private static final double PAUSE_TIME = 1000.0 / (100 * BALLS_COUNT);
    /* Program execution time limit */
    private static final int MILLIS_LIMIT = 5000;

    /* Gravitation acceleration. */
    private static final double GRAVITY = 0.5;

    /* Elasticity of balls */
    private static final double ELASTICITY = 0.9;

    /* Variable for start in milliseconds */
    private double START_MILLIS;

    /* Array for collect all balls on canvas.*/
    private final GOval[] balls = new GOval[BALLS_COUNT];
    /* Array for collect double values of acceleration direction (dx, dy) for each ball. */
    private final double[][] ballsCoefficients = new double[BALLS_COUNT][];

    /**
     * Entry point for execute balls random bounce program.
     */
    public void run() {
        addRandomBallsToCanvas();
        addBallsCoefficients();
        bounceBalls();
    }

    /**
     * Method for adding balls to canvas.
     * Ball count set in static params of class.
     * Method set each ball random color.
     * Method add this ball to array of all balls.
     */
    private void addRandomBallsToCanvas() {
        for (int i = 0; i < BALLS_COUNT; i++) {
            GOval ball = new GOval(
                    new Random().nextInt(getWidth()),
                    new Random().nextInt(getHeight()),
                    BALL_SIZE,
                    BALL_SIZE);
            ball.setFilled(true);
            ball.setFillColor(RandomGenerator.getInstance().nextColor());
            this.balls[i] = ball;
            add(ball);
        }
    }

    /**
     * Method set random start direction and
     * create and write bounce ball coefficient to array of coefficients.
     */
    private void addBallsCoefficients() {
        START_MILLIS = System.currentTimeMillis();
        for (int i = 0; i < this.ballsCoefficients.length; i++) {
            double dx = new Random().nextInt(2) == 1 ? // choose random start ball direction
                    new Random().nextInt(10) + 10 : (-1) * new Random().nextInt(10) + 10; // 10 < coef. < 20
            double dy = new Random().nextInt(2) == 1 ? // choose random start ball direction
                    new Random().nextInt(10) + 10 : (-1) * new Random().nextInt(10) + 10; // 10 < coef. < 20
            this.ballsCoefficients[i] = new double[]{dx, dy}; // set new double array of ceofficients
        }
    }

    /**
     * Method for bouncing balls for time which set in class params.
     */
    private void bounceBalls() {
        while (System.currentTimeMillis() - START_MILLIS <= MILLIS_LIMIT) {
            for (int i = 0; i < balls.length; i++) { // iterated by balls coefficients
                bounceBall(i);
            }
        }
    }

    /**
     * Simulate a bouncing ball.
     *
     * @param index index of ball in balls array and coefficients array.
     */
    private void bounceBall(int index) {
        /* Move the ball by velocity. */
        this.balls[index].move(this.ballsCoefficients[index][0], this.ballsCoefficients[index][1]);

        /* Add gravity to ball velocity coefficient */
        this.ballsCoefficients[index][1] += GRAVITY;

        ballToSideCollision(index);
        ballToBallCollision(index);
        pause(PAUSE_TIME);
    }

    /**
     * Method for check ball-to-SIDE collision by iterated by ball array and compare position of current ball
     * and position of each side of window.
     *
     * @param index current ball index in array of balls
     */
    private void ballToSideCollision(int index) {
        /* Ball below FLOR condition */
        if (ballBelowFloor(this.balls[index]) && this.ballsCoefficients[index][1] > 0) {
            this.ballsCoefficients[index][1] *= -ELASTICITY; // invert directions of movement
        }
        /* Ball over TOP condition */
        if (ballOverTop(this.balls[index]) && this.ballsCoefficients[index][1] < 0) {
            this.ballsCoefficients[index][1] *= -ELASTICITY; // invert directions of movement
        }
        /* Ball outside RIGHT side */
        if (ballOutsideRight(this.balls[index]) && this.ballsCoefficients[index][0] > 0) {
            this.ballsCoefficients[index][0] *= -ELASTICITY; // invert directions of movement
        }
        /* Ball outside LEFT side */
        if (ballOutsideLeft(this.balls[index]) && this.ballsCoefficients[index][0] < 0) {
            this.ballsCoefficients[index][0] *= -ELASTICITY; // invert directions of movement
        }
    }


    /**
     * Method for check ball-to-ball collision by iterated by ball array and compare position of current ball
     * and position of all another balls by coordinates.
     *
     * @param index current ball index in array of balls
     */
    private void ballToBallCollision(int index) {
        for (int i = 0; i < balls.length; i++) {
            if (i != index) { // check if current ball is not target ball of iteration
                // condition when ball 2 in the RIGHT UNDER position of  ball 1
                //▄▀▀▀▄
                //█ 1 █
                // ▀▀▀▄▀▀▀▄
                //    █ 2 █
                //     ▀▀▀
                if (ballToBallSecondRightUnderFirstCheck(index, i)) {
                    if (ballsCoefficients[index][0] < 0) ballsCoefficients[index][0] *= -ELASTICITY;
                    if (ballsCoefficients[index][1] < 0) ballsCoefficients[index][1] *= -ELASTICITY;
                    if (ballsCoefficients[i][0] > 0) ballsCoefficients[i][0] *= -ELASTICITY;
                    if (ballsCoefficients[i][1] > 0) ballsCoefficients[i][1] *= -ELASTICITY;
                    setRandomColorForBalls(balls[index], balls[i]);
                }
                // condition when ball 2 in the LEFT UNDER position of  ball 1
                //    ▄▀▀▀▄
                //    █ 1 █
                //▄▀▀▀▄▀▀▀
                //█ 2 █
                // ▀▀▀
                if (ballToBallSecondLeftUnderFirstCheck(index, i)) {
                    if (ballsCoefficients[index][0] > 0) ballsCoefficients[index][0] *= -ELASTICITY;
                    if (ballsCoefficients[index][1] < 0) ballsCoefficients[index][1] *= -ELASTICITY;
                    if (ballsCoefficients[i][0] < 0) ballsCoefficients[i][0] *= -ELASTICITY;
                    if (ballsCoefficients[i][1] > 0) ballsCoefficients[i][1] *= -ELASTICITY;
                    setRandomColorForBalls(balls[index], balls[i]);
                }
                // condition when ball 2 in the LEFT TOP position of  ball 1
                //▄▀▀▀▄
                //█ 2 █
                // ▀▀▀▄▀▀▀▄
                //    █ 1 █
                //     ▀▀▀
                if (ballToBallSecondLeftTopLeftCheck(index, i)) {
                    if (ballsCoefficients[i][0] < 0) ballsCoefficients[i][0] *= -ELASTICITY;
                    if (ballsCoefficients[i][1] < 0) ballsCoefficients[i][1] *= -ELASTICITY;
                    if (ballsCoefficients[index][0] > 0) ballsCoefficients[index][0] *= -ELASTICITY;
                    if (ballsCoefficients[index][1] > 0) ballsCoefficients[index][1] *= -ELASTICITY;
                    setRandomColorForBalls(balls[index], balls[i]);
                }
                // condition when ball 2 in the RIGHT TOP position of  ball 1
                //    ▄▀▀▀▄
                //    █ 2 █
                //▄▀▀▀▄▀▀▀
                //█ 1 █
                // ▀▀▀
                if (ballToBallSecondRightTopLeftCheck(index, i)) {
                    if (ballsCoefficients[i][0] > 0) ballsCoefficients[i][0] *= -ELASTICITY;
                    if (ballsCoefficients[i][1] < 0) ballsCoefficients[i][1] *= -ELASTICITY;
                    if (ballsCoefficients[index][0] < 0) ballsCoefficients[index][0] *= -ELASTICITY;
                    if (ballsCoefficients[index][1] > 0) ballsCoefficients[index][1] *= -ELASTICITY;
                    setRandomColorForBalls(balls[index], balls[i]);
                }
            }
        }
    }

    /**
     * Method set random color to ball
     *
     * @param ballsToSetColor for this ball will be given random color
     */
    private void setRandomColorForBalls(GOval... ballsToSetColor) {
        for (GOval ball : ballsToSetColor) {
            ball.setFilled(true);
            ball.setFillColor(RandomGenerator.getInstance().nextColor());
        }
    }

    /**
     * Determines whether the ball has dropped below floor level.
     *
     * @param ball ball to test.
     * @return Whether it's fallen below the floor.
     */
    private boolean ballBelowFloor(GOval ball) {
        return ball.getY() + ball.getHeight() >= getHeight();
    }

    /**
     * Determines whether the ball has grows over top level.
     *
     * @param ball ball to test.
     * @return Whether it's grows over the top.
     */
    private boolean ballOverTop(GOval ball) {
        return ball.getY() <= 0;
    }

    /**
     * Determines whether the ball outside RIGHT side
     *
     * @param ball ball to test.
     * @return Whether it's move outside RIGHT side.
     */
    private boolean ballOutsideRight(GOval ball) {
        return ball.getX() + ball.getHeight() >= getWidth();
    }

    /**
     * Determines whether the ball outside LEFT side
     *
     * @param ball ball to test.
     * @return Whether it's move outside LEFT side.
     */
    private boolean ballOutsideLeft(GOval ball) {
        return ball.getX() <= 0;
    }

    /**
     * Method for check ball-to-ball collision when second ball in the RIGHT UNDER position
     * of first ball.
     *
     * @param index index of second ball
     * @param i     index of first ball (get from loop for all balls array)
     * @return true when ball is collide
     */
    private boolean ballToBallSecondRightUnderFirstCheck(int index, int i) {
        return balls[index].getX() > balls[i].getX() &&
                balls[index].getX() < balls[i].getX() + BALL_SIZE &&
                balls[index].getY() > balls[i].getY() &&
                balls[index].getY() < balls[i].getY() + BALL_SIZE;
    }

    /**
     * Method for check ball-to-ball collision when second ball in the LEFT UNDER position
     * of first ball.
     *
     * @param index index of second ball
     * @param i     index of first ball (get from loop for all balls array)
     * @return true when ball is collide
     */
    private boolean ballToBallSecondLeftUnderFirstCheck(int index, int i) {
        return balls[index].getX() + BALL_SIZE > balls[i].getX() &&
                balls[index].getX() + BALL_SIZE < balls[i].getX() + BALL_SIZE &&
                balls[index].getY() > balls[i].getY() &&
                balls[index].getY() < balls[i].getY() + BALL_SIZE;
    }

    /**
     * Method for check ball-to-ball collision when second ball in the LEFT TOP position
     * of first ball.
     *
     * @param index index of second ball
     * @param i     index of first ball (get from loop for all balls array)
     * @return true when ball is collide
     */
    private boolean ballToBallSecondLeftTopLeftCheck(int index, int i) {
        return balls[i].getX() > balls[index].getX() &&
                balls[i].getX() < balls[index].getX() + BALL_SIZE &&
                balls[i].getY() > balls[index].getY() &&
                balls[i].getY() < balls[index].getY() + BALL_SIZE;
    }

    /**
     * Method for check ball-to-ball collision when second ball in the RIGHT TOP position
     * of first ball.
     *
     * @param index index of second ball
     * @param i     index of first ball (get from loop for all balls array)
     * @return true when ball is collide
     */
    private boolean ballToBallSecondRightTopLeftCheck(int index, int i) {
        return balls[i].getX() + BALL_SIZE > balls[index].getX() &&
                balls[i].getX() + BALL_SIZE < balls[i].getX() + BALL_SIZE &&
                balls[i].getY() > balls[index].getY() &&
                balls[i].getY() < balls[index].getY() + BALL_SIZE;
    }
}
