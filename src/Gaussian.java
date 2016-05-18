import java.util.Random;

public class Gaussian 
{
	/*The standard deviation used within the paper are 33/5,17/5,1
	 * The size of the windows S are 33,17,5
	 * The number of n-pairs are 96,128,32
	 * */
	public static int[] Gaussian(double mu,double std,Random rd,int window)
	{		
		//on va mettre au bord de la fenêtre si le nombre généré depasse la fenêtre
		int fen=window/2;
		int x0=convertDoubleToInt(rd.nextGaussian()*std+mu);x0=x0>=fen?fen:x0;
		int y0=convertDoubleToInt(rd.nextGaussian()*std+mu);y0=y0>=fen?fen:y0;
		int x1=convertDoubleToInt(rd.nextGaussian()*std+mu);x1=x1>=fen?fen:x1;
		int y1=convertDoubleToInt(rd.nextGaussian()*std+mu);y1=y1>=fen?fen:y1;
		return new int[]{x0,y0,x1,y1};
	}
	public static int convertDoubleToInt(double x)
	{
		//we will use this one,if the number is more than x.50 => return x+1, if not => return x
		int a=(int)x;
		double reste=x-a;
		if((reste-0.5)<=0.00001)return a;
		else return a+1;
	}
	public static int distanceHamming(byte[] a,byte[] b)
	{
		int lengthA=a.length;
		if(lengthA!=b.length)
		{
			System.out.println("Il y a eu erreur lors de calcule de la distance de Hamming");
			return -1;
		}
		int res=0;
		byte xor;
		for(int i=0;i<lengthA;i++)
		{
			xor=(byte)(a[i]^b[i]);
			if(xor==1)res++;			
		}
		return res;
	}
}
