Welcome to DrRacket, version 5.3.4 [3m].
Language: racket; memory limit: 128 MB.
> (segregate '(7 2 3 5 8))
'((2 8) (7 3 5))
> (segregate '(3 -5 8 16 99))
'((8 16) (3 -5 99))
> (segregate '())
'(() ())
> 