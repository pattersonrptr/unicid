/* Demonstra erro sintático */

class erroSintatico {

	int a, b;
	
	constructor() {
	
		a = 5;
		b = 10;
		
		if (b = a) {
			print(" " + b);
		}
		
	}

}