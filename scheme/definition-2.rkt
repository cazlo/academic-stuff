#lang racket
(define (my-map functionName listArg) 
          (if (empty? listArg)
              '()
              (cons (functionName (car listArg)) (my-map functionName (cdr listArg)))))