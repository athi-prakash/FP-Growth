
public class Node {

	//Attribute Value
	int item;
	
	//Count of attribute
	int count;
	
	//Pointer to parent Node
	Node parentNode;
	
	Node()
	{
		//Default Constructor
	}
	
	//Parameterized Constructor for initializing nodes 
	Node(int item, int count, Node parentNode)
	{
		this.item = item;
		this.count = count;
		this.parentNode = parentNode;
	}
}
