package circuit;
import java.util.ArrayList;
import java.util.Collections;

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
 * @version 2.410
 * @since 15 February 2019.
 */

public class CircuitAnalysis {
	/* instance variables */
	private ArrayList<Node> nodeList;
	private ArrayList<Component> components;
	/* store original lists in order to calculate specific information on them */
	private ArrayList<Component> originalComponents;
	private ArrayList<Node> originalNodes;
	private int ground;
	/* to calculate characteristics */
	private double totalV;
	private double totalR;
	private int voltageSources;
	/* to rewind resistor Id count for user to continue after calculations */
	private int countResistors;
	
	/** Only constructor for this class
	 * 
	 * @param int ground
	 * @param ArrayList<Component> comps
	 * @param ArrayList<Node> nodes
	 */
	public CircuitAnalysis(int groundId, ArrayList<Component> comps, ArrayList<Node> nodes) {
		/* initialize variables */
		originalComponents = comps;
		originalNodes = nodes;
		/* clear out any previous calculations on node/component specific information */
		for(Component original:originalComponents) {
			original.setCurrent(0.0);
		}
		for(Node original:originalNodes) {
			original.setVoltage(0.0);
			original.setCurrent(0.0);
		}
		/* initialize the rest of the variables */
		ground = groundId;
		totalR = 0.0;
		totalV = 0.0;
		voltageSources = 0;
		countResistors = 0;
		/* copy the ArrayLists so that the User can continue operations after calculation, and to enable node specific calculations based on original list */
		components = new ArrayList<>(comps);
		/* have to create new Node objects to avoid altering the input list - basically dereferencing the node objects from the input and creating clone objects of them with t he same id */
		nodeList = new ArrayList<>();
		for(Node node:nodes) {
			nodeList.add(new Node(node.getId()));
		}
		/* now point copied components to the copied nodeList, and attach the copied components to the copied nodes */
		for (Component comp:components) {
			for(Node node:nodeList) {
				/* if the component points to this iteration's node */
				if(comp.getNode1().getId()==node.getId()) {
					/* point it to the new copy object in this class nodeList */
					comp.setNode1(node);
					/* and connect it to the attached copy node */
					node.connect(comp);
				}
				/* same for second node */
				if(comp.getNode2().getId()==node.getId()) {
					comp.setNode2(node);
					node.connect(comp);
				}
			}
		}
    	
		/* sort the resistor nodes of the copies, maintain the ordering for the original list that the user input for their resistors */
    	/* sort the ArrayList of components by node 1 and node 2 (smaller of both first) - note that by construction, voltages will always have ordered nodes */
    	for (int j = 0; j<components.size();j++) {
        	for (int i = 0; i<components.size()-1;i++) {
        		if (components.get(i).compareTo(components.get(i+1))>0) {
        			/* if component nodes are disordered, swap them */
        			Collections.swap(components, i, i+1);
        		}
        	}
    	}
	}

	
	/* methods */
	
	/** Automates circuit measurements by calling analyzeVoltage(),analyzeResistance(),printCharactersitics() and reduces resistor count for resistors created for circuit calculations */
	public void analyzeCircuit() {
		/* find total voltage and count voltage sources */
		analyzeVoltage();
		/* find total resistance of the circuit */
		analyzeResistance();
		System.out.println("");
		/* print out calculated circuit characteristics */
		printCharacteristics();
		/* calculate node voltages and resistor currents */
		findSpecifics();
		/* rewind resistor count for user to continue altering circuit - for each resistor added, lower the global resistor id number to sync the number back with the user's circuit */
		for(int i=0;i<countResistors;i++) {
			Resistor.resnum--;
		}
	}

	
	
	/** Finds total voltage in the circuit - note that this program can currently only handle directly serial voltage (connected in series to each other) */
	protected void analyzeVoltage() {
		/* for each component */
		for (int i = 0; i<components.size();i++) {
			/* if it is a voltage */
			if (components.get(i).getClass() == Voltage.class) {
				/* get the voltage */
				totalV+=((Voltage)(components.get(i))).getV();
				/* count voltage sources */
				voltageSources++;
			}
		}
	}
	
	
	
	/** Finds total resistance in the circuit */
	protected void analyzeResistance() {
		/* while more than 1 resistor exists */
		while(components.size()>voltageSources+1) {
			/* reduce parallel resistors across the same nodes to one resistor */
			analyzeParallelSameNode();
			/* reduce serial resistors individually in the circuit */
			analyzeSeriesIndividually();
		}
		
		/* now that there is only one resistor in the circuit iterate through the circuit */
		for (int i = 0; i<components.size();i++) {
			/* if it is a resistor */
			if (components.get(i) instanceof Resistor) {
				/* get the resistance - note this only executes once */
				totalR+=((Resistor)components.get(i)).getR();
			}
		}
	}
	
	
	
	/** Reduces same-node parallel resistors to a single equivalent resistor - old method no longer in use - leaving in code for now */
	protected void analyzeParallelSameNodeOLDMETHOD() {
		ArrayList<Component> temp = new ArrayList<>();
		ArrayList<Component> toRemove = new ArrayList<>();
		ArrayList<Component> toConnect = new ArrayList<>();
		/* for each node */
		/* TODO eliminate second for loop to reduce time complexity */
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
							Resistor equivalent = new Resistor(parallelResistors(temp),findNode(n),findNode(m));
							/* for rewinding resistor id */
							countResistors++;
							/* queue it for connection */
							toConnect.add(equivalent);
							/* queue resistors that need to be removed */
							toRemove.addAll(temp);
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
	
	/** Method to make same node analysis more efficient with better time complexity, using ArrayLists to sort components based on their node connections
	 * TODO IS THIS MORE EFFICIENT THAN THE ABOVE METHOD?! It works now, check up on it later when more time
	 */
	public void analyzeParallelSameNode() {
		ArrayList<Component> toConnect = new ArrayList<Component>();
		ArrayList<Component> toRemove = new ArrayList<Component>();
		/* this ArrayList will store components of the same first node that also have the same second node */
		ArrayList<ArrayList<Component>> sameSecondNode = new ArrayList<ArrayList<Component>>();
		/* this ArrayList will store an ArrayList of components that have the same first node */
		ArrayList<ArrayList<Component>> sameFirstNode= new ArrayList<ArrayList<Component>>();
		/* instantiate each ArrayList - maximum size will be size of components list i.e. only serial components - chose to do this rather than dynamically create
		 * ArrayLists as with large number of components, this would result in a lot more checking to see if ArrayList is instantiated yet*/
		for(int i = 0; i<nodeList.size()*nodeList.size();i++) {
			sameFirstNode.add(new ArrayList<Component>());
			sameSecondNode.add(new ArrayList<Component>());
		}
		/* sort the components into this ArrayList based on their first node Id */
		for(Component component:components) {
			int indice1 = component.getNode1().getId();
			sameFirstNode.get(indice1).add(component);
		}
		/* now iterate through those ArrayLists to see if any are bigger than 2, since voltages cannot be parallel they must have resistors if they have multiple components */
		for(int i = 0; i<sameFirstNode.size();i++) {
			/* if there are parallel resistors */
			if (sameFirstNode.get(i).size()>1) {
				/* find components with the same second node */
				for(Component component:sameFirstNode.get(i)) {
					/* sort them into the sameSecondNode list */
					sameSecondNode.get(component.getNode2().getId()).add(component);
				}
				/* now for all sorted resistors */
				for(int j = 0; j<sameSecondNode.size();j++) {
					/* if there is more than one between the same two nodes */
					if(sameSecondNode.get(j).size()>1) {
						/* create equivalent parallel resistor */
						Resistor equivalent = new Resistor(parallelResistors(sameSecondNode.get(j)),findNode(sameSecondNode.get(j).get(0).getNode1().getId()),findNode(sameSecondNode.get(j).get(0).getNode2().getId()));
						/* for rewinding resistor id */
						countResistors++;
						/* queue it for connection */
						toConnect.add(equivalent);
						/* queue resistors that need to be removed */
						toRemove.addAll(sameSecondNode.get(j));
					}
				}
				sameSecondNode.clear();
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
	
	/** Method that reduces any two serially connected resistors individually */
	protected void analyzeSeriesIndividually() {
		ArrayList<Component> toAdd = new ArrayList<>();
		ArrayList<Component> toRemove = new ArrayList<>();
		Node firstNode = null;
		Node secondNode = null;
		/* can only perform this operation a single time before calling it again - resulted in errors created floating resistors that could not be reduced further otherwise */
		boolean doOnce = false;
		/* for each node */
		for(Node node:nodeList) {
			/* if there are 2 attachments that are both resistors */
			if (node.getAttachments().size()==2 && node.getAttachments().get(0) instanceof Resistor && node.getAttachments().get(1) instanceof Resistor && !doOnce) {
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
						/* for rewinding resistor id */
						countResistors++;
					}
				}
				/* prevent program from combining more than two serial resistors at the same time */
				doOnce = true;
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
	
	/** Method to calculate specific information about nodes/components - under construction */
	/* TODO complete */
	protected void findSpecifics() {
		/* program requires 0th node to 1st node be a voltage source (with nothing in parallel) - first set node 0 to 0V and current leaving to full current - ground voltage will be adjusted later */
		for (Node node0:originalNodes) {
			if(node0.getId()==0) {
				node0.setVoltage(0.0);
				node0.setCurrent(totalV/totalR);
			}
		}
	}
	
	/** find resistance between two specific nodes - under construction
	 * @param Node node1
	 * @param Node node2
	 * @return double resistance */
	protected double findSpecificNodeResistance(Node node1, Node node2) {
		double resistance = 0.0;
		
		
		
		return resistance;
	}
	
	
	/** Find ArrayList index - note ArrayList has built in remove with object parameter but I wanted to use this instead as I was encountering problems with the built in method 
	 * @param ArrayList<Component> findList
	 * @param Component find
	 * @return int */
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
	
	/** Determine if resistor already queued for removal, returns true to enable above loop if component is not already queued for removal 
	 * @param Component resistor
	 * @param ArrayList<Component> toRemove
	 * @return boolean*/
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
	
	
	/** Find node based on id 
	 * @param int id
	 * @return Node*/
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
	
	
	
	/** Calculate parallel resistance from a list of resistors
	 * @param ArrayList<Component> resistors
	 * @return double*/
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
	
	
	
	/** Print circuit Characteristics */
	protected void printCharacteristics() {
		System.out.println("Ground voltage is located at Node "+ground+".");
		System.out.println("Total voltage in circuit is: "+totalV+ " Volts.");
		System.out.println("Total resistance in circuit is: "+totalR+" Ohms.");
		System.out.println("Total current is: "+totalV/totalR+" Amps.");
	}
	
	/* get methods for testing private instance variables */
	
	/** get nodeList
	 * @return ArrayList<Node> nodeList
	 */
	public ArrayList<Node> getNodeList(){
		return nodeList;
	}
	
	/** gets the list of components
	 * 
	 * @return ArrayList<Component> components
	 */
	public ArrayList<Component> getComponents(){
		return components;
	}

	/** get voltage
	 * @return double totalV
	 */
	public double getV() {
		return totalV;
	}
	
	/** gets the resistance of the circuit
	 * 
	 * @return double totalR
	 */
	public double getR() {
		return totalR;
	}
	
	/** gets the ground node id
	 * 
	 * @return int ground
	 */
	public int getG() {
		return ground;
	}
	
}

	
	
	
	
	
	
