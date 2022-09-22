package bouncing_balls;
import java.lang.Math.*;

/**
 * The physics model.
 *
 * This class is where you should implement your bouncing balls model.
 *
 * The code has intentionally been kept as simple as possible, but if you wish, you can improve the design.
 *
 * @author Simon Robillard
 *
 */
class Model {

	double areaWidth, areaHeight;

	Ball [] balls;

	Model(double width, double height) {
		areaWidth = width;
		areaHeight = height;

		// Initialize the model with a few balls
		balls = new Ball[2];
		balls[0] = new Ball(width / 3, height * 0.9, 1.2, 1.6, 0.2, 0.4); 		// add dx, dy, ddx, ddy, m params
		balls[1] = new Ball(width / 3, height * 0.7, -0.6, 0.6, 0.3, 1.4); 		// add dx, dy, ddx, ddy, m params
		// balls[1] = new Ball(2 * width / 3, height * 0.7, -0.6, 0.6, 0.3, 0.7); 	// add dx, dy, ddx, ddy, m params
	}

	void step(double deltaT) {
		// TODO this method implements one step of simulation with a step deltaT
		Ball b0 = balls[0];
		Ball b1 = balls[1];
		double g = 9.82;

		for (Ball b : balls) {
			// detect collision with the border
			if (b.x > areaWidth - b.radius) {
				b.x = areaWidth - b.radius;
				b.vx *= -1; // change direction of ball
			}
			else if (b.x < b.radius) {
				b.x = b.radius;
				b.vx *= -1;
			}
			else if (b.y > areaHeight - b.radius) {
				b.y = areaHeight - b.radius;
				b.vy *= -1;
			}
			else if (b.y < b.radius) {
				b.y = b.radius;
				b.vy *= -1;
			}

			// compute new position according to the speed of the ball
			b.x += deltaT * b.vx;
			b.y += deltaT * b.vy;

			// compute new velocity according to gravity
			b.vy -= g * deltaT;

		}

		// yahoo collision detecT)
		double xDiff = b0.x - b1.x;
		double yDiff = b0.y - b1.y;
		double distanceSq = xDiff * xDiff + yDiff * yDiff;

		boolean collision = distanceSq < (b0.radius + b1.radius) * (b0.radius + b1.radius);

		if(collision){
			collisionHandler(b0, b1);
		}

	}

	void collisionHandler(Ball b0, Ball b1){
		double xDiff = b0.x - b1.x;
		double yDiff = b0.y - b1.y;

		double speedX = b1.vx - b0.vx;
		double speedY = b1.vy - b0.vy;
		double dotProduct = xDiff * speedX + yDiff * speedY;

		//If dotproduct is negative the balls are moving away from each other (no collision)
		if(dotProduct < 0){
			return;
		}

		//Angle between line of collision and x-axis
		double alpha = Math.atan2(yDiff, xDiff);

		//Rotation of vectors (basically linear transformation)
		double b0vx = b0.vx * Math.cos(alpha) + b1.vy * Math.sin(alpha);
		double b0vy = b0.vy * Math.cos(alpha) - b0.vx * Math.sin(alpha);

		double b1vx = b1.vx * Math.cos(alpha) + b1.vy * Math.sin(alpha);
		double b1vy = b1.vy * Math.cos(alpha) - b1.vy * Math.sin(alpha);

		Vector impact1 = collision1D(b0vx, b1vx, b0.m, b1.m);
		Vector impact2 = collision1D(b0vy, b1vy, b0.m, b1.m);

		//
		double newB0vx = impact1.x * Math.cos(alpha) - b0vy * Math.sin(alpha);
		double newB0vy = impact1.y * Math.sin(alpha) + b0vy * Math.cos(alpha);

		double newB1vx = impact2.x * Math.cos(alpha) - b1vy * Math.sin(alpha);
		double newB1vy = impact2.y * Math.sin(alpha) + b1vy * Math.cos(alpha);

		b0.vx = newB0vx;
		b0.vy = newB0vy;
		b1.vx = newB1vx;
		b1.vy = newB1vy;


	}

	//Math taken from https://en.wikipedia.org/wiki/Elastic_collision
	Vector collision1D(double b0U, double b1U, double m0, double m1){
		double b0V, b1V;

		b0V = (m0 - m1)/(m0 + m1) * b0U+ (2 * m1)/(m0 + m1) * b1U;
		b1V = (2 * m0)/(m0 + m1) * b0U + (m1 - m0)/(m0 + m1) * b1U;

		return new Vector(b0V, b1V);
	}

	double[] rectToPolar (double x, double y) {
		double[] polar = new double[2];

		double r = Math.sqrt(x*x + y*y);
		double theta = Math.atan2(y, x);

		polar[0] = r;
		polar[1] = theta;

		// System.out.println("(r, theta) = " + "(" +  r + ", " + theta + ")");

		return polar;
	}

	double[] polarToRect(double r, double theta){
		double[] rect = new double[2];
		double x;
		double y;

		x = r * Math.cos(theta);
		y = r * Math.sin(theta);

		rect[0] = x;
		rect[1] = y;

		// System.out.println("(x, y) = " + "(" +  x + ", " + y + ")");

		return rect;
	}

	/**
	 * Simple inner class describing balls.
	 */
	class Ball {
		// , , double ux, double uy, double dx, double dy, double ddx, double ddy
		Ball(double x, double y, double vx, double vy, double r, double m) {
			this.x = x;
			this.y = y;
			this.vx = vx;
			this.vy = vy;
			this.radius = r;
			this.m = m;
			// this.ux = ux;
			// this.uy = uy;
		}

		/**
		 * Position, speed, and radius of the ball. You may wish to add other attributes.
		 */
		// added x'=dx, y'=dy, x''=ddx, and y''=ddy, m = mass
		// , ux, uy
		double x, y, vx, vy, radius, m;
	}

	// Simple 2D vector to dodge using cheaterman libs
	class Vector{
		double x, y;

		Vector(double x, double y){
			this.x = x;
			this.y = y;
		}
	}
}
