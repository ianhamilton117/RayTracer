public class Material {
	
	String name;
	Coefficient Ka;
	Coefficient Kd;
	Coefficient Ks;
	double Ns;
	double n1;
	double Tr;
	double Kr;
	double Krf;
	
	public Material(String name) {
		this.name = name;
		Ka = new Coefficient();
		Kd = new Coefficient();
		Ks = new Coefficient();
		Ns = 1;  // Default to 1
		n1 = 1;  // Default to 1
		Tr = 0;  // Default to 0
		Kr = 0;  // Default to 0
		Krf = 0;  // Default to 0
	}
	
	public Material(String name, Coefficient Ka, Coefficient Kd, Coefficient Ks, 
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

	public void setKa(double r, double g, double b) {
		Ka.r = r;
		Ka.b = b;
		Ka.g = g;
	}
	
	public void setKd(double r, double g, double b) {
		Kd.r = r;
		Kd.b = b;
		Kd.g = g;
	}
	
	public void setKs(double r, double g, double b) {
		Ks.r = r;
		Ks.b = b;
		Ks.g = g;
	}
	
	public String toString() {
		return new String(name + "\nKa: " + Ka + "\nKd: " + Kd + "\nKs: " + Ks + 
				"\nNs: " + Ns + "\nn1: " + n1 + "\nTr: " + Tr + "\nKr: " + Kr + "\nKrf: " + Krf);
	}
}
