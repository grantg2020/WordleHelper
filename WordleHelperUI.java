import javax.swing.*;
import javax.swing.table.*;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;
import java.util.Random;
import java.util.ArrayList;

public class WordleHelperUI extends JPanel {
    public static final int BOX_SCALE = 40;
    public static final int PADDING = 10;
    public static final int MAX_GUESSES = 6;
    public static final int WORD_LENGTH = 5;
    private static final String WIN_MESSAGE = "You win!";
    private static final String LOSE_MESSAGE = "You lose!";

    public static String[] guesses = new String[MAX_GUESSES];
    public static int numGuesses = 0;
    public static JFrame frame;
    private static String guess = "";
    private static String guessed = "";
    private static String word = "";
    // private static String greenLetters = "";
    // private static String yellowLetters = "";
    private static WordleHelperIO wordleIO;
    private static boolean hasWon = false;
    private static boolean hasLost = false;
    private static ArrayList<String> words = new ArrayList<String>();
    private static ArrayList<Square[]> squares = new ArrayList<Square[]>();

    private static final String[][] keyboard = { { "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P" },
            { "A", "D", "S", "F", "G", "H", "J", "K", "L" },
            { "Z", "X", "C", "V", "B", "N", "M" }
    };

    public ArrayList<String> getValidWords() {
        // squares and words
        ArrayList<String> validWords = new ArrayList<String>();
        for (String word : words) {
            // Validate word
            boolean isValid = true;

            for (Square[] w : squares) {
                ArrayList<String> yellowLetters = new ArrayList<String>();
                for (Square letter : w) {
                    if (letter.getColor() == Color.YELLOW || letter.getColor() == Color.GREEN)
                        yellowLetters.add(letter.getLetter());
                }
                for (Square letter : w) {
                    if (letter.getLetter().length() > 0) {
                        if (letter.getColor() == Color.GRAY) {
                            // If it contains any of the bad letters
                            if (!yellowLetters.contains(letter.getLetter()) && word.contains(letter.getLetter())) {
                                isValid = false;
                            }
                        } else if (letter.getColor() == Color.YELLOW) {
                            // If it has yellow in the wrong spot
                            if (letter.getLetter().equals("" + word.charAt(letter.getPosition())))
                                isValid = false;

                            // Or does not contain it
                            if (!word.contains(letter.getLetter()))
                                isValid = false;
                        } else if (letter.getColor() == Color.GREEN) {
                            // If it does not have green in the right spot
                            if (!letter.getLetter().equals("" + word.charAt(letter.getPosition())))
                                isValid = false;
                        }
                    }
                }
            }
            if (isValid)
                validWords.add(word);
        }

        return validWords;
    }

    public WordleHelperUI() {
        wordleIO = new WordleHelperIO("words.txt", WORD_LENGTH);
        words = wordleIO.getWords();
        for (int i = 0; i < WORD_LENGTH; i++) {
            squares.add(new Square[MAX_GUESSES - 1]);
            for (int j = 0; j < squares.get(i).length; j++) {
                squares.get(i)[j] = new Square(PADDING + (BOX_SCALE + PADDING) * j, PADDING + (BOX_SCALE + PADDING) * i,
                        BOX_SCALE, "", Color.GRAY, j);
            }
        }

        addKeyListener(new WordleKeyListener());
        setFocusable(true);
        requestFocusInWindow();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Square s = getClickedSquare(e.getX(), e.getY());

                if (s != null && !"".equals(s.getLetter())) {
                    // System.out.println(s.getLetter());
                    if (s.getColor() == Color.GRAY) {
                        s.setColor(Color.GREEN);
                    } else if (s.getColor() == Color.GREEN) {
                        s.setColor(Color.YELLOW);
                    } else {
                        s.setColor(Color.GRAY);
                    }
                    repaint();
                }

                super.mouseClicked(e);
            }
        });
    }

    /**
     * Gets the square clicked if it is valid, otherwise null
     * 
     * @param x x value of mouse position
     * @param y y value of mouse position
     * @return Square at that location, else null
     */
    public Square getClickedSquare(int x, int y) {
        for (Square[] s : squares) {
            for (int i = 0; i < s.length; i++) {
                if (s[i].contains(x, y))
                    return s[i];
            }
        }
        return null;
    }

    public void paint(Graphics g) {

        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 22));

        // guessed = "";
        // greenLetters = "";
        // yellowLetters = "";

        if (guesses[guesses.length - 1] != null && guesses[guesses.length - 1].length() > 0 && !hasWon) {
            hasLost = true;
        }
        // Draws guessing grid
        for (int j = 0; j < MAX_GUESSES; j++) { // Row
            ArrayList<Character> letters = new ArrayList<Character>();
            for (int i = 0; i < word.length(); i++) {
                letters.add(word.charAt(i));
            }

            // if (guesses[j] != null) {
            // guessed += guesses[j];
            // // Remove green letters
            // for (int i = 0; i < guesses[j].length(); i++) {
            // if (guesses[j].charAt(i) == word.charAt(i)) {
            // greenLetters += "" + word.charAt(i);
            // letters.remove((Character) word.charAt(i));
            // }
            // if (word.contains("" + guesses[j].charAt(i))) {
            // yellowLetters += "" + guesses[j].charAt(i);
            // }
            // }
            // if (letters.size() == 0) {
            // hasWon = true;
            // }
            // }

            squares.forEach((Square[] s) -> {
                for (int i = 0; i < s.length; i++) {
                    Square square = s[i];
                    g.setColor(s[i].getColor());
                    g.drawRect(square.getX(), square.getY(), square.getScale(), square.getScale());
                }
            });

            if (guesses[j] != null) {
                for (int k = 0; k < guesses[j].length(); k++) { // Letters in row
                    char[] chars = { guesses[j].charAt(k) };

                    g.setColor(Color.white);
                    g.drawChars(chars, 0, 1, (BOX_SCALE / 2 - 8) + PADDING + (BOX_SCALE + PADDING) * k,
                            (BOX_SCALE / 2 + 7) +
                                    PADDING + (BOX_SCALE + PADDING) * j);
                }
            }

            // Draw current guess
            if (j == numGuesses) {
                for (int k = 0; k < guess.length(); k++) { // Letters in row
                    char[] chars = { guess.charAt(k) };

                    g.setColor(Color.white);
                    g.drawChars(chars, 0, 1, (BOX_SCALE / 2 - 8) + PADDING + (BOX_SCALE + PADDING) * k,
                            (BOX_SCALE / 2 + 7) +
                                    PADDING + (BOX_SCALE + PADDING) * j);
                }
            }
        }
    }

    /**
     * Create the GUI and show it. For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {

        // Create and set up the window.
        WordleHelperUI ui = new WordleHelperUI();

        frame = new JFrame("Wordle");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // if()
        frame.setBackground(Color.BLACK);
        frame.getContentPane().add(ui);
        frame.setSize(850, 350);
        frame.setVisible(true);
        frame.setResizable(false);

        // JButton b = new JButton("Play Again");
        // b.setBounds(50, 100, 95, 30);
        // frame.add(b);

        // Display the window.
        // frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }

        });
    }

    public void resetGame() {
        hasWon = hasLost = false;
        guessed = "";
        guesses = new String[MAX_GUESSES];
        numGuesses = 0;

        repaint();
    }

    @Override
    public void repaint() {
        super.repaint();
        if (frame != null)
            frame.setBackground(Color.black);
    }

    public class WordleKeyListener implements KeyListener {

        @Override
        public void keyReleased(KeyEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void keyPressed(KeyEvent e) {
            if ((hasWon || hasLost) && e.getKeyCode() == 10) {
                resetGame();
            }

            if (hasWon)
                return;

            char c = e.getKeyChar();

            if (Character.isLetter(c)) {
                if (guess.length() < WORD_LENGTH)
                    guess += Character.toLowerCase(c);

            } else if (e.getKeyCode() == 10) {
                System.out.println(getValidWords());
                // Guess complete
                if (guess.length() == WORD_LENGTH) {
                    if (wordleIO.isWord(guess.toLowerCase()) && numGuesses < MAX_GUESSES) {
                        guesses[numGuesses] = guess.toLowerCase();
                        Square[] s = squares.get(numGuesses);
                        for (int i = 0; i < s.length; i++) {
                            s[i].setLetter("" + guesses[numGuesses].charAt(i));
                            s[i].setPosition(i);
                        }
                        numGuesses++;

                    } else {
                        frame.setBackground(Color.red);
                    }
                    guess = "";
                }
            } else if (e.getKeyCode() == 8) {
                // Backspace
                if (guess.length() > 0)
                    guess = guess.substring(0, guess.length() - 1);
            }

            repaint();
        }

        @Override
        public void keyTyped(KeyEvent e) {
            // Leave empty
        }

        public String getGuess() {
            return guess;
        }
    }

}