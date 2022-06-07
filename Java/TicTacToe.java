import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.Arrays;

public class TicTacToe
{
    // Adjust window and grid size
    private final int FRAME_SIZE = 500;
    private final int GAME_SIZE = 3;

    public static void main(String[] args)
    {
        new TicTacToe();
    }

    // Create the frame and add panel
    public TicTacToe()
    {
        JFrame frame = new JFrame();
        frame.setTitle("Tic-Tac-Toe");
        frame.setSize(new Dimension(FRAME_SIZE, FRAME_SIZE));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.add(new Game(), BorderLayout.NORTH);
        frame.pack();
        frame.setVisible(true);
    }

    // Display and play the game
    private class Game extends JPanel
    {
        private char[][] board = new char[GAME_SIZE][GAME_SIZE];
        private char playerTurn = 'O';
        private int moveCount = 0;

        // Set panel size, reset board, and listen for clicks
        private Game()
        {
            this.setPreferredSize(new Dimension(FRAME_SIZE, FRAME_SIZE));
            resetBoard();
            mouseListener();
        }

        // Fills board array with empty char
        private void resetBoard()
        {
            for(char[] r : board)
            {
                Arrays.fill(r, ' ');
            }
        }

        // Edits the board with valid input and check for win
        private void editBoard(int x, int y)
        {
            if(board[x][y] != ' ')
            {
                return;
            }

            board[x][y] = playerTurn;
            moveCount++;

            repaint();
            checkWin();
            switchTurn();
        }

        // Check for a player win or tie
        private void checkWin()
        {
            int reply = 2;
            if(winConditions())
            {
                reply = JOptionPane.showConfirmDialog(null, playerTurn + " has won!" + "\n" + "Would you like to play again?", "", JOptionPane.YES_NO_OPTION, 3);
            }

            if(moveCount >= Math.pow(GAME_SIZE, 2) && !winConditions())
            {
                reply = JOptionPane.showConfirmDialog(null, "There has been a tie!" + "\n" + "Would you like to play again?", "", JOptionPane.YES_NO_OPTION,3);
            }

            if(reply == JOptionPane.YES_OPTION)
            {
                moveCount = 0;
                resetBoard();
                repaint();
            }
            else if(reply == JOptionPane.NO_OPTION || reply == JOptionPane.CLOSED_OPTION)
            {
                System.exit(0);
            }
        }

        // Switch turns
        private void switchTurn()
        {
            switch(playerTurn)
            {
                case 'X':
                    playerTurn = 'O';
                    break;
                case 'O':
                    playerTurn = 'X';
                    break;
            }
        }

        // Get coordinate's of user click
        private void mouseListener()
        {
            addMouseListener(new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    int x = e.getX() * GAME_SIZE / FRAME_SIZE;
                    int y = e.getY() * GAME_SIZE / FRAME_SIZE;
                    editBoard(x,y);
                }
            });
        }

        // Draw grid and player positions
        public void paintComponent(Graphics g)
        {
            Graphics2D g2D = (Graphics2D) g;
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2D.setStroke(new BasicStroke(5));

            double cMargin = 50;
            double cSize = ((FRAME_SIZE - cMargin) / GAME_SIZE);

            // Fill background
            g2D.setColor(new Color(68, 68, 68));
            g2D.fillRect(0,0,FRAME_SIZE,FRAME_SIZE);

            // Draw grid
            g2D.setColor(new Color(52, 52, 52));
            for (int y = 0; y < GAME_SIZE+1; y++)
            {
                for (int x = 0; x < GAME_SIZE+1; x++)
                {
                    g2D.draw(new Line2D.Double(x*cSize+(cMargin/2),y*cSize-cSize,x*cSize+(cMargin/2), FRAME_SIZE));
                    g2D.draw(new Line2D.Double(x*cSize-cSize,y*cSize+(cMargin/2),FRAME_SIZE, y*cSize+(cMargin/2)));
                }
            }

            // Draw player symbols
            g2D.setStroke(new BasicStroke(15));
            for(int y = 0; y < GAME_SIZE; y++)
            {
                for(int x = 0; x < GAME_SIZE; x++)
                {
                    if(board[x][y] == 'O')
                    {
                        g2D.setColor(new Color(219, 80, 74));
                        g2D.draw(new Ellipse2D.Double( x*cSize + cMargin, y*cSize + cMargin, cSize-cMargin, cSize-cMargin));
                    }
                    else if(board[x][y] == 'X')
                    {
                        g2D.setColor(new Color(68, 204, 255));
                        g2D.draw(new Line2D.Double( x*cSize+cMargin, y*cSize+cMargin, x*cSize+cSize, y*cSize+cSize));
                        g2D.draw(new Line2D.Double( x*cSize+cSize, y*cSize+cMargin, x*cSize+cMargin, y*cSize + cSize));
                    }
                }
            }
        }

        // Return true if user has filled a row, column, diagonal, or anti-diagonal
        private boolean winConditions()
        {
            //Check row
            for (int y = 0; y < GAME_SIZE; y++)
            {
                for (int x = 0; x < GAME_SIZE; x++)
                {
                    if (board[y][x] != playerTurn)
                        break;
                    if (x == GAME_SIZE - 1)
                        return true;
                }
            }

            //Check column
            for (int x = 0; x < GAME_SIZE; x++)
            {
                for (int y = 0; y < GAME_SIZE; y++)
                {
                    if (board[y][x] != playerTurn)
                        break;
                    if (y == GAME_SIZE - 1)
                        return true;
                }
            }

            //Check diagonal
            for (int xy = 0; xy < GAME_SIZE; xy++)
            {
                if (board[xy][xy] != playerTurn)
                    break;
                if (xy == GAME_SIZE - 1)
                    return true;
            }

            //Check anti-diagonal
            for (int y = GAME_SIZE-1, x = 0; y > -1; y--, x++)
            {
                if (board[y][x] != playerTurn)
                    break;
                if (x == GAME_SIZE - 1)
                    return true;
            }
            return false;
        }
    }
}
