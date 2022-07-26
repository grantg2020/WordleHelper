import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * IO for getting dictionary files
 */
public class WordleHelperIO {

    private ArrayList<String> words = new ArrayList<String>();

    public WordleHelperIO(String filename, int length) {

        try (Scanner f = new Scanner(new FileInputStream(filename))) {

            while (f.hasNextLine()) {
                String word = f.nextLine().trim();
                if (word.length() == length)
                    words.add(word);
            }

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public ArrayList<String> getWords() {
        return words;
    }

    public boolean isWord(String word) {
        return words.contains(word);
    }
}
