package com.sxt;

import java.util.ArrayList;

public class MinionRed extends Minion {

	public MinionRed(GameFrame gameFrame) {
		super(gameFrame);
		setImg("img/minion/red.jpg");
		setX(5050);
		setY(1125);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void move(ArrayList<GameObject> objList) {
		// TODO Auto-generated method stub
		if (isIfFindTarget()) {
			// 离开检测范围
			if (!recIntersectsCir(getTarget().getRec(), getX(), getY(), 200)) {
				setIfFindTarget(false);
			} else {
				if (!isHasTarget()) {
					moveToTarget();
				}
				attack(objList);
			}
		} else {
			findTarget(objList);
			// 原路线移动
			if (getY() < 3125) {
				setSpd(18);
				if (!hitMinion(getX(), getY() + getSpd(), gameFrame.redList)) {
					setY(getY() + getSpd());
				}
			} else if (getY() < 3750 && getY() >= 3125) {
				setSpd(20);
				if (!hitMinion(getX(), getY() + getSpd(), gameFrame.redList)) {
					setY(getY() + getSpd());
				}
				if (!hitMinion(getX() - getSpd(), getY(), gameFrame.redList)) {
					setX(getX() - getSpd());
				}
			} else if (getY() >= 3750) {
				setSpd(25);
				if (!hitMinion(getX() - getSpd(), getY(), gameFrame.redList)) {
					setX(getX() - getSpd());
				}
			}
		}
	}

}
