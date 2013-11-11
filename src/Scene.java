import Jama.Matrix;

public class Scene {
	
	String name;
	int image_width;
	int image_height;
	int recursion_depth;
	Vector[][] imagePlane;
	private Color[][] colorMap;
	private Color[][] depthMap;
	
	public Scene(String nameArg, int image_widthArg, int image_heightArg, int recursion_depthArg) {
		
		name = nameArg;
		image_width = image_widthArg;
		image_height = image_heightArg;
		recursion_depth = recursion_depthArg;
		imagePlane = new Vector[image_width][image_height];
		colorMap = new Color[image_width][image_height];
		depthMap = new Color[image_width][image_height];
		for (int j = 0; j < image_height; ++j) {
			for (int i = 0; i< image_width; ++i) {
				colorMap[i][j] = new Color();
				depthMap[i][j] = new Color();
			}
		}
	}
	
	public void renderScene(Camera camera){
		initializeImagePlane(camera);
		for (int j = 0; j < image_height; j++) {
			for (int i = 0; i < image_width; i++) {
				Ray ray = new Ray(camera.prp, imagePlane[i][j], camera.near, camera.far);
				ray.trace(colorMap[i][j], depthMap[i][j]);
			}
		}
	}
	
	private void initializeImagePlane(Camera camera) {
		// First, place prp at origin with vpn pointing down -z axis
		double width = 2.0 / (image_width-1); // The distance between each pixel in the x direction.
		double height = 2.0 / (image_height-1); // The distance between each pixel in the y direction.
		for (int j = 0; j < image_height; ++j) {
			for (int i = 0; i < image_width; ++i) {
				imagePlane[i][j] = new Vector(-1 + width * i, 1 - height * j, -camera.near);
			}
		}
		
		// Then, rotate and translate
		double[][] translate = {{1,0,0,-camera.prp.x},{0,1,0,-camera.prp.y},{0,0,1,-camera.prp.z},{0,0,0,1}}; // TODO Translation is reversed
		Matrix T = new Matrix(translate);
		Vector n = camera.vpn;
		Vector u = (camera.vup.cross(n)).normalize();
		Vector v = n.cross(u);
		double[][] rotate = {u.to1DArray(),v.to1DArray(),n.to1DArray(),{0,0,0,1}};
		Matrix R = new Matrix(rotate);
		Matrix transform = R.times(T);
		T.print(5, 2);
		R.print(5, 2);
		transform.print(5, 2);
		for (int j = 0; j < image_height; ++j) {
			for (int i = 0; i < image_width; ++i) {
				Matrix pixel = new Matrix(imagePlane[i][j].toArrayVert());
				imagePlane[i][j].setVals(transform.times(pixel).getArray());
			}
		}
	}
	
	public Color[][] getColorMap() {
		return colorMap;
	}
	
	public Color[][] getDepthMap() {
		return depthMap;
	}
	
	// For debugging purposes
	public void printImagePlane() {
		for (int j = 0; j < image_height; j++) {
			for (int i = 0; i < image_width; i++) {
				System.out.print(imagePlane[i][j] + " ");
			}
			System.out.print("\n");
		}
	}
	
}