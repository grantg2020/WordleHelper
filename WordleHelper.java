import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class WordleHelper {
    public static WordleHelperIO wordleIO;
    public static ArrayList<String> words = new ArrayList<String>();
    public static WordleHelper singleton;
    public static final int WORD_LENGTH = 5;

    public WordleHelper() {
        wordleIO = new WordleHelperIO("words.txt", WORD_LENGTH);
        words = wordleIO.getWords();
    }

    public static void main(String[] args) {
        singleton = new WordleHelper();

        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to WordleHelper!");
        System.out.print("Enter green letters followed by their place (e.g. a1e5 in apple): ");
        String greenLetters = scanner.next();
        System.out.print("Enter yellow letters followed by their place (e.g. a2e4 in apple): ");
        String yellowLetters = scanner.next();
        System.out.print("Enter gray letters: ");
        String grayLetters = scanner.next();

        Map<String, Integer> greenMap = new HashMap<String, Integer>();

        if (!"0".equals(greenLetters)) {
            for (int i = 0; i < greenLetters.length(); i += 2) {
                String letter = greenLetters.substring(i, i + 1);
                int position = Integer.parseInt(greenLetters.substring(i + 1, i + 2));
                greenMap.put(letter, position);
            }
        }

        Map<String, Integer> yellowMap = new HashMap<String, Integer>();
        if (!"0".equals(yellowLetters)) {
            for (int i = 0; i < yellowLetters.length(); i += 2) {
                String letter = yellowLetters.substring(i, i + 1);
                int position = Integer.parseInt(yellowLetters.substring(i + 1, i + 2));
                yellowMap.put(letter, position);
            }
        }
        
        // Filter out words
        ArrayList<String> filterWords = new ArrayList<String>();
        filterWords.addAll(words);

        for (String word : words) { // For each word
            for (String greenLetter : greenMap.keySet()) { // For each letter in green
                int index = greenMap.get(greenLetter) - 1;
                // If the word does not contain the letter at that index
                if (!greenLetter.contains("" + word.charAt(index))) {
                    // Remove from filter list
                    filterWords.remove(word);
                }
            }
            for (String yellowLetter : yellowMap.keySet()) { // For each letter in yellow
                int index = yellowMap.get(yellowLetter) - 1;
                // If the word does not contain the letter or contains the letter at that index
                if (!word.contains(yellowLetter) || yellowLetter.contains("" + word.charAt(index))) {
                    // Remove from filter list
                    filterWords.remove(word);
                }
            }

            for (char grayLetter : grayLetters.toCharArray()) { // For each letter in yellow
                String letter = "" + grayLetter;
                // If the word contains the letter
                if (word.contains(letter)) {
                    // Remove from filter list
                    filterWords.remove(word);
                }
            }

        }

        System.out.println(filterWords);
    }
}
