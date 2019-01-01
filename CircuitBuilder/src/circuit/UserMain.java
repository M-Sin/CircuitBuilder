package circuit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
  
/**
 * Main function that creates a circuit, and takes input from user to add resistors or voltage sources to the circuit, and display components within the circuit.
 * 
 * Plan to add functionality that would allow inductors and capacitors to the circuit, using rectangular form complex calculations, and possibly phasors.
 * 
 * Plan to add functionality that will allow voltage sources to be placed anywhere within the circuit.
 * 
 * Plan to add functionality that will calculate voltages at each node and current leaving each node.
 * 
 * Plan to add functionality that will allow calculate to provide circuit characteristics, then allow the user to add more components if desired rather than end the program.
 * 
 * Plan to add try/catch blocks to correct errors during runtime.
 * 
 * Plan to add functionality to process Y-Delta transformations for resistors that can't be serial or parallel calculated.
 * 
 * V2.0
 * 
 * @author Michael Sinclair.
 * @version 2.0
 * @since 31 December 2018.
*/

public class UserMain {
    
    /*Instance variables.*/
    
    /**Need to take input from user.*/
    protected static Scanner user;
    
    /**Need dynamic node list to ensure only one node exists per node id.*/
    protected static ArrayList<Node> nodeList;
    
    /**Constructor to initialize instance variables.*/
    protected UserMain(){
        UserMain.user = new Scanner(System.in);
        UserMain.nodeList = new ArrayList<>();    
    }
    /**Main method
     * @param args.*/
	public static void main(String[] args){
        
        /*Create objects in main.*/
        Circuit cir = Circuit.getInstance();
        @SuppressWarnings("unused")
		UserMain instance = new UserMain();
        
        /*Instruct user on  how to use program.*/
        System.out.println("Welcome to the circuit builder program.");
        System.out.println("Input 'add' to add components into the circuit.");
        System.out.println("Input 'edit' to remove components from the circuit.");
        System.out.println("Input 'display' to display components currently in the circuit.");
        System.out.println("Input 'calculate' to determine total resistance and current in circuit.");
        System.out.println("Input 'end' to end the program.");
        System.out.println("");
        System.out.println("Input resistors (R) and voltage sources (V) into the circuit by the following syntax:");
        System.out.println("R/V X Y Z");
        System.out.println("R indicates a resistor and V indicates a voltage source.");
        System.out.println("X is an integer indicating the first node attached to component.");
        System.out.println("Y is an integer indicating the second node attached to component.");
        System.out.println("Z a double indicating the resistance in Ohms or Voltage in volts."); 
        System.out.println("For example: R 1 2 10 will add a resistor connected to nodes 1 and 2 with a resistance of 10 Ohms.");
        System.out.println("");
        System.out.println("Rules:");
        System.out.println("Voltage/Resistor values must be non-zero and Resistor values must also be non-negative. Voltage polarity is directed to increasing node Id.");
        System.out.println("Calculation function will assume that nodes are ordered and sequential from 0 to N-1 where N is the total number of nodes.");
        System.out.println("Voltage sources cannot be placed in parallel with eachother.");
        System.out.println("");
        System.out.println("V2.0 Notes:");
        System.out.println("Resistors must be connected serially or in parallel. This program does not currently support connections that are neither.");
        System.out.println("Currently the program only supports purely directly serial voltage sources, one of which must be between nodes 0 and 1.");
        System.out.println("Voltages may not be connected in parallel with resistors across the same two nodes and voltages must be placed directly in series with each other only.");
        
        /*Request user input.*/
        String input = UserMain.user.nextLine();
        
        /*While the program is not requested to end.*/
         while (!"end".equals(input)){
        	 
        	 if ("add".equals(input)) {
        		 
        		 System.out.println("Add a resistor or a voltage.");
        	    input = UserMain.user.nextLine();
	            /* If resistor is being added - note strict input validation to be added later. */
	            if (input.charAt(0) == 'r'||input.charAt(0) == 'R' ){
	            	int firstNode = 0;
	            	int secondNode=0;
	            	double rVal=0.0;
	            	boolean inputValidation = false;
	                /*Split input into various fields.*/
	            	while(!inputValidation) {
		            	try {
			                String[] inputSplit = input.split(" ");
			                firstNode = Integer.parseInt(inputSplit[1]);
			                secondNode = Integer.parseInt(inputSplit[2]);
			                rVal = Double.parseDouble(inputSplit[3]);
		            	} catch (Exception e) {
		            		System.out.println("Invalid input. Resistor syntax is R X Y Z. Try again.");
		            		input = UserMain.user.nextLine();
		            	}
		            	break;
	            	}
	                
	                /* create nodes if they do not already exist*/
	                NodeChecker evaluate = new NodeChecker(firstNode,secondNode,nodeList);
	                @SuppressWarnings("unused")
					Node node1 = evaluate.getCheckedNode1();
	                @SuppressWarnings("unused")
	                Node node2 = evaluate.getCheckedNode2();
	                
	                /*Find list index now that the node is definitely in the array.*/
	                int index1 = evaluate.findIndex(1);
	                int index2 = evaluate.findIndex(2);
	
	                /*Create add resistor to circuit.*/
	                Resistor res = new Resistor(rVal,nodeList.get(index1),nodeList.get(index2));
	                cir.addComponent(res);
	                /* track connections through nodes */
	                nodeList.get(index1).connect(res);
	                nodeList.get(index2).connect(res);
	                
	                System.out.println("Added Resistor: "+res.toString());
	                
	            }
	            
	            /* If voltage source is being added - note that again strict validation to be added later. */
	            else if (input.charAt(0) == 'v'||input.charAt(0) == 'V'){
	            	int firstNode = 0;
	            	int secondNode=0;
	            	double vVal=0.0;
	                /*Split input into various fields.*/
	            	try {
		                String[] inputSplit = input.split(" ");
		                firstNode = Integer.parseInt(inputSplit[1]);
		                secondNode = Integer.parseInt(inputSplit[2]);
		                vVal = Double.parseDouble(inputSplit[3]);
	            	} catch (Exception e) {
	            		System.out.println("Invalid input. Voltage syntax is V X Y Z. Try again.");
	            		input = UserMain.user.nextLine();
	            	}
	                
	                /* create nodes if they do not already exist*/
	                NodeChecker evaluate = new NodeChecker(firstNode,secondNode,nodeList);
	                @SuppressWarnings("unused")
					Node node1 = evaluate.getCheckedNode1();
	                @SuppressWarnings("unused")
	                Node node2 = evaluate.getCheckedNode2();
	                
	                /*Find list index now that the node is definitely in the array.*/
	                int index1 = evaluate.findIndex(1);
	                int index2 = evaluate.findIndex(2);
	                
	                /*Create and add voltage source to circuit.*/
	                Voltage vol = new Voltage(vVal,nodeList.get(index1),nodeList.get(index2));
	                cir.addComponent(vol);
	                /* track connections through nodes */
	                nodeList.get(index1).connect(vol);
	                nodeList.get(index2).connect(vol);
	                
	                System.out.println("Voltage added: "+vol.toString());
	                
	            }
        	 }
	            
            /* option to remove components */
            
            else if ("edit".equals(input)){
            	System.out.println("Which component would you like to remove? Enter only the unique identifier with no spaces (Ex. R1 or V2):");
                /* store values */
            	input = UserMain.user.nextLine();
            	char[] question = input.toCharArray();
                char Letter = question[0];
                String Number="";
                for (int j = 1; j<question.length;j++){
                	Number += question[j];
                }
                
                /* if resistor requested */
                if (Letter == 'r' || Letter == 'R') {
                	boolean flag = false;
                	Resistor Check=null;
                	/*check if it is in the list */
                	for (int i = 0; i <cir.getComponents().size();i++){
                		/* if that component is a resistor */
                		if(cir.getComponents().get(i) instanceof Resistor){
                			Check = (Resistor)cir.getComponents().get(i);
                			if (Check.getId() == Integer.parseInt(Number)){
                				/* if it is a resistor and in the list, remove it */
                				cir.getComponents().get(i).getNode1().disconnect(cir.getComponents().get(i));
                				cir.getComponents().get(i).getNode2().disconnect(cir.getComponents().get(i));
                				cir.getComponents().remove(i);
                				System.out.println("Removed component.");
                				flag = true;
                				break;
                			}
                		}
                	}
                	if (!flag) {
                		System.out.println("Resistor not found.");
                		}
               }
                
                /* if voltage requested */
                else if (Letter == 'v' || Letter == 'V') {
                	boolean flag = false;
                	Voltage Check=null;
                	/*check if it is in the list */
                	for (int i = 0; i <cir.getComponents().size();i++){
                		/* if that component is a voltage */
                		if(cir.getComponents().get(i) instanceof Voltage){
                			Check = (Voltage)cir.getComponents().get(i);
                			if (Check.getId() == Integer.parseInt(Number)){
                				/* if it is a voltage and in the list, remove it */
                				cir.getComponents().get(i).getNode1().disconnect(cir.getComponents().get(i));
                				cir.getComponents().get(i).getNode2().disconnect(cir.getComponents().get(i));
                				cir.getComponents().remove(i);
                				System.out.println("Removed component.");
                				flag = true;
                				break;
                			}
                		}
                	}
                	if (!flag) {
                		System.out.println("Resistor not found.");
                		}
               }
                
                /* if bad input */
                else System.out.println("Input component not recognized.");
            }
        
            /*If 'display' is input - print out the circuit components.*/
            else if ("display".equals(input)){
            	if(cir.getComponents().size()>0) {
	            	System.out.println("");
            		System.out.println("Components in circuit are:");
            		System.out.println(cir.toString());
	            	System.out.println("");
            	}
            	else {
            		System.out.println("No Components have been added yet.");
            	}
            }
            
            /* calculate Total Current/Voltage */
            else if ("calculate".equals(input)) {
            	if(cir.getComponents().size()!=0) {
	            	/* get ground voltage */
                    System.out.println("");
	            	System.out.println("Where is the ground voltage? Enter the node ID number only.");
	                input = UserMain.user.nextLine();
	                /* input verification should be added here*/
	            	System.out.println("Calculating:");
	            	/* sort the ArrayList of components by node 1 and node 2 (smaller of both first) */
	            	/* needed for consistent calculations */
	            	
	            	for (int j = 0; j<cir.getComponents().size();j++) {
		            	for (int i = 0; i<cir.getComponents().size()-1;i++) {
		            		if (cir.getComponents().get(i).compare(cir.getComponents().get(i+1))>0) {
		            			/* if component nodes are disordered, swap them */
		            			Collections.swap(cir.getComponents(), i, i+1);
		            		}
		            	}
	            	}
	            	
	            	/*Display ordered components */
	            	System.out.println("");
            		System.out.println("Components in circuit are:");
	            	System.out.println(cir.toString());
	            	System.out.println("");
	            	
	            	CircuitAnalysis Calculate = new CircuitAnalysis(Integer.parseInt(input), cir.getComponents(), nodeList);
	            	Calculate.analyzeCircuit();           	
	            	System.out.println("");
	            	break;
            	}
            	else {
            		System.out.println("Must have components in circuit before calculating.");
            	}
            }
        
            /*Strict input validation not required, but this will allow wildly incorrect inputs to loop back.*/
            else{
                System.out.println("Invalid input.");
            }
        /*Request next instruction.*/
        input = UserMain.user.nextLine();
        }
         
    /* Below shows that if two components are connected to the same node, 
     * they are in fact connected to exactly the same node (object) and not 
     * just nodes with the same id. In other words, nodes 
     * only exist as single objects.*/
    
    /*System.out.println("Printing node list to show no duplicate nodes exist.");
    for (Node node : nodeList){
        System.out.println(node.toString());
    }*/
         
    /*Program end.*/     
    System.out.println("All Done");
    }
}
