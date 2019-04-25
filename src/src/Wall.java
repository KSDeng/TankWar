package src;
import java.awt.*;

//(����)ǽ
public class Wall {

	private int x,y;	//ǽ����ʼλ��
	TankClient tc;
	
	private static Toolkit tk = Toolkit.getDefaultToolkit();
	private static Image wall_img = tk.getImage(Wall.class.getClassLoader().getResource("resources/wall.jpg"));
	
	public Wall(int x,int y,TankClient tc) {
		this.x = x;
		this.y = y;
		this.tc = tc;
	}
	public static int wall_sizex() {
		return Wall.wall_img.getWidth(null);
	}
	public static int wall_sizey() {
		return Wall.wall_img.getHeight(null);
	}
	
	public void draw(Graphics g) {
		g.drawImage(wall_img, x, y, null);
	}
	
	//��ȡǽ���ڵľ��η�Χ
	public Rectangle getRect() {
		return new Rectangle(x,y,Wall.wall_sizex(),Wall.wall_sizey());
	}
}
