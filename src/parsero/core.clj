(ns parsero.core
  (:require [instaparse.core :as instaparse]))

(def grammar "
")

(def parser (instaparse/parser grammar))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))
