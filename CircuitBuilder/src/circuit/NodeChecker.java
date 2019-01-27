package circuit;
import java.util.ArrayList;

/** A helper class to simplify UserMain code.
 * 
 * Evaluates whether nodes exist already as a component is added, and if not creates them within UserMain's nodeList ArrayList.
 * 
 * Decided to compartmentalize this portion of the program to remove large duplicate code blocks and simplify UserMain.
 * 
 * Would likely normally just make this a method within UserMain if resources and time were of concern in this program. Chose to make it a separate class to make program easier to view, for now.
 * 
 * 
 * @author Michael Sinclair.
 * @version 2.302
 * @since 27 January 2019.
*/

public class NodeChecker {
	
	private Node node1;
	private Node node2;
	private ArrayList<Node> nodeList;
	
	/** constructor for building this class
	 * 
	 * @param Node nod1
	 * @param Node nod2
	 * @param ArrayList<Node> nodeList
	 */
	public NodeChecker(int nod1, int nod2, ArrayList<Node> nodeList){
		this.nodeList = nodeList;
		/*Check that component does not have the same nodes on both ends. Note would be part of input validation*/
        if (nod1 == nod2){
            throw new IllegalArgumentException("Nodes must be different for a single component.");
        }
        
        this.node1 = new Node(nod1);
        this.node2 = new Node(nod2);
        
        /*If these are the first two nodes, add them.*/
        if (nodeList.isEmpty()){
            nodeList.add(node1);
            nodeList.add(node2);
        }
        
        int flag1 = 0;
        int flag2 = 0;
        /*If nodes do not exist, create them.*/
        for (Node node : nodeList){
            if (node.getId() == node1.getId()){
                /*If found set flag and break.*/
                flag1 = 1;
                break;
            } 
        }
        for (Node node : nodeList){
            if (node.getId() == node2.getId()){
                /*If found set flag and break.*/
                flag2 = 1;
                break;
            }
        }
        
        /*If not found.*/
        if (flag1 == 0){
            nodeList.add(node1);
        }
        
        if (flag2 == 0){
            nodeList.add(node2);
        }
	}
	
	/** get first node to check, no parameters
	 * 
	 * @return Node node1
	 */
	protected Node getCheckedNode1() {
		return this.node1;
	}
	
	/** get second node to check, no parameters
	 * 
	 * @return Node node2
	 */
	protected Node getCheckedNode2() {
		return this.node2;
	}
	
	/** method to find index for node 1 or node 2, depending on whether it is called with i = 1 or i = 2 (only two values that will do anything in this method as a component can only have 2 nodes)
	 * @param int i
	 * @return index1 or index2
	 * */
	protected int findIndex(int i) {
        if (i == 1) {
			int index1 = 0;
	        for (Node node : nodeList){
	            if (node.getId() == node1.getId()){
	                break;
	            }
	            index1++;
	        }
	        return index1;
        }
        else {
            int index2 = 0;
            for (Node node : nodeList){
                if (node.getId() == node2.getId()){
                    break;
                }
                index2++;
            }
            return index2;
    	}
	}
}

