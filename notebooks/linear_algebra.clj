(ns linear-algebra
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

(wl/!
 (w/Dimensions
  (w/Table
   ['m 'i 'j]
   ['i 3]
   ['j 3])))

(wl/!
 (w/Table
  ['m 'i 'j]
  ['i 3]
  ['j 3]))


(wh/view
 (w/MatrixForm
  (w/Table
   ['m 'i 'j]
   ['i 3]
   ['j 3])))

(def mat
  [[1.5 1.6]
   [1.7 1.8]])

(wl/! (w/Thread (w/First [[1 2 3]])))

(wl/!
 (w/First [1 2 3]))

(wl/!
 (w/First [[1 2 3]]))


(wl/!
 (w/Thread '(f [a b c])))


(wl/!
 (w/Thread '(f [a b c])))

(wl/!
 (w/Thread '(f [[a]])))

(wl/! (w/Map 'f '[a b c]))


(wl/! (w/Thread '(f [a b c] x)))
'[(f a x) (f b x) (f c x)]

(wl/!
 (w/Thread (w/== '[a b c] '[x y z])))

'[(== a x) (== b y) (== c z)]

(wl/!
 (w/Thread
  (w/Log ['x w/== 'y] w/==)))

(wl/!
 (w/Thread
  (w/Log ['x w/== 'y]) w/==))


(wl/!
 (w/Thread
  (w/Log (w/Equal 'x 'y))
  w/Equal))



(wl/! (w/Part [1 2 3] 1))
(wl/! (w/Part [[1 2 3]] 1))


(wl/!
 (w/Part
  [[1 2 3]
   [4 5 6]]

  [0 1]))


(wl/!
 (w/Part [[1 2 3] [4 5 6]] [1 2]))
;; (wl/!
;;  (w/Part [[1 2 3] [4 5 6]] [[1]]))


(wl/!
 (w/Part [[1 2 3] [4 5 6]] [1]))

(wl/!
 (w/Part [[1 2 3]
          [4 5 6]
          ['foo]] [1 3]))

(wl/! (w/Part [[1 2 3] [4 5 6] ['foo]] 1))

(wl/! (w/Part (w/Part [[1 2 3] [4 5 6] ['foo]] 1) 1))

;; ----------

(wh/view (w/TeXForm (w/Xor 'a 'b)))

(comment
  (clay/make!
   {:source-path "notebooks/linear_algebra.clj"})
  (wl/start!))
