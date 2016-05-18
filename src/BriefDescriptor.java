
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.ImageFilter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import newGit.ExternProcess;

public class BriefDescriptor implements Runnable 
{
	public enum TypeBrieff
	{
		type_96,type_128,type_32;
	}
	/*
	 * We must not forget that accessing image from JNI array
	 * takes more time that Java array
	 * Cf OpenCV 3.0 Computer Vision with Java
	 */
	public ImageContainer container_ref_final;//on va utiliser ceci pour l'affichage après traitement du 
	public ImageContainer container_ref_init;
	public ImageContainer container_ref_gradient;
	public ImageContainer container_ref_gradientF;
	private BufferedImage sourceF;
	private BufferedImage sourceG;
	private BufferedImage master;
	public Mat[][] newflashTilesCV;
	private Mat masterTileFCV;
	private Mat masterTileGCV;
	private Mat sourceFCV;
	private Mat sourceGCV;//this is the reference to current tile within F
	private Mat newsourceFCV;//this will hold the new tile F1
	private Mat transportMap;
	private Mat gradient;//this will hold the gradient image of source tile
	private Mat gradientF;
	private int tileSize;
	private int i;
	private int j;
	private int[] n;
	private int[] window;
	public int[][] pairwisePixel0;
	public int[][] pairwisePixel1;
	public int[][] pairwisePixel2;
	private List _listeners;
	public String[] allBriefG;
	public Hashtable<Object, int[]> allBriefGDict;
	public String[] masterB;
	public Hashtable<Object, int[]> masterBDict;
	
	public void execute()
	{
		//step1
				gaussianTiles(sourceGCV,15.0,4);		
				//gaussianTiles(masterTileGCV,5.0,4);//we smooth it inside form.java class
				allBriefG=brief(sourceGCV);
				brief(masterTileGCV,true);
				oneTileFinished(i,j);
				//Form.showTile(sourceG, container_ref_init);
				Form.showCvDataToJava(sourceFCV, container_ref_init);
				//gaussianTiles(newsourceFCV,3.0,1);//commenting because testing regularize
				//Mat nn=new Mat(newsourceFCV.rows(),newsourceFCV.cols(),CvType.CV_8UC3);
				//Imgproc.bilateralFilter(newsourceFCV, nn, 3, 200, 200);
				//Regularization step here
				
				//step2
				//ExternProcess.MatchingHistogram(sourceFCV, newsourceFCV, newsourceFCV);//commenting because testing regularize
				//
				newflashTilesCV[i][j]=newsourceFCV;
				saveTile(newsourceFCV,"C:\\Users\\ralambomahay1\\Downloads\\Java_workspace\\newGit\\Data\\SampleTransportReflectance\\source_tile_relit_"+i+"_"+j+".jpg");
				//saveTile(transportMap,"C:\\Users\\ralambomahay1\\Downloads\\Java_workspace\\newGit\\Data\\SampleTransportReflectance\\transportMap_"+i+"_"+j+".jpg");
				saveTile(sourceFCV,"C:\\Users\\ralambomahay1\\Downloads\\Java_workspace\\newGit\\Data\\SampleTransportReflectance\\source_tile_"+i+"_"+j+".jpg");
	}
	@Override
	public void run() 
	{
		// TODO Auto-generated method stub	
		//initialize pairwise pixel => this is now initialized in Form.java once for all
		/*pairwisePixel0=nPairPixel(n[0], 0);
		pairwisePixel1=nPairPixel(n[1], 1);
		pairwisePixel2=nPairPixel(n[2], 2);*/
		//step1
		gaussianTiles(sourceGCV,15.0,4);		
		//gaussianTiles(masterTileGCV,5.0,4);//we smooth it inside form.java class
		allBriefG=brief(sourceGCV);
		brief(masterTileGCV,true);
		oneTileFinished(i,j);
		//Form.showTile(sourceG, container_ref_init);
		Form.showCvDataToJava(sourceFCV, container_ref_init);
		//gaussianTiles(newsourceFCV,3.0,1);
		//Mat nn=new Mat(newsourceFCV.rows(),newsourceFCV.cols(),CvType.CV_8UC3);
		//Imgproc.bilateralFilter(newsourceFCV, nn, 3, 200, 200);
		//step2
		//ExternProcess.MatchingHistogram(sourceFCV, newsourceFCV, newsourceFCV);
		//newsourceFCV=ExternProcess.TextureMatching(sourceFCV, newsourceFCV, newsourceFCV,6);
		newflashTilesCV[i][j]=newsourceFCV;
		saveTile(newsourceFCV,"C:\\Users\\ralambomahay1\\Downloads\\Java_workspace\\newGit\\Data\\SampleTransportReflectance\\source_tile_relit_"+i+"_"+j+".jpg");
		//saveTile(transportMap,"C:\\Users\\ralambomahay1\\Downloads\\Java_workspace\\newGit\\Data\\SampleTransportReflectance\\transportMap_"+i+"_"+j+".jpg");
		saveTile(sourceFCV,"C:\\Users\\ralambomahay1\\Downloads\\Java_workspace\\newGit\\Data\\SampleTransportReflectance\\source_tile_"+i+"_"+j+".jpg");
		//Form.showCvDataToJava(nn, container_ref_final);
		//Form.showCvDataToJava(newsourceFCV, container_ref_final);		
		//Imgproc.Sobel(newsourceFCV, gradient, sourceFCV.depth(), 0, 1);
		//Form.showCvDataToJava(gradient, container_ref_gradient);
		//Imgproc.Sobel(sourceFCV, gradientF, sourceFCV.depth(), 0, 1);
		//Form.showCvDataToJava(gradientF, container_ref_gradientF);
		
		
	}
	public BriefDescriptor(int i_,int j_,int n_,int window_,int size)
	{
		n=new int[]{96,128,32};
		i=i_;
		j=j_;
		tileSize=size;
		window=new int[]{33,17,5};
		_listeners=new ArrayList<>();
		allBriefGDict=new Hashtable<>();
	}
	public void setMaster(BufferedImage m)
	{
		master=m;
	}
	public void setMasterCV(Mat m,Mat m2)
	{
		masterTileFCV=m;
		masterTileGCV=m2;
	}
	public void setSourceFG(BufferedImage f,BufferedImage g)
	{
		sourceF=f;
		sourceG=g;
	}
	public void setSourceFGCV(Mat f,Mat g)
	{
		sourceFCV=f;
		sourceGCV=g;
		newsourceFCV=new Mat(g.rows(),g.cols(),CvType.CV_8UC3);
		transportMap=new Mat(g.rows(),g.cols(),CvType.CV_8UC3);
		gradient=new Mat(g.rows(),g.cols(),CvType.CV_8UC3);
		gradientF=new Mat(g.rows(),g.cols(),CvType.CV_8UC3);
	}	
	public static void gaussianTiles(Mat m,double size,double sigmaX)
	{
		//Mat newM=new Mat(m.rows(),m.cols(),CvType.CV_8UC3);
		Imgproc.GaussianBlur(m, m, new Size(size, size), sigmaX);
		//return newM;
	}
	private void oneTileFinished(int i_,int j_)
	{
		BriefDescriptorEvent evt = new BriefDescriptorEvent( this,i_,j_);
        Iterator listeners = _listeners.iterator();
        while( listeners.hasNext() ) 
        {
            ( (BriefDescriptorListener) listeners.next() ).oneTileProcessed(evt);
        }
	}
	public synchronized void AddBriefDescriptorEventListener(BriefDescriptorListener lst)
	{
		_listeners.add(lst);
	}
	private String[] brief(Mat m)
	{
		allBriefG=new String[m.cols()*m.rows()];
		int index=0;
		for(int i_=0;i_<m.rows();i_++)
		{
			for(int j_=0;j_<m.cols();j_++)
			{				
				allBriefG[index]=brief(m, i_, j_);
				//System.err.println(allBriefG[index]);
				allBriefGDict.put(index, new int[]{i_,j_});				
				//System.out.println(allBriefG[index]);
				index++;
			}
		}
		//System.out.println(index);
		return allBriefG;
	}
	public static void brief(Mat m,String[] allBG,Hashtable<Object, int[]> allBGDict,int[][] pairwise0,int[][] pairwise1,int[][] pairwise2)
	{
		//allBG=new String[m.cols()*m.rows()];
		//allBGDict=new Hashtable<>();
		int index=0;
		for(int i_=0;i_<m.rows();i_++)
		{
			for(int j_=0;j_<m.cols();j_++)
			{		
				//calcule du BRIEF pour un pixel (i_,j_) dans la fenêtre sont 96 ensuite 128 et enfin 32
				allBG[index]=brief(m, i_, j_,new int[]{96,128,32},new int[]{33,17,5},pairwise0,pairwise1,pairwise2);
				//System.err.println(allBriefG[index]);
				allBGDict.put(index, new int[]{i_,j_});				
				//System.out.println(allBriefG[index]);
				index++;
			}
		}
		System.out.println("1 tile a été calculé en brief");
	}
	private String[] brief(Mat m,boolean test)
	{
		//String[] masterB=new String[m.cols()*m.rows()];
		//for each pixel (s,t) in master tile => find brief(master_tile,s,t)=brief(source_tile,s',t')
		int index=0;
		int max=n[0]+n[1]+n[2];//n-erreur
		int hamming=max;
		int tempHaming=hamming;
		int id=0;
		int nbPixels=tileSize*tileSize;
		
		for( int i_=0;i_<m.rows();i_++)
		{
			for(int j_=0;j_<m.cols();j_++)
			{
				//m.put(i_, j_, new byte[]{40,25,43});
				//System.out.println(brief(m, i_, j_));
				//masterB[index]=brief(m, i_, j_);//on doit calculer en avance
				//now we have to look for best matches brief inside allBriefG
				
				for(int br=0;br<nbPixels;br++)
				{
					tempHaming=Gaussian.distanceHamming(masterB[index].getBytes(),allBriefG[br].getBytes());					
					//System.out.println(tempHaming);
					if(hamming>tempHaming)
					{
						hamming=tempHaming;
						id=br;
					}
				}
				//System.out.println("le distance minimum est "+hamming);				
				int[] match=allBriefGDict.get(id);
				//System.out.println("Le pixel correspondant est ("+match[0]+","+match[1]+")");
				byte[] colorF=new byte[3];
				byte[] tm=new byte[3];
				sourceGCV.get(match[0],match[1],tm);
				sourceFCV.get(match[0], match[1], colorF);//this is the pixel where brief best matches the master brief				
				newsourceFCV.put(i_, j_, colorF);
				transportMap.put(i_, j_, new byte[]{0,tm[1],tm[2]});//red and green channels
				index++;
				//reset
				hamming=max;
				id=0;
				//System.out.println("Pixel ("+i_+","+j_+") du master traité");
				
			}			
		}
		System.out.println("Tous les pixel ont été tratié ("+i+","+j+")");
		//newflashTilesCV[i][j]=newsourceFCV;
		//System.out.println(newsourceFCV);
		return new String[2];		
	}
	public static int[][] nPairPixel(int n,int window)
	{
		int[][] npair=new int[n][];
		int l=0;
		//double std=(double)window[id]/5;
		double std=(double)window/5;
		Date dt=new Date();		
		Random rd=new Random(dt.getTime());
		while(l<n)
		{
			int[] res=Gaussian.Gaussian(0,std ,rd,window);
			//(ligneX,colonneX)(ligneY,colonneY)
			npair[l]=new int[]{res[0],res[1],res[2],res[3]};
			l++;
		}
		return npair;
	}
	//Here we calculate the brief descriptor at one pixel
	//this returns a bit string
	//Pixel (s,t) => notation (ligne,colonne)
	public String brief(Mat tile,int s,int t)
	{
		return featureDescriptor(tile, s, t, n[0],window[0],pairwisePixel0)+featureDescriptor(tile, s, t, n[1],window[1],pairwisePixel1)+featureDescriptor(tile, s, t, n[2],window[2],pairwisePixel2);	
	}	
	public static String brief(Mat tile,int s,int t,int[] len,int[] windows,int[][] pairwise0,int[][] pairwise1,int[][] pairwise2)
	{
		return featureDescriptor(tile, s, t, len[0],windows[0],pairwise0)+featureDescriptor(tile, s, t, len[1],windows[1],pairwise1)+featureDescriptor(tile, s, t, len[2],windows[2],pairwise2);
	}
	
	public static String featureDescriptor(Mat tile,int s,int t,int n_,int window,int[][]pairsPixel)
	{
		StringBuilder sommeS=new StringBuilder();
		//int[][] pairsPixel=nPairPixel(n_,id);
		byte[] dataPixel=new byte[3];
		byte[] dataXPixel=new byte[3];
		byte[] dataYPixel=new byte[3];		
		tile.get(s, t,dataPixel);		
		//we will compute the brief
		int fen=window/2;
		
		for(int k=0;k<n_;k++)
		{			
			//pixel x
			int lx=pairsPixel[k][0];
			int cx=pairsPixel[k][1];
			//pixel y
			int ly=pairsPixel[k][2];
			int cy=pairsPixel[k][3];
			//we will check if pixel is out of bounds then we set the currentPixel (s,t)
			int rlx=s+lx;
			int rcx=t+cx;
			int rly=s+ly;
			int rcy=t+cy;
			
			tile.get(rlx,rcx,dataXPixel);
			tile.get(rly, rcy,dataYPixel);	
			//compute the difference pixelwise color
			
			//we will compute the grey level of one pixel to determine its intensity
			int gX=Gaussian.convertDoubleToInt((double)(dataXPixel[0]+dataXPixel[1]+dataXPixel[2])/3);
			int gY=Gaussian.convertDoubleToInt((double)(dataYPixel[0]+dataYPixel[1]+dataYPixel[2])/3);
			
			if(gX<gY)
			{
				//somme+=Math.pow(2, k);
				sommeS.append("1");
			}
			else
			{
				sommeS.append("0");
			}
		}
		return sommeS.toString();
	}
	public static void saveTile(Mat m,String path)
	{
		try
		{				
			int type=BufferedImage.TYPE_3BYTE_BGR;
			int bufferSize=m.channels()*m.cols()*m.rows();
			byte[] data=new byte[bufferSize];
			m.get(0, 0, data);
			BufferedImage image=new BufferedImage(m.cols(), m.rows(), type);
			final byte[] containerPixels=((DataBufferByte)image.getRaster().getDataBuffer()).getData();
			System.arraycopy(data, 0, containerPixels, 0, bufferSize);		
			//save image		
			ImageIO.write(image, "jpg", new File(path));			
		}
		catch(IOException e)
		{
			System.err.println("Error saving file");
		}		
	}
	

}
