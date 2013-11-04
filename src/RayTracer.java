import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.Scanner;
import Jama.Matrix;



public class RayTracer {
	
	public static LinkedList<Sphere> spheres = new LinkedList<Sphere>();
	public static LinkedList<Camera> cameras = new LinkedList<Camera>();
	public static LinkedList<Scene> scenes = new LinkedList<Scene>();
	
	//Takes an object input file and a command input file, puts the lines of each into a LinkedList<String>
	//and hands them off to parse2. No error checking.
	static void parse(String objectFileName, String commandFileName) {
		
		File objectFile = new File(objectFileName);
		Scanner objectIn = null;
		try {
			objectIn = new Scanner(objectFile);
		} catch (FileNotFoundException e1) {
			System.err.println("Could not find object file");
//			e1.printStackTrace();
		}
		LinkedList<String> objectFileLines = new LinkedList<String>();
		while (objectIn.hasNextLine()) {
			String line = objectIn.nextLine();
			if (!line.trim().isEmpty())
				objectFileLines.add(line);
		}
		objectIn.close();
		parseHelper(objectFileLines);
		
		File commandFile = new File(commandFileName);
		Scanner commandIn = null;
		try {
			commandIn = new Scanner(commandFile);
		} catch (FileNotFoundException e) {
			System.err.println("Could not find command file");
//			e.printStackTrace();
			return;
		}
		LinkedList<String> commandFileLines = new LinkedList<String>();
		while (commandIn.hasNextLine()) {
			String line = commandIn.nextLine();
			if (!line.trim().isEmpty())
				commandFileLines.add(line);
		}
		commandIn.close();
		parseHelper(commandFileLines);
		
	}
	
	//Takes a LinkedList<String> containing object information and adds objects, 
	//cameras, and scenes to global lists. No error checking on String formatting.
	static void parseHelper(LinkedList<String> input) {
		
		for (int i=0; i < input.size(); i++) {
			Scanner in = new Scanner(input.get(i));
			String type = in.next();
			//switch statement used so that more types of objects can be added later.
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
						double near = in.nextDouble();
						double far = in.nextDouble();
						Camera camera = new Camera(cameraName, prp_x, prp_y, prp_z, vpn_x, vpn_y, vpn_z, near,far);
						cameras.add(camera);
						break;
						
			case "r":	String sceneName = in.next();
						int width = in.nextInt();
						int height = in.nextInt();
						int recursion_depth = in.nextInt();
						Scene scene = new Scene(sceneName, width, height, recursion_depth);
						scenes.add(scene);
						break;
						
			default: break;
			
			}
			in.close();
		}
		
	}
	
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
	}
	
}