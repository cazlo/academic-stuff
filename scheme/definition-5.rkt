#lang racket
(define (segregate alist) 
        (list (getEvens alist)(getOdds alist)))

(define (getEvens alist)
          (if (empty? alist)
              '()
              (if (even? (car alist))
                  (cons (car alist) (getEvens (cdr alist)))
                  (getEvens (cdr alist)))))

(define (getOdds alist)
          (if (empty? alist)
              '()
              (if (odd? (car alist))
                  (cons (car alist) (getOdds (cdr alist)))
                  (getOdds (cdr alist))))) 