(ns parse-simian.core
  (:use clojure.contrib.json
	[clojure.contrib.string :only [as-str]]
  	[clojure.walk :only [postwalk-replace postwalk]]
	[clojure.contrib.io :only [input-stream]])
  (:use [midje.sweet :only [unfinished]]) ; only a dev dependency
  (:require (clojure [xml :as xml] [zip :as zip])
	    [clojure.contrib.zip-filter.xml :as zx]
	    [clojure.contrib.str-utils2 :as str])
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

(defn zipper-from-xml-input-stream [input-xml-stream]
  (zip/xml-zip (xml/parse input-xml-stream)))

(defn assoc-class-with-linecount [loc]
  [(to-qualified-classname (zx/attr loc :sourceFile) path-prefix) (Integer. (zx/attr (zip/up loc) :lineCount))])

(defn expand-pairs-from-set [seq-of-classes]
  (loop [[first-class & rest-classes] seq-of-classes
	 seq-of-pairs []]
    (if (nil? rest-classes)
      seq-of-pairs
      (recur rest-classes
	     (reduce (fn [pair-seq-inner next-item] (cons [first-class next-item] pair-seq-inner))
		     seq-of-pairs rest-classes)))))

(defn class-from-source-file-attr []
  (fn [loc] (to-qualified-classname (zx/attr loc :sourceFile) path-prefix)))

;;; These next 2 functions are the core.
;;; This one extracts the files across which duplication occurs
(defn extract-seq-of-duplication-sets [sim-zip]
  (map #(zx/xml-> % :block (class-from-source-file-attr)) (zx/xml-> sim-zip :check :set)))

;;; This one extracts the number of lines duplicated in each duplication set
(defn extract-seq-of-block-linecounts [sim-zip]
  (zx/xml-> sim-zip :check :set :block assoc-class-with-linecount))

;;; input is a zipper created from simian xml output
(defn extract-seq-of-relationships [sim-zip]
  (let [sets (extract-seq-of-duplication-sets sim-zip)]
    (mapcat expand-pairs-from-set sets)))

(defn increment-linecount-hash [hash [clazz count]]
  (if (contains? hash clazz) (assoc hash clazz (+ count (get hash clazz)))
      (assoc hash clazz count)))

(defn extract-map-of-total-duplicated-lines [input-zip]
  (reduce increment-linecount-hash '{} (partition 2 (extract-seq-of-block-linecounts input-zip))))

(defn parse-simian-report [input-stream]
  (let [sim-zip (zipper-from-xml-input-stream input-stream)
	linecount-seq (seq (extract-map-of-total-duplicated-lines sim-zip))
	relationship-seq (extract-seq-of-relationships sim-zip)]
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
