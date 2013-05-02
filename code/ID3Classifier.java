import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
        
        
        float[] igThresA = bestIG(attributeA);
        float[] igThresB = bestIG(attributeB);
        float[] igThresC = bestIG(attributeC);
        float[] igThresD = bestIG(attributeD);

        

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

    private static float[] bestIG(PriorityQueue<float[]> dataRange)
    {
        double threshold = 0.0;
        double infoGain = -0.1; //IG cannot be < 0.  Initialize to slighly less to force first IG to be assigned to this.
        List dataList = new ArrayList();

        // convert PQ to AL
        while (!(dataRange.empty()))
        {
            dataList.add(dataRange.poll());
        }

        double tempThresh = 0.0;
        for (int i = 0; i < dataList.size() - 1; i++)
        {
            tempThresh = dataList.get(i)[0];
            int count1L = 0;
            int count2L = 0;
            int count3L = 0;
            int j = 0;
            for (; j <= i; j++)
            {
                int currLab = dataList.get(j)[1];
                if (currLab == 1)
                {
                    count1L++;
                }
                else if (currLab == 2)
                {
                    count2L++;
                }
                else { count3L++; }
            }

            int count1R = 0;
            int count2R = 0;
            int count3R = 0;
            for (; j < dataList.size() - 1; j++)
            {
                int currLab = dataList.get(j)[1];
                if (currLab == 1)
                {
                    count1R++;
                }
                else if (currLab == 2)
                {
                    count2R++;
                }
                else { count3R++; }
            }

            //get conditional entropy
            double prL = (count1L + count2L + count3L) / double(dataList.size());
            double prR = (count1R + count2R + count3R) / double(dataList.size());

            double pr1L = (count1L / double(dataList.size())) / prL;
            double pr2L = (count2L / double(dataList.size())) / prL;
            double pr3L = (count3L / double(dataList.size())) / prL;

            double pr1R = (count1R / double(dataList.size())) / prR;
            double pr2R = (count2R / double(dataList.size())) / prR;
            double pr3R = (count3R / double(dataList.size())) / prR;

            double entropyL = -(pr1L * Math.log(pr1L)) - (pr2L * Math.log(pr2L)) - (pr3L * Math.log(pr3L));
            double entropyR = -(pr1R * Math.log(pr1R)) - (pr2R * Math.log(pr2R)) - (pr3R * Math.log(pr3R));

            double condEntro = (prL * entropyL) + (prR * entropyR);

            //get normal entropy
            double pr1 = (count1L + count1R) / double(dataList.size());
            double pr2 = (count2L + count2R) / double(dataList.size());
            double pr3 = (count3L + count3R) / double(dataList.size());
            double origEntro = -(pr1 * Math.log(pr1)) - (pr2 * Math.log(pr2)) - (pr3 * Math.log(pr3));

            //get temporary information gain
            double tempInfoGain = origEntro - condEntro;

            if (tempInfoGain > infoGain)
            {
                infoGain = tempInfoGain;
                threshold = tempThresh;
            }
        }

        return (new float[]{float(infoGain), float(threshold)});

    }

    public class Node
    {
        public int feature;
        public float threshold;
        public Node lChild;
        public Node rChild;
    }
}
