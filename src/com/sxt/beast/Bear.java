package com.sxt.beast;

import com.sxt.GameFrame;

public class Bear extends Beast {

	public Bear(int x, int y, GameFrame gameFrame) {
		super(x, y, gameFrame);
		setImg("img/beast/bear.jpg");
		width = 85;
		height = 112;
		setDis(65);
		// TODO Auto-generated constructor stub
	}

}
