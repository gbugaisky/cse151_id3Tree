import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.PriorityQueue;

public class ID3Classifier
{
    static LinkedList<float[]> dataList;
    public static void main(String[] args)
    {
        //Open the file, read the data
        File file = new File("../hw3train.txt");
        dataList = readFile(file);

        Node root = new Node();

        root = makeRule(root, dataList);

    }
    
    
    private static LinkedList<float[]> readFile(File fileFrom)
    {
        LinkedList<float[]> result = new BufferedReader(new FileReader(fileFrom));
        try {

            BufferedReader br = new BufferedReader( new FileReader ( readFrom ) );
            String line;

            while ((line = br.readLine()) != null) {
                //1. When we read a line, we want to split it
                String[] read = line.split(" ");

                //2. then convert it to ints
                float[] data = new float[read.length];

                for (int i = 0; i < read.length; i++) 
                {
                    data[i] = Float.parseFloat(read[i]);
                }

                //3. then put inside the linked list
                result.add(data);
                
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }


    private static Node makeRule(Node current, LinkedList<float[]> currentList)
    {
        //Priority queues that will contain copies of the data.
        PriorityQueue<float[]> attributeA = new PriorityQueue
            (2,
            new Comparator<float[]> () {    //a comparator on a specific element
                public int compare(float[] a, float[] b) { 
                    return ( (float) b[0]).compareTo(a[0]); //forget which order this makes,
                                                            //or if order matters here
                }
            }
            );
        PriorityQueue<float[]> attributeB = new PriorityQueue
            (2,
            new Comparator<float[]> () {
                public int compare(float[] a, float[] b) {
                    return ( (float) b[0]).compareTo(a[0]);
                }
            }
            );
        PriorityQueue<float[]> attributeC = new PriorityQueue
            (2,
            new Comparator<float[]> () {
                public int compare(float[] a, float[] b) {
                    return ( (float) b[0]).compareTo(a[0]);
                }
            }
            );
        PriorityQueue<float[]> attributeD = new PriorityQueue
            (2,
            new Comparator<float[]> () {
                public int compare(float[] a, float[] b) {
                    return ( (float) b[0]).compareTo(a[0]);
                }
            }
            );


        //first, check if the current node is pure
        int flag = 0;
        ListIterator iter = currentList.ListIterator();
        float[] prev = iter.next();
        attributeA.add(new float[]{prev[0], prev[4]});
        attributeB.add(new float[]{prev[1], prev[4]});
        attributeC.add(new float[]{prev[2], prev[4]});
        attributeD.add(new float[]{prev[3], prev[4]});

        while(iter.hasNext())
        {
            float[] cur = iter.next();
            if (cur[4] != prev[4])
            {
                flag = 1;
            }
            prev = cur;
            attributeA.add(new float[]{prev[0], prev[4]});
            attributeB.add(new float[]{prev[1], prev[4]});
            attributeC.add(new float[]{prev[2], prev[4]});
            attributeD.add(new float[]{prev[3], prev[4]});
        }

        if (flag == 0)
        {
            current.feature = null;
            current.threshold = null;
            return current;
        }
        
        
        

        //else we have to split them. Fortunately we've already loaded all of our datapoints by order

        //0. Declare variables for the best thresholds in each axis and the corresponding entropy
        //1. for each of the priority queues (aka each axis)
            //A. For each pair of adjacent data points (say the current and the previous data point)
                //A1. Get the threshold
                //A2. compute the entropies of the subgroups created
                //A3. Get the information gain from the entropies 
                //A4. If the new information gain is larger than the current record information gain, then replace the current record information gain and threshold
        //2. Compare each information gain
        //3. Create two priority queues, one for each node
        //4. For the priority queue which is associated witht the axis of the best information gain
            //A. Compare an element with the threshold
            //B. Put it in the appropriate threshold

        //5. Load the threshold and axis into the node we were passed as an argument
        //6. Create the two child nodes,
        Node childNode1 = Node();
        Node childNode2 = Node();

        //7. make the recursive call
        childNode1 = makeRule(childNode1, //one priority queue
        childNode2 = makeRule(childNode2, //one priority queue

        //8. Assign these child nodes to the node we were passed

        //9. Return the node we were passed.

    }

    public class Node
    {
        public int feature;
        public float threshold;
        public Node lChild;
        public Node rChild;
    }
}
