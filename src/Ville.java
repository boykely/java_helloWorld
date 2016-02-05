public class Ville implements MonInterface
{
	private String v;
	private String c;
	public Ville(String v_,String c_)
	{
		v=v_;
		c=c_;
	}
	public void affiche()
	{
		System.out.println("Votre ville est "+v+" dont la capitale est "+c);
	}
	@Override	
	public void ceQuoi()
	{
		System.out.println("je suis une interface dans classe Ville");
	}
}