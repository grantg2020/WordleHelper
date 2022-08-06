import java.awt.Color;

public class Square {
    private int x;
    private int y;
    private int scale;
    private String letter;
    private Color color;
    private int position;

    public Square(int x, int y, int scale, String letter, Color color, int position) {
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.letter = letter;
        this.color = color;
        this.position = position;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getScale() {
        return scale;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public boolean contains(int x, int y) {
        if ((x >= this.x && x <= this.x + scale) && (y >= this.y && y <= this.y + scale))
            return true;

        return false;
    }
}
