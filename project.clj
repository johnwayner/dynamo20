(defproject dynamo20 "1.0.0-SNAPSHOT"
  :description "Dynamo20 - A very simple hack to set a No-Ip host address"
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [clj-http "0.5.3"]
                 [org.clojure/tools.cli "0.2.2"]]
  :aot [dynamo20.core]
  :main dynamo20.core)