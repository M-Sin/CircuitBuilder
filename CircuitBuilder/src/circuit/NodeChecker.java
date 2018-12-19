package circuit;
import java.util.ArrayList;

/** A helper class to simplify UserMain code.
 * 
 * Evaluates whether nodes exist already as a component is added, and if not creates them within UserMain's nodeList ArrayList.
 * 
 * Decided to compartmentalize this portion of the program to remove large duplicate code blocks and simplify UserMain.
 * 
 * @author Michael Sinclair.
 * @version 0.1.
 * @since 18 December 2018.
*/

public class NodeChecker {
	
	private Node node1;
	private Node node2;
	private ArrayList<Node> nodeList;

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
	
	protected Node getCheckedNode1() {
		return this.node1;
	}
	
	protected Node getCheckedNode2() {
		return this.node2;
	}
	
	/* method to find index for node 1 or node 2 */
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

