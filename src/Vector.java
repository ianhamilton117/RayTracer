import java.lang.Math;

public class Vector {
	
	public double x;
	public double y;
	public double z;
	public double w;	//4th dimension is implementing homogeneous coordinates.
						//In general, w = 1 for a location and w = 0 for a direction.
	
	public Vector(double xArg, double yArg, double zArg, double wArg) {
		x = xArg;
		y = yArg;
		z = zArg;
		w = wArg;
	}
	
	//If not specified, w defaults to 0.
	public Vector(double xArg, double yArg, double zArg) {
		x = xArg;
		y = yArg;
		z = zArg;
		w = 0;
	}
	
	//Computes the dot product
	public double dot(Vector vect) {
		double a = this.x * vect.x;
		double b = this.y * vect.y;
		double c = this.z * vect.z;
		return a + b + c;
	}
	
	//Returns a normalized version of itself
	public Vector normalize() {
		
		if (w == 0) {
			double len = this.length();
			return new Vector(x/len, y/len, z/len, 0);
		}
		else
			return new Vector(x/w, y/w, z/w, 1);
	}
	
	// Returns this vector's magnitude
	public double length() {
		return Math.sqrt(x*x + y*y + z*z);
	}
	
	// Subtracts vect from this. Returns a direction, not a location (w=0).
	public Vector minus(Vector vect) {
		return new Vector(this.x - vect.x, this.y - vect.y, this.z - vect.z, 0);
	}
	
	// Adds vect to this. Returns a direction, not a location (w=0).
	public Vector plus(Vector vect) {
		return new Vector(this.x + vect.x, this.y + vect.y, this.z + vect.z, 0);
	}
	
	public String toString() {
		String out = ("(" + Double.toString(x) + "," + Double.toString(y) + "," + Double.toString(z) + ")");
		return out;
	}
	
}