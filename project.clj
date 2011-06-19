(defproject parse-simian "1.0.0-SNAPSHOT"
  :description "FIXME: write"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
		 [midje/midje "1.1.1"]
		 [compojure "0.6.3"]]
  :dev-dependencies [[swank-clojure "1.2.1"]
		     [lein-ring "0.4.0"]]
  :ring {:handler parse-simian.server/server}
  :main parse-simian.main)
