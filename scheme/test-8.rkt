Welcome to DrRacket, version 5.3.4 [3m].
Language: racket; memory limit: 128 MB.
> (flatten '((1 2) 3)
           )
'(1 2 3)
> (flatten '((((4 3) 6)((7 2 9)(5 1)))))
'(4 3 6 7 2 9 5 1)
> (flatten '(1))
'(1)
> (flatten '())
'()
> 