package circuit;
/**
 * A voltage source class that supplies voltage to the circuit and that is connected to two different nodes.
 * 
 * It contains a voltage value.
 * 
 * It also contains an inherited Id as well as two connected nodes from the Component class.
 *  
 * @author Michael Sinclair.
 * @version 2.302
 * @since 27 January 2019.
*/

public class Voltage extends Component{
    /*Instance variables.*/
    private double voltage;
    protected static int vnum = 1;
    
    /**Constructor checks that voltage is non-zero, sets voltage and attaches two nodes with consistent polarity
     * @param double v.
     * @param Node nod1
     * @param Node nod2*/
    protected Voltage(double v, Node nod1, Node nod2) {
    	super(nod1,nod2);
        double threshold = 0.00001;
        /*Check that voltage is non-zero.*/
        if (Math.abs(v) <= threshold){ 
            throw new IllegalArgumentException("Voltage must be greater than 0.");
        }
        /*Check that both nodes exist.*/
        if (nod1 == null || nod2 == null){
            throw new IllegalArgumentException("Nodes must both exist before attaching voltage source.");
        }
        this.voltage = v;
        this.setId(Voltage.vnum);
        Voltage.vnum++;
        
        /*Need a consistent directionality in the circuit, defined as in the direction of increasing node numbers.*/
        /*For example V 2 1 1.0 is equivalent to V 1 2 -1.0.*/
        if (this.nodal1.getId()>this.nodal2.getId()){   
            Node temp = this.getNode1();          
            this.nodal1 = this.nodal2;         
            this.nodal2 = temp;                 
            this.voltage = -this.voltage;
        }
    }
    
    /*Methods.*/
    
    /** method to get voltage of voltage source, no parameters
     * 
     * @return
     */
    protected double getV() {
    	return this.voltage;
    }
    
    /** method to set voltage of voltage source, no return
     * 
     * @param double v
     */
    protected void setV(double v) {
    	this.voltage = v;
    }
    
    /**Print information about voltage source, overrides toString()
     * @return String.*/
    @Override
    public String toString(){
        return "V"+this.getId()+" "+this.getNodes()[0]+" "+this.getNodes()[1]+" "+this.voltage+" Volts DC";
    }
    
    /** required override for resistor subclass, but not needed for voltage sources, so simply mimics toString() */
    public String toStringFinal() {
        return "V"+this.getId()+" "+this.getNodes()[0]+" "+this.getNodes()[1]+" "+this.voltage+" Volts DC";
    }
}