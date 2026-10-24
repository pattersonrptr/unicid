import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Classe que implementa o algorítmo de escalonamento de processos.
 * Usa o Escalonamento Circular 
 * 
 * @author patterson
 *
 */
public class Escalonador {

	/**
	 * Comparador que mantém os processos prontos ordenados por prioridade.
	 */
	private Comparator<Processo> comparator = new priorityComparator();
	
	/**
	 * Lista de processos prontos.
	 */
	private PriorityQueue<Processo> fila;

	/**
	 * Fila de processos em estado de Espera (troca de contexto)
	 */
	private Queue<Processo> filaEspera;

	/**
	 * Processo em execução.
	 */
	private Processo processoExec;
	
	/**
	 * Simula o Clock interno do processador, inicialmente 1 segundo
	 * eu sei que 1 segundo é uma eternidade mas isso é apenas um simulador ;)
	 */
	private static int tempoCPU = 1000;

	/**
	 * Classe que implementa o comparador de prioridades.
	 * @author patterson
	 *
	 */
	private class priorityComparator implements Comparator<Processo> 
	{
		@Override
		public int compare(Processo x, Processo y) {
			return x.getPrioridade() - y.getPrioridade();
		}
	}

	/**
	 * Construtor - inicía as filas e chama o método
	 * que faz o escalonamento escalonamento();
	 * @throws InterruptedException
	 */
	public Escalonador() throws InterruptedException {
		fila = new PriorityQueue<>(comparator);
		filaEspera = new LinkedList<>();

		escalonamento();
		verificaFilaEspera();
	}	

	/**
	 * Faz o escalonamento dos processos, implementa o
	 * escalonamento circular com prioridade.
	 */
	public void escalonamento() {

		new Thread() {
			@Override
			public void run() {
				while (true) {

					try {
						Thread.sleep(33); // Apenas um delay mínimo para não sobrecarregar o processador.
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					/* Se tem processo em execução e o Processo em execução finalizou, então
					 * mata o processo (aqui eu apenas o defino como nulo, mas o coletor de lixo
					 * automático do JAVA (Garbage Collector) se encarrega de limpar Objetos 
					 * nulos da memória.
					 */
					if (processoExec != null && processoExec.getEstado() == Processo.Estado.FINALIZADO) {
						processoExec = null;
					}

					/* Se tem processo em execução e passou para espera (troca de contexto)
					 * então ele vai para a fila de espera e o tempo em estado de espera 
					 * (troca de contexto) é acrescido.
					 */
					if (processoExec != null && processoExec.getEstado() == Processo.Estado.ESPERA) {
						Processo processoTemp = processoExec;
						processoExec = null;
						processoTemp.espera();
						filaEspera.add(processoTemp);
					}
					/*
					 * Se há processos prontos na fila (a fila não é vazia), então 
					 * 	Se Não há processo executando, passa o primeiro processo da 
					 * 		fila para execução, e remove=o da fila de pronto.
					 *  Se tem processo em execução, então
					 *  	faz a preempção do processo em execução SE o primeiro processo PRONTO
					 *  	tiver prioridade maior ( No caso quanto menor o número, maior a prioridade de 9 á 0 ).
					 */
					if (! fila.isEmpty()) {
						if( processoExec == null) {
							setProcessoExec(fila.remove());
							processoExec.executa();
						} else
							if(fila.peek().getPrioridade() < processoExec.getPrioridade()) {
								Processo processoTemp = processoExec;
								processoExec = fila.remove(); 
								processoExec.executa();
								processoTemp.espera();
								filaEspera.add(processoTemp);
							}
					}
				}
			}
		}.start();
	}

	/**
	 * Monitora os processos em espera (troca de contesxto)
	 * e os insere na fila de Prontos quando seus estados passarem para
	 * PRONTO.
	 */
	private void verificaFilaEspera() {
		new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(33);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	
					if ( ! filaEspera.isEmpty()) {
						
						Iterator<Processo> it = filaEspera.iterator();
						
						while(it.hasNext()) {
							
							Processo p = it.next();
							
							if(p.getEstado() == Processo.Estado.PRONTO) {
								if (filaEspera.remove(p))
									fila.add(p);
							}
						}

					}
				}
			}
		}.start();
	}
	
	// Getters and Setters

	public void addProcesso(Processo p) {
		fila.add(p);
	} 
	
	public synchronized PriorityQueue<Processo> getFila() {
		return fila;
	}

	public void setFila(PriorityQueue<Processo> fila) {
		this.fila = fila;
	}

	public Processo getProcessoExec() {
		return processoExec;
	}

	public void setProcessoExec(Processo processoExec) {
		this.processoExec = processoExec;
	}

	public synchronized Queue<Processo> getFilaEspera() {
		return filaEspera;
	}

	public void setFilaEspera(Queue<Processo> filaEspera) {
		this.filaEspera = filaEspera;
	}

	public static int getTempoCPU() {
		return tempoCPU;
	}

	public static void setTempoCPU(int tempoCPU) {
		Escalonador.tempoCPU = tempoCPU;
	}

}
