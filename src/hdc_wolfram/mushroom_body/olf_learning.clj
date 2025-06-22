(ns hdc-wolfram.bsbc
  (:require
   [wolframite.api.v1 :as wl]
   ;; [wolframite.lib.helpers :as h]
   [wolframite.runtime.defaults]
   [wolframite.tools.hiccup :as wh]
   [wolframite.wolfram :as w]
   ;; [scicloj.clay.v2.api :as clay]
   [wolframite.impl.wolfram-syms.intern :as wi]))



;;
;; Paper:
;;
;; https://www.cell.com/current-biology/fulltext/S0960-9822(16)31288-X#mmc1
;;






;; - Projection Neurons (PNs)
;; - Kenyon Cells (KCs)
;; - PN-KC connectivity matrix (either plastic or not)
;; - downstream readers: extrinsic neurons (ENs)
;; - EN-KC connectivity matrix (plastic)
;;




;;
;;
;;
;;  address register              PN-KC connectivity matrix
;;                                = pn-kc-synapses
;;
;;   PN
;;  +---+                         +---------------+
;;  | 0 |                         | 0 1 0 0 0 0 0 |  rows: PN
;;  | 0 |                         | ...           |  columns: KC,   KC-count: ~ 170.000
;;  | 1 |                         |               |
;;  | . | ----------------------> |               |
;;  | . |                         |               |
;;  +---+                         +---------------+  PN-count x KC-count
;;                                  |
;;   activations                    |
;;   PN-count: ~ 800 - 900          |
;;                                  |  inner product
;;                                  v
;;                               +----------------+
;;                               |  2 0 0 1 ...   |  `kc-inputs`     = dot product (pn-activations vector,  PN-KC-connectiviy matrix)
;;                               +----------------+
;;                                  |
;;                                  |
;;                                  | top-p   5%
;;                                  v
;;                               +----------------+
;;                               |   1 0 0 0 ...  |   `kc-activations` |  output register
;;                               +----------------+
;;                                                   👉 is a sparse hypervector / a neuronal ensemble
;;
;;                                     |
;;                                     |
;;                                     |
;;                                     v
;;                                downstream readers
;;
;;                               ...
;;
;;

;;
;; # Preference readout
;;
;;
;;
;;                               +--------------------+
;;   kc-activations -----------> |                    |    KC-EN connectivity matrix
;;                               |                    |
;;                               +--------------------+
;;                                          |              EN-count = 2         (?)
;;                                          |
;;                                          | inner prouct
;;                                          v
;;                              +---------------------+
;;                              |  200      ,    500  |   en-inputs = dot product(kc-activations vector, KC-EN-connectivity matrix)
;;                              +---------------------+
;;                                                        extrinsic neurons

;;                                EN+         EN-
;;
;;                               preference:   EN+ - EN-
;;
;;
;;
;;




;;
;; # Reward pathway / plasticity
;;
;;
;; - reward neuron
;; - plasticity at the pn-kc synapses
;; - learning related plasticity at the kc-en synapses
























;;
;; TODO
;;
;; # Mushroom body               | Sparse Distributed Memory
;;
;;
;;
;; - sensor-pn synapses  ?       | address decoder
;; - pn activations              | address decoder activations
;; - pn-kc synapses matrix       | content madrix
;;
;; - kc-count                    | word length
;; - kc sparsenes                | content word sparseness


;; missing:
;; - input register
;;
;; - conditionend stimulus ?     | input word
;;








;;
;; # Inhibition Model
;;

;; ## top-p:
;;
;; - calculate synaptic inputs for each neuron
;; - say the top `p` percent are *activated*
;;
;; Alternatives: top-k
;;
;; ## dynamic top-p:
;;
;; - 👉 Braitenberg (1978), G. Palm (2022) suggest dynamic inhibition + iterative lookups
;;      as search process mechanisms, dubbed 'thought pumps'.
;;

;;
;; ## Simple inh. models are sufficient.
;;
;; Conjecture:
;; - you don't need to model spiking interneurons to model a global inhibition

;; Quote:
;;
;; > We simulate feedback inhibition by selecting 5% of Kenyon cells that receive the largest summed inputs and we label these Kenyon cells as activated for each stimulus (Supplemental Experimental Procedures). To verify the efficiency of this implementation, we compare the sets of activated Kenyon cells so generated with the ones by a spiking network with a global feedback inhibition, showing that the two are almost identical (Figure S2A), indicating that this implementation can adequately substitute a computationally more complex spiking model.




















;; ---


;; Braitenberg V (1978) Cell assemblies in the cerebral cortex. In: Heim R, Palm G (eds) Proceedings Symposium on theoretical approaches to complex systems 1977. Lecture notes in biomathematics 21, Springer, Berlin, pp 171–188
