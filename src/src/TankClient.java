package src;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.awt.Toolkit;
import java.util.Properties;

public class TankClient extends Frame{

	//����Properties�����
	private static Properties props = new Properties();
	
	private static final long serialVersionUID = 1L;
	private static int initial_enemy_num;			//��ʼ�ĵз�̹������

	private static Toolkit tk = Toolkit.getDefaultToolkit();
	//��ȡ����ͼƬ
	private static Image bkp = tk.getImage(TankClient.class.getClassLoader().getResource("resources/background.jpg"));
	
	//�ҷ�̹��
	//���뵱ǰTankClient������,������Tank���޸�TankClient�ĳ�Ա����
	Tank myTank=new Tank(0,0,true,this);	
	//��Ҫ����ѭ���������������ӵ�,�����ArrayList�ȽϿ�
	//����ҪƵ��ɾ��������ҪƵ�������ĳ�����LinkedList����	
	
	//�洢�з�̹�˶����List
	List <Tank> tanks = new ArrayList <Tank>();
	//�洢�ӵ������List
	List <Missle> missles = new ArrayList <Missle>();
	//�洢��ը�����List
	List <Explode> explodes =  new ArrayList <Explode>();	
	//�洢ǽ�����List
	List <Wall>	walls = new ArrayList <Wall>();
	//�洢��Ѫ������List
	List<Blood> bloods = new ArrayList <Blood>();
	
	public static int mainwindow_locx,mainwindow_locy;		//������λ��
	public static int mainwindow_sizex,mainwindow_sizey;	//�����ڴ�С
	public static int repaint_time_interval;				//�ػ��¼����(����)
	
	//��ͼ����,�Ƚ�ͼ�λ��ڻ�����,�ٽ������е�����һ���Ի���ԭͼ��
	Image OffScreenImage = null;	
	
	//�û��弼��������˸
	@Override
	public void update(Graphics g) {
		//��OffScreenImageΪ��ʱ����һ����ԭͼ��ͬ�ߴ��Image
		if(OffScreenImage==null) {
			OffScreenImage=this.createImage(mainwindow_sizex,mainwindow_sizey);
		}
		//��ȡ�����"����"
		Graphics gOffScreen = OffScreenImage.getGraphics();
		//������ͼ���ڷ���
		gOffScreen.drawImage(bkp, 0, 0, null);
		//�����ݻ��ڻ�����
		paint(gOffScreen);
		//����������ݻ���ԭͼ��
		g.drawImage(OffScreenImage, 0, 0, null);
	}
	
	@Override
	public void paint(Graphics g) {		//��д�����ػ��¼�(��repaint()�����б�����)
		
		//�з�̹������֮�����¼���̹��
		if(tanks.size() == 0) {
			for(int i=0;i<initial_enemy_num;i++) {
				Direction d = Tank.get_random_dir();	//��ȡ�������
				Tank t = new Tank(0,0,false,this,d);	//����һ��̹��
				t.setRandom_Location();					//�������λ��
				tanks.add(t);
			}

		}
		
		//ʵʱ��ʾ��missles��explodes��Ԫ�ص�����
		g.drawString("Missles count: "+missles.size(), 10, 50);
		g.drawString("Explodes count: "+explodes.size(), 10, 70);
		g.drawString("Tanks count: "+tanks.size(), 10, 90);
		
		myTank.draw(g);		//�����ҷ�̹��
		myTank.collidesWithWalls(walls);	//�ҷ�̹���Ƿ�ײǽ
		myTank.collidesWithTanks(tanks);	//��ֹ�ҷ�̹����з�̹����ײ	
		myTank.eat_bloods(bloods);			//�ҷ�̹���Ƿ�Ե���Ѫ��
		//�������̹��
		for(int i=0;i<tanks.size();i++) {
			Tank t = tanks.get(i);
			t.collidesWithWalls(walls);//�ж�̹���Ƿ�ײǽ
			t.collidesWithTanks(tanks);	//��ֹ�з�̹���໥��ײ
			t.eat_bloods(bloods);		//�з�̹�˳Ե���Ѫ��
			t.draw(g);
		}

		//��������ӵ�
		for(int i=0;i<missles.size();i++) {
			Missle m = missles.get(i);	//get()�������±�ȡ�ӵ�
			m.draw(g);					//�����ӵ�
			m.hitTanks(tanks);		//�ҷ��ӵ�����з�̹��
			m.hitTank(myTank);		//�з��ӵ�����ҷ�̹��
			m.hitWalls(walls);		//�ӵ�ײǽ

		}
		//���������ը
		for(int i=0;i<explodes.size();i++) {
			Explode e = explodes.get(i);
			e.draw(g);
		}
		//�������ǽ��
		for(int i=0;i<walls.size();i++) {
			Wall w = walls.get(i);
			w.draw(g);
		}		
		//���������Ѫ��
		for(int i=0;i<bloods.size();i++) {
			Blood b = bloods.get(i);
			b.draw(g);
		}
	}
	
	public void launchFrame() {
		//���������ļ�
		try {
			props.load(TankClient.class.getClassLoader().getResourceAsStream("config/Tankwar.properties"));
			//�����������,ԭ����
			//props.load(this.getClass().getClassLoader().getResourceAsStream("config/TankWar.properties"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		//�������ļ��ж�ȡ����
		//ע��ÿ���޸������ļ���Ҫ����ߵ�Package Explorer��refreshһ��
		mainwindow_locx=Integer.parseInt(props.getProperty("mainwindow_locx"));
		mainwindow_locy=Integer.parseInt(props.getProperty("mainwindow_locy"));
		mainwindow_sizex=Integer.parseInt(props.getProperty("mainwindow_sizex"));
		mainwindow_sizey=Integer.parseInt(props.getProperty("mainwindow_sizey"));
		repaint_time_interval=Integer.parseInt(props.getProperty("repaint_time_interval"));
		initial_enemy_num=Integer.parseInt(props.getProperty("initial_enemy_num"));
		
		Tank.init();	//��ʼ��̹�������ز���
		myTank.setHp(Tank.full_hp);
		myTank.setRandom_Location();	//�ҷ�̹�˳��������λ��
		//����һ�������ĵз�̹��
		for(int i=0;i<initial_enemy_num;i++) {
			Direction d = Tank.get_random_dir();	//��ȡ�������
			Tank t = new Tank(0,0,false,this,d);	//����һ��̹��
			t.setRandom_Location();					//�������λ��
			tanks.add(t);
		}
		
		//��������ǽ��(����Ĵ��������ǽ���λ��)
		for(int i=0;i<6;i++) {
			walls.add(new Wall(600+i*50,600,this));
		}
		for(int i=0;i<5;i++) {
			walls.add(new Wall(200,400+i*50,this));
		}
		
		this.setLocation(mainwindow_locx,mainwindow_locy);			//���ô���λ��
		this.setSize(mainwindow_sizex,mainwindow_sizey); 				//���ô��ڴ�С
		this.setTitle("TankWar3.0");			//���ô��ڱ���
		
		this.addWindowListener(new WindowAdapter() {	//���崰�ڹر��¼�
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);		//��ʾ�����˳�
			}
		});

		this.addKeyListener(new KeyMonitor());	//����ʾ����֮ǰ��Ӽ��̼�������	
		
		setVisible(true);			//��ʾ����
		setResizable(false);		//������ı䴰�ڴ�С
		
		new Thread(new PaintThread()).start();	//�����ػ��̲߳�����
	}
	
	//������
	public static void main(String[] args) {
		TankClient tc=new TankClient();
		tc.launchFrame();
	}
	
	//ʵ���ػ��߳�
	//�ڲ���,���ڵ����ⲿ��ĳ�Ա�ͷ���
	//�����㹫��,ֻΪ��ǰ�����
	//�̳�һ���ӿ�ʱҪʵ�ָýӿ��е����з���,��ʹ��һ���յ�ʵ��
	private class PaintThread implements Runnable{	
		@Override
		public void run() {
			while(true) {
				repaint();	//repaint()���ȵ���update(),�ٵ���paint()
				try {
					Thread.sleep(repaint_time_interval);	//��ͣһ��ʱ��
				} catch (InterruptedException e) {
					e.printStackTrace();
				}					
			}
		}
	}
	
	//��Ӽ����¼�����,�̳�KeyAdapter��
	private class KeyMonitor extends KeyAdapter{

		//��д���¼����¼��Ĵ���
		@Override
		public void keyPressed(KeyEvent e) {
			myTank.keyPressed(e);
		}
		//��д�ͷż����¼��Ĵ���
		@Override
		public void keyReleased(KeyEvent e) {
			myTank.keyReleased(e);
		}		
	}

}
