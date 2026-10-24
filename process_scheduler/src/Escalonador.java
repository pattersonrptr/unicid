import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Escalonador de processos movido a um relógio discreto.
 *
 * Toda a simulação avança em tick(), um método synchronized: a cada tick o escalonador
 * devolve à fila os processos que terminaram a troca de contexto, aplica o envelhecimento,
 * decide preempção e despacho, executa um tick de CPU e trata término e fim de fatia de tempo.
 * Uma única thread (o "clock") chama tick() a cada tempoCPU milissegundos; a interface só lê
 * cópias do estado. Assim não há duas threads alterando o mesmo processo.
 *
 * @author patterson
 */
public class Escalonador {

	/** Timeslice (quantum), em ticks. */
	public static final int FATIA_TEMPO = 15;

	/** Duração da troca de contexto, em ticks. */
	public static final int TEMPO_TROCA_CONTEXTO = 2;

	/** A cada quantos ticks na fila de prontos a prioridade de um processo melhora em 1 (aging). */
	public static final int ENVELHECIMENTO = 30;

	/**
	 * Duração de um tick em milissegundos, inicialmente 1 segundo.
	 * Eu sei que 1 segundo é uma eternidade, mas isso é apenas um simulador ;)
	 */
	private static volatile int tempoCPU = 1000;

	private Politica politica;

	/** Fila de processos prontos, ordenada pela política. */
	private PriorityQueue<Processo> fila;

	/** Processos em troca de contexto, na ordem em que saíram da CPU. */
	private final List<Processo> filaEspera = new ArrayList<>();

	private final List<Processo> finalizados = new ArrayList<>();

	/** Processo em execução, ou null se a CPU está livre. */
	private Processo processoExec;

	/** PID que ocupou a CPU em cada tick (0 = ociosa), para o diagrama de Gantt. */
	private final List<Integer> historico = new ArrayList<>();

	private int relogio = 0;
	private int ticksOcupados = 0;
	private int ticksAtivos = 0;
	private int trocasContexto = 0;
	private long proximaOrdem = 0;

	private Thread clock;

	public Escalonador() {
		this(Politica.PRIORIDADE_RR);
	}

	public Escalonador(Politica politica) {
		this.politica = politica;
		this.fila = new PriorityQueue<>(11, politica.comparador());
	}

	/**
	 * Inicia a thread do relógio, que chama tick() a cada tempoCPU milissegundos.
	 */
	public void iniciar() {
		clock = new Thread("relogio-cpu") {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(tempoCPU);
					} catch (InterruptedException e) {
						return;
					}
					tick();
				}
			}
		};
		clock.setDaemon(true);
		clock.start();
	}

	/** Para a thread do relógio. */
	public void parar() {
		if (clock != null) {
			clock.interrupt();
		}
	}

	/**
	 * Avança a simulação em um tick.
	 */
	public synchronized void tick() {

		// 1. Quem terminou a troca de contexto volta para a fila de prontos.
		Iterator<Processo> it = filaEspera.iterator();
		while (it.hasNext()) {
			Processo p = it.next();
			if (p.contaTempoEspera >= TEMPO_TROCA_CONTEXTO) {
				it.remove();
				entraNaFila(p);
			}
		}

		// 2. Envelhecimento: quem espera muito na fila ganha prioridade (evita inanição).
		if (politica.usaEnvelhecimento()) {
			envelhece();
		}

		// 3. Preempção: o primeiro da fila toma a CPU se a política preferir ele ao atual.
		if (processoExec != null && !fila.isEmpty() && politica.devePreemptar(fila.peek(), processoExec)) {
			trocaContexto();
		}

		// 4. Despacho: CPU livre recebe o primeiro da fila.
		if (processoExec == null && !fila.isEmpty()) {
			despacha();
		}

		// 5. Um tick de tempo passa.
		boolean ativo = processoExec != null || !fila.isEmpty() || !filaEspera.isEmpty();
		if (ativo) {
			ticksAtivos++;
		}

		historico.add(processoExec != null ? processoExec.getPID() : 0);

		if (processoExec != null) {
			ticksOcupados++;
			processoExec.tempoRestante--;
			processoExec.contFatiaTempo++;
		}
		for (Processo p : filaEspera) {
			p.contaTempoEspera++;
		}
		for (Processo p : fila) {
			p.ticksNaFila++;
		}

		relogio++;

		// 6. Fim do tick: término ou fim da fatia de tempo.
		if (processoExec != null) {
			Processo p = processoExec;

			if (p.tempoRestante <= 0) {
				p.estado = Processo.Estado.FINALIZADO;
				p.fim = relogio;
				finalizados.add(p);
				processoExec = null;
			} else if (politica.usaFatiaTempo() && p.contFatiaTempo >= FATIA_TEMPO) {
				// Só vale a pena trocar de contexto se houver outro processo pronto;
				// sozinho, o processo ganha uma nova fatia sem deixar a CPU ociosa.
				if (!fila.isEmpty()) {
					trocaContexto();
				} else {
					p.contFatiaTempo = 0;
				}
			}
		}
	}

	private void entraNaFila(Processo p) {
		p.estado = Processo.Estado.PRONTO;
		p.ordem = proximaOrdem++;
		p.ticksNaFila = 0;
		fila.add(p);
	}

	private void despacha() {
		processoExec = fila.remove();
		processoExec.estado = Processo.Estado.EXEC;
		processoExec.contFatiaTempo = 0;

		if (processoExec.inicio < 0) {
			processoExec.inicio = relogio;
		}
	}

	/** Tira o processo em execução da CPU e o coloca em troca de contexto. */
	private void trocaContexto() {
		Processo p = processoExec;
		processoExec = null;
		// O ganho do aging vale enquanto o processo está na CPU; devolvê-lo no despacho
		// faria o processo envelhecido ser preemptado logo no tick seguinte.
		p.prioridade = p.getPrioridadeOriginal();
		p.estado = Processo.Estado.ESPERA;
		p.contaTempoEspera = 0;
		filaEspera.add(p);
		trocasContexto++;
	}

	private void envelhece() {
		boolean mudou = false;

		for (Processo p : fila) {
			if (p.ticksNaFila >= ENVELHECIMENTO && p.prioridade > 0) {
				p.prioridade--;
				p.ticksNaFila = 0;
				mudou = true;
			}
		}

		if (mudou) {
			// A PriorityQueue não se reordena sozinha quando a chave de um elemento muda.
			List<Processo> todos = new ArrayList<>(fila);
			fila.clear();
			fila.addAll(todos);
		}
	}

	/**
	 * Adiciona um processo novo, que chega no instante atual do relógio.
	 */
	public synchronized void addProcesso(Processo p) {
		p.chegada = relogio;
		entraNaFila(p);
	}

	/**
	 * Troca a política. A fila de prontos é reordenada pelo novo critério.
	 */
	public synchronized void setPolitica(Politica politica) {
		this.politica = politica;
		List<Processo> todos = new ArrayList<>(fila);
		fila = new PriorityQueue<>(11, politica.comparador());
		fila.addAll(todos);
	}

	/** Executa ticks até não restar nenhum processo (para testes e comparações). */
	public synchronized void executaAteTerminar(int limiteTicks) {
		for (int i = 0; i < limiteTicks && temProcessos(); i++) {
			tick();
		}
	}

	public synchronized boolean temProcessos() {
		return processoExec != null || !fila.isEmpty() || !filaEspera.isEmpty();
	}

	// Leituras: cópias, para a interface não iterar coleções que o relógio altera ----

	/** Fila de prontos na ordem em que serão atendidos. */
	public synchronized List<Processo> getFila() {
		List<Processo> copia = new ArrayList<>(fila);
		Collections.sort(copia, politica.comparador());
		return copia;
	}

	public synchronized List<Processo> getFilaEspera() {
		return new ArrayList<>(filaEspera);
	}

	public synchronized List<Processo> getFinalizados() {
		return new ArrayList<>(finalizados);
	}

	public synchronized List<Integer> getHistorico() {
		return new ArrayList<>(historico);
	}

	public synchronized Processo getProcessoExec() {
		return processoExec;
	}

	public synchronized Politica getPolitica() {
		return politica;
	}

	public synchronized int getRelogio() {
		return relogio;
	}

	public synchronized Metricas getMetricas() {
		return new Metricas(finalizados, ticksOcupados, ticksAtivos, trocasContexto);
	}

	public static int getTempoCPU() {
		return tempoCPU;
	}

	public static void setTempoCPU(int tempoCPU) {
		Escalonador.tempoCPU = tempoCPU;
	}

	/**
	 * Métricas clássicas de escalonamento, calculadas sobre os processos finalizados.
	 */
	public static class Metricas {
		public final int finalizados;
		public final double esperaMedia;
		public final double turnaroundMedio;
		public final double respostaMedia;
		/** Ticks com a CPU executando / ticks com algum processo no sistema. */
		public final double utilizacaoCPU;
		public final int trocasContexto;

		Metricas(List<Processo> fin, int ticksOcupados, int ticksAtivos, int trocasContexto) {
			this.finalizados = fin.size();
			double espera = 0, turnaround = 0, resposta = 0;
			for (Processo p : fin) {
				espera += p.getTempoEspera();
				turnaround += p.getTurnaround();
				resposta += p.getTempoResposta();
			}
			int n = Math.max(1, fin.size());
			this.esperaMedia = espera / n;
			this.turnaroundMedio = turnaround / n;
			this.respostaMedia = resposta / n;
			this.utilizacaoCPU = ticksAtivos == 0 ? 0 : 100.0 * ticksOcupados / ticksAtivos;
			this.trocasContexto = trocasContexto;
		}
	}
}
