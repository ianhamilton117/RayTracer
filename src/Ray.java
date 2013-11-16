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
			Vector prpToSphereCenter = new Vector(sphere.center.minus(prp));
			double v = prpToSphereCenter.dot(direction);
			double c = prpToSphereCenter.length();
			double b = Math.sqrt(c*c - v*v);
			double r = sphere.radius;
			if (b <= r) {
				double d = Math.sqrt(r*r - b*b);
				double intersection = v-d;
				if (intersection >= pixelToPrpDist && intersection < nearestIntersection) {
					nearestIntersection = intersection;
					Vector Q = prp.plus(direction.times(v-d));  // World coordinates where ray intersects sphere
					setColor(color, sphere, Q);
					depth.setAll((int)Math.round(MAX_RGB_VALUE * (1 - (intersection-pixelToPrpDist)/(far-near))));	// Corrected for distance from prp to image plane
				}
			}
		}		
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
			Vector directionToLight = (RayTracer.lights.get(i).position.minus(intersection)).normalize();  // L in slides
			Vector directionOfReflectedRay = ((surfaceNormal.times(2*(directionToLight.dot(surfaceNormal)))).minus(directionToLight)).normalize();  // R in slides
			if (surfaceNormal.dot(directionToLight) > 0) {
				diffuse = diffuse.add((RayTracer.lights.get(i).color.times(sphere.material.Kd)).times(surfaceNormal.dot(directionToLight)));
			}
			if (directionToCamera.dot(directionOfReflectedRay) > 0) {
				specular = specular.add((RayTracer.lights.get(i).color.times(sphere.material.Ks)).times(Math.pow(directionToCamera.dot(directionOfReflectedRay), sphere.material.Ns)));
			}
		}
		sphereColor.set(ambient.add(diffuse).add(specular));
	}
				
}