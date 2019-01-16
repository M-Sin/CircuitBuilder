Class files are located under: CircuitBuilder/CircuitBuilder/src/circuit/

This program allows the user to input components, currently voltage sources and resistors, which must be connected in series or in parallel.

This program has a few different command inputs that will allow the user to operate it, as explained when the program is run.

Input 'add' to add components to the circuit. The program will prompt you to add a component, and you must use the following syntax:

R X Y Z for a resistor connected from node X to node Y with resistance Z Ohms. X and Y are both integers, and Z is a double. Resistance must be non-zero and non-negative.

V X Y Z for a voltage source connected from node X to node Y with voltage Z Volts. X and Y are both integers, and Z is a double. Voltage is polarized, and so can be negative but cannot be zero. The polarity is directed from smaller node Id to larger.

Input 'edit' to remove components from the circuit. The program will prompt you to remove a component based on its name, for example 'R2'.

Input 'display' to display components currently in the circuit.

Input 'calculate' to determine the total voltage/resistance/current in the circuit. You may continue to add or remove components after a calculation.

Input 'end' to end the program.

The ultimate point of this program is to calculate circuit characteristics based on the input circuit components.