/**
 * SIMULADOR DE ESCALONAMENTO DE PROCESSOS.

 */

import java.awt.EventQueue;

import javax.swing.JFrame;



/**
 * @author patterson
 * 
 * Main - Classe principal responsável por executar a aplicação.
 * Cria e inicializa a tela principal TelaEscalonador
 */
public class Main {

	private JFrame schedulerFrame;

	/**
	 * Lança a aplicação.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.schedulerFrame.setVisible(true);
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		});
	}

	/**
	 * Cria a aplicação.
	 */
	public Main() {
		initialize();
	}

	/**
	 * Inicializa os conteúdos do frame.
	 */
	private void initialize() {
		schedulerFrame = new TelaEscalonador("Simulador de Escalonamento de Processos");
		schedulerFrame.setBounds(100, 100, 800, 600);
		schedulerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
