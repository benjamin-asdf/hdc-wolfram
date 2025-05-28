(ns hdc-wolfram.bsbc
  (:require
   [wolframite.api.v1 :as wl]
   ;; [wolframite.lib.helpers :as h]
   [wolframite.runtime.defaults]
   [wolframite.tools.hiccup :as wh]
   [wolframite.wolfram :as w]
   ;; [scicloj.clay.v2.api :as clay]
   [wolframite.impl.wolfram-syms.intern :as wi]))




;; hmmm,
;; not sure


(defprotocol WolframRef
  "A protocol for Wolfram references."
  (wolfram-ref [this]
    "Return the Wolfram reference for this object.")
  (wolfram-sym [this]
    "Return the Wolfram symbol for this object."))

(let [counter (atom 0)]
  (defn wolfram-gensym
    [{:keys [prefix]}]
    (let [dat {:sym (symbol (str "hdcWolfram" (swap! counter inc)))}]
      (reify
        WolframRef
          (wolfram-sym [_] (:sym dat))
        clojure.lang.IDeref
          (deref [_] (wl/! (w/Normal (:sym dat))))
        clojure.lang.IPending
        (isRealized [_] false)))))

(def ^:dynamic default-opts
  {:bsbc/segment-count 20
   :bsbc/segment-length 500})

(defn seed
  "Return a fresh block sparse hypervector."
  ([] (seed default-opts))
  ([{:bsbc/keys [segment-count segment-length]}]
   (let [sym (wolfram-gensym {:prefix "hd"})]
     (wl/! (w/do (w/= (wolfram-sym sym)
                      (w/SparseArray
                        (w/Table (w/-> (w/+ (w/* 'seg segment-length)
                                            (w/RandomInteger
                                              [1 segment-length]))
                                       1)
                                 ['seg 0 (w/- segment-count 1)])
                        [(* segment-count segment-length)]
                        0))))
     sym)))

(comment
  (def segment-count 2)
  (def segment-length 3)
  (* 2 3)

  (wl/! (w/Normal
         (w/SparseArray
          (w/Table
           (w/->
            (w/+
             (w/* 'seg segment-length)
             (w/RandomInteger [1 segment-length]))
            1)
           ['seg 0 (w/- segment-count 1)])
          [(* segment-count segment-length)]
          0))))

(wl/!
 (w/= 'similarity
      (w/fn [a b]
        (w/Divide (w/Dot a b)
                  (:bsbc/segment-count default-opts)))))

(defn similarity
  "Return the similarity between two block sparse hypervectors."
  [a b]
  (wl/! (list 'similarity
              (wolfram-sym a)
              (wolfram-sym b))))

(w/! (w/= 'superposition w/Plus))

(defn superposition
  "Return the superposition of two block sparse hypervectors."
  [a b]
  (wl/! (list 'superposition (wolfram-sym a) (wolfram-sym b))))

(defn bind
  "Return a new hypervector that is the binding of two block sparse hypervectors.



  This is a context dependent thinning.

  "
  [a b]

  )

(wl/! (w/ArgMax w/Identity (wolfram-sym a)))

(wl/! (w/Normal (w/ArrayReshape (wolfram-sym a)
                                [(:bsbc/segment-count
                                  default-opts)])))

;; hm, best make indices, sum them and then take the remainder

(wl/!
 (w/Part
  (w/ArrayReshape (wolfram-sym a)
                  [(:bsbc/segment-count default-opts)
                   (:bsbc/segment-length default-opts)])
  "NonzeroPositions"))

(wl/! "hdcWolfram18[\"NonzeroPositions\"]")




(remove zero? (take 500 @a))

(def a (seed))
(def b (seed))

(wl/! (similarity a b))
(similarity a a)
(similarity a b)

(time (wl/! (w/Dot (wolfram-sym a) (wolfram-sym b))))
(time (wl/! (w/Dot (wolfram-sym a) (wolfram-sym b))))



(wl/! (w/Normal (seed)))


; Find max index for each segment
(defn max-index-per-segment
  "Return the maximum index for each segment."
  [hv]
  (wl/! (w/Map w/Max
               (w/Map (w/fn [seg]
                        (w/Select (w/Range 1 (:bsbc/segment-length default-opts))
                                  (w/fn [i] (w/Not (w/== (w/Part seg i) 0)))))
                      (w/ArrayReshape
                        (wolfram-sym hv)
                        [(:bsbc/segment-count default-opts)
                         (:bsbc/segment-length default-opts)])))))



(max-index-per-segment a)
[210 460 364 314 223 370 207 87 471 84 373 3 317 204 347 266 137 93 33 73]
(nth @a (inc 210))
(nth @a (dec 210))

(wl/! (w/ArgMax [1 2 3]))

;; ArgMax for each segment (like torch.argmax)
(defn argmax-per-segment
  "Return the argmax (position of maximum value) for each segment."
  [hv]
  (wl/!
   (w/Map
    (w/fn [seg] (w/Ordering seg -1))
    (w/ArrayReshape hv
                    [(:bsbc/segment-count default-opts)
                     (:bsbc/segment-length default-opts)]))))

(argmax-per-segment (wolfram-sym a))

(wl/! (w/Ordering (wolfram-sym a) -1))




(wl/!
 (w/Times
  (w/Range 1 (:bsbc/segment-length default-opts))
  [0 0 1 0 0]
  ;; (wolfram-sym a)
  ))



(comment
  (wl/start!)
  (wl/stop!))
