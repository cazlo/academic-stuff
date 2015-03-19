#lang racket
(define (zipper l1 l2) 
          (if (or (empty? l1)(empty? l2))
              '()
              (cons (list (car l1) (car l2)) (zipper (cdr l1) (cdr l2)))))