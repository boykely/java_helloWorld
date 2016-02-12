import java.awt.image.BufferedImage;
import java.awt.image.ImageFilter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class BriefDescriptor implements Runnable 
{
	/*
	 * We must not forget that accessing image from JNI array
	 * takes more time that Java array
	 * Cf OpenCV 3.0 Computer Vision with Java
	 */
	public ImageContainer container_ref_final;//on va utiliser ceci pour l'affichage après traitement du 
	public ImageContainer container_ref_init;
	private BufferedImage sourceF;
	private BufferedImage sourceG;
	private BufferedImage master;
	public Mat[][] newflashTilesCV;
	private Mat masterTileFCV;
	private Mat masterTileGCV;
	private Mat sourceFCV;
	private Mat sourceGCV;//this is the reference to current tile within F
	private Mat newsourceFCV;//this will hold the new tile F1
	private int i;
	private int j;
	private int n;
	private int window;
	private List _listeners;
	private String[] allBriefG;
	private Hashtable<Object, int[]> allBriefGDict;
	
	@Override
	public void run() 
	{
		// TODO Auto-generated method stub
		//gaussianTiles(sourceGCV);
		//gaussianTiles(masterTileGCV);
		allBriefG=brief(sourceGCV);
		brief(masterTileGCV,true);
		oneTileFinished(i,j);
		//Form.showTile(sourceG, container_ref_init);
		Form.showCvDataToJava(sourceFCV, container_ref_init);
		Form.showCvDataToJava(newsourceFCV, container_ref_final);
		
	}
	public BriefDescriptor(int i_,int j_,int n_,int window_)
	{
		n=n_;
		i=i_;
		j=j_;
		window=window_;
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
	}	
	public static void gaussianTiles(Mat m)
	{
		Imgproc.GaussianBlur(m, m, new Size(15.0, 15.0), 4);
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
				allBriefGDict.put(index, new int[]{i_,j_});				
				//System.out.println(allBriefG[index]);
				index++;
			}
		}
		return allBriefG;
	}	
	private String[] brief(Mat m,boolean test)
	{
		String[] masterB=new String[m.cols()*m.rows()];
		//for each pixel (s,t) in master tile => find brief(master_tile,s,t)=brief(source_tile,s',t')
		int index=0;
		int hamming=n;//n-erreur
		int tempHaming=hamming;
		int id=0;
		for(int i_=0;i_<m.rows();i_++)
		{
			for(int j_=0;j_<m.cols();j_++)
			{
				//m.put(i_, j_, new byte[]{40,25,43});
				//System.out.println(brief(m, i_, j_));
				masterB[index]=brief(m, i_, j_);
				//now we have to look for best matches brief inside allBriefG
				for(int br=0;br<n;br++)
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
				
				sourceFCV.get(match[0], match[1], colorF);//this is the pixel where brief best matches the master brief
				
				//System.err.println(colorF[0]+"/"+colorF[1]+"/"+colorF[2]);
				//newsourceFCV.put(match[0], match[1], colorF);
				newsourceFCV.put(i_, j_, colorF);
				index++;
				//reset
				hamming=n;
				id=0;
			}
		}
		newflashTilesCV[i][j]=newsourceFCV;
		//System.out.println(newsourceFCV);
		return new String[2];		
	}
	private int[][] nPairPixel(int n)
	{
		int[][] npair=new int[n][];
		int l=0;
		double std=(double)window/5;
		Date dt=new Date();		
		Random rd=new Random(dt.getTime());
		while(l<n)
		{
			int[] res=Gaussian.Gaussian(0,std ,rd);
			//(ligneX,colonneX)(ligneY,colonneY)
			npair[l]=new int[]{res[0],res[1],res[2],res[3]};
			l++;
		}
		return npair;
	}
	//Here we calculate the brief descriptor at one pixel
	//this returns a bit string
	//Pixel (s,t) => notation (ligne,colonne)
	private String brief(Mat tile,int s,int t)
	{
		int[][] pairsPixel=nPairPixel(n);
		byte[] dataPixel=new byte[3];
		byte[] dataXPixel=new byte[3];
		byte[] dataYPixel=new byte[3];
		tile.get(s, t,dataPixel);		
		//we will compute the brief
		double somme=0;
		StringBuilder sommeS=new StringBuilder();
		for(int k=0;k<n;k++)
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
			if(rlx<0 || rcx<0)
			{
				rlx=s;
				rcx=t;
			}
			if(rly<0 || rcy<0)
			{
				rly=s;
				rcy=t;
			}
			tile.get(rlx,rcx,dataXPixel);
			tile.get(rly, rcy,dataYPixel);
			//we will compute the grey level of one pixel to determine its intensity
			int gX=(dataXPixel[0]+dataXPixel[1]+dataXPixel[2])/3;
			int gY=(dataYPixel[0]+dataYPixel[1]+dataYPixel[2])/3;
			
			if(gX<gY)
			{
				somme+=Math.pow(2, k);
				sommeS.append("1");
			}
			else
			{
				sommeS.append("0");
			}
		}
		return sommeS.toString();
	}	

}
