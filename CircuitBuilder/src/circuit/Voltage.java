package circuit;

/**
 * A voltage source class that supplies voltage to the circuit and that is connected to two different nodes.
 * 
 * It contains a voltage value.
 * 
 * It also contains an inherited Id as well as two connected nodes from the Component class.
 *  
 * @author Michael Sinclair.
 * @version 2.400
 * @since 12 February 2019.
 */

public class Voltage extends Component implements Comparable<Component>{
    /*Instance variables.*/
    private double voltage;
    protected static int vnum = 1;
    /* for testing doubles with uncertainty in last digits do not test against 0.0 directly - instead compare to a very small number - declared statically to reduce overhead*/
    private static double nonZeroVoltage= 0.00001;
    
    /**Constructor checks that voltage is non-zero, sets voltage and attaches two nodes with consistent polarity
     * @param double v.
     * @param Node nod1
     * @param Node nod2*/
    public Voltage(double v, Node nod1, Node nod2) {
    	super(nod1,nod2);
        /*Check that voltage is non-zero.*/
        if (Math.abs(v) <= nonZeroVoltage){ 
            throw new IllegalArgumentException("Voltage must be greater than 0.");
        }
        /*Check that both nodes exist.*/
        if (nod1 == null || nod2 == null){
            throw new IllegalArgumentException("Nodes must both exist before attaching voltage source.");
        }
        voltage = v;
        setId(Voltage.vnum);
        Voltage.vnum++;
        
        /*Need a consistent directionality in the circuit, defined as in the direction of increasing node numbers - for example V 2 1 1.0 is equivalent to V 1 2 -1.0.*/
        if (nodal1.getId()>nodal2.getId()){   
            Node temp = getNode1();          
            nodal1 = nodal2;         
            nodal2 = temp;                 
            voltage = -voltage;
        }
    }
    
    /*Methods.*/
    
    /** method to get voltage of voltage source
     * 
     * @return
     */
    public double getV() {
    	return voltage;
    }
    
    /** method to set voltage of voltage source
     * 
     * @param double v
     */
    public void setV(double v) {
    	voltage = v;
    }
    
    /** used for Circuit class toString() */
    public String myClass() {
    	return "Voltage";
    }
    
    /**Print information about voltage source, overrides toString()
     * @return String.*/
    @Override
    public String toString(){
    	/* use StringBuilder() and Double.toString() to optimize method */
    	StringBuilder sb = new StringBuilder("V");
    	sb.append(getId());
    	sb.append(" ");
    	sb.append(getNodes().get(0).toString());
    	sb.append(" ");
    	sb.append(getNodes().get(1).toString());
    	sb.append(" ");
    	sb.append(Double.toString(voltage));
    	sb.append(" Volts");
        return sb.toString();
    }
}