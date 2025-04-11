(ns map-example
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

;; ----------------

;; ## Multiply-Add-Permute (MAP)




^:kindly/hide-code
(comment

  ((requiring-resolve 'scicloj.clay.v2.api/make!)
   {:source-path "notebooks/map_example.clj"})

  )
