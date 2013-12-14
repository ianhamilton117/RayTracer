import java.util.ArrayList;
import java.util.LinkedList;

public class Face {
	
	ArrayList<Vector> points;
	Material material;
	LinkedList<Face> triangles;
	Vector normal;
	Vector correctedNormal;
	Vector A;
	Vector B;
	double d;
	
	public Face(ArrayList<Vector> points, Material material) {
		this.points = points;
		this.material = material;
		triangles = new LinkedList<Face>();
/*		if (RayTracer.DEBUG == true) {
			System.out.println(points);
		}*/
		triangulate();
	}
	
	private Face(ArrayList<Vector> pointsArg, Material material, boolean isTriangle) {
		this.points = pointsArg;
		this.material = material;
		A = points.get(0).minus(points.get(1));
		B = points.get(2).minus(points.get(1));
		normal = A.cross(B).normalize();
		d = -(normal.x*points.get(0).x + normal.y*points.get(0).y + normal.z*points.get(0).z);
/*		if (RayTracer.DEBUG == true) {
			System.out.println(points);
			System.out.println(A);
			System.out.println(B);
		}*/
	}
	
	private void triangulate() {
		if (points.size() < 3)
			System.err.println("Error: Face with less than 3 vertices");
		else if (points.size() == 3) {
			triangles.add(new Face(points, material, true));
		}
		else {
			for (int i=1; i<points.size()-1; ++i) {
				ArrayList<Vector> pts = new ArrayList<Vector>();
				pts.add(points.get(0));
				pts.add(points.get(i));
				pts.add(points.get(i+1));
				triangles.add(new Face(pts, material, true));
			}
		}
	}
}