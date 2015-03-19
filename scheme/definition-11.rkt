#lang racket
(define (rev-sort l)
         (if (<= (length l) 1)
             l
             (lazy-sort l)
        ))

(define (lazy-sort l)
         (if (empty? l)
             '()
             (cons (find-largest l) (lazy-sort(remove (find-largest l) l)))
        ))
(define (find-largest l)
         (if (empty? l)
             '()
             (find-largest-rec l -9999)
        ))

(define (find-largest-rec l currLargest)
         (if (empty? l)
             currLargest
             (if (>= (car l) currLargest)
                 (find-largest-rec (cdr l)(car l))
                 (find-largest-rec (cdr l) currLargest))))
  
