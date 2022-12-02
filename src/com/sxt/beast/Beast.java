package com.sxt.beast;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

import com.sxt.*;

public class Beast extends GameObject {

	public ArrayList<Beast> beastList = new ArrayList<Beast>();
	int width;
	int height;

	// 初始坐标
	int initialX;
	int initialY;

	// 有没有仇恨值
	public boolean isAggressive = false;

	// 复活的元素
	Beast beast = null;

	public Beast(GameFrame gameFrame) {
		super(gameFrame);
		beastList.add(new RedBuff(3045, 3170, gameFrame));
		beastList.add(new Bear(2800, 2855, gameFrame));
		beastList.add(new Bird(3570, 3380, gameFrame));
		beastList.add(new Xiyi(4585, 2365, gameFrame));
		beastList.add(new BlueBuff(4025, 2295, gameFrame));
		beastList.add(new Wolf(4235, 1945, gameFrame));
	}

	public Beast(int x, int y, GameFrame gameFrame) {
		super(x, y, gameFrame);
		setHp(1000);
		setCurrentHp(getHp());
		setSpd(10);
		setAttackCoolDownTime(2000);
		initialX = getX();
		initialY = getY();
		beast = this;
	}

	public void moveToTarget() {
		double dis = getDis(getX(), getY(), getTarget().getX(), getTarget().getY());
		if (dis > 500) {
			isAggressive = false;
			setHasTarget(false);
		} else {
			int xSpeed = (int) (getSpd() * (getTarget().getX() - getX()) / dis);
			int ySpeed = (int) (getSpd() * (getTarget().getY() - getY()) / dis);
			setX(getX() + xSpeed);
			setY(getY() + ySpeed);
		}
	}

	public void moveToInitialLocation() {
		double dis = getDis(getX(), getY(), initialX, initialY);
		if (dis < getSpd()) {
			setX(initialX);
			setY(initialY);
			isAggressive = true;
		} else {
			int xSpeed = (int) (getSpd() * (initialX - getX()) / dis);
			int ySpeed = (int) (getSpd() * (initialY - getY()) / dis);
			setX(getX() + xSpeed);
			setY(getY() + ySpeed);
		}
	}

	/**
	判断有没有目标及野怪的仇恨值（又没有攻击性）
		true，判断是否到攻击范围
			false，向目标移动,若中途离开出生点距离大于300或与目标距离大于400，则不再有攻击性。
			true，发射子弹，线程开始
		false，没有目标，回到出生地，自动回血，再次有攻击性
	*/
	public void move() {
		if (isHasTarget() && isAggressive) {
			if (!recIntersectsCir(getTarget().getRec(), getX(), getY(), getDis())) {
				moveToTarget();
			} else if (isAttackCoolDown() && isAlive()) {
				Bullet bullet = new Bullet(gameFrame, this, getTarget(), 500, 50, "img/bullet.gif");
				gameFrame.objList.add(bullet);
				new AttackCD().start();
			}
		} else {
			moveToInitialLocation();
			if (getCurrentHp() < getHp()) {
				setCurrentHp(getCurrentHp() + 100);
			}
		}

	}

	@Override
	public void paintSelf(Graphics g) {
		// TODO Auto-generated method stub// 生命值为0
		if (getCurrentHp() <= 0) {
			System.out.println("beast die");
			setAlive(false);
			gameFrame.removeList.add(this);
			gameFrame.beast.beastList.remove(this);
			new ReviveCD().start();
		} else {
			// 添加生命值
			addHp(g, width / 2, 80, width, 20, Color.GREEN);
			g.drawImage(getImg(), getX() - width / 2, getY() - height / 2, null);
			g.setColor(Color.RED);
			g.fillOval(getX(), getY(), 10, 10);
			g.drawOval(getX() - getDis(), getY() - getDis(), 2 * getDis(), 2 * getDis());
			move();
		}
	}

	@Override
	public Rectangle getRec() {
		// TODO Auto-generated method stub
		return new Rectangle(getX() - width / 2, getY() - height / 2, width, height);
	}

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

	class ReviveCD extends Thread {
		public void run() {
			// 线程休眠
			try {
				Thread.sleep(5000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Beast reviveBeast;
			if (beast instanceof RedBuff) {
				reviveBeast = new RedBuff(3045, 3170, gameFrame);
			} else if (beast instanceof Bear) {
				reviveBeast = new Bear(2800, 2855, gameFrame);
			} else if (beast instanceof Bird) {
				reviveBeast = new Bird(3570, 3380, gameFrame);
			} else if (beast instanceof Xiyi) {
				reviveBeast = new Xiyi(4585, 2365, gameFrame);
			} else if (beast instanceof BlueBuff) {
				reviveBeast = new BlueBuff(4025, 2295, gameFrame);
			} else {
				reviveBeast = new Wolf(4235, 1945, gameFrame);
			}
			gameFrame.objList.add(reviveBeast);
			gameFrame.beast.beastList.add(reviveBeast);
		}
	}

}
