package com.sxt;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

public abstract class Minion extends GameObject {

	// 是否生成下一个小兵
	private boolean nextMinion = true;
	// 是否生成下一波小兵
	private boolean nextLine = true;
	// 生成小兵数量
	private int minionCount = 0;
	// 是否检测到目标
	private boolean ifFindTarget = false;

	public Minion(GameFrame gameFrame) {
		super(gameFrame);
		setHp(800);
		setCurrentHp(getHp());
		setDis(100);
		setAttackCoolDownTime(2000);
		// TODO Auto-generated constructor stub
	}

	/**
	 * (1325, 3750) (4425, 3750) (5050, 3125) (5050, 1125)
	 */

	public abstract void move(ArrayList<GameObject> objList);

	/**
	 * @param x:       下一步的横坐标
	 * @param y:       下一步的纵坐标
	 * @param objList: 小兵列表
	 * @return 下一步位置与其他小兵是否碰撞
	 */
	public boolean hitMinion(int x, int y, ArrayList<GameObject> objList) {
		// 新的区域生成矩形
		Rectangle r = new Rectangle(x - 16, y - 16, 45, 45);
		for (GameObject obj : objList) {
			// 相同类 && 不是自身
			if (obj.getClass() == this.getClass() && obj != this) {
				if (r.intersects(obj.getRec())) {
					return true;
				}
			}
		}
		return false;
	}

	public void findTarget(ArrayList<GameObject> objList) {
		for (GameObject obj : objList) {
			if (recIntersectsCir(obj.getRec(), getX(), getY(), 200)) {
				setTarget(obj);
				setIfFindTarget(true);
			}
		}
		if (objList == gameFrame.blueList) {
			if (recIntersectsCir(gameFrame.player.getRec(), getX(), getY(), 200)) {
				setTarget(gameFrame.player);
				setIfFindTarget(true);
			}
		}
	}

	public void moveToTarget() {
		double dis = getDis(getX(), getY(), getTarget().getX(), getTarget().getY());
		int xSpeed = (int) (getSpd() * (getTarget().getX() - getX()) / dis);
		int ySpeed = (int) (getSpd() * (getTarget().getY() - getY()) / dis);
		if (!hitMinion(getX() + xSpeed, getY(), gameFrame.objList)) {
			setX(getX() + xSpeed);
		}
		if (!hitMinion(getX(), getY() + ySpeed, gameFrame.objList)) {
			setY(getY() + ySpeed);
		}
	}

	public void createMinion(GameFrame gameFrame, ArrayList<GameObject> minionList) {
		if (nextLine) {
			if (nextMinion) {
				// 蓝色方小兵
				if (minionList == this.gameFrame.blueList) {
					MinionBlue mb = new MinionBlue(gameFrame);
					gameFrame.objList.add(mb);
					minionList.add(mb);
				}
				// 红色方小兵
				else {
					MinionRed mr = new MinionRed(gameFrame);
					gameFrame.objList.add(mr);
					minionList.add(mr);
				}
				minionCount++;
				new NextMinion().start();
			}
			if (minionCount == 3) { //When the number of minions is 3, a new thread starts.
				minionCount = 0;
				new NextLine().start();
			}
		}
	}

	// 每个小兵生成时间
	class NextMinion extends Thread {
		public void run() {
			nextMinion = false;
			// 休眠1.5s
			try {
				Thread.sleep(1500);
			} catch (Exception e) {
				e.printStackTrace();
			}
			nextMinion = true;
			// 线程终止
			this.stop();
		}
	}

	// 每波小兵生成时间
	class NextLine extends Thread {
		public void run() {
			nextLine = false;
			// 休眠15s
			try {
				Thread.sleep(15000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			nextLine = true;
			// 线程终止
			this.stop();
		}
	}

	@Override
	public void paintSelf(Graphics g) {
		// TODO Auto-generated method stub
		// 生命值为0
		//When Hp is 0, we assign it to ‘false’ and then add it to the game's delete list.
		//we also need judge the minion is red or blue
		if (getCurrentHp() <= 0) {
			setAlive(false);
			gameFrame.removeList.add(this);
			if (this instanceof MinionBlue) {
				gameFrame.blueList.remove(this);
			} else {
				gameFrame.redList.remove(this);
			}
		} else {
			// Minion Hp
			if (this instanceof MinionBlue) {
				this.addHp(g, 17, 28, 45, 10, Color.GREEN);
			} else {
				this.addHp(g, 17, 28, 45, 10, Color.RED);
			}
			g.drawImage(getImg(), getX() - 16, getY() - 16, null);
			//绘制检测范围圆形
			//g.setColor(Color.RED);
			//g.fillOval(getX(), getY(), 10, 10);
			//g.drawRect(getX() - 16, getY() - 16, 45, 45);
			// 小兵移动
			if (!beControlled) {
				if (this instanceof MinionBlue) {
					move(gameFrame.redList);
				} else {
					move(gameFrame.blueList);
				}
			}
		}
	}

	@Override
	public Rectangle getRec() {
		// TODO Auto-generated method stub
		return new Rectangle(getX() - 16, getY() - 16, 45, 45);
	}

	/**
	 * @return the ifFindTarget
	 */
	public boolean isIfFindTarget() {
		return ifFindTarget;
	}

	/**
	 * @param ifFindTarget the ifFindTarget to set
	 */
	public void setIfFindTarget(boolean ifFindTarget) {
		this.ifFindTarget = ifFindTarget;
	}

}
