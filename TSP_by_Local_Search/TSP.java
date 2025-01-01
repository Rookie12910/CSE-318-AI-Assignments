import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;

class Node {
    int id;
    double x;
    double y;

    public Node(int id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    double distanceTo(Node p) {
        return Math.sqrt(Math.pow(this.x - p.x, 2) + Math.pow(this.y - p.y, 2));
    }

    @Override
    public String toString() {
        return "Node{" + "id=" + id + ", x=" + x + ", y=" + y + '}';
    }
}

public class TSP {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String csvFileName = "tsp_report.csv";

        // Check if the file already exists
        File csvFile = new File(csvFileName);
        boolean fileExists = csvFile.exists();

        // If the file does not exist, write the header
        if (!fileExists) {
            try (FileWriter writer = new FileWriter(csvFileName)) {
                writer.write("File Name,NNH Cost,NNH (TwoOpt),NNH (NodeSwap),NNH (NodeShift),CIH Cost,CIH (TwoOpt),CIH (NodeSwap),CIH (NodeShift),RIH Cost,RIH (TwoOpt),RIH (NodeSwap),RIH (NodeShift), Semi-GreedyCIH(3),Semi-GreedyCIH(4),Semi-GreedyCIH(5)\n");
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        boolean running = true;

        while (running) {
            System.out.println("Enter the input file name (TSP format): ");
            String filePath = scanner.nextLine();

            List<Node> nodes = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;
                boolean startReading = false;

                while ((line = br.readLine()) != null) {
                    line = line.trim();

                    // Start reading coordinates after the "NODE_COORD_SECTION" line
                    if (line.equals("NODE_COORD_SECTION")) {
                        startReading = true;
                        continue;
                    }

                    if (line.equals("EOF")) {
                        break;
                    }

                    if (startReading) {
                        String[] parts = line.split("\\s+");
                        if (parts.length >= 3) {
                            int id = Integer.parseInt(parts[0]);
                            double x = Double.parseDouble(parts[1]);
                            double y = Double.parseDouble(parts[2]);
                            nodes.add(new Node(id, x, y));
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            // Compute costs for NNH
            List<Node> tourNNH = Helper.nearestNeighbor(new ArrayList<>(nodes));
            double nnhCost = Helper.calculateTourDistance(tourNNH);
            double nnhTwoOpt = Helper.calculateTourDistance(Helper.twoOpt(new ArrayList<>(tourNNH)));
            double nnhNodeSwap = Helper.calculateTourDistance(Helper.nodeSwap(new ArrayList<>(tourNNH)));
            double nnhNodeShift = Helper.calculateTourDistance(Helper.nodeShift(new ArrayList<>(tourNNH)));

            // Compute costs for CIH
            List<Node> tourCIH = Helper.cheapestInsertion(new ArrayList<>(nodes));
            double cihCost = Helper.calculateTourDistance(tourCIH);
            double cihTwoOpt = Helper.calculateTourDistance(Helper.twoOpt(new ArrayList<>(tourCIH)));
            double cihNodeSwap = Helper.calculateTourDistance(Helper.nodeSwap(new ArrayList<>(tourCIH)));
            double cihNodeShift = Helper.calculateTourDistance(Helper.nodeShift(new ArrayList<>(tourCIH)));

            // Compute costs for RIH
            List<Node> tourRIH = Helper.randomInsertion(new ArrayList<>(nodes));
            double rihCost = Helper.calculateTourDistance(tourRIH);
            double rihTwoOpt = Helper.calculateTourDistance(Helper.twoOpt(new ArrayList<>(tourRIH)));
            double rihNodeSwap = Helper.calculateTourDistance(Helper.nodeSwap(new ArrayList<>(tourRIH)));
            double rihNodeShift = Helper.calculateTourDistance(Helper.nodeShift(new ArrayList<>(tourRIH)));

            // Compute costs for semi-greedy CIH
            List<Node> tourSemiCIH_3 = Helper.semiGreedyCheapestInsertion(new ArrayList<>(nodes),3);
            double semiCih3Cost = Helper.calculateTourDistance(tourSemiCIH_3);
            List<Node> tourSemiCIH_4 = Helper.semiGreedyCheapestInsertion(new ArrayList<>(nodes),4);
            double semiCih4Cost = Helper.calculateTourDistance(tourSemiCIH_4);
            List<Node> tourSemiCIH_5 = Helper.semiGreedyCheapestInsertion(new ArrayList<>(nodes),5);
            double semiCih5Cost = Helper.calculateTourDistance(tourSemiCIH_5);

            // Append the results to the CSV file
            try (FileWriter writer = new FileWriter(csvFileName, true)) {
                writer.write(filePath + "," +
                        nnhCost + "," + nnhTwoOpt + "," + nnhNodeSwap + "," + nnhNodeShift + "," +
                        cihCost + "," + cihTwoOpt + "," + cihNodeSwap + "," + cihNodeShift + "," +
                        rihCost + "," + rihTwoOpt + "," + rihNodeSwap + "," + rihNodeShift + "," +
                        semiCih3Cost + "," + semiCih4Cost + "," + semiCih5Cost + "\n");
                System.out.println("Results for " + filePath + " appended to " + csvFileName);
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Press Q to quit or any other key to process another file...");
            String mode = scanner.nextLine();
            if (mode.equalsIgnoreCase("Q")) running = false;
        }
    }
}
