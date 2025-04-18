(ns index
  "Entrypoint for the Clay-generated docs -> docs/generated/index.html"
  (:require
    [scicloj.kindly.v4.kind :as kind]
    [scicloj.kindly.v4.api :as kindly]))

^:kindly/hide-code
(defmacro slurp-markdown [path]
  ;; macro so it runs before kindly, which will see the content, not code;
  ;; this is a trick so that we can render our existing README as a part of the clay-generated docs
  (slurp path))

(kindly/hide-code
 (kind/md (slurp-markdown "./Readme.md")))

;; ## Further documentation
;; See the book content menu on the left side
