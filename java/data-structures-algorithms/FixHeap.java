/*******************************************************************************
*
* @AUTHOR Andrew Paettie
* DESCRIPTION: Program which takes an integer binary heap as an argument and 
*  fixes it using 3 different methods, computing the number of swaps of each
*  method
*******************************************************************************/

public final class FixHeap{
    
    public static void main(String[] args){
        if (args.length == 0){
            printHelp();
            System.exit(0);
        }
        int [] treeArr = new int [args.length + 1];
        treeArr [0] = args.length;//0 index stores size of tree
        //populate tree array with args
        for (int i = 0; i < args.length; i++){
            treeArr [i+1] = Integer.parseInt(args [i]);
        }
        System.out.print("Original heap: ");
        printTreeBFS(treeArr);
        System.out.println("-------------------------------------------------");
        System.out.println("Results of Method 1:");
        method1(treeArr);
        System.out.println("-------------------------------------------------");
        System.out.println("Results of Method 2:");
        method2(treeArr);
         System.out.println("-------------------------------------------------");
        System.out.println("Results of Method 3:");
        method3(treeArr);
        
    }
    
    //fix heap by looking at parent of all nodes    
    private static void method1(int [] origArr){
        int [] newArr = new int[origArr.length];
        System.arraycopy(origArr, 0, newArr, 0, origArr.length); 
        boolean didSwap = false;
        int numSwaps = 0;
        do{
            didSwap = false;//reset for next iteration
            for (int i = origArr[0]; i > 1; i--){
                if (newArr[i] < newArr[getParentIndex(i)]){
                    System.out.println("Swapping " + newArr[i] +
                                       " and " + newArr[getParentIndex(i)]);
                    int tmp = newArr[i];
                    newArr[i] = newArr[getParentIndex(i)];
                    newArr[getParentIndex(i)]= tmp;
                    didSwap = true;
                    numSwaps++;
                    System.out.print("Result: ");
                    printTreeBFS(newArr);
                }    
            }
        }while (didSwap);//need to keep swapping until there is no more swapping
        System.out.print("Fixed heap: ");
        printTreeBFS(newArr);
        System.out.println("Number of swaps: " + numSwaps);
    }
    
    //fix by inserting into a new heap
    private static void method2(int [] origArr){
        int [] newArr = new int [origArr.length];
        int numSwaps = 0;
        int currElements = 0;//the current number of elements in the array
        for (int i = 1; i < origArr.length; i++){
            //insert element into last available spot
            newArr[++currElements] = origArr[i];
            //to bubble up need to keep track of current index and parent index
            int parentIndex = getParentIndex(currElements);
            int currIndex = currElements;
            boolean didSwap = false;
            do{
                didSwap = false;//reset for next iteration
                if (parentIndex == -1){
                    //parent doesn't exist, element is root
                    didSwap = false;
                }
                else if (newArr[parentIndex] > newArr[currIndex]){
                    //swap parent and child(bubble up)
                    int tmp = newArr[currIndex];
                    newArr[currIndex] = newArr[parentIndex];
                    newArr[parentIndex] = tmp;
                    
                    currIndex = parentIndex;
                    parentIndex = getParentIndex(currIndex);
                    didSwap = true;
                    numSwaps++;
                }
            }while (didSwap); //keep bubbing up until there is no more swappage   
        }
        System.out.print("Fixed heap: ");
        printTreeBFS(newArr);
        System.out.println("Number of swaps: " + numSwaps);
    }
    
    //fix it by bottom up merge (linear time)
    //NOTE: since this is the last method called, just modify the original array
    private static void method3(int [] origArr){
        int numSwaps = 0;
        for (int i = origArr[0] - 1; i > 0; i--){
            int leftIndex = getLeftIndex(i, origArr[0]);
            int rightIndex = getRightIndex(i, origArr[0]);
            int currIndex = i;
            boolean didSwap = false;
            do{
                didSwap = false;
                if (rightIndex == -1){
                    //no right child
                    if (leftIndex==-1){
                    //no left child either, so do nothing
                    }
                    else if (origArr[currIndex] > origArr[leftIndex]){
                        int tmp = origArr[currIndex];
                        origArr[currIndex] = origArr[leftIndex];
                        origArr[leftIndex] = tmp;
                        numSwaps++;
                        didSwap = true;
                        //need to keep checking down to the leaves
                        currIndex = leftIndex;
                        leftIndex = getLeftIndex(currIndex, origArr[0]);
                        rightIndex = getRightIndex(currIndex, origArr[0]);
                    }
                }
                else {//it has 2 children
                    if (origArr[currIndex] > origArr[leftIndex]){
                        if (origArr[leftIndex] < origArr[rightIndex]){
                            //swap left and i
                            int tmp = origArr[currIndex];
                            origArr[currIndex] = origArr[leftIndex];
                            origArr[leftIndex] = tmp;
                            numSwaps++;
                            didSwap = true;
                            currIndex = leftIndex;
                            leftIndex = getLeftIndex(currIndex, origArr[0]);
                            rightIndex = getRightIndex(currIndex, origArr[0]);
                        }
                        else{
                            //swap right and i
                            int tmp = origArr[currIndex];
                            origArr[currIndex] = origArr[rightIndex];
                            origArr[rightIndex] = tmp;
                            numSwaps++;
                            didSwap = true;
                            currIndex = rightIndex;
                            leftIndex = getLeftIndex(currIndex, origArr[0]);
                            rightIndex = getRightIndex(currIndex, origArr[0]);
                        }
                    }
                    else if (origArr[currIndex] > origArr[rightIndex]){
                        //swap right and i
                        int tmp = origArr[currIndex];
                        origArr[currIndex] = origArr[rightIndex];
                        origArr[rightIndex] = tmp;
                        numSwaps++;  
                        didSwap = true;
                        currIndex = rightIndex;
                        leftIndex = getLeftIndex(currIndex, origArr[0]);
                        rightIndex = getRightIndex(currIndex, origArr[0]);  
                    }    
                }
            }while(didSwap);//need to check all the way to the leaves when a swap
                            // is made
        }
        System.out.print("Fixed heap: ");
        printTreeBFS(origArr);
        System.out.println("Number of swaps: " + numSwaps);
    }
    
    private static int getLeftIndex(int index, int size){
        if (2*index > size)
            return -1;//error code
        else
            return (2*index);
    }
    private static int getRightIndex(int index, int size){
        if (2*index + 1 > size)
            return -1;
        else
            return (2*index + 1);
    }
    private static int getParentIndex(int index){
        if (index == 1)
            return -1;//root has no parent
        else    
            return (index/2);
    }
    
    private static void printHelp(){
        System.out.println("Program which takes an integer binary heap as an \n"+
                           "argument and fixes it using 3 different methods,\n"+
                           "computing the number of swaps of each method\n");
    }
    //print tree using breadth first technique
    private static void printTreeBFS(int [] treeArr){
        //System.out.println("Number of Nodes: " + treeArr[0]);
        for (int i = 1; i < treeArr.length; i++){
            System.out.print(treeArr[i] + " ");
        }
        System.out.print("\n");
    }
}
