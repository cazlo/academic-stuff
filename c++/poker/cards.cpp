#include <iostream>
#include <time.h>
#include <cstdlib>
#include <string.h>

using namespace std;

#include "cards.h"

char *	valueNames [13] = {	"Two",
							"Three",
							"Four",
							"Five",
							"Six",
							"Seven",
							"Eight",
							"Nine",
							"Ten",
							"Jack",
							"Queen",
							"King",
							"Ace" };

void display (card & c)
	{
	cout << valueNames [c.v] << " of " << (char) c.s << endl;
	}

void shuffle(card deck[])
	{
	bool picked [52];
	long cardIndex;
	srand (time (0));

	memset (picked , false, 52 * sizeof (bool));
	for (int i = 0; i < 52; i++)
		{
		while (picked [cardIndex = rand () % 52]);
		deck [i].s = (suit) ((cardIndex / 13) + Hearts);
		deck [i].v = (value) (cardIndex % 13);
		picked [cardIndex] = true;
		}
	}

void deal(const card deck[], card hands [] [5])
	{
	int whichCard;
	int whichHand;
	int cardFromDeck = 0;

	for (whichCard = 0; whichCard < 5; whichCard ++)
		{
		for (whichHand = 0; whichHand < 4; whichHand++)
			{
			hands [whichHand] [whichCard] = deck [cardFromDeck++];
			}
		}
	}

void sortHands(card hands [][5], const int numHands,const int numCards)
//pass 2 extra paramaters just to keep this function portable to texas holdem style and more than 4 players
	{
	card tempCardHolder;
	int tempNumCards = numCards;
	bool sorted;
	int currentHand;
	int currentCard;
		for (currentHand = 0; currentHand < numHands; currentHand++)
			{
			do
				{
				tempNumCards --;
				sorted = true;
				for (currentCard = 0; currentCard < tempNumCards; currentCard++)
					{
					//sort by value
					if (hands [currentHand][currentCard].v > hands [currentHand][currentCard + 1].v)
						{
						tempCardHolder = hands [currentHand][currentCard];
						hands [currentHand][currentCard] = hands [currentHand][currentCard + 1];
						hands [currentHand][currentCard + 1] = tempCardHolder;
						sorted = false;
						}
					/*//sort by suits 
					not actually needed in standard poker
					else if ((hands [currentHand][currentCard].v == hands [currentHand][currentCard + 1].v) &&
						     (hands [currentHand][currentCard].s >  hands [currentHand][currentCard + 1].s))
						{
						tempCardHolder = hands [currentHand][currentCard];
						hands [currentHand][currentCard] = hands [currentHand][currentCard + 1];
						hands [currentHand][currentCard + 1] = tempCardHolder;
						sorted = false;
						}*/
					}
				}while (!sorted);
			tempNumCards = numCards;
			}
	}

int whatRank(card hand [], const int numCards)
	{
	/*
	enum rank {	
				HighestCard,		//0
				OnePair,			//1
				TwoPair,			//2
				ThreeofaKind,		//3
				Straight,			//4
				Flush,				//5
				FullHouse,			//6
				FourOfaKind,		//7
				StraightFlush,		//8
				};
			*/
	int i, j;
	int numMatches;
	int totalNumMatches = 1;
	int numPairs = 0;
	
	for (i = 0; i < numCards; i++)
		{
		numMatches = 0;
		for (j = i ; j < numCards; j++)
			{
			if (hand [i].v == hand [j].v)
				{
				numMatches ++;
				}
			}
		if(numMatches > totalNumMatches)
			{
			totalNumMatches = numMatches;
			}
		}
	switch (totalNumMatches)
		{
		case 1:
			//can be straight , straight flush , flush, or high card
			if ((hand [0].s == hand [1].s) &&
				(hand [0].s == hand [2].s) &&
				(hand [0].s == hand [3].s) &&
				(hand [0].s == hand [4].s))
				{//it is a flush
				if ((hand [1].v == (hand [0].v + 1)) &&
					(hand [2].v == (hand [1].v + 1)) &&
					(hand [3].v == (hand [2].v + 1)) &&
					(hand [4].v == (hand [3].v + 1)))
					{//straight flush
					return StraightFlush;
					break;
					}
				else
					{//just a flush
					return Flush;
					break;
					}
				}
			else if ((hand [1].v == (hand [0].v + 1)) &&
					 (hand [2].v == (hand [1].v + 1)) &&
					 (hand [3].v == (hand [2].v + 1)) &&
					 (hand [4].v == (hand [3].v + 1)))
				{//it is a straight
				return Straight;
				break;
				}
			else if ((hand [4].v == Ace)   &&
					 (hand [0].v == Two)   &&
					 (hand [1].v == Three) &&
					 (hand [2].v == Four)  &&
					 (hand [3].v == Five))
				{//ace is low for this special straight
				return Straight;
				break;
				}
			else
				{//it is a high card
				return HighestCard;
				break;
				}
		case 2:
			//can be 1 or 2 pairs
			for (i = 0; i < numCards - 1; i++)
				{
				if (hand [i].v == hand [i+1].v)
					{
					numPairs ++;
					}
				}
				
			if (numPairs == 1)
				{
				return OnePair;
				break;
				}
			else 
				{
				return TwoPair;
				break;
				}
		case 3:
			//can be three of a kind or full house
			if ( ((hand [0].v == hand [1].v) && (hand [0].v == hand [2].v)) &&
				 (hand [3].v ==  hand [4].v)
			   )
				{
				return FullHouse;
				break;
				}
			else if (	((hand [2].v == hand [3].v) && (hand [2].v == hand [4].v)) &&
						 (hand [0].v == hand [1].v))
				{
				return FullHouse;
				break;
				}
			
			else
				{
				return ThreeofaKind;
				break;
				}
		case 4:
			//four of a kind
			return FourOfaKind;
			break;
		default://will never actually be called, but put here to avoid warning from compiler
			return 0;
			break;
		}
	}

void displayWinner (int rank [], card hands [][5])
	{
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
	int i=0, j =0;
	int currentHand = 0;
	int highestSoFarIndex = -1;
	bool tie = false;
	int numTies = 0; //possible to have up to 4 way tie so need counter
	const int numHands = 4; /*when passed as a paramater of type const int, the compiler complained about the below
							array, saying that numHands wasnt const.*/
	int indexOfTies[numHands];//array with the indexes of those hands which are tied

	for (currentHand = 0; currentHand < numHands; currentHand++ )
		{
		
		if (rank [currentHand] > rank [highestSoFarIndex])
			{
			highestSoFarIndex = currentHand;
			tie = false;
			numTies = 0;
			}
		else if (rank [currentHand] == rank [highestSoFarIndex])
			{
			switch (rank [currentHand])
				{
				case HighestCard:		//0
				case OnePair:			//1
				case Flush:				//5
					for (j = 4; j >= 0; j--)
						{
						if (hands [currentHand][j].v > hands [highestSoFarIndex][j].v)
							{
							highestSoFarIndex = currentHand;
							tie = false;
							numTies = 0;
							break;
							}
						else if (hands [currentHand][j].v < hands [highestSoFarIndex][j].v)
							{//highestSoFarIndex is still supreme so do nothing
							break;
							}
						else if ((hands [currentHand][j].v == hands [highestSoFarIndex][j].v) && (j == 0)) 
							{//the hands are the same all the way to the lowest card
							tie = true;
							numTies ++;
							if (i == 0)
								{
								indexOfTies [i] =  highestSoFarIndex;
								i++;
								}
							indexOfTies [i] =  currentHand ;
							i++;
							}
						}
					break;
				case Straight:			//4
				case StraightFlush:		//8
					//just need to compare highest value in straights
					if (hands [currentHand][4].v > hands [highestSoFarIndex][4].v)
						{
						highestSoFarIndex = currentHand;
						tie = false;
						numTies = 0;
						}
					else if (hands [currentHand][4].v == hands [highestSoFarIndex][4].v)
						{
						tie = true;
						numTies ++;
						if (i == 0)
							{
							indexOfTies [i] =  highestSoFarIndex;
							i++;
							}
						indexOfTies [i] =  currentHand ;
						i++;
						}
					break;					
				case TwoPair:			//2
					//the highest pair will always have a card in the 4th slot(index 3)
					if (hands [currentHand][3].v > hands [highestSoFarIndex][3].v)
						{
						highestSoFarIndex = currentHand;
						tie = false;
						numTies = 0;
						break;
						}
					else if (hands [currentHand][3].v == hands [highestSoFarIndex][3].v)
						{//same card on high pair
						if (hands [currentHand][1].v > hands [highestSoFarIndex][1].v)
							{//the lower pair will always have a card in the 2nd slot(index 1)
							highestSoFarIndex = currentHand;
							tie = false;
							}
						else if (hands [currentHand][1].v == hands [highestSoFarIndex][1].v)//same card on low pair
							{//arrays are identical except perhaps for 1 card that can be in index 0,2, or 4
							for (j = 4; j >= 0; j-=2)
								{
								if (hands [currentHand][j].v > hands [highestSoFarIndex][j].v)
									{
									highestSoFarIndex = currentHand;
									tie = false;
									numTies = 0;
									break;
									}
								else if (hands [currentHand][j].v < hands [highestSoFarIndex][j].v)
									{
									break;
									}
								else if ((hands [currentHand][j].v == hands [highestSoFarIndex][j].v) && (j == 0)) //the hands are the same all the way to the lowest card
									{
									tie = true;
									numTies ++;
									if (i == 0)
										{
										indexOfTies [i] =  highestSoFarIndex;
										i++;
										}
									indexOfTies [i] =  currentHand ;
									i++;
									}
								}
							}
						}
					break;
				case ThreeofaKind:		//3
				case FullHouse:			//6 
				case FourOfaKind:		//7
					//the three (or four) same cards will always have a card in slot 3 (index 2)
					if (hands [currentHand][2].v > hands [highestSoFarIndex][2].v)
						{
						highestSoFarIndex = currentHand;
						tie = false;
						numTies = 0;
						}
					//there can never be a tie with a three of a kind, four of a kind, or full house as there is only 4 cards of any value
					break;
				}
			}
		}

	cout << "\nResult:\n";
	if (tie)
		{
		cout << numTies + 1 << " way tie between hands ";
		for (i = 0; i <= numTies; i++ )
			{

			cout << (indexOfTies [i ]) + 1 << ", ";
			
			}
		cout << "with " << numTies + 1 << " "<< handRankNames [rank[highestSoFarIndex]] << "s" << endl;
		}
	else
		{
		cout << "Hand " << highestSoFarIndex + 1 << " wins with a " << handRankNames [rank[highestSoFarIndex]] << endl;
		}
	}
