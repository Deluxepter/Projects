import java.awt.*;
import javax.swing.*;
import java.util.Random;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

// am stuck, will come back when smarter

public class TwentyFourtyEight 
{
    public static void main(String[] args) {new TwentyFourtyEight();}

    JFrame frame = new JFrame();

    private TwentyFourtyEight()
    {
        frame.setTitle("Tetris");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(new Game(), BorderLayout.NORTH);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private class Game extends JPanel
    {
        private final int GAME_SIZE = 4;
        private final int CELL_SIZE = 130;
        private final int MARGINS = 5;

        private int[][] board = new int[GAME_SIZE][GAME_SIZE];

        private int cellsShown = 0;
        private char direction = 'w';

        private Game()
        {
            this.setPreferredSize(new Dimension((4 * CELL_SIZE) + MARGINS, (4 * CELL_SIZE) + MARGINS));
            keyListener();
            fillBoard();
            placeNumber();
        }

        private void move()
        {
            compress();
            combine();
            compress();
            
            placeNumber();
            repaint();
        }

        private void compress()
        {
            
        }

        private void combine()
        {

        }

        private void placeNumber()
        {
            while(cellsShown < 4 * 4)
            {
                Random r = new Random();
                int y = r.nextInt(0, 4);
                int x = r.nextInt(0, 4);

                if(board[y][x] == 0)
                {
                    board[y][x] = 2;
                    cellsShown++;
                    return;
                }
            }
        }

        private void fillBoard()
        {
            for(int y = 0; y < GAME_SIZE; y++)
            {
                for(int x = 0; x < GAME_SIZE; x++)
                {
                    board[y][x] = 0;
                }
            }
        }

        private void printBoard()
        {
            for(int y = 0; y < GAME_SIZE; y++)
            {
                for(int x = 0; x < GAME_SIZE; x++)
                {
                    System.out.print(board[y][x]);
                }
                System.out.println();
            }
        }

        protected void paintComponent(Graphics g) 
        {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setColor(Color.white);
            g2d.fillRect(0, 0, 4*CELL_SIZE, 4*CELL_SIZE);
            
            for(int y = 0; y < 4; y++)
            {
                for(int x = 0; x < 4; x++)
                {
                    if(board[y][x] == 0)
                        continue;

                    g2d.setColor(Color.gray);
                    g2d.fillRect(x * CELL_SIZE + MARGINS, y * CELL_SIZE + MARGINS, CELL_SIZE - MARGINS, CELL_SIZE - MARGINS);

                    g2d.setColor(Color.white);
                    Rectangle rect = new Rectangle(x * CELL_SIZE + MARGINS, y * CELL_SIZE + MARGINS, CELL_SIZE - MARGINS, CELL_SIZE - MARGINS);           
                    Font font = new Font("Dejavu Sans", Font.PLAIN, 30);
                    FontMetrics metrics = g2d.getFontMetrics(font);
                    int rectX = rect.x + (rect.width - metrics.stringWidth(board[y][x]+"")) / 2;
                    int rectY = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
                    
                    g2d.setFont(font);
                    g2d.drawString(board[y][x]+"", rectX,rectY);
                }
            }
        }

        private void keyListener()
        {
            frame.addKeyListener(new KeyListener()
            {
                public void keyTyped(KeyEvent e) {}
                public void keyReleased(KeyEvent e) {}
                public void keyPressed(KeyEvent e)
                {
                    direction = Character.toLowerCase(e.getKeyChar());
                    move();
                }
            });
        }
    }
}
