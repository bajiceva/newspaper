(defproject newspaper "0.1.0-SNAPSHOT"
  :description "Newspaper"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.1.5"]
                 [ring/ring-core "1.2.0"]
                 [hiccup "1.0.4"]
                 [ring/ring-jetty-adapter "1.2.0"]
                 [congomongo "0.4.1"]
                 [amalloy/mongo-session "0.0.2"]
                 [lib-noir "0.7.0"]
                 [clj-time "0.6.0"]]
  
  :main newspaper.core)