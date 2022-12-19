package com.sxt;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

public class Turret extends GameObject {

	ArrayList<Turret> turretList = new ArrayList<Turret>();
	public Turret turretBlueOne;
	public Turret turretBlueTwo;
	public Turret turretBlueThree;
	public Turret turretBlueBase;
	public Turret turretRedOne;
	public Turret turretRedTwo;
	public Turret turretRedThree;
	public Turret turretRedBase;

	public Turret(GameFrame gameFrame) {
		super(gameFrame);
		setImg("img/turret.png");

		// 初始化八个防御塔
		/*The first four are blue squares, and the last four are red squares, 
		  which need to be added to the corresponding list in GameFrame
		*/
		turretList.add(turretBlueOne = new TurretBlue(1860, 3790, gameFrame));
		turretList.add(turretBlueTwo = new TurretBlue(2650, 3820, gameFrame));
		turretList.add(turretBlueThree = new TurretBlue(3995, 3830, gameFrame));
		turretList.add(turretBlueBase = new TurretBlue(1130, 3650, gameFrame));
		turretList.add(turretRedOne = new TurretRed(5100, 3030, gameFrame));
		turretList.add(turretRedTwo = new TurretRed(5120, 2100, gameFrame));
		turretList.add(turretRedThree = new TurretRed(5060, 1570, gameFrame));
		turretList.add(turretRedBase = new TurretRed(4850, 1100, gameFrame));

		// TODO Auto-generated constructor stub
	}

	public Turret(int x, int y, GameFrame gameFrame) {
		super(x, y, gameFrame);
		setImg("img/turret.png");
		setHp(6000);
		setCurrentHp(getHp());
		setAttackCoolDownTime(1000);
		setDis(300);
	}
	
	public void addTurret() {
		/**
		 * 添加到objList里会被绘制出来，添加到redList和blueList才会被攻击 前一个防御塔爆炸后再把后一个添加到列表里
		 */
		if (!gameFrame.turret.turretBlueOne.isAlive() && gameFrame.turret.turretBlueTwo.isAlive()
				&& gameFrame.blueList.indexOf(gameFrame.turret.turretBlueTwo) == -1) {
			gameFrame.blueList.add(gameFrame.turret.turretBlueTwo);
		}
		if (!gameFrame.turret.turretBlueTwo.isAlive() && gameFrame.turret.turretBlueThree.isAlive()
				&& gameFrame.blueList.indexOf(gameFrame.turret.turretBlueThree) == -1) {
			gameFrame.blueList.add(gameFrame.turret.turretBlueThree);
		}
		if (!gameFrame.turret.turretBlueThree.isAlive() && gameFrame.turret.turretBlueBase.isAlive()
				&& gameFrame.blueList.indexOf(gameFrame.turret.turretBlueBase) == -1) {
			gameFrame.blueList.add(gameFrame.turret.turretBlueBase);
		}
		if (!gameFrame.turret.turretBlueBase.isAlive()) {
			//游戏失败
			gameFrame.state = 3;
		}

		if (!gameFrame.turret.turretRedOne.isAlive() && gameFrame.turret.turretRedTwo.isAlive()
				&& gameFrame.redList.indexOf(gameFrame.turret.turretRedTwo) == -1) {
			gameFrame.redList.add(gameFrame.turret.turretRedTwo);
		}
		if (!gameFrame.turret.turretRedTwo.isAlive() && gameFrame.turret.turretRedThree.isAlive()
				&& gameFrame.redList.indexOf(gameFrame.turret.turretRedThree) == -1) {
			gameFrame.redList.add(gameFrame.turret.turretRedThree);
		}
		if (!gameFrame.turret.turretRedThree.isAlive() && gameFrame.turret.turretRedBase.isAlive()
				&& gameFrame.redList.indexOf(gameFrame.turret.turretRedBase) == -1) {
			gameFrame.redList.add(gameFrame.turret.turretRedBase);
		}
		if (!gameFrame.turret.turretRedBase.isAlive()) {
			//游戏胜利
			gameFrame.state = 2;
		}
	}
	

	@Override
	public void paintSelf(Graphics g) {
		// 生命值为0
		if (getCurrentHp() <= 0) {
			setAlive(false);
			gameFrame.removeList.add(this);
			if (this instanceof TurretBlue) {
				gameFrame.blueList.remove(this);
			} else {
				gameFrame.redList.remove(this);
			}
		} else {
			// Turret Hp
			if (this instanceof TurretBlue) {
				this.addHp(g, 50, 130, 100, 20, Color.GREEN);
				attack(gameFrame.redList);
			} else {
				this.addHp(g, 50, 130, 100, 20, Color.RED);
				attack(gameFrame.blueList);
			}
			g.drawImage(getImg(), getX() - 50, getY() - 100, null);
			g.drawOval(getX() - 300, getY() - 300, 600, 600);
		}
	}

	@Override
	public Rectangle getRec() {
		// TODO Auto-generated method stub
		return new Rectangle(getX() - 50, getY() - 100, 100, 180);
	}

}
