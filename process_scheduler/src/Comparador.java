import java.util.List;

/**
 * Roda a mesma carga em todas as políticas e imprime as métricas e um diagrama de Gantt em texto.
 *
 * Executar: java -cp out Comparador
 *
 * @author patterson
 */
public class Comparador {

	/** {chegada, tempo, prioridade} de cada processo; o PID é a posição + 1. */
	static final int[][] CARGA = {
			{ 0, 30, 3 },
			{ 2, 12, 1 },
			{ 4, 45, 7 },
			{ 6, 8, 5 },
			{ 10, 20, 1 },
	};

	public static void main(String[] args) {
		System.out.println("Carga (chegada, tempo, prioridade):");
		for (int i = 0; i < CARGA.length; i++) {
			System.out.printf("  P%d  %3d %4d %4d%n", i + 1, CARGA[i][0], CARGA[i][1], CARGA[i][2]);
		}
		System.out.println();
		System.out.printf("%-14s %8s %11s %9s %6s %7s%n",
				"Política", "Espera", "Turnaround", "Resposta", "CPU", "Trocas");

		StringBuilder gantts = new StringBuilder();
		for (Politica p : Politica.values()) {
			Escalonador e = simula(p);
			Escalonador.Metricas m = e.getMetricas();
			System.out.printf("%-14s %8.1f %11.1f %9.1f %5.0f%% %7d%n",
					p.name(), m.esperaMedia, m.turnaroundMedio, m.respostaMedia, m.utilizacaoCPU, m.trocasContexto);
			gantts.append(String.format("%-14s %s%n", p.name(), gantt(e.getHistorico(), 2)));
		}

		System.out.println();
		System.out.println("Gantt (1 caractere = 2 ticks, '.' = CPU ociosa):");
		System.out.print(gantts);
	}

	static Escalonador simula(Politica politica) {
		Escalonador e = new Escalonador(politica);
		int proximo = 0;
		while (proximo < CARGA.length || e.temProcessos()) {
			while (proximo < CARGA.length && CARGA[proximo][0] <= e.getRelogio()) {
				int[] c = CARGA[proximo++];
				e.addProcesso(new Processo(proximo, "P-" + proximo, c[1], c[2]));
			}
			e.tick();
		}
		return e;
	}

	/** Um caractere por bloco de ticks, com o PID que mais ocupou a CPU no bloco. */
	static String gantt(List<Integer> historico, int ticksPorCaractere) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < historico.size(); i += ticksPorCaractere) {
			int[] cont = new int[10];
			for (int j = i; j < Math.min(i + ticksPorCaractere, historico.size()); j++) {
				cont[historico.get(j)]++;
			}
			int melhor = 0;
			for (int pid = 1; pid < cont.length; pid++) {
				if (cont[pid] > cont[melhor]) melhor = pid;
			}
			sb.append(melhor == 0 ? '.' : Character.forDigit(melhor, 10));
		}
		return sb.toString();
	}
}
