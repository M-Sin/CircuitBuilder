package circuit;
import java.util.ArrayList;

/**
* A node class, to connect circuit components.
 * 
 * Contains an id that describes the node, as well as the voltage at the node (to be added), the current leaving the node (to be added) and an array list of components attached to the node.
 * 
 * 
 * @author Michael Sinclair.
 * @version 2.20
 * @since 8 January 2019.
*/

public class Node {
	/*Instance variables*/
    private int id;
    private double voltageAt;
    private ArrayList<Component> attachments;
    private double currentLeaving;
    
    /**Assign an id to this node.
     * @param nodal_id.*/
    protected Node(int nodalId) {
        this.id = nodalId;
        this.voltageAt = 0.0;
        this.attachments = new ArrayList<>();
        this.currentLeaving = 0.0;
    }
    
    /*Methods*/
    
    /*get id */
    protected int getId(){
    	return this.id;
    }
   
    /* set/get voltage at Node */
    protected void setVoltage(double volts) {
    	this.voltageAt = volts;
    }
    
    protected double getVoltage() {
    	return this.voltageAt;
    }
    
    /* set/get voltage current leaving Node */
    protected void setCurrent(double current) {
    	this.currentLeaving = current;
    }
    
    protected double getCurrent() {
    	return this.currentLeaving;
    }
    
    /* set/get connections, methods for tracking component connections */
    protected void connect(Component component) {
    	this.attachments.add(component);
    }
    
    protected void disconnect(Component component) {
    	this.attachments.remove(component);
    }
    
    protected ArrayList<Component> getAttachments(){
    	return this.attachments;
    }   
    
    /**Display node id
     * @return String.*/
    @Override
    public String toString() {
        return ""+this.id;
    }
    
    public String toStringSpecific() {
    	return "Node"+this.id+" Current Leaving: "+this.currentLeaving+" Amps and Voltage at node:" + this.voltageAt+" Volts.";
    }
    
    public String toStringAttachments() {
    	String str = "Node"+this.id;
    	for(int i=0;i<attachments.size();i++) {
    		str+=" "+attachments.get(i).toString();
    	}
    	return str;
    }
}