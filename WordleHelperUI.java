import javax.swing.*;
import javax.swing.table.*;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;
import java.util.Random;
import java.util.*;

public class WordleHelperUI extends JPanel {
    public static final int BOX_SCALE = 40;
    public static final int PADDING = 10;
    public static final int MAX_GUESSES = 6;
    public static final int WORD_LENGTH = 5;
    public static final int NUM_UNIQUE_GUESSES = 4;

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
    private static ArrayList<String> validWords = new ArrayList<String>();
    private static ArrayList<String> topFive = new ArrayList<String>();
    private static ArrayList<Square[]> squares = new ArrayList<Square[]>();

    /**
     * Gets the number of letters that have not been used in a guess in the word
     * provided
     * 
     * @param word word to get number of unique letters from
     * @return number of unique letters
     */
    public int getUniqueLetters(String word) {
        int count = 0;
        for (int i = 0; i < word.length(); i++) {
            if (!guessed.contains("" + word.charAt(i)))
                count++;
        }
        return count;
    }

    /**
     * Returns an array list of up to five words based on amount of new characters
     * 
     * @return list of words
     */
    public ArrayList<String> getTopFiveWords() {
        validWords = getValidWords();
        // If only 5 words fit, no filters needed
        if (validWords.size() <= 5)
            return validWords;

        ArrayList<String> usedWords;
        usedWords = numGuesses <= NUM_UNIQUE_GUESSES ? words : validWords;

        ArrayList<Word> wordScores = new ArrayList<Word>();

        for (String word : usedWords) {
            wordScores.add(new Word(word, getUniqueLetters(word)));
        }

        Collections.sort(wordScores);

        ArrayList<String> topFive = new ArrayList<String>();
        for (int i = 0; i < 5; i++) {
            topFive.add(wordScores.get(i).getWord());
        }

        /**
         * Guesses: "soare", "soaps"
         * {
         * "soare": 0,
         * "soaps": 0,
         * "built": 5,
         * "spike": 2,
         * }
         */

        return topFive;
    }

    public ArrayList<String> getValidWords() {
        // squares and words
        ArrayList<String> validWords = new ArrayList<String>();
        for (String word : words) {
            // Validate word
            boolean isValid = true;
            for (Square[] w : squares) {

                String guess = "";
                ArrayList<String> validLetters = new ArrayList<String>();
                ArrayList<String> greenLetters = new ArrayList<String>();

                // Make list of valid letters
                for (Square letter : w) {
                    if (letter.getColor() == Color.YELLOW || letter.getColor() == Color.GREEN)
                        validLetters.add(letter.getLetter());
                    if (letter.getColor() == Color.GREEN)
                        greenLetters.add(letter.getLetter());
                }
                // Save the word to a string
                for (Square letter : w)
                    guess += letter.getLetter();

                if (guess.length() != 5)
                    continue;

                // Validate letters based on color
                for (Square letter : w) {
                    String guessLetter = "" + word.charAt(letter.getPosition());
                    if (letter.getLetter().length() > 0) {
                        if (letter.getColor() == Color.GRAY) {
                            // If it contains any of the bad letters
                            if (!validLetters.contains(letter.getLetter()) && word.contains(letter.getLetter())) {
                                isValid = false;
                            }
                        } else if (letter.getColor() == Color.YELLOW) {
                            // If it has yellow in the wrong spot
                            if (letter.getLetter().equals(guessLetter))
                                isValid = false;

                            // Or does not contain it outside of green letters
                            String tempWord = word;

                            for (String le : greenLetters) {
                                if (word.contains(le))
                                    tempWord = tempWord.substring(0, tempWord.indexOf(le) + 1)
                                            + tempWord.substring(tempWord.indexOf(le) + 1);
                            }

                            if (!tempWord.contains(letter.getLetter()))
                                isValid = false;

                            int wordLetterCount = 0;
                            int guessLetterCount = 0;
                            // Or has too many or too little of that letter
                            for (int i = 0; i < word.length(); i++) {
                                if (letter.getLetter().equals(word.charAt(i) + ""))
                                    wordLetterCount++;
                                if (letter.getLetter().equals(guess.charAt(i) + ""))
                                    guessLetterCount++;
                            }

                            if (wordLetterCount != guessLetterCount)
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
                    topFive = getTopFiveWords();
                    repaint();
                }

                super.mouseClicked(e);
            }
        });

        topFive = getTopFiveWords();
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

        if (guesses[guesses.length - 1] != null && guesses[guesses.length - 1].length() > 0 && !hasWon) {
            hasLost = true;
        }
        // Draws guessing grid
        for (int j = 0; j < MAX_GUESSES; j++) { // Row
            ArrayList<Character> letters = new ArrayList<Character>();
            for (int i = 0; i < word.length(); i++) {
                letters.add(word.charAt(i));
            }

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
        g.setColor(Color.white);

        for (int i = 0; i < 5; i++) {
            if (topFive.size() > i)
                g.drawChars(topFive.get(i).toCharArray(), 0, 5,
                        (BOX_SCALE / 2 - 8) + PADDING + (BOX_SCALE + PADDING) * 7,
                        (BOX_SCALE / 2 + 7) +
                                PADDING + (BOX_SCALE + PADDING) * i);
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
            char c = e.getKeyChar();

            if (Character.isLetter(c)) {
                if (guess.length() < WORD_LENGTH)
                    guess += Character.toLowerCase(c);

            } else if (e.getKeyCode() == 10) {
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
                        guessed += guess;

                    } else {
                        frame.setBackground(Color.red);
                    }
                    guess = "";
                }

                topFive = getTopFiveWords();

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