import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

// PROG2 VT2022, Inlämningsuppgift del 1
// Grupp 212
// Albin Visteus, alvi9625

public class Place extends Circle {
    private final String name;
    private boolean selected;
    public Place(String n, double x, double y) {
        this.name = n;
        setCenterX(x);
        setCenterY(y);
        setRadius(10);
        setFill(Color.BLUE);
        setId(n);
    }

    public String getName() {
        return name;
    }

    public void setSelected(boolean isSelected) {
        //Color red = Color.valueOf("0xff0000ff");
        if (isSelected) {
            selected = true;
            setFill(Color.RED);
        } else {
            selected = false;
            setFill(Color.BLUE);
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public String toString() {
        return name;
    }
}
