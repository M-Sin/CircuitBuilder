package circuit;

/**
 * An abstract class to quantify common elements to components within the circuit. 
 * 
 * @author Michael Sinclair.
 * @version 0.1.
 * @since 18 December 2018.
*/

public abstract class Component {
    /*Instance variables.*/
    protected Node nodal1;
    protected Node nodal2;
    protected int id;
    
    /** Superclass constructor 
     * @param node1
     * @param node2
    */
    protected Component(Node node1, Node node2){
    	this.nodal1 = node1;
    	this.nodal2 = node2;
    }
    
    /*Methods */
    
    /*get/set methods*/
    
    protected Node getNode1() {
    	return this.nodal1;
    }
    
    protected Node getNode2() {
    	return this.nodal2;
    }
    
    protected void setNode1(Node node1) {
    	this.nodal1 = node1;
    }
    
    protected void setNode2(Node node2) {
    	this.nodal2 = node2;
    }
    
    protected int getId() {
    	return this.id;
    }
    
    protected void setId(int i) {
    	this.id = i;
    }
    
    /* method for testing if connected through only 1 node for use in nodal analysis */
    protected boolean testNode(Component other) {
    	if (this.nodal1 == other.nodal1) {
    		if (this.nodal2 != other.nodal1) {
    			return true;
    		}
    	}
		return false;
    }
    
    /**Return list of nodes connected to voltage source
     * @return Node[] list.*/
    protected Node[] getNodes(){
        return new Node[] {this.nodal1 , this.nodal2};
    }
    
    /* define equals method */
    protected boolean equals(Component other) {
    	if (this.getId() == other.getId()) {
    		return true;
    	}
    	else return false;
    }
    
    /* define compare method for sorting */
    /* if the first node Id is smaller than the other first node, method returns a negative number and vice versa*/
    /* if the first node Id is the same, and the second node is smaller than the other second node, method returns a negative number and vice versa */
    /* if both nodes are equal, method returns 0 */
    protected int compare(Component other) {
    	if (this.getNode1().getId() == other.getNode1().getId()) {
    		return this.getNode2().getId()-other.getNode2().getId();
    	}
    	else {
    		return this.getNode1().getId()-other.getNode1().getId();
    	}
    }
    
    
    /* make components override toString() */
    @Override
    public abstract String toString();
}
