package org.example;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Main
{
    // key = dice1 + dice2
    // val - count of
    private Map<Integer, Integer> map = new HashMap();

    private BufferedImage image;
    private Graphics2D graphics2D;

    private static final int IMG_W = 600,
                             IMG_H = 400;

    // coordinate center
    private static final Point O = new Point(20, IMG_H - 20);

    private static final int X_STEP = (IMG_W - 40) / 14;
    private static final int Y_STEP = (IMG_H - 40) / 10;

    private static final Color BACKGROUND_COLOR = new Color(245, 239, 241);
    private static final Color BAR_COLOR = new Color(56, 126, 150);


    public Main()
    {
        image = new BufferedImage(IMG_W, IMG_H, BufferedImage.TYPE_INT_RGB);
        graphics2D = image.createGraphics();

        graphics2D.setColor(BACKGROUND_COLOR);
        graphics2D.fillRect(0, 0, IMG_W, IMG_H);

        // draw center
        graphics2D.setColor(Color.BLACK);
        graphics2D.fillOval(O.x - 2, O.y - 2, 5, 5);

        // draw axis Y
        graphics2D.drawLine(O.x, O.y, O.x, IMG_H - IMG_H + 15);
        // draw axis X
        graphics2D.drawLine(O.x, O.y, IMG_W - 20, O.y);

        // draw X scale;
        for(int i = 1; i <= 14; i++)
        {
            graphics2D.drawLine(O.x + X_STEP*i, O.y - 2, O.x + X_STEP*i, O.y + 2);
            if(i >= 10)
            {
                graphics2D.drawString(String.valueOf(i), O.x + X_STEP*i - 6, O.y + 2 + 12);
            } else {
                graphics2D.drawString(String.valueOf(i), O.x + X_STEP*i - 3, O.y + 2 + 12);
            }
        }

        // draw Y scale
        for(int i = 1; i <= 10; i++)
        {
            graphics2D.drawLine(O.x - 2, O.y - Y_STEP*i, O.x + 2, O.y - Y_STEP*i);
            if(i >= 10)
            {
                graphics2D.drawString(String.valueOf(i), O.x - 2 - 16, O.y - Y_STEP*i + 5);
            } else {
                graphics2D.drawString(String.valueOf(i), O.x - 2 - 12, O.y - Y_STEP*i + 5);
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException
    {
        Main mainObj = new Main();

        int repeatCount = 2000;

        for(int i = 0; i < 3; i++)
        {
            mainObj.throwDice(repeatCount);
            mainObj.drawHistogram(repeatCount);
            mainObj.saveImage();

            repeatCount += 1000;
            Thread.sleep(1_000);
        }
    }

    // Monte Carlo method
    private void throwDice(int repeatCount)
    {
        map.clear();
        System.out.println("Number of throws : " + repeatCount);

        for(int i = 0; i < repeatCount; i++)
        {
            int dice1 = (int) (Math.random() * 6) + 1,
                dice2 = (int) (Math.random() * 6) + 1;

            map.put( (dice1 + dice2), map.getOrDefault(dice1+dice2, 0) + 1);
        }

        // count of probability
        map.forEach( (k, v) -> {
            System.out.println("For sum : " + k + " probability is : " + ((double)v/repeatCount));
        });
    }

    private void drawHistogram(int repeatCount)
    {
        // key - X axis
        // value - Y axis
        for( Map.Entry entry : map.entrySet() )
        {
            int key = (Integer) entry.getKey();
            double value = (double) ((int)entry.getValue()) / repeatCount;

            int x = X_STEP * key + O.x;
            double y = O.y - (Y_STEP * value)*10;

            int h = (int) Math.sqrt((O.y - y)*(O.y - y)) + 1;

            graphics2D.setColor(BAR_COLOR);
            graphics2D.fillRect(x, (int) y, X_STEP, h);
        }
    }

    private void saveImage() throws IOException
    {
        StringBuffer fileName = new StringBuffer("gisto_");

        LocalDateTime date = LocalDateTime.now();
        fileName.append(date.getHour()).append("_").append(date.getMinute()).append("_").append(date.getSecond()).append(".jpg");

        ImageIO.write(image, "jpg", new File(fileName.toString()));
    }
}