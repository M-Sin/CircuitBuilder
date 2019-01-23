package circuit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
  
/**
 * Main function that creates a circuit, and takes input from user to add resistors or voltage sources to the circuit, and display components within the circuit.
 * 
 * Plan to add functionality that will check that the user has input a complete circuit (complete path from node 0 to node 0 through the circuit)
 * 
 * Plan to add functionality that would allow inductors and capacitors to the circuit, likely using rectangular form complex calculations, and possibly phasors.
 * 
 * Plan to add functionality that will allow voltage sources to be placed anywhere within the circuit.
 * 
 * Plan to add functionality that will calculate voltages at each node and current leaving each node.
 * 
 * Plan to add functionality to process Y-Delta transformations for resistors that can't be serial or parallel calculated.
 * 
 * @author Michael Sinclair.
 * @version 2.301
 * @since 23 January 2019.
*/

public class UserMain {
    
    /*Instance variables.*/
    
    /* Need to take input from user */
    protected static Scanner user;
    
    /* Need dynamic node list to ensure only one node exists per node id */
    protected static ArrayList<Node> nodeList;
    
    /* Constructor to initialize instance variables */
    protected UserMain(){
        UserMain.user = new Scanner(System.in);
        UserMain.nodeList = new ArrayList<>();    
    }
    /**Main method that interacts with user
     * @param args.
     * */
	public static void main(String[] args){
        
        /* Create objects in main */
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
        System.out.println("V2.301 Notes:");
        System.out.println("Resistors must be connected serially or in parallel. This program does not currently support connections that are neither.");
        System.out.println("Currently the program only supports purely directly serial voltage sources, one of which must be between nodes 0 and 1.");
        System.out.println("Voltages may not be connected in parallel with resistors.");
        System.out.println("Currently it is the user's responsibility to enter a complete circuit.");
        System.out.println("");
        
        /* Request user input with input verification */
        String input = null;
        while(true) {
			try {
				/* test inputs */
				input = UserMain.user.nextLine();
				if(input.equals("add")) {
					break;
				}
				if(input.equals("edit")) {
					break;
				}
				if(input.equals("display")) {
					break;
				}
				if(input.equals("calculate")) {
					break;
				}
				if(input.equals("end")) {
					break;
				}
				/* if not a viable input, allow user to retry */
				throw new IllegalArgumentException("Enter a valid input.");
			} catch (Exception e) {
				/* instruct user on error and to retry */
				System.out.println(e);
				System.out.println("Retry:");
			}
        }
        
        /* While the program is not requested to end */
         while (!"end".equals(input)){
        	 
        	 /* if adding a component */
        	 if ("add".equals(input)) {
        		 
        		 /* request details with input verification */
        		System.out.println("Add a resistor or a voltage.");
    			input = UserMain.user.nextLine();
        		while(true){
	        			try {
	        			String[] testCase = input.split(" ");
	        			/* if not the proper number of data entities in order to test further down */
		                if(testCase.length!=4) {
		                	/* throw exception to keep user within loop */
		                	throw new IllegalArgumentException("Input must be R/V X Y Z.");
		                }
		                /* otherwise allow program to proceed */
		                break;
	        		} catch (IllegalArgumentException e) {
	    				/* instruct user on error and to retry */
	        			System.out.println(e);
	        			System.out.println("Try again:");
	            	    input = UserMain.user.nextLine();
	        		}
        		}
        		
	            /* If resistor is being added */
	            if ((input.charAt(0) == 'r'||input.charAt(0) == 'R')&&input.charAt(1)==' '){
	            	int firstNode = 0;
	            	int secondNode=0;
	            	double rVal=0.0;
	            	
	                /* Split input into various fields with input validation */
	            	while(true) {
		            	try {
			                String[] inputSplit = input.split(" ");
		        			/* if not the proper number of data entities */
			                if(inputSplit.length!=4) {
			                	/* throw exception to keep user within loop */
			                	throw new IllegalArgumentException("Input must be R X Y Z.");
			                }
			                /* store the data */
			                String testLetter = inputSplit[0];
			                firstNode = Integer.parseInt(inputSplit[1]);
			                secondNode = Integer.parseInt(inputSplit[2]);
			                rVal = Double.parseDouble(inputSplit[3]);
			                /* if not a resistor entered */
			                if (!testLetter.equals("r")) {
			                	if(!testLetter.equals("R")) {
				                	/* throw exception to keep user within loop */
			                		throw new IllegalArgumentException("You must enter a resistor.");
			                	}
			                }
			                /* no negative resistances - testing against a double so do not test against exactly 0 due to imprecision in floating point numbers */
			                if(rVal < 0.00001){
			                	throw new IllegalArgumentException("You enterred a resistance of "+rVal+". Resistances must be positive and non-zero.");
			                }
			                /* component must be connected to two different nodes */
			                if(firstNode == secondNode) {
			                	throw new IllegalArgumentException("Components must be connected to two different nodes.");
			                }
			                /* only reached if no exceptions are thrown */
			                break;
			            /* note could just catch all exceptions since the retry message is the same, but that is bad practice */
		            	} catch (NumberFormatException e) {
		    				/* instruct user on error and to retry */
		            		System.out.println(e);
		            		System.out.println("Invalid input. Resistor syntax is R X Y Z. Input a resistor:");
		            	    input = UserMain.user.nextLine();
		            	} catch(IllegalArgumentException e) {
		    				/* instruct user on error and to retry */
		            		System.out.println(e);
		            		System.out.println("Invalid input. Resistor syntax is R X Y Z. Input a resistor:");
		            	    input = UserMain.user.nextLine();
		            	} catch (ArrayIndexOutOfBoundsException e) {
		    				/* instruct user on error and to retry */
		            		System.out.println(e);
		            		System.out.println("Invalid input. Resistor syntax is R X Y Z. Input a resistor:");
		            	    input = UserMain.user.nextLine();
		            	}
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
	            
	            /* If voltage source is being added */
	            else if ((input.charAt(0) == 'v'||input.charAt(0) == 'V')&&input.charAt(1)==' '){
	            	int firstNode = 0;
	            	int secondNode=0;
	            	double vVal=0.0;
	            	
	                /* Split input into various fields with input validation */
	            	while(true) {
		            	try {
			                String[] inputSplit = input.split(" ");
		        			/* if not the proper number of data entities */
			                if(inputSplit.length!=4) {
			                	/* throw exception to keep user within loop */
			                	throw new IllegalArgumentException("Input must be R X Y Z.");
			                }
			                /* store the data */
			                String testLetter = inputSplit[0];
			                firstNode = Integer.parseInt(inputSplit[1]);
			                secondNode = Integer.parseInt(inputSplit[2]);
			                vVal = Double.parseDouble(inputSplit[3]);
			                /* if not a voltage entered */
			                if (!testLetter.equals("v")) {
			                	if(!testLetter.equals("V")) {
				                	/* throw exception to keep user within loop */
			                		throw new IllegalArgumentException("You must enter a voltage.");
			                	}
			                }
			                /* component must be connected to two different nodes */
			                if(firstNode == secondNode) {
			                	throw new IllegalArgumentException("Components must be connected to two different nodes.");
			                }
			                /* only reached if no exceptions are thrown */
			                break;
			            /* note could just catch all exceptions since the retry message is the same, but that is bad practice */
		            	} catch (NumberFormatException e) {
		            		/* instruct user on error and to retry */
		            		System.out.println(e);
		            		System.out.println("Invalid input. Voltage syntax is V X Y Z. Input a resistor:");
		            	    input = UserMain.user.nextLine();
		            	} catch(IllegalArgumentException e) {
		            		/* instruct user on error and to retry */
		            		System.out.println(e);
		            		System.out.println("Invalid input. Voltage syntax is V X Y Z. Input a resistor:");
		            	    input = UserMain.user.nextLine();
		            	} catch (ArrayIndexOutOfBoundsException e) {
		            		/* instruct user on error and to retry */
		            		System.out.println(e);
		            		System.out.println("Invalid input. Voltage syntax is V X Y Z. Input a resistor:");
		            	    input = UserMain.user.nextLine();
		            	}
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
	            /* catch other bad inputs */
	            else {
	            	System.out.println("Invalid input. Enter a voltage source or resistor with the following syntax R/V X Y Z. Try again:");
	            	input = UserMain.user.nextLine();
	            }
        	 }
	            
            /* option to remove components */
            else if ("edit".equals(input)){
            	System.out.println("Which component would you like to remove? Enter only the unique identifier with no spaces (Ex. R1 or V2):");
                /* store values */
            	input = UserMain.user.nextLine();
            	/* store input */
            	char[] question = null;
            	/* initialize Letter with a dummy value */
            	char Letter = '\0';
            	String Number = "";
            	/* test user input */
            	while(true) {
	            	try {
	            		/* store each character separately */
	            		question = input.toCharArray();
	            		/* if the first character entered is not in fact a character */
	            		if(!Character.isLetter(question[0])) {
	            			/* instruct user on error and to retry */
	            			throw new IllegalArgumentException("Select a resistor with 'R' or a voltage with 'V'.");
	            		}
	            		Letter = question[0];
	            		/* find the Id of the requested value */
	            		for (int j = 1; j<question.length;j++){
	            			Number += question[j];
	            		}
	            		/* if not an integer, this will throw a NumberFormatException */
	            		Integer.parseInt(Number);
	            		/* if a voltage or resistor are not selected */
	            		if(Letter!='r') {
	            			if(Letter!='R') {
	            				if(Letter!='v') {
	            					if(Letter!='V') {
	            						throw new IllegalArgumentException("Select a resistor with 'R' or a voltage with 'V'.");
	            					}
	            				}
	            			}
	            		}
	            		/* if the Number string does not contain at least one character */
	            		if(Number.length()<1) {
	            			throw new IllegalArgumentException("Must enter the unique Id of the component you wish to remove.");
	            		}
	            		/* if no exceptions are thrown */
	            		break;
	            	} catch(IllegalArgumentException e) {
	            		/* instruct user on error and to retry */
	            		System.out.println(e);
	            		System.out.println("Invalid input. Enter only the Letter (R or V) and the number of the component you wish to remove. Try again:");
	            		/* clear the Number string or else previous values will still be held within the string */
	            		Number = "";
	            	    input = UserMain.user.nextLine();
	            	} catch (ArrayIndexOutOfBoundsException e) {
	            		/* instruct user on error and to retry */
	            		System.out.println(e);
	            		System.out.println("Invalid input. Voltage syntax is V X Y Z. Input a resistor:");
	            		/* clear the Number string or else previous values will still be held within the string */
	            		Number = "";
	            	    input = UserMain.user.nextLine();
	            	}
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
                				/* stop searching */
                				flag = true;
                				break;
                			}
                		}
                	}
                	if (!flag) {
                		/* if it was not found*/
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
            		/* if it was not found*/
                	if (!flag) {
                		System.out.println("Voltage not found.");
                		}
               }
               /* if bad input */
               else System.out.println("Input component not recognized.");
            }
        
            /*If 'display' is input - print out the circuit components.*/
            else if ("display".equals(input)){
            	/* if there are components */
            	if(cir.getComponents().size()>0) {
	            	System.out.println("");
            		System.out.println("Components in circuit are:");
            		System.out.println(cir.toString());
	            	System.out.println("");
            	}
            	/* otherwise - needed to avoid trying to print an empty array */
            	else {
            		System.out.println("No Components have been added yet.");
            	}
            }
            
            /* calculate Total Current/Voltage */
            else if ("calculate".equals(input)) {
            	/* if there are components in the circuit */
            	if(cir.getComponents().size()!=0) {
	            	/* get ground voltage */
                    System.out.println("");
	            	System.out.println("Where is the ground voltage? Enter the unique node ID number only.");
	                input = UserMain.user.nextLine();
	                /* input verification - ground functionality to be added later */
	                int ground;
	                while(true) {
		                try {
		                	ground = Integer.parseInt(input);
		                	break;
		            	} catch (NumberFormatException e) {
		            		System.out.println("Invalid input. Enter only the node ID (an integer value):");
		            	    input = UserMain.user.nextLine();
		            	}
	                }

	            	System.out.println("");
	            	System.out.println("Calculating:");
	            	
	            	/* sort the ArrayList of components by node 1 and node 2 (smaller of both first) - note that by construction, voltages will always have ordered nodes */
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
	            	
	            	/* perform the circuit analysis */
	            	CircuitAnalysis Calculate = new CircuitAnalysis(ground, cir.getComponents(), nodeList);
	            	Calculate.analyzeCircuit();
	            	/* clear the old calculate object */
	            	Calculate = null;
	            	/* instruct user to continue altering circuit */
	            	System.out.println("");
	            	System.out.println("You may continue to operate on the circuit. Enter a new input command.");

            	}
            	/* if no components in the circuit - needed to avoid trying to operate on an empty circuit (empty array) */
            	else {
            		System.out.println("Must have components in circuit before calculating.");
            	}
            }
        
            /* loop back for invalid inputs */
            else{
                System.out.println("Invalid input. Enter a valid command as specified in the instructions.");
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
