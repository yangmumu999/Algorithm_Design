import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

public class project424 {
    public static class Vertex {
        public int level;
        public ArrayList<Integer> path;
        public int bound;
        public int vertexID;
        public Vertex(int vertexID){
            this.vertexID = vertexID;
            path = new ArrayList<>();
        }
    }


    public static class Node {
        public int NodeID;
        public int ConnectedNodeID;
        public int Distance;

        public Node(int NodeID, int ConnectedNodeID, int Distance) {
            this.NodeID = NodeID;
            this.ConnectedNodeID = ConnectedNodeID;
            this.Distance = Distance;
        }
    }

    public static void main(String[] args) {
        ArrayList<Node> nodes = readNodesFromCSV("/Users/linyang/IdeaProjects/project3/src/Project 4_Problem 2_InputData.csv");
        int inFinite = Integer.MAX_VALUE / 10;
        int m = nodes.size();
        int N = nodes.get(m - 1).NodeID + 1;
        int lowerTri = N * (N - 1) / 2;
        int[] oneDimensionalArray = new int[lowerTri];

        Arrays.fill(oneDimensionalArray, inFinite);
        for(Node node: nodes){
            int a = node.NodeID;
            int b = node.ConnectedNodeID;
            int c = node.Distance;
            if(a > b){
                int d = a * (a - 1) / 2 + b;
                oneDimensionalArray[d] = c;
            }
        }

        PriorityQueue<Vertex> PQ = new PriorityQueue<>(new Comparator<Vertex>() {
            @Override
            public int compare(Vertex o1, Vertex o2) {
                return o1.bound - o2.bound;
            }
        });

        ArrayList<Integer> optTour = new ArrayList<>();
        Vertex v = new Vertex(0);
        v.level = 0;
        v.path.add(v.vertexID);
        v.bound = bound(v, oneDimensionalArray, N);
        int minLength = Integer.MAX_VALUE;
        PQ.offer(v);
        while(!PQ.isEmpty()){
            Vertex flag = PQ.poll();
            if(flag.bound < minLength){
                for(int i = 1; i < N; i++){
                    if(!flag.path.contains(i)){
                        Vertex u = new Vertex(i);
                        u.level = flag.level + 1;
                        u.path.addAll(flag.path);
                        u.path.add(i);
                        if(u.level == N - 2){
                            for(int j = 1; j < N; j++){
                                if(!u.path.contains(j)){
                                    u.path.add(j);
                                    u.path.add(0);
                                    break;
                                }
                            }
                            int length = length(u, oneDimensionalArray);
                            if(length < minLength){
                                minLength = length;
                                optTour.clear();
                                optTour.addAll(u.path);
                            }
                        } else {
                            u.bound = bound(u, oneDimensionalArray, N);
                            PQ.offer(u);
                        }
                    }
                }
            }
        }

        System.out.println("NodeID\t\tConnectedNodeID\t\tDistance");
        int length = optTour.size();
        int sum = 0;
        for(int i = 0; i < length - 1; i++){
            int a = optTour.get(i);
            int b = optTour.get(i + 1);
            System.out.printf("%-12d", a);
            System.out.printf("%-20d", b);
            int index;
            if(a >= b){
                index = a * (a - 1) / 2 + b;
            } else {
                index = b * (b - 1) / 2 + a;
            }
            System.out.printf("%-12d", oneDimensionalArray[index]);
            sum += oneDimensionalArray[index];
            System.out.println();
        }
        System.out.println("The minimum distance for the salesperson to travel is " + sum);
    }

    private static int length(Vertex vertex, int[] oneDimensionalArray) {
        int length = 0;
        int n = vertex.path.size();
        for(int i = 0; i < n - 1; i++){
            int a = vertex.path.get(i);
            int b = vertex.path.get(i + 1);
            int index;
            if(a >= b){
                index = a * (a - 1) / 2 + b;
            } else {
                index = b * (b - 1) / 2 + a;
            }
            length += oneDimensionalArray[index];
        }
        return length;
    }

    public static int bound(Vertex vertex, int[] oneDimensionalArray, int N) {
        int bound = 0;
        int n = vertex.path.size();
        for(int i = 0; i < n - 1; i++){
            int a = vertex.path.get(i);
            int b = vertex.path.get(i + 1);
            int index;
            if(a >= b){
                index = a * (a - 1) / 2 + b;
            } else {
                index = b * (b - 1) / 2 + a;
            }
            bound += oneDimensionalArray[index];
        }
        int min;

        for(int i = 0; i < N; i++){
            if(vertex.path.contains(i) && i != vertex.path.get(n - 1)) continue;
            boolean flag = i == vertex.path.get(n - 1);
            min = Integer.MAX_VALUE;
            if(flag){
                for(int j = 0; j < N; j++){
                    if(vertex.path.contains(j) || i == j) continue;
                    int index;
                    if(i >= j){
                        index = i * (i - 1) / 2 + j;
                    } else {
                        index = j * (j - 1) / 2 + i;
                    }
                    if(oneDimensionalArray[index] < min){
                        min = oneDimensionalArray[index];
                    }
                }
            } else {
                for(int j = 0; j < N; j++){
                    if((vertex.path.contains(j) && j != 0) || i == j) continue;
                    int index;
                    if(i >= j){
                        index = i * (i - 1) / 2 + j;
                    } else {
                        index = j * (j - 1) / 2 + i;
                    }
                    if(oneDimensionalArray[index] < min){
                        min = oneDimensionalArray[index];
                    }
                }
            }
            bound += min;
        }

        return bound;
    }

    public static ArrayList<Node> readNodesFromCSV(String fileName) {
        ArrayList<Node> nodes = new ArrayList<>();
        Path pathToFile = Paths.get(fileName);

        // create an instance of BufferedReader
        // using try with resource, Java 7 feature to close resources
        try (BufferedReader br = Files.newBufferedReader(pathToFile, StandardCharsets.US_ASCII)) {

            // read the first line from the text file
            String line = br.readLine();
            line = br.readLine();

            // loop until all lines are read
            while (line != null) {
                // use string.split to load a string array with the values from
                // each line of
                // the file, using a comma as the delimiter
                String[] attributes = line.split(",");

                Node node = createNode(attributes);

                // adding book into ArrayList
                nodes.add(node);

                // read next line before looping
                //if end of file reached, line would be null
                line = br.readLine();
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return nodes;
    }

    public static Node createNode(String[] metadata) {
        int NodeID = Integer.parseInt(metadata[0]);
        int ConnectedNodeID = Integer.parseInt(metadata[1]);
        int Distance = Integer.parseInt(metadata[2]);

        return new Node(NodeID, ConnectedNodeID, Distance);
    }
}