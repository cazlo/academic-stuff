#lang racket
(define (in-list? e l)
          (if (empty? l)
              #f
              (if (equal? e (car l))
                   #t
                   (in-list? e (cdr l)))))