(ns parse-simian
  (:require [clojure.contrib
	     [json :as json]
	     [io :as io]
	     [command-line :as cl]]
	    [parse-simian.core :as ps]
	    [clojure.string :as str]))

(defn -main [& args]
  (cl/with-command-line args
    "parse an xml file and write graph structure to json"
    [filevec]
    (let [[filename & frest] filevec
	  [rootname & srest] (str/split filename #"\.")]
      (print "var " rootname " = " (json/json-str (ps/parse-simian-report (io/input-stream filename))) ";"))))

