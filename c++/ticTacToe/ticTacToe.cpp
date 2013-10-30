/**************************************************************
Author: Andrew Paettie
Created and Tested Using Microsoft Visual C++
Program Description:a program that allows two players to play 
the game of tic tac toe
***************************************************************/

#include <iostream>
#include <string>
#include <iomanip>
#include <cstdlib>

using namespace std;

//make rowSize and colSize global constants to make expanding
//the board size easier in the future

const int rowSize = 3, colSize = 3;

//  make a const char array which contains the marks used by
// different players

const char playerMarks[] = {'X','O'};

void clearBoard(char [rowSize][colSize]);
void displayBoard(char [rowSize][colSize]);
void getUserMove(int&, int&,unsigned int);

bool isValidInput(string, const int);
bool isWinner(char [rowSize][colSize]);
bool playagain();


int main()
    {
    unsigned int moveNumber = 0;
    char gameBoard [rowSize][colSize];
    int currentPlayerRow = 0, currentPlayerCol = 0;
    bool gameOver = false;
    bool validMove = false;

    do    //setup initial do-while loop so that later we can ask user if they want to play again
        {
        moveNumber = 0;
        currentPlayerRow = 0, currentPlayerCol = 0;
        gameOver = false;
        clearBoard (gameBoard);
        validMove = false;
        while (!gameOver)
            {
            displayBoard(gameBoard);
            moveNumber++;
            do
                {
                getUserMove(currentPlayerRow, currentPlayerCol, moveNumber);
                if (gameBoard [currentPlayerRow][currentPlayerCol]!= ' ')
                    {
                    cout << "Cannot place your move into an occupied space" << endl;
                    validMove = false;
                    }
                else 
                    {
                    gameBoard [currentPlayerRow][currentPlayerCol] = playerMarks[(moveNumber -1) % 2];
                    validMove = true;
                    }
                }while ( !validMove );

            //displayBoard(gameBoard);
            //gameOver = isGameOver(gameBoard);

            if (isWinner(gameBoard))
                {

                displayBoard(gameBoard); //redraw board with the winning mark displayed

                cout << "Player " << ((moveNumber-1) %2)+1 << " ("<<playerMarks[((moveNumber-1)%2)]<< ") wins!"<< endl;
                gameOver = true;
                }
            else if (moveNumber == (rowSize * colSize) )
                {

                //there is a tie

                displayBoard(gameBoard);
                cout << "There is a tie" << endl;
                gameOver = true;
                }
            else
                {
                gameOver = false;
                }
            }
        }while( playagain() );
    }

/*Function which takes in a char array whose row and col sizes are 
defined as a global constants.  It takes each element of the array
and puts a space in it, essentially clearing it.  Returns nothing.
*/

void clearBoard(char gameBoard [rowSize][colSize])
    {
    for (int currentRow = 0; currentRow < rowSize; currentRow++) 
        for (int currentCol = 0; currentCol < colSize; currentCol ++)
            {
            gameBoard[currentRow][currentCol] = ' ';
            }
    }

/*Function which takes in an array which represents the current
state of the board, then prints it out.  returns nothing
currently only works for a board of size 3 x 3
*/

void displayBoard(char gameBoard[rowSize][colSize])
    {
    cout <<endl<<endl<<  right   <<   "\t" <<   "\t" <<
        "      COL   COL   COL" << endl  << "\t" <<   "\t" << 
        "       1     2     3 " << endl  << "\t" <<   "\t" << 
        "          |     |    " << endl <<   "\t" <<   "\t" << 
        "ROW 1  "<< gameBoard[0][0]<< "  |  "<< gameBoard [0][1]<<"  |  "<<gameBoard[0][2]<<"  " << endl << "\t" <<    "\t" <<
        "          |     |    " << endl <<    "\t" <<   "\t" << 
        "      ---------------" << endl <<     "\t" <<   "\t" << 
        "          |     |    " << endl <<     "\t" <<   "\t" << 
        "ROW 2  "<< gameBoard[1][0]<< "  |  "<< gameBoard [1][1]<<"  |  "<<gameBoard[1][2]<<"  " << endl <<   "\t" <<   "\t" << 
        "          |     |    " << endl <<      "\t" <<    "\t" <<
        "      ---------------" << endl <<   "\t" <<    "\t" <<
        "          |     |    " << endl <<    "\t" <<    "\t" <<
        "ROW 3  "<< gameBoard[2][0]<< "  |  "<< gameBoard [2][1]<<"  |  "<<gameBoard[2][2]<<"  " << endl << "\t" <<    "\t" <<
        "          |     |    " << endl << endl <<endl;
    }

/*function which prompts the user for a row and column, and 
loops until isValidInput() returns true for both the row 
and column.  takes in 2 variables by reference representing the 
row and column, and an unsigned int which represents the current 
move number, which is needed to know which player and mark is
being prompted.  returns nothing.
*/

void getUserMove(int & playerRow, int & playerCol,unsigned int moveNumber)
    {
    string userInput;

    //int playerCol, playerRow;

    cout << "Player " << ((moveNumber -1) %2)+1 << 
        " enter the row and column you want to mark with your " <<
        playerMarks[((moveNumber -1) % 2)] << endl;
    do
        {
        cout << "ROW #: ";
        getline (cin, userInput);
        }while (!isValidInput(userInput, rowSize));

    //userInput will now be safe to convert into a number

    playerRow = atoi(userInput.c_str()) - 1;

    userInput = "";
    do
        {
        cout << "Col #: ";
        getline (cin, userInput);
        }while (!isValidInput(userInput, colSize));
    playerCol = atoi(userInput.c_str()) - 1;
    //userInput = "";
    }

/*Function which determines validity of a string passed to the 
function, in regards to the string converting to an integer, and
that integer being between 1 and a constant integer passed as 
the second parameter
*/

bool isValidInput(string userInput,const int maxNum)
    {
    if (!(atoi(userInput.c_str()))) //not a number, or has non-numbers in it, or 0 which is still invalid
        {
        cout << "Valid entry is integers between 1 and " << maxNum << ". "
            << "Please re enter ";
        return false;
        }
    else if ( (atoi(userInput.c_str() ) < 1) ||
            (atoi(userInput.c_str() ) > maxNum) )   //number, but not in range
        {
        cout << "Valid entry is integers between 1 and " << maxNum << ". "
            << "Please re enter ";
        return false;
        }
    else //number in range
        {
        return true;
        }
    }

/*Function which checks if there is a horizontal, vertical, or diagonal
winner.  Takes in a char array representing the current state of the game 
board.  returns nothing.
NOTE:code to check for diagonal will only work for boards with odd sizes in
which both the row size and col size are the same.
*/

bool isWinner(char gameBoard [rowSize][colSize])
    {
    int numSameChars = 0, currentRow = 0, currentCol = 0;
    char previousChar;

    //*******check for horizontal winner***********
     /*for some reason the following code was giving me eraneous win for o with the followong board
     x | x | 
     ----------
     o | o | x
     ----------
     o | o | x
     TODO: FIX THIS
  
    for (currentRow = 0, numSameChars = 0; currentRow < rowSize; currentRow++)
        {
        previousChar = gameBoard [currentRow][0];

        if (previousChar != ' ')   //only need to do next loop if the space is not empty
            {
            for (currentCol = 1; currentCol < colSize; currentCol++)
                {
                if ((gameBoard [currentRow][currentCol] == previousChar) 
                    && (gameBoard [currentRow][currentCol] != ' ') )
                    {
                    numSameChars++;
                    }
                previousChar = gameBoard [currentRow][currentCol];
                }
            }
        }  

    if ((numSameChars) == colSize)
        {

        cout <<endl << "horizontal win"  <<endl;

        return true;
        }     */

    for (currentRow = 0; currentRow < rowSize; currentRow++)
        {
        if (gameBoard [currentRow][0] != ' ')
            {
            if ( (gameBoard [currentRow][0] == gameBoard [currentRow][1])
                && (gameBoard [currentRow][1]  == gameBoard [currentRow][2]) )
                {
                cout <<endl << "horizontal win"  <<endl;
                return true;
                }
            }
        }

    //***********check for vertical winner***********
    /* the following should be more easily expandable for larger board sizes, but
        has some issues with the same board as above.
    for (currentCol = 0,numSameChars = 0; currentCol < colSize; currentCol++)
        {
        previousChar = gameBoard [0][currentCol];
        if (previousChar != ' ')
            {
            for (currentRow = 1; currentRow < rowSize; currentRow++)
                {
                if (gameBoard [currentRow][currentCol] == previousChar
                    && (gameBoard [currentRow][currentCol] != ' '))
                    {
                    numSameChars++;
                    }
                previousChar = gameBoard [currentRow][currentCol];
                }
            }
        }

    if ((numSameChars ) == colSize)
        {

        cout <<endl << "vertical win"  <<endl;

        return true;
        }*/

    for (currentCol = 0; currentCol < colSize; currentCol++)
        {
        if (gameBoard [0][currentCol] != ' ')
            {
            if ( (gameBoard [0][currentCol] == gameBoard [1][currentCol])
                && (gameBoard [1][currentCol]  == gameBoard [2][currentCol]) )
                {
                cout <<endl << "vertical win"  <<endl;
                return true;
                }
            }
        }

    //***********check for diagonal winner*************
    //*****start in top left corner and move down******

    numSameChars = 0;
    previousChar = gameBoard[0][0];
    if (previousChar != ' ')
        {
        for (currentCol = 1, currentRow = 1; (currentCol < colSize) && (currentRow < rowSize); currentRow++, currentCol++)
            {
            if (gameBoard [currentRow][currentCol] == previousChar
                && (gameBoard [currentRow][currentCol] != ' '))
                {
                numSameChars++;
                }
            previousChar = gameBoard [currentRow][currentCol];
            }
        }
    if ((numSameChars+1) == rowSize)
        {

        cout <<endl << "diagonal (top left to bottom right) win"  <<endl;

        return true;
        }

    //******start in top right corner and move down******

    numSameChars = 0;
    previousChar = gameBoard[0][colSize-1];
    if (previousChar != ' ')
        {
        for (currentRow = 1, currentCol = colSize -2; (currentCol >= 0) && (currentRow < rowSize); currentRow++, currentCol--)
            {
            if (gameBoard [currentRow][currentCol] == previousChar
                && (gameBoard [currentRow][currentCol] != ' '))
                        {
                        numSameChars++;
                        }
                    previousChar = gameBoard [currentRow][currentCol];
            }
        }
    if ((numSameChars+1) == rowSize)
        {

        cout <<endl << "diagonal (top right to bottom left) win"  <<endl;

        return true;
        }

    else //have now checked all possible ways to win, so no winner yet
        {
        return false;
        }
    }

/*Function which asks the user if they want to play again, then returns true
if the user enters y or Y and false if the user enters N or n.  If they dont
enter y or n, then it will explain what y and n means.  Accepts no parameters 
*/

bool playagain()
    {
    bool validInput = false;

    cout << "Would you like to play again? (y/n)";
    while ( !validInput )
        {
        switch ( cin.get() )
            {
            case ('Y'):
            case ('y'):

                validInput = true;//not really needed, but I still want it there

                while (cin.get() != '\n')
                    {

                    //skip past the \n in the buffer

                    }
                return true;
            case ('n'):
            case ('N'):
                validInput = true;
                return false;
            default:
                cout << "Enter y or Y to play again.  Enter n or N to exit." << endl;
                while (cin.get() != '\n')
                    {

                    //skip past the \n in the buffer

                    }
                validInput = false;
            }
        }
    }
