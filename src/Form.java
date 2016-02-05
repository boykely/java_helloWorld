import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Form extends JFrame 
{
	private JPanel _container;
	public Form()
	{		
		this.setSize(1700, 900);
		this.setTitle("Stage à XLIM - Semestre 4");
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//Initialisation
		_container=new JPanel();
		_container.setBackground(Color.GRAY);
		
		this.setContentPane(_container);
	}
}
