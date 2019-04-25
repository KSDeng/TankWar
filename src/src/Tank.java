package src;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.awt.Toolkit;
import java.util.Properties;

//坦克类
public class Tank {
	
	private static Properties props =  new Properties();
	
	private int x,y;			//坦克的位置	
	public static int enemytank_speedx,enemytank_speedy;		//敌方坦克在两个方向上的移动速度
	public static int mytank_speedx,mytank_speedy;	//我方坦克在两个方向上的移动速度
	private boolean bL=false,bR=false,bU=false,bD=false;	//四个方向的标记
	private boolean good;			//标记当前坦克的敌我属性
	public static int full_hp;		//满血值 
	private int hp;					//坦克当前血量
	private BloodBar bb = new BloodBar();	//血量槽
	private boolean live = true;	//标记坦克是否存活

	TankClient tc = null;	//声明TankClient变量,用于接收引用
	
	private Direction dir = Direction.STOP;	//控制坦克方向的变量,初始化为STOP
	private Direction ptdir = Direction.U;	//控制炮筒的方向,初始化为U
	private static int game_difficulty;				//游戏难度,1-100,数字越大难度越大
	private int prex,prey;			//记录坦克上一步的位置(用于处理与墙的碰撞)
	
	private static Random r = new Random();		//随机数产生器,所有的坦克对象共享
	private static int per_step_range;			//一次移动步数的波动范围
	private static int per_step_min;			//一次最小移动步数
	private int step_num = 0;
	
	private static Toolkit tk = Toolkit.getDefaultToolkit();
	//构造<Direction, Image>组成的字典,便于按方向对图片进行访问
	private static Map<Direction, Image> enemy_imgs = new HashMap<Direction, Image>();
	private static Map<Direction, Image> my_imgs =  new HashMap<Direction, Image>();
	//敌方坦克图片
	public static Image[] enemy_tank_imgs = null;
	//注意这种初始化方法,好处是可以添加除初始化以外的其它代码,更加灵活
	static{
		enemy_tank_imgs =  new Image[] {
			tk.getImage(Tank.class.getClassLoader().getResource("resources/enemy/u.png")),
			tk.getImage(Tank.class.getClassLoader().getResource("resources/enemy/d.png")),
			tk.getImage(Tank.class.getClassLoader().getResource("resources/enemy/l.png")),
			tk.getImage(Tank.class.getClassLoader().getResource("resources/enemy/r.png")),
			tk.getImage(Tank.class.getClassLoader().getResource("resources/enemy/lu.png")),
			tk.getImage(Tank.class.getClassLoader().getResource("resources/enemy/ru.png")),
			tk.getImage(Tank.class.getClassLoader().getResource("resources/enemy/ld.png")),
			tk.getImage(Tank.class.getClassLoader().getResource("resources/enemy/rd.png"))
		};
		//将键值对放入字典中
		enemy_imgs.put(Direction.U, enemy_tank_imgs[0]);
		enemy_imgs.put(Direction.D, enemy_tank_imgs[1]);
		enemy_imgs.put(Direction.L, enemy_tank_imgs[2]);
		enemy_imgs.put(Direction.R, enemy_tank_imgs[3]);
		enemy_imgs.put(Direction.LU, enemy_tank_imgs[4]);
		enemy_imgs.put(Direction.RU, enemy_tank_imgs[5]);
		enemy_imgs.put(Direction.LD, enemy_tank_imgs[6]);
		enemy_imgs.put(Direction.RD, enemy_tank_imgs[7]);
		
		//System.out.println("Enemy images initialization finished.");
	}

	//我方坦克图片
	public static Image[] my_tank_imgs = null;
	static {
		my_tank_imgs =  new Image[] {
			tk.getImage(Tank.class.getClassLoader().getResource("resources/my/u.png")),
			tk.getImage(Tank.class.getClassLoader().getResource("resources/my/d.png")),
			tk.getImage(Tank.class.getClassLoader().getResource("resources/my/l.png")),
			tk.getImage(Tank.class.getClassLoader().getResource("resources/my/r.png")),
			tk.getImage(Tank.class.getClassLoader().getResource("resources/my/lu.png")),
			tk.getImage(Tank.class.getClassLoader().getResource("resources/my/ru.png")),
			tk.getImage(Tank.class.getClassLoader().getResource("resources/my/ld.png")),
			tk.getImage(Tank.class.getClassLoader().getResource("resources/my/rd.png")),
		};
		
		my_imgs.put(Direction.U, my_tank_imgs[0]);
		my_imgs.put(Direction.D, my_tank_imgs[1]);
		my_imgs.put(Direction.L, my_tank_imgs[2]);
		my_imgs.put(Direction.R, my_tank_imgs[3]);
		my_imgs.put(Direction.LU, my_tank_imgs[4]);
		my_imgs.put(Direction.RU, my_tank_imgs[5]);
		my_imgs.put(Direction.LD, my_tank_imgs[6]);
		my_imgs.put(Direction.RD, my_tank_imgs[7]);
		
		//System.out.println("My images initialization finished.");
	}
	//根据阵营获取坦克尺寸的接口
	public static int tank_sizex(boolean g) {
		Image tmp = g ? my_tank_imgs[0] : enemy_tank_imgs[0];
		return tmp.getWidth(null);
	}
	public static int tank_sizey(boolean g) {
		Image tmp = g ? my_tank_imgs[0] : enemy_tank_imgs[0];
		return tmp.getHeight(null);
	}
	
	//hp(血量)访问接口和设置接口
	public int getHp() {
		return hp;
	}
	public void setHp(int hp) {
		this.hp = hp;
	}	
	//初始化静态变量
	public static void init() {
		try {
			props.load(Tank.class.getClassLoader().getResourceAsStream("config/TankWar.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		enemytank_speedx = Integer.parseInt(props.getProperty("enemytank_speedx"));
		enemytank_speedy = Integer.parseInt(props.getProperty("enemytank_speedy"));
		mytank_speedx = Integer.parseInt(props.getProperty("mytank_speedx"));
		mytank_speedy = Integer.parseInt(props.getProperty("mytank_speedy"));
		full_hp = Integer.parseInt(props.getProperty("full_hp"));
		game_difficulty = Integer.parseInt(props.getProperty("game_difficulty"));
		per_step_min = Integer.parseInt(props.getProperty("per_step_min"));
		per_step_range = Integer.parseInt(props.getProperty("per_step_range"));
	}
	
	//构造函数允许设置是我方还是敌方
	public Tank(int x, int y,boolean g) {
		this.x = x;
		this.y = y;
		this.prex = x;
		this.prey = y;
		this.good=g;
	}
	//允许传入管理员类的构造函数
	public Tank(int x,int y,boolean g,TankClient t) {
		this(x,y,g);		//调用另一个构造函数(这在C++中是不允许的)
		this.tc=t;
	}
	//允许传入初始方向的构造函数
	public Tank(int x,int y,boolean g,TankClient t,Direction d) {
		this(x,y,g,t);
		this.dir = d;
	}
	//用于访问私有成员good
	public boolean isGood() {
		return this.good;
	}
	//用于修改私有成员live的接口
	public void setLive(boolean l) {
		this.live = l;
	}
	//用于读取私有成员live的接口
	public boolean isLive() {
		return this.live;
	}
	//画出坦克,形参为一支画笔(Graphics)
	public void draw(Graphics g) {
		
		if(!live) {			//若当前坦克已死亡
			if(!this.good) {
				//敌方坦克死亡则从tanks中去除
				this.tc.tanks.remove(this);				
			}
			return;
		}
		if(this.isGood()) bb.draw(g); 		//我方坦克画出血槽
		//根据坦克的阵营和方向画出图片
		if(this.good)
			g.drawImage(my_imgs.get(ptdir), this.x, this.y, null);
		else
			g.drawImage(enemy_imgs.get(ptdir), this.x, this.y, null);
		
		
		move();						//每画一次调用一次move
	}
	//坦克的移动(八个方向)
	private void move() {
		//根据敌我属性的不同设置不同的速度
		int sx = this.good?mytank_speedx:enemytank_speedx;
		int sy = this.good?mytank_speedy:enemytank_speedy;
		//记录上一步坦克所在的位置
		this.prex = this.x;
		this.prey = this.y;
		//根据不同的运动方向改变坐标
		switch(dir) {
		case U:
			y-=sy;break;
		case D:
			y+=sy;break;
		case L:
			x-=sx;break;
		case R:
			x+=sx;break;
		case LU:
			x-=sx;y-=sy;break;
		case RU:
			x+=sx;y-=sy;break;
		case LD:
			x-=sx;y+=sy;break;
		case RD:
			x+=sx;y+=sy;break;
		case STOP:
			break;
		}
		//当坦克未停止时,炮筒方向与坦克运动方向相同
		if(this.dir != Direction.STOP)	
			this.ptdir = this.dir;
		
		//限制坦克不能开出边界
		int tanksizex = Tank.tank_sizex(this.good);
		int tanksizey = Tank.tank_sizey(this.good);
		if(x < 0)		x = 0;
		if(y < 50)	y = 50;	//这里边界值设为50是考虑了标题栏的宽度
		if(x + tanksizex > TankClient.mainwindow_sizex)
			x = TankClient.mainwindow_sizex - tanksizex;
		if(y + tanksizey > TankClient.mainwindow_sizey)
			y = TankClient.mainwindow_sizey - tanksizey;
		//敌方坦克随机改变方向
		if(!good) {		
			if(step_num==0) {		//step_num减为0时改变方向
				this.dir = Tank.get_random_dir();		//获取随机方向
				step_num = r.nextInt(per_step_range)+per_step_min;	//重置移动计数
			}
			else {
				step_num--;	//移动计数--
			}
			//当产生的随机数小于设置的难度值时敌方坦克开火
			if(r.nextInt(100)<game_difficulty)
				fire();
		}
	}
	//随机设置坦克的位置
	public void setRandom_Location() {
		//当产生的位置与墙或者敌方坦克冲撞或刚好吃到血块时重新产生位置
		do {
			int locx = r.nextInt(TankClient.mainwindow_sizex);
			int locy = r.nextInt(TankClient.mainwindow_sizey);
			this.x = locx;
			this.y = locy;			
		}while(this.collidesWithWalls(this.tc.walls)
				|| this.collidesWithTanks(this.tc.tanks)
				|| this.eat_bloods(this.tc.bloods));
	}
	//处理键盘按下的事件
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();	//获得按键的虚拟码
		
		switch(key) {
		case KeyEvent.VK_R:		//R键我方重新获得生命
			if(this.good && !this.live) {
				this.setLive(true);	
				this.setHp(full_hp);		//重新恢复满血
				this.setRandom_Location();	//出现在随机位置
			}
			break;
		case KeyEvent.VK_SPACE: fire();		break;	//空格键开火
		case KeyEvent.VK_S:		superFire();break;	//发射超级炮弹
		case KeyEvent.VK_UP: 	bU=true;	break;	//上方向键
		case KeyEvent.VK_DOWN:	bD=true;	break;	//下方向键
		case KeyEvent.VK_LEFT: 	bL=true;	break;	//左方向键
		case KeyEvent.VK_RIGHT: bR=true;	break;	//右方向键
		}
		locateDirection();		//处理完键盘事件后重新设置方向
	}
	//处理键盘释放的事件
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		switch(key) {
		//case KeyEvent.VK_SPACE: fire();	break;	//也可以设计为释放按键时开火,这样可以限制开火速度
		case KeyEvent.VK_UP: 	bU=false;	break;	//上方向键
		case KeyEvent.VK_DOWN:	bD=false;	break;	//下方向键
		case KeyEvent.VK_LEFT: 	bL=false;	break;	//左方向键
		case KeyEvent.VK_RIGHT: bR=false;	break;	//右方向键
		}
		locateDirection();		//处理完键盘事件后重新设置方向
	}
	//设置坦克方向
	private void locateDirection() {
		if(bL && !bR && !bU && !bD)
			dir=Direction.L;
		else if(!bL && bR && !bU && !bD)
			dir=Direction.R;
		else if(!bL && !bR && bU && !bD)
			dir=Direction.U;
		else if(!bL && !bR && !bU && bD)
			dir=Direction.D;
		else if(bL && !bR && bU && !bD)
			dir=Direction.LU;
		else if(!bL && bR && bU && !bD)
			dir=Direction.RU;
		else if(bL && !bR && !bU && bD)
			dir=Direction.LD;
		else if(!bL && bR && !bU && bD)
			dir=Direction.RD;
		else if(!bL && !bR && !bU && !bD)
			dir=Direction.STOP;
	}
	
	//坦克(朝炮筒方向)开火
	private Missle fire() {
		if(!live) return null;	//当坦克被击毁后无法再开火
		//计算子弹射出的位置, 保证其从坦克的中间位置射出
		int tanksizex = Tank.tank_sizex(this.good);
		int tanksizey = Tank.tank_sizey(this.good);
		int mx = this.x + tanksizex/2 - Missle.missle_sizex(this.good)/2;	
		int my = this.y + tanksizey/2 - Missle.missle_sizey(this.good)/2;
		
		//根据子弹位置、炮筒方向和阵营创建子弹对象(实现当坦克静止时也能发射炮弹的功能)
		Missle m =  new Missle(mx,my,this.ptdir,this.tc,this.good);
		tc.missles.add(m);		//向missles中添加元素
		return m;
	}
	
	//朝特定方向开火
	private Missle fire(Direction set_dir) {
		if(!live) return null;	
		int tanksizex = Tank.tank_sizex(this.good);
		int tanksizey = Tank.tank_sizey(this.good);
		int mx = this.x + tanksizex/2 - Missle.missle_sizex(this.good)/2;	
		int my = this.y + tanksizey/2 - Missle.missle_sizey(this.good)/2;
		
		Missle m =  new Missle(mx,my,set_dir,this.tc,this.good);
		tc.missles.add(m);		
		return m;
	}
	
	//获取包含坦克的矩形方块
	public Rectangle getRect() {
		return new Rectangle(x,y,Tank.tank_sizex(this.good),Tank.tank_sizey(this.good));
	}
	
	//坦克停在原位置
	private void stay() {
		this.x = this.prex;
		this.y = this.prey;
	}
	
	//判断坦克是否撞(单块)墙
	public boolean collidesWithWall(Wall w) {
		if(this.live && this.getRect().intersects(w.getRect())) {
			//坦克撞墙了则停在前一个位置(此时没有与墙的范围冲突,故还可以调整方向并离开当前位置)
			this.stay();	
			return true;
		}
		else
			return false;
	}
	//判断坦克是否撞击一堵墙
	public boolean collidesWithWalls(List<Wall> ws) {
		for(int i=0;i<ws.size();i++) {
			if(this.collidesWithWall(ws.get(i)))
				return true;
		}
		return false;
	}
	//判断两辆坦克是否相撞
	public boolean collidesWithTank(Tank t) {
		if(this!=t) {		//碰撞检测首先应该排除自身的情况(否则一直会发生碰撞)
			if(this.live && t.isLive()
					&& this.getRect().intersects(t.getRect())) {
				//若两辆存活的坦克"即将"则二者均改变方向
				this.stay();
				t.stay();
				return true;
			}
		}
		return false;
	}
	//判断坦克是否相互碰撞
	public boolean collidesWithTanks(java.util.List<Tank> tanks) {
		for(int i=0;i<tanks.size();i++) {
			Tank t =  tanks.get(i);
			if(this.collidesWithTank(t))
				return true;
		}
		return false;
	}
	//产生一个随机方向,作为该类的静态函数
	public static Direction get_random_dir() {
		Direction[] dir_array = Direction.values();	//枚举类型转化为数组	
		int rn = r.nextInt(dir_array.length);		//产生一个[0-dir_array.length)的随机整数
		return dir_array[rn];
	}
	
	//发射超级炮弹(八个方向各打一发)
	private void superFire() {
		Direction[] dir_array = Direction.values();
		for(int i=0;i<dir_array.length;i++) {
			if(dir_array[i]!=Direction.STOP)	
				fire(dir_array[i]);		//除了STOP的其它方向每个方向各发射一发子弹
				//this.tc.missles.add(fire(dir_array[i]));	//这种方式会导致重复调用
		}
	}
	
	//内部类:血量槽(专门为坦克类服务)
	private class BloodBar{
		public void draw(Graphics g) {
			//获取我方坦克宽度
			int tanksizex = Tank.tank_sizex(true);

			Color c = g.getColor();
			g.setColor(Color.YELLOW);
			g.drawRect(x, y-10, tanksizex, 5);	//血条外框
			int w = tanksizex*hp/100;			//血量显示的宽度
			g.fillRect(x, y-10, w, 5);				//血量
			g.setColor(c);
		}
	}
	
	//坦克是否吃到(单个)回血块
	public boolean eat_blood(Blood b) {
		//我方坦克还存活、血块还存在且碰撞,则表示吃到血块
		if(this.good && this.live && this.getRect().intersects(b.getRect())) {
			if(this.getHp()<Tank.full_hp)		//若当前不是满血
				this.setHp(this.getHp()+Missle.attack/2);	//吃到血块后回复一颗子弹一半的伤害
			this.tc.bloods.remove(b);		//吃完后移除这个血块
			return true;
		}
		//敌方坦克可以让回血块消失
		else if(!this.good && this.live && this.getRect().intersects(b.getRect())) {		
			this.tc.bloods.remove(b);
			return false;
		}
		else
			return false;
	}
	//坦克是否有吃到回血块
	public boolean eat_bloods(List<Blood> bs) {
		for(int i=0;i<bs.size();i++) {
			if(this.eat_blood(bs.get(i)))
				return true;			
		}
		return false;
	}
}






