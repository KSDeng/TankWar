package src;
import java.awt.*;
import java.awt.Toolkit;

//回血块类
public class Blood {
	int x, y;
	TankClient tc;


	private static Toolkit tk = Toolkit.getDefaultToolkit();
	private static Image blood_img = tk.getImage(Blood.class.getClassLoader().getResource("resources/blood.png"));
	
	//根据位置创建回血块
	Blood(int x,int y,TankClient tc){
		this.x = x;
		this.y = y;
		this.tc = tc;
	}
	
	public static int blood_sizex() {
		return Blood.blood_img.getWidth(null);
	}
	public static int blood_sizey() {
		return Blood.blood_img.getHeight(null);
	}
	
	public void draw(Graphics g) {
		//在当前位置画出回血块
		g.drawImage(blood_img, x, y, null);
	}
	
	//返回回血块的范围
	public Rectangle getRect() {
		return new Rectangle(x,y,Blood.blood_sizex(),Blood.blood_sizey());
	}
	
	
}
