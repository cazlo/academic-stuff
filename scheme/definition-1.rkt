#lang racket
(define (my-reverse listArg) 
          (if (empty? listArg)
              '()
              (append (my-reverse (cdr listArg)) (list (car listArg)))))