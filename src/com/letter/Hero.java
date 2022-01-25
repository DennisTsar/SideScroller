package com.letter;

import java.awt.geom.*;

public class Hero {
    int x, y, state, count, jumpCount, ground;
    int[][] nums;
    boolean jumping = false, falling = false;
    public Hero(int x, int y, int[][] nums){
        this.x = x;
        this.y = y;
        this.nums = nums;
        this.ground = ground;
        state = 0;
        count = 0;
        jumpCount = 0;
    }
    public boolean isJumping() {
        return jumping;
    }
    public void setJumping(boolean j) {
        jumping = j;
    }
    public boolean isFalling() {
        return falling;
    }
    public void setFalling(boolean f) {
        falling = f;
    }
    public int getY() {
        return y;
    }
    public int getX() {
        return x;
    }
    public int getState(){
        return state;
    }
    public void setState(int c){
        state = c;
    }
    public Rectangle2D getCollider(){
       // if(isFalling() || isJumping())
          //  return new Rectangle2D.Double(x,y,nums[jumpCount+13][2]*2,nums[jumpCount+13][3]*2);
        return new Rectangle2D.Double(x,y,nums[state][2]*2,nums[0][3]*2);
    }
    public Rectangle2D getColliderBelow(){
        return new Rectangle2D.Double(x,y+110,nums[0][2]*2-10,3);//nums[state][3]*2+5);
    }

    public int getJumpCount() {
        return jumpCount;
    }

    public void updateJumping(){
        count++;
        if(jumping)
            y--;
        else
            y++;
        if(count%25==0) {
            jumpCount++;
            if(jumpCount==6) {
                setJumping(false);
                setFalling(true);
            }
            if(jumpCount==12) {
                jumpCount = 6;
            }
        }
    }
    public void zero(){
        jumpCount = 0;
    }
}
