import java.util.Hashtable;

import org.opencv.core.Mat;

public class PixelProcess implements Runnable 
{
	public int i;
	public int j;
	public Mat m;
	public BriefDescriptor brf;
	public Hashtable<Object, int[]> allBriefGDict;
	public String[] allBriefG;
	public Mat sourceFCV;
	public Mat newsourceFCV;
	public int[] n;
	public  PixelProcess() 
	{
		// TODO Auto-generated constructor stub
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		int max=n[0]+n[1]+n[2];//n-erreur
		int hamming=max;
		int tempHaming=hamming;
		int id=0;
		//m.put(i_, j_, new byte[]{40,25,43});
		//System.out.println(brief(m, i_, j_));						
		String currentBrief=brf.brief(m, i, j);
		//now we have to look for best matches brief inside allBriefG
		for(int br=0;br<192*192;br++)
		{
			tempHaming=Gaussian.distanceHamming(currentBrief.getBytes(),allBriefG[br].getBytes());
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
		newsourceFCV.put(i, j, colorF);
		
		//reset
		hamming=max;
		id=0;
		System.out.println("Pixel ("+i+","+j+") du master traité");
	}

}
