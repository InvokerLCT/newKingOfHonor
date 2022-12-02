package com.sxt;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JButton;

public abstract class Champion extends GameObject {

	// 移动
	public boolean up, down, left, right;
	// 移动图集
	static String[] imgs = new String[8];
	// 第几张图片
	int moveCount = 1;
	//技能图片
	Image abilityOne;
	Image abilityTwo;
	Image abilityThree;
	//英雄头像
	Image classical;
	//技能冷却时间
	int coolDownTimeOne;
	int coolDownTimeTwo;
	int coolDownTimeThree;
	//三个技能是否处于冷却状态
	boolean coolDownOne = true;
	boolean coolDownTwo = true;
	boolean coolDownThree = true;
	//英雄列表
	ArrayList<Champion> championList = new ArrayList<Champion>();
	
	static {
		for (int i = 1; i < 8; i++) {
			imgs[i] = "img/move/" + i + ".png";
		}
	}

	public Champion(GameFrame gameFrame, int x, int y) {
		super(gameFrame);
		setImg("img/stand.png");
		setX(x);
		setY(y);
		setSpd(75);
		setHp(24000);
		setDis(250);
		setAttackCoolDownTime(100);
		setCurrentHp(getHp());
		championList.add(new ChampionDaji(gameFrame));
		championList.add(new ChampionHouyi(gameFrame));
	}
	
	public Champion(GameFrame gameFrame) {
		// TODO Auto-generated constructor stub
		super(gameFrame);
		setImg("img/stand.png");
		setX(700);
		setY(3800);
		setSpd(75);
		setHp(24000);
		setDis(250);
		setAttackCoolDownTime(100);
		setCurrentHp(getHp());
	}

	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_D) {
			right = true;
		}
		if (key == KeyEvent.VK_A) {
			left = true;
		}
		if (key == KeyEvent.VK_W) {
			up = true;
		}
		if (key == KeyEvent.VK_S) {
			down = true;
		}
	}

	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_D) {
			right = false;
		}
		if (key == KeyEvent.VK_A) {
			left = false;
		}
		if (key == KeyEvent.VK_W) {
			up = false;
		}
		if (key == KeyEvent.VK_S) {
			down = false;
		}
	}

	public void move() {
		if (up) {
			setY(getY() - getSpd());
		}
		if (down) {
			setY(getY() + getSpd());
		}
		if (left) {
			setX(getX() - getSpd());
		}
		if (right) {
			setX(getX() + getSpd());
		}
		if (up || down || left || right) {
			setImg(imgs[moveCount]);
			moveCount++;
			if (moveCount == 8) {
				moveCount = 1;
			}
		} else {
			setImg("img/stand.png");
		}
	}
	
	/**
	 * 添加三个技能按钮
	 * */
	public void addButton() {
		JButton button1 = new JButton();
		button1.setSize(100, 100);
		button1.setLocation(1056, 513);
		button1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				abilityOne();
			}
		});
		JButton button2 = new JButton();
		button2.setSize(100, 100);
		button2.setLocation(1090, 370);
		button2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				abilityTwo();
			}
		});
		JButton button3 = new JButton();
		button3.setSize(100, 100);
		button3.setLocation(1220, 300);
		button3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				abilityThree();
			}
		});
		gameFrame.add(button1);
		gameFrame.add(button2);
		gameFrame.add(button3);
	}

	public abstract void abilityOne();
	public abstract void abilityTwo();
	public abstract void abilityThree();
	public abstract void abilityEffect(Graphics g);
	
	@Override
	public void paintSelf(Graphics g) {
		// TODO Auto-generated method stub
		// 生命值为0
		if (getCurrentHp() <= 0) {
			setAlive(false);
			gameFrame.removeList.add(this);

		} else {
			// 添加生命值
			addHp(g, 30, 80, 80, 20, Color.GREEN);
			//绘制技能图片
			g.drawImage(abilityOne, getX() + 360, getY() + 180, null);
			g.drawImage(abilityTwo, getX() + 400, getY() + 40, null);
			g.drawImage(abilityThree, getX() + 520, getY() - 30, null);
			// 绘制图片
			g.drawImage(this.getImg(), getX() - 33, getY() - 50, null);
			// 改变画笔颜色
			//g.setColor(Color.GREEN);
			// 绘制中心圆点
			//g.fillOval(getX(), getY(), 10, 10);
			// 绘制矩形边框
			//g.drawRect(getX() - 23, getY() - 50, 60, 120);
			move();
			abilityEffect(g);
		}
	}

	@Override
	public Rectangle getRec() {
		// TODO Auto-generated method stub
		return new Rectangle(getX() - 30, getY() - 60, 60, 120);
	}

}
