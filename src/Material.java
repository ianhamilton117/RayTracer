public class Material {
	
	String name;
	Vector Ka; change these
	double Kd;
	double Ks;
	double Ns;
	double n1;
	double Tr;
	double Kr;
	double Krf;

	public Material(String name, double Ka, double Kd, double Ks, 
			double Ns, double n1, double Tr, double Kr, double Krf) {
		this.name = name;
		this.Ka = Ka;
		this.Kd = Kd;
		this.Ks = Ks;
		this.Ns = Ns;
		this.n1 = n1;
		this.Tr = Tr;
		this.Kr = Kr;
		this.Krf = Krf;
	}
	
}
