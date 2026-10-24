import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;


/**
 * @author Patterson
 * TelaEscalonador extende de JFrame e é a tela principal do simulador de
 *  esclonamento de processos.
 */
public class TelaEscalonador extends JFrame {

	private static final long serialVersionUID = 235065450911306801L;
	
	/**
	 * Painel principal que contém todos os componentes
	 * é como o JPanel mas é o ideal para conter elementos JInternalFrames.
	 */
	private JDesktopPane jdpDesktop;

	/**
	 * Cria a Tela do escalonador.
	 * @param nome
	 */
	public TelaEscalonador(String nome) {
		super(nome);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 832, 521);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnArquivo = new JMenu("Arquivo");
		mnArquivo.setMnemonic('A');
		menuBar.add(mnArquivo);

		JMenuItem mntmSair = new JMenuItem("Sair");
		mntmSair.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int op = JOptionPane.showConfirmDialog(TelaEscalonador.this, "Deseja realmente sair?", "Sair", JOptionPane.YES_NO_OPTION);
				if (op == JOptionPane.YES_OPTION)
					System.exit(0);
			}
		});
		mnArquivo.add(mntmSair);

		JMenu mnAjuda = new JMenu("Ajuda");
		mnAjuda.setMnemonic('j');
		menuBar.add(mnAjuda);

		JMenuItem mntmSobre = new JMenuItem("Sobre");
		mntmSobre.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(TelaEscalonador.this, "Simulador de Escalonamento de Processos.\n\n"
						+ "Versão: 1.7.1-beta\n\n"
						+ "Criado por:\n\n"
						+ "Alexandre CA: 14044676\n"
						+ "Patterson CA: 14055899\n"
						+ "Eric Satoshi CA: 14057123\n"
						+ "Marcelo CA: 14061333\n"
						);

			}
		});
		mnAjuda.add(mntmSobre);
		getContentPane().setLayout(new BorderLayout());

		// Cria uma instância do PainelEscalonador que é um
		// JDesktopPane especializado.
		jdpDesktop = new PainelEscalonador();
		getContentPane().add(jdpDesktop, BorderLayout.CENTER);
	}
}
