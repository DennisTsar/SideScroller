package com.letter;

import java.awt.geom.*;

public class Block {
    int x, y, width, height;
    int[][] nums;
    boolean jumping = false, falling = false;
    public Block(int x, int y, int width, int height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void updateX(int num){
        x+=num;
    }

    public int getY() {
        return y;
    }
    public int getX() {
        return x;
    }
    public Rectangle2D getCollider(){
        return new Rectangle2D.Double(x,y,width,height);
    }

}
