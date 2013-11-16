import java.lang.Math;

public class Ray {
	
	Vector direction;
	Vector origin;
	Vector prp;
	double pixelToPrpDist;
	double near;
	double far;
	private static final int MAX_RGB_VALUE = 255;

	public Ray(Vector prp, Vector pixel, double nearArg, double farArg) {
		this.prp = prp;
		direction = (pixel.minus(prp)).normalize();
		origin = pixel;
		pixelToPrpDist = (pixel.minus(prp)).length();
		near = nearArg;
		far = farArg;
	}
	
	public void trace(Color color, Color depth) {
		color.setAll(MAX_RGB_VALUE);		// Default color if no intersection is found
		depth.setAll(0);		// Default depth if no intersection is found
		double nearestIntersection = far;
		for (int i = 0; i < RayTracer.spheres.size(); ++i) {
			Sphere sphere = RayTracer.spheres.get(i);
			Vector sphereCenter = new Vector(sphere.center.minus(prp));
			double v = sphereCenter.dot(direction);
			double c = sphereCenter.length();
			double b = Math.sqrt(c*c - v*v);
			double r = sphere.radius;
			if (b <= r) {
				double d = Math.sqrt(r*r - b*b);
				double intersection = v-d;
				if (intersection >= pixelToPrpDist && intersection < nearestIntersection) {
					nearestIntersection = intersection;
					color.set(new Color());
					depth.setAll((int)Math.round(MAX_RGB_VALUE * (1 - (intersection-pixelToPrpDist)/(far-near))));	// Corrected for distance from prp to image plane
				}
			}
		}		
	}
				
}