/*******************************************************************************
* AUTHOR: Andrew Paettie (ajp073000)
* CLASS: Organization of Programming Languages CS 4337.002
* ASSIGNMENT: Homework 4
* DUE: 4-23-14
*******************************************************************************/

%%1. even if divisible by 2 %%
even(X) :- X mod 2 =:= 0.

%%2. factorial(x, y). y = factorial(x) %%
fact(0,1).
fact(X,Y) :-
     X > 0,
     X1 is X - 1,
     fact(X1, N),
     Y is N * X.

%%3. is_Prime(X).  %%
is_Prime(1).
is_Prime(2).
is_Prime(X) :-
	\+ divisible(X, 2).%really only need to test to the sqrt of x, but lazy:)
%checks to see if it is divisible by anything starting with 2 and going up by 1
divisible(X, Y) :-
	Y < X,
	X mod Y =:=0.%stop if it is divisible, go to other clause if not
divisible(X, Y) :-
	Y < X,
	Y1 is Y + 1,
	divisible(X, Y1).

%%4.  segregate list into evens and odds(2 lists)		%%
segregate(ALIST, EVENS, ODDS) :-
	findEvens(ALIST, EVENS),
	findOdds(ALIST, ODDS).

findEvens([],[]).%empty list is empty
findEvens([H | T], [H | EVENS]) :-
	even(H),
	findEvens(T, EVENS).
findEvens([_ | T], EVENS) :-
	findEvens(T, EVENS).

findOdds([],[]).
findOdds([H | T], [H | ODDS]) :-
	not(even(H)),
	findOdds(T, ODDS).
findOdds([_ | T], ODDS) :-
	findOdds(T, ODDS).

%%5.  sum_list(somelist, sum) , sums members of a list	%%
sum_list([],0).
sum_list([H | T], Sum) :-
	sum_list(T, Tail),
	Sum is H + Tail.
	
%%6.  bookends - takes 3 lists as input, tests if 1st list is prefix of third
%%					tests if 2nd list is suffix of third
%%					prefix and suffix can overlap
bookends([],[],_).
bookends(Pre, Suf, List) :-
	checkPre(Pre, List),
	reverse(List, RList),%reverse the list
	reverse(Suf, RSuf),%reverse the suffix
			  %reversed suffix = prefix
	checkPre(RSuf, RList).
	
checkPre([],_).
checkPre([PH|PT], [LH|LT]) :-
	PH = LH,
	checkPre(PT, LT).

reverse([],[]).%empty list is reversed
%reverse([X],[X]).%1 element list is reversed
reverse([H | T], Result) :-
	reverse(T, Reversed) ,
	append(Reversed, [H], Result).
	
/*7.
subslice(slice, list) - checks to see if the first list is a continuous series 
in the second list

achieved by first comparing the head of the slice list with each element in the 
other list until there is a match found.  

once the first match is found, check the rest (until atleast one list runs out),
and fail if there is a non-matching element 
*/
subslice([],[]).
subslice(Slice, List) :-
	findFirstCommon(Slice, List).


findFirstCommon([], _).
findFirstCommon([H | T], [LH | LT]) :-
	H = LH,
	%found first in common
	checkRest( T,  LT).
findFirstCommon([H | T], [_ | LT]) :-
	%not(H = LH),
	findFirstCommon([H | T], LT).
	
checkRest([],_).%ran out of slice pieces to test successfully
checkRest([X],[X]).%both lists are down to the same single element
checkRest([H | T],[LH, LT]) :-
	H = LH,
	checkRest(T, [LT | []]).%Sometimes LT gets pulled out to an atom
				%WHY IS THIS????
				%SERIOSULY IF YOU CAN EXPLAIN IN THE FEEDBACK
				%I WOULD NOMINATE YOU FOR TA OF THE YEAR
	
/*8.
graph
directed edges a -> b will be defined with rules like:
edge(a,b).

path(x,y) - returns true if there is a path from x to y
cycle(x) - returns true if there is a cycle including x
*/
%knowledge base
edge(a,b).
edge(b,c).
edge(c,d).
edge(d,e).
edge(d,a).

path(Start, Stop) :-
	rPath(Start, Stop, [Start] ).
rPath(S, S, _).%Test = Stop is base case
rPath(Start, Stop, TestPath) :-
	not(Start = Stop),%to stop cycles
	edge(Start, Test),%will check all branches that are connected to start
	not(member(Test, [TestPath])),%have we alread visited that noded?
	rPath(Test, Stop, [Test | TestPath]).%if not, then visit it recursively
	
cycle(Start):- 
	cycle(Start, []).
cycle(Test, Visited):-
	member(Test, Visited),
	!.
cycle(Test, Visited):-
	edge(Test, Next),
	cycle(Next, [Test | Visited]).
	
	
/*9.
Clue
NOTE: when running suspect (ie suspect(Killer, mrBobby).) make sure to hit space
after the first result, and not enter, or the second result will not be displayed
(type command: suspect(Killer, mrBobby").<hit enter>
  Killer = profPlum<hit space>
  Killer = colMustard.)
*/
is_married(profPlum, msGreen).
married(X, Y) :- 
	is_married(Y,X).
married(X, Y) :-
	is_married(X,Y).
	
in_affair(mrBobby, msGreen).
in_affair(mrBobby, missScarlet).
havingAffair(X, Y) :-
	in_affair(X,Y).
havingAffair(X, Y) :-
	in_affair(Y,X).
	
greedy(colMustard).

rich(mrBobby).
%rich(colMustard).%%%%%%%%%%%%%%%%%  UNCOMMENT THIS LINE FOR PART C  %%%%%%%%%%%%%%%%%%%%%%%%%

hateMotive(Dead, Killer) :-
	havingAffair(Dead, Slut),
	married(Slut, Spouce),
	Killer = Spouce.
	
greedMotive(Dead, Killer) :-
	greedy(Suspect),
	not(rich(Suspect)),
	rich(Dead),
	Killer = Suspect.

suspect(Killer, Dead):-
	hateMotive(Dead, Killer) | %%%%%%
	greedMotive(Dead, Killer).
