(ns parse-simian.core
  (:use clojure.contrib.json)
  (:use [midje.sweet :only [unfinished]]) ; only a dev dependency
  (:require (clojure [xml :as xml] [zip :as zip])
	    [clojure.contrib.string :as str]
	    [clojure.contrib.zip-filter.xml :as zx]
	    [clojure.contrib.str-utils2 :as str2]
	    [clojure.contrib.combinatorics :as combi]
	    [clojure.walk :as walk])
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
  (str2/replace (str2/replace (str2/replace file-path prefix "") "/" ".") ".java" ""))

(def path-prefix "/Users/avombatk/projects/healthcheck/build/src/")

(defn zipper-from-xml-input-stream [input-xml-stream]
  (zip/xml-zip (xml/parse input-xml-stream)))

(defn- assoc-class-with-linecount [loc]
  [(to-qualified-classname (zx/attr loc :sourceFile) path-prefix) (Integer. (zx/attr (zip/up loc) :lineCount))])

(defn expand-pairs-from-set [seq-of-classes]
  "finds all the distinct pairs that aren't a class and itelf"
  (distinct
   (filter (fn [[item1 item2]] (not (= item1 item2)))
	   (combi/combinations seq-of-classes 2))))

(defn extract-seq-of-duplication-sets [sim-zip]
  "extracts the files across which duplication occurs"
  (let [class-from-file-attr
	(fn [loc] (to-qualified-classname (zx/attr loc :sourceFile) path-prefix))]
    (map #(zx/xml-> % :block class-from-file-attr) (zx/xml-> sim-zip :check :set))))

(defn extract-seq-of-block-linecounts [sim-zip]
  "extracts the number of lines duplicated in each duplication set"
  (zx/xml-> sim-zip :check :set :block assoc-class-with-linecount))

(defn extract-seq-of-relationships [sim-zip]
  "input is a zipper created from simian xml output"
  (let [sets (extract-seq-of-duplication-sets sim-zip)]
    (mapcat expand-pairs-from-set sets)))

(defn increment-linecount-hash [hash [clazz count]]
  "used in a reduce to either add a new class to the hash or increment the count for a class already present"
  (if (contains? hash clazz) (assoc hash clazz (+ count (get hash clazz)))
      (assoc hash clazz count)))

(defn extract-map-of-total-duplicated-lines [input-zip]
  "for each file, computes and stores the total number of lines duplicated"
  (reduce increment-linecount-hash '{} (partition 2 (extract-seq-of-block-linecounts input-zip))))

(defn package-name-from-class [fully-qualified-class]
  (str/replace-re #"\.\w+$" "" fully-qualified-class))

(defn parse-simian-report [xml-input-stream]
  (let [sim-zip (zipper-from-xml-input-stream xml-input-stream)
	linecount-seq (seq (extract-map-of-total-duplicated-lines sim-zip))
	relationship-seq (extract-seq-of-relationships sim-zip)]
    (loop [[[clazz lc] & restlc] linecount-seq
	   rs relationship-seq
	   node-counter 0
	   node-seq []]
      (if (nil? lc)
	{:nodes node-seq :links (map (fn [[s t]] {:source s :target t :value 1}) rs)}
	(recur restlc
	       (walk/postwalk-replace {clazz node-counter} rs)
	       (inc node-counter)
	       (cons {:nodeName clazz :group (package-name-from-class clazz) :size lc} node-seq))))))
