#include <conio.h>//reliance on this makes it a windows only function
#include <string.h>
#include <iostream>
#include <sstream>

#include "numberOnlyInput.h"

using namespace std;

double readNumber ()
	{
	char	c;
	bool	isNegative = false;
	long	wholePart = 0;
	long	numCharsEntered	= 0;

	//bool	pastDecimal = false;
	long	numPastDecimal = 0;
	long	fractionalPart = 0;
	long	numLeadingZeros = 0;
	double	completeNumber = 0;
	char	lastCharEntered;

	while ((c = _getch ()) != '\r')
		{
		switch (c)
			{
			case '0'://if it is zero right after the decimal, the second number will be wrong 
				if (numPastDecimal == 0)
					{
					wholePart = (wholePart * 10) + (c - '0');
					numCharsEntered++;
					lastCharEntered = c;
					break;
					}
				else 
					{
					//will only occur if this is beyond decimal
					if (numPastDecimal == 1 )
						{
						numLeadingZeros++;
						}
					else if (lastCharEntered == '0' && numLeadingZeros == (numPastDecimal - 1) )
						{
						numLeadingZeros++;
						}
					else
						{
						fractionalPart = (fractionalPart * 10) + (c - '0');
						}
					numPastDecimal ++;
					numCharsEntered ++;
					lastCharEntered = c;
					break;
					}
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				if (numPastDecimal < 1)
					{
					wholePart = (wholePart * 10) + (c - '0');
					numCharsEntered++;
					lastCharEntered = c;
					break;
					}
				else 
					{
					fractionalPart = (fractionalPart * 10) + (c - '0');
					numPastDecimal ++;
					numCharsEntered ++;
					lastCharEntered = c;
					break;
					}
			case '.':
				if (numPastDecimal == 0 )
					{
					numPastDecimal++;
					numCharsEntered++;
					break;
					}
				else
					{
					c = '\a';
					break;
					}
			case '\b':
				if (numCharsEntered > 0)
						{
						_putch ('\b');
						_putch (' ');
						if (numPastDecimal == 0)
							{
							wholePart = wholePart / 10;
							//long, so last number is truncated
							}
						else
							{
							numPastDecimal --;
							if (numPastDecimal == numLeadingZeros)
								{
								numLeadingZeros --;
								}
							fractionalPart = fractionalPart / 10;
							}
						numCharsEntered--;
						if (numCharsEntered == 0)
							{
							isNegative = false;
							}
						}
				else
					{	
					c = '\a';
					}
				break;
			case '-':
				if (numCharsEntered == 0)
					{
					isNegative = true;
					numCharsEntered++;
					}
				else
					{
					c = '\a';
					}
				break;
			case '+':
				if (numCharsEntered == 0)
					{
					numCharsEntered++;
					}
				else
					{
					c = '\a';
					}
				break;
			default:
				c = '\a';
			}
		_putch (c);
		}
	_putch ('\n');
	if (isNegative)
			wholePart = -wholePart;
	if (numPastDecimal == 0 )
		{
		return wholePart;
		}
	else //put them together
		{
		string completeNumberString;
		stringstream ss1;
		ss1 << wholePart << '.' ;
		if (numLeadingZeros > 0)
			{
			for (int i = 0; i < numLeadingZeros; i++)
				{
				ss1 << '0';
				}
			}
		ss1 << fractionalPart ;
		completeNumberString = ss1.str(); 
		//cout << completeNumberString  << endl;
		char * pEnd; //needed for strtod
		completeNumber = strtod (completeNumberString.c_str(), &pEnd);
		return completeNumber;
		}
	}
