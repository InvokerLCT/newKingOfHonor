package com.sxt;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;

import com.sxt.beast.Beast;

public class GameFrame extends JFrame {

	// 游戏模式 0 选择英雄，1 游戏开始，2 游戏胜利，3 游戏失败
	int state = 0;
	// 窗口尺寸
	private int windowWidth = 1400;
	private int windowHeight = 700;
	// 双缓冲图片
	private Image offScreenImage = null;
	// 攻击图片
	private Image attack = Toolkit.getDefaultToolkit().getImage("img/attack.jpg");
	//游戏胜利失败图片
	private Image gameWin = Toolkit.getDefaultToolkit().getImage("img/gameWin.png");
	private Image gameLose = Toolkit.getDefaultToolkit().getImage("img/gameLose.png");
	// 游戏背景
	Background background = new Background(this);
	// 游戏玩家
	Champion player;
	Champion champion = new ChampionHouyi(this,700,3800);
	// 双方小兵
	MinionBlue mb = new MinionBlue(this);
	MinionRed mr = new MinionRed(this);
	// 防御塔
	Turret turret = new Turret(this);
	//野怪
	public Beast beast = new Beast(this);
	// 攻击按钮
	JButton attackButton;
	// 游戏元素列表
	public ArrayList<GameObject> objList = new ArrayList<GameObject>();
	ArrayList<GameObject> redList = new ArrayList<GameObject>();// 红色方
	ArrayList<GameObject> blueList = new ArrayList<GameObject>();// 蓝色方
	public ArrayList<GameObject> removeList = new ArrayList<GameObject>();// 存放将要删除的元素

	public void launch() {
		// 设置尺寸
		setSize(windowWidth, windowHeight);
		// 窗口居中
		setLocationRelativeTo(null);
		// 关闭事件
		setDefaultCloseOperation(3);
		// 用户不能调整窗口大小
		setResizable(false);
		// 标题
		setTitle("New王者荣耀");
		// 窗口可见
		setVisible(true);
		// 添加键盘监视器
		this.addKeyListener(new GameFrame.KeyMonitor());
		// 添加游戏元素 （cannot change the order）
		objList.add(background);
		objList.addAll(turret.turretList);
		objList.addAll(beast.beastList);
		blueList.add(turret.turretList.get(0));
		redList.add(turret.turretList.get(4));

		/**
		 * 攻击按钮
		 * Note that the coordinates here are its position on the window
		 */
		attackButton = new JButton();
		attackButton.setSize(130, 132);
		attackButton.setLocation(1150, 430);
		attackButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 按钮事件
				player.attack(redList);
			}
		});
		this.add(attackButton);

		while (true) {
			if (state == 1) {
				// 添加小兵
				//mb.createMinion(this, blueList);
				//mr.createMinion(this, redList);
				// 添加防御塔
				turret.addTurret();
			}
			repaint();
			try {
				Thread.sleep(17);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void paint(Graphics g) {
		//When it is an empty image, operate this to it.
		if (offScreenImage == null) {
			offScreenImage = this.createImage(5984, 4452);
		}
		//get the brush for that image
		Graphics gImage = offScreenImage.getGraphics();
		if (state == 0) {
			for(int i = 0; i < champion.championList.size(); i++) {
				//添加图片
				Image classical = champion.championList.get(i).classical;
				gImage.drawImage(classical, i*160, 20, null);
				
				//添加按钮
				JButton championButton = new JButton();
				championButton.setSize(150, 150);
				championButton.setLocation(i*150, 0);
				int a = i;
				championButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						state = 1;
						player = champion.championList.get(a);
						objList.add(player);
						player.addButton();
					}
				});
				this.add(championButton);
			}
		} else if (state == 1) {
			for (int i = 0; i < objList.size(); i++) {
				objList.get(i).paintSelf(gImage);
			}
			// 绘制攻击图片
			gImage.drawImage(attack, player.getX() + 500, player.getY() + 100, null);
			objList.removeAll(removeList);
		} else if (state == 2) {//游戏胜利
			gImage.drawImage(gameWin, 0, 5, null);
		} else if (state == 3) {//游戏失败
			gImage.drawImage(gameLose, 0, 5, null);
		}
		if (state != 1) {
			g.drawImage(offScreenImage, 0, 0, null);
		} else {
			g.drawImage(offScreenImage, -player.getX() + 700, -player.getY() + 350, null); //The hero is in the center
		}
		/**
		 * 添加按钮后不能调用键盘事件 因为程序的焦点变成了按钮 this.requestFocus() 把焦点重新改变到游戏界面上
		 */
		this.requestFocus();
	}

	// main方法
	public static void main(String[] args) {
		GameFrame gameFrame = new GameFrame();
		gameFrame.launch();
	}
	
	// 键盘事件
	private class KeyMonitor extends KeyAdapter {
		@Override
		//will be called only when the keyboard is pressed
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode(); //get the key value of the key that was pressed
			player.keyPressed(e); //call the player's keyboard event in the game interface
		}
		@Override
		//will be called only when the keyboard is released.
		public void keyReleased(KeyEvent e) {
			int key = e.getKeyCode(); //get the key value of the key that was pressed
			player.keyReleased(e);  //call the player's keyboard event in the game interface
		}
	}
}