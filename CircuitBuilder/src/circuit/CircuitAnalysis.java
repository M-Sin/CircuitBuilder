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
		/* test print - remove later */
		for (Node node:nodeList) {
			System.out.println(node.toStringAttachments());
		}
		/* find total voltage and count voltage sources */
		this.analyzeVoltage();
		/* find total resistance of the circuit */
		this.analyzeResistance();
		/* test print - remove later */
		for (Node node:nodeList) {
			System.out.println(node.toStringAttachments());
		}
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
		System.out.println("Components size is " + components.size());
		
		/* while circuit has not been reduced to directly serial voltages and one mega-equivalent resistor */
		// UNDER CONSTRUCTION
		//while(components.size()>this.VoltageSources+1) {
			//System.out.println("Components " + components.size());
			/* combine any existing serial resistors */
			//UNDER CONSTRUCTION
			//this.analyzeSerialResistances();
			/* while parallel resistors still exist, combine them */
			// UNDER CONSTRUCTION
			//this.analyzeParallelMultiNode(nodeList.get(0));
		//}
		
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
										/* test print */
										System.out.println("I am adding here");
										/* add it to temporary list */
										temp.add(components.get(k));
										/* remove it from the nodes and from the component list */
										components.get(k).getNode1().connect(components.get(k));
										components.get(k).getNode2().connect(components.get(k));
										/* track which components needs to be removed in place of equivalent single resistor */
										removal.add(components.get(k));
										components.remove(components.get(k));
									}
									/* if a parallel connection was found */
									if(temp.size()>1) {
										Resistor temporary = new Resistor(this.parallelResistors(temp),nodeList.get(n),nodeList.get(m));
										components.add(temporary);
										temporary.getNode1().connect(temporary);
										temporary.getNode2().connect(temporary);
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
				removal.get(i).getNode1().disconnect(removal.get(i));
				/* test print */
				System.out.println("Removed "+removal.get(i));
				removal.get(i).getNode2().disconnect(removal.get(i));
				/* test print */
				System.out.println("Removed "+removal.get(i));
				components.remove(this.findIndex(components, removal.get(i)));
			}
		}
	}
	
	/* UNDER CONSTRUCTION */
	protected void analyzeParallelMultiNode(Node start) {
		/* for each component */
		for (Component comp:this.components) {
			/* looking forwards */
			if(comp.getNode1().getId()>start.getId())
				/* if it is a resistor */
				if(comp.getClass()==Resistor.class) {
					/* if its first node has more than 1 connection and so is in parallel */
					if (comp.getNode1().getAttachments().size()>2) {
						/* for each attachment */
						for (int i = 0; i<comp.getNode1().getAttachments().size();i++) {
							/* looking forwards */
							if (comp.getNode1().getAttachments().get(i).getNode2().getId()>comp.getNode1().getId()) {
								/* find the node to which they connect together again */
								ArrayList<ArrayList<Node>> Temp = new ArrayList<>();
								/* for each component attached in parallel to this node */
								for(Component comps:comp.getNode1().getAttachments()) {
									/* if it is a resistor - given that this program currently does not support parallel voltages */
									if (comps.getClass()==Resistor.class){
										/* if not the 'most parallel' meaning stretching across the farthest nodes */
										
									}
								}
							}
						}
					}	
				}
			}
		}
	
	/* UNDER CONSTRUCTION */
	protected void analyzeSerialResistances() {
		/* temporary ArrayList to store components to be removed */
		ArrayList<Component> temp = new ArrayList<>();
		/* for each component */
		for(int j = 0;j<components.size();j++) {
			/* for each other component */
			for(int i = 0; i<components.size();i++) {
				/* node 0 requires a special case - would consider also eliminating the 0th node as an ID */
				if(components.get(j).getNode1().getId()!=0) {
					/* obviously do not consider the same component */
					if(i!=j && i<j) {
						System.out.println("1");
						/* for two resistors */
						if(components.get(i).getClass() == Resistor.class && components.get(j).getClass()==Resistor.class) {
							/* if not already reduced (this method will need to be run repeatedly if multiple resistors are in series */
							if(!temp.contains(components.get(i))&&!temp.contains(components.get(j))) {
								System.out.println("2");
								System.out.println("comp1 node2ID "+ components.get(i).getNode2().getId());
								System.out.println("comp2 node1ID "+ components.get(j).getNode1().getId());
								System.out.println("attachments comp1 "+ components.get(i).getNode2().getAttachments().size());
								System.out.println("attachments comp2 "+ components.get(j).getNode1().getAttachments().size());
								/* condition for two serial resistors */
								if(components.get(i).getNode2().getId() == components.get(j).getNode1().getId() && components.get(i).getNode2().getAttachments().size()==2 && components.get(j).getNode1().getAttachments().size()==2) {
									temp.add(components.get(i));
									temp.add(components.get(j));
									Resistor equivalent = new Resistor(((Resistor)components.get(i)).getR(),((Resistor)components.get(i)).getNode1(),((Resistor)components.get(j)).getNode2());
									components.add(equivalent);
									System.out.println("Added: "+equivalent.toString());
								}
							}
						}
					}
				}
				else {
					/* obviously do not consider the same component */
					if(i!=j && i<j) {
						System.out.println("1");
						/* for two resistors */
						if(components.get(i).getClass() == Resistor.class && components.get(j).getClass()==Resistor.class) {
							/* if not already reduced during THIS iteration of this method (this method will need to be run repeatedly if multiple resistors are in series */
							if(!temp.contains(components.get(i))&&!temp.contains(components.get(j))) {
								System.out.println("2");
								System.out.println("comp1 node2ID "+ components.get(i).getNode2().getId());
								System.out.println("comp2 node2ID "+ components.get(j).getNode2().getId());
								System.out.println("attachments comp1 "+ components.get(i).getNode2().getAttachments().size());
								System.out.println("attachments comp2 "+ components.get(j).getNode2().getAttachments().size());
								/* condition for two serial resistors under special case where second resistor node 1 is 0 and node 2 is node N*/
								if(components.get(i).getNode2().getId() == components.get(j).getNode2().getId() && components.get(i).getNode2().getAttachments().size()==2 && components.get(j).getNode2().getAttachments().size()==2) {
									temp.add(components.get(i));
									temp.add(components.get(j));
									Resistor equivalent = new Resistor(((Resistor)components.get(i)).getR(),((Resistor)components.get(i)).getNode1(),((Resistor)components.get(j)).getNode2());
									components.add(equivalent);
									System.out.println("Added: "+equivalent.toString());
								}
							}
						}
					}
				}
			}
		}
		/* remove resistors replaced by single equivalent resistors */
		for(Component comp:temp) {
			components.remove(comp);
			/* test print */
			System.out.println("Removed "+comp.toString());
		}
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
