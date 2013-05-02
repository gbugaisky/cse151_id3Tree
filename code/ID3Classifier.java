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
            System.out.println("Predict " + (int)prev[4]);
            return current;
        }
        
        //ArrayLists that contain copies of the data.
        ArrayList<float[]> attributeA = sortList(currentList, 0);
        ArrayList<float[]> attributeB = sortList(currentList, 1);
        ArrayList<float[]> attributeC = sortList(currentList, 2);
        ArrayList<float[]> attributeD = sortList(currentList, 3);
        
        //0. Declare variables for the best thresholds in each axis and the corresponding entropy
        //1. for each of the priority queues (aka each axis), get it. the first is the gain, the second is the threshold for that
        float[] igThresA = bestIG(attributeA, 0);
        float[] igThresB = bestIG(attributeB, 1);
        float[] igThresC = bestIG(attributeC, 2);
        float[] igThresD = bestIG(attributeD, 3);

        //2. Compare each information gain
        ArrayList<float[]> dataToSplit;
        float threshold;
        int axis = 0;
        float IG = igThresA[0];
        dataToSplit = attributeA;
        threshold = igThresA[1];

        if (IG < igThresB[0])
        {
            axis = 1;
            IG = igThresB[0];
            dataToSplit = attributeB;
            threshold = igThresB[1];
        }

        if (IG < igThresC[0])
        {
            axis = 2;
            IG = igThresC[0];
            dataToSplit = attributeC;
            threshold = igThresC[1];
        }

        if (IG < igThresD[0])
        {
            axis = 3;
            IG = igThresD[0];
            dataToSplit = attributeD;
            threshold = igThresD[1];
        }

        //3. Get the list that matches the axis we want

        //System.out.println("At " + threshold + " outside is " + dataToSplit.get((int)threshold)[axis]);
        //4. Create two lists based on the threshold, one list being less than or equal to it and one being greater than it
        ArrayList lowOrEqual = new ArrayList(dataToSplit.subList(0, (int)threshold+1));
        ArrayList greaterThan = new ArrayList(dataToSplit.subList((int)threshold+1, dataToSplit.size()));

        //5. Load the threshold and axis into the node we were passed as an argument
        current.threshold = dataToSplit.get((int)threshold)[axis];
        current.feature = axis;

        //6. Create the two child nodes,
        current.lChild = classifier.new Node();
        current.rChild = classifier.new Node();

        //7. make the recursive calls, interlacing them with the prints
        System.out.println("If Feature " + (current.feature+1) + " <= " + dataToSplit.get((int)threshold)[axis] + ":");
        tabs = tabs + "\t";
        System.out.println(tabs + "------> Yes: ");
        current.lChild = makeRule(current.lChild, lowOrEqual); //one priority queue
        System.out.println(tabs + "------>  No: ");
        current.rChild = makeRule(current.rChild, greaterThan);//one priority queue
        if (tabs.length() > 1) tabs = tabs.substring(0, tabs.length()-1);
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


    private static float[] bestIG(ArrayList<float[]> dataRange, int feature)
    {
        //A. For each pair of adjacent data points (say the current and the previous data point)
            //A1. Get the threshold
            //A2. compute the entropies of the subgroups created
            //A3. Get the information gain from the entropies 
            //A4. If the new information gain is larger than the current record information gain, then replace the current record information gain and threshold
        double threshold = 0.0;
        double infoGain = -0.1; //IG cannot be < 0.  Initialize to slighly less to force first IG to be assigned to this.
        int place = 0;
        ArrayList<float[]> dataList = dataRange;

        for (int i = 0; i < dataList.size() - 2; i++)
        {
            int count1L = 0;
            int count2L = 0;
            int count3L = 0;
            int j = 0;
            for (; j <= i; j++)
            {
                int currLab = (int)dataList.get(j)[feature];
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
                int currLab = (int)dataList.get(j)[feature];
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
            
            double log1, log2, log3;
            if (pr1L == 0)
                log1 = 0;
            else
                log1 = Math.log(pr1L);
            if (pr2L == 0)
                log2 = 0;
            else
                log2 = Math.log(pr2L);
            if (pr3L == 0)
                log3 = 0;
            else
                log3 = Math.log(pr3L);

            double entropyL = -(pr1L * log1) - (pr2L * log2) - (pr3L * log3);
            
            if (pr1R == 0)
                log1 = 0;
            else
                log1 = Math.log(pr1R);
            if (pr2R == 0)
                log2 = 0;
            else
                log2 = Math.log(pr2R);
            if (pr3R == 0)
                log3 = 0;
            else
                log3 = Math.log(pr3R);

            double entropyR = -(pr1R * log1) - (pr2R * log2) - (pr3R * log3);

            double condEntro = (prL * entropyL) + (prR * entropyR);

            //get normal entropy
            double pr1 = (count1L + count1R) / (double)(dataList.size());
            double pr2 = (count2L + count2R) / (double)(dataList.size());
            double pr3 = (count3L + count3R) / (double)(dataList.size());

            if (pr1 == 0)
                log1 = 0;
            else
                log1 = Math.log(pr1);
            if (pr2 == 0)
                log2 = 0;
            else
                log2 = Math.log(pr2);
            if (pr3 == 0)
                log3 = 0;
            else
                log3 = Math.log(pr3);

            double origEntro = -(pr1 * log1) - (pr2 * log2) - (pr3 * log3);

            //get temporary information gain
            double tempInfoGain = origEntro - condEntro;

            //System.out.println("tempInfoGain: " + tempInfoGain);
            //System.out.println("  dataList.size(): " + dataList.size() + ", prR: " + prR); 
            if (tempInfoGain > infoGain)
            {
                infoGain = tempInfoGain;
                threshold = dataList.get(i)[feature];              //now this is the index of the array that gives us the best,
                                            // rather than the actual threshold. This steamlines the thresholding process
                place = i;
            }
        }

        System.out.println(tabs+'\t'+"Inside: place = " + place + ", threshold = " + threshold + ", IG = " +infoGain);
        return (new float[]{(float)(infoGain), (float)(place)});

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
