package com.sxt;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JButton;

import com.sxt.beast.Beast;

public class ChampionHouyi extends Champion {

	// 技能是否处于释放状态
	boolean ifAbilityThree = false;
	boolean ifAbilityTwo = false;
	// 鼠标监视器（Initialize the mouse monitor and don't forget to add it to the game interface）
	MouseMonitor m;
	MouseMonitorTwo m2;
	// 三技能多边形
	Polygon p;
	// 三技能三角函数
	double sin;
	double cos;
	// 三技能命中的目标
	GameObject abilityThreeTarget;
	// 三技能是否出界
	boolean ifXOutside;
	boolean ifYOutside;
	// 二技能位置
	int X_AbilityTwo;
	int Y_AbilityTwo;

	public ChampionHouyi(GameFrame gameFrame) {
		super(gameFrame);
		abilityOne = Toolkit.getDefaultToolkit().getImage("img/Houyi/abilityOne.jpg");
		abilityTwo = Toolkit.getDefaultToolkit().getImage("img/Houyi/abilityTwo.jpg");
		abilityThree = Toolkit.getDefaultToolkit().getImage("img/Houyi/abilityThree.jpg");
		classical = Toolkit.getDefaultToolkit().getImage("img/Houyi/Classical.jpg");
		// 三个技能冷却时间
		coolDownTimeOne = 14000;
		coolDownTimeTwo = 10000;
		coolDownTimeThree = 28000;
		// TODO Auto-generated constructor stub
	}

	public ChampionHouyi(GameFrame gameFrame, int i, int j) {
		// TODO Auto-generated constructor stub
		super(gameFrame, i, j);
	}

	public void exit(MouseAdapter ma) {
		this.gameFrame.removeMouseListener(ma);
	}

	public void attack() {
		if (isAttackCoolDown()) {
			ArrayList<GameObject> targets = new ArrayList<GameObject>();// 目标列表，最多三个目标
			for (GameObject redObj : this.gameFrame.redList) {
				if (this.recIntersectsCir(redObj.getRec(), getX() - 250, getY() - 250, 500)) {
					targets.add(redObj);
					if (targets.size() == 3) {
						break;
					}
				}
			}
			for (GameObject beastObj : this.gameFrame.beast.beastList) {
				if (this.recIntersectsCir(beastObj.getRec(), getX() - 250, getY() - 250, 500)) {
					targets.add(beastObj);
					if (targets.size() == 3) {
						break;
					}
				}
			}
			for (int i = 0; i < targets.size(); i++) {
				Bullet bullet;
				if (i == 0) {
					bullet = new Bullet(gameFrame, this, targets.get(i), 400, 50, "img/bullet.gif");
				} else {
					bullet = new Bullet(gameFrame, this, targets.get(i), 200, 50, "img/bullet.gif");
				}
				gameFrame.objList.add(bullet);
			}
			new AttackCD().start();
		}
	}

	public void abilityTwoAttack() {
		for (GameObject redObj : this.gameFrame.objList) {
			if (redObj instanceof MinionRed || Beast.class.isAssignableFrom(redObj.getClass())) {
				if (this.recIntersectsCir(redObj.getRec(), X_AbilityTwo - 60, Y_AbilityTwo - 60, 120)) {
					redObj.setCurrentHp(redObj.getCurrentHp() - 400);
				}
				if (this.recIntersectsCir(redObj.getRec(), X_AbilityTwo - 30, Y_AbilityTwo - 30, 60)) {
					redObj.setCurrentHp(redObj.getCurrentHp() - 200);
				}
			}
		}
	}

	public void abilityThreeMove() {
		p.translate((int) (50 * cos), -(int) (50 * sin));
		for (GameObject redObj : gameFrame.objList) {
			// 是红色方小兵 && 发生碰撞
			if ((redObj instanceof MinionRed || Beast.class.isAssignableFrom(redObj.getClass()))
					&& p.intersects(redObj.getRec())) {
				// 击中目标后，目标减血并眩晕，多边形消失
				redObj.setCurrentHp(redObj.getCurrentHp() - 400);
				abilityThreeTarget = redObj;
				new AbilityControlCD().start();
				ifAbilityThree = false;
				new AbilityThreeCD().start();
			}
		}
		if (!ifXOutside) {
			for (int x : p.xpoints) {
				if (x < 0 || x >= 5165) {
					ifXOutside = true;
					break;
				}
			}
		}
		if (!ifYOutside) {
			for (int y : p.ypoints) {
				if (y < 0 || y >= 4085) {
					ifYOutside = true;
					break;
				}
			}
		}
	}

	/**
	 * 一技能 制作游戏界面攻击按钮的替身 替换攻击按钮 新的攻击按钮事件： 1. 定义目标列表 2. 添加目标，最多添加三个 3.
	 * 目标列表第一个造成400伤害，其余200伤害 4. 持续5秒，结束后替换攻击按钮️
	 */
	@Override
	public void abilityOne() {
		// TODO Auto-generated method stub
		if (coolDownOne) {
			new AbilityOneDuration().start();// 强化普攻持续时间
			new AbilityOneCD().start();
		}
	}

	/**
	 * 二技能 先点击按钮释放技能 再点击技能范围内任意位置 之后点击的位置会出现 两个大小不同的圆 在大圆内会受到300伤害 在小圆内受到额外200伤害
	 */
	@Override
	public void abilityTwo() {
		// TODO Auto-generated method stub
		if (coolDownTwo) {
			m2 = new MouseMonitorTwo();
			gameFrame.addMouseListener(m2);
			X_AbilityTwo = 0;
			Y_AbilityTwo = 0;
		}

	}

	@Override
	public void abilityThree() {
		// TODO Auto-generated method stub
		if (coolDownThree) {
			m = new MouseMonitor();
			p = new Polygon();
			gameFrame.addMouseListener(m);
			ifAbilityThree = true;
			ifXOutside = false;
			ifYOutside = false;
		}
	}

	@Override
	public void abilityEffect(Graphics g) {
		// TODO Auto-generated method stub
		if (ifAbilityTwo) {
			System.out.println("222");
			g.setColor(Color.RED);
			g.fillOval(X_AbilityTwo - 60, Y_AbilityTwo - 60, 120, 120);
			g.setColor(Color.BLACK);
			g.fillOval(X_AbilityTwo - 30, Y_AbilityTwo - 30, 60, 60);
			abilityTwoAttack();
			X_AbilityTwo = 0;
			Y_AbilityTwo = 0;
			ifAbilityTwo = false;
			new AbilityTwoCD().start();
		}
		if (ifAbilityThree) {
			System.out.println("333");
			g.setColor(Color.RED);
			g.fillPolygon(p);
			abilityThreeMove();
			// 如果出界，技能释放结束
			if (ifXOutside || ifYOutside) {
				ifAbilityThree = false;
				p = new Polygon();
				new AbilityThreeCD().start();
			}
		}
	}

	// 攻击冷却时间
	class AttackCD extends Thread {
		public void run() {
			// 将攻击功能设置为冷却状态
			setAttackCoolDown(false);
			// 线程休眠
			try {
				Thread.sleep(getAttackCoolDownTime());
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 将攻击功能设置为攻击状态
			setAttackCoolDown(true);
			// 线程终止
			this.stop();
		}
	}

	// 一技能冷却时间
	class AbilityOneCD extends Thread {
		public void run() {
			// 将攻击功能设置为冷却状态
			coolDownOne = false;
			// 休眠
			try {
				// one用来储存冷却时间
				int one = coolDownTimeOne;
				while (coolDownTimeOne > 0) {
					Thread.sleep(1000);
					System.out.println("技能一冷却时间： " + coolDownTimeOne / 1000);
					coolDownTimeOne -= 1000;
				}
				coolDownTimeOne = one;
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 将攻击功能解除冷却状态
			coolDownOne = true;
			// 线程终止
			this.stop();
		}
	}

	// 技能一持续时间
	class AbilityOneDuration extends Thread {
		public void run() {
			// 制作攻击按钮的替身
			JButton substitute = gameFrame.attackButton;
			gameFrame.remove(gameFrame.attackButton);

			JButton button = new JButton();
			button.setSize(130, 132);
			button.setLocation(1150, 430);
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// 获取到的事件源就是按钮本身
					attack();
				}
			});
			gameFrame.add(button);
			// 休眠
			try {
				Thread.sleep(5000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			gameFrame.remove(button);
			gameFrame.add(substitute);
			// 线程终止
			this.stop();
		}
	}

	// 二技能冷却时间
	class AbilityTwoCD extends Thread {
		public void run() {
			// 将二技能设置为冷却状态
			coolDownTwo = false;
			// 休眠
			try {
				int two = coolDownTimeTwo;
				while (coolDownTimeTwo > 0) {
					Thread.sleep(1000);
					System.out.println("技能二冷却时间： " + coolDownTimeTwo / 1000);
					coolDownTimeTwo -= 1000;
				}
				coolDownTimeTwo = two;
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 将攻击功能解除冷却状态
			coolDownTwo = true;
			// 线程终止
			this.stop();
		}
	}

	// 技能三冷却时间
	class AbilityThreeCD extends Thread {
		public void run() {
			// 将技能三设置为冷却状态
			coolDownThree = false;
			// 线程休眠
			try {
				// three来表示三技能冷却时间
				int three = coolDownTimeThree;
				while (three > 0) {
					Thread.sleep(1000);
					System.out.println("三技能冷却时间： " + three / 1000);
					three -= 1000;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 将技能三设置为攻击状态
			coolDownThree = true;
			// 线程终止
			this.stop();
		}
	}

	// 技能三控制时间
	class AbilityControlCD extends Thread {
		public void run() {
			abilityThreeTarget.beControlled = true;
			// 线程休眠
			try {
				Thread.sleep(20000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			abilityThreeTarget.beControlled = false;
			this.stop();
		}

	}

	// 鼠标监视器（Second skill）
	private class MouseMonitorTwo extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {// 当鼠标点击时
			System.out.println("pressed");
			int mouseX = e.getX(), mouseY = e.getY(), playerX = 700, playerY = 350;
			double dis = getDis(mouseX, mouseY, playerX, playerY);
			if (dis < 250) {
				X_AbilityTwo = e.getX() - playerX + getX();
				Y_AbilityTwo = e.getY() - playerY + getY();
			}
			ifAbilityTwo = true;
			exit(this);
		}
	}

	// 鼠标监视器
	private class MouseMonitor extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {// 当鼠标点击时
			int mouseX = e.getX(), mouseY = e.getY(), playerX = 700, playerY = 350;
			double dis = getDis(mouseX, mouseY, playerX, playerY);
			// 三角函数
			cos = (mouseX - playerX) / dis;
			sin = -(mouseY - playerY) / dis;
			// 坐标差
			int difX = (int) (60 * sin);
			int difY = (int) (60 * cos);
			p.addPoint(getX() - difX, getY() - difY);
			p.addPoint(getX() + difX, getY() + difY);
			p.addPoint(getX() + difX + (int) (20 * cos), getY() + difY - (int) (20 * sin));
			p.addPoint(getX() - difX + (int) (20 * cos), getY() - difY - (int) (20 * sin));
			ifAbilityThree = true;
			exit(this);
		}
	}

}
