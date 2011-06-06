(ns parse-simian.main
  (:require [clojure.contrib
	     [json :as json]
	     [io :as io]
	     [command-line :as cl]
	     [string :as str]]
	    [parse-simian.core :as ps])
  (:import (java.io PrintWriter PushbackReader StringWriter StringReader Reader EOFException)))

;; Hack to get clojure.contrib.json to write arrays without the quotes around keys
(defn- write-json-object-without-quotes [m #^PrintWriter out]
  (.print out \{)
  (loop [x m]
    (when (seq m)
      (let [[k v] (first x)]
        (when (nil? k)
          (throw (Exception. "JSON object keys cannot be nil/null")))
        (.print out (str/as-str k))
        (.print out \:)
        (json/write-json v out))
      (let [nxt (next x)]
        (when (seq nxt)
          (.print out \,)
          (recur nxt)))))
  (.print out \}))

;; This overrides the extension for Map objects on the protocol Write-JSON
;; Yikes!  this appears brittle
(extend java.util.Map json/Write-JSON
        {:write-json write-json-object-without-quotes})

(defn -main [& args]
  (cl/with-command-line args
    "parse an xml file and write graph structure to json"
    [filevec]
    (let [[filename & frest] filevec
	  [rootname & srest] (str/split  #"\." filename)]
      (print "var " rootname " = " (json/json-str (ps/parse-simian-report (io/input-stream filename))) ";"))))

