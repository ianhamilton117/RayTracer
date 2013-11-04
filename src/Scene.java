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
		double width = 2.0 / (image_width-1); // The distance between each pixel in the x direction.
		double height = 2.0 / (image_height-1); // The distance between each pixel in the y direction.
		for (int j = 0; j < image_height; ++j) {
			for (int i = 0; i < image_width; ++i) {
				imagePlane[i][j] = new Vector(-1 + width * i, 1 - height * j, -camera.near);
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