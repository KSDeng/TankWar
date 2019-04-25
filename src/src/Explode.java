package src;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

//显示爆炸效果的类
public class Explode {
	int x,y;								//爆炸事件发生的位置
	private boolean live = true;			//标记是否存活
	private TankClient tc;					//"大管家"的引用	
	
	//创建Toolkit用于进行操作系统级别的操作
	private static Toolkit tk = Toolkit.getDefaultToolkit();
	
	//加载图片(反射机制)
	//注意:图片素材必须在classpath目录中,而src目录会被默认编译到classpath中,因此把素材放到这个目录下是可以的
	//每次更新这里的代码需要refresh一下eclipse左边Package Explorer中对应的目录树,否则会报空指针错误(由找不到文件引起)
	private static Image[] imgs = {
		tk.getImage(Explode.class.getClassLoader().getResource("resources/explode/0.png")),
		tk.getImage(Explode.class.getClassLoader().getResource("resources/explode/1.png")),
		tk.getImage(Explode.class.getClassLoader().getResource("resources/explode/2.png")),
		tk.getImage(Explode.class.getClassLoader().getResource("resources/explode/3.png")),
		tk.getImage(Explode.class.getClassLoader().getResource("resources/explode/4.png")),
		tk.getImage(Explode.class.getClassLoader().getResource("resources/explode/5.png"))
	};
	int step = 0;							//标记当前画到第几步
	
	private boolean init = false;			//标记是否初始化
	
	public Explode(int x,int y,TankClient tc) {
		this.x = x;
		this.y = y;
		this.tc = tc;
	}
	public void draw(Graphics g) {
		//初始化将图片加入内存中,解决第一次爆炸无图片效果的bug
		if(!init) {
			for(int i=0;i<imgs.length;i++) {
				g.drawImage(imgs[i], -200, -200, null);
			}
			init = true;
		}
		
		//当step超出diameter数组的最后一个元素的下标索引时step重新置零,爆炸消亡
		if(step == imgs.length) {
			live = false;
			step = 0;
			return;
		}		
		//当爆炸事件不存活时从explodes中移除,并结束方法
		if(!live) {
			this.tc.explodes.remove(this);	
			return;
		}
		//直接画出每张图片
		g.drawImage(imgs[step], this.x, this.y, null);
		step++;

	}
}
