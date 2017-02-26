import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class HananExactNd {
    private static HashSet<Vertex> currentVertex = new HashSet<Vertex>(){{
        add(new Vertex(2, 2, 4));
        add(new Vertex(3, 4, 2));
        add(new Vertex(4, 5, 3));
//        add(new Vertex(5, 6));
//        add(new Vertex(6, 2));
    }};


    private static HashSet<HashSet<Vertex>> powerSet (HashSet<Vertex> originalSet) {
        HashSet<HashSet<Vertex>> sets = new HashSet<>();
//        System.out.println(originalSet.size());
//        System.exit(0);

        List<Vertex> originalList = new ArrayList<>(originalSet);
        for(int i = 0; i < Math.pow(2, originalSet.size()); i++) {
            System.out.println(i);
            HashSet<Vertex> currentSet = new HashSet<>();
            int code = i;

            for (int j = 0; j < originalSet.size(); j++) {//convert to base currentVertex.size() and get the corresponding permutation
                int config = code % 2;
                code = code / 2;
                if (config == 0) currentSet.add(originalList.get(j));
            }

            sets.add(currentSet);
        }
        return sets;
    }


    /** Generating a set of vertices in the Hanan's grid (i.e. union of intersections of all d-hyperplanes, each passing through
     * an observed point and parallel to exactly 1 axis), modelled by Hanan's theorem.
     */
    private static HashSet<Vertex> makeGrid() {
        //Set containing all points in the Hanan grid
        HashSet<Vertex> hananGrid = new HashSet<>();

        //Get dimension of the dataset
        int dim = 0;
        for (Vertex v : currentVertex) {
            dim = v.dim;
            break;
        }

        //List containing lists of possible permutation choice in each dimension
        ArrayList<ArrayList<Integer>> masterSet = new ArrayList<>(dim);
        for (int i = 0; i < dim; i++) {

            //Loop through each data point and collect the i-th dimension coordinate
            ArrayList<Integer> minorSet = new ArrayList<>();
            for (Vertex v : currentVertex) minorSet.add(v.tuple[i]);

            masterSet.add(minorSet);
        }

        //Adding objects to Hanan grid
        for (int i = 0; i < Math.pow(currentVertex.size(), dim); i++) {//loop through the entire search space

            //Mapping an integer from [currentVertex.size()^dim] to a point in the Hanan grid
            int[] currentTuple = new int[dim];
            int code = i;

            for (int j = 0; j < dim; j++) {//convert to base currentVertex.size() and get the corresponding permutation
                int config = code % currentVertex.size();
                code = code / (currentVertex.size());
                currentTuple[j] = masterSet.get(j).get(config);
            }

            //Check whether the point is observed or has already been collected
            boolean repeated = false;
            boolean observed = false;
            Vertex currentV = new Vertex(currentTuple);

            for (Vertex v : hananGrid) if (v.equals(currentV)) repeated = true;
            for (Vertex v : currentVertex) if (v.equals(currentV)) observed = true;

            //Add new points
            if (!observed && !repeated) hananGrid.add(currentV);
        }
        return hananGrid;
    }

    /**Method to return the upper bound of tree score by finding the largest tree possible (consist of all points on the grid)
     * and compute its MST weight.
     *
     * @return  upper bound of tree scores
     */
    private static int findMaxScore(HashSet<HashSet<Vertex>> hananPowerSet) {
        HashSet<Vertex> maxScoreSet = new HashSet<>(0);

        //Find the full set
        for (HashSet<Vertex> set : hananPowerSet) if (set.size() > maxScoreSet.size()) maxScoreSet = set;
        maxScoreSet.addAll(currentVertex);

        //Create the full tree and return its weight
        MST t = new MST();
        int[][] maxGraph = distanceMatrix(maxScoreSet);

        return (t.primMST(maxGraph, maxScoreSet.size()));
    }

    /**Method to return an adjacency matrix from the set of vertices in n-dimension, with rectilinear metric.
     *
     * @param set   set of vertices
     * @return      adjacency matrix describing the full tree
     */
    private static int[][] distanceMatrix(HashSet<Vertex> set) {
        int[][] graph = new int[set.size()][set.size()];

        int row = 0;
        for (Vertex i : set) {
            int col = 0;
            for (Vertex j : set) {
                int distance = 0;
                if (!i.equals(j)) {//if on the diagonal then the value is 0
                    for (int counter = 0; counter < i.dim; counter++){//rectilinear metric calculation
                        distance += Math.abs(i.tuple[counter] - j.tuple[counter]);
                    }
                }
                graph[row][col] = distance;
                col++;
                System.out.print(distance + "  ");

            }
            System.out.println("");
            row++;
        }

        return graph;
    }


    public static void main (String[] args) {
        long startTime = System.currentTimeMillis();

        //Initialize the Hanan grid
        HashSet<Vertex> hananGrid = makeGrid();

        //Initialize possible set of vertices
        HashSet<HashSet<Vertex>> hananPowerSet = powerSet(hananGrid);

        //Collect density
        int maxScore = findMaxScore(hananPowerSet);
        int[] scoreDensity = new int[maxScore + 1];
        for (HashSet<Vertex> set : hananPowerSet) {
            set.addAll(currentVertex);
            int[][] graph = distanceMatrix(set);
            MST t = new MST();
            scoreDensity[t.primMST(graph, set.size())]++;
        }

        for (int i = 0; i < scoreDensity.length; i++) {
            if (scoreDensity[i] != 0) System.out.println(i + " : " + scoreDensity[i]);
        }
        long endTime = System.currentTimeMillis();

        System.out.println("That took " + (endTime - startTime) + " milliseconds");
    }
}
