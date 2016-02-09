import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import javax.imageio.ImageIO;


public class Form extends JFrame 
{
	private ImageContainer _containerF;
	private ImageContainer _containerG;
	private ImageContainer _containerSVBRDF;
	private JScrollPane _scrollF;
	private JScrollPane _scrollG;
	private JScrollPane _scrollSVBRDF;
	private JPanel _containerToolBox;
	private JButton _btnLoad;
	private JButton _btnProcess;
	private FlowLayout _layout;
	private GridLayout _layoutToolBox;
	private File[] imageFiles;
	//les variables BufferedImage sont utilisé pour l'affichage seulement
	private BufferedImage[] imageData;
	private boolean imageLoaded=false;
	//image information
	private int tileSize;
	private int tileLenH;
	private int tileLenW;
	private int width;
	private int height;
	private BufferedImage[][] flashTiles;
	private BufferedImage[][] guideTiles;
	//variable pour le traitement opencv
	private Mat[][] flashTilesCV;
	private Mat[][] guideTilesCV;
	private Mat[] imageDataCV;
	private Thread[] listThread;
	public Form()
	{		
		this.setSize(1000, 900);
		this.setTitle("Stage à XLIM - Semestre 4");
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Initialisation
		_containerF=new ImageContainer();	
		_containerG=new ImageContainer();
		_containerSVBRDF=new ImageContainer();
		_containerToolBox=new JPanel();
		_scrollF=new JScrollPane(_containerF);
		_scrollG=new JScrollPane(_containerG);
		_scrollSVBRDF=new JScrollPane(_containerSVBRDF);
		_scrollF.setPreferredSize(new Dimension(400,400));
		_scrollG.setPreferredSize(new Dimension(400,400));
		_scrollSVBRDF.setPreferredSize(new Dimension(1000, 400));
		
		
		_containerF.setBackground(Color.black);
		_containerG.setBackground(Color.red);
		
		
		_layout=new FlowLayout();
		_layout.setHgap(10);		
		_layoutToolBox=new GridLayout(1,2);
		_layoutToolBox.setHgap(5);
		_layoutToolBox.preferredLayoutSize(_containerToolBox);
		
			
		
		_btnLoad=new JButton("Charger Photos");		
		_btnProcess=new JButton("Générer SVBRDF");
		
		
		_containerToolBox.setLayout(_layoutToolBox);	
		_containerToolBox.add(_btnLoad);
		_containerToolBox.add(_btnProcess);
		this.setLayout(_layout);		
		this.add(_scrollF);
		this.add(_scrollG);
		this.add(_containerToolBox);	
		this.add(_scrollSVBRDF);
		
		imageData=new BufferedImage[2];
		imageDataCV=new Mat[2];
		//init event
		initEvent();
		//
		
				
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
		try 
		{
			imageData[0]=ImageIO.read(imageFiles[0]);
			imageData[1]=ImageIO.read(imageFiles[1]);
			System.out.println("Flash image:"+imageFiles[0].getAbsolutePath());
			System.out.println("Guide image:"+imageFiles[1].getAbsolutePath());
			imageDataCV[0]=Highgui.imread(imageFiles[0].getAbsolutePath());
			imageDataCV[1]=Highgui.imread(imageFiles[1].getAbsolutePath());
			_containerF.image=imageData[0];
			_containerG.image=imageData[1];
			_containerF.setIcon(new ImageIcon(imageData[0]));
			_containerG.setIcon(new ImageIcon(imageData[1]));
			//init image information
	        width=imageData[0].getWidth();
	        height=imageData[0].getHeight();
	        tileLenW = width / 197;
	        tileLenH = height / 197;
	        tileSize=197;
	        flashTiles=new BufferedImage[tileLenH][tileLenW];
	        guideTiles=new BufferedImage[tileLenH][tileLenW];
	        flashTilesCV=new Mat[tileLenH][tileLenW];
	        guideTilesCV=new Mat[tileLenH][tileLenW];	        
			if(imageFiles.length==2)imageLoaded=!imageLoaded;
			initTiles();			
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
		System.out.println("Compute start");
		reflectanceSampleTransport();
		System.out.println("Compute end");
	}	
	private void initTiles()
	{
		int rgb;
		int rgb_;
		double[] dd;
		double[] dd_;
		//ligne
		for(int i=0;i<tileLenH;i++)
		{			
			//colonne
			for(int j=0;j<tileLenW;j++)
			{
				//System.out.println(i+"-"+j);
				flashTiles[i][j]=new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_3BYTE_BGR);
				guideTiles[i][j]=new BufferedImage(tileSize,tileSize,BufferedImage.TYPE_3BYTE_BGR);
				flashTilesCV[i][j]=new Mat(tileSize,tileSize,CvType.CV_8UC3);
				guideTilesCV[i][j]=new Mat(tileSize,tileSize,CvType.CV_8UC3);
				//ligne
				for(int s=0;s<tileSize;s++)
				{
					//colonne
					for(int t=0;t<tileSize;t++)
					{
						//System.out.println("x="+(t+(197*j))+"/y="+ (s+(197*i)));
						rgb=imageData[0].getRGB(t+tileSize*j, s+tileSize*i);
						rgb_=imageData[1].getRGB(t+tileSize*j, s+tileSize*i);
						flashTiles[i][j].setRGB(t, s, rgb);
						guideTiles[i][j].setRGB(t, s, rgb_);
						dd=imageDataCV[0].get(s, t);	
						dd_=imageDataCV[1].get(s,t);
						flashTilesCV[i][j].put(s, t, dd);
						guideTilesCV[i][j].put(s, t, dd_);
					}					
				}				
			}
			if(i==3)this.showCvDataToJava(flashTilesCV[i][4],_containerSVBRDF);
		}
		System.out.println("Initialisation des tiles terminé");
		listThread=new Thread[tileLenH*tileLenW];
		System.out.println("Initialisation des threads terminé");
	}
	private void reflectanceSampleTransport()
	{
		Date d=new Date();
		Random rd=new Random(d.getTime());
		int ml=rd.nextInt(tileLenH);
		int mc=rd.nextInt(tileLenW);
		BufferedImage masterTile=flashTiles[ml][mc];
		int k=0;
		//ligne
		for(int i=0;i<tileLenH;i++)
		{
			//colonne
			for(int j=0;j<tileLenW;j++)
			{
				BriefDescriptor brief=new BriefDescriptor(i,j,33);
				brief.setMaster(masterTile);
				brief.setSourceFG(flashTiles[i][j], guideTiles[i][j]);
				listThread[k]=new Thread(brief);
				listThread[k].start();
				k++;
			}
		}
	}
	private void showCvDataToJava(Mat m,ImageContainer container)
	{
		int type=BufferedImage.TYPE_3BYTE_BGR;
		int bufferSize=m.channels()*m.cols()*m.rows();
		byte[] data=new byte[bufferSize];
		m.get(0, 0, data);
		container.image=new BufferedImage(m.cols(), m.rows(), type);
		final byte[] containerPixels=((DataBufferByte)container.image.getRaster().getDataBuffer()).getData();
		System.arraycopy(data, 0, containerPixels, 0, bufferSize);
		container.setIcon(new ImageIcon(container.image));
	}
}
