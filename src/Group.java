import java.util.LinkedList;

public class Group {
	
	public String name;
	public LinkedList<Face> faces;
	
	public Group(String name) {
		this.name = name;
		faces = new LinkedList<Face>();
	}
	
	public void addFace(Face face) {
		faces.add(face);
	}
}