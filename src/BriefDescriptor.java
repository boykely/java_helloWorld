import java.awt.image.BufferedImage;
import java.awt.image.ImageFilter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

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
	private BufferedImage sourceF;
	private BufferedImage sourceG;
	private BufferedImage master;
	private Mat masterCV;
	private Mat sourceFCV;
	private Mat sourceGCV;	
	private int i;
	private int j;
	private int window;
	private List _listeners;
	
	@Override
	public void run() 
	{
		// TODO Auto-generated method stub
		gaussianTiles(sourceGCV);		
		oneTileFinished(i,j);
	}
	public BriefDescriptor(int i_,int j_,int window_)
	{
		i=i_;
		j=j_;
		window=window_;
		_listeners=new ArrayList<>();
	}
	public void setMaster(BufferedImage m)
	{
		master=m;
	}
	public void setMasterCV(Mat m)
	{
		masterCV=m;
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
	private byte[] brief()
	{
		//for each pixel (s,t) in master tile => find brief(master_tile,s,t)=brief(source_tile,s',t')
		for(int i=0;i<masterCV.rows();i++)
		{
			for(int j=0;j<masterCV.cols();j++)
			{
				
			}
		}
		return new byte[1];
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
			npair[l]=new int[]{res[0],res[1]};
			l++;
		}
		return npair;
	}

}
