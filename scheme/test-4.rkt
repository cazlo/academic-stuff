Welcome to DrRacket, version 5.3.4 [3m].
Language: racket; memory limit: 128 MB.
> (zipper '(1 2 3 4) '(a b c d))
'((1 a) (2 b) (3 c) (4 d))
> (zipper '(1 2 3) '(4 9 5 7))
'((1 4) (2 9) (3 5))
> (zipper '(3 5 6) '("one" 6.18 #t "two"))
'((3 "one") (5 6.18) (6 #t))
> (zipper '(5) '())
'()
> 