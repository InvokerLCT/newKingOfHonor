package com.sxt;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import com.sxt.beast.Beast;

public class Bullet extends GameObject {

	// 发射子弹的游戏元素
	GameObject attacker;
	// 目标
	GameObject target;
	// 攻击力
	int ad;

	public Bullet(GameFrame gameFrame, GameObject attacker, GameObject target, int ad, int spd) {
		super(attacker.getX(), attacker.getY(), gameFrame);
		this.attacker = attacker;
		this.target = target;
		setAd(ad);
		setSpd(spd);
		// TODO Auto-generated constructor stub
	}

	public Bullet(GameFrame gameFrame, GameObject attacker, GameObject target, int ad, int spd, String img) {
		super(attacker.getX(), attacker.getY(), gameFrame);
		this.attacker = attacker;
		this.target = target;
		setImg(img);
		setAd(ad);
		setSpd(spd);
		// TODO Auto-generated constructor stub
	}
	public Bullet() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void move() {
		// 子弹与目标碰撞，子弹消失，目标减血
		if (recIntersectsRec(getRec(), target.getRec())) {
			if (Beast.class.isAssignableFrom(target.getClass())) {
				target.setTarget(gameFrame.player);
				target.setHasTarget(true);
			}

			target.setCurrentHp(target.getCurrentHp() - getAd());
			gameFrame.removeList.add(this);
		}
		//Orthogonal decomposition of trigonometric functions is used
		double dis = getDis(getX(), getY(), target.getX(), target.getY());
		int xSpeed = (int) (getSpd() * (target.getX() - getX()) / dis);
		int ySpeed = (int) (getSpd() * (target.getY() - getY()) / dis);
		setX(getX() + xSpeed);
		setY(getY() + ySpeed);
	}

	@Override
	public void paintSelf(Graphics g) {
		// TODO Auto-generated method stub
		g.drawImage(getImg(), getX() - 16, getY() - 16, null);
		if (this.getImg() == null) {
			g.setColor(Color.BLACK);
			g.fillOval(getX() - 5, getY() - 5, 10, 10);
			g.drawRect(getX() - 5, getY() - 5, 10, 10);
		}
		move();
	}

	@Override
	public Rectangle getRec() {
		// TODO Auto-generated method stub
		return new Rectangle(getX() - 5, getY() - 5, 10, 10);
	}

	/**
	 * @return the ad
	 */
	public int getAd() {
		return ad;
	}

	/**
	 * @param ad the ad to set
	 */
	public void setAd(int ad) {
		this.ad = ad;
	}

}
