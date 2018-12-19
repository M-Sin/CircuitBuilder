package circuit;
import java.util.ArrayList;

/** Assistant class to calculate current through resistors or voltage between nodes, that takes a ground reference and the components in the circuit.
 * Input requires reference ground node, components in the circuit, and the two nodes between which the analysis takes place.
 * 
 * This program will make the following assumptions:
 * Voltages will not be connected in parallel, only in series (which means there is a node between them).
 * 
 * Currents will be calculated clockwise.
 * 
 * I decided to compartmentalize this part of the program in a separate class to simplify the coding and maintenance.
 * 
 * V1.11
 * 
 * @author Michael Sinclair.
 * @version 0.1.
 * @since 18 December 2018.
* */

public class CircuitAnalysis {
	/* instance variables */
	private ArrayList<Node> nodeList;
	private ArrayList<Component> components;
	private int ground;
	private double totalV;
	private double totalR;
	
	protected CircuitAnalysis(int ground, ArrayList<Component> components, ArrayList<Node> nodeList) {
		/* initialize variables */
		this.ground = ground;
		this.components = components;
		this.nodeList = nodeList;
		this.totalR = 0.0;
		this.totalV = 0.0;
		}
	
	/* methods */
	
	protected void analyzeCircuit() {
		this.analyzeVoltage();
		this.analyzeResistance();
		this.printCharacteristics();
	}

	/* node analysis */
	protected void analyzeVoltage() {
		/* find total voltage */
		for (int i = 0; i<components.size();i++) {
			if (components.get(i).getClass() == Voltage.class) {
				this.totalV+=((Voltage)(components.get(i))).getV();
			}
		}
	}
	
	protected void analyzeResistance() {
		this.analyzeParallel();
		
		/* now that all resistors are serial resistors */
		for (int i = 0; i<components.size();i++) {
			if (components.get(i) instanceof Resistor) {
				this.totalR+=((Resistor)components.get(i)).getR();
			}
		}
	}
	
	protected void analyzeParallel() {
		/* starting with all resistors connected between the same two nodes */
		ArrayList<Component> temp = new ArrayList<>();
		ArrayList<Component> removal = new ArrayList<>();
		/* for each node */
		for (int n = 0; n<nodeList.size();n++) {
			/* find components connected to each other node */
			for (int m = 0; m<nodeList.size();m++) {
				/* components cannot have the same node on both sides */
					if (n!=m && n<m) {
						/* for each component */
						for (int k = 0;k<components.size();k++) {
							if(components.get(k).getNode1().getId() == n && components.get(k).getNode2().getId() == m) { 
								/* if it is a resistor */
								if (components.get(k).getClass() == Resistor.class) {
									/* if it is between the two nodes */
									if(components.get(k).getNode1().getId() == n && components.get(k).getNode2().getId() == m) {
										/* add it to temporary list */
										temp.add(components.get(k));
										/* remove it from the nodes and from the component list */
										components.get(k).getNode1().remove(components.get(k));
										components.get(k).getNode2().remove(components.get(k));
										/* track which components needs to be removed in place of equivalent single resistor */
										removal.add(components.get(k));
										components.remove(components.get(k));
									}
									/* if a parallel connection was found */
									if(temp.size()>1) {
										components.add(new Resistor(this.parallelResistors(temp),nodeList.get(n),nodeList.get(m)));
									}
								}
							}
						}
					}
				}
			}
		/* remove resistors to be replaced by single equivalent resistor */
		for (int i = 0; i<removal.size();i++) {
			if(components.contains(removal.get(i))){
				components.remove(removal.get(i));
				System.out.println("Removed "+removal.get(i).toString());
			}
		}
	}
	
	/* Calculate parallel resistance */
	protected double parallelResistors(ArrayList<Component> resistors) {
		double parallelR = 0.0;
		if(resistors.size()==0) {
	        throw new IllegalArgumentException("Must input at least one resistor.");
		}
		for (Component res:resistors) {
			/* quick check to make sure only resistances get added to the total */
			if(res.getClass()==Resistor.class) {
				parallelR+=1/(((Resistor)res).getR());
			}
		}
		return 1/parallelR;
	}
	
	protected void printCharacteristics() {
		System.out.println("Ground voltage is located at Node "+this.ground+".");
		System.out.println("Total voltage in circuit is: "+this.totalV+ " Volts.");
		System.out.println("Total resistance in circuit is: "+this.totalR+" Ohms.");
		System.out.println("Total current is: "+this.totalV/this.totalR+" Amps.");
	}
	
}
