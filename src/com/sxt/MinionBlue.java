package com.sxt;

import java.util.ArrayList;

public class MinionBlue extends Minion {

	public MinionBlue(GameFrame gameFrame) {
		super(gameFrame);
		setImg("img/minion/blue.jpg");
		setX(1325);
		setY(3750);
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
			if (getX() < 4425) {
				setSpd(25);
				if (!hitMinion(getX() + getSpd(), getY(), gameFrame.blueList)) {
					setX(getX() + getSpd());

				}
			} else if (getX() < 5100 && getX() >= 4425) {
				setSpd(20);
				if (!hitMinion(getX() + getSpd(), getY(), gameFrame.blueList)) {
					setX(getX() + getSpd());
				}
				if (!hitMinion(getX(), getY() - getSpd(), gameFrame.blueList)) {
					setY(getY() - getSpd());
				}
			} else if (getX() >= 4900) {
				setSpd(18);
				if (!hitMinion(getX(), getY() - getSpd(), gameFrame.blueList)) {
					setY(getY() - getSpd());
				}
			}
		}
	}
}
