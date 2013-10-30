#ifndef CARDS_H
#define CARDS_H

enum	suit {Hearts = 3, Diamonds, Clubs, Spades};
enum	value	{
				Two, 
				Three, 
				Four, 
				Five, 
				Six, 
				Seven, 
				Eight, 
				Nine, 
				Ten, 
				Jack, 
				Queen, 
				King, 
				Ace
				};
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
struct card
	{
	suit	s;
	value	v;
	};

void display (card &);

void shuffle (card []);
	
void deal(const card[], card [][5]);

void sortHands(card[][5],int, int);

int whatRank(card [], int);

void displayWinner (int [], card [][5]);

#endif
