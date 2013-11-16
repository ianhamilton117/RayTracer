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
	
	public String toString() {
		String ret = new String(name + "\nNumber of faces: " + faces.size());
		for(int i=0; i<faces.size(); ++i)
			ret += ("\nFace material: " + faces.get(i).material.name);
		return ret;
	}
}