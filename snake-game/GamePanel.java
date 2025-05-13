import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    private final int UNIT_SIZE = 25;
    private final int WIDTH = 600;
    private final int HEIGHT = 600;
    private int delay = 140;
    private final int START_SIZE = 5;

    private final ArrayList<Point> snake = new ArrayList<>();
    private Point food;
    private char direction = 'R';
    private boolean running = false;
    private boolean paused = false;
    private Timer timer;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.black);
        setFocusable(true);
        addKeyListener(new MyKeyAdapter());
        startGame();
    }

    private void startGame() {
        snake.clear();
        for (int i = START_SIZE - 1; i >= 0; i--) {
            snake.add(new Point(i * UNIT_SIZE, 0));
        }
        direction = 'R';
        delay = 140;
        placeFood();
        running = true;
        paused = false;
        timer = new Timer(delay, this);
        timer.start();
    }

    private void placeFood() {
        Random rand = new Random();
        food = new Point(
            rand.nextInt(WIDTH / UNIT_SIZE) * UNIT_SIZE,
            rand.nextInt(HEIGHT / UNIT_SIZE) * UNIT_SIZE
        );
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (running) {
            g.setColor(Color.red);
            g.fillOval(food.x, food.y, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < snake.size(); i++) {
                g.setColor(i == 0 ? new Color(0, 200, 0) : Color.green);
                Point p = snake.get(i);
                g.fillRect(p.x, p.y, UNIT_SIZE, UNIT_SIZE);
            }

            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Score: " + (snake.size() - START_SIZE), 10, 25);

        } else {
            showGameOver(g);
        }
    }

    private void move() {
        Point head = new Point(snake.get(0));
        switch (direction) {
            case 'U' -> head.y -= UNIT_SIZE;
            case 'D' -> head.y += UNIT_SIZE;
            case 'L' -> head.x -= UNIT_SIZE;
            case 'R' -> head.x += UNIT_SIZE;
        }

        snake.add(0, head);

        if (head.equals(food)) {
            placeFood();
            if (delay > 60) {
                delay -= 5; // Speed up
                timer.setDelay(delay);
            }
        } else {
            snake.remove(snake.size() - 1);
        }
    }

    private void checkCollisions() {
        Point head = snake.get(0);
        if (head.x < 0 || head.x >= WIDTH || head.y < 0 || head.y >= HEIGHT) {
            running = false;
        }
        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                running = false;
                break;
            }
        }
        if (!running) timer.stop();
    }

    private void showGameOver(Graphics g) {
        String msg = "Game Over";
        g.setColor(Color.red);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.drawString(msg, (WIDTH - g.getFontMetrics().stringWidth(msg)) / 2, HEIGHT / 2);

        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.setColor(Color.white);
        g.drawString("Press R to Restart or Q to Quit", (WIDTH - 250) / 2, HEIGHT / 2 + 40);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running && !paused) {
            move();
            checkCollisions();
        }
        repaint();
    }

    private class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT -> { if (direction != 'R') direction = 'L'; }
                case KeyEvent.VK_RIGHT -> { if (direction != 'L') direction = 'R'; }
                case KeyEvent.VK_UP -> { if (direction != 'D') direction = 'U'; }
                case KeyEvent.VK_DOWN -> { if (direction != 'U') direction = 'D'; }
                case KeyEvent.VK_P -> paused = !paused;
                case KeyEvent.VK_R -> {
                    if (!running) startGame();
                }
                case KeyEvent.VK_Q -> System.exit(0);
            }
        }
    }
}
