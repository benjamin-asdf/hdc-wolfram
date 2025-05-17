(ns intro
  (:require
   [clojure.set :as set]
   [clojure.string :as str]
   [wolframite.api.v1 :as wl]
   [wolframite.core :as wc]
   [wolframite.lib.helpers :as h]
   [wolframite.runtime.defaults :as defaults]
   [wolframite.tools.hiccup :as wh]
   [wolframite.wolfram :as w]
   [scicloj.kindly.v4.kind :as k]
   [scicloj.clay.v2.api :as clay]))

;;
;; # HD/VSA Intro walkthrough
;;
;; https://github.com/benjamin-asdf/hdc-wolfram/issues/4
;;

;; ### Neural Computing à la John Von Neumann

;; - A very humble initial observation: Brains have a high *count of elements* (large circuits).
;; - let's see what happens if you build a computer with very **wide words**.
;; - Instead of 64 bit words, use 10.000 bit words


;; ----------------


;; ## Multiply-Add-Permute (MAP)

;; # The Hypervector
;; We call vectors of 1000+ bit lenght *hypervectors*

;; 10.000 dimensions is a common choice.
(def dimension (long 1e4))

;;
;; Making a binary, random hypervector.
;; This is also a **point** in the **vectorspace** denoted
;;
;; latex
;; H := { -1, 1 } ^ dimensions
;;

(defn seed
  "Retuns a fresh random HD with [[dimension]]."
  ([n]
   (w/Table (w/RandomChoice [-1 1]) ['n n] ['k dimension]))
  ([] (w/Table (w/RandomChoice [-1 1]) ['k dimension])))


;; A binary hypervector can be displayed as QR code like array plot for fun""
(defn plot
  [hd]
  (w/ArrayPlot
   (w/Plus 1 (w/ArrayReshape hd [(w/Sqrt dimension) (w/Sqrt dimension)]))))

(wh/view (plot (seed)))

(defn similarity [a b]
  (w/Divide
   (w/Dot
    ;; (w/ReshapeLayer [w/Automatic dimension] a)
    a
    b)
   dimension))


(wl/! (w/Divide [1 2 3] 3))
(wl/! (similarity (seed) (seed)))

(wl/! (w/HammingDistance (seed) (seed)))
(wl/!
 (w/HammingDistance (seed 2) (seed)))

(wl/!
 (w/Map w/HammingDistance (seed 2) (seed)))

(let [a (wl/! (seed))]
  (wl/! (similarity a a)))

(let [a (wl/! (seed))]
  (wl/! (similarity a a)))

(let [a (wl/! (seed))
      b (seed)]
  (wl/! (similarity a b)))


(bit-xor -1 1)

(bit-xor 1 1)
(bit-xor -1 -1)


(bit-xor 1 -1)
(bit-and 1 -1)
(bit-and 1 -1)
(bit-and 1 1)


(wl/! (w/Inner 'f ['a 'b] ['x 'y] 'g))
(wl/! (w/Outer 'f ['a 'b] ['x 'y] 'g))


(wl/! (w/Outer 'f ['a 'b] ['x 'y 'z]))
(wl/! (w/Inner 'f ['a 'b] ['x 'y] 'g))



(wl/! (w/Inner
       'f
       ['a 'b]
       ['x 'y] w/List))

(wl/! (w/Outer
       'f
       [['a 'b]]
       ['x 'y]))



(wl/!
 (w/Inner
  (w/fn [x y]

    ;; (w/If (w/Equal x y) 1 0)
    ;; [x y (w/Equal x y)]
    0)
  [[-1 1] [0 1]]
  [-1 1]
  w/List))

[[[-1 -1 true]
  [1 1 true]]
 [[0 -1 false]
  [1 1 true]]]

[[(f -1 -1) (f 1 1)]
 [(f 0 -1) (f 1 1)]]

(wl/! (w/Dot [-1 1 1] [-1 1 1]))


(wl/! (w/Total [true false]))

(bit-or -1 1)
(bit-or -1 1)
(bit-xor -1 1)
(bit-xor 1 -1)

(bit-xor 1 1)
(bit-xor -1 -1)
(= true false)


#_(defn cleanup-memory
    [n]
    (let [codebook (wl/! (seed n))
          state (atom {})]
      {:->hd (fn [obj]
               ;;
               (or (get obj @state)
                   (let [next-hd (nth codebook (count @state))]
                     (swap! state assoc obj next-hd)
                     next-hd)))
       :cleanup (fn [hd]
                  (let [similarities

                        ])
                  ;; -> returns obj
                  )
       :codebook codebook
       :state state}))






(comment
  (seed 10)

  (def codebook (wl/! (seed 10)))

  (wl/!
   (w/Times
    codebook
    (nth codebook 0)))

  (wl/!
   (w/Dot
    [[1 -1 1]
     [1 1 1]]
    [1 -1 1]))



  (w/ElementwiseLayer
   )

  (wl/!
   (w/Inner
    w/BitAnd
    [[1 -1 1]
     [1 1 1]]
    [1 -1 1]))

  (w/BitAnd -1 -1)
  (wl/! (w/Xor -1 -1))

  (wl/! (w/BitXor -1 -1))
  (wl/! (w/BitXor 1 1))
  (wl/! (w/BitXor 1 -1))
  (wl/! (w/BitXor -1 1))



  (wl/! (w/BitXor -1 1))
  (wl/! (w/BitAnd 1 1))
  (wl/! (w/BitAnd -1 1))
  (wl/! (w/BitAnd -1 1))

  (wl/! (w/DotEqual -1 1))

  (wl/!
   (w/Thread)
   (w/Equal -1 1))



  (wl/! (w/Thread '(f [a b c])))

  (wl/! (w/Thread
         '(List
           [a b c]
           [x y z])))

  (wl/!
   (w/Thread
    '(f
      [a b c]
      [x y z])))

  ;;
  ;; codebook:
  ;;
  ;; +-----------+
  ;; |           | h1
  ;; +-----------+
  ;; |           | h2,...
  ;; +-----------+
  ;; |           |
  ;; |           |
  ;; |           |
  ;; |           |
  ;; |           |
  ;; |           |
  ;; +-----------+
  ;;

  (wl/start!)


  (wl/!
   (w/KroneckerProduct
    [[1 2]]
    [[1 2]])))
