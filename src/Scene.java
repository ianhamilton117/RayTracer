import Jama.Matrix;

public class Scene {
	
	String name;
	int image_width;
	int image_height;
	int recursion_depth;
	Vector[][] imagePlane;
	private Color[][] colorMap;
	private Color[][] depthMap;
	
	private static final int MAX_RGB_VALUE = 255;
	
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
				Vector direction = imagePlane[i][j].minus(camera.prp);
				Ray ray = new Ray(camera.prp, direction, recursion_depth);
				if (RayTracer.DEBUG == true) {
					if (i == 439 && j == 1024/2)
						Ray.RAY_DEBUG = true;
					else
						Ray.RAY_DEBUG = false;
				}
				double distance = ray.trace(colorMap[i][j]);
				if (distance == -1) {
					depthMap[i][j].setAll(0);
				}
				else {
					depthMap[i][j].setAll((int)Math.round(MAX_RGB_VALUE * (1 - (distance)/(camera.far-camera.near))));
				}
			}
		}
	}
	
	private void initializeImagePlane(Camera camera) {
		
		Vector n = new Vector(camera.vpn);
		Vector u = (camera.vup.cross(n)).normalize();
		Vector v = n.cross(u);
		Vector topLeft = new Vector(camera.prp);	// topLeft will specify the location of the top left corner of the image plane.
		topLeft = topLeft.minus(camera.vpn.times(camera.near));	// Move to center of image plane
		topLeft = topLeft.minus(u);	// Move to u = -1 (left) edge of image plane
		topLeft = topLeft.plus(v);	// Move to v = +1 (top) edge of image plane
		double pixWidth = 2.0 / (image_width-1); // The distance between each pixel in the horizontal (u) direction.
		double pixHeight = 2.0 / (image_height-1); // The distance between each pixel in the vertical (v) direction.
		for (int j = 0; j < image_height; ++j) {
			for (int i = 0; i < image_width; ++i) {
				imagePlane[i][j] = new Vector(topLeft.plus(u.times(pixWidth*i)).minus(v.times(pixHeight*j)));
				imagePlane[i][j].w = 1;
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