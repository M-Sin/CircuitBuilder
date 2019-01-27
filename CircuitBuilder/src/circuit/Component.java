package circuit;

/**
 * An abstract class to quantify common elements to components within the circuit. 
 * 
 * Mainly handles component Id features.
 * 
 * 
 * @author Michael Sinclair.
 * @version 2.302
 * @since 27 January 2019.
*/

public abstract class Component {
    /*Instance variables.*/
    protected Node nodal1;
    protected Node nodal2;
    protected int id;
    
    /** Superclass constructor 
     * @param Node node1
     * @param Node node2
    */
    protected Component(Node node1, Node node2){
    	this.nodal1 = node1;
    	this.nodal2 = node2;
    }
    
    /*Methods */
    
    /*get/set methods*/
    
    /** get first node, no parameters
     * 
     * @return Node nodal1
     */
    protected Node getNode1() {
    	return this.nodal1;
    }
    
    /** get second node, no parameters
     * 
     * @return Node nodal2
     */
    protected Node getNode2() {
    	return this.nodal2;
    }
    
    /** set first node, no return
     * 
     * @param Node node1
     */
    protected void setNode1(Node node1) {
    	this.nodal1 = node1;
    }
    
    /** set second node, no return
     * 
     * @param Node node2
     */
    protected void setNode2(Node node2) {
    	this.nodal2 = node2;
    }
    
    /** get component id, no parameters
     * 
     * @return int id
     */
    protected int getId() {
    	return this.id;
    }
    
    /** set component id, no return
     * 
     * @param int i
     */
    protected void setId(int i) {
    	this.id = i;
    }
    
    /** method for testing if connected through only 1 node for use in nodal analysis , returns true if components are connected through the first node and not the second
     * 
     * @param Component other
     * @return boolean
     * */
    protected boolean testNode(Component other) {
    	if (this.nodal1 == other.nodal1) {
    		if (this.nodal2 != other.nodal1) {
    			return true;
    		}
    	}
		return false;
    }
    
    /**Return list of nodes connected to voltage source, no parameters
     * @return Node[] list.
     * */
    protected Node[] getNodes(){
        return new Node[] {this.nodal1 , this.nodal2};
    }
    
    /** define equals method, returns true if Ids are the same otherwise false
     * @param Component other
     * @return boolean
     * */
    protected boolean equals(Component other) {
    	if (this.getId() == other.getId()) {
    		return true;
    	}
    	else return false;
    }
    
    /** define compare method for sorting
    * if the first node Id is smaller than the other first node, method returns a negative number and vice versa
    * if the first node Id is the same, and the second node is smaller than the other second node, method returns a negative number and vice versa
    * if both nodes are equal, method returns 0 
    * @param Component other
    * @return int
    * */
    protected int compare(Component other) {
    	if (this.getNode1().getId() == other.getNode1().getId()) {
    		return this.getNode2().getId()-other.getNode2().getId();
    	}
    	else {
    		return this.getNode1().getId()-other.getNode1().getId();
    	}
    }
    
    
    /** make components override toString() 
     * @return String 
     */
    @Override
    public abstract String toString();
    
    /** desire a toString that displays different information 
     * @return String
     * */
    public abstract String toStringFinal();
}
