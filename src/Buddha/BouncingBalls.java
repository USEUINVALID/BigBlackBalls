package Buddha;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;
import java.util.*;

public class BouncingBalls extends JPanel {

    private static final int WIDTH = 600;

    private static final int HEIGHT = 400;

    public static final int BALL_SIZE = 50;

    public static final Random RANDOM = new Random();

    public static final List<Ball> BALLS = new ArrayList<>();

    public BouncingBalls() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                addBall(e.getX(), e.getY());
            }
        });
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        for (Ball ball : BALLS) {
            ball.move();
            ball.draw(graphics);
        }

        repaint();

        try {
            Thread.sleep(10);
        } catch (Exception ignored) {
        }
    }

    private void addBall(int x, int y) {
        Ball ball = new Ball(
                x,
                y,
                BALL_SIZE,
                new Color(0,0,0),
                this
        );

        BALLS.add(ball);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Bouncing Balls");
        BouncingBalls bouncingBalls = new BouncingBalls();

        frame.add(bouncingBalls);
        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static class Ball {
        private final Color color;
        private final Component container;

        private final int size;

        private int x, y;
        private int xSpeed, ySpeed;

        private boolean checkCollision(Ball ball1, Ball ball2) {
            int deltaX = ball1.x - ball2.x;
            int deltaY = ball1.y - ball2.y;
            int distance = (int) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

            return distance < BouncingBalls.BALL_SIZE; // Шарики сталкиваются, если расстояние меньше суммы их радиусов
        }

        public Ball(int x, int y, int size, Color color, Component container) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.color = color;
            this.container = container;

            this.xSpeed = RANDOM.nextInt(1, 5);
            this.ySpeed = RANDOM.nextInt(1, 5);
        }

        public void draw(Graphics g) {
            g.setColor(color);
            g.fillOval(x, y, size, size);
        }

        private void move() {
            if (x + xSpeed < 0 || x + xSpeed > container.getWidth() - size) {
                xSpeed = -xSpeed;
            }

            if (y + ySpeed < 0 || y + ySpeed > container.getHeight() - size) {
                ySpeed = -ySpeed;
            }

            x += xSpeed;
            y += ySpeed;

            for (Ball ball : BouncingBalls.BALLS) {
                if (ball != this && checkCollision(this, ball)) {
                    // Обработка столкновения
                    int tempXSpeed = xSpeed;
                    int tempYSpeed = ySpeed;
                    xSpeed = ball.xSpeed;
                    ySpeed = ball.ySpeed;
                    ball.xSpeed = tempXSpeed;
                    ball.ySpeed = tempYSpeed;

                    playPivoSound();
                }
            }
        }

        public void playPivoSound() {
            try {
                Clip clip = AudioSystem.getClip();
                clip.open(AudioSystem.getAudioInputStream(new File("pivo.wav")));
                clip.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}