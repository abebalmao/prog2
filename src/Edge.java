import java.io.Serializable;

// PROG2 VT2022, Inlämningsuppgift del 1
// Grupp 212
// Albin Visteus, alvi9625

public class Edge<T> implements Serializable {

    private T destination;
    private String name;
    private int weight;

    public Edge(T d, String n, int w) {
        this.destination = d;
        this.name = n;
        this.weight = w;
    }

    public String getName() {
        return name;
    }

    public T getDestination() {
        return destination;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        // Om vikten är negativ, kasta exception
        if (weight < 0) {
            throw new IllegalArgumentException();
        } else {
            // Annars sätt vikten
            this.weight = weight;
        }
    }

    public String toString() {
        return "to " + destination + " by " + name + " takes " + weight;
    }
}