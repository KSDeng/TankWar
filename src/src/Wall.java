package src;
import java.awt.*;

//(单块)墙
public class Wall {

	private int x,y;	//墙的起始位置
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
	
	//获取墙所在的矩形范围
	public Rectangle getRect() {
		return new Rectangle(x,y,Wall.wall_sizex(),Wall.wall_sizey());
	}
}
