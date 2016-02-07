import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class ImageContainer extends JPanel 
{	
	public BufferedImage image;	
	
	public ImageContainer()
	{
		
	}
	public void paint(Graphics g)
	{		
		super.paint(g);		
		boolean res=g.drawImage(image, 0, 0,null);		
	}
}
