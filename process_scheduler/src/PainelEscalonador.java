import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * PainelEscalonador - Painel que contém todos os componentes
 * gráficos e manipula a classe Escalonador.
 * 
 * @author patterson
 *
 */
public class PainelEscalonador extends JDesktopPane {

	private static final long serialVersionUID = 4628817960428136689L;

	protected Escalonador escalonador;

	private JInternalFrame novoProcesso;

	private JLabel lblPid;
	private JLabel lblNome;
	private JLabel lblTempo;
	private JLabel lblProcessoExec;


	private DefaultListModel<String> listModel, listModelEsp;
	private JLabel lblPrioridade;

	int listCont = 0;
	private JLabel lblTempoRestante;
	private JScrollPane scrollPane_1;
	private JList<String> list_1;

	private Queue<Integer> pids;
	private JSlider slider;

	/**
	 * Construtor - Inicía o Escalonador, inicía os componentes gráficos
	 * e chama métodos que atualizam as listas de pronto e espera 
	 * através de threads que as monitoram. Também chama o método
	 * repaintTh(), que implementa uma Thread responsável por repintar
	 * os gráficos. 
	 */
	public PainelEscalonador() {

		pids = new LinkedList<>();

		try {
			escalonador = new Escalonador();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		geraPids(1000);
		initComponents();
		verificaProcessoExec();
		atualizaListaProcessos();
		atualizaListaEspera();
		repaintTh();
	}

	/**
	 * Inicía os componentes gráficos.
	 */
	private void initComponents() {
		setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 52, 264, 131);
		add(scrollPane);

		listModel = new DefaultListModel<>();
		JList<String> list = new JList<>(listModel);
		scrollPane.setViewportView(list);

		scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(294, 52, 264, 131);
		add(scrollPane_1);

		listModelEsp = new DefaultListModel<>();
		list_1 = new JList<>(listModelEsp);
		scrollPane_1.setViewportView(list_1);

		JButton btnNovoProcesso = new JButton("Novo Processo");
		btnNovoProcesso.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				PainelEscalonador.this.createInternalFrame();
			}
		});

		btnNovoProcesso.setBounds(12, 380, 138, 25);
		add(btnNovoProcesso);

		lblProcessoExec = new JLabel("Processo Exec.");
		lblProcessoExec.setBounds(22, 195, 112, 15);
		add(lblProcessoExec);

		lblNome = new JLabel("Nome:");
		lblNome.setBounds(22, 248, 138, 15);
		add(lblNome);

		lblPid = new JLabel("PID:");
		lblPid.setBounds(22, 221, 138, 15);
		add(lblPid);

		lblTempo = new JLabel("Tempo: ");
		lblTempo.setBounds(22, 302, 138, 15);
		add(lblTempo);

		lblPrioridade = new JLabel("Prioridade: ");
		lblPrioridade.setBounds(22, 275, 112, 15);
		add(lblPrioridade);

		lblTempoRestante = new JLabel("Tempo Restante: ");
		lblTempoRestante.setBounds(22, 329, 209, 15);
		add(lblTempoRestante);

		JLabel lblProntos = new JLabel("Prontos");
		lblProntos.setBounds(12, 27, 70, 15);
		add(lblProntos);

		slider = new JSlider();
		slider.setMaximum(1000);
		slider.setMinimum(50);
		slider.setValue(1000);
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Escalonador.setTempoCPU(slider.getValue());
			}
		});
		slider.setBounds(358, 397, 200, 16);
		add(slider);

		JLabel lblTempoDaCpu = new JLabel("Tempo da CPU");
		lblTempoDaCpu.setBounds(406, 369, 125, 15);
		add(lblTempoDaCpu);

		JLabel lblLento = new JLabel("Lento");
		lblLento.setBounds(576, 397, 70, 15);
		add(lblLento);

		JLabel lblRpido = new JLabel("Rápido");
		lblRpido.setBounds(289, 398, 70, 15);
		add(lblRpido);
	}

	/** 
	 * Cria uma lista de PIDs únicos.
	 * @param max
	 */
	private void geraPids(int max) {
		for (int i = 1; i < max; i++)
			pids.add(i);

	}

	/**
	 * verifica se tem um processo em execução. Se tiver, 
	 * mostra os dados do processo na tela.
	 */
	public void verificaProcessoExec() {
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

					Processo processoExec = escalonador.getProcessoExec();

					if (processoExec != null) {
						lblNome.setText("Nome: " + processoExec.getNome());
						lblPid.setText("PID: " + processoExec.getPID());
						lblPrioridade.setText("Prioride: " + processoExec.getPrioridade());
						lblTempo.setText("Tempo: " + processoExec.getTempo());
						lblTempoRestante.setText("Tempo Restante: " + processoExec.getTempoRestante());
					} else {
						lblNome.setText("Nome: ");
						lblPid.setText("PID: ");
						lblPrioridade.setText("Prioride: ");
						lblTempo.setText("Tempo: ");
						lblTempoRestante.setText("Tempo Restante: ");
					}
				}
			}
		}.start();
	}

	/**
	 * Cria a janela interna responsável por
	 * criar um novo processo.
	 */
	public void createInternalFrame() {
		novoProcesso = new NovoProcesso(this);

		this.add(novoProcesso);
		novoProcesso.setBounds(280, 280, 420, 240);
		novoProcesso.setVisible(true);

		try {
			novoProcesso.setSelected(true);
		} catch (java.beans.PropertyVetoException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adiciona um novo processo no escalonador.
	 * @param pr - Processo a ser adicionado.
	 */
	public void adicionaProcesso(Processo pr) {
		escalonador.addProcesso(pr);
	}

	/**
	 * Monitora a lista de processos prontos e atualiza a 
	 * JList de processos prontos sempre que houver mudanças
	 * na fila de prontos do escalonador.
	 */
	public void atualizaListaProcessos() {
		new Thread() {
			@Override
			public void run() {

				while(true) {

					try {
						Thread.sleep(100);

						if (listModel.size() != escalonador.getFila().size()) {

							listModel.clear();

							Iterator<Processo> it = escalonador.getFila().iterator();

							while(it.hasNext()) {
								Processo p = it.next();
								listModel.addElement(p.getNome() + " Prioridade: " + p.getPrioridade());
							}
						}
					} catch (InterruptedException  | ArrayIndexOutOfBoundsException ex) {
						// TODO Auto-generated catch block
						ex.printStackTrace();
					}
				}
			}
		}.start();
	}

	/**
	 * Monitora a lista de processos em espera e atualiza a 
	 * JList de processos em espera sempre que houver mudanças
	 * na fila de espera do escalonador.
	 */
	public void atualizaListaEspera() {
		new Thread() {
			@Override
			public void run() {

				while(true) {

					try {
						Thread.sleep(100);

						if (listModelEsp.size() != escalonador.getFilaEspera().size()) {

							listModelEsp.clear();

							Iterator<Processo> it = escalonador.getFilaEspera().iterator();

							while(it.hasNext()) {

								Processo p = it.next();

								listModelEsp.addElement(p.getNome() + " Prioridade: " + p.getPrioridade());
							}
						}

					} catch (InterruptedException  | ArrayIndexOutOfBoundsException ex) {
						// TODO Auto-generated catch block
						ex.printStackTrace();
					}
				}

			}
		}.start();
	}

	/**
	 * Controla o tempo de atualização dos gráficos
	 * com base no valor do slider.
	 */
	private void sleep() {
		try {
			Thread.sleep(slider.getValue());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Repinta os gráficos e gera o efeito de animação,
	 * o tempo entre cara repaint depende do tempo definido
	 * no slider.
	 */
	public void repaintTh() {
		new Thread() {
			@Override
			public void run() {
				while (true) {
					repaint(); 
					PainelEscalonador.this.sleep();
				}
			}
		}.start();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 * Método que desenha os gráficos de barra dos processos.
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		g2d.setColor(new Color(185, 185, 185));
		g2d.fillRect(0, 0, this.getWidth(), this.getHeight());

		/* Variáveis que controlam a posição onde cada barra será desenhada
		 * x e y definem onde as barras serão desenhadas
		 * largura e altura definem a largura e a altura das barras
		 */
		int x = 0, y = 0, largura = 0, altura = 0;

		if (escalonador != null) {
			Processo p = escalonador.getProcessoExec();

			if (p != null) {
				x = 250;
				y = p.getTempo();
				largura = 20;
				altura = p.getTempo();

				//				g2d.setColor(Color.BLUE);
				//				g2d.fillOval(x - 3, 350 - y - 30, 25, 25);
				//				g2d.setColor(Color.BLACK);
				//				g2d.drawString("E", x + 5, 350 - y - 13);

				g2d.setColor(Color.DARK_GRAY);
				g2d.drawRect(x, 350 - y + 1, largura, altura + 1);
				g2d.setColor(Color.GRAY);
				g2d.fillRect(x +1 , 352 - y , largura - 1, altura);
				g2d.setColor(new Color(10, 25, 125));
				y = altura = p.getTempoRestante();
				g2d.fillRect(251, 352 - y , largura - 1, altura);

				g2d.setColor(Color.WHITE);
				g2d.drawString(new Integer(p.getTempoRestante()).toString(), 252, 348);
				g2d.setColor(Color.BLACK);
				g2d.drawString(new Integer(p.getPrioridade()).toString(), 255, 370);
			}

			Iterator<Processo> it = escalonador.getFila().iterator();
			x = 250;
			largura = 20;

			while(it.hasNext()) {
				p = it.next();
				x += 25;
				y = p.getTempo();
				altura = p.getTempo();

				//				g2d.setColor(Color.GREEN);
				//				g2d.fillOval(x - 3, 350 - y - 30, 25, 25);
				//				g2d.setColor(Color.BLACK);
				//				g2d.drawString("P", x + 5, 350 - y - 13);

				g2d.setColor(Color.DARK_GRAY);
				g2d.drawRect(x, 350 - y + 1, largura, altura + 1);
				g2d.setColor(Color.GRAY);
				g2d.fillRect(x + 1, 352 - y , largura - 1, altura);
				g2d.setColor(new Color(10, 25, 125));
				y = altura = p.getTempoRestante();
				g2d.fillRect(x + 1, 352 - y , largura - 1, altura);

				g2d.setColor(Color.WHITE);
				g2d.drawString(new Integer(p.getTempoRestante()).toString(), x + 3, 348);
				g2d.setColor(Color.BLACK);
				g2d.drawString(new Integer(p.getPrioridade()).toString(), x + 5, 370);
			}

			it = escalonador.getFilaEspera().iterator();

			while(it.hasNext()) {
				p = it.next();
				x += 25;
				y = p.getTempo();
				altura = p.getTempo();

				//				g2d.setColor(Color.YELLOW);
				//				g2d.fillOval(x - 3, 350 - y - 30, 25, 25);
				//				g2d.setColor(Color.BLACK);
				//				g2d.drawString("W", x + 5, 350 - y - 13);

				g2d.setColor(Color.DARK_GRAY);
				g2d.drawRect(x, 350 - y + 1, largura, altura + 1);
				g2d.setColor(Color.GRAY);
				g2d.fillRect(x + 1, 352 - y , largura - 1, altura);
				g2d.setColor(Color.LIGHT_GRAY);
				y = altura = p.getTempoRestante();
				g2d.fillRect(x + 1, 352 - y , largura - 1, altura);

				g2d.setColor(Color.DARK_GRAY);
				g2d.drawString(new Integer(p.getTempoRestante()).toString(), x + 3, 348);
				g2d.setColor(Color.BLACK);
				g2d.drawString(new Integer(p.getPrioridade()).toString(), x + 5, 370);

			}
		}
	}

	@Override
	public void update(Graphics g) {
		paint(g);
	}

	public Queue<Integer> getPids() {
		return pids;
	}

	public void setPids(Queue<Integer> pids) {
		this.pids = pids;
	}
}





