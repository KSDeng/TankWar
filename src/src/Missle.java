package src;

import java.awt.*;
import java.util.List;
import java.awt.Toolkit;

public class Missle {
	private int x,y;				//�ӵ���λ��
	private Direction dir;		//�ӵ����˶�����
	private boolean good;			//�ӵ�������(�ҷ��������ǵз�����)
	public static int attack = 20;		//һ���ӵ�������(���к�Ѫ������ʧֵ)
	
	private static final int speedx=Tank.enemytank_speedx*4,speedy=Tank.enemytank_speedy*4;		//�ӵ����ٶ�

	private TankClient tc;			//���չ���Ա����
	
	private static Toolkit tk = Toolkit.getDefaultToolkit();
	private static Image my_missle = tk.getImage(Missle.class.getClassLoader().getResource("resources/my/missle.png"));
	private static Image enemy_missle = tk.getImage(Missle.class.getClassLoader().getResource("resources/enemy/missle.gif"));
	
	public Missle(int x,int y,Direction d) {
		this.x = x;
		this.y = y;
		this.dir = d;
	}
	//���������Ա������õĹ��췽��
	public Missle(int x,int y,Direction d,TankClient t) {
		this(x,y,d);
		this.tc = t;
	}
	//�����趨�ӵ����ԵĹ��췽��
	public Missle(int x,int y,Direction d,TankClient t,boolean g) {
		this(x,y,d,t);
		this.good = g;
	}
	//������Ӫ��ȡ�ӵ��ߴ�Ľӿ�
	public static int missle_sizex(boolean g) {
		Image tmp = g ? my_missle : enemy_missle;
		return tmp.getWidth(null);
	}
	public static int missle_sizey(boolean g) {
		Image tmp = g ? my_missle : enemy_missle;
		return tmp.getHeight(null);
	}
	//�����ӵ�
	public void draw(Graphics g) {
		Image tmp = this.good ? my_missle : enemy_missle;
		g.drawImage(tmp, this.x, this.y, null);
		move();
	}

	private void move() {
		switch(dir) {
		case U:
			y-=speedy;break;
		case D:
			y+=speedy;break;
		case L:
			x-=speedx;break;
		case R:
			x+=speedx;break;
		case LU:
			x-=speedx;y-=speedy;break;
		case RU:
			x+=speedx;y-=speedy;break;
		case LD:
			x-=speedx;y+=speedy;break;
		case RD:
			x+=speedx;y+=speedy;break;
			default:break;
		}
		//�ж��ڵ��Ƿ�Խ���߽�
		if(x<0 || y<0 || x>TankClient.mainwindow_sizex || y>TankClient.mainwindow_sizey) {
			tc.missles.remove(this);	//���ڵ�������ֱ���ڹ���Ա���н����Ƴ�
		}
	}
	//���ذ�����ǰ�ӵ���һ�����η���
	public Rectangle getRect() {
		return new Rectangle(x,y,Missle.missle_sizex(this.good),Missle.missle_sizey(this.good));
	}
	//����ӵ��Ƿ������ĳһ��̹��
	public boolean hitTank(Tank t) {
		//Rectangle.intersects()�����ж����������Ƿ��ཻ
		//����ཻ������̹�˻�����,��ô�Ƴ��ӵ����Ƴ������е�̹��
		//��Ҫ�ж��ӵ����Ժͻ��е�̹�������Ƿ�����(�ӵ������Ѿ�)
		if( t.isLive() && this.getRect().intersects(t.getRect())
				&& this.good !=t.isGood()) {
			Explode e = new Explode(this.x,this.y,this.tc);	//����һ��explode
			this.tc.explodes.add(e);			//��ӵ�explodes��
			
			if(t.isGood()) {	//�ҷ�̹�˱����к����һ��Ѫ��
				t.setHp(t.getHp()-attack);
				if(t.getHp()<=0)
					t.setLive(false);
			}
			else {				//�з�̹��ֱ������
				t.setLive(false);
				//ͬʱ��ԭλ�ò���һ����Ѫ��
				this.tc.bloods.add(new Blood(this.x,this.y,this.tc));
			}
			tc.missles.remove(this);	//����̹�˺��Ƴ���ǰ��ö�ӵ�			
			return true;
		}
		else
			return false;
	}
	
	//����ӵ��Ƿ������̹��(�������̹��)
	public boolean hitTanks(List<Tank> t) {
		for(int i=0;i<t.size();i++) {
			if(this.hitTank(t.get(i)))
				return true;
		}
		return false;
	}
	
	//�ӵ�ײ(����)ǽ��
	public boolean hitWall(Wall w) {
		if(this.getRect().intersects(w.getRect())) {
			this.tc.missles.remove(this);	//ײǽ��ֱ���Ƴ������ӵ�
			return true;
		}
		else {
			return false;
		}
	}
	//�ӵ�ײ��һ��ǽ
	public boolean hitWalls(List<Wall> ws) {
		for(int i=0;i<ws.size();i++) {
			if(this.hitWall(ws.get(i)))
				return true;
		}
		return false;
	}
	
}
