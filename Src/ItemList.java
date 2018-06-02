import java.util.ArrayList;
import java.util.HashMap;

/*
 * ItemList contains nodes at same level
 * Maintain their count and next node in the list
 */
public class ItemList 
{

	//index of attribute value
	int index;
	
	//Count of values
	int count;
	
	//List for holding ndoes
	ArrayList<Node> list = new ArrayList<Node>();
	
	//header to maintain levels
	HashMap<Node, Node> srcMap = new HashMap<Node, Node>(); 
	
	//Constructor for first element of list 
	ItemList (int index,int count,Node node)
	{
		this.index=index;
		this.count=count;
		list.add(node); 
	}
	
	//Constructor for list when source node is given
	ItemList (int index,int count,Node node,Node src)
	{
		this.index=index;
		this.count=count;
		list.add(node); 
		srcMap.put(src, node);
	}
	
	//Add node to the list
	public void addNode(Node node)
	{
		list.add(node);
		this.count+=node.count;
	}
	
	//Add node to the list, when source node is given
	public Node addNode(Node node,Node src)
	{
		Node n=srcMap.get(src);
		if (n!=null)
		{
			n.count+=node.count;
			this.count+=node.count;
			return n;
		}
		else
		{
			list.add(node);
			this.count+=node.count;
			srcMap.put(src, node);
			return node;
		}
	}
}