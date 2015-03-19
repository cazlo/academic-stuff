#lang racket

;-----------------1) my-reverse---------------
(define (my-reverse listArg) 
          (if (empty? listArg)
              '()
              (append (my-reverse (cdr listArg)) (list (car listArg)))))

;-----------------2) my-map---------------
(define (my-map functionName listArg) 
          (if (empty? listArg)
              '()
              (cons (functionName (car listArg)) (my-map functionName (cdr listArg)))))
              ;apply functionName to the first element and cons this with the recursive call

;-----------------3) function-3---------------
(define (function-3 functionName) 
         (functionName 3))

;-----------------4) zipper---------------
(define (zipper l1 l2) 
          (if (or (empty? l1)(empty? l2))
              '();if either of the lists is empty, end the cons
              (cons (list (car l1) (car l2)) (zipper (cdr l1) (cdr l2)))))

;-----------------5) segregate---------------
(define (segregate alist) 
        (list (getEvens alist)(getOdds alist)))

;returns a list of only evens
(define (getEvens alist)
          (if (empty? alist)
              '()
              (if (even? (car alist))
                  (cons (car alist) (getEvens (cdr alist)));add it to the list if even
                  (getEvens (cdr alist)))));otherwise just recurse past this value

(define (getOdds alist)
          (if (empty? alist)
              '()
              (if (odd? (car alist))
                  (cons (car alist) (getOdds (cdr alist)))
                  (getOdds (cdr alist))))) 

;-----------------6) in-list?---------------
(define (in-list? e l)
          (if (empty? l)
              #f;if reached the end without returning #t, the element is not inlist
              (if (equal? e (car l))
                   #t
                   (in-list? e (cdr l)))))

;-----------------7) my-sorted---------------
(define (my-sorted? l)
         (if (number? (car l))
            (numCmpr l);need a different comparator function for strings and numbers
            (stringCmpr l)
            ))

(define (numCmpr l)
         (if (<= (length l) 1)
            #t
            (and (<= (car l) (cadr l)) (numCmpr (cdr l)) )))
            ;if there is one pair of adjacent numbers, a and b, which do not satisfy
            ;the condition a <= b; then the list is not sorted

(define (stringCmpr l)
         (if (<= (length l) 1)
            #t
            (and (string<=? (car l) (cadr l))(stringCmpr (cdr l)) )))

;-----------------8) flatten---------------
(define (flatten l)
          (if (empty? l)
              '()
              (if (list? (car l))
                  (append (flatten (car l)) (flatten (cdr l)));if the first element in the parent list is a list, flatten this
                  (cons (car l) (flatten (cdr l))); first element is an atom -> just flatten the rest of the list
              )
         ))

;-----------------9) threshold---------------
(define (threshold l t)
         (if (empty? l)
             '();base case -> end the cons
             (if (< (car l) t)
                 (cons (car l)(threshold (cdr l) t));add element to list of < threshold
                 (threshold (cdr l) t))    
        ))

;-----------------10) value-at---------------
(define (value-at l i)
         (if (>= i (length l))
             "index out of bounds"
             (recursiveValueAt l i 0)
        ))

(define (recursiveValueAt l i cI);cI = current Index
         (if (eq? cI i)
             (car l)
             (recursiveValueAt (cdr l) i (add1 cI))
        ))

;-----------------11) rev-sort---------------
(define (rev-sort l)
         (if (<= (length l) 1)
             l
             (lazy-sort l)
        ))

; sort which finds the largest number then lazy-sorts the rest of the 
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