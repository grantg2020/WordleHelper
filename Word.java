public class Word implements Comparable<Word> {

    /** Word the object represents */
    private String word;
    /** Score of the word */
    private int score;

    public Word(String word, int score) {
        this.word = word;
        this.score = score;
    }

    public String getWord() {
        return word;
    }

    // public void setWord(String word) {
    // this.word = word;
    // }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public int compareTo(Word o) {
        return ((Word) o).getScore() - score;
    }
}
