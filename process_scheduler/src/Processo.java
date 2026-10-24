public class Processo {

	/**
	 * Número de identificação do processo, cada
	 * processo deve ter PID único.
	 */
	private int PID = 0;
	
	/**
	 * Tempo de duração do processo (burst-time).
	 */
	private int tempo = 0;
	
	/**
	 * Tempo restante de execução do processo.
	 */
	private int tempoRestante = 0;
	
	/**
	 * Tempo de troca de contexto 
	 * */
	private int tempoEmEspera = 2;
	
	/**
	 *  Contador de tempo de troca de contexto 
	 *  */
	private int contaTempoEspera = 0;
	
	/** 
	 * Timeslice, constante definida para todos os processos 
	 */
	static final int fatiaTempo = 15;
	
	/** Contador de fatia de tempo
	*/
	private int contFatiaTempo = 0;

	/**
	 * Prioridade do processo
	 */
	private int prioridade = 9;

	/**
	 * Nome do processo
	 */
	private String nome = null;

	/**
	 * Enum que representa os possíveis estados do processo.
	 * @author patterson
	 *
	 */
	public static enum Estado {
		PRONTO, EXEC, ESPERA, FINALIZADO
	}

	/**
	 * Estado atual do processo.
	 */
	private Estado estado = Estado.PRONTO;

	public Processo(int PID, String nome, int tempo, int prioridade) {
		this.PID = PID;
		this.nome = nome;
		this.tempo = tempo;
		this.prioridade = prioridade;
		tempoRestante = tempo;
	}	

	/**
	 * Executa o processo, controla o timeslice e o tempo total restante de execução.
	 */
	public void executa()  {

		estado = Estado.EXEC;
		contFatiaTempo = 0;
		

		new Thread() {
			@Override
			public void run() {
				while (estado == Estado.EXEC ) {
					try {
						Thread.sleep(Escalonador.getTempoCPU());
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					if ( --tempoRestante <= 0) {
						estado = Estado.FINALIZADO;
						continue;
					}
					
					if (++contFatiaTempo >= fatiaTempo) {
						estado = Estado.ESPERA;
					}
					
				}
			}
		}.start();
	}
	
	/**
	 * Coloca o processo em estado de espera (troca de contexto) e conta o tempo de espera.
	 */
	public void espera() {
		
		estado = Estado.ESPERA;
		contaTempoEspera = 0;

		new Thread() {
			@Override
			public void run() {
				while (estado == Estado.ESPERA) {
					
					try {
						Thread.sleep(Escalonador.getTempoCPU());
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if ( ++contaTempoEspera >= tempoEmEspera ) {
						estado = Estado.PRONTO;
					}
				}
			}
		}.start();
	}


	// GETTERS & SETTERS ------------------------------------------------------

	public int getPID() {
		return PID;
	}

	public void setPID(int pID) {
		PID = pID;
	}

	public int getTempo() {
		return tempo;
	}

	public void setTempo(int tempo) {
		this.tempo = tempo;
	}

	public int getTempoRestante() {
		return tempoRestante;
	}

	public void setTempoRestante(int tempoRestante) {
		this.tempoRestante = tempoRestante;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public int getPrioridade() {
		return prioridade;
	}

	public void setPrioridade(int prioridade) {
		this.prioridade = prioridade;
	}

}
