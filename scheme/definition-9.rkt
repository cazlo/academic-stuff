#lang racket
(define (threshold l t)
         (if (empty? l)
             '()
             (if (< (car l) t)
                 (cons (car l)(threshold (cdr l) t))
                 (threshold (cdr l) t))    
        ))