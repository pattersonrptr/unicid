/**
 * Processo simulado. Guarda apenas dados: quem altera o estado é o Escalonador,
 * a cada tick do relógio, o que evita condições de corrida entre threads.
 *
 * @author patterson
 */
public class Processo {

	/**
	 * Enum que representa os possíveis estados do processo.
	 * ESPERA é o tempo de troca de contexto após sair da CPU sem terminar.
	 */
	public static enum Estado {
		PRONTO, EXEC, ESPERA, FINALIZADO
	}

	/**
	 * Número de identificação do processo, cada processo deve ter PID único.
	 */
	private final int PID;

	/**
	 * Nome do processo.
	 */
	private final String nome;

	/**
	 * Tempo de duração do processo (burst time), em ticks.
	 */
	private final int tempo;

	/**
	 * Prioridade definida na criação: de 0 (maior) a 9 (menor).
	 */
	private final int prioridadeOriginal;

	// Estado controlado pelo Escalonador ------------------------------------

	Estado estado = Estado.PRONTO;

	/** Tempo restante de execução. */
	int tempoRestante;

	/** Prioridade efetiva: melhora com o envelhecimento (aging) enquanto o processo espera na fila. */
	int prioridade;

	/** Ticks executados desde que ganhou a CPU (controle da fatia de tempo). */
	int contFatiaTempo = 0;

	/** Ticks já passados na troca de contexto. */
	int contaTempoEspera = 0;

	/** Ticks na fila de prontos desde o último envelhecimento. */
	int ticksNaFila = 0;

	/** Ordem de entrada na fila de prontos: desempate FIFO entre processos equivalentes. */
	long ordem = 0;

	/** Instantes (em ticks) de chegada, primeira execução e término. */
	int chegada = -1, inicio = -1, fim = -1;

	public Processo(int PID, String nome, int tempo, int prioridade) {
		this.PID = PID;
		this.nome = nome;
		this.tempo = tempo;
		this.prioridadeOriginal = prioridade;
		this.prioridade = prioridade;
		this.tempoRestante = tempo;
	}

	// Métricas (válidas depois que o processo termina) -----------------------

	/** Turnaround: tempo total no sistema, da chegada ao término. */
	public int getTurnaround() {
		return fim - chegada;
	}

	/** Tempo de espera: tempo no sistema sem usar a CPU (fila de prontos + trocas de contexto). */
	public int getTempoEspera() {
		return getTurnaround() - tempo;
	}

	/** Tempo de resposta: da chegada até a primeira vez na CPU. */
	public int getTempoResposta() {
		return inicio - chegada;
	}

	// GETTERS ------------------------------------------------------------------

	public int getPID() {
		return PID;
	}

	public String getNome() {
		return nome;
	}

	public int getTempo() {
		return tempo;
	}

	public int getTempoRestante() {
		return tempoRestante;
	}

	public int getPrioridade() {
		return prioridade;
	}

	public int getPrioridadeOriginal() {
		return prioridadeOriginal;
	}

	public Estado getEstado() {
		return estado;
	}

	public int getChegada() {
		return chegada;
	}

	public int getInicio() {
		return inicio;
	}

	public int getFim() {
		return fim;
	}

	@Override
	public String toString() {
		return nome;
	}
}
