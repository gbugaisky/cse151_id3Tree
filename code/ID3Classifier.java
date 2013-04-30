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
        PriorityQueue<float> attributeA = new PriorityQueue();
        PriorityQueue<float> attributeB = new PriorityQueue();
        PriorityQueue<float> attributeC = new PriorityQueue();
        PriorityQueue<float> attributeD = new PriorityQueue();


        //first, check if the current node is pure
        int flag = 0;
        ListIterator iter = currentList.ListIterator();
        float[] prev = iter.next();
        attributeA.add(prev[0]);
        attributeB.add(prev[1]);
        attributeC.add(prev[2]);
        while(iter.hasNext())
        {
            float[] cur = iter.next();
            if (cur[4] != prev[4])
            {
                flag = 1;
                break;
            }
            prev = cur;
        }

        if (flag == 0)
        {
            current.feature = null;
            current.threshold = null;
            return current;
        }

        float[] 
    }

    public class Node
    {
        public int feature;
        public float threshold;
        public Node lChild;
        public Node rChild;
    }
}