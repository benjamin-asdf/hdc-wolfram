(ns build
  "Project build config as code for clojure's tools.build"
  (:require [build-config]
            [clojure.tools.build.api :as b]
                                        ;[scicloj.clay.v2.api :as clay] -> dynamic require, due to https://github.com/scicloj/clay/issues/98
            [clojure.edn :as edn]
            [clojure.string :as str]
            [clojure.java.process :as p]))

(def lib 'hdc-wolfram/lib)
(def version
  (str/trim (p/exec "git" "rev-parse" "--short" "HEAD")))

(def class-dir "target/classes")
(def basis (b/create-basis {:project "deps.edn"}))
(def jar-file (format "target/%s-%s.jar" (name lib) version ))

(defn clean [_]
  (b/delete {:path "target"}))

(defn build-site [opts]
  (println "Going to build docs ...")
  ((requiring-resolve 'wolframite.core/start!))
  ((requiring-resolve 'scicloj.clay.v2.api/make!)
   (assoc build-config/config
          :clean-up-target-dir true
          :show false))
  opts)

(comment
  (build-site {}))
