/*******************************************************************************
//	DESCRIPTION:	Lab 13: Almost a poker game.  Cards are dealt out to users 
//                  and the winning hand is discovered.  Just need to implement
//                  betting and AI it would be a decent poker game.  NOTE: the 
//                  current implementation relies on windows, as it prints ASCII
//                  codes which are control charaters on other systems.  Could 
//                  fix this by using unicode instead.
//  AUTHOR:			Andrew Paettie
//	DATE:			September 2011
//*****************************************************************************/

#include <iostream>

using namespace std;

#include "cards.h"

int main ()
	{
	card deck [52];
	const int numHands = 4;
	const int numCards = 5;
	card hands [numHands] [numCards];
	char *	handRankNames [9] = {	
								"Highest Card",
								"One Pair",
								"Two Pair",
								"Three of a Kind",
								"Straight",
								"Flush",
								"Full House",
								"Four of a Kind",
								"Straight Flush",
								};
	int rank [numHands];
	int currentHand = 0, currentCard = 0;
	
	//int i =0;

	shuffle (deck);
	deal (deck, hands);
	sortHands (hands, numHands, numCards);
	/*//testing
	hands [0][0].s = Hearts;
	hands [0][0].v = Two;
	hands [0][1].s = Clubs;
	hands [0][1].v = Four;
	hands [0][2].s = Spades;
	hands [0][2].v = Four;
	hands [0][3].s = Diamonds;
	hands [0][3].v = Six;
	hands [0][4].s = Hearts;
	hands [0][4].v = Six;

	hands [1][0].s = Clubs;
	hands [1][0].v = Two;
	hands [1][1].s = Hearts;
	hands [1][1].v = Four;
	hands [1][2].s = Diamonds;
	hands [1][2].v = Four;
	hands [1][3].s = Clubs;
	hands [1][3].v = Six;
	hands [1][4].s = Spades;
	hands [1][4].v = Six;//*/
	/*
	hands [2][0].s = Hearts;
	hands [2][0].v = Ace;
	hands [2][1].s = Hearts;
	hands [2][1].v = Four;
	hands [2][2].s = Hearts;
	hands [2][2].v = Four;
	hands [2][3].s = Hearts;
	hands [2][3].v = Four;
	hands [2][4].s = Hearts;
	hands [2][4].v = Four;//*/
	for (currentHand = 0; currentHand < numHands; currentHand ++)
		{
		cout << "Hand " << currentHand + 1 << ":" << endl; 
		for (currentCard = 0; currentCard < numCards; currentCard++)
			{
			display (hands[currentHand][currentCard]);
			}
		rank [currentHand] = whatRank (hands [currentHand], numCards);
		cout << handRankNames[ rank[currentHand] ] << endl << endl;
		}
	displayWinner(rank, hands);
	return 0;
	}
