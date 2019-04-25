package src;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

//��ʾ��ըЧ������
public class Explode {
	int x,y;								//��ը�¼�������λ��
	private boolean live = true;			//����Ƿ���
	private TankClient tc;					//"��ܼ�"������	
	
	//����Toolkit���ڽ��в���ϵͳ����Ĳ���
	private static Toolkit tk = Toolkit.getDefaultToolkit();
	
	//����ͼƬ(�������)
	//ע��:ͼƬ�زı�����classpathĿ¼��,��srcĿ¼�ᱻĬ�ϱ��뵽classpath��,��˰��زķŵ����Ŀ¼���ǿ��Ե�
	//ÿ�θ�������Ĵ�����Ҫrefreshһ��eclipse���Package Explorer�ж�Ӧ��Ŀ¼��,����ᱨ��ָ�����(���Ҳ����ļ�����)
	private static Image[] imgs = {
		tk.getImage(Explode.class.getClassLoader().getResource("resources/explode/0.png")),
		tk.getImage(Explode.class.getClassLoader().getResource("resources/explode/1.png")),
		tk.getImage(Explode.class.getClassLoader().getResource("resources/explode/2.png")),
		tk.getImage(Explode.class.getClassLoader().getResource("resources/explode/3.png")),
		tk.getImage(Explode.class.getClassLoader().getResource("resources/explode/4.png")),
		tk.getImage(Explode.class.getClassLoader().getResource("resources/explode/5.png"))
	};
	int step = 0;							//��ǵ�ǰ�����ڼ���
	
	private boolean init = false;			//����Ƿ��ʼ��
	
	public Explode(int x,int y,TankClient tc) {
		this.x = x;
		this.y = y;
		this.tc = tc;
	}
	public void draw(Graphics g) {
		//��ʼ����ͼƬ�����ڴ���,�����һ�α�ը��ͼƬЧ����bug
		if(!init) {
			for(int i=0;i<imgs.length;i++) {
				g.drawImage(imgs[i], -200, -200, null);
			}
			init = true;
		}
		
		//��step����diameter��������һ��Ԫ�ص��±�����ʱstep��������,��ը����
		if(step == imgs.length) {
			live = false;
			step = 0;
			return;
		}		
		//����ը�¼������ʱ��explodes���Ƴ�,����������
		if(!live) {
			this.tc.explodes.remove(this);	
			return;
		}
		//ֱ�ӻ���ÿ��ͼƬ
		g.drawImage(imgs[step], this.x, this.y, null);
		step++;

	}
}
