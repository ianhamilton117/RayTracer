import java.util.ArrayList;

public class Face {
	
	ArrayList<Vector> points;
	Material material;
	
	public Face(ArrayList<Vector> points, Material materail) {
		this.points = points;
		this.material = material;
		triangulate();
	}
	
	private void triangulate() {
		// TODO implement
	}
}