package com.sxt;

import javax.swing.*;
import java.awt.*;

public class Background extends GameObject {

    public Background(GameFrame gameFrame) {
		super(gameFrame);
		// TODO Auto-generated constructor stub
	}

	Image bg = Toolkit.getDefaultToolkit().getImage("img/Map.jpg");

    public void paintSelf(Graphics g){
        g.drawImage(bg,0,0,null);

    }

	@Override
	public Rectangle getRec() {
		// TODO Auto-generated method stub
		return null;
	}
}