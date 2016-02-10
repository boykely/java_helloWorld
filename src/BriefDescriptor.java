import java.awt.image.BufferedImage;
import java.awt.image.ImageFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class BriefDescriptor implements Runnable 
{
	private BufferedImage sourceF;
	private BufferedImage sourceG;
	private BufferedImage master;
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
	private void gaussianTiles(Mat m)
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

}
