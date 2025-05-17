^:kindly/hide-code
(ns nn
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

;; https://reference.wolfram.com/language/tutorial/NeuralNetworksIntroduction.html

;;
;;
;; MNIST

^:kindly/hide-code
(comment
  (wl/!
   (w/do
     (w/= 'trainingData (w/ResourceData "MNIST" "TrainingData"))
     nil))

  (wl/!
   (w/do
     (w/= 'testData (w/ResourceData "MNIST" "TestData"))
     nil))



  (wh/view
   (w/Column
    (w/RandomSample 'trainingData 5))))

(comment
  (wl/!
   (w/do
     (w/= 'lenetModel
          (w/NetModel "LeNet Trained on MNIST Data"))
     nil))

  (first (wl/! (w/Keys (w/RandomSample 'testData 5))))

  (let
      [elms (wl/! (w/Keys (w/RandomSample 'testData 5)))
       classifier-output (wl/! (list 'lenetModel elms))]
      (wh/view
       (w/Column
        [(w/Column elms)
         (w/Column classifier-output)]))))


;; ----------------------------

(comment
  (wl/!
   (w/do
     (w/= 'myLenetModel
          (w/NetTrain
           (w/NetModel "LeNet")
           'trainingData
           '(-> ValidationSet testData)))
     nil)))

^:kindly/hide-code
(comment
  (wl/stop!)
  (wl/start!))
