import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * @author patterson
 * NovoProcesso - JInternalFrame responsável por criar um novo processo.
 */
public class NovoProcesso extends JInternalFrame {

	private static final long serialVersionUID = -2925483834821694020L;
	private JTextField txtNome;
	private JTextField txtPID;
	private JTextField txtPrio;
	private JTextField txtTempo;
	protected Processo processo;

	private PainelEscalonador painel;

	/**
	 * Cria a tela.
	 * @param painel - Referência à classe que a está chamando, para poder acessar seus métodos públicos.
	 * Os dados  do novo processo ( nome, tempo, prioridade) são gerados aleatóriamente mas permitindo que sejam auterados
	 * pelo usuário diretamente nos txtFilds, já o PID é obtido de uma fila de PIDS únicos de 1 a 1000 que é criada na classe 
	 * PainelEscalonador, isso garante que o PID será sempre único.
	 */
	public NovoProcesso(final PainelEscalonador painel) {

		super("Novo Processo", true, true, true, true);

		this.painel = painel;

		getContentPane().setLayout(null);

		JLabel lblNome = new JLabel("Nome");
		lblNome.setBounds(12, 45, 70, 15);
		getContentPane().add(lblNome);

		JLabel lblPid = new JLabel("PID:");
		lblPid.setBounds(12, 12, 70, 15);
		getContentPane().add(lblPid);

		JLabel lblTempo = new JLabel("Tempo:");
		lblTempo.setBounds(12, 72, 70, 15);
		getContentPane().add(lblTempo);

		JLabel lblPrioridade = new JLabel("Prioridade: ");
		lblPrioridade.setBounds(12, 103, 85, 15);
		getContentPane().add(lblPrioridade);

		txtPID = new JTextField();
		txtPID.setBounds(115, 10, 114, 19);
		txtPID.setText(new Integer(painel.getPids().poll()).toString());
		txtPID.setEditable(false);
		getContentPane().add(txtPID);
		txtPID.setColumns(10);

		txtNome = new JTextField();
		txtNome.setBounds(115, 43, 114, 19);
		txtNome.setText("P-" + txtPID.getText());
		getContentPane().add(txtNome);
		txtNome.setColumns(10);

		txtTempo = new JTextField();
		txtTempo.setBounds(115, 70, 114, 19);
		txtTempo.setText(new Integer(new Random().nextInt(99) + 1).toString()); // O tempo nunca será maior que 100 nem menor que 1
		getContentPane().add(txtTempo);
		txtTempo.setColumns(10);

		txtPrio = new JTextField();
		txtPrio.setBounds(115, 101, 114, 19);
		txtPrio.setText(new Integer(new Random().nextInt(9)).toString());
		getContentPane().add(txtPrio);
		txtPrio.setColumns(10);

		JButton btnCriar = new JButton("Criar");

		btnCriar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				/* Cria novo processo chamando o método adicionaProcesso da
				 * classe painel escalonador e passando um novo objeto
				 * processo como parâmetro. */
				NovoProcesso.this.painel.adicionaProcesso(
						new Processo(
								Integer.parseInt(NovoProcesso.this.txtPID.getText()), 
								NovoProcesso.this.txtNome.getText(), 
								Integer.parseInt(NovoProcesso.this.txtTempo.getText()), 
								Integer.parseInt(NovoProcesso.this.txtPrio.getText())
								)
						);
				
				try {
					NovoProcesso.this.setClosed(true);
				} catch (PropertyVetoException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		btnCriar.setBounds(12, 160, 117, 25);
		getContentPane().add(btnCriar);

	}
}
