(ns parse-simian
  (:use [clojure.contrib.json :only [json-str pprint-json]]
	[clojure.contrib.io :only [input-stream]]
  	[clojure.contrib.command-line :only [with-command-line]]
	[parse-simian.core :only [parse-simian-report]]))

(defn -main [& args]
  (with-command-line args
    "parse an xml file and write graph structure to json"
    [filevec]
    (print (json-str (parse-simian-report (input-stream (first filevec)))))))

