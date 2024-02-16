(defproject undertow-clj "0.1.0-SNAPSHOT"
  :description "undertow server, handlers, and utils for using undertow"
  :license {:name "Apache License Version 2.0"
            :url "https://www.apache.org/licenses/LICENSE-2.0.txt"}
  :dependencies [[com.github.seancfoley/ipaddress "5.4.2"]
                 [hato "0.9.0"]
                 [io.undertow/undertow-core "2.3.11.Final"]
                 [org.clojure/clojure "1.10.3"]
                 [org.clojure/java.data "1.1.103"]]
  :repl-options {:init-ns undertow-clj.core})
