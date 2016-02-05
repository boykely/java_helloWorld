import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Form extends JFrame 
{
	private JPanel _containerF;
	private JPanel _containerG;
	private JPanel _containerToolBox;
	private JButton _btnLoad;
	private JButton _btnProcess;
	private FlowLayout _layout;
	private GridLayout _layoutToolBox;
	public Form()
	{		
		this.setSize(1700, 900);
		this.setTitle("Stage à XLIM - Semestre 4");
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Initialisation
		_containerF=new JPanel();	
		_containerG=new JPanel();
		_containerToolBox=new JPanel();
		
		_containerF.setBackground(Color.black);
		_containerG.setBackground(Color.red);		
		
		
		_layout=new FlowLayout();
		_layout.setHgap(10);		
		_layoutToolBox=new GridLayout(1,2);
		_layoutToolBox.setHgap(5);
		_layoutToolBox.preferredLayoutSize(_containerToolBox);
		
			
		_containerF.setPreferredSize(new Dimension(800,600));
		_containerG.setPreferredSize(new Dimension(800,600));
		_btnLoad=new JButton("Charger Photos");		
		_btnProcess=new JButton("Générer SVBRDF");
		
		
		_containerToolBox.setLayout(_layoutToolBox);	
		_containerToolBox.add(_btnLoad);
		_containerToolBox.add(_btnProcess);
		this.setLayout(_layout);
		getContentPane().add(_containerF);
		getContentPane().add(_containerG);
		getContentPane().add(_containerToolBox);			
	}
}
