public class Coefficient {
	
	double r;
	double g;
	double b;
	
	public Coefficient() {
		r = 1;  // Default to 1
		g = 1;  // Default to 1
		b = 1;  // Default to 1
	}
	
	public Coefficient(double r, double g, double b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public String toString() {
		return new String(r + " " + g + " " + b);
	}
}