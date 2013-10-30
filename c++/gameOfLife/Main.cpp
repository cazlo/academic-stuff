/*******************************************************************************
//	DESCRIPTION:	Lab 7: Martin Gardner Simulation of Life
//  AUTHOR:			Andrew Paettie
//	DATE:			September 2011
//*****************************************************************************/

#include <iostream>
#include <string.h>

using namespace std;

int main ()
	{
	const int	numCols	= 60;
	const int	numRows	= 60;
	bool	board	[numRows + 2] [numCols + 2];
	bool	nextBoard	[numRows + 2] [numCols + 2];

	long	col;
	long	row;
	long	numOccupied;
	long	numNeighbors = 0;

	char yesOrNo = 0;

	memset (board, false, (numRows + 2) * (numCols + 2) * sizeof (bool)); //Sterilization
	memset (nextBoard, false, (numRows + 2) * (numCols + 2) * sizeof (bool)); //Sterilization
	
	cout << "How many cells do you want occupied: ";
	cin >> numOccupied;
	for (int i = 0; i < numOccupied; i++)
		{
		cout << "Please enter coodinates in the following format: " << endl;
		cout << "Row followed by a space followed by Column" << endl;
		cin >> row >> col;
		if ((row < 1 || col < 1 || row > 60 || col > 60))
			{
			cout << "Out of bounds.  Please reenter" << endl;
			i--;
			}

		board [row] [col] = true;
		nextBoard [row] [col] = true;
		}
	
	//use a preset list of cords for easy debugging
	/*
		1 2 3 4 5
	1   * * * *
	2   *   * *
	3	*   *
	4	  *
	5   *

	1, 4 should be born
	1, 2 should die of overcrowding
	1, 3

	1,5 will die of lonieliness
	
	board [1][1] = true;
	board [1][2] = true;
	board [1][3] = true;
	board [1][4] = true;
	board [2][1] = true;
	board [2][3] = true;
	board [2][4] = true;
	board [3][1] = true;
	board [3][3] = true;
	board [4][2] = true;
	board [5][1] = true;

	nextBoard [1][1] = true;
	nextBoard [1][2] = true;
	nextBoard [1][3] = true;
	nextBoard [1][4] = true;
	nextBoard [2][1] = true;
	nextBoard [2][3] = true;
	nextBoard [2][4] = true;
	nextBoard [3][1] = true;
	nextBoard [3][3] = true;
	nextBoard [4][2] = true;
	nextBoard [5][1] = true;

	*/
		//display board
		for (row = 1; row <= numRows; row++)
			{
			for (col = 1; col <= numCols; col++)
				//cout << (board [row] [col] ? '*' : ' ');
				if (board [row] [col])//true or alive
					cout  << '*';
				else 
					cout << ' ';
			cout << endl;
			}
	
		/**************************************************************/
		do
		{
		for (row = 1; row <= numRows; row++)
			{
			for (col = 1; col <= numCols; col++)
				{
				/**************************/
				//count neighbors of this cell
				if (board [row -1][col-1])
					{ numNeighbors++; }
				if (board [row - 1][col])
					{ numNeighbors++; }
				if (board [row -1][col +1])
					{ numNeighbors++; }

				if (board [row][col -1])
					{ numNeighbors++; }
				if (board [row][col + 1])
					{ numNeighbors++; }

				if (board [row + 1][col -1])
					{ numNeighbors++; }
				if (board [row + 1][col])
					{ numNeighbors++; }
				if (board [row +1][col + 1])
					{ numNeighbors++; }
				/****************************/
				if (board [row][col] && numNeighbors >= 4)//cell is alive and has 4 neghbors
					{
					nextBoard [row][col] = false;//died of overcrowding
					cout << row << ", " << col << " died of overcrowding" << endl;
					}
				if (board [row][col] && numNeighbors <= 1)
					{
					nextBoard[row][col] = false; //died of lonlieness
					cout << row << ", " << col << " died of lonlieness" << endl ;
					}
				if (!board[row][col]  && numNeighbors == 3)//cell is dead with 3 neighbors
					{
					nextBoard[row][col] = true;//birth cell
					cout << row << ", " << col << " is born" << endl ;
					}
				numNeighbors = 0; //need to reset this
				}
			}
		memcpy (board, nextBoard, (numRows + 2) * (numCols +2) * sizeof(bool));
		//copy entire array^^^^^


		//clear screen 
		//system("CLS");

		cout << endl << endl << "================================================" << endl << endl;

		//display board
		for (row = 1; row <= numRows; row++)
			{
			for (col = 1; col <= numCols; col++)
				//cout << (board [row] [col] ? '*' : ' ');
				if (board [row] [col])//true or alive
					cout  << '*';
				else 
					cout << ' ';
			cout << endl;
			}

		cout << "Would you like to continue? (Y to continue, any key to exit)" << endl;
		cin >> yesOrNo;
		//cout << yesOrNo << endl;

		}
	while (yesOrNo == 'y' || yesOrNo == 'Y');

    return 0;
	
	}
