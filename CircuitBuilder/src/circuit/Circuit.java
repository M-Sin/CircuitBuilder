package circuit;
import java.util.ArrayList;

/**
* The circuit to which components can be added to. 
* 
* An object of this class exists in the program as a singleton object. 
* 
* It holds the ArrayList of components in the circuit.
* 
* 
* @author Michael Sinclair.
* @version 0.1.
* @since 22 December 2018.
* */

public class Circuit {
	
	/*Creates only one instance of a circuit.*/
	private static Circuit instance = null;
	protected static Circuit getInstance() {
		if (instance == null) {
        instance = new Circuit();
		}
    return instance;
	}
	
	/**Instance variable list to contain components.*/
	private ArrayList<Component> components;
	/**Private constructor ensures only one instance of circuit can be created.*/
	private Circuit() {
		this.components = new ArrayList<>();
	}

	/*Methods.*/

	/* get/set methods*/
	protected ArrayList<Component> getComponents(){
		return this.components;
	}

	/**Add component to circuit
     * @param c.*/
	protected void addComponent(Component c){
		this.components.add(c);
	}

	/**Return information of all components in circuit
	     * @return String.*/
	@Override
	public String toString(){
	    String str="";
	    /*For each object in array.*/
	    for(Object obj : components){
	        /*If it is a resistor.*/
	        if (obj.getClass() == Resistor.class){
	            /*Downcast to original object type to use class toString() method.*/
	            str+= ("Resistor: "+(Resistor)obj).toString()+"\n";
	        }
	        
	        /*Another form of testing object class, if it is a voltage.*/
	        if (obj instanceof Voltage){
	            /*Downcast to original object type to use class toString() method.*/
	            str+= ("Voltage:  "+(Voltage)obj).toString()+"\n";
	        }
	    }
	    /*Remove last \n produced from the above iteration.*/
	    str = str.substring(0,str.length()-1);
	    return str;
	}
	}
