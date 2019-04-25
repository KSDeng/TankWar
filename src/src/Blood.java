package src;
import java.awt.*;
import java.awt.Toolkit;

//��Ѫ����
public class Blood {
	int x, y;
	TankClient tc;


	private static Toolkit tk = Toolkit.getDefaultToolkit();
	private static Image blood_img = tk.getImage(Blood.class.getClassLoader().getResource("resources/blood.png"));
	
	//����λ�ô�����Ѫ��
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
		//�ڵ�ǰλ�û�����Ѫ��
		g.drawImage(blood_img, x, y, null);
	}
	
	//���ػ�Ѫ��ķ�Χ
	public Rectangle getRect() {
		return new Rectangle(x,y,Blood.blood_sizex(),Blood.blood_sizey());
	}
	
	
}
