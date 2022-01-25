package com.letter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.awt.event.*;
import java.util.*;


public class Main {

    public static void main(String[] args) {
        new SideScroller();
    }
}
class SideScroller extends JPanel implements KeyListener, Runnable{
    JFrame frame;
    BufferedImage aladdinSheet, smallCity, bigCity, clouds, sky, blockImage;
    BufferedImage[] aladdin = new BufferedImage[13];
    BufferedImage[] aladdinJumping = new BufferedImage[12];
    int count = 0, sCnt = 0, bCnt = 0, cCnt = 0;
    Thread timer;
    boolean right = false;
    Hero hero;
    Block block, floor;
    HashMap<Integer,ArrayList<Block>> blocks = new HashMap<>();
    int col;

    public SideScroller(){
        frame = new JFrame("Aladdin");
        frame.add(this);

        try{
            aladdinSheet = ImageIO.read(new File("src/Aladdin.png"));
            smallCity = ImageIO.read(new File("src/smallCity.png"));
            bigCity = ImageIO.read(new File("src/bigCity.png"));
            clouds = ImageIO.read(new File("src/clouds.png"));
            sky = ImageIO.read(new File("src/sunset.png"));
            blockImage = resize(ImageIO.read(new File("src/box.png")),50,50);

            File file = new File("src/map");
            BufferedReader input = new BufferedReader(new FileReader(file));
            String s;
            int row = 0;
            while((s=input.readLine())!=null){
                String[] split = s.split("");
                for(int c = 0; c<split.length; c++) {
                    if (split[c].equals("B")) {
                        if (!blocks.containsKey(c))
                            blocks.put(c, new ArrayList<Block>());
                        blocks.get(c).add(new Block(50 * c, 50 * row, 50, 50));
                    }
                    if(split[c].equals("h"))
                        col = c;
                }
                row++;
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }

        int[][] nums = new int[][]{//x,y,width,height
                {337,3,23,55},//standing
                {4,64,31,53},//first running = index 1
                {34,64,31,51},
                {62,64,31,51},
                {92,64,31,51},
                {127,64,37,51},
                {166,64,31,51},
                {205,64,31,51},
                {233,64,30,51},
                {263,61,30,56},
                {292,61,34,56},
                {325,60,41,56},
                {367,60,36,56},//last running = index 12
                {4,294,31,59},//first jumping = index 13
                {35,300,29,58},
                {62,301,38,56},
                {100,301,36,56},
                {140,303,41,50},
                {183,304,49,47},
                {230,303,42,50},
                {278,302,37,54},
                {321,303,33,56},
                {4,363,35,64},
                {42,365,36,63},
                {168,361,25,55}//last jumping = inde 24
        };

        for(int i = 0; i<13; i++)
            aladdin[i] = resize(aladdinSheet.getSubimage(nums[i][0],nums[i][1],nums[i][2],nums[i][3]),
                    nums[i][2]*2,nums[i][3]*2);
        for(int i = 13; i<25; i++)
            aladdinJumping[i-13] = resize(aladdinSheet.getSubimage(nums[i][0],nums[i][1],nums[i][2],nums[i][3]),
                    nums[i][2]*2,nums[i][3]*2);

        hero = new Hero(50*col,450,nums);
        floor = new Block(0,565,1000,50);

        frame.addKeyListener(this);
        frame.setSize(900,600);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

        timer = new Thread(this);
        timer.start();
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(sky,0,0,this);

        g.drawImage(clouds,cCnt+960,0,this);
        g.drawImage(clouds,cCnt-960,0,this);

        g.drawImage(bigCity,bCnt+960,100,this);
        g.drawImage(bigCity,bCnt-960,100,this);

        g.drawImage(smallCity,sCnt+960,-40,this);
        g.drawImage(smallCity,sCnt-960,-40,this);

        if(hero.isJumping() || hero.isFalling())
            g.drawImage(aladdinJumping[hero.getJumpCount()],hero.getX(),hero.getY(),this);
        else
            g.drawImage(aladdin[hero.getState()],hero.getX(),hero.getY(),this);

        for(int j = col-5; j<col+17; j++)
            if(blocks.containsKey(j))
                for(Block i : blocks.get(j))
                    g.drawImage(blockImage,i.getX(),i.getY(),this);
    }
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode()==39)
            right = true;
        if(e.getKeyCode()==32 && !hero.isJumping() && !hero.isFalling())
            hero.setJumping(true);
    }
    public void keyTyped(KeyEvent e) {

    }
    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode()==39) {
            right = false;
            hero.setState(0);
        }
    }
    public void run() {
        while(true) {
            boolean collidesBelow = collidesBelow();
            if(hero.isJumping())
                hero.updateJumping();
            else if(hero.isFalling()) {
                if(!collidesBelow && !hero.getColliderBelow().intersects(floor.getCollider()))
                    hero.updateJumping();
                else {
                    hero.setFalling(false);
                    hero.zero();
                }
            }
            else if(!hero.getColliderBelow().intersects(floor.getCollider()) && !collidesBelow)
                hero.setFalling(true);

            if(right && !collides()) {
                count++;
                if(count%25==0)
                    hero.setState((hero.getState() + 1) % 12 + 1);

                if(count%50==0)
                    col++;

                sCnt--;
                if(sCnt<-1920)
                    sCnt+=1920;

                if(count%2==0)
                    bCnt+= (bCnt < -1920) ? 1919 : -1;

                if(count%10==0)
                    cCnt+= (cCnt < -1920) ? 1919 : -1;

                for(int j : blocks.keySet())
                    for(Block i : blocks.get(j))
                        i.updateX(-1);
            }
            try{
                timer.sleep(3);
            }
            catch(InterruptedException e){
                e.printStackTrace();
            }
            repaint();
        }
    }

    public BufferedImage resize(BufferedImage image, int width, int height){
        Image temp = image.getScaledInstance(width,height,Image.SCALE_SMOOTH);
        BufferedImage result = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = result.createGraphics();
        g2.drawImage(temp,0,0,null);
        g2.dispose();
        return result;
    }

    public boolean collides(){
        for(int j = col-1; j<col+2; j++)
            if(blocks.containsKey(j))
                for(Block i : blocks.get(j))
                    if(hero.getCollider().intersects(i.getCollider()))
                        return true;
        return false;
    }
    public boolean collidesBelow(){
        for(int j = col-1; j<col+2; j++)
            if(blocks.containsKey(j))
                for(Block i : blocks.get(j))
                    if(hero.getColliderBelow().intersects(i.getCollider()))
                        return true;
        return false;
    }
}
