package circuit;
import java.util.ArrayList;

/** Assistant class to calculate circuit characteristics.
 * 
 * Input requires reference ground node and the components in the circuit.
 * 
 * I decided to compartmentalize this part of the program in a separate class to simplify the coding and maintenance, but when considering resource management it would likely just be methods within UserMain.
 * 
 * The resistor reduction algorithm used by this class is to first reduce resistors that are in parallel between the same two nodes to a single equivalent resistor between those nodes, then to reduce any serial resistors 
 * to a single equivalent resistor between the two outer-nodes which will then create more parallel resistors between the same two nodes, and so on.
 * 
 * 
 * @author Michael Sinclair.
 * @version 2.13
 * @since 8 January 2019.
*/

public class CircuitAnalysis {
	/* instance variables */
	private ArrayList<Node> nodeList;
	private ArrayList<Component> components;
	private int ground;
	/* to calculate characteristics */
	private double totalV;
	private double totalR;
	private int VoltageSources;
	
	protected CircuitAnalysis(int ground, ArrayList<Component> components, ArrayList<Node> nodeList) {
		/* initialize variables */
		this.ground = ground;
		this.totalR = 0.0;
		this.totalV = 0.0;
		this.VoltageSources = 0;
		this.components=components;
		this.nodeList=nodeList;
		/* PORTION BELOW IS UNDER CONSTRUCTION */
		///* copy the ArrayLists so that the User can continue operations after calculation, and to enable node specific calculations based on original list */
		//this.components = new ArrayList<>(components);
		//this.nodeList = new ArrayList<>(nodeList);
		///* have to point the new components list to the new node list and vice versa */
		///* first clear all attachments */
		//for(Node node:this.nodeList) {
		//	node.getAttachments().clear();
		//}
		///* now point copied components to the copied nodeList, and attach the components to the nodes */
		//for (Component comp:this.components) {
		//	for(Node node:this.nodeList) {
		//		/* if the component points to this iteration's node */
		//		if(comp.getNode1().getId()==node.getId()) {
		//			/* point it to the new copy object in this class nodeList */
		//			comp.setNode1(node);
		//			node.connect(comp);
		//		}
		//		/* same for second node */
		//		if(comp.getNode2().getId()==node.getId()) {
		//			comp.setNode2(node);
		//			node.connect(comp);
		//		}
		//	}
		//}
		/* test print */
		//System.out.println("Components"+components.toString());
		//System.out.println("Components"+this.components.toString());
		//System.out.println("Attachments passed in:");
		//for(Node node:nodeList) {
		//	System.out.println(node.toStringAttachments());
		//}
		//System.out.println("Attachments copied:");
		//for(Node node:this.nodeList) {
		//	System.out.println(node.toStringAttachments());
		//}
	}
	
	
	
	/* methods */
	
	/* automate circuit measurements */
	protected void analyzeCircuit() {
		/* find total voltage and count voltage sources */
		this.analyzeVoltage();
		/* find total resistance of the circuit */
		this.analyzeResistance();
		/* print out calculated circuit characteristics */
		this.printCharacteristics();
	}

	
	
	/* find total voltage - note that this program can currently only handle directly serial voltage (connected in series to each other) */
	protected void analyzeVoltage() {
		/* for each component */
		for (int i = 0; i<components.size();i++) {
			/* if it is a voltage */
			if (components.get(i).getClass() == Voltage.class) {
				/* get the voltage */
				this.totalV+=((Voltage)(components.get(i))).getV();
				/* count voltage sources */
				this.VoltageSources++;
			}
		}
	}
	
	
	
	/* find resistance */
	protected void analyzeResistance() {
		/* while more than 1 resistor exists */
		while(components.size()>this.VoltageSources+1) {
			/* reduce parallel resistors across the same nodes to one resistor */
			this.analyzeParallelSameNode();
			/* reduce serial resistors individually in the circuit */
			this.analyzeSeriesIndividually();
			System.out.println(this.components.toString());
		}
		
		/* now that there is only one resistor in the circuit iterate through the circuit */
		for (int i = 0; i<components.size();i++) {
			/* if it is a resistor */
			if (components.get(i) instanceof Resistor) {
				/* get the resistance - note this only executes once */
				this.totalR+=((Resistor)components.get(i)).getR();
			}
		}
	}
	
	
	
	/* reduce same-node parallel resistors to a single equivalent resistor */
	protected void analyzeParallelSameNode() {
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
									}
								}
							}
						}
						/* if a parallel connection was found between node n and m*/
						if(temp.size()>1) {
							/* create equivalent parallel resistor */
							Resistor equivalent = new Resistor(this.parallelResistors(temp),this.findNode(n),this.findNode(m));
							/* queue it for connection */
							toConnect.add(equivalent);
							/* queue resistors that need to be removed */
							for(Component remove:temp) {
								toRemove.add(remove);
							}
						}
						/* clear the list for future calculations */
						temp.clear();
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
	
	
	
	/* reduce any two serially connected resistors individually */
	protected void analyzeSeriesIndividually() {
		ArrayList<Component> toAdd = new ArrayList<>();
		ArrayList<Component> toRemove = new ArrayList<>();
		Node firstNode = null;
		Node secondNode = null;
		/* for each node */
		for(Node node:nodeList) {
			/* if there are 2 attachments that are both resistors */
			if (node.getAttachments().size()==2 && node.getAttachments().get(0) instanceof Resistor && node.getAttachments().get(1) instanceof Resistor) {
				/* find first and second node by Id - one must have a first node prior to the current node being tested and one must have a node after */
				if(node.getAttachments().get(0).getNode1().getId()<node.getAttachments().get(1).getNode1().getId()) {
					firstNode = node.getAttachments().get(0).getNode1();
					secondNode = node.getAttachments().get(1).getNode2();
				}
				else {
					firstNode = node.getAttachments().get(1).getNode1();
					secondNode = node.getAttachments().get(0).getNode2();
				}
				/* if not already queued for removal */
				if(!toRemove.contains(node.getAttachments().get(0))) {
					if(!toRemove.contains(node.getAttachments().get(1))) {
						toRemove.add(node.getAttachments().get(0));
						toRemove.add(node.getAttachments().get(1));
						toAdd.add(new Resistor(((Resistor)node.getAttachments().get(0)).getR()+((Resistor)node.getAttachments().get(1)).getR(),firstNode,secondNode));
					}
				}
			}
		}
		/* combine serial resistors individually - first remove them from the circuit */
		for(Component remove:toRemove) {
			remove.getNode1().disconnect(remove);
			remove.getNode2().disconnect(remove);
			components.remove(remove);
		}
		/* then add the equivalent resistors */
		for(Component addR:toAdd) {
			addR.getNode1().connect(addR);
			addR.getNode2().connect(addR);
			components.add(addR);
		}
	}
	
	
	
	/* Find ArrayList index - note ArrayList has built in remove with object parameter but I wanted to use this instead as I was encountering problems with the built in method */
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
	
	
	
	/* Determine if resistor already queued for removal, returns true to enable above loop if component is not already queued for removal */
	protected boolean queuedRemoval(Component resistor, ArrayList<Component> toRemove){
		/* for each component queued for removal */
		for(Component component:toRemove) {
			/* if the Id matches any resistor Id in the removal list, and for good measure check that it is a resistor */
			if(component.getId()==resistor.getId() && component.getClass()==Resistor.class) {
				/* return false to disable the above loop */
				return false;
			}
		}
		/* else return true */
		return true;
	}
	
	
	
	/* Find node based on id */
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
	
	
	
	/* Print circuit Characteristics */
	protected void printCharacteristics() {
		System.out.println("Ground voltage is located at Node "+this.ground+".");
		System.out.println("Total voltage in circuit is: "+this.totalV+ " Volts.");
		System.out.println("Total resistance in circuit is: "+this.totalR+" Ohms.");
		System.out.println("Total current is: "+this.totalV/this.totalR+" Amps.");
	}
	
	
	
	
	
	/* METHOD NO LONGER IN USE - changed algorithm for solving circuit problem - storing it in case it is useful in future */
	/* Reduce multiple serial resistors to a single equivalent resistor */
	protected void analyzeSerialResistances() {
		ArrayList<Component> temp = new ArrayList<>();
		ArrayList<Component> toRemove = new ArrayList<>();
		int nodalCase = 0;
		/* for each node */
		for(Node node1:nodeList) {
			/* compare to other nodes */
			for(Node node2:nodeList) {
				/* if not the same node and looking forwards */
				if(node1.getId()<node2.getId()) {
					/* if both nodes only have 2 attachments */
					if(node1.getAttachments().size()==2 && node2.getAttachments().size()==2) {
						/* iterate through the attachments that are resistors */
						for(int i = 0; i<2; i++) {
							for(int j = 0; j<2; j++) {
								/* if the components are not already queued for removal */
								if(this.queuedRemoval(node1.getAttachments().get(i),toRemove) && this.queuedRemoval(node2.getAttachments().get((j+1)%2),toRemove)) {
									/* if a common resistor is found between the nodes and both nodes only have 2 attachments, then the common resistor must be in series with the second nodes attached item */
									if (node1.getAttachments().get(i).getId()==node2.getAttachments().get(j).getId() && node1.getAttachments().get(i) instanceof Resistor) {
										/* if the second node's other attached item is also a resistor */
										if(node2.getAttachments().get((j+1)%2) instanceof Resistor) {
											/* queue them for equivalence calculation */
											temp.add(node1.getAttachments().get(i));
											temp.add(node2.getAttachments().get((j+1)%2));
											/* find the common node */
											if(temp.get(0).getNode1().getId() == temp.get(1).getNode1().getId()) {
												/* queue equivalent resistor nodes to be the non-common nodes */
												nodalCase = 1;
											}
											if(temp.get(0).getNode1().getId() == temp.get(1).getNode2().getId()) {
												/* queue equivalent resistor nodes to be the non-common nodes */
												nodalCase = 2;
											}
											if(temp.get(0).getNode2().getId() == temp.get(1).getNode1().getId()) {
												/* queue equivalent resistor nodes to be the non-common nodes */
												nodalCase = 3;
											}
											/* note chose to not use just plain else to verify the last condition is true, even though it is the only possible combination of common nodes left */
											if(temp.get(0).getNode2().getId() == temp.get(1).getNode2().getId()) {
												nodalCase = 4;
											}
										}
									}
								}
							}
						}
						/* if series resistors were found */
						if(temp.size()==2) {
							/* queue resistors for removal */
							toRemove.add(temp.get(0));
							toRemove.add(temp.get(1));
							Resistor equivalent = null;
							/* queue equivalent resistor to be added */
							if(nodalCase == 1) {
								/* first nodal case - shared 1st/1st node so connect equivalent resistor between both 2nd nodes */
								equivalent = new Resistor(((Resistor)temp.get(0)).getR()+((Resistor)temp.get(1)).getR(),temp.get(0).getNode2(),temp.get(1).getNode2());
							}
							if(nodalCase == 2) {
								/* second nodal case - shared 1st/2nd node so connect equivalent resistor between 2nd/1st nodes */
								equivalent = new Resistor(((Resistor)temp.get(0)).getR()+((Resistor)temp.get(1)).getR(),temp.get(0).getNode2(),temp.get(1).getNode1());
							}
							if(nodalCase == 3) {
								/* third nodal case - shared 2nd/1st node so connect equivalent resistor between 1st/2nd nodes */
								equivalent = new Resistor(((Resistor)temp.get(0)).getR()+((Resistor)temp.get(1)).getR(),temp.get(0).getNode1(),temp.get(1).getNode2());
							}
							/* chose not to use simple else for reason stated above */
							if(nodalCase == 4) {
								/* last nodal case - shared 2nd/2nd node so connect equivalent resistor between both 1st nodes */
								equivalent = new Resistor(((Resistor)temp.get(0)).getR()+((Resistor)temp.get(1)).getR(),temp.get(0).getNode1(),temp.get(1).getNode1());
							}
							components.add(equivalent);
							equivalent.getNode1().connect(equivalent);
							equivalent.getNode2().connect(equivalent);
							temp.clear();
						}
					}
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
						components.remove(i);
						/* need to consider that components has shrunk by 1 */
						i--;
					}
				}
			}
		}
	}
	
}

	
	
	
	
	
	
