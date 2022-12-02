package com.sxt.beast;

import com.sxt.GameFrame;

public class Wolf extends Beast {

	public Wolf(int x, int y, GameFrame gameFrame) {
		super(x, y, gameFrame);
		setImg("img/beast/wolf.jpg");
		width = 145;
		height = 140;

		setDis(65);
		// TODO Auto-generated constructor stub
	}

}
