import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.imageio.ImageIO;

public class Form extends JFrame 
{
	private ImageContainer _containerF;
	private ImageContainer _containerG;
	private JScrollPane _scrollF;
	private JScrollPane _scrollG;
	private JPanel _containerToolBox;
	private JButton _btnLoad;
	private JButton _btnProcess;
	private FlowLayout _layout;
	private GridLayout _layoutToolBox;
	private File[] imageFiles;
	private BufferedImage[] imageData;
	private boolean imageLoaded=false;
	public Form()
	{		
		this.setSize(1000, 900);
		this.setTitle("Stage à XLIM - Semestre 4");
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Initialisation
		_containerF=new ImageContainer();	
		_containerG=new ImageContainer();
		_containerToolBox=new JPanel();
		_scrollF=new JScrollPane(_containerF);
		_scrollG=new JScrollPane(_containerG);
		
		_containerF.setBackground(Color.black);
		_containerG.setBackground(Color.red);		
		
		
		_layout=new FlowLayout();
		_layout.setHgap(10);		
		_layoutToolBox=new GridLayout(1,2);
		_layoutToolBox.setHgap(5);
		_layoutToolBox.preferredLayoutSize(_containerToolBox);
		
		_scrollF.setPreferredSize(new Dimension(400,600));
		//_containerF.setPreferredSize(new Dimension(400,600));
		//_containerG.setPreferredSize(new Dimension(400,600));		
		
		_btnLoad=new JButton("Charger Photos");		
		_btnProcess=new JButton("Générer SVBRDF");
		
		
		_containerToolBox.setLayout(_layoutToolBox);	
		_containerToolBox.add(_btnLoad);
		_containerToolBox.add(_btnProcess);
		this.setLayout(_layout);		
		getContentPane().add(_scrollF);
		getContentPane().add(_scrollG);
		getContentPane().add(_containerToolBox);	
		
		imageData=new BufferedImage[2];
		//init event
		initEvent();
	}
	private void initEvent()
	{
		_btnLoad.addMouseListener(new MouseListener() {	
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				selectImages();
			}
		});
		_btnProcess.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				if(imageLoaded)
				{
					computeSVBRDF();
				}
			}
		});
	}
	private void selectImages()
	{
		FileDialog dlg=new FileDialog(this, "Choisier images",FileDialog.LOAD);
		dlg.setMultipleMode(true);
		dlg.setVisible(true);
		imageFiles=dlg.getFiles();
		if(imageFiles.length==2)imageLoaded=!imageLoaded;
		try 
		{
			imageData[0]=ImageIO.read(imageFiles[0]);
			imageData[1]=ImageIO.read(imageFiles[1]);
			_containerF.image=imageData[0];
			_containerG.image=imageData[1];

			_containerF.repaint();
			_containerG.repaint();
			
		} 
		catch (IOException e) {
			// TODO: handle exception
			System.out.println("Exception lancé par chargement d'images:");
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
	private void computeSVBRDF()
	{
		
	}
	
}
