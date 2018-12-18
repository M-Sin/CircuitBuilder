package circuit;

/**
* A node class, to connect circuit components.
* Contains an id that describes the node, as well as the voltage at the node, 
* the current leaving the node (clockwise) and the resistance to the 'right' (again, clockwise)
* of the node. Note that if a voltage source is to the 'right' the resistance will be 0.0.
* 
* It currently supports linear circuits with components placed sequentially.
* 
* @author Michael Sinclair.
* @version 0.1.
* @since 18 December 2018.
*/

public class Node {
	/*Instance variables*/
	/* exploring possibility of what tracking this information at the nodes can do */
    private int id;
    private double voltageAt;
    private double currentLeaving;
    private double ResRight;
    private int connections;
    
    /**Assign an id to this node.
     * @param nodal_id.*/
    protected Node(int nodalId) {
        this.id = nodalId;
        this.voltageAt = 0.0;
        this.currentLeaving = 0.0;
        this.ResRight = 0.0;
        this.connections = 0;
    }
    
    /*Methods*/
    
    /*get id */
    protected int getId(){
    	return this.id;
    }
   
    /* set/get voltage */
    protected void setVoltage(double volts) {
    	this.voltageAt = volts;
    }
    
    protected double getVoltage() {
    	return this.voltageAt;
    }
    
    /* set/get current_right */
    protected void setCurrentRight(double cur) {
    	this.currentLeaving = cur;
    }
    
    protected double getCurrentRight() {
    	return this.currentLeaving;
    }
    
    /* set/get resistance_right */
    protected void setResistanceRight(double res) {
    	this.ResRight = res;
    }
    
    protected double getResistanceRight() {
    	return this.ResRight;
    }
    
    /* set/get connections, methods for tracking component connections */
    protected void connect() {
    	this.connections++;
    }
    
    protected void disconnect() {
    	this.connections--;
    }
    
    protected int getConnections() {
    	return this.connections;
    }
    
    
    /**Display node id
     * @return String.*/
    @Override
    public String toString() {
        return ""+this.id;
    }
    
    public String toStringSpecific() {
    	return "Node"+this.id+" CL "+this.currentLeaving+" RR "+this.ResRight+" V@" + this.voltageAt;
    }
}