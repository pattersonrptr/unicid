/*
	BUBBLE SORT
*/


class bubblesort {

	int lista[][];
	int tam;
	
	constructor(int l[][], int t) {
		lista = l;
		tam = t;		
	}
	
	int [][] sort() {
		int i, j;
		
		for(i = 0; i < tam; i = i + 1) {
			for(j = 0; j < tam - i - 1; j = j + 1) {
				if(lista[j][0] > lista[j + 1][0]) {
					int aux;
					aux = lista[j][0];
					lista[j][0] = lista[j + 1][0];
					lista[j + 1][0] = aux;
				}
			}
		}
		
		return lista;	
	}
	
	int start() {
	
		print "Tamanho do array: ";
		int size;
		read size;
		
		
		int l[][];
		l = new int [size][1];
		
		int i;
		
		print "Digite os elementos do vetor. \n";
		
		for(i = 0; i < size; i = i + 1) {
			print("\n" + (i + 1) + "º elemento: ");
			read l[i][0];
		}
	
		bubblesort bubble;
		bubble = new bubblesort(l, size);
		
		for(i = 0; i < size; i = i + 1) {
			print(l[i][0] + " ");
		}
		
		print "\n";
		
		l = bubble.sort();
		
		for(i = 0; i < size; i = i + 1) {
			print(l[i][0] + " ");
		}
		
		print "\n";
		
		return 0;
	}
	
}