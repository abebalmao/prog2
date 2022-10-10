import java.io.Serializable;
import java.util.*;

public class ListGraph<T> implements Graph<T>, Serializable {

    private HashMap<T, Set<Edge<T>>> locations = new HashMap<>();

    public void add(T t1) {
        locations.putIfAbsent(t1, new HashSet<>());
    }
    public void remove(T location) {

        if(!locations.containsKey(location)) {
            throw new NoSuchElementException();
        }
        for (T t : locations.keySet()) {
            if (getEdgeBetween(t, location) != null){
                locations.get(t).removeIf(n -> n.equals(getEdgeBetween(t, location)));
            }
        }
        locations.remove(location);
    }
    public void connect(T t1, T t2, String name, int weight) {

        if (!locations.containsKey(t1) || !locations.containsKey(t2)){
            throw new NoSuchElementException("Connection error");
        } else if (weight < 0) {
            throw new IllegalArgumentException();
        } else if (getEdgeBetween(t1, t2) != null) {
            return;
        }
        
        Set<Edge<T>> firstSet = locations.get(t1);
        Edge<T> edge1 = new Edge<T>(t2, name, weight);
        firstSet.add(edge1);

        Set<Edge<T>> sndSet = locations.get(t2);
        Edge<T> edge2 = new Edge<T>(t1, name, weight);
        sndSet.add(edge2);


    }

    public void clear(){
        locations.clear();
    }

    public void disconnect(T t1, T t2) {
        if (!locations.containsKey(t1) || !locations.containsKey(t2)) {
            throw new NoSuchElementException();
        }
        if (getEdgeBetween(t1, t2) == null) {
            throw new IllegalStateException();
        }
        locations.get(t1).removeIf(n -> n.equals(getEdgeBetween(t1, t2)));
        locations.get(t2).removeIf(n -> n.equals(getEdgeBetween(t2, t1)));
    }

    public Edge<T> getEdgeBetween(T t1, T t2) {

        if (!locations.containsKey(t1) || !locations.containsKey(t2)) {
            throw new NoSuchElementException();
        }

        for (Edge<T> edge : locations.get(t1)) {
            if (edge.getDestination().equals(t2)) { return edge;
            }
        }
        return null;
    }

    public Collection<Edge<T>> getEdgesFrom(T t1) {
        if (locations.get(t1)==null) {
            throw new NoSuchElementException();
        } else {
            return locations.get(t1);
        }
    }

    public List<Edge<T>> getPath(T t1, T t2) {
        Set<T> visited = new HashSet<>();
        Map<T, T> via = new HashMap<>();
        neighbourSearch(t1, null, visited, via);
        List<Edge<T>> path = new ArrayList<>();

        if (!visited.contains(t2)) {
            return null;
        }

        T oldLocation = t2;
        while (!oldLocation.equals(t1)) {
            T newLocation = via.get(oldLocation);
            path.add(getEdgeBetween(newLocation, oldLocation));
            oldLocation = newLocation;
        }

        Collections.reverse(path);
        return path;
    }
    public Set<T> getNodes() {
        return new HashSet<>(locations.keySet());
    }

    public void setConnectionWeight(T t1, T t2, int weight) {

        if (!locations.containsKey(t1)) {
            throw new NoSuchElementException();
        } else if (!locations.containsKey(t2)) {
            throw new NoSuchElementException();
        } else if(weight < 0) {
            throw new IllegalArgumentException();
        }

        getEdgeBetween(t1, t2).setWeight(weight);
        getEdgeBetween(t2, t1).setWeight(weight);
    }
    public boolean pathExists(T t1, T t2) {
        if (locations.get(t1)==null || locations.get(t2)==null) {
            return false;
        }
        Set<T> visitedPlace = new HashSet<>();
        neighbourSearch(t1, visitedPlace);
        return visitedPlace.contains(t2);
    }

    private void neighbourSearch(T t1, Set<T> visitedLocation){
        visitedLocation.add(t1);

        for (Edge<T> e : locations.get(t1)){
            if(!visitedLocation.contains(e.getDestination())) {
                neighbourSearch(e.getDestination(), visitedLocation);
            }
        }
    }
    private void neighbourSearch(T t1, T t2, Set<T> visited, Map<T, T>via){
        visited.add(t1);
        via.put(t1, t2);
        for (Edge<T> e : locations.get(t1)){
            if (!visited.contains(e.getDestination())) {
                neighbourSearch(e.getDestination(), t1, visited, via);
            }
        }

    }
    @Override
    public String toString(){
        StringBuilder string = new StringBuilder();
        for(T location : locations.keySet()){
            string.append(location).append(": ");
            for(Edge<T> e : locations.get(location)){
                string.append(e.toString());
                string.append("\n"); } }
        return string.toString();
    }

}
