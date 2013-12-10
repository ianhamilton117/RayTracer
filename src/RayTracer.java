import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;
import Jama.Matrix;



public class RayTracer {
	
	public static boolean DEBUG = true;
	
	public static ArrayList<Sphere> spheres = new ArrayList<Sphere>();
	public static LinkedList<Camera> cameras = new LinkedList<Camera>();
	public static LinkedList<Light> lights = new LinkedList<Light>();
	public static LinkedList<Scene> scenes = new LinkedList<Scene>();
	public static ArrayList<Vector> vertices = new ArrayList<Vector>();
	public static ArrayList<Group> groups = new ArrayList<Group>();
	public static LinkedList<Material> materials = new LinkedList<Material>();
	
	public static void main(String[] args) throws IOException {
		parse(args[0], args[1]);
		for (int s = 0; s < scenes.size(); ++s) {
			for (int c = 0; c < cameras.size(); ++c) {
			scenes.get(s).renderScene(cameras.get(c));
			Color[][] colorMap = scenes.get(s).getColorMap();
			Color[][] depthMap = scenes.get(s).getDepthMap();
			String colorFile = (scenes.get(s).name + "_" + cameras.get(c).name + "_color.ppm");
			String depthFile = (scenes.get(s).name + "_" + cameras.get(c).name + "_depth.ppm");
			write(colorMap, colorFile);
			write(depthMap, depthFile);
			}
		}
		System.out.println("Done");
	}
	
	//Takes an object input file and a command input file, puts the lines of each into a LinkedList<String>
	//and hands them off to other methods. No error checking.
	static void parse(String objectFileName, String commandFileName) {
		
		File objectFile = new File(objectFileName);
		Scanner objectIn = null;
		try {
			objectIn = new Scanner(objectFile);
		} catch (FileNotFoundException e1) {
			System.err.println("Could not find object file");
//			e1.printStackTrace();
		}
		String commandFilePath = objectFileName.substring(0, objectFileName.lastIndexOf('/')+1);  // Chops off command file name while leaving path
		LinkedList<String> objectFileLines = new LinkedList<String>();
		while (objectIn.hasNextLine()) {
			String line = objectIn.nextLine();
			if (!line.trim().isEmpty())
				objectFileLines.add(line);
		}
		objectIn.close();
		objectFilePassOne(objectFileLines);
		objectFilePassTwo(objectFileLines, commandFilePath);
		
		File commandFile = new File(commandFileName);
		Scanner commandIn = null;
		try {
			commandIn = new Scanner(commandFile);
		} catch (FileNotFoundException e2) {
			System.err.println("Could not find command file");
//			e2.printStackTrace();
			return;
		}
		LinkedList<String> commandFileLines = new LinkedList<String>();
		while (commandIn.hasNextLine()) {
			String line = commandIn.nextLine();
			if (!line.trim().isEmpty())
				commandFileLines.add(line);
		}
		commandIn.close();
		commandFileParse(commandFileLines);
		
	}
	
	// Reads vertices from object file
	static void objectFilePassOne(LinkedList<String> input) {
		vertices.add(new Vector(0, 0, 0, 0));  // Dummy vertex so that list starts at index 1

		for (int i=0; i < input.size(); i++) {
			Scanner in = new Scanner(input.get(i));
			String type = in.next();
			switch (type) {
			
			case "v":	double x = in.nextDouble();
						double y = in.nextDouble();
						double z = in.nextDouble();
						double w;
						if (in.hasNextDouble())
							w = in.nextDouble();
						else
							w = 1;
						vertices.add(new Vector(x, y, z, w));
						break;
						
			default:	break;
			}
			in.close();
		}
	}
	
	// Reads faces, groups, and spheres from object file
	static void objectFilePassTwo(LinkedList<String> input, String mtlPath) {
		groups.add(new Group("default"));
		Group currentGroup = groups.get(groups.size()-1);
		Material currentMaterial = null;
		for (int i=0; i < input.size(); i++) {
			Scanner in = new Scanner(input.get(i));
			String type = in.next();
			switch (type) {
			
			case "mtllib":	File materialFile = new File(mtlPath + in.next());
							Scanner materialIn = null;
							try {
								materialIn = new Scanner(materialFile);
							} catch (FileNotFoundException e1) {
								System.err.println("Could not find material file");
//								e1.printStackTrace();
							}
							LinkedList<String> materialFileLines = new LinkedList<String>();
							while (materialIn.hasNextLine()) {
								String line = materialIn.nextLine();
								if (!line.trim().isEmpty())
									materialFileLines.add(line);
							}
							materialIn.close();
							materialFileParse(materialFileLines);
							break;
							
			
			case "usemtl":	String mtlName = in.next();
							int mtlIndex = findMtl(mtlName);
							if (mtlIndex == -1)
								System.out.println("Error: Could not find material: " + mtlName);
							currentMaterial = materials.get(mtlIndex);
							break;
			
			case "f":	ArrayList<Vector> points = new ArrayList<Vector>();
						while (in.hasNextInt())
							points.add(vertices.get(in.nextInt()));
						currentGroup.addFace(new Face(points, currentMaterial));
						break;
						
			case "g":	groups.add(new Group(in.next()));
						currentGroup = groups.get(groups.size()-1);
						// TODO If the group already exists, use that one rather than creating a new one?
						break;
			
			case "s":	String sphereName = in.next();
						double x = in.nextDouble();
						double y = in.nextDouble();
						double z = in.nextDouble();
						double radius = in.nextDouble();
						Sphere sphere = new Sphere(sphereName, x, y, z, radius, currentMaterial);
						spheres.add(sphere);
						break;
						
			default:	break;
			}
			in.close();
		}
	}
	
	// Reads camera specs, light sources, and ray cast commands from command file
	static void commandFileParse(LinkedList<String> input) {
		lights.add(new Light(0, 0, 0, 0, 20, 20, 20));  // The first light is the ambient light
		for (int i=0; i < input.size(); i++) {
			Scanner in = new Scanner(input.get(i));
			String type = in.next();
			switch (type) {
			
			case "c":	String cameraName = in.next();
						double prp_x = in.nextDouble();
						double prp_y = in.nextDouble();
						double prp_z = in.nextDouble();
						double vpn_x = in.nextDouble();
						double vpn_y = in.nextDouble();
						double vpn_z = in.nextDouble();
						double vup_x = in.nextDouble();
						double vup_y = in.nextDouble();
						double vup_z = in.nextDouble();
						double near = in.nextDouble();
						double far = in.nextDouble();
						Camera camera = new Camera(cameraName, prp_x, prp_y, prp_z, vpn_x, vpn_y, vpn_z, vup_x, vup_y, vup_z, near,far);
						cameras.add(camera);
						break;

			case "l":	double x = in.nextDouble();
						double y = in.nextDouble();
						double z = in.nextDouble();
						double w = in.nextDouble();
						int r = in.nextInt();
						int g = in.nextInt();
						int b = in.nextInt();
						lights.add(new Light(x, y, z, w, r, g, b));
						break;
			
			case "r":	String sceneName = in.next();
						int width = in.nextInt();
						int height = in.nextInt();
						int recursion_depth = in.nextInt();
						Scene scene = new Scene(sceneName, width, height, recursion_depth);
						scenes.add(scene);
						break;
			}
			in.close();
		}
	}
	
	static void materialFileParse(LinkedList<String> input) {
		Material currentMaterial = null;
		double r;
		double g;
		double b;
		for (int i=0; i < input.size(); i++) {
			Scanner in = new Scanner(input.get(i));
			String type = in.next();
			switch (type) {
			
			case "newmtl":	materials.add(new Material(in.next()));
							currentMaterial = materials.getLast();
							break;
			
			case "Ka":		r = in.nextDouble();
							g = in.nextDouble();
							b = in.nextDouble();
							currentMaterial.setKa(r, g, b);
							break;
							
			case "Kd":		r = in.nextDouble();
							g = in.nextDouble();
							b = in.nextDouble();
							currentMaterial.setKd(r, g, b);
							break;
							
			case "Ks":		r = in.nextDouble();
							g = in.nextDouble();
							b = in.nextDouble();
							currentMaterial.setKs(r, g, b);
							break;
							
			case "Ns":		double Ns = in.nextDouble();
							currentMaterial.Ns = Ns;
							break;
							
			case "n1":		double n1 = in.nextDouble();
							currentMaterial.n1 = n1;
							break;
							
			case "Tr":		double Tr = in.nextDouble();
							currentMaterial.Tr = Tr;
							break;
							
			case "Kr":		double Kr = in.nextDouble();
							currentMaterial.Kr = Kr;
							break;
							
			case "Krf":		double Krf = in.nextDouble();
							currentMaterial.Krf = Krf;
							break;
							
			default:		break;
			
			}
			in.close();
		}
	}
	
	static int findMtl(String name) {
		for (int i=0; i<materials.size(); ++i) {
			if (name.compareTo(materials.get(i).name) == 0)
				return i;
		}
		return -1;
	}

	
/*	//Takes a LinkedList<String> containing object information and adds objects, 
	//cameras, and scenes to global lists. No error checking on String formatting.
	static void parseHelper(LinkedList<String> input) {
		
		for (int i=0; i < input.size(); i++) {
			Scanner in = new Scanner(input.get(i));
			String type = in.next();
			switch (type) {
			
			case "s":	String sphereName = in.next();
						double x = in.nextDouble();
						double y = in.nextDouble();
						double z = in.nextDouble();
						double radius = in.nextDouble();
						int r = in.nextInt();
						int g = in.nextInt();
						int b = in.nextInt();
						Sphere sphere = new Sphere(sphereName, x, y, z, radius, r, g, b);
						spheres.add(sphere);
						break;
						
			case "c":	String cameraName = in.next();
						double prp_x = in.nextDouble();
						double prp_y = in.nextDouble();
						double prp_z = in.nextDouble();
						double vpn_x = in.nextDouble();
						double vpn_y = in.nextDouble();
						double vpn_z = in.nextDouble();
						double vup_x = in.nextDouble();
						double vup_y = in.nextDouble();
						double vup_z = in.nextDouble();
						double near = in.nextDouble();
						double far = in.nextDouble();
						Camera camera = new Camera(cameraName, prp_x, prp_y, prp_z, vpn_x, vpn_y, vpn_z, vup_x, vup_y, vup_z, near,far);
						cameras.add(camera);
						break;
						
			case "r":	String sceneName = in.next();
						int width = in.nextInt();
						int height = in.nextInt();
						int recursion_depth = in.nextInt();
						Scene scene = new Scene(sceneName, width, height, recursion_depth);
						scenes.add(scene);
						break;
						
			default: 	break;
			
			}
			in.close();
		}
		
	}*/
	
	static void write(Color[][] map, String fileName) {
		try {
			File file = new File(fileName);
			FileOutputStream os = new FileOutputStream(file);
			OutputStreamWriter osw = new OutputStreamWriter(os);    
			Writer w = new BufferedWriter(osw);
			w.write("P3 " + Integer.toString(map.length) + " " + Integer.toString(map[0].length) + " 256\n");
			for (int j = 0; j < map[0].length; ++j) {
				for (int i = 0; i < map.length; ++i) {
					w.write(map[i][j].toString());
					w.write("\n");
				}
			}
			w.close();
		} catch (IOException e) {
			System.err.println("Problem writing to the file");
		}
	}
	
	static void printAllGlobals() {		
		System.out.println("Spheres:");
		for(int i=0; i<spheres.size(); ++i)
			System.out.println(spheres.get(i));
		System.out.println();
		
		System.out.println("Vertices:");
		for(int i=0; i<vertices.size(); ++i)
			System.out.println(vertices.get(i));
		System.out.println();
		
		System.out.println("Groups:");
		for(int i=0; i<groups.size(); ++i)
			System.out.println(groups.get(i));
		System.out.println();
		
		System.out.println("Materials:");
		for(int i=0; i<materials.size(); ++i)
			System.out.println(materials.get(i));
		System.out.println();
		
		System.out.println("Cameras:");
		for(int i=0; i<cameras.size(); ++i)
			System.out.println(cameras.get(i).name);
		System.out.println();
		
		System.out.println("Lights:");
		for(int i=0; i<lights.size(); ++i)
			System.out.println(lights.get(i));
		System.out.println();
		
		System.out.println("Scenes:");
		for(int i=0; i<scenes.size(); ++i)
			System.out.println(scenes.get(i).name);
		System.out.println();
	}
	
}