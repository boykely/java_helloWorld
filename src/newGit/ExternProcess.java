package newGit;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.ejml.data.DenseMatrix64F;
import org.ejml.simple.SimpleMatrix;
import org.ejml.simple.SimpleSVD;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.DenseOpticalFlow;

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
		{/*
			valueR+=R[i];RC[i]=R[i]==0?0:valueR;
			valueG+=G[i];GC[i]=G[i]==0?0:valueG;
			valueB+=B[i];BC[i]=B[i]==0?0:valueB;*/
			valueR+=R[i];RC[i]=valueR;
			valueG+=G[i];GC[i]=valueG;
			valueB+=B[i];BC[i]=valueB;
			
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
				/*pixel[0]=b>256?127:b<0?0:(byte)b;
				pixel[1]=g>256?127:g<0?0:(byte)g;
				pixel[2]=r>256?127:r<0?0:(byte)r;*/			
				pixel[0]=(byte)b;
				pixel[1]=(byte)g;
				pixel[2]=(byte)r;
				result.put(i, j, pixel);
			}
		}
	}
	public static void LaplacianPyramid(Mat src,Mat dest,List<Mat> gauss)
	{		
		Mat temp=src.clone();		
		Imgproc.pyrDown(temp, dest);
		Imgproc.pyrUp(dest, dest,temp.size());
		gauss.add(dest.clone());//we will use it to collapse-pyramid		
		//temp=dest.clone();		
		Core.subtract(src, dest, dest);
		//Core.add(dest, temp, dest);//Si on veut retrouver l'image originale Gi		
	}
	public static void createLaplacianPyramid(Mat dest,int n,List<Mat>pyramid,List<Mat>gauss)
	{
		int i=0;
		Mat temp=dest.clone();
		while(i<n)
		{			
			LaplacianPyramid(temp, dest,gauss);
			pyramid.add(dest.clone());			
			Imgproc.pyrDown(temp, temp);			
			i++;
		}
	}
	public static double[][] MeanRGBChannel(Mat ImRef,Mat ImTar)
	{
		/*
		 *  0 => image ref
		 *  1 => image tar
		 */
		double[][] mean=new double[2][3];
		byte[] ref=new byte[3];
		byte[] tar=new byte[3];
		int pixels=ImRef.rows()*ImRef.cols();
		int[][] total=new int[2][3]; 
		for(int i=0;i<ImRef.rows();i++)
		{
			for(int j=0;j<ImRef.cols();j++)
			{
				ImRef.get(i, j,ref);
				ImTar.get(i, j,tar);
				total[0][0]+=byteColorCVtoIntJava(ref[2]);total[0][1]+=byteColorCVtoIntJava(ref[1]);total[0][2]+=byteColorCVtoIntJava(ref[0]);
				total[1][0]+=byteColorCVtoIntJava(tar[2]);total[1][1]+=byteColorCVtoIntJava(tar[1]);total[1][2]+=byteColorCVtoIntJava(tar[0]);
			}
		}
		mean[0][0]=((double)total[0][0])/pixels;mean[0][1]=((double)total[0][1])/pixels;mean[0][2]=((double)total[0][2])/pixels;
		mean[1][0]=((double)total[1][0])/pixels;mean[1][1]=((double)total[1][1])/pixels;mean[1][2]=((double)total[1][2])/pixels;
		System.out.println(mean[0][0]+"/"+mean[0][1]+"/"+mean[0][2]);
		System.out.println(mean[1][0]+"/"+mean[1][1]+"/"+mean[1][2]);
		return mean;
	}
	public static SimpleMatrix computeA(int pixels,Mat ImRef,double[][] meanRGB)
	{
		DenseMatrix64F A=new DenseMatrix64F(3,pixels);
		int k=0;
		for(int i=0;i<ImRef.rows();i++)
		{
			for(int j=0;j<ImRef.cols();j++)
			{
				byte[] bgr=new byte[3];
				ImRef.get(i, j, bgr);
				double r=byteColorCVtoIntJava(bgr[2])-meanRGB[0][0];
				double g=byteColorCVtoIntJava(bgr[1])-meanRGB[0][1];
				double b=byteColorCVtoIntJava(bgr[0])-meanRGB[0][2];
				A.add(0, k, Math.abs(r));A.add(1, k, Math.abs(g));A.add(2, k, Math.abs(b));
				k++;
			}
		}
		return new SimpleMatrix(A);
	}
	public static SimpleMatrix computeC(int pixels,SimpleMatrix A) 
	{
		return A.mult(A.transpose()).divide(pixels-1);// from IPOL avec division 
		//return A.mult(A.transpose());//from Original sans division
	}
	public static SimpleMatrix computeP(SimpleMatrix C)
	{
		return C.svd().getU();
	}
	public static SimpleMatrix computePt(SimpleMatrix C)
	{
		return C.svd().getV();
	}
	public static Mat RGB2PCAColor(Mat source,SimpleMatrix Pt,double[] mean)
	{
		Mat result=new Mat(source.rows(),source.cols(),CvType.CV_32FC3);
		double[] pixel=new double[3];
		for(int i=0;i<source.rows();i++)
		{
			for(int j=0;j<source.cols();j++)
			{
				source.get(i, j, pixel);
				double r=pixel[2]-mean[0];
				double g=pixel[1]-mean[1];
				double b=pixel[0]-mean[2];
				SimpleMatrix rgb=new SimpleMatrix(new double[][]{
					{r},
					{g},
					{b}
				});
				SimpleMatrix pca=Pt.mult(rgb);
				r=pca.get(0, 0);r=r<0?Math.abs(r):r;
				g=pca.get(1, 0);g=g<0?Math.abs(g):g;
				b=pca.get(2, 0);b=b<0?Math.abs(b):b;
				result.put(i, j, new double[]{b,g,r});
			}
		}
		return result;
	}
	public static Mat PCA2RGBColor(Mat source,SimpleMatrix P,double[][] mean)
	{
		Mat result=new Mat(source.rows(),source.cols(),CvType.CV_32FC3);
		SimpleMatrix m=new SimpleMatrix(new double[][]{
			{mean[0][0]},
			{mean[0][1]},
			{mean[0][2]}	
		});
		double[] bgr=new double[3];
		for(int i=0;i<source.rows();i++)
		{
			for(int j=0;j<source.cols();j++)
			{
				source.get(i, j,bgr);
				SimpleMatrix pca=new SimpleMatrix(new double[][]{
					{bgr[2]},
					{bgr[1]},
					{bgr[0]}
				});
				SimpleMatrix rgb=(P.mult(pca));
				double r=rgb.get(0,0)+mean[0][0];r=r<0?Math.abs(r):r;
				double g=rgb.get(1,0)+mean[0][1];g=g<0?Math.abs(g):g;
				double b=rgb.get(2, 0)+mean[0][2];b=b<0?Math.abs(b):b;
				//System.out.println(r+"-"+g+"-"+b);
				result.put(i, j, new double[]{
						b,
						g,
						r
				});
			}
		}
		return result;
	}
	public static Mat TextureMatching(Mat imRef,Mat imTar,Mat result)
	{
		/*
		 * Correction color space from IPOL
		 */
		List<Mat> pyramidRef=new ArrayList<>();
		List<Mat> pyramidTar=new ArrayList<>();
		List<Mat> gaussRef=new ArrayList<>();
		List<Mat> gaussTar=new ArrayList<>();
		Mat tempRef=imRef.clone();
		Mat tempTar=imTar.clone();
		MatchingHistogram(imRef, imTar, imTar);//on met le résultat dans imTar
		createLaplacianPyramid(tempRef, 5, pyramidRef,gaussRef);
		int i=0;		
		while(i<5)
		{
			createLaplacianPyramid(tempTar, 5, pyramidTar,gaussTar);
			//result=new Mat();
			for(int j=0;j<pyramidTar.size();j++)
			{
				//System.out.println(i+"//"+j);
				MatchingHistogram(pyramidRef.get(j),pyramidTar.get(j), pyramidTar.get(j));
			}
			result=collapsePyramid(pyramidTar,gaussTar);
			System.out.println(result);
			MatchingHistogram(imRef, result, result);
			pyramidTar.clear();
			gaussTar.clear();
			tempTar=result.clone();
			i++;
		}		
		return result;
	}
	public static Mat TextureMatching(Mat imRef,Mat imTar,Mat result,int n)
	{
		//version stable
		List<Mat> pyramidRef=new ArrayList<>();
		List<Mat> pyramidTar=new ArrayList<>();
		List<Mat> gaussRef=new ArrayList<>();
		List<Mat> gaussTar=new ArrayList<>();
		Mat tempRef=imRef.clone();
		Mat tempTar=imTar.clone();
		MatchingHistogram(imRef, imTar, imTar);//on met le résultat dans imTar
		createLaplacianPyramid(tempRef, n, pyramidRef,gaussRef);
		//createLaplacianPyramid(tempTar, n, pyramidTar,gaussTar);	
		//MatchingHistogram(imRef, imTar, imTar);
		int i=0;		
		while(i<n)
		{
			createLaplacianPyramid(tempTar, n, pyramidTar,gaussTar);
			//result=new Mat();
			for(int j=0;j<pyramidTar.size();j++)
			{
				//System.out.println(i+"//"+j);
				MatchingHistogram(pyramidRef.get(j),pyramidTar.get(j), pyramidTar.get(j));
			}
			result=collapsePyramid(pyramidTar,gaussTar);
			MatchingHistogram(imRef, result, result);
			pyramidTar.clear();
			gaussTar.clear();
			tempTar=result.clone();
			//MatchingHistogram(pyramidRef.get(i),pyramidTar.get(i), pyramidTar.get(i));
			//result=pyramidTar.get(i).clone();
			i++;
		}		
		//result=collapsePyramid(pyramidTar,gaussTar);
		//MatchingHistogram(imRef, result, result);
		return result;
		/*
		 * Version taloha 28 juin 2016
		List<Mat> pyramidRef=new ArrayList<>();
		List<Mat> pyramidTar=new ArrayList<>();
		List<Mat> gaussRef=new ArrayList<>();
		List<Mat> gaussTar=new ArrayList<>();
		Mat tempRef=imRef.clone();
		Mat tempTar=imTar.clone();
		createLaplacianPyramid(tempRef, n, pyramidRef,gaussRef);
		createLaplacianPyramid(tempTar, n, pyramidTar,gaussTar);	
		MatchingHistogram(imRef, imTar, imTar);
		int i=0;		
		while(i<n)
		{
			//result=new Mat();
			MatchingHistogram(pyramidRef.get(i),pyramidTar.get(i), pyramidTar.get(i));
			//result=pyramidTar.get(i).clone();
			i++;
		}		
		result=collapsePyramid(pyramidTar,gaussTar);
		MatchingHistogram(imRef, result, result);
		return result;
		*/
	}
	public static Mat collapsePyramid(List<Mat> pyramid,List<Mat>gauss)
	{
		int i=pyramid.size()-1;
		Mat temp=new Mat();		
		while(i>=0)
		{
			//Imgproc.pyrUp(pyramid.get(i), temp,pyramid.get(i-1).size());			
			Core.add(pyramid.get(i), gauss.get(i), temp);
			//
			i--;
		}		
		return temp;
	}
	public static int minimum(int value,int[] cumul,Hashtable<Integer, Integer>inv_cumul)
	{
		int min=Math.abs(value-cumul[0]);
		int[] temp=new int[256];
		Hashtable<Integer, Integer>tempHash=new Hashtable<>();
		for(int i=0;i<256;i++)
		{
			temp[i]=Math.abs(value-cumul[i]);			
			tempHash.put(temp[i], cumul[i]);
			if(min>=temp[i])
			{
				min=temp[i];
			}
		}		
		int a=0;
		//To avoid sorting like this we will find the minimum value min above
		/*
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
		return inv_cumul.get(tempHash.get(temp[0]));*/
		return inv_cumul.get(tempHash.get(min));
	}
	public static void regularizeSVBRDF(Mat[][] f1,Mat[][]f2,int tileLenH,int tileLenW,int hauteur)
	{
		//f1 et f2 doivent être initialisé avant
		int[][] temp=new int[hauteur*hauteur][3];
		int index=0;
		byte[] pixel=new byte[3];
		for(int i=0;i<tileLenH;i++)
		{
			//parcours tous les tiles
			for(int j=0;j<tileLenW;j++)
			{
				for(int row=0;row<hauteur;row++)
				{
					//parcours tous les pixels du tile
					for(int col=0;col<hauteur;col++)
					{
						f1[i][j].get(row, col,pixel);
						temp[index][0]+=byteColorCVtoIntJava(pixel[0]);
						temp[index][1]+=byteColorCVtoIntJava(pixel[1]);
						temp[index][2]+=byteColorCVtoIntJava(pixel[2]);
						index++;
					}
				}
			}
			index=0;
		}
		//on va faire l'inverse pour créer f2
		for(int i=0;i<tileLenH;i++)
		{
			//parcours tous les tiles
			for(int j=0;j<tileLenW;j++)
			{
				for(int row=0;row<hauteur;row++)
				{
					//parcours tous les pixels du tile
					for(int col=0;col<hauteur;col++)
					{						
						int B=temp[index][0]/(tileLenH*tileLenW);
						int G=temp[index][1]/(tileLenH*tileLenW);
						int R=temp[index][2]/(tileLenH*tileLenW);
						pixel=new byte[]{(byte)B,(byte)G,(byte)R};
						f2[i][j].put(row, col, pixel);
						index++;
					}
				}
			}
			index=0;
		}
	}
}
