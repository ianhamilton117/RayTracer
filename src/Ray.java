import java.lang.Math;
import Jama.Matrix;

public class Ray {
	
	public static boolean RAY_DEBUG = false;
	
	Vector origin;  // Origin of the ray, not the origin of world coordinates
	Vector direction;
	int recursionDepth;
	private static final int MAX_RGB_VALUE = 255;
	private static final int DEFAULT_COLOR = 0;  // Background color for the scene

	// Constructor for use with rays from camera
	public Ray(Vector origin, Vector direction, int recursionDepth) {
		this.origin = origin;
		this.direction = direction.normalize();
		this.recursionDepth = recursionDepth;
	}
	
	public double trace(Color color) {
		
/*		if (RAY_DEBUG == true) {
			int i = 1;
			if(1==i){}  // Just here to get a breakpoint for debugging
//			System.out.println("poo");
		}*/
		
		color.setAll(DEFAULT_COLOR);		// Default color if no intersection is found
		
		double nearestSphereDistance = -1;
		Sphere sphereFinal = null;
		for (int i = 0; i < RayTracer.spheres.size(); ++i) {
			Sphere sphere = RayTracer.spheres.get(i);
			Vector originToSphereCenter = new Vector(sphere.center.minus(origin));
			double v = originToSphereCenter.dot(direction);
			if (v >= 0) {  // If the sphere is in front of the ray, not behind
				double c = originToSphereCenter.length();
				double b = Math.sqrt(c*c - v*v);
				double r = sphere.radius;
				if (b <= r) {  // If the ray hit the sphere
					double d = Math.sqrt(r*r - b*b);
					double distance = v-d;  // The distance from the origin to the point of intersection (distance the ray traveled)
					if (distance >= 0.000000001 && (distance < nearestSphereDistance || nearestSphereDistance == -1)) {  // If this is the closest intersection so far
						nearestSphereDistance = distance;
						sphereFinal = sphere;
					}
				}
			}
		}
		
		double nearestTriangleDistance = -1;
		Face triangleFinal = null;
		Vector triangleNormalFinal = null;
		for (int i=0; i<RayTracer.groups.size(); ++i) {
			Group group = RayTracer.groups.get(i);
			for (int j=0; j<group.faces.size(); ++j) {
				Face face = group.faces.get(j);
				for (int k=0; k<face.triangles.size(); ++k) {
					Face triangle = face.triangles.get(k);
					Vector triangleNormal = triangle.normal;
					if (direction.dot(triangleNormal) > 0)
						triangleNormal = triangleNormal.times(-1);  // Ensure the normal is pointed towards the viewer
					double[][] array1 = {{triangle.A.x, triangle.B.x, -direction.x},
							             {triangle.A.y, triangle.B.y, -direction.y},
							             {triangle.A.z, triangle.B.z, -direction.z}};
					double[][] array2 = {{origin.x - triangle.points.get(1).x},
							             {origin.y - triangle.points.get(1).y},
							             {origin.z - triangle.points.get(1).z}};
					Matrix matrix1 = new Matrix(array1);
					Matrix matrix2 = new Matrix(array2);
					try {
						Matrix result = matrix1.inverse().times(matrix2);  // result = [beta, gamma, t] (see slides)
						double insideCheck = result.get(0, 0) + result.get(1, 0);
						if (insideCheck <= 1 && insideCheck > 0 && result.get(0, 0) >= 0 && result.get(1, 0) >= 0) {  // If the ray hit the triangle
							double distance = result.get(2, 0);  // The distance from the origin to the point of intersection (distance the ray traveled)
							if (distance >= 0.000000001 && (distance < nearestTriangleDistance || nearestTriangleDistance == -1)) {  // If nearest intersection so far (and triangle not behind ray)
								nearestTriangleDistance = distance;
								triangleFinal = triangle;
								triangleNormalFinal = triangleNormal;
							}
						}
					} catch (RuntimeException e) {
						// Singular matrix, means that the ray was parallel to the plane of the triangle
					}
				}
			}
		}

		
		// If the ray hit a sphere
		if (nearestSphereDistance != -1 && (nearestTriangleDistance == -1 || nearestSphereDistance <= nearestTriangleDistance)) {
			Vector intersection = origin.plus(direction.times(nearestSphereDistance));  // World coordinates where ray intersects sphere
			Vector surfaceNormal = (intersection.minus(sphereFinal.center)).normalize();
			color.set(setColor(surfaceNormal, intersection, sphereFinal.material, true, sphereFinal));
			return nearestSphereDistance;
		}
		// If the ray hit a triangle
		else if (nearestTriangleDistance != -1 && (nearestSphereDistance == -1 || nearestTriangleDistance < nearestSphereDistance)) {
			Vector intersection = origin.plus(direction.times(nearestTriangleDistance));
			Vector surfaceNormal = triangleNormalFinal;
			color.set(setColor(surfaceNormal, intersection, triangleFinal.material, false, null));
			return nearestTriangleDistance;
		}
		// If the ray didn't hit anything
		else {
			color.setAll(DEFAULT_COLOR);
			return -1;
		}
	}
	
	// TODO some instances of prp may need to be replaced by origin or something for reflection or refraction rays
	private Color setColor(Vector surfaceNormal, Vector intersection, Material material, boolean isSphere, Sphere sphere) {
		if (recursionDepth == 0) {  // If it's a shadow checker, don't compute the color
			return new Color();
		}
		
		// Ambient
		Color ambient = new Color(RayTracer.lights.get(0).color.times(material.Ka));  // lights.get(0) is the ambient light. Is set in RayTracer.commandFileParse
		
		// Diffuse and specular
		Color diffuse = new Color();
		Color specular = new Color();
		Vector directionToViewer = direction.times(-1);  // V in slides
		// TODO Implement lights at infinity
		for (int i=1; i<RayTracer.lights.size(); ++i) {
			Light light = RayTracer.lights.get(i);
			Vector directionToLight;  // L in slides
			double distanceToLight;
			if (light.position.w == 0) {  // Light is at infinity
				distanceToLight = Double.MAX_VALUE;
				directionToLight = light.position.normalize();
			}
			else {
				distanceToLight = light.position.minus(intersection).length();
				directionToLight = (light.position.minus(intersection)).normalize();
			}
			if (directionToLight.dot(surfaceNormal) > 0) {  // Check for self-shadowing
				Ray shadowCheckRay = new Ray(intersection, directionToLight, 0);
				Color junk = new Color();
				double shadowCheckDepth = shadowCheckRay.trace(junk);
				if (shadowCheckDepth > distanceToLight || shadowCheckDepth == -1) {  // Check if it's being shadowed
					diffuse = diffuse.add((RayTracer.lights.get(i).color.times(material.Kd)).times(surfaceNormal.dot(directionToLight)));
					Vector directionOfReflectedRay = ((surfaceNormal.times(2*(directionToLight.dot(surfaceNormal)))).minus(directionToLight)).normalize();  // R in slides
					if (directionToViewer.dot(directionOfReflectedRay) > 0) {
						specular = specular.add((RayTracer.lights.get(i).color.times(material.Ks)).times(Math.pow(directionToViewer.dot(directionOfReflectedRay), material.Ns)));
					}
				}
			}
		}
		
		// Reflection and refraction
		Color reflectionColor = new Color();
		Color refractionColor = new Color();
		Double Tr = 1.0;  // Transparency: 1 means not transparent, 0 means completely transparent
		if (recursionDepth > 1) {
			Vector reflectionDirection = ((surfaceNormal.times(2*(direction.times(-1).dot(surfaceNormal)))).minus(direction.times(-1))).normalize();
			Ray reflectionRay = new Ray(intersection, reflectionDirection, recursionDepth-1);
			double reflectionDistance = reflectionRay.trace(reflectionColor);
			if (reflectionDistance == -1) {
				reflectionColor.setAll(0);  // Don't accumulate light from background color
			}
			if (isSphere) {
				Vector insideRayDirection = refract(direction, surfaceNormal, 1, material.n1);
				Vector R = intersection.minus(sphere.center);
				Vector A = insideRayDirection.times(R.times(-1).dot(insideRayDirection));
				Vector exitPoint = sphere.center.plus(R).plus(A.times(2));
				Vector refractionRayDirection = refract(insideRayDirection, (sphere.center.minus(exitPoint)).normalize(), material.n1, 1);
				Ray refractionRay = new Ray(exitPoint, refractionRayDirection, recursionDepth);  // Recursion depth is NOT decremented for refraction
				double refractionDistance = refractionRay.trace(refractionColor);
				Tr = material.Tr;
				if (refractionDistance == -1) {
					refractionColor.setAll(DEFAULT_COLOR);
				}
			}
		}
		Color color = new Color(((ambient.add(diffuse).add(specular).add(reflectionColor.times(material.Kr))).times(Tr)).add(refractionColor.times(1 - Tr).times(material.Krf)));
		return color;
	}
	
	private Vector refract(Vector entryDirection, Vector surfaceNormal, double mu1, double mu2) {
		if (Math.abs(surfaceNormal.length() - 1) > 0.0000000000001) {
			System.err.println("Error: surfaceNormal argument to refract() function not normalized");
			System.err.println("surfaceNormal length:" + surfaceNormal.length());
		}
		double mu = mu1 / mu2;
		double alpha = -mu;
		double temp = entryDirection.times(-1).normalize().dot(surfaceNormal);
		double beta = mu*temp - Math.sqrt(1 - mu*mu + mu*mu*temp*temp);
		return entryDirection.times(-1).times(alpha).plus(surfaceNormal.times(beta)).normalize();
	}
	
}