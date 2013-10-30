/*******************************************************************************
//	DESCRIPTION:	Lab 9: Basic craps game.  Reliance on conio.h makes this 
//                  a windows only program.
//  AUTHOR:			Andrew Paettie
//	DATE:			October 2011
//*****************************************************************************/

#include <iostream>
#include <stdlib.h>
#include <time.h>
#include "numberOnlyInput.h"

using namespace std;

int rollDice(int);

int main ()
	{
	double userMoney = 50.00;
	double bet ;
	int diceSize = 6;
	int firstDieValue;
	int secondDieValue;
	int totalDieValue;
	int point = 0;
	char exitChar;
	bool continueGambling = false;
	srand ( (unsigned)time(0) ); //seed for rand

	do
		{
		do
			{
			cout << "You have $" << userMoney << " available to wager." << endl;
			cout << "Enter your bet: " << endl;
			bet = readNumber();
			if (bet > userMoney)
				{
				cout << "Not enough money for that bet" << endl;
				}
			}
		while ((bet > userMoney));

		firstDieValue = rollDice( diceSize);
		secondDieValue = rollDice(diceSize);
		totalDieValue = firstDieValue + secondDieValue;
		cout << "You rolled a "<< firstDieValue << " and a " << secondDieValue << endl;
		if (totalDieValue == 7 || totalDieValue == 11)
			{
			//instant winner
			cout << "You win $" << bet << endl;
			userMoney += bet;
			}
		else if (totalDieValue == 2 || totalDieValue == 3 || totalDieValue == 12)
			{
			// instant loser
			cout << "You lose $" << bet << endl;
			userMoney -= bet;
			}
		else
			{
			point = totalDieValue;
			do
				{
				cout << "Roll again" << endl;
				firstDieValue = rollDice( diceSize);
				secondDieValue = rollDice(diceSize);
				totalDieValue = firstDieValue + secondDieValue;
				cout << "You rolled a "<< firstDieValue << " and a " << secondDieValue << endl;
				if (totalDieValue == point)
					{
					//instant winner
					cout << "You win $" << bet << endl;
					userMoney += bet;
					break;
					}
				else if (totalDieValue == 7)
					{
					// instant loser
					cout << "You lose $" << bet << endl;
					userMoney -= bet;
					break;
					}
				}
			while (totalDieValue != 7 || totalDieValue !=point);
			}
		if (userMoney > 0)
			{	
			cout << "Press X to exit or any other key to continue gambling" << endl;
			cin >> exitChar;
			if (exitChar == 'x' || exitChar == 'X')
				{
				continueGambling = false;
				}
			else
				{
				continueGambling = true;
				}
			}
		else
			{
			cout << "You have run out of money" << endl;
			continueGambling = false;
			}
		}
		while (continueGambling);
	return 0;	
	}

int rollDice(int diceSize)
	{
	int randomNumber;
	/*Needs to be called only once
	srand ( (unsigned)time(0) ); //seed for rand */
	randomNumber = (rand () % diceSize) + 1;
	return randomNumber;
	}
