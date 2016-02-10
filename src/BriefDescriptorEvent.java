import java.util.EventObject;

public class BriefDescriptorEvent extends EventObject 
{
	private int i;
	private int j;
	public BriefDescriptorEvent(Object arg0,int i_,int j_) 
	{
		super(arg0);
		// TODO Auto-generated constructor stub
		i=i_;
		j=j_;
	}
	public int getLigne()
	{
		return i;
	}
	public int getColonne()
	{
		return j;
	}
	
}
