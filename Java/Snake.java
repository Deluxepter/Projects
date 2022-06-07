import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

public class Snake
{
    // Adjust frame size, grid size, and game speed
    private final int FRAME_SIZE = 600;
    private final int GRID_SIZE = 30;
    private final int GAME_SPEED = 75;

    private JFrame frame = new JFrame();

    public static void main(String[] args)
    {
        new Snake();
    }

    // Create frame and add panels
    public Snake()
    {
        frame.setTitle("Snake");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(new ScorePanel(), BorderLayout.NORTH);
        frame.add(new GamePanel(), BorderLayout.SOUTH);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    JLabel scoreLabel = new JLabel();
    JLabel bestScoreLabel = new JLabel();

    // Panel to display current score and best score
    private class ScorePanel extends JPanel
    {
        private ScorePanel()
        {
            this.setPreferredSize(new Dimension(FRAME_SIZE,50));
            this.setLayout(new BorderLayout());
            this.setBorder(new EmptyBorder(15,15,15,15));
            scoreLabel.setText("Current score: ");
            this.add(bestScoreLabel, BorderLayout.EAST);
            this.add(scoreLabel, BorderLayout.WEST);
        }

        public void paintComponent(Graphics g)
        {
            g.setColor(new Color(166,201,70));
            g.fillRect(0,0,FRAME_SIZE,70);
            g.setColor(new Color(83,83,83));
            g.fillRect(0,48, FRAME_SIZE, 2);
        }
    }

    // Panel to display and play game
    private class GamePanel extends JPanel implements ActionListener
    {
        char direction;
        int[] headX;
        int[] headY;
        int appleX;
        int appleY;
        int score;
        int topScore;

        Timer t = new Timer(GAME_SPEED,this);

        // Set panel size, setup game, and initialize keyListener
        private GamePanel()
        {
            this.setPreferredSize(new Dimension(FRAME_SIZE, FRAME_SIZE));
            setupGame();
            keyListener();
        }

        // Check if current score is greater than best score, reset variables, and start timer
        private void setupGame()
        {
            if(score > topScore)
            {
                topScore = score;
                bestScoreLabel.setText("Best Score: " + topScore + " ");
            }
            score = 0;
            direction = 'd';
            appleX = GRID_SIZE*3;
            appleY = GRID_SIZE*3;
            headX = new int[GRID_SIZE*GRID_SIZE];
            headY = new int[GRID_SIZE*GRID_SIZE];
            scoreLabel.setText("Current score: ");
            t.start();
            repaint();
        }

        // Timer executes
        public void actionPerformed(ActionEvent e)
        {
            moveSnake();
            checkApple();
            checkWallCollision();
            repaint();
        }

        // Exits or restarts game when called
        private void gameOVer()
        {
            int dResult = JOptionPane.showConfirmDialog(null, "You lost. Play again?", "", JOptionPane.YES_NO_OPTION);
            if(dResult == 0)
            {
                t.stop();
                setupGame();
            }
            else
            {
                System.exit(0);
            }

        }

        // Checks if head has collided with wall
        private void checkWallCollision()
        {
            if(headX[0] >= FRAME_SIZE) {}
            else if(headX[0] < 0) {}
            else if(headY[0] >= FRAME_SIZE) {}
            else if(headY[0] < 0) {}
            else
            {
                return;
            }
            gameOVer();
        }

        // Check if head has collided with body
        private void checkBodyCollision()
        {
            for(int i = 1; i < score; i++)
            {
                if((headX[0]==headX[i])&&(headY[0]==headY[i]))
                {
                    gameOVer();
                }
            }
        }

        // Move the head and body parts, check for collision with self
        private void moveSnake()
        {
            // Shift body parts in array
            for(int i = score; i > 0; i--)
            {
                headX[i] = headX[(i-1)];
                headY[i] = headY[(i-1)];
            }

            switch (direction)
            {
                case 'w':
                    headY[0] -= GRID_SIZE;
                    break;
                case 'a':
                    headX[0] -= GRID_SIZE;
                    break;
                case 's':
                    headY[0] += GRID_SIZE;
                    break;
                case 'd':
                    headX[0] += GRID_SIZE;
                    break;
            }
            checkBodyCollision();
        }

        // BUG! If the player inputs two keys fast enough the snake can do a 180 which results in game over
        // Get valid key input to change direction
        private void keyListener()
        {
            frame.addKeyListener(new KeyListener()
            {
                public void keyTyped(KeyEvent e) {}
                public void keyReleased(KeyEvent e) {}
                public void keyPressed(KeyEvent e)
                {
                    char input = Character.toLowerCase(e.getKeyChar());

                    if(input == 'w' && direction != 's') {}
                    else if(input == 'a' && direction != 'd') {}
                    else if(input == 's' && direction != 'w') {}
                    else if(input == 'd' && direction != 'a') {}
                    else
                    {
                        return;
                    }

                    direction = input;
                }
            });
        }

        // If player has touched apple, update score, update label, and generate new apple
        private void checkApple()
        {
            if((headX[0] == appleX) && (headY[0] == appleY))
            {
                score++;
                headX[score] = headX[(score-1)];
                headY[score] = headY[(score-1)];
                scoreLabel.setText("Current score: " + score);
                while(!createApple());
            }
        }

        // IMPROVEMENT? Current algorithm works, but can be inefficient at high scores when trying to place apple.
        // Create new apple coordinates
        private boolean createApple()
        {
            Random r = new Random();
            while (true)
            {
                appleX = r.nextInt(FRAME_SIZE / GRID_SIZE) * GRID_SIZE;
                appleY = r.nextInt(FRAME_SIZE / GRID_SIZE) * GRID_SIZE;
                for(int i = 0; i < score; i++)
                {
                    if((headX[i]==appleX)&&(headY[i]==appleY))
                    {
                        return false;
                    }
                }
                return true;
            }
        }

        // Call draw methods
        public void paintComponent(Graphics g)
        {
            drawBackground(g);
            drawApple(g);
            drawSnake(g);
        }

        // Draw checkerboard background
        private void drawBackground(Graphics g)
        {
            for (int x = 0; x < GRID_SIZE; x++)
            {
                for(int y = 0; y < GRID_SIZE; y++ )
                {
                    if(x % 2 == y % 2)
                    {
                        g.setColor(new Color(173,206,76));

                    }
                    else
                    {
                        g.setColor(new Color(166,201,70));
                    }
                    g.fillRect(x*GRID_SIZE, y*GRID_SIZE, GRID_SIZE, GRID_SIZE);
                }
            }
        }

        // Draw apple
        private void drawApple(Graphics g)
        {
            g.setColor(new Color(228,87,46));
            g.fillRect(appleX, appleY, GRID_SIZE, GRID_SIZE);
        }

        // Draw snake head, then body parts
        private void drawSnake(Graphics g)
        {
            for(int i = 0; i <= score; i++)
            {
                if(i==0)
                {
                    g.setColor(new Color(70,100,200));
                    g.fillRect(headX[i], headY[i], GRID_SIZE, GRID_SIZE );
                }
                else
                {
                    g.setColor(new Color(88,114,244));
                    g.fillRect(headX[i], headY[i], GRID_SIZE, GRID_SIZE );
                }

            }
            // IMPORTANT! Game stutters without sync
            Toolkit.getDefaultToolkit().sync();
        }
    }
}
