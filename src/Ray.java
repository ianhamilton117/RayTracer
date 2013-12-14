import java.lang.Math;
import Jama.Matrix;

public class Ray {
	
	public static boolean RAY_DEBUG = false;
	
	Vector direction;
	Vector origin;
	Vector prp;
	double pixelToPrpDist;
	double near;
	double far;
	int recursionDepth;
	private static final int MAX_RGB_VALUE = 255;
	private static final int DEFAULT_COLOR = 0;

	// Constructor for use with rays from camera
	public Ray(Vector prp, Vector pixel, double nearArg, double farArg, int recursionDepth) {
		this.prp = prp;
		this.recursionDepth = recursionDepth;
		direction = (pixel.minus(prp)).normalize();
		origin = pixel;
		pixelToPrpDist = (pixel.minus(prp)).length();
		near = nearArg;
		far = farArg;
	}
	
	// Constructor for use with reflected rays
	public Ray(Vector origin, Vector direction, int recursionDepth) {
		prp = origin;
		this.origin = origin;
		this.direction = direction;
		pixelToPrpDist = 0;
		near = 0;
		far = 0;
		this.recursionDepth = recursionDepth;
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
				double intersection = v-d;  // intersection is the distance from the prp to the point of intersection
				if (intersection >= pixelToPrpDist && (intersection < nearestSphereIntersection || nearestSphereIntersection == -1)) {
					nearestSphereIntersection = intersection;
					sphereFinal = sphere;
					vFinal = v;
					dFinal = d;
				}
			}
		}
		
		double nearestTriangleIntersection = -1;
		double t = 0;  // t will be the distance from the PIXEL to the point of intersection
		Face triangleFinal = null;
		for (int i=0; i<RayTracer.groups.size(); ++i) {
			Group group = RayTracer.groups.get(i);
			for (int j=0; j<group.faces.size(); ++j) {
				Face face = group.faces.get(j);
				for (int k=0; k<face.triangles.size(); ++k) {
					Face triangle = face.triangles.get(k);
/*					if (triangle.normal.dot(triangle.points.get(0)) < 0) {
						triangle.normal = triangle.normal.times(-1);
					}*/
					Vector Q = prp.plus(direction.times(t + pixelToPrpDist));  // World coordinates where ray intersects triangle
					if (Q.dot(triangle.normal) < 0)
						triangle.correctedNormal = triangle.normal;
					else
						triangle.correctedNormal = triangle.normal.times(-1);
					t = -((triangle.normal).dot(origin) + triangle.d) / (triangle.normal).dot(direction);
					double[][] array1 = {{triangle.A.x, triangle.B.x, -direction.x},
							             {triangle.A.y, triangle.B.y, -direction.y},
							             {triangle.A.z, triangle.B.z, -direction.z}};
					double[][] array2 = {{origin.x - triangle.points.get(1).x},
							             {origin.y - triangle.points.get(1).y},
							             {origin.z - triangle.points.get(1).z}};
					Matrix matrix1 = new Matrix(array1);
					Matrix matrix2 = new Matrix(array2);
					try {
						Matrix result = matrix1.inverse().times(matrix2);
						double insideCheck = result.get(0, 0) + result.get(1, 0);
						if (insideCheck <= 1 && insideCheck > 0 && result.get(0, 0) >= 0 && result.get(1, 0) >= 0) {
							double intersection = t + pixelToPrpDist;  // intersection is the distance from the prp to the point of intersection
							if (intersection >= pixelToPrpDist && (intersection < nearestTriangleIntersection || nearestTriangleIntersection == -1)) {
								nearestTriangleIntersection = intersection;
								triangleFinal = triangle;
							}
						}
					} catch (RuntimeException e) {
						// Singular matrix
					}
				}
			}
		}

		
		// If the ray hit a sphere
		if (nearestSphereIntersection != -1 && (nearestTriangleIntersection == -1 || nearestSphereIntersection <= nearestTriangleIntersection)) {
			if (near != 0 || far != 0) {  // Don't enter if() if it's a shadow checker
				Vector Q = prp.plus(direction.times(vFinal-dFinal));  // World coordinates where ray intersects sphere
				setColor(color, sphereFinal, Q);
				depth.setAll((int)Math.round(MAX_RGB_VALUE * (1 - (nearestSphereIntersection-pixelToPrpDist)/(far-near))));	 // Corrected for distance from prp to image plane
			}
			return nearestSphereIntersection;
		}
		// If the ray hit a triangle
		else if (nearestTriangleIntersection != -1 && (nearestSphereIntersection == -1 || nearestTriangleIntersection <= nearestSphereIntersection)) {
			if (near != 0 || far != 0) {  // Don't enter if() if it's a shadow checker
				Vector Q = prp.plus(direction.times(t + pixelToPrpDist));
				setColor(color, triangleFinal, Q);
				depth.setAll((int)Math.round(MAX_RGB_VALUE * (1 - (nearestTriangleIntersection-pixelToPrpDist)/(far-near))));  // Corrected for distance from prp to image plane
			}
			return nearestTriangleIntersection;
		}
		return -1;
	}
	
	// TODO some instances of prp may need to be replaced by origin or something for reflection or refraction rays
	private void setColor(Color sphereColor, Sphere sphere, Vector intersection) {
		// Ambient
		Color ambient = new Color(RayTracer.lights.get(0).color.times(sphere.material.Ka));
		
		// Diffuse and specular
		Color diffuse = new Color();
		Color specular = new Color();
		Vector surfaceNormal = (intersection.minus(sphere.center)).normalize();  // N in slides
		Vector directionToCamera = (prp.minus(intersection)).normalize();  // V in slides
		// TODO Implement lights at infinity
		for (int i=1; i<RayTracer.lights.size(); ++i) {
			double distanceToLight = RayTracer.lights.get(i).position.minus(intersection).length();
			Vector directionToLight = (RayTracer.lights.get(i).position.minus(intersection)).normalize();  // L in slides
			Ray shadowCheck = new Ray(intersection, directionToLight);
			double check = shadowCheck.trace(new Color(), new Color());
			if (check > distanceToLight || check == -1) {
				Vector directionOfReflectedRay = ((surfaceNormal.times(2*(directionToLight.dot(surfaceNormal)))).minus(directionToLight)).normalize();  // R in slides
				if (surfaceNormal.dot(directionToLight) > 0) {
					diffuse = diffuse.add((RayTracer.lights.get(i).color.times(sphere.material.Kd)).times(surfaceNormal.dot(directionToLight)));
				}
				if (directionToCamera.dot(directionOfReflectedRay) > 0) {
					specular = specular.add((RayTracer.lights.get(i).color.times(sphere.material.Ks)).times(Math.pow(directionToCamera.dot(directionOfReflectedRay), sphere.material.Ns)));
				}
			}
		}
		
		Color reflectionColor = new Color(0, 0, 0);
		if (recursionDepth > 1) {
			Vector reflectionDirection = ((surfaceNormal.times(2*(direction.times(-1).dot(surfaceNormal)))).minus(direction.times(-1))).normalize();
			reflectionColor = new Color();
			Color reflectionDepth = new Color();  // Junk object
			Ray reflectionRay = new Ray(intersection, reflectionDirection, recursionDepth-1);
			double distance = reflectionRay.trace(reflectionColor, reflectionDepth);
			reflectionColor = reflectionColor.times(sphere.material.Kr);
			if (distance == -1) {
				reflectionColor.setAll(0);
			}
		}
		else
			reflectionColor.setAll(0);
		sphereColor.set(ambient.add(diffuse).add(specular).add(reflectionColor));
	}
	
	private void setColor(Color triangleColor, Face triangle, Vector intersection) {
		// Ambient
		Color ambient = new Color(RayTracer.lights.get(0).color.times(triangle.material.Ka));
		
		// Diffuse and specular
		Color diffuse = new Color();
		Color specular = new Color();
		Vector surfaceNormal = triangle.correctedNormal;
		Vector directionToCamera = (prp.minus(intersection)).normalize();
		for (int i=1; i<RayTracer.lights.size(); ++i) {
			double distanceToLight = RayTracer.lights.get(i).position.minus(intersection).length();
			Vector directionToLight = (RayTracer.lights.get(i).position.minus(intersection)).normalize();
/*			if (prp.dot(triangle.normal) < 0)
				surfaceNormal = triangle.normal;
			else
				surfaceNormal = triangle.normal.times(-1);
*/	//		Ray shadowCheck = new Ray(intersection, directionToLight);
//			double check = shadowCheck.trace(new Color(), new Color());
//			if (check > distanceToLight || check == -1) {
				Vector directionOfReflectedRay = ((surfaceNormal.times(2*(directionToLight.dot(surfaceNormal)))).minus(directionToLight)).normalize();  // TODO Maybe need to switch normal
				if (surfaceNormal.dot(directionToLight) > 0) {
					diffuse = diffuse.add((RayTracer.lights.get(i).color.times(triangle.material.Kd)).times(surfaceNormal.dot(directionToLight)));
				} 
				//TODO There's red in the green!!! (with shadowCheck commented out)
				if (directionToCamera.dot(directionOfReflectedRay) > 0) {
					specular = specular.add((RayTracer.lights.get(i).color.times(triangle.material.Ks)).times(Math.pow(directionToCamera.dot(directionOfReflectedRay), triangle.material.Ns)));
				}
//			}
		}
		triangleColor.set(ambient.add(diffuse).add(specular));
	}
	
/*	private double nearestIntersection() {
		
	}*/
	
}