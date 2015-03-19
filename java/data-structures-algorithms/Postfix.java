/*******************************************************************************
*
* @AUTHOR Andrew Paettie
* DESCRIPTION: program that converts an infix expression given as an argument 
* to the program into a postfix expression and then evaluates the postfix 
* expression
*******************************************************************************/
import java.lang.reflect.Array;//needed to make array with unknown class at 
                               //compile time (<generic> array)

public final class Postfix{

    public static void main(String[] args){
        if (args.length == 0){
            printHelp();
            System.exit(0);
        }
        
        String inString = args[0];//alias first arg as input string
        
        if (inString.equals("-h")){
            System.out.println(inString);
            printHelp();
            System.exit(0);
        }
        
        if (inString.length() > 100){
            System.out.println("ERROR: input is limited to 100 characters");
            System.exit(1);//exit with error
        }
        
        processInput(inString);
        
    }
    
    private static void processInput(String inString){
        Stack <Double> numStack = new Stack<Double>(Double[].class, 100);
                                                      //create stack for numbers
        Stack <Character>opStack = new Stack<Character>(Character[].class, 100); 
                                                   //create stack for operations
        //wasting space here allocating 100 to each, but lets just be safe, plus
        //50 left parentheses and 50 right parentheses should still be valid!
        String outputString = "";
        char currChar ;
        
        /*******************************************************************
         *                        begin conversion 
         *                      from infix to potfix
         *****************************************************************/
        
        System.out.println("Conversion");
        
        for(int i = 0; i < inString.length(); i++){//process 1 char at a time
            currChar = inString.charAt(i);
            switch (currChar){
                case '0': case '1': case '2': case '3': case '4':
                case '5': case '6': case '7': case '8': case '9':
                    outputString = outputString + currChar;
                    //System.out.println("outString: "+ outputString);
                    
                    break;
                case '+':
                case '-':
                    //while (opStack.peek() == + || - || * || /){
                    //pop the top of the stack and out it in the output}
                    //push currentChar to stack
                    ///*
                    if (!opStack.isEmpty()) {//don't want to throw exception 
                                            //from an empty stack
                        while (opStack.peek() == '+' ||
                               opStack.peek() == '-' ||
                               opStack.peek() == '*' || 
                               opStack.peek() == '/'){
                            outputString = outputString + opStack.pop();
                            
                            
                            if (opStack.isEmpty()){
                                break;//get out of the loop if stack is empty
                            }
                        }
                    }//*/
                    
                    opStack.push(currChar);
                    break;
                case '*':
                case '/':
                    //while (opStack.peek() == * || / ){
                    //pop the top of the stack and put it in the output}
                    //push currentChar onto stack
                    if (!opStack.isEmpty()){//don't want to throw exception 
                                            //from an empty stack
                        while (opStack.peek() == '*' || 
                               opStack.peek() == '/'){
                            outputString = outputString + opStack.pop();
                            if (opStack.isEmpty()){
                                break;//get out of the loop if stack is empty 
                            }
                          
                        }
                    }
                    opStack.push(currChar);
                    break;
                case '(':
                    opStack.push('(');
                    break;
                case ')':
                    if (!opStack.isEmpty()){
                        while(opStack.peek()!= '('){
                            if (opStack.peek() == null){//reached end of stack 
                                System.out.println("ERROR: unmatched parentheses");
                                System.exit(1);
                            }
                            else{
                                System.out.println("character");
                                outputString = outputString + opStack.pop();
                            }
                        }
                        //pop the '('
                        if (opStack.peek() == '('){
                            opStack.pop();
                        }
                    }
                    break;
                default://some illegal character is in the input string
                    System.out.println("ERROR: Illegal character encountered");
                    System.exit(1);
                    break;
            }
            //output currentCharacter, the stack contents, and the ouput
            System.out.println("character: " + currChar);
            System.out.print("stack: " );
            opStack.printStack();
            System.out.println("output: " + outputString);
            System.out.println();
        }
        
        while (!opStack.isEmpty()){
            char currOP = opStack.pop();
            if (currOP == '+'){
                outputString = outputString + '+';
            }
            else if (currOP == '-'){
                outputString = outputString + '-';
            }
            else if (currOP == '*'){
                outputString = outputString + '*';
            }
            else if (currOP == '/'){
                outputString = outputString + '/';
            }
            System.out.println ("stack: ");
            opStack.printStack();
            System.out.println("output: " + outputString);
            System.out.println();
        }
        /***************************************************************
         *                    solve postfix expression
         * ***********************************************************/
        System.out.println("Evaluation");
        
        for (int i = 0; i < outputString.length(); i ++){//process one char at a time
            currChar = outputString.charAt(i);
            switch (currChar){
                //              process number
                //push it onto stack
                case '0': case '1': case '2': case '3': case '4':
                case '5': case '6': case '7': case '8': case '9':
                    numStack.push(Double.parseDouble("" + currChar));
                    break;
                    //          process operator
                case '+': case '-': case '*': case '/':
                    if (numStack.size() < 2){
                        System.out.println("ERROR: insufficient values for operation: "
                                           + currChar);
                        System.exit(1);//exit with error
                    }
                    else{
                        double pop1 = numStack.pop();
                        double pop2 = numStack.pop();
                        if (currChar == '+'){
                            numStack.push((pop2 + pop1));
                        }
                        else if (currChar == '-'){
                            numStack.push((pop2 - pop1));
                        }
                        else if (currChar == '*'){
                            numStack.push((pop2 * pop1));
                        }
                        else if (currChar == '/'){
                            numStack.push((pop2 / pop1));
                        }
                        
                    }    
                    break;
            }
            //output currentCharacter, the stack contents, and the 
            System.out.println("character: " + currChar);
            System.out.print("stack: ");
            numStack.printStack();
            System.out.println();
        
        }
        
        if (numStack.size() == 1){
            System.out.println("output: ");
            numStack.printStack();
        }
        else{
            System.out.println("ERROR: User input too many values");
        }
        
    }
    
    private static void printHelp(){
        System.out.println("Postfix.java: a program to convert infix expressions\n"+
                           "to postfix and evaluate them.  The input is a string\n"+
                           "provided as the argument to this program. The input\n"+  
                           "should be an expression written without spaces so that:\n");
        System.out.println("a) the operands are single digit numbers");
        System.out.println("b) the operations are +,-,*,/,(, and )");
    }
    
    /**************************************************************************
     *based on stack implementation recommended by professor, but generic [] 
     * instead of int []. the original was at:
     *http://www.algolist.net/Data_structures/Stack/Array-based_implementation
     **************************************************************************/
    
    private static class Stack<E> {
      private int top = -1;
      private E[] storage;
 
      Stack(Class<E[]> elementClass, int capacity) {
        if (capacity <= 0)
            throw new IllegalArgumentException("Stack's capacity must be positive");
            storage = elementClass.cast(
                      Array.newInstance(elementClass.getComponentType(), capacity));
            //top = -1;
      }
      
      void push(E element) {
            if (top == storage.length){
                System.out.println("ERROR: Storage limit reached");
            }
            else{
                top++;
                storage[top] = element;
            }
      }
 
      E peek() {
            if (top == -1){
                System.out.println("ERROR: Stack cannot be peeked when it is empty");
                return null;
            }
            return storage[top];
      }
 
      E pop() {
            if (top == -1){
                System.out.println("ERROR: Stack cannot be popped when it is empty");
                return null;
            }
            top--;
            return storage[top + 1];
      }
 
      boolean isEmpty() {
        return (top == -1);
      }
      
      public int size(){
          return (top+1);
      }
      
      public void printStack(){
         if (top == -1){
            System.out.print("Stack is empty\n");
         }
         else{
             for (int i = 0; i <= top; i++){
                System.out.print(storage[i].toString() + " ");
             }
             System.out.print("\n");
         }
      }
    }
}
