import java.lang.Math;

public class Ray {
	
	Vector direction;
	Vector origin;
	Vector prp;
	double pixelToPrpDist;
	double near;
	double far;
	private static final int MAX_RGB_VALUE = 255;
	private static final int DEFAULT_COLOR = 255;

	// Constructor for use with rays from camera
	public Ray(Vector prp, Vector pixel, double nearArg, double farArg) {
		this.prp = prp;
		direction = (pixel.minus(prp)).normalize();
		origin = pixel;
		pixelToPrpDist = (pixel.minus(prp)).length();
		near = nearArg;
		far = farArg;
	}
	
	// Constructor for use with shadow checking rays
	public Ray(Vector origin, Vector direction) {
		this.direction = direction;
		this.origin = origin;
		prp = origin;
		pixelToPrpDist = 0;
		near = 0;
		far = 0;
	}
	
	public double trace(Color color, Color depth) {
		color.setAll(DEFAULT_COLOR);		// Default color if no intersection is found
		depth.setAll(0);		// Default depth if no intersection is found
		double nearestSphereIntersection = -1;
		Sphere sphereFinal = null;
		double vFinal = 0;
		double dFinal = 0;
		for (int i = 0; i < RayTracer.spheres.size(); ++i) {
			Sphere sphere = RayTracer.spheres.get(i);
			Vector prpToSphereCenter = new Vector(sphere.center.minus(prp));
			double v = prpToSphereCenter.dot(direction);
			double c = prpToSphereCenter.length();
			double b = Math.sqrt(c*c - v*v);
			double r = sphere.radius;
			if (b <= r) {
				double d = Math.sqrt(r*r - b*b);
				double intersection = v-d;
				if (intersection >= pixelToPrpDist && (intersection < nearestSphereIntersection || nearestSphereIntersection == -1)) {
					nearestSphereIntersection = intersection;
					sphereFinal = sphere;
					vFinal = v;
					dFinal = d;
				}
			}
		}
		if (nearestSphereIntersection != -1 && (near != 0 || far != 0)) {  // Don't enter if() if it's a shadow checker
			Vector Q = prp.plus(direction.times(vFinal-dFinal));  // World coordinates where ray intersects sphere
			setColor(color, sphereFinal, Q);
			depth.setAll((int)Math.round(MAX_RGB_VALUE * (1 - (nearestSphereIntersection-pixelToPrpDist)/(far-near))));	// Corrected for distance from prp to image plane
		}
		return nearestSphereIntersection;
	}
	
	private void setColor(Color sphereColor, Sphere sphere, Vector intersection) {
		// Ambient
		Color ambient = new Color(RayTracer.lights.get(0).color.times(sphere.material.Ka));
		
		// Diffuse and specular
		Color diffuse = new Color();
		Color specular = new Color();
		Vector surfaceNormal = (intersection.minus(sphere.center)).normalize();  // N in slides
		Vector directionToCamera = (prp.minus(intersection)).normalize();  // V in slides
		for (int i=1; i<RayTracer.lights.size(); ++i) {
			double distanceToLight = RayTracer.lights.get(i).position.minus(intersection).length();
			Vector directionToLight = (RayTracer.lights.get(i).position.minus(intersection)).normalize();  // L in slides
//			Ray shadowCheck = new Ray(intersection, directionToLight);
//			if (shadowCheck.trace(new Color(), new Color()) > distanceToLight) {
				Vector directionOfReflectedRay = ((surfaceNormal.times(2*(directionToLight.dot(surfaceNormal)))).minus(directionToLight)).normalize();  // R in slides
				if (surfaceNormal.dot(directionToLight) > 0) {
					diffuse = diffuse.add((RayTracer.lights.get(i).color.times(sphere.material.Kd)).times(surfaceNormal.dot(directionToLight)));
				}
				if (directionToCamera.dot(directionOfReflectedRay) > 0) {
					specular = specular.add((RayTracer.lights.get(i).color.times(sphere.material.Ks)).times(Math.pow(directionToCamera.dot(directionOfReflectedRay), sphere.material.Ns)));
				}
//			}
		}
		sphereColor.set(ambient.add(diffuse).add(specular));
	}
	
/*	private double nearestIntersection() {
		
	}*/
				
}