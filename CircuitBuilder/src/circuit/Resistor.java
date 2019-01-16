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
 * @version 2.300
 * @since 16 January 2019.
 */
public class Resistor extends Component{
    /*Instance variables.*/
    private double resistance;
    protected static int resnum = 1;
    /* functionality will be added later */
    private double current_through;
    
    /**Constructor that checks that resistor is non-zero and non-negative, sets resistance and attaches nodes
     * @param res.
     * @param nod1
     * @param nod2*/
    protected Resistor(double res, Node nod1, Node nod2) {
    	super(nod1,nod2);
        double threshold = 0.00001;
        /*Ensure resistor is greater than 0, and not negative.*/
        if (res <= threshold){
            throw new IllegalArgumentException("Resistance must be greater than 0.");
        }
        /*Ensure the specified nodes exist for the resistor.*/
        if (nod1 == null || nod2 == null){
            throw new IllegalArgumentException("Nodes must both exist before attaching resistor.");
        }
        /*Create the resistor variables.*/
        this.resistance = res;
        this.setId(Resistor.resnum);
        Resistor.resnum++;
        this.current_through=0.0;
        
        /* required ordered nodes for calculations */
        if (this.nodal1.getId()>this.nodal2.getId()){   
            Node temp = this.getNode1();          
            this.nodal1 = this.nodal2;         
            this.nodal2 = temp;                 
        }
    }
      
    /*Methods.*/
    
    /* get/set methods */
    protected double getR() {
    	return this.resistance;
    }
    
    /* functionality will be added later */
    protected void set_current(double i_c){
    	this.current_through = i_c;
    }
    
    protected double get_current(){
    	return this.current_through;
    }
    
    /**Return the information of the resistor
     * @return String.*/
    @Override
    public String toString(){
        return "R"+this.getId()+" "+this.getNodes()[0]+" "+this.getNodes()[1]+" "+this.resistance+" Ohms";
    }
    
    /* a toString that does not display the resistor Id */
    public String toStringFinal() {
        return "Req "+this.getNodes()[0]+" "+this.getNodes()[1]+" "+this.resistance+" Ohms";
    }
}