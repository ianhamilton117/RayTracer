public class Color {
	
	public int r;
	public int g;
	public int b;
	private static final int MAX_RGB_VALUE = 255;

	
	public Color() {
		r = 0;
		g = 0;
		b = 0;
	}
	
	public Color(int rArg, int gArg, int bArg) {
		r = rArg;
		g = gArg;
		b = bArg;
		truncate();
	}
	
	public Color(Color color) {
		r = color.r;
		g = color.g;
		b = color.b;
	}
	
	public void set(Color color) {
		r = color.r;
		g = color.g;
		b = color.b;
		truncate();
	}
	
	public void setAll(int arg) {
		r = arg;
		g = arg;
		b = arg;
		truncate();
	}
	
	public Color times(Coefficient K) {
		Color ret = new Color((int)(r * K.r), (int)(g * K.g), (int)(b * K.b));
		ret.truncate();
		return ret;
	}
	
	public Color times(double d) {
		Color ret = new Color((int)(r*d), (int)(g*d), (int)(b*d));
		ret.truncate();
		return ret;
	}
	
	// Add the values of r, g, and b to create a new color
	public Color add(Color color) {
		Color ret = new Color(r+color.r, g+color.g, b+color.b);
		ret.truncate();
		return ret;
	}
	
	// Change any values over 255 to 255
	public void truncate() {
		if (r > MAX_RGB_VALUE)
			r = MAX_RGB_VALUE;
		if (g > MAX_RGB_VALUE)
			g = MAX_RGB_VALUE;
		if (b > MAX_RGB_VALUE)
			b = MAX_RGB_VALUE;
	}
	
	public String toString() {
		String out = (Integer.toString(r) + " " + Integer.toString(g) + " " + Integer.toString(b));
		return out;
	}
	
}