package circuit;
import java.util.ArrayList;

/**
 * An abstract class to quantify common elements to components within the circuit. 
 * 
 * Mainly handles component Id features.
 * 
 * 
 * @author Michael Sinclair.
 * @version 2.400
 * @since 12 February 2019.
 */

public abstract class Component implements Comparable<Component>{
    /*Instance variables.*/
    protected Node nodal1;
    protected Node nodal2;
    protected int id;
    /* functionality will be added later */
    protected double currentThrough;
    
    /** Superclass constructor 
     * @param Node node1
     * @param Node node2
    */
    public Component(Node node1, Node node2){
    	nodal1 = node1;
    	nodal2 = node2;
    	currentThrough = 0.0;
    }
    
    /*Methods */
    
    /*get/set methods*/
    
    /** get first node
     * 
     * @return Node nodal1
     */
    public Node getNode1() {
    	return nodal1;
    }
    
    /** get second node
     * 
     * @return Node nodal2
     */
    public Node getNode2() {
    	return nodal2;
    }
    
    /** set first node
     * 
     * @param Node node1
     */
    public void setNode1(Node node1) {
    	nodal1 = node1;
    }
    
    /** set second node
     * 
     * @param Node node2
     */
    public void setNode2(Node node2) {
    	nodal2 = node2;
    }
    
    /** get component id
     * 
     * @return int id
     */
    public int getId() {
    	return id;
    }
    
    /** set component id
     * 
     * @param int i
     */
    public void setId(int i) {
    	id = i;
    }
   
    
    /** functionality will be added later, sets current through this component
     * @param double iC
     * */
    public void setCurrent(double iC){
    	currentThrough = iC;
    }
    
    /** functionality will be added later, gets current through this component
     * 
     * @return double current_through
     */
    public double getCurrent(){
    	return currentThrough;
    }
    
    /** method for testing if connected through only 1 node for use in nodal analysis , returns true if components are connected through the first node and not the second
     * 
     * @param Component other
     * @return boolean
     * */
    protected boolean testNode(Component other) {
    	if (nodal1 == other.nodal1) {
    		if (nodal2 != other.nodal1) {
    			return true;
    		}
    	}
		return false;
    }
    
    /**Return list of nodes connected to voltage source
     * @return Node[] list.
     * */
    protected ArrayList<Node> getNodes(){
    	ArrayList<Node> retList = new ArrayList<Node>();
    	retList.add(nodal1);
    	retList.add(nodal2);
        return retList;
    }
    
    /** Override equals method, returns true if Ids are the same otherwise false
     * @param Component other
     * @return boolean
     * */
    @Override
    public boolean equals(Object other) {
    	if (other instanceof Component) {
    		Component testEqual = (Component) other;
        	return getClass() == testEqual.getClass() && getId() == testEqual.getId();
    	}
    	return false;
    }
    
    /** define compare method for sorting
    * if the first node Id is smaller than the other first node, method returns a negative number and vice versa
    * if the first node Id is the same, and the second node is smaller than the other second node, method returns a negative number and vice versa
    * if both nodes are equal, method returns 0 
    * @param Component other
    * @return int
    * */
    @Override
    public int compareTo(Component other) {
    	int result = getNode1().compareTo(other.getNode1());
    	if (result == 0) {
    	    result = getNode2().compareTo(other.getNode2());
    	}
    	return result;
    }
    
    /** get specific component type for toString() */
    public abstract String myClass();
    
    
    /** make components override toString() 
     * @return String 
     */
    @Override
    public abstract String toString();

}
