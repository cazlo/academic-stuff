Welcome to DrRacket, version 5.3.4 [3m].
Language: racket; memory limit: 128 MB.
> (threshold '(3 6.2 7 2 9 5.5 1) 6)
'(3 2 5.5 1)
> (threshold '(1 2 3 4 5) 4)
'(1 2 3)
> (threshold '(4 8 5 6 7) 6.1)
'(4 5 6)
> (threshold '(8 3 5 7) 2)
'()
> 