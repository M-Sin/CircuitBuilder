package circuit;
import java.util.ArrayList;

/**
 * The circuit to which components can be added to. 
 * 
 * An object of this class exists in the program as a singleton object. 
 * 
 * It holds the ArrayList of components in the circuit.
 * 
 * @author Michael Sinclair.
 * @version 2.400
 * @since 12 February 2019.
 */

/*
 * TODO Explore storing nodes and components as HashMap - Map<String,Node> or Map<String,Component> where they would be named rather than numbered.
 * TODO Explore not having a singleton Circuit.
 */

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
		components = new ArrayList<>();
	}

	/*Methods.*/

	/**Get method to get list of components
	 * @param none
	 * @return ArrayList<Component> components*/
	protected ArrayList<Component> getComponents(){
		return components;
	}

	/**Add component to circuit
     * @param Component c.*/
	protected void addComponent(Component c){
		components.add(c);
	}

	/**Return information of all components in circuit
	     * @return String.*/
	@Override
	public String toString(){
		/* use StringBuilder to reduce overhead from operating on immutable strings */
	    StringBuilder sb = new StringBuilder();
	    /*For each component in array.*/
	    for(Component component : components){
	    	/* obtain component information in general way to make program more scalable (add more component types) */
	    	sb.append(component.myClass());
	    	sb.append(": ");
	    	sb.append(component.toString());
	    	sb.append("\n");
	    }
	    /* remove last \n character */
	    if(sb.length()>0) {
	    	sb.setLength(sb.length()-1);
	    }
	    return sb.toString();
	}
}
