public class Light {
	
	Vector position;
	Color color;
	
	public Light(double x, double y, double z, double w, int r, int g, int b) {
		position = new Vector(x, y, z, w);
		color = new Color(r, g, b);
	}
	
	public String toString() {
		return new String("Light\nPosition: " + position.toString() + "\nColor: " + color.toString());
	}
}