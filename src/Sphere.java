public class Sphere {
	
	public String name;
	public Vector center;
	public double radius;
	public Material material;
	
	public Sphere(String nameArg, double x, double y, double z, double radiusArg, Material mtlArg) {
		name = nameArg;
		center = new Vector(x, y, z, 1);
		radius = radiusArg;
		material = mtlArg;
	}
	
	public String toString() {
		return new String(name + "\nCenter: " + center + "\nRadius: " + radius + "\nMaterial: " + material.name);
	}

}