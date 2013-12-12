public class Camera {
	
	public String name;
	public Vector prp;
	public Vector vpn;
	public Vector vup;
	public double near;
	public double far;
	
	public Camera(String nameArg, double prp_x, double prp_y, double prp_z, double vpn_x, double vpn_y, 
			double vpn_z, double vup_x, double vup_y, double vup_z, double nearArg, double farArg) {
		name = nameArg;
		prp = new Vector(prp_x, prp_y, prp_z, 1);
		vpn = new Vector(vpn_x, vpn_y, vpn_z);
		vpn = vpn.normalize();
//		lookAt(new Vector(0, 0, 0));	// For testing. Comment out for regular use.
		vup = new Vector(vup_x, vup_y, vup_z);
		near = nearArg;
		far = farArg;
	}
	
	private void lookAt(Vector lookAt) {
		vpn = (prp.minus(lookAt)).normalize();	// TODO Only works from origin
	}
}