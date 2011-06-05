(ns parse-simian
  (:use [clojure.contrib.json :only [json-str pprint-json]]
	[clojure.contrib.io :only [input-stream]]
  	[clojure.contrib.command-line :only [with-command-line]]
	[parse-simian.core :only [parse-simian-report]])
  (:require [clojure.string :as str]))


(defn -main [& args]
  (with-command-line args
    "parse an xml file and write graph structure to json"
    [filevec]
    (let [[filename & frest] filevec
	  [rootname & srest] (str/split filename #"\.")]
      (print "var " rootname " = " (json-str (parse-simian-report (input-stream filename))) ";"))))

