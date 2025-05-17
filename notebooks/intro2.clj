(ns intro2
  (:require
   [wolframite.api.v1 :as wl]
   ;; [wolframite.lib.helpers :as h]
   [wolframite.runtime.defaults]
   [wolframite.tools.hiccup :as wh]
   [wolframite.wolfram :as w]
   ;; [scicloj.clay.v2.api :as clay]
   ))

;;
;; # HD/VSA MAP Intro
;;
;; https://github.com/benjamin-asdf/hdc-wolfram/issues/4
;;

;; This follows the paper
;; Hyperdimensional Computing: An Introduction to Computing in Distributed Representation with High-Dimensional Random Vectors
;; P. Kanerva 2009

;; ### Neural Computing à la John Von Neumann

;;
;; - A very humble initial observation: Brains have a high *count of elements* (large circuits).
;; - let's see what happens if you build a computer with very **wide words**.
;; - Instead of 64 bit words, use 10.000 bit words, **hypervectors** (HDV).
;; - This datatype together with a set of operations is a **Vector Symbolic Architecture**
;;

;; -----------------------

;; ## Multiply-Add-Permute (MAP)
;;
;; A simple flavor of VSA where you use +, * and permute.
;;

;; # The Hypervector
;; We call vectors of 1000+ bit lenght *hypervectors*

;;
;; ```
;;    ----------------------------
;;    | -1 1 -1 1 1 1 -1         |
;;    ----------------------------
;;               ------------> ,  d
;;
;; ```
;;
;;
;;
;; dimensions = d = 10.000
;;
;; Using -1 and 1 as elements, we can denote
;;
;; `H` = {-1, 1}^10.000
;;
;; as the 10.000 dimensional hypervector space.
;;
;; - The hypervector is a point in hyperspace.
;; - There are 2^10.000 points in the hypervector space.
;;
;; - 2 random hypervectors agree in 1/2 of the bits.
;; - They have roughly 50% similarity.
;; - The points are not clustered; But looking from one point,
;;   the distances are highly concentrated mid-way into the space.
;; - Quote Kanerva 2009: It is easy to see that half the space is closer to a point than 0.5 and the other half is further away, but it is somewhat surprising that less than a millionth of the space is closer than 0.476 and less than a thousand-millionth is closer than 0.47; similarly, less than a millionth is further than 0.524 away and less than a thousand-millionth is further than 0.53.
;;
;;
;;
;; There are different flavors including sparse hypervectors
;; See
;; Schlegel et.al. 2021  A comparison of Vector Symbolic Architectures
;; http://www.arxiv.org/abs/2001.11797
;;


;; 10.000 dimensions is a common choice.
(def dimensions (long 1e4))

(wl/! (w/_= 'seed
            (w/fn []
              (w/Table (w/RandomChoice [-1 1])
                       [dimensions]))))

(wl/! (w/_= 'seedb
            (w/fn [n]
              (w/Table (w/RandomChoice [-1 1])
                       ['n n]
                       ['k dimensions]))))

(defn seed
  ([n] (list 'seedb n))
  ([] (list 'seed)))

(count (wl/! (seed)))

(take 10 (wl/! (seed)))


;; Making a QR code kinda thing out of a hypervector:

(defn plot
  [hd]
  (w/ArrayPlot (w/Plus 1
                       (w/ArrayReshape hd
                                       [(w/Sqrt dimensions)
                                        (w/Sqrt
                                         dimensions)]))))


(wh/view-no-summary
 (wl/! (w/Block [(w/= 'x (plot (seed)))] 'x)))

;; Go back to (mostly) -1 and 1's after superposition and such.

(wl/! (w/_= 'normalize w/Sign))

(defn normalize [hd]
  `(~'normalize ~hd))


;; ## Similarity

;; For example Hamming distance, counting the bits that are different:

(wl/! (w/HammingDistance [-1 1 1] [-1 1 1]))

(wl/! (w/HammingDistance [-1 -1 1] [-1 1 1]))

(wl/! (w/HammingDistance [1 -1 -1] [-1 1 1]))

(wl/! (w/HammingDistance (seed) (seed)))

(wl/! (w/_= 'distance
            (w/fn [a b]
              (w/Divide (w/HammingDistance a b) dimensions))))


(defn distance [a b]
  `(~'distance ~a ~b))


;; ------
;; I use dot similarity for the rest of this page

;; ## Dot similarity

;; - Counts where the vectors agree minus where they disagree
;; - Divide by dimensions
;; -  `-1` for the negative (opposite) vector
;; -  `~0` for unrelated vectors
;; -   `1` for the equal vector
;; -  `>1` if one of the vectors has values higher than 1 they agree
;;
;;

(wl/!
 (w/_= 'similarity
       (w/fn [a b]
         (w/Divide (w/Dot a b) dimensions))))

(defn similarity [a b]
  `(~'similarity ~a ~b))

;; 1. Pick a random hypervector a
;; 2. Pick 10.000 random hypervectors (n)
;; 3. Find the similarity between i = 0...n and a

(def a (wl/! (seed)))

(wh/view-no-summary
 (w/ListPlot
  (w/Table
   `(~'similarity ~a ~'(seed))
   [10000])
  '(->
    AxesLabel ["n" "Similarity"])))


;; - In fact, you can pick and pick and keep picking and the similarity will be around 0.5.
;; - The universe will run out of time before it runs out of hypervectors.
;; - Hyperspace is 'seemingly infinite', this to me is an interesting property for a cogntive datatype.
;; - I can recommend Kanerva 2009 and 'Sparse Distributed Memory' (1998) for more.
;;

;; ----------------------

(wh/view-no-summary*
 (w/ListPlot (w/Table (similarity (seed) (seed)) 10))
 false)


;; ------------------------

;; # Item Memory


;; Bookkeeping to go between HD/VSA domain and symbolic domain.

;; similiraty above 0.15 is almost certainly 'related' | 0.45 difference
(def default-threshold 0.15)

(defn ->item-memory [] {})

(defn cleanup-1
  [mem x threshold]
  (let [items (into [] mem)
        hdvs (into [] (map val) items)
        ;; perf bottleneck, guessing the datatrensfers
        similarities (wl/! (w/Map (w/fn [sim]
                                    (w/Divide sim dimensions))
                                  (w/Dot hdvs x)))]
      (->> (map-indexed vector similarities)
           (filter (comp #(<= threshold %) second))
           (sort-by second (fn [a b] (compare b a)))
           (map (fn [[index similarity]]
                  {:hdv (hdvs index)
                   :obj (key (items index))
                   :similarity similarity})))))

(defn cleanup*
  ([mem x] (cleanup* mem x default-threshold))
  ([mem x threshold] (map :obj (cleanup-1 mem x threshold))))

(defn store-item [mem x]
  (assoc mem x (wl/! (seed))))

;; ------

(defonce item-memory (atom (->item-memory)))

(defn book-keep!
  [x]
  (or (@item-memory x)
      (get (swap! item-memory store-item x) x)))

(defn item-cleanup [hdv]
  (cleanup* @item-memory hdv default-threshold))

;; ------

;; # Vector Symbolic Arithmetic (VSA)

;;
;; Asking what could the ALU operations of this hyperdimensional computer be.
;;
;; These operations return the same datatype as the inputs, hypervectors of the same length.
;; In programming, we say the VSA is closed under the hypervector.
;;


;; ## Superposition

;; A binary operation that returns a hypervector that is similar to its inputs.
;; It is the collection of the inputs.
;;
;;
;; I imagine overlapping 2 see-through pieces of paper, the outcome is the superposition.
;;
;; Since neurons have sparse activation, the superposition of 2 neuronal ensembles could just be the combined activation.
;;

(wl/! (w/_= 'superposition 'Plus))

(defn superposition
  "`superposition` returns a hdv that is the multiset
  of the input `hdvs`.

  The resulting hdv is similar in equal meassure to all it's inputs.

  Set membership can be tested with [[similarity]].

  Like `+`, superposition is associative and commutative.

  Superposition distributes over permutation.

  Alias `bundle`.
  "
  [& hdvs]
  `(~'superposition ~@hdvs))

;; ### Null

;; The null element of the superposition operation is a zero hypervector.

(wl/!
 (w/_= 'zeros
       (w/fn [] (w/Table 0 [dimensions]))))

(defn zeros
  "Returns a zero hypervector.
   This is the null element of the superposition operation."
  []
  (list 'zeros))

;; ### Negative

;;
;; The reverse of superposition is the superposition with the negative hdv (substraction).
;;

(wl/! (w/_= 'negative 'Minus))

(defn negative
  "returns a hdv that is the negative of the input hdv."
  [hdv]
  `(~'negative ~hdv))

(let [a (wl/! (seed))]
  (wl/! (similarity a (negative a))))

(let [a (wl/! (seed))
      b (wl/! (seed))]
  (wl/!
   (w/==
    a
    (superposition
     (superposition a b)
     (negative b)))))


;; ## Bind

;;
;;
;; - The other binary operation, it returns a hypervector that is *dissimilar* to its inputs.
;; - It is possible to recover the other input, given one input and the output.
;; - Therefore, 'bind' can be used to represent a key-value pair.
;;

(wl/! (w/_= 'bind 'Times))

(defn bind
  "returns a hdv that is different from the inputs.

  The inverse of bind is binding with the 'inverse' vector.
  In MAP, this is the same as the original vector.
  (In MAP, bind is the inverse of itself.)

  The bind represents a `key-value-pair` of the inputs.

  Bind is associative and commutative.
  Bind distributes over superposition and permutation.
  Bind preserves similarity.
  "
  [& hdvs]
  `(~'bind ~@hdvs))

(let [a (wl/! (seed))
      b (wl/! (seed))
      c (wl/! (seed))]
  [
   ;; Revocer input
   (wl/! (w/== a (bind (bind a b) b)))

   ;; Commutative
   (wl/! (w/== (bind a b) (bind b a)))

   ;; Associative
   (wl/! (w/== (bind (bind a b) c) (bind (bind a b) c)))

   ;; Distributive over superposition
   (wl/! (w/== (superposition (bind a c) (bind b c))
               (bind c (superposition a b))))

   ;; preserve similarity
   ;; It is like `a`, `b` and their relationship are perfectly mirrored in the `c` domain.
   (wl/!
    (w/==
     (similarity a b)
     (similarity (bind a c) (bind b c))))])


;; ### Identity

;; The identity element of the bind operation is the identity hypervector.

;; For MAP, this is an hdv where all bits are 1.

(wl/! (w/_= 'identityHdv
            (w/fn [] (w/Table 1 [dimensions]))))

(defn identity-hdv []
  (list 'identityHdv))

;; binding a hdv with itself yields the identity hdv.
(wl/! (w/== (identity-hdv) (bind a a)))

;; binding a hdv with the identity hdv yields the hdv itself.
(wl/! (w/== a (bind a (identity-hdv))))


;; {`H`, `bind`, `identity-hdv`} and {`H`, `superposition`, `zeros`} are commutative monoids.

;; The whole thing together with `inverse` is also a ring or something.


;; ### Inverse

(wl/! (w/_= 'inverse w/Identity))

(defn inverse
  "`inverse` returns the inverse of the input hdv.

  Bind with the inverse hdv is equivalent to unbind with the hdv.

  (this is trivial in MAP, bind is it's own inverse, and a hdv is it's own inverse)."
  [hdv]
  `(~'inverse ~hdv))

(let [a (wl/! (seed))
      b (wl/! (seed))]
  (= b (wl/! (bind (bind a b) (inverse a)))))


(def kvp
  (wl/! (bind (book-keep! :a)
              (book-keep! :b))))

(wl/!
 (similarity
  (book-keep! :b)
  (bind kvp (book-keep! :a))))

(wl/!
 (similarity
  (book-keep! :a)
  (bind kvp (book-keep! :a))))

;; Bind can be thought of returning a point b 'in the domain of' the input a.

(let [m (wl/!
         (bind
          (book-keep! :green-domain)
          (superposition
           (book-keep! :a)
           (book-keep! :b)
           (book-keep! :c))))]
  [(float (wl/! (similarity m (book-keep! :c))))

   ;; Ask given :c 'in the the green-domain', is it contained in m?
   (float
    (wl/! (similarity m
                      (bind
                       (book-keep! :green-domain)
                       (book-keep! :c)))))])



;; ----------


;; ## Permute

;; shift / roll the bits of a hdv

(wl/! (w/_= 'permute (w/fn [hdv n] (w/RotateLeft hdv n))))

(defn permute
  "`permute` returns a hdv that is the input hdv rotated by n bits.
  With second arg `n`, permute this number of times.

  This is used to create a point from `hdv` in an unrelated domain.

  - to create a quotation
  - to use as position marker in a sequence
  - to implement a non-commutative bind

  Alias `protect` / `unprotect`.

  Inverse [[permute-inverse]].
  "
  ([hdv]
   (permute hdv 1))
  ([hdv n]
   `(~'permute ~hdv ~n)))

(defn permute-inverse
  "`permute-inverse` returns a hdv that is the input hdv rotated by -n bits.
  See [[permute]].
  "
  ([hdv]
   (permute-inverse hdv 1))
  ([hdv n]
   (permute hdv (- n))))

;; This is sort of subtle but powerful.

;; A permuted hdv is unrelated to the original hdv.
;; We say permute is 'randomizing' the hdv, making it unrelated to hdvs of the input domain.

(wl/! (similarity a (permute a)))

(wl/! (similarity a (permute-inverse (permute a))))

;; Using non-commutative bind, we can represent directed edges for a directed graph.

(defn non-commutative-bind
  ([a b]
   (bind a (permute b))))

(defn directed-edge
  "Given a tail and a head, return a directed edge."
  [tail head]
  (non-commutative-bind tail head))

(defn edge-head
  "Given the tail and a directed edge, return the head of the edge."
  [edge tail]
  (bind edge (permute tail)))

(defn edge-tail
  "Given the head and a directed edge, return the tail of the edge."
  [edge head]
  (permute-inverse (bind edge head)))

(defn directed-graph
  [edges]
  (apply superposition
         (map (fn [[tail head]] (directed-edge tail head)) edges)))

(let [g (directed-graph [[(book-keep! :a) (book-keep! :b)]
                         [(book-keep! :b) (book-keep! :c)]
                         [(book-keep! :c) (book-keep! :a)]])]
  (item-cleanup
   (edge-tail g (book-keep! :a))))

;; ---------------------------------

;; # Examples with Cognitive Connotations

;; ## What is the Dollar in Mexico?

(defn record [m]
  (apply superposition
         (map (fn [[k v]] (bind k v)) m)))

(defn ->record
  [m]
  (wl/! (record (map #(mapv book-keep! %) m))))

(def usa-record
  (->record
   {:country :usa
    :capital :washington
    :currency :dollar}))

(def mexico-record
  (->record
   {:capital :mexico-city
    :country :mexico
    :currency :peso}))

(def query-outcome
  (wl/!
   ;; ~ :peso
   (bind
    mexico-record
    ;; ~ :currency
    (bind usa-record (book-keep! :dollar)))))

(take 10 query-outcome)

(map
 #(select-keys % [:obj :similarity])
 (cleanup-1 @item-memory query-outcome 0.2))


;; ----------------------

;; Superposition enables 'programming in superposition'.

(let [rec (wl/!
           (record
            [[(book-keep! :x) (book-keep! 1)]
             [(book-keep! :y) (book-keep! 2)]
             [(book-keep! :z) (superposition
                               (book-keep! 1)
                               (book-keep! 2)
                               (book-keep! 3))]]))]


  [
   ;; the value in the :x domain is 1
   (item-cleanup (bind rec (book-keep! :x)))
   ;; the value in the :y domain is 2
   (item-cleanup (bind rec (book-keep! :y)))
   ;; the value in the :z domain is the superposition of 1,2,3
   (item-cleanup (bind rec (book-keep! :z)))])


;; Programming in superposition...

;; - A collection can be treated like an element.
;; - Everything is a set.
;; - ambiguity / continuity is first class.


;; -------
;; Dot similarity explorations:

(let [a (wl/! (seed))]
  (wl/! (similarity a a)))


(let [a (wl/! (seed))
      b (wl/! (seed))
      c (wl/! (seed))]
  (float
   (wl/!
    (similarity a (w/Plus a b c)))))

(let [a (wl/! (seed))
      b (wl/! (seed))
      c (wl/! (seed))]
  (float
   (wl/!
    (similarity
     (w/Plus a b c)
     (w/Plus a b c)))))

(let [a (wl/! (seed))
      b (wl/! (seed))
      c (wl/! (seed))]
  (float
   (wl/!
    (similarity
     (normalize (w/Plus a b c))
     (normalize (w/Plus a b c))))))

(let [a (wl/! (seed))
      b (wl/! (seed))
      c (wl/! (seed))]
  (float
   (wl/!
    (similarity
     a
     (normalize (w/Plus a b c))))))


^:kindly/hide-code
(comment
  (book-keep! :a)
  (book-keep! :b)
  (book-keep! :c)

  (def ab
    (wl/! (bind (book-keep! :a)
                (book-keep! :b))))

  (wl/!
   (similarity
    (book-keep! :b)
    (bind ab (book-keep! :a))))

  (wl/!
   (similarity
    (book-keep! :a)
    (bind ab (book-keep! :a)))))




^:kindly/hide-code
(comment
  (def mem (->item-memory))
  (def mem (store-item mem :f))
  (def mem (store-item mem :a))
  (def mem (store-item mem :b))

  (cleanup* mem (val (first mem)) 0.2)
  (cleanup* mem (val (first mem)) 0.2)
  (first (cleanup* mem (val (first mem)) 0.2)))


^:kindly/hide-code
(comment

  (def a (wl/! (seed)))
  (def b (wl/! (seed)))
  (def ab (wl/! (superposition a b)))
  (def c (wl/! (bind a b)))

  ;; unbind (in MAP a hdv is the inverse of itself)
  (wl/! (bind a c))
  (wl/! (similarity (seed) (seed)))
  (wl/! (similarity c b))
  (wl/! (similarity a (bind c b))))


^:kindly/hide-code
(comment
  (def a+b+c
    (wl/!
     (superposition
      (book-keep! :a)
      (book-keep! :b)
      (book-keep! :c))))

  (wl/! (w/_=
         'cosSimilarity
         (w/fn [a b]
           (w/N (w/Subtract 1 (w/CosineDistance a b))))))

  (defn cos-similarity [a b]
    `(~'cosSimilarity ~a ~b))

  (wl/! (cos-similarity a+b+c (seed)))
  (wl/! (cos-similarity a+b+c (book-keep! :a)))

  (wl/! (similarity a+b+c (book-keep! :a)))

  ;; commutativity experiment
  (for [_n (range 10)]
    (let [hdvs [(seed) (seed)]
          hdvs (wl/! hdvs)]
      (for [_n (range 5)]
        (= (wl/! (apply bind (shuffle hdvs)))
           (wl/! (apply bind (shuffle hdvs)))))))
  (let [hdvs [(seed) (seed) (seed) (seed) (seed)]
        hdvs (wl/! hdvs)]
    (apply =
           (for [_ (range 5)] (wl/! (apply superposition (shuffle hdvs)))))))

^:kindly/hide-code
(comment
  (reset! item-memory (->item-memory))
  (wl/start!)
  ((requiring-resolve 'scicloj.clay.v2.api/make!)
   {:source-path "notebooks/intro2.clj"}))
