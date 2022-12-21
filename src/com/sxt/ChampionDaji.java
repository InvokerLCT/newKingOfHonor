package com.sxt;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;

import com.sxt.beast.Beast;

public class ChampionDaji extends Champion {

	// 技能是否处于释放状态
	boolean ifAbilityOne = false;
	boolean ifAbilityTwo = false;
	// 鼠标监视器
	MouseMonitor m;
	// 一技能多边形
	Polygon p;
	// 一技能三角函数
	double sin;
	double cos;
	// 一技能已经攻击过的目标
	ArrayList<GameObject> attacked;
	// 一技能移动次数
	int step = 0;
	// 技能二目标
	GameObject abilityTwoTarget;
	// 技能二子弹
	Bullet abilityTwoBullet;
	// 三技能子弹列表
	Bullet[] bulletList = { new Bullet(), new Bullet(), new Bullet(), new Bullet(), new Bullet() };

	public ChampionDaji(GameFrame gameFrame) {
		super(gameFrame);
		abilityOne = Toolkit.getDefaultToolkit().getImage("img/Daji/abilityOne.jpg");
		abilityTwo = Toolkit.getDefaultToolkit().getImage("img/Daji/abilityTwo.jpg");
		abilityThree = Toolkit.getDefaultToolkit().getImage("img/Daji/abilityThree.jpg");
		classical = Toolkit.getDefaultToolkit().getImage("img/Daji/Classical.jpg");
		// 三个技能冷却时间
		coolDownTimeOne = 6000;
		coolDownTimeTwo = 8000;
		coolDownTimeThree = 8000;
	}

	public void exit() {
		this.gameFrame.removeMouseListener(m);
	}
	
    //It's essentially a rectangle moving
	public void abilityOneMove() {
		p.translate((int) (50 * cos), -(int) (50 * sin));
		for (GameObject redObj : gameFrame.objList) {
			// 是红色方小兵 && 发生碰撞 && 没在attacked列表里
			if (redObj instanceof MinionRed && p.intersects(redObj.getRec()) && attacked.indexOf(redObj) == -1) {
				// 小兵扣血，添加到attacked里
				redObj.setCurrentHp(redObj.getCurrentHp() - 400);
				attacked.add(redObj);
			}else if (Beast.class.isAssignableFrom(redObj.getClass()) && p.intersects(redObj.getRec()) && attacked.indexOf(redObj) == -1) {
				redObj.setCurrentHp(redObj.getCurrentHp() - 400);
				attacked.add(redObj);
			}
			
		}
	}

	@Override
	public void abilityOne() {
		// TODO Auto-generated method stub
		if (coolDownOne) {
			m = new MouseMonitor();
			p = new Polygon();
			gameFrame.addMouseListener(m);
			attacked = new ArrayList<GameObject>();
		}
	}

	@Override
	public void abilityTwo() {
		// TODO Auto-generated method stub
		if (coolDownTwo) {
			boolean find = false;
			for (GameObject redObj : gameFrame.objList) {
				// 是红色小兵 && 距离小于250 && 存活
				if ((redObj instanceof MinionRed || Beast.class.isAssignableFrom(redObj.getClass())) && recIntersectsCir(redObj.getRec(), getX(), getY(), 250)
						&& redObj.isAlive()) {
					// 添加子弹
					//adding an extra parameter to the bullet picture
					abilityTwoBullet = new Bullet(gameFrame, this, redObj, 250, 60, "img/Daji/abilityTwoBullet.png");
					gameFrame.objList.add(abilityTwoBullet);
					// 给目标赋值
					abilityTwoTarget = redObj;
					// 释放二技能
					ifAbilityTwo = true;
					find = true;
					break;
				}
			}
			if (find) {
				new AbilityTwoCD().start();
				find = false;
			}
		}
	}

	/**
	 * 点击技能三释放技能 先将技能范围内目标储存到targetList里 提前定义五个子弹 技能释放时初始化五个子弹 子弹目标从targetList里随机选择
	 * 如果期间目标死亡，制作一个目标替身，生命值设置为true 子弹与目标或替身碰撞后消失
	 */
	@Override
	public void abilityThree() {
		// TODO Auto-generated method stub
		// 三技能没在冷却状态
		if (coolDownThree) {
			// 创建列表来储存目标
			ArrayList<GameObject> targetList = new ArrayList<GameObject>();
			// 遍历redList
			for (int i = 0; i < gameFrame.objList.size(); i++) {
				GameObject target = gameFrame.objList.get(i);
				// 是红色小兵 && 在技能范围里 && 存活
				if ((target instanceof MinionRed || Beast.class.isAssignableFrom(target.getClass())) && recIntersectsCir(target.getRec(), getX(), getY(), 250)
						&& target.isAlive()) {
					targetList.add(target);
				}
			}
			//找到目标
			if(targetList.size() != 0) {
				//初始化五个子弹，随机攻击列表里的目标
				Random random = new Random();
				int count = 0;
				while(count < 5) {
					int r = random.nextInt(targetList.size());
					if(!targetList.get(r).isAlive()) {
						//目标死亡，制作替身
						GameObject substitute = targetList.get(r);
						substitute.setAlive(true);
						bulletList[count] = new Bullet(gameFrame, this, substitute, 250, 60, "img/Daji/abilityTwoBullet.png");
					}else {
						bulletList[count] = new Bullet(gameFrame, this, targetList.get(r), 250, 60, "img/Daji/abilityTwoBullet.png");
					}
					count++;
				}
				new AbilityThreeBulletCD().start();
				new AbilityThreeCD().start();
			}
		}
	}

	@Override
	public void abilityEffect(Graphics g) {
		// TODO Auto-generated method stub
		if (ifAbilityOne) {
			g.setColor(Color.RED);
			g.fillPolygon(p);
			abilityOneMove();
			step++;
			if (step == 10) {
				step = 0;
				ifAbilityOne = false;
			}
		}
		if (ifAbilityTwo) {
			System.out.println(abilityTwoTarget.beControlled);
			if (abilityTwoBullet.getRec().intersects(abilityTwoTarget.getRec())) {
				new AbilityControllCD().start();
				ifAbilityTwo = false;
			}
		}
	}

	// 技能一冷却时间
	class AbilityOneCD extends Thread {
		public void run() {
			// 将技能一设置为冷却状态
			coolDownOne = false;
			// 线程休眠
			try {
				// one来表示一技能冷却时间
				int one = coolDownTimeOne;
				while (one > 0) {
					Thread.sleep(1000);
					System.out.println("一技能冷却时间： " + one / 1000);
					one -= 1000;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 将技能一设置为攻击状态
			coolDownOne = true;
			// 线程终止
			this.stop();
		}
	}

	// 技能二冷却时间
	class AbilityTwoCD extends Thread {
		public void run() {
			// 将技能二设置为冷却状态
			coolDownTwo = false;
			// 线程休眠
			try {
				// one来表示二技能冷却时间
				int two = coolDownTimeTwo;
				while (two > 0) {
					Thread.sleep(1000);
					System.out.println("二技能冷却时间： " + two / 1000);
					two -= 1000;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 将技能二设置为攻击状态
			coolDownTwo = true;
			// 线程终止
			this.stop();
		}
	}

	// 技能二控制时间
	class AbilityControllCD extends Thread {
		public void run() {
			abilityTwoTarget.beControlled = true;
			// 线程休眠
			try {
				Thread.sleep(20000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			abilityTwoTarget.beControlled = false;
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
	
	//技能三子弹时间间隔
	class AbilityThreeBulletCD extends Thread{
		public void run() {
			try {
				gameFrame.objList.add(bulletList[0]);
				Thread.sleep(200);
				gameFrame.objList.add(bulletList[1]);
				Thread.sleep(200);
				gameFrame.objList.add(bulletList[2]);
				Thread.sleep(200);
				gameFrame.objList.add(bulletList[3]);
				Thread.sleep(200);
				gameFrame.objList.add(bulletList[4]);
			}catch(Exception e) {
				e.printStackTrace();
			}
			this.stop();
		}
	}

	// 鼠标监视器
	//inner class
	private class MouseMonitor extends MouseAdapter {
		@Override
		//Add mouse event
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
			exit();
			new AbilityOneCD().start();
			//When the mouse is clicked, the first skill is launched
			ifAbilityOne = true;
		}
	}
}
