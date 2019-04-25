package src;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.awt.Toolkit;
import java.util.Properties;

//̹����
public class Tank {
	
	private static Properties props =  new Properties();
	
	private int x,y;			//̹�˵�λ��	
	public static int enemytank_speedx,enemytank_speedy;		//�з�̹�������������ϵ��ƶ��ٶ�
	public static int mytank_speedx,mytank_speedy;	//�ҷ�̹�������������ϵ��ƶ��ٶ�
	private boolean bL=false,bR=false,bU=false,bD=false;	//�ĸ�����ı��
	private boolean good;			//��ǵ�ǰ̹�˵ĵ�������
	public static int full_hp;		//��Ѫֵ 
	private int hp;					//̹�˵�ǰѪ��
	private BloodBar bb = new BloodBar();	//Ѫ����
	private boolean live = true;	//���̹���Ƿ���

	TankClient tc = null;	//����TankClient����,���ڽ�������
	
	private Direction dir = Direction.STOP;	//����̹�˷���ı���,��ʼ��ΪSTOP
	private Direction ptdir = Direction.U;	//������Ͳ�ķ���,��ʼ��ΪU
	private static int game_difficulty;				//��Ϸ�Ѷ�,1-100,����Խ���Ѷ�Խ��
	private int prex,prey;			//��¼̹����һ����λ��(���ڴ�����ǽ����ײ)
	
	private static Random r = new Random();		//�����������,���е�̹�˶�����
	private static int per_step_range;			//һ���ƶ������Ĳ�����Χ
	private static int per_step_min;			//һ����С�ƶ�����
	private int step_num = 0;
	
	private static Toolkit tk = Toolkit.getDefaultToolkit();
	//����<Direction, Image>��ɵ��ֵ�,���ڰ������ͼƬ���з���
	private static Map<Direction, Image> enemy_imgs = new HashMap<Direction, Image>();
	private static Map<Direction, Image> my_imgs =  new HashMap<Direction, Image>();
	//�з�̹��ͼƬ
	public static Image[] enemy_tank_imgs = null;
	//ע�����ֳ�ʼ������,�ô��ǿ�����ӳ���ʼ���������������,�������
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
		//����ֵ�Է����ֵ���
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

	//�ҷ�̹��ͼƬ
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
	//������Ӫ��ȡ̹�˳ߴ�Ľӿ�
	public static int tank_sizex(boolean g) {
		Image tmp = g ? my_tank_imgs[0] : enemy_tank_imgs[0];
		return tmp.getWidth(null);
	}
	public static int tank_sizey(boolean g) {
		Image tmp = g ? my_tank_imgs[0] : enemy_tank_imgs[0];
		return tmp.getHeight(null);
	}
	
	//hp(Ѫ��)���ʽӿں����ýӿ�
	public int getHp() {
		return hp;
	}
	public void setHp(int hp) {
		this.hp = hp;
	}	
	//��ʼ����̬����
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
	
	//���캯�������������ҷ����ǵз�
	public Tank(int x, int y,boolean g) {
		this.x = x;
		this.y = y;
		this.prex = x;
		this.prey = y;
		this.good=g;
	}
	//���������Ա��Ĺ��캯��
	public Tank(int x,int y,boolean g,TankClient t) {
		this(x,y,g);		//������һ�����캯��(����C++���ǲ������)
		this.tc=t;
	}
	//�������ʼ����Ĺ��캯��
	public Tank(int x,int y,boolean g,TankClient t,Direction d) {
		this(x,y,g,t);
		this.dir = d;
	}
	//���ڷ���˽�г�Աgood
	public boolean isGood() {
		return this.good;
	}
	//�����޸�˽�г�Աlive�Ľӿ�
	public void setLive(boolean l) {
		this.live = l;
	}
	//���ڶ�ȡ˽�г�Աlive�Ľӿ�
	public boolean isLive() {
		return this.live;
	}
	//����̹��,�β�Ϊһ֧����(Graphics)
	public void draw(Graphics g) {
		
		if(!live) {			//����ǰ̹��������
			if(!this.good) {
				//�з�̹���������tanks��ȥ��
				this.tc.tanks.remove(this);				
			}
			return;
		}
		if(this.isGood()) bb.draw(g); 		//�ҷ�̹�˻���Ѫ��
		//����̹�˵���Ӫ�ͷ��򻭳�ͼƬ
		if(this.good)
			g.drawImage(my_imgs.get(ptdir), this.x, this.y, null);
		else
			g.drawImage(enemy_imgs.get(ptdir), this.x, this.y, null);
		
		
		move();						//ÿ��һ�ε���һ��move
	}
	//̹�˵��ƶ�(�˸�����)
	private void move() {
		//���ݵ������ԵĲ�ͬ���ò�ͬ���ٶ�
		int sx = this.good?mytank_speedx:enemytank_speedx;
		int sy = this.good?mytank_speedy:enemytank_speedy;
		//��¼��һ��̹�����ڵ�λ��
		this.prex = this.x;
		this.prey = this.y;
		//���ݲ�ͬ���˶�����ı�����
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
		//��̹��δֹͣʱ,��Ͳ������̹���˶�������ͬ
		if(this.dir != Direction.STOP)	
			this.ptdir = this.dir;
		
		//����̹�˲��ܿ����߽�
		int tanksizex = Tank.tank_sizex(this.good);
		int tanksizey = Tank.tank_sizey(this.good);
		if(x < 0)		x = 0;
		if(y < 50)	y = 50;	//����߽�ֵ��Ϊ50�ǿ����˱������Ŀ��
		if(x + tanksizex > TankClient.mainwindow_sizex)
			x = TankClient.mainwindow_sizex - tanksizex;
		if(y + tanksizey > TankClient.mainwindow_sizey)
			y = TankClient.mainwindow_sizey - tanksizey;
		//�з�̹������ı䷽��
		if(!good) {		
			if(step_num==0) {		//step_num��Ϊ0ʱ�ı䷽��
				this.dir = Tank.get_random_dir();		//��ȡ�������
				step_num = r.nextInt(per_step_range)+per_step_min;	//�����ƶ�����
			}
			else {
				step_num--;	//�ƶ�����--
			}
			//�������������С�����õ��Ѷ�ֵʱ�з�̹�˿���
			if(r.nextInt(100)<game_difficulty)
				fire();
		}
	}
	//�������̹�˵�λ��
	public void setRandom_Location() {
		//��������λ����ǽ���ߵз�̹�˳�ײ��պóԵ�Ѫ��ʱ���²���λ��
		do {
			int locx = r.nextInt(TankClient.mainwindow_sizex);
			int locy = r.nextInt(TankClient.mainwindow_sizey);
			this.x = locx;
			this.y = locy;			
		}while(this.collidesWithWalls(this.tc.walls)
				|| this.collidesWithTanks(this.tc.tanks)
				|| this.eat_bloods(this.tc.bloods));
	}
	//������̰��µ��¼�
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();	//��ð�����������
		
		switch(key) {
		case KeyEvent.VK_R:		//R���ҷ����»������
			if(this.good && !this.live) {
				this.setLive(true);	
				this.setHp(full_hp);		//���»ָ���Ѫ
				this.setRandom_Location();	//���������λ��
			}
			break;
		case KeyEvent.VK_SPACE: fire();		break;	//�ո������
		case KeyEvent.VK_S:		superFire();break;	//���䳬���ڵ�
		case KeyEvent.VK_UP: 	bU=true;	break;	//�Ϸ����
		case KeyEvent.VK_DOWN:	bD=true;	break;	//�·����
		case KeyEvent.VK_LEFT: 	bL=true;	break;	//�����
		case KeyEvent.VK_RIGHT: bR=true;	break;	//�ҷ����
		}
		locateDirection();		//����������¼����������÷���
	}
	//��������ͷŵ��¼�
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		switch(key) {
		//case KeyEvent.VK_SPACE: fire();	break;	//Ҳ�������Ϊ�ͷŰ���ʱ����,�����������ƿ����ٶ�
		case KeyEvent.VK_UP: 	bU=false;	break;	//�Ϸ����
		case KeyEvent.VK_DOWN:	bD=false;	break;	//�·����
		case KeyEvent.VK_LEFT: 	bL=false;	break;	//�����
		case KeyEvent.VK_RIGHT: bR=false;	break;	//�ҷ����
		}
		locateDirection();		//����������¼����������÷���
	}
	//����̹�˷���
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
	
	//̹��(����Ͳ����)����
	private Missle fire() {
		if(!live) return null;	//��̹�˱����ٺ��޷��ٿ���
		//�����ӵ������λ��, ��֤���̹�˵��м�λ�����
		int tanksizex = Tank.tank_sizex(this.good);
		int tanksizey = Tank.tank_sizey(this.good);
		int mx = this.x + tanksizex/2 - Missle.missle_sizex(this.good)/2;	
		int my = this.y + tanksizey/2 - Missle.missle_sizey(this.good)/2;
		
		//�����ӵ�λ�á���Ͳ�������Ӫ�����ӵ�����(ʵ�ֵ�̹�˾�ֹʱҲ�ܷ����ڵ��Ĺ���)
		Missle m =  new Missle(mx,my,this.ptdir,this.tc,this.good);
		tc.missles.add(m);		//��missles�����Ԫ��
		return m;
	}
	
	//���ض����򿪻�
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
	
	//��ȡ����̹�˵ľ��η���
	public Rectangle getRect() {
		return new Rectangle(x,y,Tank.tank_sizex(this.good),Tank.tank_sizey(this.good));
	}
	
	//̹��ͣ��ԭλ��
	private void stay() {
		this.x = this.prex;
		this.y = this.prey;
	}
	
	//�ж�̹���Ƿ�ײ(����)ǽ
	public boolean collidesWithWall(Wall w) {
		if(this.live && this.getRect().intersects(w.getRect())) {
			//̹��ײǽ����ͣ��ǰһ��λ��(��ʱû����ǽ�ķ�Χ��ͻ,�ʻ����Ե��������뿪��ǰλ��)
			this.stay();	
			return true;
		}
		else
			return false;
	}
	//�ж�̹���Ƿ�ײ��һ��ǽ
	public boolean collidesWithWalls(List<Wall> ws) {
		for(int i=0;i<ws.size();i++) {
			if(this.collidesWithWall(ws.get(i)))
				return true;
		}
		return false;
	}
	//�ж�����̹���Ƿ���ײ
	public boolean collidesWithTank(Tank t) {
		if(this!=t) {		//��ײ�������Ӧ���ų���������(����һֱ�ᷢ����ײ)
			if(this.live && t.isLive()
					&& this.getRect().intersects(t.getRect())) {
				//����������̹��"����"����߾��ı䷽��
				this.stay();
				t.stay();
				return true;
			}
		}
		return false;
	}
	//�ж�̹���Ƿ��໥��ײ
	public boolean collidesWithTanks(java.util.List<Tank> tanks) {
		for(int i=0;i<tanks.size();i++) {
			Tank t =  tanks.get(i);
			if(this.collidesWithTank(t))
				return true;
		}
		return false;
	}
	//����һ���������,��Ϊ����ľ�̬����
	public static Direction get_random_dir() {
		Direction[] dir_array = Direction.values();	//ö������ת��Ϊ����	
		int rn = r.nextInt(dir_array.length);		//����һ��[0-dir_array.length)���������
		return dir_array[rn];
	}
	
	//���䳬���ڵ�(�˸��������һ��)
	private void superFire() {
		Direction[] dir_array = Direction.values();
		for(int i=0;i<dir_array.length;i++) {
			if(dir_array[i]!=Direction.STOP)	
				fire(dir_array[i]);		//����STOP����������ÿ�����������һ���ӵ�
				//this.tc.missles.add(fire(dir_array[i]));	//���ַ�ʽ�ᵼ���ظ�����
		}
	}
	
	//�ڲ���:Ѫ����(ר��Ϊ̹�������)
	private class BloodBar{
		public void draw(Graphics g) {
			//��ȡ�ҷ�̹�˿��
			int tanksizex = Tank.tank_sizex(true);

			Color c = g.getColor();
			g.setColor(Color.YELLOW);
			g.drawRect(x, y-10, tanksizex, 5);	//Ѫ�����
			int w = tanksizex*hp/100;			//Ѫ����ʾ�Ŀ��
			g.fillRect(x, y-10, w, 5);				//Ѫ��
			g.setColor(c);
		}
	}
	
	//̹���Ƿ�Ե�(����)��Ѫ��
	public boolean eat_blood(Blood b) {
		//�ҷ�̹�˻���Ѫ�黹��������ײ,���ʾ�Ե�Ѫ��
		if(this.good && this.live && this.getRect().intersects(b.getRect())) {
			if(this.getHp()<Tank.full_hp)		//����ǰ������Ѫ
				this.setHp(this.getHp()+Missle.attack/2);	//�Ե�Ѫ���ظ�һ���ӵ�һ����˺�
			this.tc.bloods.remove(b);		//������Ƴ����Ѫ��
			return true;
		}
		//�з�̹�˿����û�Ѫ����ʧ
		else if(!this.good && this.live && this.getRect().intersects(b.getRect())) {		
			this.tc.bloods.remove(b);
			return false;
		}
		else
			return false;
	}
	//̹���Ƿ��гԵ���Ѫ��
	public boolean eat_bloods(List<Blood> bs) {
		for(int i=0;i<bs.size();i++) {
			if(this.eat_blood(bs.get(i)))
				return true;			
		}
		return false;
	}
}






