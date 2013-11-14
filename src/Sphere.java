public class Sphere {
	
	public String name;
	public Vector center;
	public double radius;
	public Material mtl;
	
	public Sphere(String nameArg, double x, double y, double z, double radiusArg, Material mtlArg) {
		name = nameArg;
		center = new Vector(x, y, z, 1);
		radius = radiusArg;
		mtl = mtlArg;
	}

}