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

			// compute new velocity according to gravity
			b.vy -= g * deltaT;

			// compute new position according to the speed of the ball
			b.x += deltaT * b.vx;
			b.y += deltaT * b.vy;

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
		double distanceSq = xDiff * xDiff + yDiff * yDiff;

		double speedX = b1.vx - b0.vx;
		double speedY = b1.vy - b0.vy;
		double dotProduct = xDiff * speedX + yDiff * speedY;

		//dotproduct is positive when balls move towards each other
		if(dotProduct > 0){
			double scalar = dotProduct/distanceSq;
			double xColl = xDiff * scalar;
			double yColl = yDiff * scalar;

			double totalMass = b0.m + b1.m;
			double collisionMass0 = 2 * b1.m / totalMass;
			double collisionMass1 = 2 * b0.m / totalMass;

			b0.vx += (collisionMass0 * xColl);
			b0.vy += (collisionMass0 * yColl);

			b1.vx += (collisionMass1 * xColl);
			b1.vy += (collisionMass1 * yColl);
		}
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
}
