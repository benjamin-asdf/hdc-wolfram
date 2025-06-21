(ns hdc-wolfram.bsbc
  (:require
   [wolframite.api.v1 :as wl]
   ;; [wolframite.lib.helpers :as h]
   [wolframite.runtime.defaults]
   [wolframite.tools.hiccup :as wh]
   [wolframite.wolfram :as w]
   ;; [scicloj.clay.v2.api :as clay]
   [wolframite.impl.wolfram-syms.intern :as wi])
  ;; (:refer-clojure :exclude [empty])
  )

;; ----------
;;
;; Paper: https://ieeexplore.ieee.org/document/7348414
;;


;; hm,

(let [counter (atom 0)]
  (defn w-gensym
    [{:keys [prefix]}]
    (symbol (str "hdcWolfram" (swap! counter inc)))))

(defn wsym
  [opts expr]
  (let [sym (w-gensym opts)]
    (wl/! (w/do (w/= sym expr)))
    sym))

(defn wforce [s] (wl/! (w/Normal s)))

;; -----------------


(def default-opts
  ;; {:bsbc/segment-length 3 :bsbc/segment-count 3}
  {:bsbc/segment-length 500
   :bsbc/segment-count 20})

(wl/! (w/= 'fromIndices
           (w/fn [indices segmentcount segmentlength]
             (let [idxs (->> indices
                             (w/+ 1)
                             (w/+ (w/Range 0
                                           (w/* (w/- segmentcount 1) segmentlength)
                                           segmentlength))
                             (w/Flatten)
                             (w/Map (w/fn [idx]
                                      (w/-> idx 1))))]
               (w/SparseArray idxs [(w/* segmentcount segmentlength)] 0)))))

(wl/!
 (w/=
  'zeros
  (w/fn [segmentcount segmentlength]
    (w/SparseArray (w/Table 0 [(w/* segmentcount segmentlength)])))))

(defn zeros []
  (list 'zeros
        (:bsbc/segment-count default-opts)
        (:bsbc/segment-length default-opts)))

(wl/! (w/= 'ones
           (w/fn [segmentcount segmentlength]
             (w/SparseArray (w/Table 1 [(w/* segmentcount segmentlength)])))))

(defn ones
  []
  (list 'ones
        (:bsbc/segment-count default-opts)
        (:bsbc/segment-length default-opts)))


;; identity hd was hard to think about with 1 based indices
(defn from-indices
  "Whatch out: these are zero based indices."
  [indices]
  (list 'fromIndices
        indices
        (:bsbc/segment-count default-opts)
        (:bsbc/segment-length default-opts)))

(wl/! (w/= 'indices
           (w/fn [hd segmentlength]
             (w/Map (w/fn [seg]
                      (w/- (w/Part (w/Position (w/Normal seg) (w/Max seg)) 1)
                           1))
                    (w/Partition hd [segmentlength])))))

(defn indices
  "Returns hd indices.

  Watch out: These are zero based.
  "
  [hd]
  (list 'indices hd (:bsbc/segment-length default-opts)))

(wl/! (w/= 'randomIndices
           (w/fn [segmentcount segmentlength]
             (w/Table (w/RandomInteger [0 (w/- segmentlength 1)])
                      ['seg 0 (w/- segmentcount 1)]))))

(wl/! (w/= 'seed
           (w/fn [segmentcount segmentlength]
             (from-indices (list 'randomIndices segmentcount segmentlength)))))

(defn seed
  "Return a fresh block sparse hypervector."
  ([] (seed default-opts))
  ([{:bsbc/keys [segment-count segment-length]}]
   (list 'seed segment-count segment-length)))

(defn wseed []
  (wsym {:prefix "hd"} (seed default-opts)))

(wl/!
 (w/= 'similarity
      (w/fn [a b segmentcount]
        (w/Divide
         (w/Dot a b)
         segmentcount))))

(defn similarity
  "Return the similarity between two block sparse hypervectors."
  [a b]
  (list 'similarity a b (:bsbc/segment-count default-opts)))

(wl/! (w/= 'superposition w/Plus))

(defn superposition
  "Return the superposition of two block sparse hypervectors."
  [a b]
  (list 'superposition a b))

(wl/! (w/= 'bind1
           (w/fn [a b alpha segmentcount segmentlength]
             (-> (w/+ (indices a)
                      ;; todo fix multiarity
                      (w/* alpha (indices b)))
                 (w/Mod segmentlength)
                 (from-indices)))))

(defn bind
  "Return a new hypervector that is the binding of two block sparse hypervectors.


  This is a context dependent thinning.

  "
  ([a b] (bind a b 1))
  ([a b alpha]
   (list 'bind1
         a
         b
         alpha
         (:bsbc/segment-count default-opts)
         (:bsbc/segment-length default-opts))))

(defn unbind
  "Returns a new hypervector where b is unbound from a."
  [a b]
  (bind a b -1))

(wl/!
 (w/= 'identityHd
      (w/fn [segmentcount]
        (from-indices (w/Table 0 segmentcount)))))

(defn identity-hd
  "Returns the hd that is the identity element of [[bind]]."
  []
  (list 'identityHd (:bsbc/segment-count default-opts)))

(wl/! (w/= 'inverse
           (w/fn [hd segmentcount segmentlength]
             (from-indices
              (w/Mod (w/- segmentlength (indices hd))
                     segmentlength)))))

(defn inverse
  "Returns the inverse of `hd`.

  Bind with the inverse vector is equivalent to unbind."
  [hd]
  (list 'inverse
        hd
        (:bsbc/segment-count default-opts)
        (:bsbc/segment-length default-opts)))

(wl/!
 (w/=
  'normalize
  (w/fn [hd]
    (from-indices (indices hd)))))

(defn normalize [hd]
  (list 'normalize hd))



(comment
  ;; ----------------


  (bind (from-indices [1 1 1]) (from-indices [1 1 1]))
  (wforce
   (unbind (from-indices [1 1 1]) (from-indices [1 1 1])))
  (wforce
   (unbind (from-indices [1 1 1]) (from-indices [1 1 1])))
  (wforce
   (bind (from-indices [1 1 1]) (inverse (from-indices [1 1 1]))))

  (wforce (indices (inverse (from-indices [1 1 1]))))
  (wforce (indices (inverse (from-indices [0 1 2]))))

  (wforce (inverse (from-indices [0 1 2])))

  (def a (wseed))
  (def b (wseed))

  (wl/! (w/== a (bind (bind a b) (inverse b))))

  (wforce [a b (bind a (inverse b))])
  (wforce
   [(from-indices [0 0 0])
    (from-indices [1 1 1])
    (bind (from-indices [0 0 0])
          (from-indices [1 1 1]))
    (bind (from-indices [0 0 0])
          (inverse (from-indices [1 1 1])))])


  (wl/! (similarity (bind a b) a))
  (wl/! (similarity (unbind (bind b a) b) a))
  ;; -------
  (wl/start!)
  (wl/stop!))
