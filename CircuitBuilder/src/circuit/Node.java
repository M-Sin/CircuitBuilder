package circuit;
import java.util.ArrayList;

/**
 * A node class, to connect circuit components.
 * 
 * Contains an id that describes the node, as well as the voltage at the node (to be added), the current leaving the node (to be added) and an array list of components attached to the node.
 * 
 * 
 * @author Michael Sinclair.
 * @version 2.400
 * @since 12 February 2019.
 */

public class Node implements Comparable<Node>{
	/*Instance variables*/
    private final int id;
    private double voltageAt;
    private ArrayList<Component> attachments;
    private double currentLeaving;
    
    /**Assign an id to this node.
     * @param nodal_id.*/
    public Node(int nodalId) {
        id = nodalId;
        voltageAt = 0.0;
        attachments = new ArrayList<>();
        currentLeaving = 0.0;
    }
    
    /*Methods*/
    
    /**get id
     * @return int id
     * */
    protected int getId(){
    	return id;
    }
   
    /** set voltage at Node
     * @param double volts
     * */
    protected void setVoltage(double volts) {
    	voltageAt = volts;
    }
    
    
    /** get voltage at Node
     * @return double voltageAt
     * */
    protected double getVoltage() {
    	return voltageAt;
    }
    
    /** set current leaving Node
     * @param double current
     * */
    protected void setCurrent(double current) {
    	currentLeaving = current;
    }
    
    /** get current leaving Node
     * @return double currentLeaving
     * */
    protected double getCurrent() {
    	return currentLeaving;
    }
    
    /** connection a component to this node, methods for tracking component connections
     * @param Component component
     * */
    protected void connect(Component component) {
    	attachments.add(component);
    }
    
    /** disconnect a component from this node, methods for tracking component connections
     * 
     * @param Component component
     */
    protected void disconnect(Component component) {
    	attachments.remove(component);
    }
    
    /** get the list of attachments that are attached to this node
     * 
     * @return ArrayList<Component> attachments
     */
    protected ArrayList<Component> getAttachments(){
    	return attachments;
    }   
    
    
    /**Display node id, overrides toString()
     * @return String.*/
    @Override
    public String toString() {
    	/* use Integer.toString() to directly return string representation of id 
    	 * reduces overhead as otherwise writing +id would call Integer.toString() anyway to create a string representation, 
    	 * concatenate that string with the empty string that would need to be present by typing ""+, thus creating a third string
    	 */
        return Integer.toString(id);
    }
    
    /** Override compareTo() to compare two nodes 
     * returns 0 if they are equal
     * returns a positive integer if this node Id is greater than the one being compared to it
     * returns a negative integer if this node Id is less than the one being compared to it
     * @param Object other*/
    @Override
    public int compareTo(Node other) {
    	/* use Integer.compare() to prevent possible errors from overflow due to subtraction */
    	return Integer.compare(getId(), other.getId());
    }
    
    /** method for displaying the attachments connected to this node, mainly used for debugging, displays a string version of the list of attachments
     * 
     * @return String
     */
    public String toStringAttachments() {
    	/* use StringBuilder and Integer.toString() to reduce overhead of operating on strings
    	 */
    	StringBuilder sb = new StringBuilder("Node"+Integer.toString(id));
    	for(int i=0;i<attachments.size();i++) {
    		sb.append(attachments.get(i).toString());
    	}
    	return sb.toString();
    }
}