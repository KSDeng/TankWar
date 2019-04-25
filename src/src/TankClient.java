package src;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.awt.Toolkit;
import java.util.Properties;

public class TankClient extends Frame{

	//创建Properties类对象
	private static Properties props = new Properties();
	
	private static final long serialVersionUID = 1L;
	private static int initial_enemy_num;			//初始的敌方坦克数量

	private static Toolkit tk = Toolkit.getDefaultToolkit();
	//获取背景图片
	private static Image bkp = tk.getImage(TankClient.class.getClassLoader().getResource("resources/background.jpg"));
	
	//我方坦克
	//传入当前TankClient的引用,便于在Tank中修改TankClient的成员变量
	Tank myTank=new Tank(0,0,true,this);	
	//需要不断循环遍历画出所有子弹,因此用ArrayList比较快
	//在需要频繁删减、不需要频繁遍历的场景下LinkedList更快	
	
	//存储敌方坦克对象的List
	List <Tank> tanks = new ArrayList <Tank>();
	//存储子弹对象的List
	List <Missle> missles = new ArrayList <Missle>();
	//存储爆炸对象的List
	List <Explode> explodes =  new ArrayList <Explode>();	
	//存储墙对象的List
	List <Wall>	walls = new ArrayList <Wall>();
	//存储回血块对象的List
	List<Blood> bloods = new ArrayList <Blood>();
	
	public static int mainwindow_locx,mainwindow_locy;		//主窗口位置
	public static int mainwindow_sizex,mainwindow_sizey;	//主窗口大小
	public static int repaint_time_interval;				//重画事件间隔(毫秒)
	
	//画图缓冲,先将图形画在缓冲上,再将缓冲中的内容一次性画在原图上
	Image OffScreenImage = null;	
	
	//用缓冲技术消除闪烁
	@Override
	public void update(Graphics g) {
		//当OffScreenImage为空时创建一张与原图相同尺寸的Image
		if(OffScreenImage==null) {
			OffScreenImage=this.createImage(mainwindow_sizex,mainwindow_sizey);
		}
		//获取缓冲的"画笔"
		Graphics gOffScreen = OffScreenImage.getGraphics();
		//将背景图画在反面
		gOffScreen.drawImage(bkp, 0, 0, null);
		//将内容画在缓冲上
		paint(gOffScreen);
		//将缓冲的内容画在原图上
		g.drawImage(OffScreenImage, 0, 0, null);
	}
	
	@Override
	public void paint(Graphics g) {		//重写窗口重画事件(在repaint()方法中被调用)
		
		//敌方坦克死光之后重新加入坦克
		if(tanks.size() == 0) {
			for(int i=0;i<initial_enemy_num;i++) {
				Direction d = Tank.get_random_dir();	//获取随机方向
				Tank t = new Tank(0,0,false,this,d);	//产生一个坦克
				t.setRandom_Location();					//设置随机位置
				tanks.add(t);
			}

		}
		
		//实时显示出missles和explodes中元素的数量
		g.drawString("Missles count: "+missles.size(), 10, 50);
		g.drawString("Explodes count: "+explodes.size(), 10, 70);
		g.drawString("Tanks count: "+tanks.size(), 10, 90);
		
		myTank.draw(g);		//画出我方坦克
		myTank.collidesWithWalls(walls);	//我方坦克是否撞墙
		myTank.collidesWithTanks(tanks);	//防止我方坦克与敌方坦克碰撞	
		myTank.eat_bloods(bloods);			//我方坦克是否吃到回血块
		//逐个画出坦克
		for(int i=0;i<tanks.size();i++) {
			Tank t = tanks.get(i);
			t.collidesWithWalls(walls);//判断坦克是否撞墙
			t.collidesWithTanks(tanks);	//防止敌方坦克相互碰撞
			t.eat_bloods(bloods);		//敌方坦克吃掉回血块
			t.draw(g);
		}

		//逐个画出子弹
		for(int i=0;i<missles.size();i++) {
			Missle m = missles.get(i);	//get()方法按下标取子弹
			m.draw(g);					//画出子弹
			m.hitTanks(tanks);		//我方子弹打击敌方坦克
			m.hitTank(myTank);		//敌方子弹打击我方坦克
			m.hitWalls(walls);		//子弹撞墙

		}
		//逐个画出爆炸
		for(int i=0;i<explodes.size();i++) {
			Explode e = explodes.get(i);
			e.draw(g);
		}
		//逐个画出墙块
		for(int i=0;i<walls.size();i++) {
			Wall w = walls.get(i);
			w.draw(g);
		}		
		//逐个画出回血块
		for(int i=0;i<bloods.size();i++) {
			Blood b = bloods.get(i);
			b.draw(g);
		}
	}
	
	public void launchFrame() {
		//加载配置文件
		try {
			props.load(TankClient.class.getClassLoader().getResourceAsStream("config/Tankwar.properties"));
			//以下这句会出错,原因不明
			//props.load(this.getClass().getClassLoader().getResourceAsStream("config/TankWar.properties"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		//从配置文件中读取设置
		//注意每次修改配置文件后还要在左边的Package Explorer中refresh一下
		mainwindow_locx=Integer.parseInt(props.getProperty("mainwindow_locx"));
		mainwindow_locy=Integer.parseInt(props.getProperty("mainwindow_locy"));
		mainwindow_sizex=Integer.parseInt(props.getProperty("mainwindow_sizex"));
		mainwindow_sizey=Integer.parseInt(props.getProperty("mainwindow_sizey"));
		repaint_time_interval=Integer.parseInt(props.getProperty("repaint_time_interval"));
		initial_enemy_num=Integer.parseInt(props.getProperty("initial_enemy_num"));
		
		Tank.init();	//初始化坦克类的相关参数
		myTank.setHp(Tank.full_hp);
		myTank.setRandom_Location();	//我方坦克出现在随机位置
		//加入一定数量的敌方坦克
		for(int i=0;i<initial_enemy_num;i++) {
			Direction d = Tank.get_random_dir();	//获取随机方向
			Tank t = new Tank(0,0,false,this,d);	//产生一个坦克
			t.setRandom_Location();					//设置随机位置
			tanks.add(t);
		}
		
		//加入两组墙块(这里的代码控制了墙块的位置)
		for(int i=0;i<6;i++) {
			walls.add(new Wall(600+i*50,600,this));
		}
		for(int i=0;i<5;i++) {
			walls.add(new Wall(200,400+i*50,this));
		}
		
		this.setLocation(mainwindow_locx,mainwindow_locy);			//设置窗口位置
		this.setSize(mainwindow_sizex,mainwindow_sizey); 				//设置窗口大小
		this.setTitle("TankWar3.0");			//设置窗口标题
		
		this.addWindowListener(new WindowAdapter() {	//定义窗口关闭事件
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);		//表示正常退出
			}
		});

		this.addKeyListener(new KeyMonitor());	//在显示窗口之前添加键盘监听对象	
		
		setVisible(true);			//显示窗口
		setResizable(false);		//不允许改变窗口大小
		
		new Thread(new PaintThread()).start();	//创建重画线程并启动
	}
	
	//主函数
	public static void main(String[] args) {
		TankClient tc=new TankClient();
		tc.launchFrame();
	}
	
	//实现重画线程
	//内部类,便于调用外部类的成员和方法
	//不方便公开,只为当前类服务
	//继承一个接口时要实现该接口中的所有方法,即使是一个空的实现
	private class PaintThread implements Runnable{	
		@Override
		public void run() {
			while(true) {
				repaint();	//repaint()首先调用update(),再调用paint()
				try {
					Thread.sleep(repaint_time_interval);	//暂停一段时间
				} catch (InterruptedException e) {
					e.printStackTrace();
				}					
			}
		}
	}
	
	//添加键盘事件监听,继承KeyAdapter类
	private class KeyMonitor extends KeyAdapter{

		//重写按下键盘事件的处理
		@Override
		public void keyPressed(KeyEvent e) {
			myTank.keyPressed(e);
		}
		//重写释放键盘事件的处理
		@Override
		public void keyReleased(KeyEvent e) {
			myTank.keyReleased(e);
		}		
	}

}
