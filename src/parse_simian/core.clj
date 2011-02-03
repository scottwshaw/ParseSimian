(ns parse-simian.core
  (:use clojure.contrib.json
	[clojure.contrib.string :only [as-str]])
  (:use [midje.sweet :only [unfinished]]) ; only a dev dependency
  (:require (clojure [xml :as xml] [zip :as zip])
	    [clojure.contrib.zip-filter.xml :as zx])
  (:require [clojure.contrib.str-utils2 :as str])
  (:import (java.io PrintWriter PushbackReader StringWriter StringReader Reader EOFException)))

;; Hack to get clojure.contrib.json to write arrays without the quotes around keys
(defn- write-json-object-without-quotes [m #^PrintWriter out]
  (.print out \{)
  (loop [x m]
    (when (seq m)
      (let [[k v] (first x)]
        (when (nil? k)
          (throw (Exception. "JSON object keys cannot be nil/null")))
;        (.print out \")
        (.print out (as-str k))
;        (.print out \")
        (.print out \:)
        (write-json v out))
      (let [nxt (next x)]
        (when (seq nxt)
          (.print out \,)
          (recur nxt)))))
  (.print out \}))

;; This overrides the extension for Map objects on the protocol Write-JSON
;; Yikes!  this appears brittle
(extend java.util.Map Write-JSON
        {:write-json write-json-object-without-quotes})

(defn to-qualified-classname [file-path prefix]
  (str/replace (str/replace (str/replace file-path prefix "") "/" ".") ".java" ""))

(def path-prefix "/Users/avombatk/projects/healthcheck/build/src/")

(defn assoc-class-with-linecount [loc]
  [(to-qualified-classname (zx/attr loc :sourceFile) path-prefix) (Integer. (zx/attr (zip/up loc) :lineCount))])

;; Takes a filename or input-stream
(defn extract-seq-of-block-linecounts-untested [input]
  (let [sim-zip (zip/xml-zip (xml/parse input))]
    (zx/xml-> sim-zip :check :set :block assoc-class-with-linecount)))

(unfinished extract-seq-of-block-linecounts)
(unfinished extract-seq-of-relationships)

(defn increment-linecount-hash [hash [clazz count]]
  (cond (contains? hash clazz) (assoc hash clazz (+ count (get hash clazz)))
	true (assoc hash clazz count)))

(defn extract-map-of-total-duplicated-lines [input]
  (reduce increment-linecount-hash '{} (partition 2 (extract-seq-of-block-linecounts input))))


(defn parse-simian-report [input]
  (let [linecount-map (extract-map-of-total-duplicated-lines input)
	relationship-seq (extract-seq-of-relationships input)]
    false))

;;
;; This function increments the hash for a single value in the vector

(defn parse-and-write-graph-as-json [input-xml]
  (pprint-json (parse-simian-report input-xml)))
