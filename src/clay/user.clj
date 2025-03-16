(ns clay.user
  (:require
   [scicloj.clay.v2.api :as clay]))

(defn user-clay-make! []
  (clay/make! {:source-path "notebooks/"}))

(intern 'user 'user-clay-make! user-clay-make!)

(comment
  (user-clay-make!)
  (clay/browse!))
