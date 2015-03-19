#lang racket
(define (value-at l i)
         (if (>= i (length l))
             "index out of bounds"
             (recursiveValueAt l i 0)
        ))

(define (recursiveValueAt l i cI)
         (if (eq? cI i)
             (car l)
             (recursiveValueAt (cdr l) i (add1 cI))
        ))