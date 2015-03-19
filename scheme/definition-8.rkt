#lang racket
(define (flatten l)
          (if (empty? l)
              '()
              (if (list? (car l))
                  (append (flatten (car l)) (flatten (cdr l)))
                  (cons (car l) (flatten (cdr l)))
              )
         ))
