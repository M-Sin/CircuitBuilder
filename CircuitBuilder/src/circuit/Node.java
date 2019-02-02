package circuit;
import java.util.ArrayList;

/**
* A node class, to connect circuit components.
 * 
 * Contains an id that describes the node, as well as the voltage at the node (to be added), the current leaving the node (to be added) and an array list of components attached to the node.
 * 
 * 
 * @author Michael Sinclair.
 * @version 2.305
 * @since 2 February 2019.
*/

public class Node {
	/*Instance variables*/
    private int id;
    private double voltageAt;
    private ArrayList<Component> attachments;
    private double currentLeaving;
    
    /**Assign an id to this node.
     * @param nodal_id.*/
    public Node(int nodalId) {
        this.id = nodalId;
        this.voltageAt = 0.0;
        this.attachments = new ArrayList<>();
        this.currentLeaving = 0.0;
    }
    
    /*Methods*/
    
    /**get id, no parameters
     * @return int id
     * */
    protected int getId(){
    	return this.id;
    }
   
    /** set voltage at Node, no return
     * @param double volts
     * */
    protected void setVoltage(double volts) {
    	this.voltageAt = volts;
    }
    
    
    /** get voltage at Node, no parameters
     * @return double voltageAt
     * */
    protected double getVoltage() {
    	return this.voltageAt;
    }
    
    /** set current leaving Node, no return
     * @param double current
     * */
    protected void setCurrent(double current) {
    	this.currentLeaving = current;
    }
    
    /** get current leaving Node, no parameters
     * @return double currentLeaving
     * */
    protected double getCurrent() {
    	return this.currentLeaving;
    }
    
    /** connection a component to this node, methods for tracking component connections, no return
     * @param Component component
     * */
    protected void connect(Component component) {
    	this.attachments.add(component);
    }
    
    /** disconnect a component from this node, methods for tracking component connections, no return
     * 
     * @param Component component
     */
    protected void disconnect(Component component) {
    	this.attachments.remove(component);
    }
    
    /** get the list of attachments that are attached to this node, no parameters
     * 
     * @return ArrayList<Component> attachments
     */
    protected ArrayList<Component> getAttachments(){
    	return this.attachments;
    }   
    
    /**Display node id, overrides toString(), no parameters
     * @return String.*/
    @Override
    public String toString() {
        return ""+this.id;
    }
    
    /** method for displaying specific information about this node, no parameters
     * 
     * @return String
     */
    public String toStringSpecific() {
    	return "Node"+this.id+" Current Leaving: "+this.currentLeaving+" Amps and Voltage at node:" + this.voltageAt+" Volts.";
    }
    
    /** method for displaying the attachments connected to this node, mainly used for debugging, no parameters, displays a string version of the list of attachments
     * 
     * @return String
     */
    public String toStringAttachments() {
    	String str = "Node"+this.id;
    	for(int i=0;i<attachments.size();i++) {
    		str+=" "+attachments.get(i).toString();
    	}
    	return str;
    }
}