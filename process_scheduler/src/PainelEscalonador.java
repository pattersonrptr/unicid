import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * PainelEscalonador - Painel que contém todos os componentes
 * gráficos e manipula a classe Escalonador.
 *
 * Toda a atualização da tela acontece em um javax.swing.Timer, que roda na
 * thread de eventos do Swing (EDT): componentes Swing não são thread-safe.
 *
 * @author patterson
 *
 */
public class PainelEscalonador extends JDesktopPane {

	private static final long serialVersionUID = 4628817960428136689L;

	/** Intervalo de atualização da tela, em milissegundos. */
	private static final int ATUALIZACAO_TELA = 50;

	/** Linha de base do gráfico de barras; a barra mais alta (TEMPO_MAX) sobe 100 pixels. */
	private static final int BASE_BARRAS = 590;
	private static final int LARGURA_BARRA = 20;
	private static final int PASSO_BARRA = 25;

	private static final Color COR_EXEC = new Color(10, 25, 125);
	private static final Color COR_PRONTO = new Color(60, 110, 200);
	private static final Color COR_ESPERA = Color.LIGHT_GRAY;

	protected Escalonador escalonador;

	private JInternalFrame novoProcesso;

	private JLabel lblPid;
	private JLabel lblNome;
	private JLabel lblTempo;
	private JLabel lblPrioridade;
	private JLabel lblTempoRestante;

	private JLabel lblRelogio, lblFinalizados, lblEspera, lblTurnaround, lblResposta, lblCPU, lblTrocas;

	private DefaultListModel<String> listModel, listModelEsp;

	private JComboBox<Politica> cbPolitica;

	private Queue<Integer> pids;
	private JSlider slider;

	/**
	 * Construtor - Inicia o Escalonador e seu relógio, os componentes gráficos
	 * e o Timer que atualiza a tela.
	 */
	public PainelEscalonador() {

		pids = new LinkedList<>();
		geraPids(1000);

		escalonador = new Escalonador();
		escalonador.iniciar();

		initComponents();

		new Timer(ATUALIZACAO_TELA, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				atualizaTela();
			}
		}).start();
	}

	/**
	 * Inicia os componentes gráficos.
	 */
	private void initComponents() {
		setLayout(null);

		JLabel lblProntos = new JLabel("Prontos (ordem de atendimento)");
		lblProntos.setBounds(12, 27, 264, 15);
		add(lblProntos);

		listModel = new DefaultListModel<>();
		JScrollPane scrollPane = new JScrollPane(new JList<>(listModel));
		scrollPane.setBounds(12, 52, 264, 131);
		add(scrollPane);

		JLabel lblTrocaContexto = new JLabel("Troca de contexto");
		lblTrocaContexto.setBounds(294, 27, 264, 15);
		add(lblTrocaContexto);

		listModelEsp = new DefaultListModel<>();
		JScrollPane scrollPane_1 = new JScrollPane(new JList<>(listModelEsp));
		scrollPane_1.setBounds(294, 52, 264, 131);
		add(scrollPane_1);

		// Processo em execução
		JLabel lblProcessoExec = new JLabel("Processo Exec.");
		lblProcessoExec.setBounds(22, 195, 112, 15);
		add(lblProcessoExec);

		lblPid = addLabel("PID:", 22, 221, 138);
		lblNome = addLabel("Nome:", 22, 248, 138);
		lblPrioridade = addLabel("Prioridade:", 22, 275, 200);
		lblTempo = addLabel("Tempo:", 22, 302, 138);
		lblTempoRestante = addLabel("Tempo Restante:", 22, 329, 209);

		// Política
		JLabel lblPolitica = new JLabel("Política de escalonamento");
		lblPolitica.setBounds(580, 27, 230, 15);
		add(lblPolitica);

		cbPolitica = new JComboBox<>(Politica.values());
		cbPolitica.setSelectedItem(escalonador.getPolitica());
		cbPolitica.setBounds(580, 52, 290, 24);
		cbPolitica.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				escalonador.setPolitica((Politica) cbPolitica.getSelectedItem());
			}
		});
		add(cbPolitica);

		// Métricas
		JLabel lblMetricas = new JLabel("Métricas");
		lblMetricas.setFont(lblMetricas.getFont().deriveFont(Font.BOLD));
		lblMetricas.setBounds(580, 90, 230, 15);
		add(lblMetricas);

		lblRelogio = addLabel("", 580, 112, 230);
		lblFinalizados = addLabel("", 580, 132, 230);
		lblEspera = addLabel("", 580, 152, 230);
		lblTurnaround = addLabel("", 580, 172, 230);
		lblResposta = addLabel("", 580, 192, 230);
		lblCPU = addLabel("", 580, 212, 230);
		lblTrocas = addLabel("", 580, 232, 230);

		// Botões
		JButton btnNovoProcesso = new JButton("Novo Processo");
		btnNovoProcesso.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				PainelEscalonador.this.createInternalFrame();
			}
		});
		btnNovoProcesso.setBounds(12, 380, 138, 25);
		add(btnNovoProcesso);

		JButton btnCarga = new JButton("Carga de exemplo");
		btnCarga.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				for (int[] p : CARGA_EXEMPLO) {
					int pid = pids.poll();
					adicionaProcesso(new Processo(pid, "P-" + pid, p[0], p[1]));
				}
			}
		});
		btnCarga.setBounds(12, 412, 138, 25);
		add(btnCarga);

		JButton btnReiniciar = new JButton("Reiniciar");
		btnReiniciar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				reiniciar();
			}
		});
		btnReiniciar.setBounds(160, 380, 110, 25);
		add(btnReiniciar);

		// Velocidade do relógio
		slider = new JSlider();
		slider.setMaximum(1000);
		slider.setMinimum(50);
		slider.setValue(Escalonador.getTempoCPU());
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Escalonador.setTempoCPU(slider.getValue());
			}
		});
		slider.setBounds(358, 397, 200, 16);
		add(slider);

		addLabel("Tempo da CPU", 406, 369, 125);
		addLabel("Lento", 576, 397, 70);
		addLabel("Rápido", 289, 398, 70);
	}

	/** Processos da carga de exemplo: {tempo, prioridade}. */
	private static final int[][] CARGA_EXEMPLO = {
		{30, 3}, {12, 1}, {45, 7}, {8, 5}, {20, 1}
	};

	private JLabel addLabel(String texto, int x, int y, int largura) {
		JLabel lbl = new JLabel(texto);
		lbl.setBounds(x, y, largura, 15);
		add(lbl);
		return lbl;
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
	 * Descarta a simulação atual e começa outra, vazia, com a política selecionada.
	 */
	private void reiniciar() {
		escalonador.parar();
		escalonador = new Escalonador((Politica) cbPolitica.getSelectedItem());
		escalonador.iniciar();
		pids.clear();
		geraPids(1000);
	}

	/**
	 * Atualiza rótulos, listas e gráficos com uma cópia do estado do escalonador.
	 * Roda na EDT, chamado pelo Timer.
	 */
	private void atualizaTela() {
		Processo processoExec = escalonador.getProcessoExec();

		if (processoExec != null) {
			lblNome.setText("Nome: " + processoExec.getNome());
			lblPid.setText("PID: " + processoExec.getPID());
			lblPrioridade.setText("Prioridade: " + prioridade(processoExec));
			lblTempo.setText("Tempo: " + processoExec.getTempo());
			lblTempoRestante.setText("Tempo Restante: " + processoExec.getTempoRestante());
		} else {
			lblNome.setText("Nome: ");
			lblPid.setText("PID: ");
			lblPrioridade.setText("Prioridade: ");
			lblTempo.setText("Tempo: ");
			lblTempoRestante.setText("Tempo Restante: ");
		}

		preencheLista(listModel, escalonador.getFila());
		preencheLista(listModelEsp, escalonador.getFilaEspera());

		Escalonador.Metricas m = escalonador.getMetricas();
		lblRelogio.setText("Relógio: " + escalonador.getRelogio() + " ticks");
		lblFinalizados.setText("Finalizados: " + m.finalizados);
		lblEspera.setText(String.format("Espera média: %.1f", m.esperaMedia));
		lblTurnaround.setText(String.format("Turnaround médio: %.1f", m.turnaroundMedio));
		lblResposta.setText(String.format("Resposta média: %.1f", m.respostaMedia));
		lblCPU.setText(String.format("Uso da CPU: %.1f%%", m.utilizacaoCPU));
		lblTrocas.setText("Trocas de contexto: " + m.trocasContexto);

		repaint();
	}

	private void preencheLista(DefaultListModel<String> model, List<Processo> processos) {
		model.clear();
		for (Processo p : processos) {
			model.addElement(p.getNome() + "  Prioridade: " + prioridade(p) + "  Restante: " + p.getTempoRestante());
		}
	}

	/** Prioridade atual, com a original entre parênteses se o aging a alterou. */
	private static String prioridade(Processo p) {
		return p.getPrioridade() == p.getPrioridadeOriginal()
				? Integer.toString(p.getPrioridade())
				: p.getPrioridade() + " (era " + p.getPrioridadeOriginal() + ")";
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

	/*
	 * (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 * Desenha os gráficos de barra dos processos: altura cinza = tempo total,
	 * parte colorida = tempo restante, número abaixo = prioridade.
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		g2d.setColor(new Color(185, 185, 185));
		g2d.fillRect(0, 0, this.getWidth(), this.getHeight());

		if (escalonador == null) {
			return;
		}

		g2d.setColor(Color.BLACK);
		g2d.drawString("Processos: barra = tempo total, parte colorida = restante, número abaixo = prioridade", 12, 470);

		int x = 12 - PASSO_BARRA;

		Processo p = escalonador.getProcessoExec();
		if (p != null) {
			x += PASSO_BARRA;
			desenhaBarra(g2d, p, x, COR_EXEC, Color.WHITE);
		}

		for (Processo pronto : escalonador.getFila()) {
			x += PASSO_BARRA;
			desenhaBarra(g2d, pronto, x, COR_PRONTO, Color.WHITE);
		}

		for (Processo espera : escalonador.getFilaEspera()) {
			x += PASSO_BARRA;
			desenhaBarra(g2d, espera, x, COR_ESPERA, Color.DARK_GRAY);
		}

		// Legenda
		int ly = 280;
		desenhaLegenda(g2d, COR_EXEC, "Em execução", ly);
		desenhaLegenda(g2d, COR_PRONTO, "Pronto", ly + 20);
		desenhaLegenda(g2d, COR_ESPERA, "Troca de contexto", ly + 40);
	}

	private void desenhaBarra(Graphics2D g2d, Processo p, int x, Color cor, Color corTexto) {
		if (x + LARGURA_BARRA > getWidth()) {
			return; // fila maior que a tela: as listas acima mostram todos
		}
		int altura = p.getTempo();

		g2d.setColor(Color.DARK_GRAY);
		g2d.drawRect(x, BASE_BARRAS - altura - 1, LARGURA_BARRA, altura + 1);
		g2d.setColor(Color.GRAY);
		g2d.fillRect(x + 1, BASE_BARRAS - altura, LARGURA_BARRA - 1, altura);

		int restante = p.getTempoRestante();
		g2d.setColor(cor);
		g2d.fillRect(x + 1, BASE_BARRAS - restante, LARGURA_BARRA - 1, restante);

		g2d.setColor(corTexto);
		g2d.drawString(Integer.toString(restante), x + 3, BASE_BARRAS - 3);
		g2d.setColor(Color.BLACK);
		g2d.drawString(Integer.toString(p.getPrioridade()), x + 7, BASE_BARRAS + 16);
	}

	private void desenhaLegenda(Graphics2D g2d, Color cor, String texto, int y) {
		g2d.setColor(cor);
		g2d.fillRect(580, y, 12, 12);
		g2d.setColor(Color.BLACK);
		g2d.drawString(texto, 598, y + 11);
	}

	public Queue<Integer> getPids() {
		return pids;
	}

	public void setPids(Queue<Integer> pids) {
		this.pids = pids;
	}
}
