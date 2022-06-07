import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class Pong
{
    private final int GAME_PANEL_HEIGHT = 500;
    private final int GAME_PANEL_WIDTH = 750;
    private final int SCORE_PANEL_HEIGHT = 50;
    private final int GAME_SPEED = 10;

    public static void main(String[] args) 
    {
        new Pong();
    }

    JFrame frame = new JFrame();

    public Pong()
    {
        frame.setTitle("Pong");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(new Score(), BorderLayout.NORTH);
        frame.add(new Game(), BorderLayout.SOUTH);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    JLabel scoreOneLabel = new JLabel();
    JLabel scoreTwoLabel = new JLabel();

    private class Score extends JPanel
    {
        private Score()
        {
            Font font = new Font("DejaVu Sans", Font.PLAIN, SCORE_PANEL_HEIGHT/3);
            this.setPreferredSize(new Dimension(GAME_PANEL_WIDTH, SCORE_PANEL_HEIGHT));
            this.setLayout(new BorderLayout());
            this.setBorder(new EmptyBorder(15,15,15,15));
            this.add(scoreOneLabel, BorderLayout.WEST);
            this.add(scoreTwoLabel, BorderLayout.EAST);
            scoreOneLabel.setForeground(Color.white);
            scoreTwoLabel.setForeground(Color.white);
            scoreOneLabel.setFont(font);
            scoreTwoLabel.setFont(font);
        }

        public void paintComponent(Graphics g)
        {
            g.setColor(Color.darkGray);
            g.fillRect(0,0, GAME_PANEL_WIDTH, SCORE_PANEL_HEIGHT);

            g.setColor(Color.white);
            g.fillRect(0,SCORE_PANEL_HEIGHT-1, GAME_PANEL_WIDTH, 1);
        }
    }

    private class Game extends JPanel implements ActionListener
    {
        int ballX, ballY, ballXVel, ballYVel, playerOneDir, playerTwoDir, scoreOne, scoreTwo;
        int ballSize = 18;
        int ballSpeed = 4;
        int halfBall = ballSize/2;
        int paddleHeight = GAME_PANEL_HEIGHT/5;
        int paddleWidth = 10;
        int paddleMoveDistance = 7;
        int paddleOneX = (GAME_PANEL_HEIGHT / 2) - (paddleHeight / 2);
        int paddleTwoX = (GAME_PANEL_HEIGHT / 2) - (paddleHeight / 2);

        private Game()
        {
            this.setPreferredSize(new Dimension(GAME_PANEL_WIDTH, GAME_PANEL_HEIGHT));
            frame.addKeyListener(new TAdapter());
            Timer t = new Timer(GAME_SPEED, this);
            resetBall();
            t.start();
        }

        public void actionPerformed(ActionEvent e)
        {
            setScore();
            moveBall();
            checkCollision();
            movePaddles();
            repaint();
        }

        private void setScore()
        {
            scoreOneLabel.setText(scoreOne + "");
            scoreTwoLabel.setText(scoreTwo + "");
        }

        private void resetBall()
        {
            Random r = new Random();

            ballXVel = 1;
            ballYVel = 1;
            ballX = paddleWidth;
            ballY = r.nextInt(0, GAME_PANEL_HEIGHT);
        }

        // Move the ball
        private void moveBall()
        {
            ballX += ballXVel * ballSpeed;
            ballY += ballYVel * ballSpeed;
        }

        // Check collisions with walls and paddles
        private void checkCollision()
        {
            // Check collision with top and bottom of panel
            if(ballY > GAME_PANEL_HEIGHT - halfBall )
            {
                ballYVel = -1;
            }
            else if(ballY <= 0 + halfBall)
            {
                ballYVel = 1;
            }

            // Check collision with left and right of panel
            if(ballX > GAME_PANEL_WIDTH - halfBall)
            {
                scoreOne++;
                resetBall();
            }
            else if(ballX <= 0)
            {
                scoreTwo++;
                resetBall();
            }

            // Check collision with paddleTwo
            if(ballY >= paddleTwoX && ballY <= paddleTwoX + paddleHeight)
            {
                if(ballX > GAME_PANEL_WIDTH-paddleWidth - halfBall)
                {
                    ballXVel = -1;
                }
            }

            // Check collision with paddleOne
            if(ballY >= paddleOneX && ballY <= paddleOneX+paddleHeight)
            {
                if(ballX < 0+paddleWidth + halfBall)
                {
                    ballXVel = +1;
                }
            }
        }

        // Move paddles
        private void movePaddles()
        {
            switch (playerOneDir)
            {
                case -1:
                    if(paddleOneX > 0)
                    {
                        paddleOneX -= paddleMoveDistance;
                    }
                    break;
                case 1:
                    if(paddleOneX < GAME_PANEL_HEIGHT - paddleHeight)
                    {
                        paddleOneX += paddleMoveDistance;
                    }
                    break;
            }

            switch (playerTwoDir)
            {
                case -1:
                    if(paddleTwoX > 0)
                    {
                        paddleTwoX -= paddleMoveDistance;
                    }
                    break;
                case 1:
                    if(paddleTwoX < GAME_PANEL_HEIGHT - paddleHeight)
                    {
                        paddleTwoX += paddleMoveDistance;
                    }
                    break;
            }
        }

        public void paintComponent(Graphics g)
        {
            Graphics2D g2d = (Graphics2D) g;
            Toolkit.getDefaultToolkit().sync();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setColor(Color.darkGray);

            // Draw background
            g2d.fillRect(0,0,GAME_PANEL_WIDTH,GAME_PANEL_HEIGHT);

            g2d.setColor(Color.white);

            // Draw paddles
            g2d.fillRect(0,paddleOneX, paddleWidth, paddleHeight);
            g2d.fillRect(GAME_PANEL_WIDTH - paddleWidth,paddleTwoX, paddleWidth, paddleHeight);

            // Draw center
            g2d.drawLine(GAME_PANEL_WIDTH / 2,0,GAME_PANEL_WIDTH / 2, GAME_PANEL_HEIGHT);

            // Draw ball
            g2d.setColor(Color.red);
            g2d.fillOval(ballX - halfBall,ballY - halfBall, ballSize, ballSize);
        }

        private class TAdapter extends KeyAdapter
        {
            public void keyPressed(KeyEvent e)
            {
                switch (e.getKeyCode())
                {
                    case KeyEvent.VK_W:
                        playerOneDir = -1;
                        break;
                    case KeyEvent.VK_S:
                        playerOneDir = 1;
                        break;
                    case KeyEvent.VK_UP:
                        playerTwoDir = -1;
                        break;
                    case KeyEvent.VK_DOWN:
                        playerTwoDir = 1;
                        break;
                }
            }

            public void keyReleased(KeyEvent e)
            {
                switch (e.getKeyCode())
                {
                    case (KeyEvent.VK_W):
                        playerOneDir = 0;
                        break;
                    case KeyEvent.VK_S:
                        playerOneDir = 0;
                        break;
                    case KeyEvent.VK_UP:
                        playerTwoDir = 0;
                        break;
                    case KeyEvent.VK_DOWN:
                        playerTwoDir = 0;
                        break;
                }
            }
        }
    }
}
