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
import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
	private Hashtable<String, int[]> flashIndex;
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
		//init event
		initEvent();
		//
		flashIndex=new Hashtable<>();
				
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
			if(imageFiles.length==2)imageLoaded=!imageLoaded;			
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
		initTiles();
		System.out.println("Compute");
	}
	private void initTiles()
	{
		int i = 0;
        int j = 0;
        int k = 0;
        int z = 0;
        int compte=0;
        int x=0;
        int y=0;
        int c;
        Color cc;
        for (int ligne = 0; ligne <height; ligne++)
        {
            if (ligne % tileSize == 0 && i<tileLenH)
            {
                if (flashTiles[i][ j] == null)
                {
                    flashTiles[i][ j] = new BufferedImage(tileSize, tileSize,BufferedImage.TYPE_INT_ARGB);
                    guideTiles[i][ j] = new BufferedImage(tileSize, tileSize,BufferedImage.TYPE_INT_ARGB);
                    flashIndex.put(i+"-"+j, new int[]{ligne,compte});
                    z = j;
                    k = i;                                     
                }
                y=0;   
            }
            x=0;
            for (int colonne = 0; colonne < width; colonne++)
            {
            	compte=colonne;
                if (colonne % tileSize == 0 && j < tileLenW && i<tileLenH)
                {                        
                    if (flashTiles[i][j] == null)
                    {
                        flashTiles[i][j] = new BufferedImage(tileSize, tileSize,BufferedImage.TYPE_INT_ARGB);
                        guideTiles[i][j] = new BufferedImage(tileSize, tileSize,BufferedImage.TYPE_INT_ARGB);
                        flashIndex.put(i+"-"+j, new int[]{ligne,compte});
                        k = i;
                        z = j;                        
                    }
                    j++;
                    x=0;
                }
                System.out.println(ligne+"-"+colonne);
                c=imageData[0].getRGB(colonne,ligne);
                System.out.println(c);
                cc=new Color(c);
                System.out.println("("+ligne+","+colonne+")="+cc.getRed()+"/"+cc.getGreen()+"/"+cc.getBlue());
                //System.out.println("Tile:("+(colonne-197*z)+","+(ligne-197*z)+")");
                System.out.println("Tile:("+y+","+x+")");
                //flashTiles[k][z].setRGB(x, y, c);
                x++;
                //System.out.println(k+"/"+z);
                
            }
            j = 0;
            i++;
            y++;
            x=0;
        }
	}
}
