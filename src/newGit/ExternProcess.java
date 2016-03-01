package newGit;

import java.util.Hashtable;

import org.opencv.core.Mat;

public class ExternProcess 
{
	public static void HistogrammeRGB(Mat m,int[] R,int[] G,int[] B)
	{
		byte[] col=new byte[3];
		for(int i=0;i<m.rows();i++)
		{
			for(int j=0;j<m.cols();j++)
			{					
				m.get(i, j,col);
				R[byteColorCVtoIntJava(col[2])]+=1;
				G[byteColorCVtoIntJava(col[1])]+=1;
				B[byteColorCVtoIntJava(col[0])]+=1;				
			}
		}
	}
	public static int byteColorCVtoIntJava(byte b)
	{		
		int i=(b+128)+128;		
		return b>=0?(int)b:i;
	}
	public static void HistogrammeCumuleRGB(Mat m,int[] R,int[] G,int[] B,int[] RC,int[] GC,int[] BC,int N)
	{
		int valueR=0;int valueG=0;int valueB=0;
		for(int i=0;i<256;i++)
		{
			valueR+=R[i];RC[i]=R[i]==0?0:valueR;
			valueG+=G[i];GC[i]=G[i]==0?0:valueG;
			valueB+=B[i];BC[i]=B[i]==0?0:valueB;
			
		}
	}
	public static void InverseHistogrammeRGB(int[] RC,int[] GC,int[] BC,Hashtable<Integer, Integer> InvHistoCumulR,Hashtable<Integer, Integer> InvHistoCumulG,Hashtable<Integer, Integer> InvHistoCumulB )
	{
		for(int i=0;i<256;i++)
		{				
			InvHistoCumulR.put(RC[i], i);
			InvHistoCumulG.put(GC[i], i);
			InvHistoCumulB.put(BC[i], i);			
		}
	}
	public static void MatchingHistogram(Mat imRef,Mat imTarget,Mat result)
	{
		int[] RRef=new int[256];int[] RCRef=new int[256];Hashtable<Integer, Integer> InvHistoCumulRRef=new Hashtable<>();
		int[] GRef=new int[256];int[] GCRef=new int[256];Hashtable<Integer, Integer> InvHistoCumulGRef=new Hashtable<>();
		int[] BRef=new int[256];int[] BCRef=new int[256];Hashtable<Integer, Integer> InvHistoCumulBRef=new Hashtable<>();
		int[] RTar=new int[256];int[] RCTar=new int[256];Hashtable<Integer, Integer> InvHistoCumulRTar=new Hashtable<>();
		int[] GTar=new int[256];int[] GCTar=new int[256];Hashtable<Integer, Integer> InvHistoCumulGTar=new Hashtable<>();
		int[] BTar=new int[256];int[] BCTar=new int[256];Hashtable<Integer, Integer> InvHistoCumulBTar=new Hashtable<>();
		int N=imRef.cols()*imRef.rows();
		HistogrammeRGB(imRef, RRef, GRef, BRef);
		HistogrammeCumuleRGB(imRef, RRef, GRef, BRef,RCRef,GCRef,BCRef,N);
		InverseHistogrammeRGB(RCRef, GCRef, BCRef, InvHistoCumulRRef, InvHistoCumulGRef, InvHistoCumulBRef);
		
		HistogrammeRGB(imTarget, RTar, GTar, BTar);
		HistogrammeCumuleRGB(imTarget, RTar, GTar, BTar,RCTar,GCTar,BCTar,N);
		InverseHistogrammeRGB(RCTar, GCTar, BCTar, InvHistoCumulRTar, InvHistoCumulGTar, InvHistoCumulBTar);		
		byte[] pixel=new byte[3];
		byte[] pixelTarget=new byte[3];		
		for(int i=0;i<imRef.rows();i++)
		{
			for(int j=0;j<imRef.cols();j++)
			{
				imRef.get(i, j,pixel);
				imTarget.get(i, j,pixelTarget);
				byte blue=pixelTarget[0];byte green=pixelTarget[1];byte red=pixelTarget[2];				
				int r=minimum(RCTar[byteColorCVtoIntJava(red)],RCRef,InvHistoCumulRRef);
				int g=minimum(GCTar[byteColorCVtoIntJava(green)],GCRef,InvHistoCumulGRef);
				int b=minimum(BCTar[byteColorCVtoIntJava(blue)],BCRef,InvHistoCumulBRef);		
				pixel[0]=b>256?(byte)255:(byte)b;
				pixel[1]=b>256?(byte)255:(byte)g;
				pixel[2]=b>256?(byte)255:(byte)r;				
				result.put(i, j, pixel);
			}
		}
	}
	public static int minimum(int value,int[] cumul,Hashtable<Integer, Integer>inv_cumul)
	{
		int[] temp=new int[256];
		Hashtable<Integer, Integer>tempHash=new Hashtable<>();
		for(int i=0;i<256;i++)
		{
			temp[i]=Math.abs(value-cumul[i]);
			tempHash.put(temp[i], cumul[i]);
		}		
		int a=0;
		for(int i=0;i<255;i++)
		{
			for(int j=i+1;j<256;j++)
			{
				if(temp[i]>temp[j])
				{
					a=temp[j];
					temp[j]=temp[i];
					temp[i]=a;
				}
			}
		}
		return inv_cumul.get(tempHash.get(temp[0]));
	}
}
