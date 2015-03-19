/*******************************************************************************
*
* @AUTHOR Andrew Paettie
* DESCRIPTION: program tha takes a space delimited list of  integers as an 
* argument and sorts them using a 3-way split merge sort, and counting the 
* number of comparisons.
*******************************************************************************/


public class ThreewayMergesort{

    private static int numComparisons = 0;

    public static void threewayMergeSort(Integer [] arr){
      Integer [] tmp = new Integer [arr.length];
      for (int i = 0; i < tmp.length; i++)
	tmp[i] = -1;
      threewayMergeSort(arr, tmp, 0, arr.length - 1);
    }
    
    private static void threewayMergeSort(Integer [] arr, Integer [] tmp, int left, int right){
      if (left < right){
	int start1, start2, start3, end1, end2, end3;
	if ((right - left + 1) == 2){ //only 2 elements in sub arrray
	  //System.out.println("Only 2 elements in sub array");
	  start1 = left;
	  end1 = left;
	  start2 = -1;
	  end2 = -1;
	  start3 = right;
	  end3 = right;
	}
	else{
	  int gap = (right - left) / 3;
	  int twiceGap = 2*gap;
	  
	  start1 = left;
	  end1 = left + gap;
	  start2 = end1+1;
	  end2 = left + twiceGap;
	  start3 = end2+1;
	  end3 = right;
	}
	
	threewayMergeSort(arr, tmp, start1, end1);
	threewayMergeSort(arr, tmp, start2, end2);
	threewayMergeSort(arr, tmp, start3, end3);
	
	threewayMerge(arr, tmp, start1, end1, end2, end3);
      }
    }
    /*********************************************************************************************/
    private static void threewayMerge
    (Integer [] arr, Integer [] tmp, int start1, int end1, int end2, int end3){
      int numElements = end3 - start1 + 1;
      int tmpIndex = start1;              //the index for working with tmp array
      int start2, start3;
      if (end2 == -1){                   //setup base case of 2 element array
	start2 = 0;
	start3 = start1+1;
      }
      else{                             //regular case (elements >2)
	start2 = end1+1;
	start3 = end2+1;
      }
      
      //find smallest of elements in 3 subarrays
      while((start1 <= end1) && (start2 <= end2) && (start3 <= end3)){
	if (arr[start1] <= arr[start2]){
	  if (arr[start1] <= arr[start3]){               //[start1] is smallest
	    printSwap(tmpIndex, start1, arr, tmp);
	    tmp[tmpIndex ++] = arr[start1++];      
	    System.out.print("Temp array: ");
	    printArr(tmp);
	    numComparisons +=2;
	  }
	  else{//[start1] < [start2] && [start1] > [start3] -> [start2] > [start3]
	    printSwap(tmpIndex, start3, arr, tmp);
	    tmp[tmpIndex ++] = arr[start3++];           //[start3] is smallest
	    System.out.print("Temp array: ");
	    printArr(tmp);
	    numComparisons +=2;
	  }
	}
	else{                                           //[start1]>[start2]
	  if (arr[start2] <= arr[start3]){
	    printSwap(tmpIndex, start2, arr, tmp);
	    tmp[tmpIndex ++] = arr[start2++];          //[start2] is smallest
	    System.out.print("Temp array: ");
	    printArr(tmp);
	    numComparisons +=2;
	  }
	  else{//[start1] > [start2] && [start2] > [start3] -> [start1] > [start3]
	    printSwap(tmpIndex, start3, arr, tmp);
	    tmp[tmpIndex ++] = arr[start3++];           //[start3] is smallest
	    System.out.print("Temp array: ");
	    printArr(tmp);
	    numComparisons +=2;
	  }
	}
      }
      /** this method uses way more comparisions than above
      while((start1 <= end1) && (start2 <= end2) && (start3 <= end3)){
	if ((arr[start1] <= arr[start2]) && (arr[start1] <= arr[start3])){
	  printSwap(tmpIndex, start1, arr, tmp);
	  tmp[tmpIndex ++] = arr[start1++];      //arr[start1] is smallest
	  System.out.print("Temp array: ");
	  printArr(tmp);
	  numComparisons +=2;
	}
	else if ((arr[start2] <= arr[start1]) && (arr[start2] <= arr[start3])){
	  printSwap(tmpIndex, start2, arr, tmp);
	  tmp[tmpIndex ++] = arr[start2++];      //arr[start2] is smallest
	  System.out.print("Temp array: ");
	  printArr(tmp);
	  numComparisons +=4;
	}
	else if ((arr[start3] <= arr[start1]) && (arr[start3] <= arr[start2])){
	  printSwap(tmpIndex, start3, arr, tmp);
	  tmp[tmpIndex ++] = arr[start3++];      //arr[start3] is smallest
	  System.out.print("Temp array: ");
	  printArr(tmp);
	  numComparisons +=6;
	}
      }*/
      
      //find smallest of elements in 2 subarrays
      while((start1 <= end1) && (start2 <= end2)){
	if (arr[start1] <= arr[start2]){
	  printSwap(tmpIndex, start1, arr, tmp);
	  tmp[tmpIndex++] = arr[start1++];
	  System.out.print("Temp array: ");
	  printArr(tmp);
	}
	else{
	  printSwap(tmpIndex, start2, arr, tmp);
	  tmp[tmpIndex++] = arr[start2++];
	  System.out.print("Temp array: ");
	  printArr(tmp);
	}
	numComparisons ++;
      }
      
      while((start1 <= end1) && (start3 <= end3)){
	if (arr[start1] <= arr[start3]){
	  printSwap(tmpIndex, start1, arr, tmp);
	  tmp[tmpIndex++] = arr[start1++];
	  System.out.print("Temp array: ");
	  printArr(tmp);
	}
	else{
	  printSwap(tmpIndex, start3, arr, tmp);
	  tmp[tmpIndex ++] = arr[start3++];      //arr[start3] is smallest
	  System.out.print("Temp array: ");
	  printArr(tmp);
	}
	numComparisons ++;
      }
      
      while((start2 <= end2) && (start3 <= end3)){
	if (arr[start3] <= arr[start2]){
	  printSwap(tmpIndex, start3, arr, tmp);
	  tmp[tmpIndex ++] = arr[start3++];      //arr[start3] is smallest
	  System.out.print("Temp array: ");
	  printArr(tmp);
	}
	else{
	  printSwap(tmpIndex, start2, arr, tmp);
	  tmp[tmpIndex++] = arr[start2++];
	  System.out.print("Temp array: ");
	  printArr(tmp);
	}
	numComparisons ++;
      }
      
      //empty out non-empty subarrays
      if (start1 <= end1)
	System.out.println("Emptying first subarray:");
      while(start1 <= end1){
	tmp[tmpIndex++] = arr[start1++];
	System.out.print("Temp array: ");
	printArr(tmp);
      }
      
      if (start2 <= end2)
	System.out.println("Emptying second subarray:");
      while(start2 <= end2){
	tmp[tmpIndex++] = arr[start2++];
	System.out.print("Temp array: ");
	printArr(tmp);
      }
      
      if (start3 <= end3)
	System.out.println("Emptying third subarray:");
      while(start3 <= end3){
	tmp[tmpIndex++] = arr[start3++];
	System.out.print("Temp array: ");
	printArr(tmp);
      }
    
      //copy back
      System.out.println("Copying back:");
      for (int i = 0; i < numElements; i++, end3--)
	arr[end3] = tmp[end3];
	System.out.print("Array: ");
	printArr(arr);
    }
    /*********************************************************************************************/
    public static void main(String[] args){
      if (args.length == 0){
	printHelp();
	System.exit(0);
      }
        
      Integer [] testArr = new Integer [args.length];
      //populate array with args
      for (int i = 0; i < args.length; i++)
          testArr [i] = Integer.parseInt(args [i]);
      
      System.out.print("Unsorted Array: ");
      printArr(testArr);
      System.out.println("====================================================");
      threewayMergeSort(testArr);
      System.out.println("====================================================");
      System.out.print("Sorted Array: ");
      printArr(testArr);
      System.out.println("Number of comparisons: " + numComparisons);
    }
    
    /**
     * i1 = index of tmp array
     * i2 = index of array
     * arr = array pointer
     * tmp = tmp array pointer
     */
    private static void printSwap(int i1, int i2, Integer [] arr, Integer [] tmp){
      System.out.println("Placing arr["+i2+"] ("+ arr[i2]+") into tmp["+i1+"] ("+tmp[i1]+")");
    }
    
    private static <ArrType> void printArr(ArrType [] arr){
      for (int i = 0; i < arr.length; i ++)
	System.out.print(arr[i].toString()+" ");
      System.out.print("\n");
    }
    
    private static void printHelp(){
        System.out.println("Program which takes a space delimited list of \n" +
                           "integers as an argument and sorts them using a\n"+
                           "3-way split merge sort, counting the number of \n" +
                           "comparisons.\n" );
    }
}