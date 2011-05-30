(ns parse-simian.core-test
  (:use :reload parse-simian.core)
  (:use	clojure.test (incanter core stats charts)
	[midje.sweet :only [fact]]
	[midje.util.checkers :only [map-containing exactly truthy]]))


(use 'clojure.contrib.string)
(import (java.io PrintWriter PushbackReader StringWriter
                    StringReader Reader EOFException BufferedReader))
(use 'clojure.contrib.json)
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

(json-str '{:a :b, :c :d})

(extend java.util.Map Write-JSON
        {:write-json write-json-object-without-quotes})

(defn parse-and-write-graph-as-json [simian-report-file]
  (binding [*out* (new java.io.PrintWriter *out*)]
    (pprint-json {:nodes [{:nodename "au/com/westpac/pda/beans/report/ReportTasksBean.java", :group :class}
			  {:nodename "au/com/westpac/di/transformation/dao/model/IMSSG30510Root.java", :group :class}],
		  :links [{:source 1, :target 0, :value 1}]})))
(use 'clojure.contrib.trace)
(dotrace [write-json-object-without-quotes clojure.contrib.json/pprint-json-object] (parse-and-write-graph-as-json "somefile"))

(use 'clojure.contrib.string)

(as-str :nodes)

(def simple-simian-report "<?xml version=\"1.0\" encoding=\"UTF-8\"?><?xml-stylesheet href=\"simian.xsl\" type=\"text/xsl\"?><!--Similarity Analyser 2.2.24 - http://www.redhillconsulting.com.au/products/simian/index.htmlCopyright (c) 2003-08 RedHill Consulting Pty. Ltd.  All rights reserved.Simian is not free unless used solely for non-commercial or evaluation purposes.--><simian version=\"2.2.24\">    <check ignoreCharacterCase=\"true\" ignoreCurlyBraces=\"true\" ignoreIdentifierCase=\"true\" ignoreModifiers=\"true\" ignoreStringCase=\"true\" threshold=\"6\">        <set lineCount=\"6\">            <block sourceFile=\"/Users/avombatk/projects/healthcheck/build/src/au/com/westpac/pda/beans/report/ReportTasksBean.java\" startLineNumber=\"333\" endLineNumber=\"340\"/>            <block sourceFile=\"/Users/avombatk/projects/healthcheck/build/src/au/com/westpac/pda/beans/cct/CCTTasksBean.java\" startLineNumber=\"187\" endLineNumber=\"194\"/>        </set> <set lineCount=\"7\"> <block sourceFile=\"/Users/avombatk/projects/healthcheck/build/src/au/com/westpac/pda/lodge/LodgementUtilities.java\" startLineNumber=\"175\" endLineNumber=\"182\"/>            <block sourceFile=\"/Users/avombatk/projects/healthcheck/build/src/au/com/westpac/pda/lodge/rebuid/SystemRebuilderImpl.java\" startLineNumber=\"199\" endLineNumber=\"206\"/>    <block sourceFile=\"/Users/avombatk/projects/healthcheck/build/src/au/com/westpac/pda/beans/report/ReportTasksBean.java\" startLineNumber=\"333\" endLineNumber=\"340\"/>    </set><summary duplicateFileCount=\"241\" duplicateLineCount=\"10208\" duplicateBlockCount=\"830\" totalFileCount=\"662\" totalRawLineCount=\"138208\" totalSignificantLineCount=\"60994\" processingTime=\"1173\"/>     </check></simian>")

(require '(clojure [xml :as xml] [zip :as zip]))

(require '[clojure.contrib.zip-filter.xml :as zx])

(require '[clojure.contrib.zip-filter :as z])

(require '[clojure.contrib.io :only [input-stream]])

(zip/xml-zip (xml/parse (clojure.contrib.io/input-stream (.getBytes simple-simian-report))))

(let [sim-zip (zip/xml-zip (xml/parse (clojure.contrib.io/input-stream (.getBytes simple-simian-report))))]
  (zx/xml-> sim-zip :check :set :block #(zip/up %) #(zx/attr % :lineCount)))

(require '[clojure.contrib.str-utils2 :as str])

(defn toClassname [filepath]
  (str/replace (str/replace (str/replace filepath "/Users/avombatk/projects/healthcheck/build/src/" "") ".java" "") "/" "."))

(defn assoc-class-with-linecount-1 [loc]
  [(toClassname (zx/attr loc :sourceFile)) (Integer. (zx/attr (zip/up loc) :lineCount))])
(def countseq
     (let [sim-zip (zip/xml-zip (xml/parse (clojure.contrib.io/input-stream (.getBytes simple-simian-report))))]
       (zx/xml-> sim-zip :check :set :block assoc-class-with-linecount-1)))

(defn class-from-source-file-attr []
  (fn [loc] (toClassname (zx/attr loc :sourceFile))))

(let [sim-zip (zip/xml-zip (xml/parse (clojure.contrib.io/input-stream (.getBytes simple-simian-report))))]
  (map #(zx/xml-> % :block (class-from-source-file-attr)) (zx/xml-> sim-zip :check :set)))


(def myseq [:a 1 :b 1 :b 1 :c 2 :c 2 :d 2 :d 2 :d 2])
(def mymap {:a 1 :b 1 :c 2 :d 2})

(loop [[s & r] (int seq mymap)]
  (println s)
  (if (not (nil? s)) (recur r)))

(defn- increment-linecount-hash [hash [clazz count]]
  (cond (contains? hash clazz) (assoc hash clazz (+ count (get hash clazz)))
	true (assoc hash clazz count)))

simple-simian-report

(println countseq)

(increment-linecount-hash '{} ["class" 6])

(reduce increment-linecount-hash '{} (partition 2 countseq))

(reduce increment-linecount-hash '{} (partition 2 myseq))

(cons '[:y] '[:x])

(reduce pair-up '[] myseq)
    
(map (fn [[source target]] {:source source :target target :value 1}) [[1 2] [3 4] [5 6]])

(def sim-zip [{:tag :simian, :attrs {:version "2.2.24"}, :content [{:tag :check, :attrs {:ignoreCharacterCase "true", :ignoreCurlyBraces "true", :ignoreIdentifierCase "true", :ignoreModifiers "true", :ignoreStringCase "true", :threshold "6"}, :content [{:tag :set, :attrs {:lineCount "6"}, :content [{:tag :block, :attrs {:sourceFile "/Users/avombatk/projects/healthcheck/build/src/au/com/westpac/pda/beans/report/ReportTasksBean.java", :startLineNumber "333", :endLineNumber "340"}, :content nil} {:tag :block, :attrs {:sourceFile "/Users/avombatk/projects/healthcheck/build/src/au/com/westpac/pda/beans/cct/CCTTasksBean.java", :startLineNumber "187", :endLineNumber "194"}, :content nil}]} {:tag :set, :attrs {:lineCount "7"}, :content [{:tag :block, :attrs {:sourceFile "/Users/avombatk/projects/healthcheck/build/src/au/com/westpac/pda/lodge/LodgementUtilities.java", :startLineNumber "175", :endLineNumber "182"}, :content nil} {:tag :block, :attrs {:sourceFile "/Users/avombatk/projects/healthcheck/build/src/au/com/westpac/pda/lodge/rebuid/SystemRebuilderImpl.java", :startLineNumber "199", :endLineNumber "206"}, :content nil} {:tag :block, :attrs {:sourceFile "/Users/avombatk/projects/healthcheck/build/src/au/com/westpac/pda/beans/report/ReportTasksBean.java", :startLineNumber "333", :endLineNumber "340"}, :content nil}]} {:tag :summary, :attrs {:duplicateFileCount "241", :duplicateLineCount "10208", :duplicateBlockCount "830", :totalFileCount "662", :totalRawLineCount "138208", :totalSignificantLineCount "60994", :processingTime "1173"}, :content nil}]}]} nil])

(zx/xml-> sim-zip :check :set :block assoc-class-with-linecount-1)

(seq [:a :b :c])

(json-str (parse-simian-report simple-simian-report))

(use 'clojure.contrib.repl-utils)
(clojure.contrib.repl-utils/show (clojure.contrib.io/reader "simian_report.xml"))

(.readLine (clojure.contrib.io/reader "simian_report.xml"))

(clojure.contrib.repl-utils/show (clojure.contrib.io/input-stream "simian_report.xml"))

