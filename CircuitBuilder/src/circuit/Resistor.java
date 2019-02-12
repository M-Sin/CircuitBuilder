package circuit;

/**
 * A resistor class with resistance that is connected to two different nodes.
 * 
 * It contains a resistance value, a current through the resistor (to be added) which will correspond to voltage drop across the resistor.
 * 
 * It also contains an inherited Id as well as two connected nodes from the Component class.
 * 
 * 
 * 
 * @author Michael Sinclair.
 * @version 2.400
 * @since 12 February 2019.
 */

public class Resistor extends Component implements Comparable<Component>{
    /*Instance variables.*/
    private double resistance;
    protected static int resnum = 1;
    /* for testing doubles with uncertainty in last digits do not test against 0.0 directly - instead compare to a very small number - declared statically to reduce overhead*/
    private static double nonZeroNonNegativeResistance= 0.00001;
    
    /**Constructor that checks that resistor is non-zero and non-negative, sets resistance and attaches nodes
     * @param res.
     * @param nod1
     * @param nod2*/
    public Resistor(double res, Node nod1, Node nod2) {
    	super(nod1,nod2);
        /*Ensure resistor is greater than 0, and not negative.*/
        if (res <= Resistor.nonZeroNonNegativeResistance){
            throw new IllegalArgumentException("Resistance must be greater than 0.");
        }
        /*Ensure the specified nodes exist for the resistor.*/
        if (nod1 == null || nod2 == null){
            throw new IllegalArgumentException("Nodes must both exist before attaching resistor.");
        }
        /*Create the resistor variables.*/
        resistance = res;
        setId(Resistor.resnum);
        Resistor.resnum++;
        currentThrough=0.0;
    }
      
    /*Methods.*/
    
    /** get the resistance
     * 
     * @return double resistance
     */
    public double getR() {
    	return resistance;
    }
    
    
    /** used for Circuit class toString() */
    public String myClass() {
    	return "Resistor";
    }
    
    
    /**Return the information of the resistor
     * @return String.*/
    @Override
    public String toString(){
    	/* use StringBuilder() and Double.toString() to optimize method */
    	StringBuilder sb = new StringBuilder("R");
    	sb.append(getId());
    	sb.append(" ");
    	sb.append(getNodes().get(0).toString());
    	sb.append(" ");
    	sb.append(getNodes().get(1).toString());
    	sb.append(" ");
    	sb.append(Double.toString(resistance));
    	sb.append(" Ohms");
        return sb.toString();
    }

}