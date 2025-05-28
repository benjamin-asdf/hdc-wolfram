(ns hdc-wolfram.attic.bsbc
  (:require
   [wolframite.api.v1 :as wl]
   ;; [wolframite.lib.helpers :as h]
   [wolframite.runtime.defaults]
   [wolframite.tools.hiccup :as wh]
   [wolframite.wolfram :as w]
   ;; [scicloj.clay.v2.api :as clay]
   [wolframite.impl.wolfram-syms.intern :as wi]))

;; High-dimensional computing with sparse vectors
;; https://ieeexplore.ieee.org/document/7348414

(def ^:dynamic default-opts
  {:bsbc/segment-count 20
   :bsbc/segment-length 500})

(wl/! "Needs[\"CUDALink`\"]")

(wl/!
 "vec = CUDASparseVector[ SparseArray[Table[{i} -> i, {i, 3}]], \"Real64\"];")

(wl/! "Normal[Normal[vec]]")

(wl/! "v = Table[{i} -> i, {i, 3}]")

(wl/! (w/Normal (w/SparseArray [(w/-> [1] 1) (w/-> [2] 2) (w/-> [3] 3)])))


(def seg-count 20)
(def seg-length 500)

(wl/! (w/do
        (w/= 'a
             (w/SparseArray
              (into []
                    (comp (map (fn [segment]
                                 (+ (* segment seg-length)
                                    (rand-int seg-length))))
                          (map (fn [idx]
                                 (w/-> idx
                                       1))))
                    (range seg-count))
              [(* seg-length seg-count)]
              0))
        [(* seg-length seg-count)]))


(wl/!
 (w/_=
  'seed
  (let [v 'vec]
    (w/fn []
      (w/do (w/= 'v
                 (w/SparseArray
                  (into []
                        (comp (map (fn [segment]
                                     (+ (* segment seg-length)
                                        (rand-int
                                         seg-length))))
                              (map (fn [idx]
                                     (w/-> idx
                                           1))))
                        (range seg-count))
                  [(* seg-length seg-count)]
                  0))
            (w/= v (list 'CUDASparseVector 'v "Real32"))
            [(* seg-length seg-count)])))))


(defn seed-1
  [v]
  (wl/!
   (w/do
     (w/= 'v
          (w/SparseArray
           (into []
                 (comp (map (fn [segment]
                              (+ (* segment seg-length)
                                 (rand-int
                                  seg-length))))
                       (map (fn [idx]
                              (w/-> idx 1))))
                 (range seg-count))
           [(* seg-length seg-count)]
           0))
     (w/= v (list 'CUDASparseVector 'v "Real32"))
     [(* seg-length seg-count)])))

(wl/! (list 'seed))
(wl/! "Normal[Normal[vec]]")

(seed-1 'a)
(seed-1 'b)

(wl/! (w/Dot (list 'Normal (list 'Normal 'a))
             (list 'Normal (list 'Normal 'b))))

(wl/! (list 'Normal (list 'Normal 'a)))


(wl/!  (list 'CUDAMemoryGet 'a))



;; CUDAMemoryGet[CUDAMemoryLoad[Range[10]]]

(defn dot
  [a b]
  (wl/!
   (w/do
     (w/= 'out (list 'CUDADot a b))
     (list 'Normal 'out))))




(dot 'a 'a)
(dot 'a 'b)

(wl/! "out = CUDADot[a,b];")
(wl/! "Normal[out]")
(wl/! "out = CUDADot[b,b];")
(wl/! "Normal[out]")


(def address-matrix "addr matrix" 'addrmatrix)

(def address-length (* 20 500))
(def address-count (long 1e4))
;; 0.000003
(def density 0.002)

(let [address-count 3
      address-length 2
      density 0.5]
  (wl/!
   (w/do
     (w/= 'v
          (w/SparseArray
           (w/Map
            (w/fn [p] (w/-> p 1))
            (w/DeleteDuplicates
             (w/Table
              [(w/RandomInteger address-count)
               (w/RandomInteger address-length)]
              ['i (* address-count address-length density)])))
           [address-count address-length]
           0))
     ;; (w/= address-matrix (list 'CUDASparseMatrix 'v "Real32"))
     ;; (w/= address-matrix (list 'CUDASparseMatrix 'v "Real64"))
     ;; [address-count address-length]
     (w/Normal 'v))))


(let [address-count 500
      address-length 10
      density 0.1]
  (wl/! (w/Normal
         (w/SparseArray
          (w/Map (w/fn [p]
                   (w/-> p
                         1))
                 (w/DeleteDuplicates
                  (w/Table
                   [(w/RandomInteger [1 address-count])
                    (w/RandomInteger [1 address-length])]
                   ['i
                    (* address-count address-length density)])))
          [address-count address-length]
          0))))


(let [address-count 500
      address-length 10
      density 0.1]
  (wl/!
   (w/do
     (w/= 'v
          (w/SparseArray
           (w/Map
            (w/fn [p]
              (w/-> p
                    1))
            (w/DeleteDuplicates
             (w/Table
              [(w/RandomInteger [1 address-count])
               (w/RandomInteger [1 address-length])]
              ['i
               (* address-count address-length density)])))
           [address-count address-length]
           0))
     (w/= address-matrix (list 'CUDASparseMatrix 'v "Real32"))
     [address-count address-length])))

(wl/! (w/Normal (w/Normal address-matrix)))

(wl/!
 (w/do
   (w/= 'v
        (w/SparseArray
         (w/Map
          (w/fn [p]
            (w/-> p
                  1))
          (w/DeleteDuplicates
           (w/Table
            [(w/RandomInteger [1 address-count])
             (w/RandomInteger [1 address-length])]
            ['i
             (* address-count address-length density)])))
         [address-count address-length]
         0))
   (w/= address-matrix (list 'CUDASparseMatrix 'v "Real32"))
   [address-count address-length]))


(wl/! (w/= 'out (list 'CUDADot address-matrix 'a)))

(time
 (do
   ;; (wl/! "dout = ;")
   (wl/! "maxarg = CUDAArgMaxList[CUDADot[addrmatrix,a]];")
   (wl/! (w/Normal 'maxarg))))


(wl/! "i = CUDAMemoryInformation[a];")
(wl/! (w/Normal 'i))




(time
 (do
   ;; (wl/! "dout = ;")
   (wl/! "maxarg = CUDAArgMaxList[CUDADot[addrmatrix,a]];")
   (wl/! (w/Normal 'maxarg))))

(time
 (do
   ;; (wl/! "dout = ;")
   (wl/! "Normal[ CUDAArgMaxList[CUDADot[addrmatrix,a]]]")))





(wl/! "CUDAArgMaxList[CUDAVector[1.0*Range[10000000, 0, -1], \"Real32\"]]")


(dot address-matrix 'a)

(time
 (wl/!
  (w/do
    (wl/! "out = CUDAArgMaxList[CUDADot[addrmatrix,a]];")
    (w/Normal 'out))))



(let [address-count 1
      ;; (long 1e5)
      density 0.5]
  (wl/!
   (w/do
     (w/= 'addrmatrix
          (w/SparseArray
           (w/Map (w/fn [p]
                    (w/-> p 1))
                  (w/DeleteDuplicates
                   (w/Table
                    [(w/RandomInteger [1 address-count])
                     (w/RandomInteger [1 address-length])]
                    ['i
                     (* address-count address-length density)])))
           [address-count
            address-length]
           0))
     [])))

(wl/!
 (w/do
   (w/= 'bvec
        (w/SparseArray
         (into []
               (comp (map (fn [segment]
                            (+ (* segment seg-length)
                               (rand-int seg-length))))
                     (map (fn [idx] (w/-> idx 1))))
               (range seg-count))
         [(* seg-length seg-count)]
         0))
   [(* seg-length seg-count)]))


(wl/! "addrmM = CUDAMemoryLoad[addrmatrix];")
(wl/! "bvegM = CUDAMemoryLoad[bvec];")

(wl/! "CUDAMemoryInformation[addrmatrix]")
(wl/! "Normal[meminfo]")

(time
 (wl/!
  (w/do
    (wl/! "out = CUDAArgMaxList[CUDADot[addrmM,bvegM]];")
    (w/Normal 'out))))

(time
 (wl/!
  (w/do
    (wl/! "out = CUDADot[addrmatrix,bvec];")
    (w/Normal 'out))))

(time
 (wl/!
  (w/do
    (wl/! "out = CUDADot[addrmM,bvegM];")
    (w/Normal 'out))))


(time
 (wl/!
  (w/do
    (wl/! "out = Dot[addrmatrix,bvec];")
    (w/Normal 'out))))

(wl/!
 (w/do
   (w/= 'out
        (list
         'CUDAMap 'Ceiling
         (w/RandomReal 1 10)))
   (w/Normal (w/Normal 'out))))


(wl/!
 (w/_= 'vec2 (list 'CUDASparseVector (w/SparseArray [[1]] [5 10]))))
(wl/! "Normal[Normal[vec2]]")

(def vec3 "sparse vec" 'vec3)

(wl/! (w/do
        (w/= 'myvec (list 'CUDAVector [1. 2. 3.]))
        :ok))

(wl/! "Normal[Normal[myvec]]")




(do (wl/! (w/do (w/= 'myvec
                     (list 'CUDASparseVector
                           (w/SparseArray [1. 2. 3.])
                           "Real64"))
                :ok))
    (wl/! "Normal[Normal[myvec]]"))


(do (wl/! (w/do (w/= 'myvec
                     (list 'CUDASparseVector
                           (w/SparseArray [0 1 0])
                           "Real"))
                :ok))
    (wl/! "Normal[Normal[myvec]]"))



(do
  (wl/!
   (w/do (w/= 'myvec (list 'CUDASparseVector (w/SparseArray [0 1 0]) "Real32")) :ok))
  (wl/! "Normal[Normal[myvec]]"))




(wl/! (w/AbsoluteTiming (w/do (w/Dot 'randM 'randM)    nil)))
(time (wl/! (w/do (w/Dot 'randM 'randM) nil)))
(time (wl/! (w/do (list 'CUDADot 'randM 'randM) nil)))



(time (wl/! "randM = RandomReal[1, {4000, 4000}];"))
(time (wl/! "CUDADot[randM,randM];"))
(time (wl/! "randMG = CUDAMemoryLoad[randM];"))
(time (wl/! "res = CUDADot[randMG,randMG];"))










(do
  (wl/! "Needs[\"CUDALink`\"]")
  (wl/! "v = SparseArray[Table[{i} -> i, {i, 3}]];")
  (wl/! "spv = CUDASparseVector[v,\"Real64\"];")
  (wl/! "Normal[v]")
  (wl/! "Normal[Normal[spv]]"))


(do
  (wl/! "Needs[\"CUDALink`\"]")
  (wl/! "v = SparseArray[Table[{i} -> i, {i, 3}]];")
  (wl/! "spv = CUDASparseVector[v,\"Real64\"];")
  (wl/! "Normal[v]")
  (wl/! "Normal[Normal[spv]]"))


(do
  (wl/! "Needs[\"CUDALink`\"]")
  (wl/! "vec = CUDASparseVector[ SparseArray[Table[{i} -> i, {i, 3}]], \"Real64\"];")
  (wl/! "Normal[Normal[vec]]"))











(wl/! 'myvec)
(wl/! (list 'CUDADot 'myvec 'myvec))

(wl/!
 (w/do
   ;; (w/= 'v (w/SparseArray [[0 1]] [5 10]))
   (w/= vec3 (list 'CUDASparseVector
                   (w/SparseArray
                    [(w/-> [1] 1)
                     (w/-> [2] 2)
                     (w/-> [3] 3)])))
   nil))

(wl/! (w/Normal vec3))


(wl/! (w/Normal (w/SparseArray [[0 1]] [5 10])))
(wl/! (w/Normal (w/SparseArray [[0 1]] [5 10])))





(defn seed [n {:bsbc/keys [segment-count segment-length]}]


  )



(comment
  (wl/stop!)
  (wl/start!))
