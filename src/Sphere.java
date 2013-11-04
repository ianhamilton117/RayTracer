public class Sphere {
	
	public String name;
	public Vector center;
	public double radius;
	public Color color;
	
	public Sphere(String nameArg, double x, double y, double z, double radiusArg, int r, int g, int b) {
		name = nameArg;
		center = new Vector(x, y, z, 1);
		radius = radiusArg;
		color = new Color(r, g, b);
	}

}