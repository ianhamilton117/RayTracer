public class Color {
	
	public int r;
	public int g;
	public int b;
	
	public Color() {
		r = 0;
		g = 0;
		b = 0;
	}
	
	public Color(int rArg, int gArg, int bArg) {
		r = rArg;
		g = gArg;
		b = bArg;
	}
	
	public void set(Color color) {
		r = color.r;
		g = color.g;
		b = color.b;
	}
	
	public void setAll(int arg) {
		r = arg;
		g = arg;
		b = arg;
	}
	
	public String toString() {
		String out = (Integer.toString(r) + " " + Integer.toString(g) + " " + Integer.toString(b));
		return out;
	}
	
}