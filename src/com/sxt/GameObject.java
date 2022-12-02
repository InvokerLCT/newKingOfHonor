package com.sxt;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.ArrayList;

import com.sxt.beast.Beast;

public abstract class GameObject {

	// 坐标
	private int x;
	private int y;
	// 图片
	private Image img;
	// 游戏界面
	public GameFrame gameFrame;
	// 速度
	private int spd;
	// 初始生命值
	private int hp;
	// 当前生命值
	private int currentHp;
	// 攻击目标
	private GameObject target;
	// 是否有目标
	private boolean hasTarget = false;
	// 攻击距离
	private int dis;
	// 攻击时间间隔
	private int attackCoolDownTime;
	// 攻击是否冷却
	private boolean attackCoolDown = true;
	// 是否存活
	private boolean alive = true;
	// 是否被控制
	boolean beControlled = false;

	public GameObject(GameFrame gameFrame) {
		this.gameFrame = gameFrame;
	}

	public GameObject(int x, int y, GameFrame gameFrame) {
		this.x = x;
		this.y = y;
		this.gameFrame = gameFrame;
	}

	public GameObject() {
		// TODO Auto-generated constructor stub
	}

	public void addHp(Graphics g, int difX, int difY, int width, int height, Color color) {
		// 绘制外部轮廓
		g.setColor(Color.black);
		g.drawRect(getX() - difX, getY() - difY, width, height);
		// 填充矩形
		g.setColor(color);
		g.fillRect(getX() - difX, getY() - difY, (int) (width * getCurrentHp() / getHp()), height);
	}

	public double getDis(int x1, int y1, int x2, int y2) {
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}

	// 矩形矩形碰撞检测
	public boolean recIntersectsRec(Rectangle r1, Rectangle r2) {
		return r1.intersects(r2);
	}

	public boolean recIntersectsCir(Rectangle rec, int x, int y, int r) {
		/** 矩形于圆相交： 圆心到至少一个矩形定点的距离小于r */
		if ((getDis(x, y, rec.x, rec.y) < r) || (getDis(x, y, rec.x, rec.y + rec.height) < r)
				|| (getDis(x, y, rec.x + rec.width, rec.y) < r)
				|| (getDis(x, y, rec.x + rec.width, rec.y + rec.height) < r)) {
			return true;
		}
		return false;
	}

	// 攻击方法
	public void attack(ArrayList<GameObject> gameObjList) {
		if (hasTarget) {
			// 目标离开范围后寻找新的目标
			if (!recIntersectsCir(target.getRec(), getX(), getY(), getDis())) {
				setHasTarget(false);
			}
			// 目标死亡，寻找新目标
			else if (!target.isAlive()) {
				setHasTarget(false);
			} else if (isAttackCoolDown() && isAlive()) {
				Bullet bullet = null;
				// 防御塔攻击
				if (Turret.class.isAssignableFrom(getClass())) {
					bullet = new Bullet(gameFrame, this, getTarget(), 500, 50);
				}
				// 小兵攻击
				else if (Minion.class.isAssignableFrom(getClass())) {
					bullet = new Bullet(gameFrame, this, getTarget(), 50, 30);
				}
				// 玩家攻击
				else if (this instanceof Champion) {
					bullet = new Bullet(gameFrame, this, getTarget(), 500, 50, "img/bullet.gif");
				}
				gameFrame.objList.add(bullet);
				// 线程开始
				new AttackCD().start();
			}
		} else {
			// 遍历列表
			for (GameObject obj : gameObjList) {
				// 判断攻击范围（圆形）与敌方（矩形）是否相交
				if (recIntersectsCir(obj.getRec(), getX(), getY(), getDis())) {
					// 找到目标
					setTarget(obj);
					setHasTarget(true);
					// 跳出循环
					break;
				}
			}
			// 玩家是否在攻击范围内
			if (!hasTarget && gameObjList == gameFrame.blueList) {
				if (recIntersectsCir(gameFrame.player.getRec(), getX(), getY(), getDis())) {
					// 找到目标
					setTarget(gameFrame.player);
					setHasTarget(true);
				}
			} else {
				for (GameObject obj : gameFrame.beast.beastList) {
					// 判断攻击范围（圆形）与敌方（矩形）是否相交
					if (recIntersectsCir(obj.getRec(), getX(), getY(), getDis())) {
						// 找到目标
						setTarget(obj);
						setHasTarget(true);
						// 跳出循环
						break;
					}
				}
			}
		}
	}

	class AttackCD extends Thread {
		public void run() {
			// 将攻击功能设置为冷却状态
			setAttackCoolDown(false);
			// 线程休眠
			try {
				Thread.sleep(attackCoolDownTime);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 将攻击功能设置为攻击状态
			setAttackCoolDown(true);
			// 线程终止
			this.stop();
		}
	}

	// 绘制元素
	public abstract void paintSelf(Graphics g);

	// 返回矩形
	public abstract Rectangle getRec();

	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * @return the img
	 */
	public Image getImg() {
		return img;
	}

	/**
	 * @param img the img to set
	 */
	public void setImg(String img) {
		this.img = Toolkit.getDefaultToolkit().getImage(img);
	}

	/**
	 * @return the spd
	 */
	public int getSpd() {
		return spd;
	}

	/**
	 * @param spd the spd to set
	 */
	public void setSpd(int spd) {
		this.spd = spd;
	}

	/**
	 * @return the hp
	 */
	public int getHp() {
		return hp;
	}

	/**
	 * @param hp the hp to set
	 */
	public void setHp(int hp) {
		this.hp = hp;
	}

	/**
	 * @return the currentHp
	 */
	public int getCurrentHp() {
		return currentHp;
	}

	/**
	 * @param currentHp the currentHp to set
	 */
	public void setCurrentHp(int currentHp) {
		if (currentHp < getHp()) {
			if (Beast.class.isAssignableFrom(getClass())) {
				System.out.println("yes");
				setTarget(gameFrame.player);
				setHasTarget(true);
			}
		}
		this.currentHp = currentHp;
	}

	/**
	 * @return the target
	 */
	public GameObject getTarget() {
		return target;
	}

	/**
	 * @param target the target to set
	 */
	public void setTarget(GameObject target) {
		this.target = target;
	}

	/**
	 * @return the hasTarget
	 */
	public boolean isHasTarget() {
		return hasTarget;
	}

	/**
	 * @param hasTarget the hasTarget to set
	 */
	public void setHasTarget(boolean hasTarget) {
		this.hasTarget = hasTarget;
	}

	/**
	 * @return the dis
	 */
	public int getDis() {
		return dis;
	}

	/**
	 * @param dis the dis to set
	 */
	public void setDis(int dis) {
		this.dis = dis;
	}

	/**
	 * @return the attackCoolDownTime
	 */
	public int getAttackCoolDownTime() {
		return attackCoolDownTime;
	}

	/**
	 * @param attackCoolDownTime the attackCoolDownTime to set
	 */
	public void setAttackCoolDownTime(int attackCoolDownTime) {
		this.attackCoolDownTime = attackCoolDownTime;
	}

	/**
	 * @return the attackCoolDown
	 */
	public boolean isAttackCoolDown() {
		return attackCoolDown;
	}

	/**
	 * @param attackCoolDown the attackCoolDown to set
	 */
	public void setAttackCoolDown(boolean attackCoolDown) {
		this.attackCoolDown = attackCoolDown;
	}

	/**
	 * @return the alive
	 */
	public boolean isAlive() {
		return alive;
	}

	/**
	 * @param alive the alive to set
	 */
	public void setAlive(boolean alive) {
		this.alive = alive;
	}
}