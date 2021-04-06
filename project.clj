(defproject katas "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [org.clojure/test.check "0.9.0"]
                 [bigml/sketchy       "0.4.2"]]
  :main katas.core
  :repl-options {:init-ns katas.core})
