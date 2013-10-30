/*************************************************************************************************
 *  @AUTHOR Andrew Paettie
 *  DESCRIPTION: Program which displays the amorization of a loan.  It is flexible 										                                        
 *  about what kind of information it uses to determine this, allowing different 
 *  types of compounding interest, and choosing between length of loan and monthly 
 *  payment, etc.  
 *  I know that it is a bit messy, and it is a single huge file, but this was one of my first 
 *  major programs.  I wanted to mess around with java and swing a bit, so I added
 *  graphs to show the breakdown between interest paid and monthly payment of a loan. 
 *************************************************************************************************/

//format decimals
import java.text.DecimalFormat;
//for gui//
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
//for pie chart and graph
import java.awt.geom.*;


public class loanamorizationtable
{

/**
  define various elements of the gui 
 */
static JButton runAgainButton, quitButton, showGraphsButton, lineGraphButton, pieChartButton; 
static JTable amortizationTable; 
static JFrame mainFrame, graphFrame;
static JFrame pieChartFrame; 
static Container pane, graphPane, chartPane;  
static DefaultTableModel tableModel; 
static JScrollPane scrollPane;
static JPanel panel, graphPanel, chartPanel; 
static JLabel APRlabel, initialPrincipleLabel, interestPaidLabel, principlePaidLabel, totalPaidLabel ;
static JLabel graphTitleLabel, graphYAxisLabel, graphXAxisLabel ;
static JLabel dynamicLegendMonth, dynamicLegendPrinciple, dynamicLegendInterest;
static Component legendSelectorLine;
static JLabel APRLabel;
static JLabel InitialPrincipleLabel;

/**
  define e, the base of the natural log 
  for use in continuous interest        
 */
static final double E = 2.718281828459045;

/**
  define various public variables       
  (need to be public in order for the 
   other classes to be able to see them)
 */
public int numberOfRows;
public double monthlyPayment;
public double totalInterestPaid;
public double totalAmountPaid;
public double principlePercentage;
public double interestPercentage;
public double [][] tableData;
public double boxWidth;
public double APR;
public double initialLoanPrinciple;
public int years;

public static void main(String [] args)
/*********************************
 * main method...doesn't do much *
 *********************************/    
    {	
	//need to create a new instance, as many methods for the gui require a non-static context. made things easier in the end than mixing static and non-static methods, admittedly with some performace hit, albeit small, however at this stage in my proramming career, I don't feel that I need to worry so much about memory management and performance.
    	new loanamorizationtable();
    }//end of main

public loanamorizationtable()
    {
/****************************************************************************************
 * guides the program until it is run once...then the run botton action method runs it  *
 ****************************************************************************************/

	//set an array to return multiple values from 1 method...
	double [] input = input();
		//input = {initialLoanPrinciple,APR,interestType, lengthOfLoan};
		initialLoanPrinciple = input [0] ;
		APR = input [1]  ;
		monthlyPayment = input [2]  ;
		double interestType = input [3] ;
		double lengthOfLoan = input [4] ;

	//call howManyRows to determine correct # of rows to setup tableData array correctly
	numberOfRows = howManyRows(initialLoanPrinciple, APR, interestType, lengthOfLoan);
	while (numberOfRows < 0)//howManyRows returns a negative # when an infinite loop was broken up, so better data must be entered
		{
		//set an array to return multiple values from 1 method...
		input = input();
	      //input = {initialLoanPrinciple,APR,monthlyPayment,interestType, lengthOfLoan};
			initialLoanPrinciple = input [0] ;
			APR = input [1]  ;
			monthlyPayment = input [2]  ;
			interestType = input [3] ;
			lengthOfLoan = input [4];
	
		//call howManyRows to determine correct # of rows to setup array correctly
		numberOfRows = howManyRows(initialLoanPrinciple, APR, interestType, lengthOfLoan);
		}//loop will only terminate when data is entered such that there is no infinite loop...

	//setup new array to hold the values for the table.  setting up an array makes it easier to read little bits and pieces of the data as needed later for the graphs.
	tableData = new double [numberOfRows][6];
	tableData = doTheMath(initialLoanPrinciple, APR, interestType, lengthOfLoan);
	
	// setting up the number of years passed is necessary for the table to display the correct amount of rows, as I use a year label at a yearly interval.  See the tableFrame class for more info...
	
	// months passed is the same as numberOfRows;
	double yearMarker = numberOfRows % 12;
	years = numberOfRows / 12 ;
		if (yearMarker != 0)//a year and some months has passed, so need to add in 1 more year
				{
				years++;
				}

	//show the table window
        mainFrame = new tableFrame();
	mainFrame.setVisible(true);

	//arrays start at 0. not 1, so subtract 1 from the total # of rows
	double totalInterestPaid = tableData[numberOfRows - 1][5] ;
	
	//draw the pie chart
	pieChart(initialLoanPrinciple, totalInterestPaid);

	//draw the graph
	graph();
	
    }

public  double [] input ()
/************************************
* method to prompt user for input   *
************************************/
   {
	//define all of the variables as negative, so they do the loop atleast once, getting input from user
	double initialLoanPrinciple = -1;
	double APR = -1;
	monthlyPayment = -1;
	double interestType = -1;
	double lengthOfLoan = -1 ;

	/*================================================================================*/		
	/**
	 Initial Principle
	*/
	/*Set up loop to account for - principle.  
	same structure for all other similar loops */
	do 
	{
	String initialLoanPrincipleString = JOptionPane.showInputDialog(null, "What is the Initial Loan Principle?");
	while (initialLoanPrincipleString == null)//while the cancel/close button are clicked
		{
		int reallyCancel = JOptionPane.showConfirmDialog (null, "Do you really want to quit?", "Quit?" , JOptionPane.YES_NO_OPTION);//see if they actually want to quit
			if (reallyCancel == 0) //clicked yes to quit
				{
				System.exit(0); 
				}
			else //clicked no, canel, or the close, so they dont want to quit
				{
				initialLoanPrincipleString = JOptionPane.showInputDialog("What is the Initial Loan Principle?");
				}
		}
	initialLoanPrinciple = bugProofInput(initialLoanPrincipleString);//call the bugproffing method to avoid invalid input
	if (initialLoanPrinciple < 0)//then it is either negative, or contains no numbers
		{
		JOptionPane.showMessageDialog(null, "Initial Loan Principle must be a positive number.");
		}
	}
	while (initialLoanPrinciple <= 0);//loop, de-loop, de-loop...
	
	/*================================================================================*/
	/**
	 Monthly Interest Rate or APR
         */
	//people either know the monthly payment or the APR, so ask which they know...
	String[] optionsInterest = {"Monthly Interest Rate", "APR"};//button labels

	int clickedButtonInterest = JOptionPane.showOptionDialog (null, "Which do you know, the monthly interest rate or the APR?", "Monthly Interest Rate or APR", 0,JOptionPane.QUESTION_MESSAGE,null, optionsInterest, optionsInterest[1]);

	if (clickedButtonInterest == -1)//the close box was clicked...
		{
		System.exit(0);
		}
	else if (clickedButtonInterest == 0)
	   {
	   /**
	   Monthly Interest Rate
	   */

	   do 
	    {	
	    String monthlyInterestRateString = JOptionPane.showInputDialog("What is the monthly interest rate? (%)");
	    while (monthlyInterestRateString == null)
		{
		int reallyCancel = JOptionPane.showConfirmDialog (null, "Do you really want to quit?", "Quit?" , JOptionPane.YES_NO_OPTION);//see if they actually want to quit
		if (reallyCancel == 0) //clicked yes to quit
		   {
		   System.exit(0); 
		   }
		else //clicked no, canel, or the close
		   {
		   monthlyInterestRateString = JOptionPane.showInputDialog("What is the monthly interest rate? (%)"); 
		   }
		}
	    double monthlyInterestRate = bugProofInput(monthlyInterestRateString);
	    if (monthlyInterestRate < 0)
		{
		JOptionPane.showMessageDialog(null, "Monthly interest rate must be a positive number.");
		}
	    APR = monthlyInterestRate * 12 ;
	    }
	   while (APR <= 0);

	   }
	else if (clickedButtonInterest == 1)
	   {
	   /**
	   APR  
	   */

	   do 
	    {
	    String APRString = JOptionPane.showInputDialog("What is the Annual Percentage Rate? (%)");
	    while (APRString == null)
		{
		int reallyCancel = JOptionPane.showConfirmDialog (null, "Do you really want to quit?", "Quit?" , JOptionPane.YES_NO_OPTION);//see if they actually want to quit
		if (reallyCancel == 0) //clicked yes to quit
		    {
		    System.exit(0); 
		    }
		else //clicked no, canel, or the close
		   {
		   APRString = JOptionPane.showInputDialog("What is the Annual Percentage Rate? (%)"); 
		   }
		}
	    APR = bugProofInput(APRString);
		if (APR < 0)
		   {
		   JOptionPane.showMessageDialog(null, "APR must be a positive number.");
		   }
	    }
	    while (APR < 0);
	   }

	/*================================================================================*/	
	/**
	 Compound or Simple Interest?           
	                                        
	   interest type values are as follows: 
		0 is simple                     
		1 is continuous compounding     
		2 is periodic compounding       
	*/

	String[] opts = {"Simple", "Compound"};//button labels
	interestType = JOptionPane.showOptionDialog (null, "Is this simple or compound interest?", "Interest Type", 0,JOptionPane.QUESTION_MESSAGE,null,  opts, opts[1]);
	if (interestType == 1)// it is compound interest, so we need to determine the type of compound interest...
	   {
	   Object[] newopts = {"Continuous", "Periodic"};//button labels
	   interestType = JOptionPane.showOptionDialog (null, "Is this continually compounding interest\nor periodic compounding interest?", "Compound Interest Type", 0,JOptionPane.QUESTION_MESSAGE,null,  newopts, newopts[1]);
		/**
		the value returned is the offset of the button  but we need to add one to the value, 
		because the interest types can only be 1 or 2 in this circumstance, 
		not the 0 or 1 which the showOptionDialog returns
		*/
		interestType ++;			
	   }
	else if (interestType == -1)//the close box was clicked...
	   {
	   System.exit(0);
	   }

	/*================================================================================*/
	/**
	 Monthly Payment or Length of Loan 
         */
	//people either know the monthly payment or the length of the loan, so ask which they know...
	String[] options = {"Monthly Payment", "Length of The Loan"};//button labels
	int clickedButton = JOptionPane.showOptionDialog (null, "Which do you know, the monthly payment or the length of the loan?", "Monthly Payment or Length of Loan", 0,JOptionPane.QUESTION_MESSAGE,null, options, options[0]);
	if (clickedButton == -1)//the close box was clicked...
	   {
	   System.exit(0);
	   }
	else if (clickedButton == 0)
	   {
	   /**
	   Monthly Payment 
	   */
	   do
	    {	
	    String monthlyPaymentString = JOptionPane.showInputDialog("What is the Monthly Payment?");
	    while (monthlyPaymentString == null)
		{
		int reallyCancel = JOptionPane.showConfirmDialog (null, "Do you really want to quit?", "Quit?" , JOptionPane.YES_NO_OPTION);//see if they actually want to quit
		if (reallyCancel == 0) //clicked yes to quit
		   {
		   System.exit(0); 
		   }
		else //clicked no, canel, or the close
		   {
		   monthlyPaymentString = JOptionPane.showInputDialog("What is the Monthly Payment?"); 
		   }
		}
	    monthlyPayment = bugProofInput(monthlyPaymentString);
	    if (monthlyPayment < 0)
		{
		JOptionPane.showMessageDialog(null, "Monthly Payment must be a positive number.");
		}
	    }
	   while (monthlyPayment <= 0);
	   }

	else if (clickedButton == 1)
	   {
	   /**
	    Length of Loan 
	   */

	   do 
	    {	
	    String lengthOfLoanString = JOptionPane.showInputDialog("What is the length of the loan?");
	    while (lengthOfLoanString == null)
		{
		int reallyCancel = JOptionPane.showConfirmDialog (null, "Do you really want to quit?", "Quit?" , JOptionPane.YES_NO_OPTION);//see if they actually want to quit
		if (reallyCancel == 0) //clicked yes to quit
		   {
		   System.exit(0); 
		   }
		else //clicked no, canel, or the close
		   {
		   lengthOfLoanString = JOptionPane.showInputDialog("What is the length of the loan?"); 
		   }
		}
	    lengthOfLoan = bugProofInput(lengthOfLoanString);
	    if (lengthOfLoan < 0)
		{
		JOptionPane.showMessageDialog(null, "The length of the loan must be a positive number.");
		}
	    }
	   while (lengthOfLoan <= 0);

	   /**
	   was input months or years? 
	   **/
	   Object[] optionsMonthsOrYears = {"Months", "Years"};//button labels
	   int monthsOrYearsButton = JOptionPane.showOptionDialog (null, "You entered \"" +lengthOfLoan+ "\"\nIs this in months or years?", "Months or Years", 0,JOptionPane.QUESTION_MESSAGE,null, optionsMonthsOrYears, optionsMonthsOrYears[0]);
	   if (monthsOrYearsButton == 1)
		{
		lengthOfLoan = lengthOfLoan * 12; //lengthOfLoan is in months throughout the program, so set it as so...
		}
	   else if (clickedButton == -1)//the close box was clicked...
		{
		int reallyCancel = JOptionPane.showConfirmDialog (null, "Do you really want to quit?");
		if (reallyCancel == 0) //clicked yes to quit
			{
			System.exit(0); 
			}
		else //clicked no, canel, or the close
			{
			monthsOrYearsButton = JOptionPane.showOptionDialog (null, "You entered \"" +lengthOfLoan+ "\"\nIs this in months or years?", "Months or Years", 0,JOptionPane.QUESTION_MESSAGE,null, optionsMonthsOrYears, optionsMonthsOrYears[0]);
			}
		}
	   }

	/*================================================================================*/
	/**
	set up an array to send all of these values back to the  
	method which called for input...                         
	*/
	double [] input = {initialLoanPrinciple,APR,monthlyPayment, interestType,lengthOfLoan}; 
	return input ; //return the array of values...

   }// end input

public  double bugProofInput (String Input)
/*****************************************************************************************************************
* a method to check for unwanted characters in input and remove them, in order to be able to parse input to #'s  *
* foolproffs against users entering $, %, commas, words, and anything non-numeric(periods count as numeric)      *
*****************************************************************************************************************/
    {
	//if the string is empty or null
	if (Input == null || Input == "" || Input.length() == 0 )
	   {
	   double output = -1;//return a negative number so it loops back and asks again for the input
	   return output;
	   }	

	int positionInString = 0;//start at first character
	char character = Input.charAt(0);//define character to be questioned
	int stringLength = Input.length() ;//find length of string
	int numberOfNonNumbers = 0;//so far there are no non-numerical values in the string...now let's test for them
	for (positionInString = 0; positionInString < stringLength ;positionInString ++ )//check 1 character at a time...
	   {
	   character = Input.charAt(positionInString);
	   if (Character.isDigit(character) != true && character != '.')//if the character in question is not a digit and not a period...
		{
		numberOfNonNumbers ++;//add 1 to th enumber of non-numbers
		}
	   }
	if (numberOfNonNumbers == stringLength)//if the entire string contains no numbers
	   {
	   double output = -1;//return a negative number so it loops back and asks again for the input
	   return output;
	   }	
	else 
	   {//the string contains atleast 1 number...
	   //test to remove unwanted characters...
	   for (positionInString = 0; positionInString < stringLength ; )
	    {
	     character = Input.charAt(positionInString);
	     if (Character.isDigit(character) != true && character != '.')//if the character in question is not a digit and not a period...
		{
		String ch = "" + character; //set string ch equal to the offending character 
		Input = Input.replace(ch, "");//replace ch with a blank string, essentially deleting all instances of it
		positionInString = 0;//go back to beginning to get out others
		stringLength = Input.length();//reset the string length
		}
	     else//the character at this point in the string is a digit, so...
		{
		positionInString ++;//move on to find other non-digits...
		}
	    }
	   double output = Double.parseDouble(Input);//parse the 100% numeric string to a double
	   return output;//return this double to whoever called for it
	   }
    }//end bugProofInput

public  int howManyRows (double initialLoanPrinciple, double APR, double interestType, double lengthOfLoan)
/***********************************************************************
* sees for how many months the loan runs , and then returns this value *
* needed to setup array for table and the table itself...              *
***********************************************************************/
    {
	int month = 0;
	double remainingPrinciple = initialLoanPrinciple;//in the beggining you have the whole thing left to pay off
	double monthlyInterest = 0;
	double monthlyPrinciple = 0;
	double finalMonthlyPayment = 0;
	double totalInterest = 0;
	double totalLoanAmount = 0;

	if (monthlyPayment == -1)//monthly payment is unknown
	   {
	   if (interestType == 0)//interest is simple 
		{
		totalInterest = initialLoanPrinciple * (APR / 100.0)  ;
		totalLoanAmount = totalInterest + initialLoanPrinciple;
		monthlyPayment = totalLoanAmount / lengthOfLoan;
		}
	   else if (interestType == 1)//interest is continuous compound
		{
		//interest = prin * (e^(r*t))
		totalLoanAmount  = ( initialLoanPrinciple * ( Math.pow( E , ( (APR / 100.0) * ( lengthOfLoan / 12.0 ) ) ) ) ) ;
		monthlyPayment = totalLoanAmount /  lengthOfLoan ;
		}
	   else if (interestType == 2)//interest is periodic compound 
		{
		//Monthly payment = Principle * ( (APR/ (100 *12)) / (1 - (1 + (APR/ (100 *12))) ^ -lengthOfLoan ))
		monthlyPayment = initialLoanPrinciple * ((APR/(100.0 *12.0)) / (1 - Math.pow( 1.0 + (APR / (100.0 *12.0)), -lengthOfLoan ) ) );
		}
	   }
	while (monthlyPayment < remainingPrinciple)
	   {	
	   if (interestType == 0)/*interest is simple */
		{
		DecimalFormat formatter = new DecimalFormat("#0");
		totalInterest = initialLoanPrinciple * (APR / 100.0)  ;
		totalLoanAmount = totalInterest + initialLoanPrinciple;
		numberOfRows = (int)Math.round(totalLoanAmount / monthlyPayment );
		return numberOfRows;
		}
	   else if (interestType == 1)/*interest is continuous compound */
		{
		//new amount = prin. * (e^(r*t))
		monthlyInterest = ( remainingPrinciple * ( Math.pow( E , ( (APR / 100.0) * ( 1.0 / 12.0 ) ) ) ) ) - remainingPrinciple  ;
		}
	   else if (interestType == 2)/*interest is periodic compound */
		{
		//compund interest = principle * ((1 + (interest rate / 100 )/12)^(months / 12) ) - 1 ) 
		monthlyInterest = remainingPrinciple * (APR / 100.0) * (1.0 / 12.0);
		}
	   monthlyPrinciple = monthlyPayment - monthlyInterest ;
	   remainingPrinciple = remainingPrinciple - monthlyPrinciple ;
	   /*Let's avoid an infinite loop here...*/
	   if (monthlyInterest >= monthlyPayment)
		{
		JOptionPane.showMessageDialog(null, "Your monthly interest is greater than your monthly payment." + "\nYou will never pay this loan off.  Sorry.");
		int tryAgain = JOptionPane.showConfirmDialog (null, "Would you like to try again?");
		if (tryAgain == 0) //clicked yes to try again
			{
			return -1;
			}
		else //clicked no, canel, or the close
			{
			System.exit(0); 
			}
		}
	   month++;
	   }
	//last month is different///////////////////////
	if (monthlyPayment > remainingPrinciple)//account for lower final monthly payment
	   {
	   month++;
	   }
	numberOfRows = month ;
	return numberOfRows;
    }//end of howManyRows


public  double [][] doTheMath (double initialLoanPrinciple, double APR, double interestType, double lengthOfLoan)
/********************************************************************
* does the main calculations of the program, as well as assembling  *
* all data into a 2d array for easy reproduction in the table       *
********************************************************************/
    {
	int month = 1;
	double remainingPrinciple = initialLoanPrinciple;
	double monthlyInterest = 0;
	double monthlyPrinciple = 0;
        totalInterestPaid = 0;
	double totalInterestPaid = 0;
	double finalMonthlyPayment = 0;
	int year = 1;
	DecimalFormat formatter = new DecimalFormat("#000.00"); /*to make table look good*/
	DecimalFormat monthFormatter = new DecimalFormat("#0"); /*to make table look good*/
	double totalInterest = 0;
	double totalLoanAmount = 0;

	//need to setup data array and put all of the data into it...
	double [][] tableData = new double [numberOfRows ][6];
	
	/*================================================================================*/
	if (monthlyPayment == -1)//monthly payment is unknown
	   {
	    if (interestType == 0)//interest is simple 
		{
		totalInterest = initialLoanPrinciple * (APR / 100.0)  ;
		totalLoanAmount = totalInterest + initialLoanPrinciple;
		monthlyPayment = totalLoanAmount / lengthOfLoan ;
		}
	    else if (interestType == 1)//interest is continuous compound
		{
		//interest = prin * (e^(r*t))
		totalLoanAmount  = ( initialLoanPrinciple * ( Math.pow( E , ( (APR / 100.0) * ( lengthOfLoan / 12.0 ) ) ) ) )  ;
		monthlyPayment = totalLoanAmount / lengthOfLoan ;
		}
	    else if (interestType == 2)//interest is periodic compound 
		{
		//Monthly payment = Principle * ( APR / (1 - (1 + APR) ^ -lengthOfLoan * 12))
		monthlyPayment = initialLoanPrinciple * ( (APR/(100.0 * 12.0) ) / (1 - Math.pow( 1.0 + (APR / (100.0 * 12.0) ), -lengthOfLoan ) ) );
		}
	   }
	/*================================================================================*/
	
	//setup count to put data in right row
	int row = 0;
	while (monthlyPayment < remainingPrinciple)
	   {
	   /**
	   known = interest rate (APR), principle, payment
	   interest + principle = payment   ==>  payment - interest = monthly principle 
	   compund interest = ( principle * ((1 + (interest rate / 100 ))^(months / 12) ) - 1 ) 
	   simple interest = principle * (interest rate/100) * (month/12)
	   contin. compound interest = ( prin. * (e^(r*t)) ) - princ.
	   */	
	    if (interestType == 0)/*interest is simple */
		{
		monthlyInterest = ( initialLoanPrinciple * (APR / 100.0)  ) /numberOfRows ;
		}
	    else if (interestType == 1)/*interest is continuous compound */
		{
		//interest = prin. * (e^(r*t))
		monthlyInterest = remainingPrinciple * ( Math.pow( E , ( (APR / 100.0) * ( 1.0 / 12.0 ) ) ) )  - remainingPrinciple  ;
		}
	    else if (interestType == 2)/*interest is periodic compound */
		{
		//compund interest = principle * ((1 + (interest rate / 100 ))^(months / 12) ) - 1 ) 
		monthlyInterest = remainingPrinciple * (APR / 100.0) * (1.0 / 12.0);//( Math.pow( 1.0 + APR / 100.0  , 1.0 / 12.0) - 1.0 )  ;
		}
	    monthlyPrinciple = monthlyPayment - monthlyInterest ;
	    remainingPrinciple = remainingPrinciple - monthlyPrinciple ;		
	    totalInterestPaid = monthlyInterest + totalInterestPaid ;
/**
tableData array is set up as such:
	collum  0           1                 2                 3                     4                    5
              Month   Monthly Payment   Monthly Interest  Monthly Principle   Remaining Principle  Total Interest Paid 
  row 0
  row 1
   etc...	
to put data into the tableData array, which is of type double [][], 
we need to first format it to the right decimal place, which returns a string.
then we need to parse the formatted string to the a double and place it in the correct place in the string
*/ 

	    tableData [row] [0] = Double.parseDouble(monthFormatter.format(month));
	    tableData [row] [1] = Double.parseDouble(formatter.format(monthlyPayment));
	    tableData [row] [2] = Double.parseDouble(formatter.format(monthlyInterest)) ;
	    tableData [row] [3] = Double.parseDouble(formatter.format(monthlyPrinciple)) ;
	    tableData [row] [4] = Double.parseDouble(formatter.format(remainingPrinciple)) ;
	    tableData [row] [5] = Double.parseDouble(formatter.format(totalInterestPaid)) ;

	    month++ ;
	    row ++ ;
	   }//end of while loop to populate array while it is not the last month
	
	//last month is different///////////////////////
	if (monthlyPayment > remainingPrinciple)//account for lower final monthly payment
	   {
	    if (interestType == 0)/*interest is simple */
		{
		monthlyInterest = ( initialLoanPrinciple * (APR / 100.0)  ) /numberOfRows ;
		}
	    else if (interestType == 1)/*interest is continuous compound */
		{
		//interest = prin. * (e^(r*t))
		monthlyInterest = remainingPrinciple * ( Math.pow( E , ( (APR / 100.0) * ( 1.0 / 12.0 ) ) ) )  - remainingPrinciple  ;
		}
	    else if (interestType == 2)/*interest is periodic compound */
		{
		//compund interest = principle * ((1 + (interest rate / 100 ))^(months / 12) ) - 1 ) 
		monthlyInterest = remainingPrinciple * (APR / 100.0) * (1.0 / 12.0);//( Math.pow( 1.0 + APR / 100.0  , 1.0 / 12.0) - 1.0 )  ;
		}
	    finalMonthlyPayment = monthlyInterest + remainingPrinciple;
	    monthlyPrinciple = finalMonthlyPayment - monthlyInterest ;
	    totalInterestPaid = monthlyInterest + totalInterestPaid ;
	    remainingPrinciple = 0;

	    tableData [row] [0] = Double.parseDouble(monthFormatter.format(month));
	    tableData [row] [1] = Double.parseDouble(formatter.format(finalMonthlyPayment));
	    tableData [row] [2] = Double.parseDouble(formatter.format(monthlyInterest)) ;
	    tableData [row] [3] = Double.parseDouble(formatter.format(monthlyPrinciple)) ;
	    tableData [row] [4] = Double.parseDouble(formatter.format(remainingPrinciple)) ;
	    tableData [row] [5] = Double.parseDouble(formatter.format(totalInterestPaid)) ;
	   }
	return tableData;
    }//end of doTheMath

/*================================================================================*/
/***********************************************************
This part of the Program draws the graphical user interface 
of the loan amortization table, including setting up colors 
and buttons.  (Made mostly with netbeans).
 ************************************************************/
class tableFrame extends JFrame
    {
    private JPanel MasterPanel;
    private JPanel buttonPanel;
    private JLabel emptySpaceLabelLeft;
    private JLabel emptySpaceLabelRight;
    private JPanel tablePanel;
    private JPanel topLabelPanel;
    private final int WINDOW_WIDTH = 475;
    private final int WINDOW_HEIGHT = 775;

    public tableFrame() 
    	{
        initComponents();
    	}

    private void initComponents() 
        {
	///////////////define objects////////////////////
	DecimalFormat formatter = new DecimalFormat("#,000.00"); /*to make table look good*/
	DecimalFormat monthFormatter = new DecimalFormat("#0"); /*month doesn't need decimals*/
	
	MasterPanel = new JPanel();//everything goes onto the master panel

        topLabelPanel = new JPanel();
        emptySpaceLabelLeft = new JLabel();
        APRLabel = new JLabel();
        InitialPrincipleLabel = new JLabel();
        emptySpaceLabelRight = new JLabel();

        tablePanel = new JPanel();

        buttonPanel = new JPanel();
        runAgainButton = new JButton();
        showGraphsButton = new JButton();
        quitButton = new JButton();

	//make them do stuff
	runAgainButton.addActionListener(new runAgainButton_Action());
	showGraphsButton.addActionListener(new showGraphsButton_Action());
	quitButton.addActionListener(new quitButton_Action());	

        ////////////////setup various things/////////////////////////////////////////
	setTitle("Loan Amortization Table");
	setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Close when X is clicked


	/////////////master panel/////////////
	//set it up w/ a border layout so it automatically resizes when resized or maximized
	MasterPanel.setLayout(new BorderLayout(10 ,10) );
	
	//////top panel////////////
        topLabelPanel.setLayout(new java.awt.GridLayout(1, 4));
        topLabelPanel.add(emptySpaceLabelLeft);
        APRLabel.setText("APR: "+APR + "%");
        topLabelPanel.add(APRLabel);
        InitialPrincipleLabel.setText("Initial Principle : $"+ formatter.format(initialLoanPrinciple));
        topLabelPanel.add(InitialPrincipleLabel);
        topLabelPanel.add(emptySpaceLabelRight);


	//////////////create the table//////////////
	tableModel = new DefaultTableModel(){public boolean isCellEditable(int rowIndex, int mColIndex){return false;}};//why would people want to edit the table?
	amortizationTable = new JTable(tableModel); //initialize the table using the above table model
	scrollPane = new JScrollPane(amortizationTable); //make it scrollable
	//add the headers to the table
	String[] tableHeaders = {"Month", "Monthly Payment","Monthly Interest","Monthly Principle","Remaining Principle","Total Interest Paid"};
	for (int i=0; i<6; i++)//use a for loop to add the headers to the table, b/c im lazy =), also easier to add/ subtract collums later
		{
		tableModel.addColumn(tableHeaders[i]);
		}
	//setup the table
	amortizationTable.getParent().setBackground(amortizationTable.getBackground()); //background is nothing right now(it is set later)
	//allow resize, but not reorder...you'll see why...(has to do with the year label)
	amortizationTable.getTableHeader().setResizingAllowed(true);
	amortizationTable.getTableHeader().setReorderingAllowed(false);
	//allow selection of everything...
	amortizationTable.setColumnSelectionAllowed(true);
	amortizationTable.setRowSelectionAllowed(true);
	amortizationTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	//show the grid to make it easier to read
	amortizationTable.setShowGrid(true);
	//row/column count
	amortizationTable.setRowHeight(38);
	tableModel.setColumnCount(6);
	tableModel.setRowCount(numberOfRows + years);//need to have enough rows for the data and the year markers...
        //make the table scrollable
        scrollPane.setViewportView(amortizationTable);

	//refresh table
	refreshTable (tableData, years); //refresh table 

	/////////button Panel/////////////////////
        buttonPanel.setLayout(new java.awt.GridLayout(1, 3, 170, 0));
        runAgainButton.setText("Run Again");
        buttonPanel.add(runAgainButton);
        showGraphsButton.setText("Show Graphs");
        buttonPanel.add(showGraphsButton);
        quitButton.setText("Quit");
        buttonPanel.add(quitButton);

	/////////put everything onto the frame//////////////
	MasterPanel.add(topLabelPanel , BorderLayout.NORTH );
	MasterPanel.add(scrollPane , BorderLayout.CENTER );
	MasterPanel.add(buttonPanel, BorderLayout.SOUTH );
	MasterPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 5) );
	add(MasterPanel);

	//pack for your vacation...
        pack();
        }//end of the initComponents method
   }//end of the tableFrame class

public  void refreshTable(double [][] tableData , int years)
/****************************************************************************
* this method clears the table of previous data and fills the table w/data  *
****************************************************************************/
   {
	DecimalFormat formatter = new DecimalFormat(",##0.00"); /*to make table look good*/
	DecimalFormat monthFormatter = new DecimalFormat("#0"); /*to make table look good*/
	//allow buttons
	runAgainButton.setEnabled(true);
	quitButton.setEnabled(true);
	//Clear table
	for (int row=0; row< (numberOfRows + years); row++)
	   {
	     for (int collum=0; collum<6; collum++)
		{
		tableModel.setValueAt(null, row, collum);//fill the table w/null values
		}
	   }
	//years is the total number of years that the loan runs for
	//year is the current year when drawing the table...used as a counter..not to be confused with years
	int year = 0;
	//populate table with data
	for (int row = 0; row < (numberOfRows + years); )
	   {
	    if (row  % 13 == 0)//at year mark draw the year label
/*****************************************************************************************************************
 * you would expect that it would be if row %12 == 0, but its 13.  This is a little wierd but let me explain:    *
 * the rundown of the table goes as follows:									 *
 * row 0	Year 1											 	 *
 *     1   month 1 data.....										 	 *
 * 	        (etc)...											 *
 *     12	month 12 data.....										 *
 *     13	Year 2											 	 *
 *     14  month 13 data....											 *
 * 	        (etc)...											 *
 *     25  month 24 data...											 *
 *     26  	Year 3												 *
 * 	        (etc)...											 *
 * so, because of the first year label, the second label will occur 12 lines after row 0, which is row 13        *
 * also, notice how the offset of the data from its original value in the array increases by one for every year..*
 * (ie) month 1 is held at [0][0] in the array, but because of the year label, 					 *
 * 	it occurs at [1][0] in the table, so the offset of data in year 1 is 1					 *
 *      month 13 is held at [12][0] in the array, but becase of the year label, 			         *
 * 	it occurs at [14][0] in the table, so the offset of data in year 2 is 2					 *
 *****************************************************************************************************************/
		{
		year ++;//add one to the year displayed
		tableModel.setValueAt(null, row, 0);
		tableModel.setValueAt(null, row, 1);
		tableModel.setValueAt("Year", row, 2);
		tableModel.setValueAt(year , row, 3);				
		tableModel.setValueAt(null, row, 4);
		tableModel.setValueAt(null, row, 5);
		}
	    else 
		{//all other data, expand the collum
		 for (int collum = 0; collum < 6; collum ++)//loop to extract collum by collum the data from the array
		    {
		    if (collum == 0)//format the month value w/o decimals...
			{
			tableModel.setValueAt(monthFormatter.format(tableData[row - year][collum]), row , collum);
			//must subtract the year, as this is the offset of the data from the orginal array
			}
		    else    
		        {
			tableModel.setValueAt(formatter.format(tableData[row - year][collum]), row , collum);
			//must subtract the year, as this is the offset of the data from the orginal array
		        }
		    }
		}
		row++;
	   }
	//colorize your world...
	amortizationTable.setDefaultRenderer(amortizationTable.getColumnClass(0), new amortizationTableRenderer());
   }//end of refreshTable

class amortizationTableRenderer extends DefaultTableCellRenderer
  {
    public Component getTableCellRendererComponent (JTable table, Object value, boolean selected, boolean focused, int row, int column)
	/*********************************
	 * applies color to the table... *
         *********************************/
	{
        super.getTableCellRendererComponent(table, value, selected, focused, row, column);
	if (row % 13 == 0)//year mark
	     {
		setBackground(new Color(220, 225, 255));//blue
	     }

	else {
		if (row % 2 == 0){ //even
			setBackground(new Color(220, 255, 220));//greenish
		}
		else{ //odd
			setBackground(new Color(255, 255, 255));//white
	            }
             }
            setBorder(null);
            setForeground(Color.black);
            return this;  
        }
  }
class runAgainButton_Action implements ActionListener 
   {
     public void actionPerformed (ActionEvent e)
        /*************************************
	 *if the run again button is clicked *
         *************************************/
        {
	   //set an array to return multiple values from 1 method...
	   double [] input = input();
	           //input = {initialLoanPrinciple,APR,interestType, lengthOfLoan};
	   double initialLoanPrinciple = input [0] ;
	   double APR = input [1]  ;
	   monthlyPayment = input [2]  ;
	   double interestType = input [3] ;
	   double lengthOfLoan = input [4] ;
	
	   //call howManyRows to determine correct # of rows to setup tableData array correctly
	   numberOfRows = howManyRows(initialLoanPrinciple, APR, interestType, lengthOfLoan);
	   while (numberOfRows < 0)//howManyRows returns a negative # when an infinite loop was broken up, so better data must be entered
		{
		//set an array to return multiple values from 1 method...
		input = input();
	      //input = {initialLoanPrinciple,APR,interestType};
			initialLoanPrinciple = input [0] ;
			APR = input [1]  ;
			monthlyPayment = input [2]  ;
			interestType = input [3] ;
			lengthOfLoan = input [4];
		
		//call howManyRows to determine correct # of rows to setup array correctly
		numberOfRows = howManyRows(initialLoanPrinciple, APR, interestType, lengthOfLoan);
		}//loop will only terminate when data is entered such that there is no infinite loop...
	
	   //setup new array to hold the values for the table
	   tableData = new double [numberOfRows][6];
	   //set up the array to equal the result of the doTheMath method
	   tableData = doTheMath(initialLoanPrinciple, APR, interestType, lengthOfLoan);
		
	   // months passed = numberOfRows;
	   double yearMarker = numberOfRows % 12;
	   years = numberOfRows / 12 ;
		if (yearMarker != 0)//a year and some months has passed, so need to add in 1 more year
		   {
		   years++;
		   }

	   mainFrame.setVisible(false);
	   mainFrame = new tableFrame();
	   //for some reason, labels don't get set correctly when run twice, so reset the values for the labels
	   APRLabel.setText("APR: "+APR + "%");
	   DecimalFormat formatter = new DecimalFormat(",##0.00"); /*to make table look good*/
	   InitialPrincipleLabel.setText("Initial Principle : $"+ formatter.format(initialLoanPrinciple));

	   mainFrame.setVisible(true);

	   double totalInterestPaid = tableData[numberOfRows - 1][5] ;
	   pieChart(initialLoanPrinciple, totalInterestPaid);
	   graph();
        }
   }

class quitButton_Action implements ActionListener
   {
     public void actionPerformed (ActionEvent e)
        /*************************************
	 *  if the quit button is clicked    *
         *************************************/
        {
	System.exit(0); 
        }
    }

class showGraphsButton_Action implements ActionListener
   {
     public void actionPerformed (ActionEvent e)
        /*************************************
	 *  if the graph button is clicked   *
         *************************************/
        {
	 graphFrame.setVisible(false);
         pieChartFrame.setVisible(true);
        }
    }

/*================================================================================*/
/***********************************************
 This part of the Program draws the pie chart,
 which graphically shows the amount paid on
 principle and amount paid in interest
 This allows the user to see at a glance if 
 the loan is a good idea, allowing them to 
 easily see how much interest is being paid,
 visually
 ***********************************************/

public  void pieChart(double initialLoanPrinciple, double totalInterestPaid)
/***********************************
 * this method draws the pie chart *
 ***********************************/
   {
	DecimalFormat formatter = new DecimalFormat("#,000.00");
	DecimalFormat percentFormatter = new DecimalFormat("#0.0%");
	int radius = 300 ;

	 totalAmountPaid = initialLoanPrinciple + totalInterestPaid;
	 principlePercentage = initialLoanPrinciple / totalAmountPaid ; //find percentage of total which is the initial principle
	 interestPercentage = totalInterestPaid / totalAmountPaid ; //find percentage of total which is the interest
	 double totalPaidPercentage = totalAmountPaid / initialLoanPrinciple; //find percentage of total compared to inital principle
   	///////////////////////draw the window//////////////////////////////
   		//set up the frame
		pieChartFrame = new JFrame ("Pie Chart"); //Create frame
		pieChartFrame.setSize( 2 * radius + 60, 2 * radius + 30 + 105); //Set size to be a function of the radius...
		chartPane = pieChartFrame.getContentPane(); //Get content pane
		chartPane.setLayout(null); //setup the frame as blank

		//create button
		lineGraphButton = new JButton ("Line Graph");//button to switch graph type
		//make it do stuff
		lineGraphButton.addActionListener(new lineGraphButton_Action());

		//create labels
		principlePaidLabel = new JLabel ();
		principlePaidLabel.setText("Principle Paid: $" + formatter.format(initialLoanPrinciple) + "  (" + percentFormatter.format(principlePercentage) +")"  );
		interestPaidLabel = new JLabel  ();
		interestPaidLabel.setText("Interest Paid: $" + formatter.format(totalInterestPaid)+ "  (" + percentFormatter.format(interestPercentage) +")" );
		totalPaidLabel = new JLabel ();
		totalPaidLabel.setText ( "Total Paid: $" + formatter.format(totalAmountPaid)+ "  (" + percentFormatter.format(totalPaidPercentage) +" of initial principle)" );

		chartPanel = new JPanel(null); //new panel w/nothing on it
		
		Component PieSlice = new PieSlice();
		Component LegendSquares = new LegendSquares();
		//add stuff to the window
		chartPane.add(chartPanel);
		//chartPanel.add(Circle);
		chartPanel.add(PieSlice);
		chartPanel.add(LegendSquares);
		chartPanel.add(lineGraphButton);
		chartPanel.add(principlePaidLabel);
		chartPanel.add(interestPaidLabel);
		chartPanel.add(totalPaidLabel);
					
		//place the stuff at specific places in window
		//setBounds(x , y , width , height)
		chartPanel.setBounds(0, 0, 2 * radius + 60 , 2 * radius + 30 + 105);
		PieSlice.setBounds(0,0, 2*radius, 2*radius);
		lineGraphButton.setBounds(radius - 45, 2 * radius + 70, 120, 25);
		LegendSquares.setBounds(radius-100, 2*radius + 10, 20,50);
		principlePaidLabel.setBounds(radius - 70 , 2 * radius + 10 , 300 , 20);
		interestPaidLabel.setBounds(radius - 70 , 2 * radius + 40 , 300 , 20);
		totalPaidLabel.setBounds(radius - 140 , 2 * radius - 20, 400 , 20);

		//make frame visible
		pieChartFrame.setResizable(false);
		pieChartFrame.setVisible(false);//don't show the frame just yet, will shwo when the show graphs button is clicked...
   }//end of pieChart()

 class PieSlice extends JComponent {
        /*************************************
	 *  draws the pieChart               *
         *************************************/
            public void paint(Graphics g) {
                Graphics2D g2d = (Graphics2D)g;

                // Draw an oval that fills the window
		double radius = 300 ;

		//draw pie slice for interest paid
		double X = 50;
		double Y = 30;
		double stopAngle = 360 * interestPercentage;
		
		g2d.setPaint(new Color(225, 0, 0));//red
		g2d.fill (new Arc2D.Double(X,Y, radius *2 - 62, radius*2 -60, 0.0 ,stopAngle, Arc2D.PIE));
		
		double stopAngle2 = 360 * principlePercentage;
		g2d.setPaint(new Color(0, 225, 0));//green
		g2d.fill (new Arc2D.Double(X,Y, radius *2 - 62, radius*2 -60, stopAngle ,stopAngle2, Arc2D.PIE));
            }
	}

 class LegendSquares extends JComponent {
        /*************************************
	 *  draws the legend squares         *
         *************************************/
            public void paint(Graphics g) {

                Graphics2D g2d = (Graphics2D)g;
		double X = 0;
		double Y = 0;
		int length = 20;
		int width = 20;
		
		g2d.setPaint(new Color(0, 225, 0));//green
		g2d.fill (new Rectangle2D.Double(X,Y, length, width));

		g2d.setPaint(new Color(225, 0, 0));//red		
		g2d.fill (new Rectangle2D.Double(X,Y +30, length, width));
            				}
	}
class lineGraphButton_Action implements ActionListener
   {
     public void actionPerformed (ActionEvent e)
        /*************************************
	 *  if the graph button is clicked   *
         *************************************/
        {
	 graphFrame.setVisible(true);
         pieChartFrame.setVisible(false);//show the frame
        }
    }

/*================================================================================*/
/***************************************************************
 This part of the Program draws the graph which shows the user
 the breakdown of the monthly payment (how much is being paid to
 interest and how much is being paid to the principle)  It is 
 not necessary, but I think that it is a valueable addition to 
 the program, as well as a challenge for myself 
 (it's always necessary to push the envelope...).                                                 
*****************************************************************/
public  void graph()
/***********************************
 * this method draws the graph     *
 ***********************************/
   {
	//set up the width and height of window as variables, so it is easier change this later
	int width = 600;
	int height = 640;
   	///////////////////////draw the window//////////////////////////////
   		//set up the frame
		graphFrame = new JFrame ("Graph"); //Create frame
		graphFrame.setSize( width,height); //Set size 
		graphPane = graphFrame.getContentPane(); //Get content pane
		graphPane.setLayout(null); //setup the frame as blank

		//create button
		pieChartButton = new JButton ("Pie Chart");//button to switch graph type
		//make it do stuff do stuff
		pieChartButton.addActionListener(new pieChartButton_Action());

		//create labels
		graphTitleLabel = new JLabel ();
		graphTitleLabel.setText("Breakdown Of Monthly Payment" );

		graphYAxisLabel = new JLabel ();
		graphYAxisLabel.setText("$" );

		graphXAxisLabel = new JLabel ();
		graphXAxisLabel.setText("Month" );

		JLabel principleLabel = new JLabel ();
		principleLabel.setText("Monthly Principle");

		JLabel interestLabel = new JLabel ();
		interestLabel.setText("Monthly Interest");

		dynamicLegendMonth = new JLabel ();
		dynamicLegendPrinciple = new JLabel ();
		dynamicLegendInterest = new JLabel ();
		dynamicLegendMonth.setText(    "Put your mouse");
		dynamicLegendPrinciple.setText(" on the graph ");
		dynamicLegendInterest.setText( "for the legend");

		Component Graph = new drawGraph();
		Graph.addMouseMotionListener(new mouseMotion_Action());

		Component LegendSquares = new LegendSquares();

		legendSelectorLine = new legendSelectorLine();

		graphPanel = new JPanel(null); //new panel w/nothing on it

		//add stuff to the window

		graphPane.add(graphPanel);
		graphPanel.add(legendSelectorLine);
		graphPanel.add(Graph);
		graphPanel.add(LegendSquares);
		graphPanel.add(principleLabel);
		graphPanel.add(interestLabel);
		graphPanel.add(pieChartButton);
		graphPanel.add(graphTitleLabel);
		graphPanel.add(graphYAxisLabel);
		graphPanel.add(graphXAxisLabel);
		graphPanel.add(dynamicLegendMonth);
		graphPanel.add(dynamicLegendPrinciple);
		graphPanel.add(dynamicLegendInterest);


		//place the stuff at specific places in window
		//setBounds(x , y , width , height)
		graphPanel.setBounds(0, 0,600,620);
		Graph.setBounds(35,40,width - 35,height -120);
		principleLabel.setBounds(40, height - 90 , 300 , 20);
		interestLabel.setBounds(40, height - 90 + 30 , 300 , 20);
		LegendSquares.setBounds(10, height - 90, 20,50);
		graphTitleLabel.setBounds(width/2 - 105,10,210,20);
		graphYAxisLabel.setBounds(10,height/2 - 25,25,20);
		graphXAxisLabel.setBounds(width/2 - 25,height - 100,50,20);
		pieChartButton.setBounds(width/2 - 60, height - 65, 110, 25);
		dynamicLegendMonth.setBounds(width - 200, height - 90, 100,20);
		dynamicLegendPrinciple.setBounds(width - 200, height - 70, 250,20);
		dynamicLegendInterest.setBounds(width - 200, height - 50, 250,20);
		legendSelectorLine.setBounds(601,625,3,600-125);

		//make frame visible
		graphFrame.setResizable(false);
		graphFrame.setVisible(false);//don't show the frame just yet, will shwo when the show graphs button is clicked...

   }

 class drawGraph extends JComponent
   {
/*******************************************
 * this class draws the graph component    *
 *******************************************/
    public void paint(Graphics g) 
	{
	//draw the Grid
	double width = 600 - 80 - 30;
	double height = 600 - 140 - 10;
	double boxHeight;
		/*	      width
			|---------------|	
			   boxWidth
			       || 
			       \/
	_		_________________
	| boxHeight---->| | | |	| | | | |
	|		_________________
	|		| | | |	| | | | |
height	|		_________________
	|		| | | |	| | | | |
	|		_________________
	|		| | | |	| | | | |
	_		_________________
		*/
	if (numberOfRows >= width)
		{
		boxWidth = width / (numberOfRows );
		boxHeight = (height - 15) / 19;
		}
	else
		{
		boxWidth = (int)Math.round(width / (numberOfRows ));
		boxHeight = (int)Math.round((height - 15) / 19);
		}
        Graphics2D g2d = (Graphics2D)g;

	//background for graph
	g2d.setPaint(new Color(225, 225 ,225) );//gray
	g2d.fill (new Rectangle2D.Double(50,10, boxWidth * (numberOfRows -1), boxHeight * 20));

	//draw the vertical lines...
	double x,y;
	x = 50;
	int year = 1;
	for (int counter = 0; counter < numberOfRows ; counter ++)
		{
		if (counter % ((11 * year) + year - 1) == 0)//year
			{
			g2d.setColor(new Color(0, 0, 235));//blue
			year ++;
			}
		else if (counter == 11)
			{
			g2d.setColor(new Color(0, 0, 235));//blue
			}
		else if (counter % 2 == 0)//every other line
			{
			//g2d.setColor(new Color(100, 155, 100));//greenish
			g2d.setColor(Color.white);
			}
		else 
			{
			g2d.setColor(Color.white);
			}
    		g2d.draw(new Line2D.Double(x, 10, x, (boxHeight * 20) + 10));
		x += boxWidth;
		}
	
	//draw the horizontal lines...
	y = 10;
	for (int counter = 0; counter < 21; counter ++)
		{
		g2d.setColor(Color.white);			
    		g2d.draw(new Line2D.Double(50, y, boxWidth * (numberOfRows -1) + 50, y ));
		y += boxHeight;
		}

	//draw the scale on the y axis
	g2d.setColor(Color.black);
	double interval = monthlyPayment / 20; //the interval of the y axis is the monthly payment divided by the number of rows in the y axis
	double currentValue = 0;
	y = 600 - 125 ;
	for (int counter = 0; counter <= 20 ; counter ++)
	   {
	   DecimalFormat formatter = new DecimalFormat("#0.00"); 
	   String currentValueString = formatter.format(currentValue);
	   g2d.drawString(currentValueString, 0, (int)Math.round(y));
	   currentValue += interval;
	   y -= boxHeight ;
	   }

	//draw the lines to represent the values of interest paid and principle paid
	double x1 = 50;
	double x2 = boxWidth + 50;
	double y1 , y2;
	//interest paid
	for (int currentRow = 0; currentRow < numberOfRows -1; currentRow ++)
		{
		g2d.setColor(new Color(225, 0, 0));//red
		double interest1 = tableData [currentRow][2] ;
		y1 = (height +10)  * (1 - (interest1 / monthlyPayment) ) + 10;
		double interest2 = tableData [currentRow + 1][2] ;
		y2 = (height +10) * (1 - (interest2 / monthlyPayment) ) + 10;
		g2d.draw(new Line2D.Double(x1,y1,x2,y2));
		x1 += boxWidth;
		x2 += boxWidth;
		}
	//principle paid
	x1 = 50;
	x2 = (int)Math.round(boxWidth + 50);
	for (int currentRow = 0; currentRow < numberOfRows -1; currentRow ++)
		{
		g2d.setColor(new Color(0, 200, 0));//dark green
		double interest1 = tableData [currentRow][3] ;
		y1 = (height +10 ) * (1 - (interest1 / monthlyPayment) ) + 10;
		double interest2 = tableData [currentRow + 1][3] ;
		y2 = (height + 10)  * (1 - (interest2 / monthlyPayment) ) + 10;
		g2d.draw(new Line2D.Double(x1,y1,x2,y2));
		x1 += boxWidth;
		x2 += boxWidth;
		}
        }
   }

class legendSelectorLine extends JComponent
/***********************************************
 * this class draws the dynamic selector line  *
 ***********************************************/
   {
   public void paint(Graphics g)
	{
	Graphics2D g2d = (Graphics2D)g;
	g2d.setColor(Color.yellow);
	g2d.draw(new Line2D.Double(1,0,1,600-125));
	}
   }

class mouseMotion_Action implements MouseMotionListener 
/***********************************************
 * this class moves the dynamic selector line  *
 ***********************************************/
   {
   public void mouseMoved(MouseEvent event) 
	{
	updateDynamicLegend(event);
	}

   public void mouseDragged(MouseEvent event) 
	{
	updateDynamicLegend(event);
	}
	
   public void updateDynamicLegend(MouseEvent event) 
	{
	int position = event.getX();
	position -= 50;
	int boxWidthInt = (int)Math.round(boxWidth);
	if (position % boxWidthInt == 0 && position >= 0 && position <= ((numberOfRows * boxWidthInt) - boxWidthInt) )//it is on the month
		{
		dynamicLegendMonth.setText("Month: " + ( (position / boxWidthInt) + 1 ) );
		dynamicLegendPrinciple.setText("Monthly Principle: $" + tableData [(position / boxWidthInt)] [3]  );
		dynamicLegendInterest.setText("Monthly Interest: $" + tableData [(position / boxWidthInt)] [2] );
		legendSelectorLine.setBounds(position + 35 + 50,50 ,3,600-140);
		}
   	}
   }

class pieChartButton_Action implements ActionListener
   {
     public void actionPerformed (ActionEvent e)
        /*************************************
	 *  if the graph button is clicked   *
         *************************************/
        {
	 graphFrame.setVisible(false);
         pieChartFrame.setVisible(true);//show the frame
        }
    }

}
/*End of the program.  Hope you had fun reading it!*/
