import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    private static final int UNIT_SIZE = 25;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;
    private static final int START_SIZE = 5;
    private static final int INITIAL_DELAY = 140;
    private static final int MIN_DELAY = 60;

    private final ArrayList<Point> snake = new ArrayList<>();
    private Point food;
    private char direction = 'R';
    private boolean running = false;
    private boolean paused = false;
    private int delay = INITIAL_DELAY;
    private int score = 0;
    private static int highScore = 0;
    private Timer timer;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new MyKeyAdapter());
        startGame();
    }

    private void startGame() {
        snake.clear();
        for (int i = START_SIZE - 1; i >= 0; i--)
            snake.add(new Point(i * UNIT_SIZE, 0));
        direction = 'R';
        delay = INITIAL_DELAY;
        score = 0;
        paused = false;
        placeFood();
        running = true;
        if (timer != null) timer.stop();
        timer = new Timer(delay, this);
        timer.start();
    }

    private void placeFood() {
        Random rand = new Random();
        Point candidate;
        int maxAttempts = (WIDTH / UNIT_SIZE) * (HEIGHT / UNIT_SIZE);
        do {
            candidate = new Point(
                rand.nextInt(WIDTH / UNIT_SIZE) * UNIT_SIZE,
                rand.nextInt(HEIGHT / UNIT_SIZE) * UNIT_SIZE
            );
            maxAttempts--;
        } while (snake.contains(candidate) && maxAttempts > 0);
        food = candidate;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (running) {
            drawGrid(g2);

            // food
            g2.setColor(new Color(220, 50, 50));
            g2.fillOval(food.x + 2, food.y + 2, UNIT_SIZE - 4, UNIT_SIZE - 4);

            // snake
            for (int i = 0; i < snake.size(); i++) {
                Point p = snake.get(i);
                if (i == 0) {
                    g2.setColor(new Color(0, 220, 80));
                } else {
                    int shade = Math.max(80, 180 - i * 2);
                    g2.setColor(new Color(0, shade, 40));
                }
                g2.fillRoundRect(p.x + 1, p.y + 1, UNIT_SIZE - 2, UNIT_SIZE - 2, 6, 6);
            }

            // HUD
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Monospaced", Font.BOLD, 16));
            g2.drawString("Score: " + score, 8, 22);
            String hsText = "Best: " + highScore;
            g2.drawString(hsText, WIDTH - g2.getFontMetrics().stringWidth(hsText) - 8, 22);

            if (paused) drawPausedOverlay(g2);

        } else {
            showGameOver(g2);
        }
    }

    private void drawGrid(Graphics2D g) {
        g.setColor(new Color(20, 20, 20));
        for (int x = 0; x < WIDTH; x += UNIT_SIZE)
            g.drawLine(x, 0, x, HEIGHT);
        for (int y = 0; y < HEIGHT; y += UNIT_SIZE)
            g.drawLine(0, y, WIDTH, y);
    }

    private void drawPausedOverlay(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        String msg = "PAUSED";
        g.drawString(msg, (WIDTH - g.getFontMetrics().stringWidth(msg)) / 2, HEIGHT / 2);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.setColor(Color.WHITE);
        String hint = "Press P to resume";
        g.drawString(hint, (WIDTH - g.getFontMetrics().stringWidth(hint)) / 2, HEIGHT / 2 + 36);
    }

    private void showGameOver(Graphics2D g) {
        g.setColor(new Color(200, 40, 40));
        g.setFont(new Font("Arial", Font.BOLD, 44));
        String msg = "Game Over";
        g.drawString(msg, (WIDTH - g.getFontMetrics().stringWidth(msg)) / 2, HEIGHT / 2 - 30);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        String scoreMsg = "Score: " + score + "   Best: " + highScore;
        g.drawString(scoreMsg, (WIDTH - g.getFontMetrics().stringWidth(scoreMsg)) / 2, HEIGHT / 2 + 10);

        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.setColor(Color.LIGHT_GRAY);
        String hint = "R = Restart   Q = Quit";
        g.drawString(hint, (WIDTH - g.getFontMetrics().stringWidth(hint)) / 2, HEIGHT / 2 + 44);
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
            score++;
            placeFood();
            if (delay > MIN_DELAY) {
                delay -= 5;
                timer.setDelay(delay);
            }
        } else {
            snake.remove(snake.size() - 1);
        }
    }

    private void checkCollisions() {
        Point head = snake.get(0);
        if (head.x < 0 || head.x >= WIDTH || head.y < 0 || head.y >= HEIGHT) {
            endGame();
            return;
        }
        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) { endGame(); return; }
        }
    }

    private void endGame() {
        running = false;
        if (score > highScore) highScore = score;
        timer.stop();
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
                case KeyEvent.VK_LEFT, KeyEvent.VK_A  -> { if (direction != 'R') direction = 'L'; }
                case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> { if (direction != 'L') direction = 'R'; }
                case KeyEvent.VK_UP, KeyEvent.VK_W    -> { if (direction != 'D') direction = 'U'; }
                case KeyEvent.VK_DOWN, KeyEvent.VK_S  -> { if (direction != 'U') direction = 'D'; }
                case KeyEvent.VK_P -> { if (running) paused = !paused; }
                case KeyEvent.VK_R -> { if (!running) startGame(); }
                case KeyEvent.VK_Q -> System.exit(0);
            }
        }
    }
}
