import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;

public class TestScript {
    private static final String DEFAULT_REG = "reg [NUM_OF_BITS:0]INPUT";
    private static final String DEFAULT_WIRE = "wire [NUM_OF_BITS:0]OUTPUT";
    private static final String DEFAULT_MODULE = "module MODULE();"; 
    private static final String DEFAULT_BEGIN = "initial begin";
    private static final String DEFAULT_END = "end";
    private static final String DEFAULT_TIME = "50";
    private static StringBuilder builder = new StringBuilder();

    public static void main(String[] args) {
        String moduleName = args[0];
        int numOfInputs = Integer.parseInt(args[1]);
        int numOfOutputs = Integer.parseInt(args[2]);
        String[] input = new String[numOfInputs];
        String[] output = new String[numOfOutputs];
        int[] inputBit = new int[numOfInputs];
        int[] outputBit = new int[numOfOutputs];


        int j = 3;
        for(int i = 0; i < numOfInputs; i++)
            input[i] = args[j++];
            
        for(int i = 0; i < numOfOutputs; i++)
            output[i] = args[j++];

        for(int i = 0; i < numOfInputs; i++)
            inputBit[i] = Integer.parseInt(args[j++]);

        for(int i = 0; i < numOfOutputs; i++)
            outputBit[i] = Integer.parseInt(args[j++]);

        String testBench = createTestBench(moduleName, input, output, inputBit, outputBit);

        try {
            PrintWriter outputStream = new PrintWriter(new File("tb_ " + moduleName + ".v"));
            outputStream.println(testBench);
            outputStream.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }

    private static String createTestBench(String moduleName, String[] input, String[] output, int[] inputBit, int[] outputBit) {
        createModule(moduleName);
        createReg(inputBit, input);
        createWire(outputBit, output);
        createInstance(moduleName, input, output);

        builder.append("\n\n\t" + DEFAULT_BEGIN + "\n\n");
        createTest("", input, inputBit, 0);
        builder.append("\t" + DEFAULT_END + "\n\nendmodule");

        return builder.toString();
    }

    private static void createModule(String moduleName) {
        String temp = new String(DEFAULT_MODULE);
        temp = temp.replaceAll("MODULE", moduleName);
        builder.append(temp + "\n\n");
    }

    private static void createReg(int[] inputBit, String[] input) {
        String temp;

        for(int i = 0; i < input.length; i++) {
            temp = new String(DEFAULT_REG);
            temp = temp.replaceAll("NUM_OF_BITS", Integer.toString(inputBit[i]));
            temp = temp.replaceAll("INPUT", input[i]);
            builder.append("\t" + temp + ";\n");
        }
    }

    private static void createWire(int[] outputBit, String[] output) {
        String temp;

        for(int i = 0; i < output.length; i++) {
            temp = new String(DEFAULT_WIRE);
            temp = temp.replaceAll("NUM_OF_BITS", Integer.toString(outputBit[i]));
            temp = temp.replaceAll("OUTPUT", output[i]);
            builder.append("\t" + temp + ";\n");
        }
    
        builder.append("\n");
    }

    private static void createInstance(String moduleName, String[] input, String[] output) {
        builder.append("\t" + moduleName + " uut (");
        
        for(int i = 0; i < input.length; i++) {
            builder.append(input[i] + ", ");
        }

        for(int i = 0; i < output.length - 1; i++) {
            builder.append(output[i] + ", ");
        }
        builder.append(output[output.length - 1] + ");");
        builder.append("\n");
    }

    //end yazmadan once fazladan newline basiyor???
    private static void createTest(String currentString, String[] input, int[] inputBit, int current) {
        if(current == input.length)
            return;

        int decimalBase = computeDecimal(inputBit[current]);
        for(int i = 0; i < decimalBase; i++) {
            String temp = currentString;
            temp += input[current] + " = " + i + "; ";
            createTest(temp, input, inputBit, current + 1);

            if(current == input.length - 1) {
                builder.append("\t\t" + temp + "#" + DEFAULT_TIME + ";");
                //builder.append("\n");
            }
            if(current != 0)
                builder.append("\n");
        }
    }

    private static int computeDecimal(int input) {
        int temp = 0;

        for(int i = 0; i < input; i++) {
            temp += (int)Math.pow(2, i);
        }
    
        return temp + 1;
    }
}