package src;

import java.awt.*;
import java.util.List;
import java.awt.Toolkit;

public class Missle {
	private int x,y;				//子弹的位置
	private Direction dir;		//子弹的运动方向
	private boolean good;			//子弹的属性(我方发出还是敌方发出)
	public static int attack = 20;		//一颗子弹的威力(击中后血量的损失值)
	
	private static final int speedx=Tank.enemytank_speedx*4,speedy=Tank.enemytank_speedy*4;		//子弹的速度

	private TankClient tc;			//接收管理员对象
	
	private static Toolkit tk = Toolkit.getDefaultToolkit();
	private static Image my_missle = tk.getImage(Missle.class.getClassLoader().getResource("resources/my/missle.png"));
	private static Image enemy_missle = tk.getImage(Missle.class.getClassLoader().getResource("resources/enemy/missle.gif"));
	
	public Missle(int x,int y,Direction d) {
		this.x = x;
		this.y = y;
		this.dir = d;
	}
	//允许传入管理员类的引用的构造方法
	public Missle(int x,int y,Direction d,TankClient t) {
		this(x,y,d);
		this.tc = t;
	}
	//允许设定子弹属性的构造方法
	public Missle(int x,int y,Direction d,TankClient t,boolean g) {
		this(x,y,d,t);
		this.good = g;
	}
	//根据阵营获取子弹尺寸的接口
	public static int missle_sizex(boolean g) {
		Image tmp = g ? my_missle : enemy_missle;
		return tmp.getWidth(null);
	}
	public static int missle_sizey(boolean g) {
		Image tmp = g ? my_missle : enemy_missle;
		return tmp.getHeight(null);
	}
	//画出子弹
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
		//判断炮弹是否越过边界
		if(x<0 || y<0 || x>TankClient.mainwindow_sizex || y>TankClient.mainwindow_sizey) {
			tc.missles.remove(this);	//若炮弹出界则直接在管理员类中将其移除
		}
	}
	//返回包含当前子弹的一个矩形方块
	public Rectangle getRect() {
		return new Rectangle(x,y,Missle.missle_sizex(this.good),Missle.missle_sizey(this.good));
	}
	//检测子弹是否击中了某一辆坦克
	public boolean hitTank(Tank t) {
		//Rectangle.intersects()方法判断两个矩形是否相交
		//如果相交且这辆坦克还活着,那么移除子弹，移除被击中的坦克
		//还要判断子弹属性和击中的坦克属性是否相异(子弹不打友军)
		if( t.isLive() && this.getRect().intersects(t.getRect())
				&& this.good !=t.isGood()) {
			Explode e = new Explode(this.x,this.y,this.tc);	//创建一个explode
			this.tc.explodes.add(e);			//添加到explodes中
			
			if(t.isGood()) {	//我方坦克被击中后减少一定血量
				t.setHp(t.getHp()-attack);
				if(t.getHp()<=0)
					t.setLive(false);
			}
			else {				//敌方坦克直接死亡
				t.setLive(false);
				//同时在原位置产生一个回血块
				this.tc.bloods.add(new Blood(this.x,this.y,this.tc));
			}
			tc.missles.remove(this);	//击中坦克后移除当前这枚子弹			
			return true;
		}
		else
			return false;
	}
	
	//检测子弹是否打中了坦克(检测所有坦克)
	public boolean hitTanks(List<Tank> t) {
		for(int i=0;i<t.size();i++) {
			if(this.hitTank(t.get(i)))
				return true;
		}
		return false;
	}
	
	//子弹撞(单个)墙块
	public boolean hitWall(Wall w) {
		if(this.getRect().intersects(w.getRect())) {
			this.tc.missles.remove(this);	//撞墙了直接移除该粒子弹
			return true;
		}
		else {
			return false;
		}
	}
	//子弹撞击一组墙
	public boolean hitWalls(List<Wall> ws) {
		for(int i=0;i<ws.size();i++) {
			if(this.hitWall(ws.get(i)))
				return true;
		}
		return false;
	}
	
}
