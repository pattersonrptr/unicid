import java.util.Comparator;

/**
 * Políticas de escalonamento disponíveis. Cada uma define:
 * a chave que ordena a fila de prontos (menor = vai antes; empate = ordem de chegada),
 * se é preemptiva, se usa fatia de tempo (quantum) e se usa envelhecimento (aging).
 *
 * @author patterson
 */
public enum Politica {

	FCFS("FCFS — First Come, First Served", false, false, false) {
		int chave(Processo p) { return 0; }
	},

	SJF("SJF — Shortest Job First", false, false, false) {
		int chave(Processo p) { return p.getTempo(); }
	},

	SRTF("SRTF — Shortest Remaining Time First", true, false, false) {
		int chave(Processo p) { return p.getTempoRestante(); }
	},

	RR("Round Robin", false, true, false) {
		int chave(Processo p) { return 0; }
	},

	PRIORIDADE_RR("Prioridade preemptiva + Round Robin + Aging", true, true, true) {
		int chave(Processo p) { return p.getPrioridade(); }
	};

	private final String descricao;
	private final boolean preemptiva;
	private final boolean usaFatiaTempo;
	private final boolean usaEnvelhecimento;

	Politica(String descricao, boolean preemptiva, boolean usaFatiaTempo, boolean usaEnvelhecimento) {
		this.descricao = descricao;
		this.preemptiva = preemptiva;
		this.usaFatiaTempo = usaFatiaTempo;
		this.usaEnvelhecimento = usaEnvelhecimento;
	}

	/** Critério da política: o processo de menor chave tem preferência. */
	abstract int chave(Processo p);

	/** Ordena a fila de prontos pela chave; empates por ordem de chegada na fila (FIFO). */
	Comparator<Processo> comparador() {
		return new Comparator<Processo>() {
			@Override
			public int compare(Processo a, Processo b) {
				int c = Integer.compare(chave(a), chave(b));
				return c != 0 ? c : Long.compare(a.ordem, b.ordem);
			}
		};
	}

	/** Em política preemptiva, o processo pronto toma a CPU se tiver chave estritamente menor. */
	boolean devePreemptar(Processo pronto, Processo emExecucao) {
		return preemptiva && chave(pronto) < chave(emExecucao);
	}

	public boolean isPreemptiva() {
		return preemptiva;
	}

	public boolean usaFatiaTempo() {
		return usaFatiaTempo;
	}

	public boolean usaEnvelhecimento() {
		return usaEnvelhecimento;
	}

	@Override
	public String toString() {
		return descricao;
	}
}
