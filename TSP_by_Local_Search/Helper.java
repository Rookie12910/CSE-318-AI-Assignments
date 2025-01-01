import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Helper {


    static class InsertionCost {
        int position;
        double cost;

        InsertionCost(int position, double cost) {
            this.position = position;
            this.cost = cost;
        }
    }

    static double calculateTourDistance(List<Node> tour) {
        double distance = 0;
        for (int i = 0; i < tour.size(); i++) {
            Node from = tour.get(i);
            Node to = tour.get((i + 1) % tour.size());
            distance += from.distanceTo(to);
        }
        return distance;
    }
    
    static Node getNearestNode(List<Node> nodes, Node source)
    {
        Node nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Node node : nodes) {
            double distance = source.distanceTo(node);
            if (distance < minDistance) {
                minDistance = distance;
                nearest = node;
            }
        }
        return nearest;
    }
    
    // Nearest Neighbor Heuristic
    static List<Node> nearestNeighbor(List<Node> nodes) {
        List<Node> tour = new ArrayList<>();
        Node start = nodes.remove(0);
        tour.add(start);

        while (!nodes.isEmpty()) {
            Node last = tour.get(tour.size() - 1);
            Node nearest = getNearestNode(nodes,last);
            tour.add(nearest);
            nodes.remove(nearest);
        }

        return tour;
    }

    // Cheapest Insertion Heuristic
    static List<Node> cheapestInsertion(List<Node> nodes) {
        List<Node> tour = new ArrayList<>();
        Node start = nodes.remove(0);
        tour.add(start);

        while (!nodes.isEmpty()) {
            Node toInsert = nodes.get(0);
            int bestPosition = 0;
            double LeastIncrease = Double.MAX_VALUE;

            for (int i = 0; i < tour.size(); i++) {
                Node current = tour.get(i);
                Node next = tour.get((i + 1) % tour.size());
                double increase = current.distanceTo(toInsert) + toInsert.distanceTo(next) - current.distanceTo(next);

                if (increase < LeastIncrease) {
                    LeastIncrease = increase;
                    bestPosition = i + 1;
                }
            }

            tour.add(bestPosition, toInsert);
            nodes.remove(toInsert);
        }

        return tour;
    }

    //Random Insertion Heuristic
    static List<Node> randomInsertion(List<Node> nodes) {
        List<Node> tour = new ArrayList<>();
        Random rand = new Random();
        Node start = nodes.remove(0);
        tour.add(start);

        while (!nodes.isEmpty()) {
            int randomIndex = rand.nextInt(nodes.size());
            Node nodeToAdd = nodes.get(randomIndex);
            int insertPosition = rand.nextInt(tour.size() + 1);
            tour.add(insertPosition, nodeToAdd);
            nodes.remove(nodeToAdd);
        }

        return tour;
    }

    //Semi-greedy Cheapest Insertion Heuristic
    static List<Node> semiGreedyCheapestInsertion(List<Node> nodes, int k) {
        List<Node> tour = new ArrayList<>();
        Node start = nodes.remove(0);
        tour.add(start);

        while (!nodes.isEmpty()) {
            Node toInsert = nodes.get(0);
            List<InsertionCost> costs = new ArrayList<>();

            for (int i = 0; i < tour.size(); i++) {
                Node current = tour.get(i);
                Node next = tour.get((i + 1) % tour.size());
                double cost = current.distanceTo(toInsert) + toInsert.distanceTo(next) - current.distanceTo(next);
                costs.add(new InsertionCost(i + 1, cost));
            }
            costs.sort((a, b) -> Double.compare(a.cost, b.cost));
            Random random = new Random();
            InsertionCost bestInsertion = costs.get(random.nextInt(Math.min(k, costs.size())));
            tour.add(bestInsertion.position, toInsert);
            nodes.remove(toInsert);
        }

        return tour;
    }

    //2-opt improvement algorithm
    static List<Node> twoOpt(List<Node> tour) {
        int n = tour.size();
        boolean improvement = true;

        while (improvement) {
            improvement = false;
            for (int i = 1; i < n - 1; i++) {
                for (int j = i + 1; j < n; j++) {
                    if (j - i == 1) continue;

                    List<Node> newTour = new ArrayList<>(tour);
                    Collections.reverse(newTour.subList(i, j));

                    double currentDistance = calculateTourDistance(tour);
                    double newDistance = calculateTourDistance(newTour);

                    if (newDistance < currentDistance) {
                        tour = newTour;
                        improvement = true;
                    }
                }
            }
        }
        return tour;
    }


    //Node-swap improvement algorithm
    static List<Node> nodeSwap(List<Node> tour) {
        int n = tour.size();
        boolean improvement = true;

        while (improvement) {
            improvement = false;
            double currentDistance = calculateTourDistance(tour);

            for (int i = 1; i < n - 1; i++) {
                for (int j = i + 1; j < n - 1; j++) {
                    Collections.swap(tour, i, j);
                    double newDistance = calculateTourDistance(tour);

                    if (newDistance < currentDistance) {
                        currentDistance = newDistance;
                        improvement = true;
                    } else {
                        Collections.swap(tour, i, j);
                    }
                }
            }
        }
        return tour;
    }


    // Node-Shift improvement algorithm
    public static List<Node> nodeShift(List<Node> tour) {
        int n = tour.size();
        boolean improvement = true;

        while (improvement) {
            improvement = false;

            for (int i = 1; i < n - 1; i++) {
                Node nodeToShift = tour.get(i);
                double bestDistance = calculateTourDistance(tour);
                int bestPosition = i;

                for (int j = 1; j < n - 1; j++) {
                    if (j == i) continue;

                    tour.remove(i);
                    tour.add(j, nodeToShift);
                    double newDistance = calculateTourDistance(tour);

                    if (newDistance < bestDistance) {
                        bestDistance = newDistance;
                        bestPosition = j;
                        improvement = true;
                    }

                    tour.remove(j);
                    tour.add(i, nodeToShift);
                }

                if (bestPosition != i) {
                    tour.remove(i);
                    tour.add(bestPosition, nodeToShift);
                }
            }
        }

        return tour;
    }


}
