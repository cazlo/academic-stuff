Welcome to DrRacket, version 5.3.4 [3m].
Language: racket; memory limit: 128 MB.
> (in-list? 6 '(4 8 6 2 1))
#t
> (in-list? 7 '(4 8 6 2 1))
#f
> (in-list "foo" '(4 5 #f "foo" a))
. . in-list: arity mismatch;
 the expected number of arguments does not match the given number
  expected: 1
  given: 2
  arguments...:
   "foo"
   '(4 5 #f "foo" a)
> (in-list? "foo" '(4 5 #f "foo" a))
#t
> 