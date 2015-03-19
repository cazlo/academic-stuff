#lang racket
(define (my-sorted? l)
         (if (number? (car l))
            (numCmpr l)
            (stringCmpr l)
            ))

(define (numCmpr l)
         (if (<= (length l) 1)
            #t
            (and (<= (car l) (cadr l)) (numCmpr (cdr l)) )))

(define (stringCmpr l)
         (if (<= (length l) 1)
            #t
            (and (string<=? (car l) (cadr l))(stringCmpr (cdr l)) )))