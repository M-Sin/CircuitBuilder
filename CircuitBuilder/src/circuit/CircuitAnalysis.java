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
 * I decided to compartmentalize this part of the program in a separate class to simplify the coding and maintenance, but when considering resource management it would likely just be methods within UserMain.
 * 
 * 
 * @author Michael Sinclair.
 * @version 0.1.
 * @since 21 December 2018.
* */

public class CircuitAnalysis {
	/* instance variables */
	private ArrayList<Node> nodeList;
	private ArrayList<Component> components;
	private int ground;
	private double totalV;
	private double totalR;
	private int VoltageSources;
	
	protected CircuitAnalysis(int ground, ArrayList<Component> components, ArrayList<Node> nodeList) {
		/* initialize variables */
		this.ground = ground;
		this.components = components;
		this.nodeList = nodeList;
		this.totalR = 0.0;
		this.totalV = 0.0;
		this.VoltageSources = 0;
		}
	
	/* methods */
	
	/* automate circuit measurements */
	protected void analyzeCircuit() {
		/* find total voltage and count voltage sources */
		this.analyzeVoltage();
		/* find total resistance of the circuit */
		this.analyzeResistance();
		/* print out equivalent components */
		System.out.println(components.toString());
		/* print out calculated circuit characteristics */
		this.printCharacteristics();
	}

	/* find total voltage */
	protected void analyzeVoltage() {
		/* note that this program can currently only handle directly serial voltage (connected in series to each other) */
		/* for each component */
		for (int i = 0; i<components.size();i++) {
			/* if it is a voltage */
			if (components.get(i).getClass() == Voltage.class) {
				/* get the voltage */
				this.totalV+=((Voltage)(components.get(i))).getV();
				this.VoltageSources++;
			}
		}
	}
	
	/* find resistance */
	protected void analyzeResistance() {
		/* combine parallel resistors between the same two nodes into one equivalent resistor */
		this.analyzeParallelSameNode();
		
		/* test print */
		System.out.println("Circuit contains "+components.size());
		
		/* combine serial and multi-node parallel resistors until only 1 mega-equivalent resistor remains */
		//to do
		
		/* now that all resistors are serial resistors, for each component */
		for (int i = 0; i<components.size();i++) {
			/* if it is a resistor */
			if (components.get(i) instanceof Resistor) {
				/* get the resistance and sum them all together */
				this.totalR+=((Resistor)components.get(i)).getR();
			}
		}
	}
	
	/* reduce parallel resistors to a single equivalent resistor */
	protected void analyzeParallelSameNode() {
		/* starting with all resistors connected between the same two nodes */
		ArrayList<Component> temp = new ArrayList<>();
		ArrayList<Component> toRemove = new ArrayList<>();
		ArrayList<Component> toConnect = new ArrayList<>();
		/* for each node */
		for (int n = 0; n<nodeList.size();n++) {
			/* find components connected to each other node */
			for (int m = 0; m<nodeList.size();m++) {
				/* components cannot have the same node on both sides & don't want to do the same two nodes twice */
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
										System.out.println("Temp stored: "+components.get(k).toString());
									}
								}
							}
						}
						/* if a parallel connection was found between node n and m*/
						if(temp.size()>1) {
							/* test print */
							System.out.println("Node1 "+n+" Node2 "+m);
							/* create equivalent parallel resistor */
							Resistor equivalent = new Resistor(this.parallelResistors(temp),this.findNode(n),this.findNode(m));
							/* queue it for connection */
							toConnect.add(equivalent);
							/* queue resistors that need to be removed */
							for(Component remove:temp) {
								toRemove.add(remove);
							}
							temp.clear();
							/* test print */
							System.out.println("Temp cleared.");
						}
						temp.clear();
						/* test print */
						System.out.println("Temp cleared.");
					}
				}
			}
		/* remove resistors to be replaced by single equivalent resistor */
		/* if there are items to be removed */
		if(toRemove.size()>0) {
			/* for each component to be removed */
			for (Component remove:toRemove) {
				/* for each component */
				for(int i = 0; i <components.size();i++) {
					/* if the component is a resistor and it is in the list of resistors to be removed */
					if(components.get(i).getId()==remove.getId()&&components.get(i) instanceof Resistor) {
						/* remove it from components */
						remove.getNode1().disconnect(remove);
						remove.getNode2().disconnect(remove);
						/* test print */
						System.out.println("Removed "+components.get(i).toString());
						components.remove(i);
						/* need to consider that components has shrunk by 1 */
						i--;
					}
				}
			}
		}
		/* attach equivalent resistors */
		for(Component comp:toConnect) {
			components.add(comp);
			comp.getNode1().connect(comp);
			comp.getNode2().connect(comp);
		}
	}
	
	/* UNDER CONSTRUCTION */
	protected void analyzeParallelMultiNode(Node start) {

		}
	
	/* UNDER CONSTRUCTION */
	protected void analyzeSerialResistances() {

	}
	
	/* Find ArrayList index */
	protected int findIndex(ArrayList<Component> findList, Component find) {
		int i;
		/* iterate through ArrayList until object is found */
		for (i = 0;i<findList.size();i++) {
			if(findList.contains(find)) {
				break;
			}
		}
		return i;
	}
	
	/* find node based on id */
	protected Node findNode(int id) {
		/* value to store index */
		int i  = 0;
		/* for each node */
		for(Node node:nodeList) {
			/* if it does not equal the desired node */
			if(node.getId()!=id) {
				/* increase the index */
				i++;
			}
			/* if it does */
			else {
				/* stop searching */
				break;
			}
		}
		return nodeList.get(i);
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
	
	/* print circuit Characteristics */
	protected void printCharacteristics() {
		System.out.println("Ground voltage is located at Node "+this.ground+".");
		System.out.println("Total voltage in circuit is: "+this.totalV+ " Volts.");
		System.out.println("Total resistance in circuit is: "+this.totalR+" Ohms.");
		System.out.println("Total current is: "+this.totalV/this.totalR+" Amps.");
	}
	
}
