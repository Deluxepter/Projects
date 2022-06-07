import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Random;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

public class MineSweeper 
{
    private final int GAME_SIZE = 10;
    private final int GAME_PANEL_SIZE = GAME_SIZE * 40;
    private final int GAME_CELL_SIZE = GAME_PANEL_SIZE / GAME_SIZE;

    private final int GAME_DIFFICULTY = 10;

    public static void main(String[] args) {new MineSweeper();}

    JFrame frame = new JFrame();

    public MineSweeper()
    {
        frame.setTitle("MineSweeper");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(new ScoreBoard(), BorderLayout.NORTH);
        frame.add(new Game(), BorderLayout.SOUTH);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    JLabel flagsLabel = new JLabel();
    JLabel timeLabel = new JLabel();

    private class ScoreBoard extends JPanel
    {
        private ScoreBoard()
        {
            this.setPreferredSize(new Dimension(GAME_PANEL_SIZE, GAME_CELL_SIZE));
            this.setLayout(new BorderLayout());
            this.setBorder(new EmptyBorder(0,15,0,15));
            this.add(flagsLabel, BorderLayout.WEST);
            this.add(timeLabel, BorderLayout.EAST);
        }

        protected void paintComponent(Graphics g) 
        {
            g.setColor(Color.lightGray);
            g.fillRect(0, 0, GAME_PANEL_SIZE, GAME_CELL_SIZE);
        }
    }

    private class Game extends JPanel implements ActionListener 
    {
        private int[][] board = new int[GAME_SIZE][GAME_SIZE];
        private int[][] revealed = new int[GAME_SIZE][GAME_SIZE];
        BufferedImage[] img = new BufferedImage[3];
        private int totalMines;
        private int minesLeft;
        private int flagsPlaced;
        private int timeElapsed;
        Timer t = new Timer(1000, this);

        private Game()
        {
            this.setPreferredSize(new Dimension(GAME_PANEL_SIZE, GAME_PANEL_SIZE));
            initializeGame();
            mouseListener();
        }

        private void initializeGame()
        {
            totalMines = 0;
            minesLeft = 0;
            flagsPlaced = 0;
            timeElapsed = 0;
            loadImages();
            generateMines();
            generateNumbers();

            for (int[] row : revealed)
                Arrays.fill(row, 0);

            flagsLabel.setText("Flags left: " + (totalMines-flagsPlaced));
            timeLabel.setText("Time elapsed: " + timeElapsed++);

            t.start();
        }

        public void actionPerformed(ActionEvent arg0) 
        {
            timeLabel.setText("Time elapsed: " + timeElapsed++);
        }

        private void endGame()
        {
            revealAll();
            repaint();

            t.stop();
            
            String msg;

            if(minesLeft == 0)
            {
                msg = "Congratulations! ";
            }
            else
            {
                msg = "You blew up! ";
            }
                
            int playAgain = JOptionPane.showConfirmDialog(null, msg + "Would you like to play again?", "", JOptionPane.YES_NO_OPTION, 3);

            if(playAgain == JOptionPane.YES_OPTION)
            {
                initializeGame();
            }
            else
            {
                System.exit(0);
            }
        }

        private void revealAll()
        {
            for(int x = 0; x < board.length; x++)
            {
                for(int y = 0; y < board.length; y++)
                {
                    revealed[x][y] = 1;
                }
            }
        }

        private void generateMines()
        {
            Random random = new Random();

            for(int x = 0; x < board.length; x++)
            {
                for(int y = 0; y < board.length; y++)
                {
                    int ranInt = random.nextInt(0, GAME_DIFFICULTY);

                    if(ranInt == 1)
                    {
                        board[x][y] = 9;
                        totalMines++;
                        minesLeft++;
                    }
                    else
                    {
                        board[x][y] = 0;
                    }
                }
            }
        }

        private void generateNumbers()
        {
            for(int y = 0; y < board.length; y++)
            {
                for(int x = 0; x < board.length; x++)
                {
                    if(board[x][y] == 0)
                    {
                        board[x][y] = numberOfAdjacentMines(x, y);
                    }
                }
            }

            repaint();
        }

        private int numberOfAdjacentMines(int xPos, int yPos)
        {
            xPos--; yPos--;
            
            int mineCount = 0;

            for(int y = 0; y < 3; y++)
            {
                for(int x = 0; x < 3; x++)
                {
                    try
                    {
                        if(board[x+xPos][y+yPos] == 9)
                        {
                            mineCount++;
                        }
                    }
                    catch(Exception e){}
                }
            }
            return mineCount;
        }

        private void revealAdjacentEmptyCells(int xPos, int yPos)
        {
            try
            {
                if(board[xPos][yPos] > 0)
                    revealed[xPos][yPos] = 1;
                    
                if(board[xPos][yPos] != 0)
                    return;
               
                if(revealed[xPos][yPos] == 1)
                    return;      
                    
                revealed[xPos][yPos] = 1;

                revealAdjacentEmptyCells(xPos - 1, yPos - 1);
                revealAdjacentEmptyCells(xPos - 1, yPos);
                revealAdjacentEmptyCells(xPos - 1, yPos + 1);
                revealAdjacentEmptyCells(xPos, yPos + 1);
                revealAdjacentEmptyCells(xPos + 1, yPos - 1);
                revealAdjacentEmptyCells(xPos + 1, yPos);
                revealAdjacentEmptyCells(xPos + 1, yPos + 1);
                revealAdjacentEmptyCells(xPos, yPos - 1);
            }
            catch(Exception e){}
        }

        private void revealCell(int x, int y)
        {
            if(revealed[x][y] == 2)
                return;

            switch(board[x][y])
            {
                case 0:
                    revealAdjacentEmptyCells(x, y);
                    break;
                case 9:
                    endGame();
                    return;
            }
            revealed[x][y] = 1;
            repaint();
        }

        private void placeFlag(int xPos, int yPos)
        {
            int boardValue = board[xPos][yPos];
            int revealedValue = revealed[xPos][yPos];

            // Check if cell is revealed
            if(revealedValue == 1)
                return;
        
            // Check if flag already exists
            if(revealedValue == 2)
            {
                flagsPlaced--;
                revealed[xPos][yPos] = 0;

                if(board[xPos][yPos] == 9)
                {
                    minesLeft++;
                }
            }
            else
            {
                // Check if flag limit is reached
                if(flagsPlaced >= totalMines)
                    return;

                // Check if there is a mine
                if(boardValue == 9)
                    minesLeft--;
                    
                flagsPlaced++;
                revealed[xPos][yPos] = 2;
            }

            if(minesLeft == 0)
            {
                endGame();
            }
            
            flagsLabel.setText("Flags left: " + (totalMines-flagsPlaced));
            repaint();
        }

        private void loadImages()
        {
            try
            {
                img[0] = ImageIO.read(new File("Java/assets/minesweeper_mine.png"));
                img[1] = ImageIO.read(new File("Java/assets/minesweeper_flag.png"));
            }
            catch(Exception e)
            {
                e.getStackTrace();
            }
        }

        public void paintComponent(Graphics g) 
        {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for(int x = 0; x < board.length; x++)
            {
                for(int y = 0; y < board.length; y++)
                {
                    if(drawFlag(g2d, x, y))
                    {
                        continue;
                    }

                    g2d.setColor(Color.lightGray);
                    g2d.fillRect(x * GAME_CELL_SIZE, y * GAME_CELL_SIZE, GAME_CELL_SIZE, GAME_CELL_SIZE);
                    g2d.setColor(Color.gray);
                    g2d.drawRect(x * GAME_CELL_SIZE, y * GAME_CELL_SIZE, GAME_CELL_SIZE, GAME_CELL_SIZE);
                        
                    if(board[x][y] == 9)
                    {
                        g2d.drawImage(img[0], x * GAME_CELL_SIZE, y * GAME_CELL_SIZE, GAME_CELL_SIZE, GAME_CELL_SIZE, null);
                        continue;
                    }    

                    drawNumber(g2d, x, y);
                }
            }    
        }

        private void drawNumber(Graphics2D g2d, int x, int y)
        {
            switch(board[x][y])
            {
                case 0:
                    g2d.setColor(Color.lightGray);
                    break;
                case 1:
                    g2d.setColor(new Color(64, 98, 187));
                    break;
                case 2:
                    g2d.setColor(new Color(58, 146, 96));
                    break;
                case 3:
                    g2d.setColor(new Color(215, 38, 56));
                    break;
                case 4:
                    g2d.setColor(new Color(58, 8, 66));
                    break;
                case 5:
                    g2d.setColor(new Color(250, 163, 0));
                    break;
                default:
                    g2d.setColor(new Color(215, 38, 56));
                    break;
            }

            // https://stackoverflow.com/questions/27706197/how-can-i-center-graphics-drawstring-in-java
            Rectangle rect = new Rectangle(x * GAME_CELL_SIZE, y * GAME_CELL_SIZE, GAME_CELL_SIZE, GAME_CELL_SIZE);           
            Font font = new Font("Dejavu Sans", Font.PLAIN, 30);
            FontMetrics metrics = g2d.getFontMetrics(font);
            int rectX = rect.x + (rect.width - metrics.stringWidth(numberOfAdjacentMines(x, y)+"")) / 2;
            int rectY = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
            
            g2d.setFont(font);
            g2d.drawString(numberOfAdjacentMines(x, y)+"", rectX,rectY);
        }

        private boolean drawFlag(Graphics2D g2d, int x, int y)
        {
            switch(revealed[x][y])
            {
                case 2:
                    g2d.drawImage(img[1], x * GAME_CELL_SIZE, y * GAME_CELL_SIZE, GAME_CELL_SIZE, GAME_CELL_SIZE, null);
                    return true;
                case 0:
                    g2d.setColor(new Color(150,150,150));
                    g2d.fillRect(x * GAME_CELL_SIZE, y * GAME_CELL_SIZE, GAME_CELL_SIZE, GAME_CELL_SIZE);
                    g2d.setColor(Color.gray);
                    g2d.drawRect(x * GAME_CELL_SIZE, y * GAME_CELL_SIZE, GAME_CELL_SIZE, GAME_CELL_SIZE);
                    return true;
                default:
                    return false;
            }
        }

        private void mouseListener()
        {
            addMouseListener(new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    int xPos = e.getX() * GAME_SIZE / GAME_PANEL_SIZE;
                    int yPos = e.getY() * GAME_SIZE / GAME_PANEL_SIZE;
            
                    if(SwingUtilities.isLeftMouseButton(e))
                    {
                        revealCell(xPos, yPos);
                    }
                    else if(SwingUtilities.isRightMouseButton(e))
                    {
                        placeFlag(xPos, yPos);
                    }
                }
            });
        }
    }
}
