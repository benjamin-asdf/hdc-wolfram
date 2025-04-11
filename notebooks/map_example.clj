(ns map-example
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [wolframite.api.v1 :as wl]
            [wolframite.core :as wc]
            [wolframite.lib.helpers :as h]
            [wolframite.runtime.defaults :as defaults]
            [wolframite.tools.hiccup :as wh]
            [wolframite.wolfram :as w :refer :all :exclude
             [* + - -> / < <= = == > >= fn Byte Character Integer Number Short
              String Thread]]
            [scicloj.kindly.v4.kind :as k]
            [scicloj.clay.v2.api :as clay]))


;; Multiply-Add-Permute (MAP)
;; https://github.com/benjamin-asdf/hdc-wolfram/issues/4
