package testPackage;

import static org.junit.Assert.*;
import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import circuit.CircuitAnalysis;
import circuit.Component;
import circuit.Node;
import circuit.Resistor;
import circuit.Voltage;

/**
 * A test package that builds a custom circuit and compares the calculated values with expected values.
 * 
 * Current test circuit contains: parallel resistors between the same two nodes, parallel resistors between different nodes and serial resistors.
 * 
 * Useful for testing this level of proven functionality in the program, as I continue to expand on the program.
 * 
 * @author Michael Sinclair.
 * @version 2.305
 * @since 2 February 2019.
*/

public class CalculationTest {
	
	/* JUnit requires static variables */
	public static ArrayList<Component> testComps;
	public static ArrayList<Node> testNodes;
	public static int testGround;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		/* build a test circuit - creating the CircuitAnalysis object requires the following parameters: int ground, ArrayList<Component> comps, ArrayList<Node> nodes */
		testComps = new ArrayList<>();
		testNodes = new ArrayList<>();
		testGround = 5;
		/* create a circuit with 6 nodes */
		for(int i = 0; i<6;i++) {
			testNodes.add(new Node(i));
		}
		/* voltage 5V between nodes 0 and 1 */
		testComps.add(new Voltage(5.0,testNodes.get(0),testNodes.get(1)));
		/* resistor 10 Ohms between nodes 1 and 2 */
		testComps.add(new Resistor(10.0,testNodes.get(1),testNodes.get(2)));
		/* resistor 20 Ohms between nodes 2 and 3 */
		testComps.add(new Resistor(20.0,testNodes.get(2),testNodes.get(3)));
		/* resistor 30 Ohms between nodes 2 and 3 */
		testComps.add(new Resistor(30.0,testNodes.get(2),testNodes.get(3)));
		/* resistor 40 Ohms between nodes 3 and 4 */
		testComps.add(new Resistor(40.0,testNodes.get(3),testNodes.get(4)));
		/* resistor 50 Ohms between nodes 2 and 4 */
		testComps.add(new Resistor(50.0,testNodes.get(2),testNodes.get(4)));
		/* resistor 60 Ohms between nodes 4 and 5 */
		testComps.add(new Resistor(60.0,testNodes.get(4),testNodes.get(5)));
		/* resistor 70 Ohms between nodes 5 and 0 - note that UserMain sorts resistor nodes from smaller to larger when passing the ArrayList into circuit analysis within the calculate command */
		testComps.add(new Resistor(70.0,testNodes.get(0),testNodes.get(5)));
		/* ensure the lists contain the right amount of nodes */
		assertEquals(8,testComps.size());
		assertEquals(6,testNodes.size());
	}

	@Test
	public void testCircuitAnalysis() {
		/* set the ground to node 2 */
		testGround = 2;
		/* build the circuit analysis object and perform the calculations */
		CircuitAnalysis testCircuit = new CircuitAnalysis(testGround,testComps,testNodes);
		testCircuit.analyzeCircuit();
		/* testing total value calculations - use assertTrue to test doubles within a small threshold - expected total resistance of 165.4901961 Ohms & total voltage of 5.0000 V & total current of 0.03021327 Amps - use Math.abs to test magnitude of values */
		assertTrue(Math.abs(165.4901961-testCircuit.getR())<0.00001);
		assertTrue(Math.abs(5.0-testCircuit.getV())<0.00001);
		assertTrue((Math.abs(0.03021327-(testCircuit.getV()/testCircuit.getR())))<0.00001);
		assertTrue(testGround == testCircuit.getG());
		/* note that running this test will print out the circuit characteristics to the console */
	}
	
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		/* release resources */
		testComps = null;
		testNodes = null;
	}

}
