/*******************************************************************************
//	DESCRIPTION:	Lab 8: Basic calculator. Dependence on numberOnlyInput.h, 
//                  which relies on conio.h, makes this a windows only program
//  AUTHOR:			Andrew Paettie
//	DATE:			October 2011
//*****************************************************************************/

#include <iostream>
#include "numberOnlyInput.h"
#include "mathFunctions.h"

using namespace std;

bool operatorValid(char [], char &);
double numberEntry();

int main ()
	{
	double firstNumber;
	double secondNumber;
	char firstOperator = 0;
	char validOperators [] = "+-*/XxCc"; /*{'+', '-', '*', '/', 'X','x', 'C', 'c'};*/
	double result;

	while (true)
		{
		firstNumber = numberEntry();
		while (true)
			{
			cout << "Press C to clear and start new, X to turn off calculator: " << endl;
			cout << "Enter either +, -, *, or / to continue." << endl;
			cin >> firstOperator;
			while (!operatorValid(validOperators , firstOperator))
				{
				cout << "The operator entered is not a valid operator." << endl;
				cout << "Press C to clear and start new, X to turn off calculator: " << endl;
				cout << "Enter either +, -, *, or / to continue." << endl;
				cin >> firstOperator;
				}
			if (firstOperator == 'X' || firstOperator == 'x')
				{
				return (1);
				}
			if (firstOperator == 'C' || firstOperator == 'c')
				{
				cout << endl;
				break;
				}
			secondNumber = numberEntry();

			switch (firstOperator)
				{
				case '+':
					//result = firstNumber + secondNumber; 					
					result = addition (firstNumber , secondNumber) ;
					cout << "The result is: " << result << endl;
					firstNumber = result;
					break;
				case '-':
					//result = firstNumber - secondNumber;
					result = subtraction (firstNumber, secondNumber);
					cout << "The result is: " << result << endl;
					firstNumber = result;
					break;
				case '*':
					//result = firstNumber * secondNumber;
					result = multiplication (firstNumber, secondNumber);
					cout << "The result is: " << result << endl;
					firstNumber = result;
					break;
				case '/':
					if (secondNumber == 0)
						{
						cout << "Cannot divide by zero." << endl;
						break;
						}
			 		else 
						{
						//result = firstNumber / secondNumber;
						result = division (firstNumber, secondNumber);
						cout << "The result is: " << result << endl;
						firstNumber = result;
						break;
						}
					/*
				default:
					cout << "The operator entered is not a valid operator." << endl;
					break;
					*/
				}
			}
		}
	return 0;
	}

bool operatorValid(char _validOperators [], char &referenceOperator )
	{
	for (int i = 0; _validOperators[i] != '\0' ; i++)
		{
		if (referenceOperator == _validOperators[i])
			{
			return(true);
			}
		}
	return (false);
	}

double numberEntry()
	{
	double number;
	cout << "Enter number:" << endl;
	//cin >> number;
	number = readNumber();
	return (number);
	}


