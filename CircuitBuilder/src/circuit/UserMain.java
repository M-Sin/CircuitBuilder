package circuit;
import java.util.ArrayList;
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
 * @version 2.410
 * @since 15 February 2019.
 */

/*
 * TODO Investigate better way to validate inputs than exception handling.
 */

public class UserMain {

    /**Main method that interacts with user
     * @param String[] args.
     */
	public static void main(String[] args){
        
        /* Create objects in main */
        Circuit cir = Circuit.getInstance();
        Scanner user = new Scanner(System.in);
        ArrayList<Node> nodeList = new ArrayList<>();
        
        /*Instruct user on  how to use program.*/
        instructUser();
        
        /* Request user input with input verification */
        String input = null;
        while(true) {
			try {
				/* test inputs */
				input = user.nextLine();
				if(input.equals("add") || input.equals("edit") || input.equals("display") || input.equals("calculate") || input.equals("end")) {
					break;
				}
				/* if not a viable input, allow user to retry */
				throw new IllegalArgumentException("Invalid input. Enter a valid command as specified in the instructions.");
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
    			input = user.nextLine();

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
	            	    input = user.nextLine();
	        		}
        		}
        		
	            /* If resistor is being added */
	            if ((input.charAt(0) == 'r'||input.charAt(0) == 'R')&&input.charAt(1)==' '){
	            	addResistor(input, nodeList, user, cir);
	            }
	            
	            /* If voltage source is being added */
	            else if ((input.charAt(0) == 'v'||input.charAt(0) == 'V')&&input.charAt(1)==' '){
	                addVoltage(input,nodeList,user,cir);
	            }
	            /* catch other bad inputs */
	            else {
	            	System.out.println("Invalid input. Enter a voltage source or resistor with the following syntax R/V X Y Z. Try again:");
	            	input = user.nextLine();
	            }
        	 }
	            
            /* option to remove components */
            else if ("edit".equals(input)){
            	editComponent(input, nodeList, user, cir);
            }
        
            /*If 'display' is input - print out the circuit components.*/
            else if ("display".equals(input)){
            	displayCircuit(cir);
            }
            
            /* calculate Total Current/Voltage */
            else if ("calculate".equals(input)) {
            	calculate(input, nodeList, user, cir);
            }
        
            /* loop back for invalid inputs */
            else{
                System.out.println("Invalid input. Enter a valid command as specified in the instructions.");
            }
	        /*Request next instruction.*/
	        System.out.println("Enter next command.");
	        input = user.nextLine();
	    }
         
	    /*Program end.*/     
	    System.out.println("Circuit Builder has ended.");
	    /* close scanner to avoid resource leaks */
	    user.close();
    
    }
	

	
	/* Methods */
	
	/** Print instructions */
	public static void instructUser(){
        System.out.println("Welcome to the circuit builder program.");
        System.out.println("Input 'add' to add components into the circuit.");
        System.out.println("Input 'edit' to remove components from the circuit.");
        System.out.println("Input 'display' to display components currently in the circuit.");
        System.out.println("Input 'calculate' to determine total resistance and current in circuit.");
        System.out.println("Input 'end' to end the program.");
        System.out.println();
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
        System.out.println("V2.410 Notes:");
        System.out.println("Resistors must be connected serially or in parallel. This program does not currently support connections that are neither.");
        System.out.println("Currently the program only supports purely directly serial voltage sources, one of which must be between nodes 0 and 1.");
        System.out.println("Voltages may not be connected in parallel with resistors.");
        System.out.println("Currently it is the user's responsibility to enter a complete circuit.");
        System.out.println("");
        System.out.println("Enter a command.");
	}
	
	/** Add a resistor to the circuit 
	 * @param String input
	 * @param ArrayList<Node> nodeList
	 * @param Scanner user
	 * @param Circuit cir
	 */
	public static void addResistor(String input, ArrayList<Node> nodeList, Scanner user, Circuit cir){
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
                if ( !testLetter.equals("r") && !testLetter.equals("R") ) {
	                /* throw exception to keep user within loop */
                	throw new IllegalArgumentException("You must enter a resistor.");
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
            /* note could just catch all exceptions since the retry message is the same, but that is bad practice - NumberFormatException is caught by IllegalArgumentException so is not needed inside catch condition*/
        	} catch ( IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
				/* instruct user on error and to retry */
        		System.out.println(e);
        		System.out.println("Invalid input. Resistor syntax is R X Y Z. Input a resistor:");
        	    input = user.nextLine();
        	}
    	}
    	
        /* create nodes if they do not already exist*/
    	Node node1 = findOrCreate(firstNode, nodeList);
    	Node node2 = findOrCreate(secondNode, nodeList);

    	/*Create and add resistor to circuit.*/
    	Resistor resistor = new Resistor(rVal, node1, node2);
        cir.addComponent(resistor);
    	/* track node connections */
    	node1.connect(resistor);
    	node2.connect(resistor);
        
        System.out.println("Added Resistor: "+resistor.toString());
        
    }
	
	/** Add a voltage to the circuit
	 * @param String input
	 * @param ArrayList<Node> nodeList
	 * @param Scanner user
	 * @param Circuit cir
	 */
	public static void addVoltage(String input, ArrayList<Node> nodeList, Scanner user, Circuit cir) {
		int firstNode=0;
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
	            if (!testLetter.equals("v") && !testLetter.equals("V")) {
	                /* throw exception to keep user within loop */
	            	throw new IllegalArgumentException("You must enter a voltage.");
	            }
	            /* component must be connected to two different nodes */
	            if(firstNode == secondNode) {
	            	throw new IllegalArgumentException("Components must be connected to two different nodes.");
	            }
	            /* only reached if no exceptions are thrown */
	            break;
	        /* note could just catch all exceptions since the retry message is the same, but that is bad practice - against NumberFormatException is caught by IllegalArgumentException*/
	    	} catch ( IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
	    		/* instruct user on error and to retry */
	    		System.out.println(e);
	    		System.out.println("Invalid input. Voltage syntax is V X Y Z. Input a voltage source:");
	    	    input = user.nextLine();
	    	}
		}
	    
	    /* create nodes if they do not already exist*/
		Node node1 = findOrCreate(firstNode, nodeList);
		Node node2 = findOrCreate(secondNode, nodeList);
	
		/*Create and add resistor to circuit.*/
		Voltage voltage = new Voltage(vVal, node1, node2);
	    cir.addComponent(voltage);
		/* track node connections */
		node1.connect(voltage);
		node2.connect(voltage);
	    
	    System.out.println("Voltage added: "+voltage.toString());
	}
	
	/** Method to perform calculation operation
	 * @param String input
	 * @param ArrayList<Node> nodeList
	 * @param Scanner user
	 * @param Circuit cir
	 */
	public static void calculate(String input, ArrayList<Node> nodeList, Scanner user, Circuit cir) {
		/* if there are components in the circuit */
		if(cir.getComponents().size()!=0) {
	    	/* get ground voltage */
	        System.out.println("");
	    	System.out.println("Where is the ground voltage? Enter the unique node ID number only.");
	        input = user.nextLine();
	        /* input verification - ground functionality to be added later */
	        int ground;
	        while(true) {
	            try {
	            	ground = Integer.parseInt(input);
	            	break;
	        	} catch (NumberFormatException e) {
	        		System.out.println("Invalid input. Enter only the node ID (an integer value):");
	        	    input = user.nextLine();
	        	}
	        }
	
	    	System.out.println("");
	    	System.out.println("Calculating:");
	
	    	
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
	    	System.out.println("You may continue to operate on the circuit.");
	
		}
		/* if no components in the circuit - needed to avoid trying to operate on an empty circuit (empty array) */
		else {
			System.out.println("Must have components in circuit before calculating.");
		}
	}
	
	/** Method to edit/remove components from circuit
	 * @param String input
	 * @param ArrayList<Node> nodeList
	 * @param Scanner user
	 * @param Circuit cir
	 */
	public static void editComponent(String input, ArrayList<Node> nodeList, Scanner user, Circuit cir) {
    	System.out.println("Which component would you like to remove? Enter only the unique identifier with no spaces (Ex. R1 or V2):");
        /* store values */
    	input = user.nextLine();
    	/* store input */
    	char[] question = null;
    	/* initialize letter with a dummy value */
    	char letter = '\0';
    	String number = "";
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
        		letter = question[0];
        		/* find the Id of the requested value */
        		for (int j = 1; j<question.length;j++){
        			number += question[j];
        		}
        		/* if not an integer, this will throw a NumberFormatException */
        		Integer.parseInt(number);
        		/* if a voltage or resistor are not selected */
        		if(letter!='r' && letter!='R' && letter!='v' && letter!='V') {
        			throw new IllegalArgumentException("Select a resistor with 'R' or a voltage with 'V'.");
        		}
        		/* if the number string does not contain at least one character */
        		if(number.length()<1) {
        			throw new IllegalArgumentException("Must enter the unique Id of the component you wish to remove.");
        		}
        		/* if no exceptions are thrown */
        		break;
        	} catch(IllegalArgumentException e) {
        		/* instruct user on error and to retry */
        		System.out.println(e);
        		System.out.println("Invalid input. Enter only the Letter (R or V) and the number of the component you wish to remove. Try again:");
        		/* clear the number string or else previous values will still be held within the string */
        		number = "";
        	    input = user.nextLine();
        	} catch (ArrayIndexOutOfBoundsException e) {
        		/* instruct user on error and to retry */
        		System.out.println(e);
        		System.out.println("Invalid input. Voltage syntax is V X Y Z. Input a resistor:");
        		/* clear the number string or else previous values will still be held within the string */
        		number = "";
        	    input = user.nextLine();
        	}
    	}
        
        /* if resistor requested */
        if (letter == 'r' || letter == 'R') {
        	boolean flag = false;
        	Resistor Check=null;
        	/*check if it is in the list */
        	for (int i = 0; i <cir.getComponents().size();i++){
        		/* if that component is a resistor */
        		if(cir.getComponents().get(i) instanceof Resistor){
        			Check = (Resistor)cir.getComponents().get(i);
        			if (Check.getId() == Integer.parseInt(number)){
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
        else if (letter == 'v' || letter == 'V') {
        	boolean flag = false;
        	Voltage Check=null;
        	/*check if it is in the list */
        	for (int i = 0; i <cir.getComponents().size();i++){
        		/* if that component is a voltage */
        		if(cir.getComponents().get(i) instanceof Voltage){
        			Check = (Voltage)cir.getComponents().get(i);
        			if (Check.getId() == Integer.parseInt(number)){
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
	
	/** Method to display components in circuit
	 * @param Circuit cir
	 */
	public static void displayCircuit(Circuit cir) {
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
	
    /** Method to ensure duplicate nodes are not created and that components are attached to the same node objects 
     * @param int nodeId
     * @param ArrayList<Node> nodeList
     * @return Node*/
    public static Node findOrCreate(int nodeId, ArrayList<Node> nodeList) {
        for(Node node : nodeList) {
            if (node.getId() == nodeId)
                return node;
        }

        Node newNode = new Node(nodeId);
        nodeList.add(newNode);
        return newNode;
    }
}
