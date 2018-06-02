import java.io.*;
import java.util.*;
import java.util.Map.Entry;


public class SortedSetTest {

	//Holds minimum support required
	static int min_support = 0;
	
	//Holds number of transactions in dataset
	static int count = 0;
	
	//File Handle to write frequent Patterns to output file
	static Writer writer;
	
	//Contains unique values and their support
	static TreeMap<String, Integer> uniqueItems = new TreeMap<String, Integer>();
	
	//Unique value to index mapping
	static TreeMap<String, Integer> ItemIndex = new TreeMap<String, Integer>();
	
	//index to unique values mapping
	static TreeMap<Integer, String> reverseItemIndex = new TreeMap<Integer, String>();
	
	//Contains the heading values of dataset
	static String headings[];
	
	//Contains Data
	static List<ArrayList<String>> data =new ArrayList<ArrayList<String>>();
	
	//Contains Encoded data
	static List<ArrayList<Integer>> dataInt =new ArrayList<ArrayList<Integer>>();
	
	//Holds execution start time
	static long startTime = 0;
	
	//Holds execution end time
	static long stopTime = 0;
	
	//Holds number of rules/patterns generated
	static int rulesCount=0;
	
	/*
	 * METHOD
	 * Build FP Tree with recursion call
	 * 
	 * ARGUMENTS: 
	 * pos	:	Column number on which we are working
	 * list	:	Arraylist holding subtransactions
	 * node	:	Contains reference to parent node
	 */
	static void buildFP(int pos, List<ArrayList<Integer>> list, Node parent)
	{
		if(list.get(0).size() <= pos)
		{
			return;
		}
		
		int previousIndex = list.get(0).get(pos); 
		
		List<ArrayList<Integer>> SubList = new ArrayList<ArrayList<Integer>>();
		
		int count = 0;
		for(ArrayList<Integer> row : list)
		{
			//System.out.println(row);
			if( (row.size() <= pos) || (row.get(pos) != previousIndex) )
			{
				Node node = new Node(previousIndex, count, parent);

				if (ItemListMap.containsKey(previousIndex))
				{
					//System.out.println("Check"+pos+"-"+parent.item+"-"+previousIndex+"-"+ItemListMap.get(previousIndex).count+"-"+node.count+"-"+ItemListMap.get(previousIndex).list.size());
					ItemListMap.get(previousIndex).addNode(node); 
				}
				else
				{
					ItemList value = new ItemList(previousIndex, count, node);
					ItemListMap.put(previousIndex, value);
				}
					
				buildFP(pos+1, SubList, node);
				if(row.size() <= pos)
				{
					return;
				}
				previousIndex = row.get(pos);
				count = 0;
				SubList = new ArrayList<ArrayList<Integer>>();
			}	
			count++;
			ArrayList<Integer> temp = new ArrayList<Integer>(row);
			SubList.add(temp);
		}
		Node node = new Node(previousIndex, count, parent);

		if (ItemListMap.containsKey(previousIndex))
		{
			ItemListMap.get(previousIndex).addNode(node); 
		}
		else
		{
			ItemList value = new ItemList(previousIndex, count, node);
			ItemListMap.put(previousIndex, value);
		}
		buildFP(pos+1, SubList, node);
	}
	
	//Integer: index
	//ItemList: class
	static HashMap<Integer, ItemList> ItemListMap = new HashMap<Integer, ItemList>();

	/*
	 * METHOD <Pre-Processing>
	 * Sorts transactions in required order
	 */
	private static void order(List<ArrayList<Integer>> AL) 
	{
	    Collections.sort(AL, new Comparator() 
	    {
	    	//Overloading Comparator as per requirement
	        public int compare(Object o1, Object o2) 
	        {
	        	int len = 0,flag=0;
	        	if( (((ArrayList) o1).size()) < (((ArrayList) o2).size()))
	        	{
	        			len = ((ArrayList) o1).size();
	        			flag=1;
	        	}
	        	else
	        	{	
	        		len = ((ArrayList) o2).size();
	        		flag=-1;
	        	}
	        	for(int i=0;i<len;i++)
	        	{
	        		Integer x1 = (Integer) ((ArrayList) o1).get(i);
	        		Integer x2 = (Integer) ((ArrayList) o2).get(i);
	        		int sComp = x1.compareTo(x2);

	            if (sComp != 0) 
	            {
	               return sComp;
	            } 
	        }
	    //return value to comparator
		return flag;
	    }});
	}    
	
	/*
	 * main METHOD
	 * Execution starts from here
	 */
	public static void main(String []args)
	{
	    //using Merge for sort() method
	    System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
	      	
	    try
	    {
	    	Scanner in = new Scanner(System.in);
	    	System.out.print("Enter file name: ");
	    	String inputFile = in.nextLine();
	    	
	    	if(!(new File(inputFile).exists()))			
			{	
				System.out.println("File doesn't exist. So exiting the program...");
				System.exit(-1);
			}//#Validation, if input file doesn't exists
	    	
	    	System.out.print("Enter support(0.0 to 1.0): " );
	    	double d = Double.parseDouble(in.nextLine());
	    	
	    	if(d<=0.0 || d>1.0)
			{
				System.out.println("You entered wrong value of support. So exiting the program...");
				System.exit(-1);
			}//#Validation, if wrong support value is entered

	    	BufferedReader reader = new BufferedReader(new FileReader(inputFile));
	    	String line = reader.readLine();
		
	    	//Reading headings from the file
			headings = line.split(",");
			
			while (((line = reader.readLine()) != null)) 
			{ 
				if (line.isEmpty() == true) 
				{
					continue;
				}

				ArrayList<String> row = new ArrayList<String>();
				String[] temp = line.split(",");
				
				/*
				 *Finding unique values and encoding them as:
				 *heading_value 
				 */
				for(int i=0 ; i<temp.length; i++)
				{
					if(uniqueItems.containsKey("" +i +"_" +temp[i]))
					{
						int count = uniqueItems.get("" +i +"_" +temp[i]);
						count++;
						uniqueItems.put("" +i +"_" +temp[i], count);
					}
					else
					{
						uniqueItems.put("" +i +"_" +temp[i], 1);
					}
					row.add("" +i +"_" +temp[i]);
				}
				count++;
				data.add(row);
			}
			 
			//Getting minimum support in terms of number of transactions
			min_support =  (int) Math.round((double)count * d);
		
			//Sorting the unique values found
			uniqueItems = (TreeMap<java.lang.String, java.lang.Integer>) sortByValues(uniqueItems);
			
			int index = 0;
			/*
			 * Initial Pruning
			 * Remove unique values, for which 
			 * support count is < minimum support
			 */
			for(Map.Entry<String,Integer> entry : uniqueItems.entrySet()) {
				  String key = entry.getKey();
				  Integer value = entry.getValue();
	
				  if(value < min_support)
				  {
					  uniqueItems.remove(key);			//remove unfrequent values
					  continue;
				  }
				  //System.out.println((index+1)+") "+key + " => " + value);
				  ItemIndex.put(key, index);
				  reverseItemIndex.put(index, key);
				  index++;
				}
		
			/*
			 * Encoding transactions
			 */
			//System.out.println(ItemIndex);
			for(ArrayList<String> a : data)
			{
				ArrayList<Integer> rowIndex = new ArrayList<Integer>();
				for(String s : a)
				{
					if(ItemIndex.containsKey(s))
					{
						rowIndex.add(ItemIndex.get(s));
						
					}
				}
				//System.out.println(rowIndex);
				Collections.sort(rowIndex);
				/*
				 * Following comment used to make input file for Liver Poll 
				 * University implementation of FP growth
				 * writer.write(rowIndex +"\n");
				 */
				dataInt.add(rowIndex);
			}
			order(dataInt);
			//System.out.println(dataInt);
			//Store execution timestamp
			startTime =  System.currentTimeMillis();
			
			/*
			 * Call method to build FP tree
			 * null is the root node of tree
			 */
			buildFP(0, dataInt, null);

			/*
			 * writer redirects the output to output file Results
			 * in the current working directory
			 */
			writer = new BufferedWriter(new OutputStreamWriter( new FileOutputStream(".\\Results")));
			writer.write("===============================================================\n");
			writer.write("CSCI 6405, Term Project\n");
			writer.write("===============================================================\n");
			writer.write("Athi Narayanan, B00736197\n");
			writer.write("Kartik Puri, B00762840\n");
			writer.write("===============================================================\n");
			writer.write("Total Rows: " +count +"\n");
			writer.write("Minimum Support: " +min_support +"\n");
			writer.write("===============================================================\n\n");
			writer.write("FREQUENT PATTERNS\n");
			
			/*
			 * Calling projection method
			 */
			//System.out.println("Size of args: " +args.length);
			if(args.length > 0)
				projection(ItemIndex.size()-1, ItemListMap,"", args[0]);
			else
				projection(ItemIndex.size()-1, ItemListMap,"", "prune");
			
			//Store completion timestamp
			stopTime = System.currentTimeMillis();
			
			
			//System.out.println("Ending");
			//Calculate execution time in millis
			long executionTime = stopTime - startTime;

			System.out.println("Execution time is (ms) " +executionTime);
			System.out.println("Total patterns generated: " +rulesCount +"\n");
			System.out.println("Frequent Patterns can be found in Results file");
			System.out.println("Thanks for using...");
			
			writer.write("\n===============================================================\n");
			writer.write("SUMMARY\n");
			writer.write("Total Patterns generated: " +rulesCount +"\n");
			writer.write("Execution Time: " +executionTime +" ms\n");
			writer.write("===============================================================");
			
			/*
			 * Flush all content to the output file
			 */
			writer.flush();
			//Close the file handle
			writer.close();
			
			//System.out.println("Execution time is s" +executionTime/1000);
			}
		    
		    catch(Exception e)
		    {
		    	e.printStackTrace();
		    }
	    }
	    
	    /*
	     * METHOD
	     * Sorting a TreeMap with values
	     */
	    public static <String, Integer extends Comparable<Integer>> Map<String,Integer> sortByValues(final Map<String,Integer> map) 
	    {
	        Comparator<String> valueComparator =  new Comparator<String>() 
	        {
	        	//Overloading compare() method
	            public int compare(String k1, String k2) 
	            {
	                int compare = map.get(k2).compareTo(map.get(k1));
	                if (compare == 0) return 1;
	                else return compare;
	            }
	        };
	        Map<String, Integer> sortedByValues = new TreeMap<String, Integer>(valueComparator);
	        sortedByValues.putAll(map);

	        return sortedByValues;
	    }
	    
	    /*
	     * METHOD
	     * This method is used for projection of tree
	     */
	    public static void projection(int index, HashMap<Integer, ItemList> ItemListMap1,String transaction, String pruneFlag) throws IOException
	    {
	    	HashMap<Integer, ItemList> subItemListMap = new HashMap<Integer, ItemList>();
	    	String transaction1 = "";
	    	for(int i=index ; i>=0; i--)
	    	{		
	    		if(!ItemListMap1.containsKey(i) || ItemListMap1.get(i).count<min_support)
	    		{
	    			continue;
	    		}//#skip if condition satisfied
	    		
	    		transaction1 = transaction +" " +"{" +headings[Integer.parseInt(reverseItemIndex.get(i).split("_")[0])] +":" +reverseItemIndex.get(i).split("_")[1] +"}";
	    		writer.write(transaction1 +" => " +ItemListMap1.get(i).count +"\n");
	    		rulesCount++;
	    		ItemList nodeItemList = ItemListMap1.get(i);
	    		int x=0;
	    		
	    		//Start projection from deepest level
	    		for(Node n : nodeItemList.list)
	    		{
	    			Node child=new Node(n.item, n.count, n.parentNode);
	    			Node parent=n.parentNode;
	    			Node newNode=null;
	    			while(parent!= null)
	    			{
	    				Node node = new Node(parent.item, child.count, parent.parentNode);
	    				if (subItemListMap.containsKey(parent.item))
	    				{
	    					newNode=subItemListMap.get(parent.item).addNode(node,parent);
	    				}
	    				else
	    				{
	    					ItemList value = new ItemList(parent.item, child.count, node, parent);
	    					newNode=node;
	    					subItemListMap.put(parent.item, value);
	    				}
	    				parent=parent.parentNode;
	    				child.parentNode=newNode;
	    				child=node;
	    			}
	    		}
	    		if (i==9)
	    		{
	    			int g=0;
	    			
	    		}
	    		
	    		//Calling for pruning, if not explicitly skipped
	    		if(!pruneFlag.equalsIgnoreCase("y"))
	    		pruning(subItemListMap, i );
	    		
	    		/*
	    		 * Recursive call for projection
	    		 */
	    		//System.out.println(reverseItemIndex);
	    		projection (i-1,subItemListMap,transaction1, pruneFlag);
	    		subItemListMap = new HashMap<Integer, ItemList>();
	    	}
	    }
	    
	    
	    /*
	     * METHOD
	     * Pruning for performance enhancement
	     * alpha pruning based on FP-Bonsai technique
	     */
	    public static void pruning(HashMap<Integer, ItemList> subItemListMap1, int size)
	    {
	    	//System.out.println("Pruning called");
	    	ArrayList<Integer> infrequentIndex = new ArrayList<Integer>();
	    	for(int i=0; i<size; i++)
	    	{
	    		if(!subItemListMap1.containsKey(i))
	    		{
	    			continue;
	    		}
	    		else if (subItemListMap1.get(i).count < min_support)
	    		{	
	    			infrequentIndex.add(i);
	    		}//#Prune element from header
	    	}
	    	if (infrequentIndex.isEmpty())
	    	{
	    		return;
	    	}
	    	Collections.sort(infrequentIndex);
	    	HashMap<Node, Node> outterMap = new HashMap<Node, Node>();
	    	
	    	//Remove element from tree and merging child nodes if required
	    	for(int i=1+infrequentIndex.get(0); i<size; i++)
	    	{
	    		if(!subItemListMap1.containsKey(i) || infrequentIndex.contains(i))
	    		{
	    			continue;
	    		}
	    		ArrayList<Node> temp = subItemListMap1.get(i).list;
	    		HashMap<Node, Node> innerMap = new HashMap<Node, Node>();
	    		ArrayList<Node> tempNodes = new ArrayList<Node>();
	    		for(Node n : temp)
	    		{
	    			int flag=0;
	    			while (n.parentNode!=null && infrequentIndex.contains(n.parentNode.item))
	    			{
	    				flag=1;
	    				n.parentNode=n.parentNode.parentNode;
	    				//System.out.println("Prune");
	    			}//#Prune
	    			if (outterMap.containsKey(n.parentNode))
	    			{
	    				flag=1;
	    				n.parentNode=outterMap.get(n.parentNode);
	    				//System.out.println("MergeChild");
	    			}//#Merge Children
	    			if (innerMap.containsKey(n.parentNode))
	    			{
	    				tempNodes.add(n);
	    				innerMap.get(n.parentNode).count+=n.count;
	    				outterMap.put(n,innerMap.get(n.parentNode));
	    				flag=0;
	    				//System.out.println("Prune-Merge");
	    			}//#Prune-Merge
	    			if (flag==1)
	    			{
	    				innerMap.put(n.parentNode, n);
	    			}
	    		}
	    		temp.removeAll(tempNodes);
	    	}
	    	
	    	//Actual Removal of elements from header
	    	for(int i : infrequentIndex )
    		{
	    		subItemListMap1.remove(i);
    		}
	    }
}