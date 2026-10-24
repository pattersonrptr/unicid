import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Testes do núcleo do escalonador, sem dependências (não usa JUnit).
 * O relógio é avançado manualmente com tick(), então os testes são determinísticos.
 *
 * Executar: javac -d out src/*.java test/*.java && java -cp out EscalonadorTest
 */
public class EscalonadorTest {

	private static int executados = 0, falhas = 0;

	public static void main(String[] args) {
		fcfsAtendePorOrdemDeChegada();
		fcfsCalculaMetricasCorretamente();
		sjfEscolheOMaisCurto();
		srtfPreemptaQuandoChegaUmMaisCurto();
		roundRobinRevezaComFatiaDeTempo();
		roundRobinRespeitaOrdemDeChegadaNoEmpate();
		prioridadeMaiorTomaACpu();
		preemptadoPassaPelaTrocaDeContexto();
		processoSozinhoNaoDeixaACpuOciosa();
		envelhecimentoEvitaInanicao();
		nenhumProcessoFantasmaNaFilaDeEspera();

		System.out.println();
		System.out.println(executados + " testes, " + falhas + " falhas");
		System.exit(falhas == 0 ? 0 : 1);
	}

	// Testes ---------------------------------------------------------------

	static void fcfsAtendePorOrdemDeChegada() {
		Escalonador e = new Escalonador(Politica.FCFS);
		Processo a = novo(e, 1, 5, 9), b = novo(e, 2, 3, 0);
		e.executaAteTerminar(100);
		verifica("FCFS: ordem de chegada, sem olhar prioridade",
				a.getFim() == 5 && b.getFim() == 8 && iguais(gantt(e), "1:5 2:3"));
	}

	static void fcfsCalculaMetricasCorretamente() {
		// P1 (5 ticks) roda de 0 a 5; P2 (3 ticks) espera 5 e roda de 5 a 8.
		Escalonador e = new Escalonador(Politica.FCFS);
		novo(e, 1, 5, 0);
		novo(e, 2, 3, 0);
		e.executaAteTerminar(100);
		Escalonador.Metricas m = e.getMetricas();
		verifica("Métricas: espera 2.5, turnaround 6.5, resposta 2.5, CPU 100%",
				m.esperaMedia == 2.5 && m.turnaroundMedio == 6.5 && m.respostaMedia == 2.5
						&& m.utilizacaoCPU == 100.0 && m.trocasContexto == 0);
	}

	static void sjfEscolheOMaisCurto() {
		Escalonador e = new Escalonador(Politica.SJF);
		novo(e, 1, 10, 0);
		novo(e, 2, 2, 0);
		novo(e, 3, 5, 0);
		e.executaAteTerminar(100);
		verifica("SJF: mais curto primeiro", iguais(gantt(e), "2:2 3:5 1:10"));
	}

	static void srtfPreemptaQuandoChegaUmMaisCurto() {
		Escalonador e = new Escalonador(Politica.SRTF);
		novo(e, 1, 10, 0);
		avanca(e, 3);                       // P1 já rodou 3, restam 7
		novo(e, 2, 2, 0);                   // chega um de 2: preempta
		e.executaAteTerminar(100);
		// P1 roda 3, troca de contexto (2 ticks) enquanto P2 roda 2, e P1 volta.
		verifica("SRTF: preempção pelo menor tempo restante", iguais(gantt(e), "1:3 2:2 1:7"));
	}

	static void roundRobinRevezaComFatiaDeTempo() {
		Escalonador e = new Escalonador(Politica.RR);
		novo(e, 1, 20, 0);
		novo(e, 2, 20, 0);
		e.executaAteTerminar(200);
		int q = Escalonador.FATIA_TEMPO;
		verifica("RR: cada um roda no máximo uma fatia por vez",
				iguais(gantt(e), "1:" + q + " 2:" + q + " 1:" + (20 - q) + " 2:" + (20 - q)));
	}

	static void roundRobinRespeitaOrdemDeChegadaNoEmpate() {
		Escalonador e = new Escalonador(Politica.PRIORIDADE_RR);
		for (int pid = 1; pid <= 5; pid++) {
			novo(e, pid, 3, 4);
		}
		e.executaAteTerminar(100);
		verifica("Prioridade igual: atendimento FIFO", iguais(gantt(e), "1:3 2:3 3:3 4:3 5:3"));
	}

	static void prioridadeMaiorTomaACpu() {
		Escalonador e = new Escalonador(Politica.PRIORIDADE_RR);
		novo(e, 1, 10, 5);
		avanca(e, 2);
		novo(e, 2, 4, 1);                   // número menor = mais prioritário
		e.executaAteTerminar(100);
		verifica("Prioridade: o mais prioritário preempta", iguais(gantt(e), "1:2 2:4 1:8"));
	}

	static void preemptadoPassaPelaTrocaDeContexto() {
		Escalonador e = new Escalonador(Politica.PRIORIDADE_RR);
		Processo p1 = novo(e, 1, 10, 5);
		avanca(e, 2);
		novo(e, 2, 20, 1);
		avanca(e, 1);
		boolean emEspera = p1.getEstado() == Processo.Estado.ESPERA && e.getFilaEspera().contains(p1);
		avanca(e, Escalonador.TEMPO_TROCA_CONTEXTO);
		boolean deVolta = p1.getEstado() == Processo.Estado.PRONTO && e.getFila().contains(p1);
		verifica("Troca de contexto dura " + Escalonador.TEMPO_TROCA_CONTEXTO + " ticks", emEspera && deVolta);
	}

	static void processoSozinhoNaoDeixaACpuOciosa() {
		Escalonador e = new Escalonador(Politica.PRIORIDADE_RR);
		novo(e, 1, 50, 3);                  // mais de 3 fatias de tempo
		e.executaAteTerminar(200);
		Escalonador.Metricas m = e.getMetricas();
		verifica("Sozinho na CPU: sem troca de contexto inútil",
				m.trocasContexto == 0 && m.utilizacaoCPU == 100.0 && iguais(gantt(e), "1:50"));
	}

	static void envelhecimentoEvitaInanicao() {
		// Um processo de prioridade 9 e uma enxurrada de processos de prioridade 2 chegando sem parar.
		Escalonador e = new Escalonador(Politica.PRIORIDADE_RR);
		Processo baixa = novo(e, 1, 5, 9);
		int pid = 2;
		for (int t = 0; t < 2000 && baixa.getEstado() != Processo.Estado.FINALIZADO; t++) {
			if (t % 5 == 0) {
				novo(e, pid++, 5, 2);
			}
			e.tick();
		}
		verifica("Aging: prioridade 9 termina mesmo sob carga contínua de prioridade 2",
				baixa.getEstado() == Processo.Estado.FINALIZADO);
		// Regressão: o processo envelhecido perdia o ganho ao entrar na CPU e era
		// preemptado no tick seguinte, rodando 1 tick por vez. Agora roda os 5 de uma vez.
		verifica("Aging: o processo envelhecido não é preemptado logo ao entrar na CPU",
				(" " + gantt(e) + " ").contains(" 1:5 "));
	}

	static void nenhumProcessoFantasmaNaFilaDeEspera() {
		// Regressão: um processo preemptado no mesmo tick em que terminava ficava
		// FINALIZADO dentro da fila de espera para sempre.
		boolean ok = true;
		for (int semente = 0; semente < 200 && ok; semente++) {
			Random r = new Random(semente);
			Politica politica = Politica.values()[semente % Politica.values().length];
			Escalonador e = new Escalonador(politica);
			List<Processo> todos = new ArrayList<>();
			for (int t = 0; t < 600; t++) {
				if (todos.size() < 40 && r.nextInt(4) == 0) {
					todos.add(novo(e, todos.size() + 1, 1 + r.nextInt(30), r.nextInt(10)));
				}
				e.tick();
				ok &= invariantesValem(e);
			}
			e.executaAteTerminar(100000);
			ok &= invariantesValem(e) && e.getFinalizados().size() == todos.size();
		}
		verifica("Invariantes de estado em 200 simulações aleatórias (todas as políticas)", ok);
	}

	// Auxiliares -------------------------------------------------------------

	static boolean invariantesValem(Escalonador e) {
		for (Processo p : e.getFila())
			if (p.getEstado() != Processo.Estado.PRONTO) return false;
		for (Processo p : e.getFilaEspera())
			if (p.getEstado() != Processo.Estado.ESPERA) return false;
		for (Processo p : e.getFinalizados())
			if (p.getEstado() != Processo.Estado.FINALIZADO || p.getTempoRestante() != 0) return false;
		Processo exec = e.getProcessoExec();
		return exec == null || exec.getEstado() == Processo.Estado.EXEC;
	}

	static Processo novo(Escalonador e, int pid, int tempo, int prioridade) {
		Processo p = new Processo(pid, "P-" + pid, tempo, prioridade);
		e.addProcesso(p);
		return p;
	}

	static void avanca(Escalonador e, int ticks) {
		for (int i = 0; i < ticks; i++) {
			e.tick();
		}
	}

	/** Diagrama de Gantt compacto: "PID:duração" de cada trecho na CPU, ignorando ociosidade. */
	static String gantt(Escalonador e) {
		StringBuilder sb = new StringBuilder();
		int atual = -1, duracao = 0;
		for (int pid : e.getHistorico()) {
			if (pid == 0) continue;
			if (pid == atual) {
				duracao++;
			} else {
				if (atual > 0) sb.append(atual).append(':').append(duracao).append(' ');
				atual = pid;
				duracao = 1;
			}
		}
		if (atual > 0) sb.append(atual).append(':').append(duracao);
		return sb.toString().trim();
	}

	static boolean iguais(String obtido, String esperado) {
		if (!obtido.equals(esperado)) {
			System.out.println("    esperado: " + esperado + "\n    obtido:   " + obtido);
			return false;
		}
		return true;
	}

	static void verifica(String nome, boolean ok) {
		executados++;
		if (!ok) falhas++;
		System.out.println((ok ? "  ok    " : "  FALHA ") + nome);
	}
}
