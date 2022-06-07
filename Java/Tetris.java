import java.awt.*;
import javax.swing.*;
import java.util.Arrays;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Tetris
{
    public static void main(String[] args) { new Tetris(); }
    
    JFrame frame = new JFrame();
    
    private Tetris()
    {
        frame.setTitle("Tetris");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(new Game(), BorderLayout.SOUTH);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private class Game extends JPanel implements ActionListener
    {
        private final int TETROMINO_SHAPES[][][] = new int[][][]
        {
            {{0,0}, {0,1}, {1,0}, {1,1}}, // O
            {{0,0}, {3,0}, {2,0}, {1,0}}, // I
            {{1,0}, {0,1}, {2,1}, {1,1}}, // T
            {{0,0}, {0,1}, {2,1}, {1,1}}, // L
            {{2,0}, {0,1}, {2,1}, {1,1}}, // J
            {{1,0}, {2,0}, {0,1}, {1,1}}, // S
            {{0,0}, {1,0}, {2,1}, {1,1}}, // Z
        };
        
        private final Color colors[] = new Color[]
        { 
            Color.yellow,
            Color.cyan,
            Color.magenta,
            Color.orange,
            Color.blue,
            Color.green,
            Color.red,
            Color.darkGray // Background
        };

        private final int GAME_WIDTH = 10;
        private final int GAME_HEIGHT = 20;
        private final int CELL_SIZE = 30;
        private final int GAME_SPEED = 500;

        private Timer t = new Timer(GAME_SPEED, this);
        private RandomBag rBag = new RandomBag(TETROMINO_SHAPES.length);
        
        private int board[][] = new int[GAME_WIDTH][GAME_HEIGHT];

        private int tetrominoPos[][] = new int[4][2];     
        private int currentTetromino = 0;
        private char tetrominoDirection = 'S';
        

        private Game()
        {
            this.setPreferredSize(new Dimension(GAME_WIDTH * CELL_SIZE, GAME_HEIGHT * CELL_SIZE));

            for (int[] row : board)
            {
                Arrays.fill(row, colors.length-1);
            }

            keyListener();
            createTetromino();
            t.start();
        }

        public void actionPerformed(ActionEvent e)
        {
            tetrominoDirection = 'S';
            moveTetromino();
        }

        private void createTetromino()
        {
            if(rBag.isEmpty())
            {
                for(int i = 0; i < TETROMINO_SHAPES.length; i++)
                {
                    rBag.add(i);
                }
            }

            int rBagValue =  (int) rBag.randomRemove();

            currentTetromino = rBagValue;

            for(int x = 0; x < tetrominoPos.length; x++)
            {
                for(int y = 0; y < tetrominoPos[0].length; y++)
                {
                    tetrominoPos[x][y] = TETROMINO_SHAPES[rBagValue][x][y];
                }
            }
        }

        private void placeTetrominoOnBoard()
        {
            for(int y = 0; y < tetrominoPos.length; y++)
            {
                int yPos = tetrominoPos[y][0];
                int xPos = tetrominoPos[y][1];

                board[yPos][xPos] = currentTetromino;
            }
        }

        private int[][] cloneTerminoPos()
        {
            int[][] cloned = new int[tetrominoPos.length][];

            for(int i = 0; i < tetrominoPos.length; i++)
                cloned[i] = tetrominoPos[i].clone();
            
            return cloned;
        }

        private void moveTetromino()
        {
            int[][] array = calculateTetrominoMove(cloneTerminoPos());

            if(reachedTop())
                System.exit(0);

            if(isBlocked(array))
                return;
             
            if(hasCollided(array))
            {
                placeTetrominoOnBoard();
                clearLines();
                createTetromino();
            }
            else
            {
                for(int y = 0; y < array.length; y++)
                {
                    tetrominoPos[y][0] = array[y][0];
                    tetrominoPos[y][1] = array[y][1];
                }
            }

            repaint();
        }

        private boolean reachedTop()
        {
            for(int x = 0; x < GAME_WIDTH; x++)
            {
                if(board[x][1] != colors.length-1)
                {
                    return true;
                }
            }
            return false;
        }

        private boolean isBlocked(int[][] array)
        {
            try 
            {
                for(int y = 0; y < array.length; y++)
                {
                    int xPos = array[y][1];
                    int YPos = array[y][0];

                    if(!hasCollided(array))
                    {
                        if(board[YPos][xPos] != 7)
                        {
                            return true;
                        }
                    }
                }
            } 
            catch (Exception e) 
            {
                return true;
            }
            
            return false;
        }

        private void clearLines()
        {
            for(int y = 0; y < GAME_HEIGHT; y++)
            {
                for(int x = 0; x < GAME_WIDTH; x++)
                {
                    if(board[x][y] == 7)
                        break;

                    if(x == GAME_WIDTH-1)
                    {
                        for(int i = 0; i < GAME_WIDTH; i++)
                        {
                            board[i][y] = colors.length-1;
                        }
                        compressLines(y);
                    }
                }
            }
        }

        private void compressLines(int yMax)
        {
            for(int y = yMax; y > 0; y--)
            {
                for(int x = 0; x < GAME_WIDTH; x++)
                {
                    board[x][y] = board[x][y-1];
                }
            }
        }
            
        private int[][] calculateTetrominoMove(int[][] array)
        {
            switch(tetrominoDirection)
            {
                case 'W': 
                    int xFromOrigin = tetrominoPos[tetrominoPos.length-1][1];
                    int yFromOrigin = tetrominoPos[tetrominoPos.length-1][0];

                    for(int y = 0; y < array.length; y++)
                    {
                        int xCoord = array[y][1];
                        int yCoord = array[y][0];

                        /*
                            Formulas used to determine new x and y positions for right rotation
                            x2 = (y1 + px - py)
                            y2 = (px + py - x1)
                        */ 

                        int xRotated = (yCoord + xFromOrigin - yFromOrigin);
                        int yRotated = (xFromOrigin + yFromOrigin - xCoord);

                        array[y][1] = xRotated;
                        array[y][0] = yRotated;
                    }
                    break;
                case 'S':
                    array[0][1]++;
                    array[1][1]++;
                    array[2][1]++;
                    array[3][1]++;
                    break;
                case 'A':
                    array[0][0]--;
                    array[1][0]--;
                    array[2][0]--;
                    array[3][0]--;
                    break;
                case 'D':
                    array[0][0]++;
                    array[1][0]++;
                    array[2][0]++;
                    array[3][0]++;
                    break;
            }
            return array;
        }
        
        private boolean hasCollided(int[][] array)
        {
            for(int y = 0; y < array.length; y++)
            {
                for(int x = 0; x < array[0].length; x++)
                {
                    if(array[y][x] >= GAME_HEIGHT)
                    {
                        return true;
                    }
                }
            }

            for(int y = 0; y < array.length; y++)
            {
                int xPos = array[y][1];
                int YPos = array[y][0];

                if(board[YPos][xPos] != 7)
                {
                    if(tetrominoDirection == 'S')
                    {
                        return true;
                    }
                }
            }
            return false;
        }

        protected void paintComponent(Graphics g) 
        {
            // Paint background and placed tetrominos
            for(int x = 0; x < GAME_WIDTH; x++)
            {
                for(int y = 0; y < GAME_HEIGHT; y++)
                {
                    g.setColor(colors[board[x][y]]);
                    g.fillRect(x*CELL_SIZE, y*CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }

            // Paint current tetromino
            g.setColor(colors[currentTetromino]);
            g.fillRect(tetrominoPos[0][0] * CELL_SIZE, tetrominoPos[0][1]* CELL_SIZE, CELL_SIZE, CELL_SIZE);
            g.fillRect(tetrominoPos[1][0] * CELL_SIZE, tetrominoPos[1][1]* CELL_SIZE, CELL_SIZE, CELL_SIZE);
            g.fillRect(tetrominoPos[2][0] * CELL_SIZE, tetrominoPos[2][1]* CELL_SIZE, CELL_SIZE, CELL_SIZE);
            g.fillRect(tetrominoPos[3][0] * CELL_SIZE, tetrominoPos[3][1]* CELL_SIZE, CELL_SIZE, CELL_SIZE);
            Toolkit.getDefaultToolkit().sync();
        }

        private void keyListener()
        {
            frame.addKeyListener(new KeyListener()
            {
                public void keyTyped(KeyEvent e) {}
                public void keyReleased(KeyEvent e) {}
                public void keyPressed(KeyEvent e)
                {
                    tetrominoDirection = Character.toUpperCase(e.getKeyChar());
                    moveTetromino();
                }
            });
        }
    }
    
}
