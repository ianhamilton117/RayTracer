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
	
	// Copy constructor
	public Vector(Vector orig) {
		x = orig.x;
		y = orig.y;
		z = orig.z;
		w = orig.w;
	}
	
	//If not specified, w defaults to 0.
	public Vector(double xArg, double yArg, double zArg) {
		x = xArg;
		y = yArg;
		z = zArg;
		w = 0;
	}
	
	// Multiplies by a scalar
	public Vector times(double num) {
		return new Vector(this.x*num, this.y*num, this.z*num, this.w);	// Does not change value of w
	}
	
	//Computes the dot product
	public double dot(Vector vect) {
		double a = this.x * vect.x;
		double b = this.y * vect.y;
		double c = this.z * vect.z;
		return a + b + c;
	}
	
	//Computes the cross product
	public Vector cross(Vector vect) {
		double xNew = this.y*vect.z - this.z*vect.y;
		double yNew = this.z*vect.x - this.x*vect.z;
		double zNew = this.x*vect.y - this.y*vect.x;
		return new Vector(xNew, yNew, zNew);
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
	
	public void setVals(double[][] array) {
		try {
			x = array[0][0];
			y = array[1][0];
			z = array[2][0];
			w = array[3][0];
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("Array not the right size");
			e.printStackTrace();
		}
	}
	
	public double[][] toArrayVert() {
		double[][] ret = {{x},{y},{z},{w}};
		return ret;
	}
	
	public double[][] toArrayHoriz() {
		double[][] ret = {{x,y,z,w}};
		return ret;
	}
	
	public double[] to1DArray() {
		double[] ret = {x,y,z,w};
		return ret;
	}
	
	public void isNormalized() {
		if (w != 1)
			System.out.println("Not normalized)");
	}
	
	public String toString() {
		String out = ("(" + Double.toString(x) + "," + Double.toString(y) + "," + Double.toString(z) + "," + Double.toString(w) + ")");
		return out;
	}
	
}