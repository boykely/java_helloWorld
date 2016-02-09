import java.awt.image.BufferedImage;

public class BriefDescriptor implements Runnable 
{
	private BufferedImage sourceF;
	private BufferedImage sourceG;
	private BufferedImage master;
	private int i;
	private int j;
	private int window;
	@Override
	public void run() 
	{
		// TODO Auto-generated method stub
		System.out.println("Tile ("+i+","+j+") est terminé");
	}
	public BriefDescriptor(int i_,int j_,int window_)
	{
		i=i_;
		j=j_;
		window=window_;
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

}
