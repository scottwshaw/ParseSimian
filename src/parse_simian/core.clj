(ns parse-simian.core
  (:use clojure.contrib.json
	[clojure.contrib.string :only [as-str]]
  	[clojure.walk :only [postwalk-replace postwalk]]
	[clojure.contrib.io :only [input-stream]])
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
  (if (contains? hash clazz) (assoc hash clazz (+ count (get hash clazz)))
      (assoc hash clazz count)))

(defn extract-map-of-total-duplicated-lines [input]
  (reduce increment-linecount-hash '{} (partition 2 (extract-seq-of-block-linecounts input))))

(defn parse-simian-report [input]
  (let [linecount-seq (seq (extract-map-of-total-duplicated-lines input))
	relationship-seq (extract-seq-of-relationships input)]
    (loop [[[clazz lc] & restlc] linecount-seq
	   rs relationship-seq
	   node-counter 0
	   node-seq []]
      (if (nil? lc)
	{:nodes node-seq :links (map (fn [[s t]] {:source s :target t :value 1}) rs)}
	(recur restlc
	       (postwalk-replace {clazz node-counter} rs)
	       (inc node-counter)
	       (cons {:nodename clazz :group :class :size lc} node-seq))))))

(unfinished expand-pairs-from-set)
(unfinished zipper-from-xml-input)

(defn zipper-from-xml-input [input-xml]
  (zip/xml-zip (xml/parse (clojure.contrib.io/input-stream (.getBytes input-xml)))))

(defn class-from-source-file-attr []
  (fn [loc] (to-qualified-classname (zx/attr loc :sourceFile) path-prefix)))

(defn extract-seq-of-duplication-sets [input-xml]
  (let [sim-zip (zipper-from-xml-input input-xml)]
    (map #(zx/xml-> % :block (class-from-source-file-attr)) (zx/xml-> sim-zip :check :set))))

(defn extract-seq-of-relationships [input-xml]
  (let [sets (extract-seq-of-duplication-sets input-xml)]
    (mapcat expand-pairs-from-set sets)))

;;
;; This function increments the hash for a single value in the vector
(defn parse-and-write-graph-as-json [input-xml]
  (pprint-json (parse-simian-report input-xml)))
