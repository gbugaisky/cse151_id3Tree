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
    static ArrayList<float[]> dataList;
    static Node root;
    static ID3Classifier classifier;
    static String tabs;

    public static void main(String[] args)
    {
        //Open the file, read the data
        File file = new File("../hw3train.txt");
        dataList = readFile(file);

        classifier = new ID3Classifier();
        root = classifier.new Node();

        tabs = "";
        root = makeRule(root, dataList);

    }
    
    private static ArrayList<float[]> readFile(File fileFrom)
    {
        ArrayList<float[]> result = new ArrayList<float[]>();
        try {

            BufferedReader br = new BufferedReader( new FileReader ( fileFrom ) );
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

    private static Node makeRule(Node current, ArrayList<float[]> currentList)
    {
        //Very first, check if the current node is pure
        int flag = 0;
        float[] prev = currentList.get(0);

        for(int i = 1; i < currentList.size(); i++)
        {
            float[] cur = currentList.get(i);
            if (cur[4] != prev[4])
            {
                flag = 1;
            }
            prev = cur;
        }

        //If it's pure, return
        if (flag == 0)
        {
            return current;
        }
        
        //ArrayLists that contain copies of the data.
        ArrayList<float[]> attributeA = sortList(currentList, 0);
        ArrayList<float[]> attributeB = sortList(currentList, 1);
        ArrayList<float[]> attributeC = sortList(currentList, 2);
        ArrayList<float[]> attributeD = sortList(currentList, 3);
        
        //0. Declare variables for the best thresholds in each axis and the corresponding entropy
        //1. for each of the priority queues (aka each axis), get it. the first is the gain, the second is the threshold for that
        float[] igThresA = bestIG(attributeA);
        float[] igThresB = bestIG(attributeB);
        float[] igThresC = bestIG(attributeC);
        float[] igThresD = bestIG(attributeD);

        //2. Compare each information gain
        int axis = 0;
        float IG = igThresA[0];

        if (IG < igThresB[0])
        {
            axis = 1;
            IG = igThresB[0];
        }

        if (IG < igThresC[0])
        {
            axis = 2;
            IG = igThresC[0];
        }

        if (IG < igThresD[0])
        {
            axis = 3;
            IG = igThresD[0];
        }

        //3. Get the list that matches the axis we want

        ArrayList<float[]> dataToSplit;
        float threshold;
        switch(axis)
        {
            case 1:
                dataToSplit = attributeA;
                threshold = igThresA[1];
                break;
            case 2:
                dataToSplit = attributeB;
                threshold = igThresB[1];
                break;
            case 3:
                dataToSplit = attributeC;
                threshold = igThresC[1];
                break;
            default:
                dataToSplit = attributeD;
                threshold = igThresD[1];
        }
        
        //4. Create two lists based on the threshold, one list being less than or equal to it and one being greater than it
        ArrayList lowOrEqual = new ArrayList(dataToSplit.subList(0, (int)threshold));
        ArrayList greaterThan = new ArrayList(dataToSplit.subList((int)threshold, dataToSplit.size()));

        //5. Load the threshold and axis into the node we were passed as an argument
        current.threshold = dataToSplit.get((int)threshold)[axis];
        current.feature = axis;

        //6. Create the two child nodes,
        current.lChild = classifier.new Node();
        current.rChild = classifier.new Node();

        //7. make the recursive calls, interlacing them with the prints
        System.out.println(tabs + "If Feature " + current.feature + " <= " + threshold + ":");
        tabs = tabs + "\t";
        System.out.print(tabs + "------> Yes: ");
        current.lChild = makeRule(current.lChild, lowOrEqual); //one priority queue
        System.out.print(tabs + "------>  No: ");
        current.rChild = makeRule(current.rChild, greaterThan);//one priority queue
        tabs = tabs.substring(0, tabs.length()-2);
        //8. Return the node we were passed.
        return current;

    }
   
    private static ArrayList<float[]> sortList(ArrayList<float[]> list, int feature)
    {
        //1. if the array's length is <= 1, it's sorted
        if (list.size() <= 1)
            return list;

        float[] pivotEl = list.get(list.size()/2);
        list.remove(list.size()/2);

        ArrayList less = new ArrayList();
        ArrayList greater = new ArrayList();

        for(int i = 0; i < list.size(); i++)
        {
            if (list.get(i)[feature] <= pivotEl[feature])
            {
                less.add(list.get(i));
            } else {
                greater.add(list.get(i));
            }

        }

        //the recursive call
        less = sortList(less, feature);
        greater = sortList(greater, feature);

        //our version of concatenating the lists
        less.add(pivotEl);

        for(int j = 0; j < greater.size(); j++)
        {
            less.add(greater.get(j));
        }

        return less;

    }


    private static float[] bestIG(ArrayList<float[]> dataRange)
    {
        //A. For each pair of adjacent data points (say the current and the previous data point)
            //A1. Get the threshold
            //A2. compute the entropies of the subgroups created
            //A3. Get the information gain from the entropies 
            //A4. If the new information gain is larger than the current record information gain, then replace the current record information gain and threshold
        double threshold = 0.0;
        double infoGain = -0.1; //IG cannot be < 0.  Initialize to slighly less to force first IG to be assigned to this.
        ArrayList<float[]> dataList = dataRange;

        for (int i = 0; i < dataList.size() - 1; i++)
        {
            int count1L = 0;
            int count2L = 0;
            int count3L = 0;
            int j = 0;
            for (; j <= i; j++)
            {
                int currLab = (int)dataList.get(j)[1];
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
                int currLab = (int)dataList.get(j)[1];
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
            double prL = (count1L + count2L + count3L) / (double)(dataList.size());
            double prR = (count1R + count2R + count3R) / (double)(dataList.size());

            double pr1L = (count1L / (double)(dataList.size())) / prL;
            double pr2L = (count2L / (double)(dataList.size())) / prL;
            double pr3L = (count3L / (double)(dataList.size())) / prL;

            double pr1R = (count1R / (double)(dataList.size())) / prR;
            double pr2R = (count2R / (double)(dataList.size())) / prR;
            double pr3R = (count3R / (double)(dataList.size())) / prR;

            double entropyL = -(pr1L * Math.log(pr1L)) - (pr2L * Math.log(pr2L)) - (pr3L * Math.log(pr3L));
            double entropyR = -(pr1R * Math.log(pr1R)) - (pr2R * Math.log(pr2R)) - (pr3R * Math.log(pr3R));

            double condEntro = (prL * entropyL) + (prR * entropyR);

            //get normal entropy
            double pr1 = (count1L + count1R) / (double)(dataList.size());
            double pr2 = (count2L + count2R) / (double)(dataList.size());
            double pr3 = (count3L + count3R) / (double)(dataList.size());
            double origEntro = -(pr1 * Math.log(pr1)) - (pr2 * Math.log(pr2)) - (pr3 * Math.log(pr3));

            //get temporary information gain
            double tempInfoGain = origEntro - condEntro;

            if (tempInfoGain > infoGain)
            {
                infoGain = tempInfoGain;
                threshold = i;              //now this is the index of the array that gives us the best,
                                            // rather than the actual threshold. This steamlines the thresholding process
            }
        }

        return (new float[]{(float)(infoGain), (float)(threshold)});

    }

    public class Node
    {
        public int feature;
        public float threshold;
        public Node lChild;
        public Node rChild;

        public Node()
        {
            feature = -1;
            threshold = -1;
            lChild = null;
            rChild = null;
        }


    }
}
