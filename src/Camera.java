public class Camera {
	
	public String name;
	public Vector prp;
	public Vector vpn;
	public double near;
	public double far;
	
	public Camera(String nameArg, double prp_x, double prp_y, double prp_z, double vpn_x, double vpn_y, double vpn_z, double nearArg, double farArg) {
		name = nameArg;
		prp = new Vector(prp_x, prp_y, prp_z, 1);
		vpn = new Vector(vpn_x, vpn_y, vpn_z);
		near = nearArg;
		far = farArg;
	}
}