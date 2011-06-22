(ns parse-simian.main
  (:use parse-simian.json-mods) ; this class overrides some behavior on clojure.contrib.json
  (:require [clojure.contrib
	     [command-line :as cl]
	     [io :as io]
	     [json :as json]
	     [string :as str]]
	    [parse-simian.core :as ps])
  (:import (java.io PrintWriter)))

(defn -main [& args]
  (cl/with-command-line args
    "parse an xml file and write graph structure as json"
    [filevec]
    (let [[filename & frest] filevec
	  [rootname & srest] (str/split  #"\." filename)]
      (print "var " rootname " = " (json/json-str (ps/parse-simian-report (io/input-stream filename))) ";"))))
