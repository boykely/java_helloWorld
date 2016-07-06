import java.awt.Color;
import java.awt.Dialog;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.ejml.simple.SimpleMatrix;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import newGit.ExternProcess;

import javax.imageio.ImageIO;


public class Form extends JFrame implements BriefDescriptorListener
{
	private ImageContainer _containerF;
	private ImageContainer _containerG;
	private ImageContainer _containerSVBRDF;
	private ImageContainer _containerNext;
	private ImageContainer _containerNextCV;
	private ImageContainer _containerGradientF;
	private ImageContainer _containerGradient;
	private JScrollPane _scrollF;
	private JScrollPane _scrollG;
	private JScrollPane _scrollSVBRDF;
	private JScrollPane _scrollNext;
	private JScrollPane _scrollNextCV;
	private JScrollPane _scrollGradientF;
	private JScrollPane _scrollGradient;
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
	private int[][] pairwisePixel0;
	private int[][] pairwisePixel1;
	private int[][] pairwisePixel2;
	private String[] masterB;
	private Hashtable<Object, int[]> masterBDict;
	private BufferedImage[][] flashTiles;
	private BufferedImage[][] guideTiles;
	//variable pour le traitement opencv
	private Mat[][] newFlashTilesCV;//it will contains the reference of F1
	private Mat[][] flashTilesCV;
	private Mat[][] guideTilesCV;
	private Mat[][] f1;
	private Mat[][] f2;//it will contains the reference of F2
	private Mat[] imageDataCV;
	private Mat origin;
	private Thread[] listThread;
	private List<Thread> listThread2;
	//pour test image
	private int next=0;
	private int previous=0;
	private int nnn=0;
	public static int tileProcessed=0;
	public static int start=0;
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
		_containerNext=new ImageContainer();
		_containerNextCV=new ImageContainer();
		_containerGradient=new ImageContainer();
		_containerGradientF=new ImageContainer();
		_containerToolBox=new JPanel();
		
		_scrollF=new JScrollPane(_containerF);		
		_scrollG=new JScrollPane(_containerG);
		_scrollSVBRDF=new JScrollPane(_containerSVBRDF);
		_scrollNext=new JScrollPane(_containerNext);
		_scrollNextCV=new JScrollPane(_containerNextCV);
		_scrollGradient=new JScrollPane(_containerGradient);
		_scrollGradientF=new JScrollPane(_containerGradientF);
		_scrollF.setPreferredSize(new Dimension(400,400));
		_scrollG.setPreferredSize(new Dimension(400,400));
		_scrollSVBRDF.setPreferredSize(new Dimension(197, 197));
		_scrollNext.setPreferredSize(new Dimension(197, 197));
		_scrollNextCV.setPreferredSize(new Dimension(197, 197));
		_scrollGradient.setPreferredSize(new Dimension(197,197));
		
		
		_containerF.setBackground(Color.black);
		_containerG.setBackground(Color.red);
		
		
		_layout=new FlowLayout();
		_layout.setHgap(10);		
		_layoutToolBox=new GridLayout(1,2);
		_layoutToolBox.setHgap(5);
		_layoutToolBox.preferredLayoutSize(_containerToolBox);
		
			
		
		_btnLoad=new JButton("Charger Photos");		
		_btnProcess=new JButton("Défiler Tiles");
		
		
		_containerToolBox.setLayout(_layoutToolBox);	
		_containerToolBox.add(_btnLoad);
		_containerToolBox.add(_btnProcess);
		this.setLayout(_layout);		
		this.add(_scrollF);
		this.add(_scrollG);
		this.add(_containerToolBox);	
		this.add(_scrollSVBRDF);
		this.add(_scrollNext);
		this.add(_scrollNextCV);
		this.add(_scrollGradientF);
		this.add(_scrollGradient);
		
		imageData=new BufferedImage[2];
		imageDataCV=new Mat[2];
		//init event
		initEvent();
		//
		//init pairwise pixel
		pairwisePixel0=BriefDescriptor.nPairPixel(96, 33);
		pairwisePixel1=BriefDescriptor.nPairPixel(128, 17);
		pairwisePixel2=BriefDescriptor.nPairPixel(32, 5);
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
				/*if(Form.tileProcessed!=tileLenH*tileLenW)return;			
				if(next<tileLenW)
				{
					//showCvDataToJava(flashTilesCV[nnn][next],_containerNext);
					System.out.println(nnn+"-"+next);
					showTile(flashTiles[nnn][next], _containerNext);					
					showCvDataToJava(newFlashTilesCV[nnn][next], _containerNextCV);
					next++;
				}
				else
				{
					nnn++;
					if(nnn<tileLenH)
					{
						next=0;
						//showCvDataToJava(flashTilesCV[nnn][next],_containerNext);
						System.out.println(nnn+"-"+next);
						showTile(flashTiles[nnn][next], _containerNext);						
						showCvDataToJava(newFlashTilesCV[nnn][next], _containerNextCV);
					}
					else{
						next=0;nnn=0;
					}
				}*/
				/*if(Form.tileProcessed!=1)return;			
				if(next<tileLenW)
				{
					//showCvDataToJava(flashTilesCV[nnn][next],_containerNext);
					System.out.println(nnn+"-"+next);
					if(newFlashTilesCV[nnn][next]!=null)
					{
						
						showTile(flashTiles[nnn][next], _containerNext);					
						showCvDataToJava(newFlashTilesCV[nnn][next], _containerNextCV);
					}
					
					next++;
				}
				else
				{
					nnn++;
					if(nnn<tileLenH)
					{
						next=0;
						//showCvDataToJava(flashTilesCV[nnn][next],_containerNext);
						System.out.println(nnn+"-"+next);
						if(newFlashTilesCV[nnn][next]!=null)
						{
							
							showTile(flashTiles[nnn][next], _containerNext);						
							showCvDataToJava(newFlashTilesCV[nnn][next], _containerNextCV);
						}
						
					}
					else{
						next=0;nnn=0;
					}
				}*/
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
			imageDataCV[0]=Imgcodecs.imread(imageFiles[0].getAbsolutePath());
			imageDataCV[1]=Imgcodecs.imread(imageFiles[1].getAbsolutePath());	
			System.out.println(imageDataCV[0]);
			System.out.println(imageDataCV[1]);
			_containerF.image=imageData[0];
			_containerG.image=imageData[1];
			
			_containerF.setIcon(new ImageIcon(imageData[0]));
			_containerG.setIcon(new ImageIcon(imageData[1]));
			//init image information
	        width=imageData[0].getWidth();
	        height=imageData[0].getHeight();
	        tileSize=192;
	        tileLenW = width / tileSize;//pour un test on va retrecir
	        tileLenH = height / tileSize;	
	        System.out.println(tileLenH+" - "+tileLenW);
	        flashTiles=new BufferedImage[tileLenH][tileLenW];
	        guideTiles=new BufferedImage[tileLenH][tileLenW];
	        flashTilesCV=new Mat[tileLenH][tileLenW];
	        guideTilesCV=new Mat[tileLenH][tileLenW];
	        f2=new Mat[1][3];
	        f1=new Mat[1][3];
	        newFlashTilesCV=new Mat[tileLenH][tileLenW];	      
			initTiles();			
		} 
		catch (IOException e) {
			// TODO: handle exception
			System.out.println("Exception lancé par chargement d'images:");
		}
		catch(Exception e){
			System.out.println("Select images:"+e.getMessage());
			JDialog exp=new JDialog(this, "Exception erreur",true);
			exp.setVisible(true);
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
				flashTilesCV[i][j]=new Mat(tileSize,tileSize,imageDataCV[0].type());
				guideTilesCV[i][j]=new Mat(tileSize,tileSize,imageDataCV[0].type());				
				//ligne
				for(int s=0;s<tileSize;s++)
				{
					//colonne
					for(int t=0;t<tileSize;t++)
					{
						rgb=imageData[0].getRGB(t+tileSize*j, s+tileSize*i);
						rgb_=imageData[1].getRGB(t+tileSize*j, s+tileSize*i);
						flashTiles[i][j].setRGB(t, s, rgb);
						guideTiles[i][j].setRGB(t, s, rgb_);						
					}					
				}				
			}			
		}
		//init tiles for modification
		Mat temp;
		Mat tempG;
		int index=0;
		for(int i=0;i<tileLenH;i++)
		{
			for(int j=0;j<tileLenW;j++)
			{
				temp=convertTileToCV(flashTiles[i][j]);
				tempG=convertTileToCV(guideTiles[i][j]);
				flashTilesCV[i][j]=temp;
				guideTilesCV[i][j]=tempG;
				//this is only used for testing TextureStatisticsTransfert function. we can erase later
				//BriefDescriptor.saveTile(flashTilesCV[i][j], "C:\\Users\\ralambomahay1\\Downloads\\Java_workspace\\newGit\\Data\\source_Ref_"+i+"_"+j+".jpg");
				//BriefDescriptor.saveTile(guideTilesCV[i][j], "C:\\Users\\ralambomahay1\\Downloads\\Java_workspace\\newGit\\Data\\source_Tar_"+i+"_"+j+".jpg");
				//BriefDescriptor.saveTile(flashTilesCV[i][j], "C:\\Users\\ralambomahay1\\Downloads\\Java_workspace\\newGit\\Data\\SampleTransportReflectance\\optimize4\\source_tile_"+i+"_"+j+".jpg");
			}
		}
		System.out.println("Initialisation des tiles terminé");
		//listThread=new Thread[tileLenH*tileLenW];
		listThread=new Thread[tileLenH*tileLenW];
		listThread2=new ArrayList<>();
		System.out.println("Initialisation des threads terminé");
		//System.out.println("Reflectance Sample Transport commence...");
		//System.err.println(tileLenH+"-"+tileLenW);//12-16
		//reflectanceSampleTransport(true);
		//reflectanceSampleTransport();
		textureStatisticsTransfert();
	}
	private void textureStatisticsTransfert()
	{
		//flashTilesCV contains the original source tiles image
		//guideTilesCV contains the source tile image after step 2 => optimize image
		System.out.println("Texture statistique transfert commence...");
		Mat newsourceCV=new Mat(flashTilesCV[0][0].rows(),flashTilesCV[0][0].cols(),CvType.CV_8UC3);
		for(int i=0;i<tileLenH;i++)
		{
			for(int j=0;j<tileLenW;j++)
			{				
				Mat result=new Mat();
				int pixels=flashTilesCV[0][0].cols()*flashTilesCV[0][0].rows();
				double[][] meanRGB=ExternProcess.MeanRGBChannel(flashTilesCV[i][j],guideTilesCV[i][j]);
				SimpleMatrix A=ExternProcess.computeA(pixels,flashTilesCV[i][j],meanRGB);
				SimpleMatrix C=ExternProcess.computeC(pixels,A);
				SimpleMatrix P=ExternProcess.computeP(C);
				SimpleMatrix Pt=ExternProcess.computePt(C);
				flashTilesCV[i][j].convertTo(flashTilesCV[i][j], CvType.CV_64FC3);
				guideTilesCV[i][j].convertTo(guideTilesCV[i][j], CvType.CV_64FC3);
				Mat refU=ExternProcess.RGB2PCAColor(flashTilesCV[i][j], Pt.transpose(), meanRGB[0]);
				Mat tarU=guideTilesCV[i][j].clone();
				refU.convertTo(refU, CvType.CV_8UC3);
				tarU.convertTo(tarU, CvType.CV_8UC3);
				result=ExternProcess.TextureMatching(refU, tarU, result);
				result.convertTo(result, CvType.CV_64FC3);
				Mat res=ExternProcess.PCA2RGBColor(result, Pt, meanRGB);
				ExternProcess.saveTile(res, "C:\\Users\\ralambomahay1\\Downloads\\Java_workspace\\newGit\\Data\\SampleTransportReflectance\\TT\\TF_"+i+"_"+j+".jpg");
				System.out.println(i+","+j+" tile traité.");
			}
		}
		
		System.out.println("Fin step 2");
	}
	//this is used to create a thread pool
	private void reflectanceSampleTransport(boolean end)
	{
		Date d=new Date();
		Random rd=new Random(d.getTime());
		int ml=rd.nextInt(tileLenH);
		int mc=rd.nextInt(tileLenW);
		//System.err.println(ml+","+mc);
		//BufferedImage masterTile=flashTiles[10][9];
		BufferedImage masterTile=flashTiles[5][5];
		//we will create new matrice image to manipulate inside each thread
		Mat masterTileFCV=convertTileToCV(masterTile);
		//Mat masterTileGCV=convertTileToCV(guideTiles[10][9]);//pour les test sup
		Mat masterTileGCV=convertTileToCV(guideTiles[5][5]);
		BriefDescriptor.saveTile(masterTileGCV, "C:\\Users\\ralambomahay1\\Downloads\\Java_workspace\\newGit\\Data\\SampleTransportReflectance\\master_tileOrigin.jpg");
		BriefDescriptor.gaussianTiles(masterTileGCV,15.0,4);
		BriefDescriptor.saveTile(masterTileFCV, "C:\\Users\\ralambomahay1\\Downloads\\Java_workspace\\newGit\\Data\\SampleTransportReflectance\\master_tile.jpg");
		BriefDescriptor.saveTile(masterTileGCV, "C:\\Users\\ralambomahay1\\Downloads\\Java_workspace\\newGit\\Data\\SampleTransportReflectance\\master_tileGaussian.jpg");
		//calcul une bonne fois pour toute masterB
		masterB=new String[masterTileGCV.cols()*masterTileGCV.rows()];
		masterBDict=new Hashtable<>();
		Mat guideCV=imageDataCV[1].clone();
		BriefDescriptor.gaussianTiles(guideCV,15.0,4);
		BriefDescriptor.brief(guideCV,masterTileGCV,5,5, masterB, masterBDict, pairwisePixel0, pairwisePixel1, pairwisePixel2);		
		//
		showTile(masterTile,_containerSVBRDF);
		int k=0;		
		//Pour chaque tile (i,j)
		//ligne
		int z=0;
		int testLimite=0;
		ExecutorService executor = Executors.newFixedThreadPool(8);
		for(int i=0;i<tileLenH;i++)
		{
			//colonne
			for(int j=0;j<tileLenW;j++)
			{	
				BriefDescriptor brief=new BriefDescriptor(i,j,32,5,tileSize,guideCV);
				brief.pairwisePixel0=pairwisePixel0;
				brief.pairwisePixel1=pairwisePixel1;
				brief.pairwisePixel2=pairwisePixel2;
				brief.masterB=masterB;
				brief.masterBDict=masterBDict;
				brief.container_ref_final=_containerNextCV;
				brief.container_ref_init=_containerNext;
				brief.container_ref_gradient=_containerGradient;
				brief.container_ref_gradientF=_containerGradientF;
				brief.newflashTilesCV=newFlashTilesCV;
				brief.setMaster(masterTile);
				brief.setMasterCV(masterTileFCV,masterTileGCV);
				brief.setSourceFG(flashTiles[i][j], guideTiles[i][j]);
				brief.setSourceFGCV(convertTileToCV(flashTiles[i][j]),convertTileToCV(guideTiles[i][j]));
				brief.AddBriefDescriptorEventListener((BriefDescriptorListener)this);
	            executor.execute(brief);
			}
		}
		executor.shutdown();
		 while (!executor.isTerminated()) 
	     {
	     }
		System.out.println("fin reflectance transport");
	}
	private void reflectanceSampleTransport()
	{
		Date d=new Date();
		Random rd=new Random(d.getTime());
		int ml=rd.nextInt(tileLenH);
		int mc=rd.nextInt(tileLenW);
		//System.err.println(ml+","+mc);
		//BufferedImage masterTile=flashTiles[10][9];
		BufferedImage masterTile=flashTiles[5][5];
		//we will create new matrice image to manipulate inside each thread
		Mat masterTileFCV=convertTileToCV(masterTile);
		//Mat masterTileGCV=convertTileToCV(guideTiles[10][9]);//pour les test sup
		Mat masterTileGCV=convertTileToCV(guideTiles[5][5]);
		BriefDescriptor.saveTile(masterTileGCV, "C:\\Users\\ralambomahay1\\Downloads\\Java_workspace\\newGit\\Data\\master_tileOrigin.jpg");
		BriefDescriptor.gaussianTiles(masterTileGCV,15.0,4);
		BriefDescriptor.saveTile(masterTileFCV, "C:\\Users\\ralambomahay1\\Downloads\\Java_workspace\\newGit\\Data\\master_tile.jpg");
		BriefDescriptor.saveTile(masterTileGCV, "C:\\Users\\ralambomahay1\\Downloads\\Java_workspace\\newGit\\Data\\master_tileGaussian.jpg");
		//calcul une bonne fois pour toute masterB
		masterB=new String[masterTileGCV.cols()*masterTileGCV.rows()];
		masterBDict=new Hashtable<>();
		Mat guideCV=imageDataCV[1].clone();
		BriefDescriptor.gaussianTiles(guideCV,15.0,4);
		BriefDescriptor.brief(guideCV,masterTileGCV,5,5, masterB, masterBDict, pairwisePixel0, pairwisePixel1, pairwisePixel2);		
		//
		showTile(masterTile,_containerSVBRDF);
		int k=0;		
		//Pour chaque tile (i,j)
		//ligne
		int z=0;
		int testLimite=0;
		boolean end=false;
		for(int i=2;i<tileLenH;i++)
		{
			//colonne
			if(end)break;
			for(int j=0;j<tileLenW;j++)
			{	
				
				/*BriefDescriptor brief=new BriefDescriptor(i,j,32,5,tileSize);
				brief.pairwisePixel0=pairwisePixel0;
				brief.pairwisePixel1=pairwisePixel1;
				brief.pairwisePixel2=pairwisePixel2;
				brief.masterB=masterB;
				brief.masterBDict=masterBDict;
				brief.container_ref_final=_containerNextCV;
				brief.container_ref_init=_containerNext;
				brief.container_ref_gradient=_containerGradient;
				brief.container_ref_gradientF=_containerGradientF;
				brief.newflashTilesCV=newFlashTilesCV;
				brief.setMaster(masterTile);
				brief.setMasterCV(masterTileFCV,masterTileGCV);
				brief.setSourceFG(flashTiles[i][j], guideTiles[i][j]);
				brief.setSourceFGCV(convertTileToCV(flashTiles[i][j]),convertTileToCV(guideTiles[i][j]));
				brief.AddBriefDescriptorEventListener((BriefDescriptorListener)this);
				brief.execute();*/
				if(testLimite<6)
				{
					BriefDescriptor brief=new BriefDescriptor(i,j,32,5,tileSize,guideCV);
					brief.pairwisePixel0=pairwisePixel0;
					brief.pairwisePixel1=pairwisePixel1;
					brief.pairwisePixel2=pairwisePixel2;
					brief.masterB=masterB;
					brief.masterBDict=masterBDict;
					brief.container_ref_final=_containerNextCV;
					brief.container_ref_init=_containerNext;
					brief.container_ref_gradient=_containerGradient;
					brief.container_ref_gradientF=_containerGradientF;
					brief.newflashTilesCV=newFlashTilesCV;
					brief.setMaster(masterTile);
					brief.setMasterCV(masterTileFCV,masterTileGCV);
					brief.setSourceFG(flashTiles[i][j], guideTiles[i][j]);
					brief.setSourceFGCV(convertTileToCV(flashTiles[i][j]),convertTileToCV(guideTiles[i][j]));
					brief.AddBriefDescriptorEventListener((BriefDescriptorListener)this);
					//don't forget to uncomment listThread as array
					listThread[k]=new Thread(brief);
					listThread[k].start();
					k++;
					testLimite++;
				}
				if(j==tileLenW-1)
				{
					end=!end;
					break;
				}
				
			}
		}
		//Regularize SVBRDF => it does not work yet
		/*ExternProcess.regularizeSVBRDF(f1, f2, 1, 3, 192);
		BriefDescriptor.saveTile(f2[0][0], "C:\\Users\\ralambomahay1\\Downloads\\Java_workspace\\newGit\\Data\\source_tile_relit1_0_0.jpg");
		BriefDescriptor.saveTile(f2[0][1], "C:\\Users\\ralambomahay1\\Downloads\\Java_workspace\\newGit\\Data\\source_tile_relit1_3_2.jpg");
		BriefDescriptor.saveTile(f2[0][2], "C:\\Users\\ralambomahay1\\Downloads\\Java_workspace\\newGit\\Data\\source_tile_relit1_4_1.jpg");*/
	}
	public static void showCvDataToJava(Mat m,ImageContainer container)
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
	public static void showTile(BufferedImage im,ImageContainer container)
	{
		container.image=im;
		container.setIcon(new ImageIcon(container.image));
	}
	public static Mat convertTileToCV(BufferedImage im)
	{
		Mat m=new Mat(im.getHeight(), im.getWidth(), CvType.CV_8UC3);
		byte[] data=((DataBufferByte)im.getRaster().getDataBuffer()).getData();
		m.put(0, 0, data);
		return m;
	}
	@Override
	public void oneTileProcessed(BriefDescriptorEvent evt) 
	{
		// TODO Auto-generated method stub
		System.err.println("One tile a été traité:("+evt.getLigne()+"-"+evt.getColonne()+")");
		Form.tileProcessed++;
		Form.start--;
	}	
}
