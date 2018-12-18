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
 * If resources were a concern however, I likely would not do so as it duplicates a lot of memory locations.
 * 
 * Compartmentalizing this will also let me copy data out of UserMain rather than directly operate on it, which will allow the calculation function not need to end the program
 * and instead provide a snapshot of current circuit characteristics, then allow more components to be added.
 * 
 * @author Michael Sinclair.
 * @version 0.1.
 * @since 18 December 2018.
* */

public class NodalAnalysis {
	/* instance variables */
	private ArrayList<Node> nodeList;
	private ArrayList<Component> simplifiedCircuit;
	private ArrayList<Component> components;
	private int ground;
	private double totalV;
	private double totalR;
	
	protected NodalAnalysis(int ground, ArrayList<Component> components, ArrayList<Node> nodeList) {
		/* initialize variables */
		this.ground = ground;
		this.components = components;
		this.nodeList = nodeList;
		this.simplifiedCircuit = new ArrayList<Component>();
		this.totalR = 0.0;
		this.totalV = 0.0;
		}
	
	/* methods */

	/* node analysis */
	protected void analyzeCircuit() {
		/* find total voltage */
		for (int i = 0; i<components.size();i++) {
			if (components.get(i).getClass() == Voltage.class) {
				this.totalV+=((Voltage)(components.get(i))).getV();
			}
		}
		System.out.println("Total voltage in circuit is: "+this.totalV+ " Volts.");
		
		/* build simplified circuit */
		
		/* starting with all resistors connected between the same two nodes */
		
		/* for each node */
		for (int n = 0; n<nodeList.size();n++) {
			/* find components connected to each other node */
			for (int m = 0; m<nodeList.size();m++) {
				/* components cannot have the same node on both sides */
				if (n!=m && n<m) {
					ArrayList<Component> temp = new ArrayList<>();
					/* for each component */
					for (int k = 0;k<components.size();k++) {
						if(components.get(k).getNode1().getId() == n && components.get(k).getNode2().getId() == m) { 
							/* if it is a resistor */
							if (components.get(k).getClass() == Resistor.class) {
								/* if it is between the two nodes */
								if(components.get(k).getNode1().getId() == n && components.get(k).getNode2().getId() == m) {
									temp.add(components.get(k));
								}
							}
							/* if not a resistor, then it is a voltage component */
							else {
									this.simplifiedCircuit.add(components.get(k));
								}
							}
					}
					/* find resistance between two nodes and insert single resistor into simplified circuit if at least one resistor was between the nodes*/
					if (temp.size()>0) {
						this.simplifiedCircuit.add(new Resistor(analyzeR(temp),nodeList.get(n),nodeList.get(m)));
					}
				}
			}
		}
		
		/* look for multi-node parallel resistors */	
		/* CURRENTLY UNDER CONSTRUCTION - ADDING FUNCTIONALITY TO ALLOW MULTI-NODE PARALLEL RESISTORS */
		for (Node node1:nodeList) {
			/* find components connected to each other node */
			for (Node node2:nodeList) {
				/* components cannot have the same node on both sides, and in the simplified circuit only one component is connected between each node */
				if (node1!=node2 && node1.getId()<node2.getId() && node1.getId()!=node2.getId()+1) { 
					/* for each component */
					for (int k = 0;k<simplifiedCircuit.size()-1;k++) {
						/* test it against each other component */
						for (int p = 0; p<simplifiedCircuit.size()-1;p++) {
							/* if not the same component and both resistors */
							if(k!=p && simplifiedCircuit.get(k) instanceof Resistor && simplifiedCircuit.get(p) instanceof Resistor) {
								/* if any components share a first node but not a second node */
								if(simplifiedCircuit.get(k).testNode(simplifiedCircuit.get(p))) {
									this.multiNodeResistors(node1,node2);
								}
							}
						}
					}
				}
			}
		}
		
		/* analyze simplified circuit */
		totalR = this.seriesResistor(simplifiedCircuit);
		System.out.println("Total resistance in circuit is: "+this.totalR+" Ohms.");
		System.out.println("Total current is: "+this.totalV/this.totalR+" Amps.");
		
		for (int x = 0;x<simplifiedCircuit.size();x++) {
			System.out.println("Connections" + this.simplifiedCircuit.get(x).getNode1().getConnections());
		}
						
	}
	protected void analyzeSpecifics() {
		System.out.println("Ground voltage is located at Node "+this.ground+".");
		// to do - add more specifics for added functionality
		
		/* assign currents/resistances to nodes */
		/* this is part of functionality I plan to build into this program in order to assign voltages to each node */
		//for (int i = 0; i<nodeList.size()-1;i++) {
		//	this.nodeList.get(i).setCurrentRight(this.totalV/this.totalR);
		//	if (this.simplifiedCircuit.get(i).getClass()==Resistor.class) {
		//		this.nodeList.get(i).setResistanceRight(((Resistor)(this.simplifiedCircuit.get(i))).getR());
		//	}
		//}
		
		/* assign voltages to nodes */
		// to do
	}
	
	protected void serialNodeResistors() {
		for(int i = 0;i<simplifiedCircuit.size();i++) {
			if(true) {
				
			}
		}
	}
	
	/* recursive method for adding up multi-node parallel resistors */
	protected void multiNodeResistors(Node nodal1, Node nodal2) {
		/* have to test if further parallel resistors exist downstream */
		for (Node node1:nodeList) {
			/* find components connected to each other node */
			for (Node node2:nodeList) {
				/* components cannot have the same node on both sides, and in the simplified circuit only one component is connected between each node now checking that each first node is further downstream as well */
				if (node1!=node2 && node1.getId()<node2.getId() && node1.getId()!=node2.getId()+1 && node1.getId()>nodal1.getId()) { 
					/* for each component */
					for (int k = 0;k<simplifiedCircuit.size()-1;k++) {
						/* test it against each other component */
						for (int p = 0; p<simplifiedCircuit.size()-1;p++) {
							/* if not the same component and both resistors */
							if(k!=p && simplifiedCircuit.get(k) instanceof Resistor && simplifiedCircuit.get(p) instanceof Resistor) {
								/* if any components share a first node but not a second node and are part of this iteration */
								if(simplifiedCircuit.get(k).testNode(simplifiedCircuit.get(p)) && simplifiedCircuit.get(k).getNode1().getId() == node1.getId() && simplifiedCircuit.get(p).getNode1().getId() == node1.getId()) {
									/* check if any further resistors parallel resistors exist */
									ArrayList<Component> temp = new ArrayList<>();
									double testR = 0.0;
									for (int q = 0;q<simplifiedCircuit.size();q++) {
										if(simplifiedCircuit.get(q).getNode1().getId()<=nodal1.getId() && simplifiedCircuit.get(q).getNode2().getId()<=nodal2.getId()) {
											temp.add(simplifiedCircuit.get(q));
											testR+=((Resistor)(simplifiedCircuit.get(q))).getR();
										}
									}
									/* if FURTHER parallel resistors exist downstream - call function recursively */
									if (seriesResistor(temp)!=testR) {
										this.multiNodeResistors(node1, node2);
									}
									else {
										;
									}
								/* remove the above resistors from the simplified circuit */
								}	

							}
							/* calculate the new parallel equivalent resistor */
								
							/* add it to the circuit */
								
						}	
					}
				}
			}
		}
	}
	
	/* calculate resistance */
	protected double analyzeR(ArrayList<Component> resistors) {
		/* quick check to make sure the relevant list has a resistor as its first element*/
		if (resistors.get(0).getClass() == Resistor.class){	
			/* if more than one resistor, find total resistance */
			double total;
			if (resistors.size()>1) {
				total = this.parallelResistor(resistors);
			}
			/* if not total R is just the single resistor */
			else {
				total = ((Resistor)(resistors.get(0))).getR();
			}
			return total;
		}
		
		/* indicate if something went wrong */
		else {
			System.out.println("Error with relevant list.");
			return 0.0;
		}
	}
	
	/* Calculate parallel resistors */
	protected double parallelResistor(ArrayList<Component> resistors) {
		double parallelR = 0.0;
		if(resistors.size()==0) {
	        throw new IllegalArgumentException("Must input at least one resistor.");
		}
		for (Component res:resistors) {
			if(res.getClass()==Resistor.class) {
				parallelR+=1/(((Resistor)res).getR());
				/* disconnect resistor from nodes */
				res.getNode2().disconnect();
				res.getNode1().disconnect();
			}
		}
		return 1/parallelR;
	} 
	
	/* Calculate series resistors */
	protected double seriesResistor(ArrayList<Component> resistors) {
		double seriesR = 0.0;
		if(resistors.size()==0) {
	        throw new IllegalArgumentException("Must input at least one resistor.");
		}
		for (Component res:resistors) {
			if(res.getClass()==Resistor.class) {
				seriesR+=((Resistor)res).getR();
			}
		}
		return seriesR;
	}
}
