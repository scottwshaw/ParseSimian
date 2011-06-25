(ns parse-simian.core-test
  (:use :reload parse-simian.core)
  (:use	clojure.contrib.json clojure.test)
  (:use midje.sweet)
  (:require (clojure [xml :as xml] [zip :as zip])
	    [clojure.contrib.io :as io]
	    [clojure.contrib.zip-filter.xml :as zx]))

(defn- simple-simian-report-stream []
  (clojure.contrib.io/input-stream (.getBytes
				    "<?xml version=\"1.0\" encoding=\"UTF-8\"?><?xml-stylesheet href=\"simian.xsl\" type=\"text/xsl\"?><!--Similarity Analyser 2.2.24 - http://www.redhillconsulting.com.au/products/simian/index.htmlCopyright (c) 2003-08 RedHill Consulting Pty. Ltd.  All rights reserved.Simian is not free unless used solely for non-commercial or evaluation purposes.--><simian version=\"2.2.24\">  <check ignoreCharacterCase=\"true\" ignoreCurlyBraces=\"true\" ignoreIdentifierCase=\"true\" ignoreModifiers=\"true\" ignoreStringCase=\"true\" threshold=\"6\">    <set lineCount=\"6\">      <block sourceFile=\"/Users/avombatk/projects/healthcheck/build/src/au/com/westpac/pda/beans/report/ReportTasksBean.java\" startLineNumber=\"333\" endLineNumber=\"340\"/>            <block sourceFile=\"/Users/avombatk/projects/healthcheck/build/src/au/com/westpac/pda/beans/cct/CCTTasksBean.java\" startLineNumber=\"187\" endLineNumber=\"194\"/>        </set> <set lineCount=\"7\"> <block sourceFile=\"/Users/avombatk/projects/healthcheck/build/src/au/com/westpac/pda/lodge/LodgementUtilities.java\" startLineNumber=\"175\" endLineNumber=\"182\"/>            <block sourceFile=\"/Users/avombatk/projects/healthcheck/build/src/au/com/westpac/pda/lodge/rebuid/SystemRebuilderImpl.java\" startLineNumber=\"199\" endLineNumber=\"206\"/>    <block sourceFile=\"/Users/avombatk/projects/healthcheck/build/src/au/com/westpac/pda/beans/report/ReportTasksBean.java\" startLineNumber=\"333\" endLineNumber=\"340\"/>    </set><summary duplicateFileCount=\"241\" duplicateLineCount=\"10208\" duplicateBlockCount=\"830\" totalFileCount=\"662\" totalRawLineCount=\"138208\" totalSignificantLineCount=\"60994\" processingTime=\"1173\"/>     </check></simian>")))

(def simple-simian-graph
     {:nodes [{:nodeName "au.com.westpac.pda.beans.report.ReportTasksBean" :group "au.com.westpac.pda.beans.report" :size 13}
	      {:nodeName "au.com.westpac.pda.beans.cct.CCTTasksBean" :group "au.com.westpac.pda.beans.cct" :size 6}
	      {:nodeName "au.com.westpac.pda.lodge.LodgementUtilities" :group "au.com.westpac.pda.lodge" :size 7}
	      {:nodeName "au.com.westpac.pda.lodge.rebuid.SystemRebuilderImpl" :group "au.com.westpac.pda.lodge.rebuid" :size 7}]
      :links [{:source 1 :target 0 :value 1}
	      {:source 1 :target 3 :value 1}
	      {:source 3 :target 2 :value 1}]})

(deftest should-parse-input-xml-and-produce-a-graph-structure
  (fact (parse-simian-report ...input-stream...) => simple-simian-graph
	  (provided (zipper-from-xml-input-stream ...input-stream...) => ...input-zip...
		    (extract-map-of-total-duplicated-lines ...input-zip...) => {"au.com.westpac.pda.lodge.rebuid.SystemRebuilderImpl" 7
										"au.com.westpac.pda.lodge.LodgementUtilities" 7
										"au.com.westpac.pda.beans.cct.CCTTasksBean" 6
										"au.com.westpac.pda.beans.report.ReportTasksBean" 13}
		    (extract-seq-of-relationships ...input-zip...) =>
		    [["au.com.westpac.pda.lodge.LodgementUtilities" "au.com.westpac.pda.lodge.rebuid.SystemRebuilderImpl"]
		     ["au.com.westpac.pda.lodge.LodgementUtilities" "au.com.westpac.pda.beans.report.ReportTasksBean"]
		     ["au.com.westpac.pda.beans.report.ReportTasksBean" "au.com.westpac.pda.beans.cct.CCTTasksBean"]]
		    (package-name-from-class "au.com.westpac.pda.lodge.rebuid.SystemRebuilderImpl") => "au.com.westpac.pda.lodge.rebuid"
		    (package-name-from-class "au.com.westpac.pda.lodge.LodgementUtilities") => "au.com.westpac.pda.lodge"
		    (package-name-from-class "au.com.westpac.pda.beans.cct.CCTTasksBean") => "au.com.westpac.pda.beans.cct"
		    (package-name-from-class "au.com.westpac.pda.beans.report.ReportTasksBean") => "au.com.westpac.pda.beans.report")))

(deftest should-parse-input-xml-file-and-produce-a-graph-structure
  (fact (parse-simian-report ...input-stream...) => simple-simian-graph
	  (provided (zipper-from-xml-input-stream ...input-stream...) => ...input-zip...
		    (extract-map-of-total-duplicated-lines ...input-zip...) =>
		    {"au.com.westpac.pda.lodge.rebuid.SystemRebuilderImpl" 7
		     "au.com.westpac.pda.lodge.LodgementUtilities" 7
		     "au.com.westpac.pda.beans.cct.CCTTasksBean" 6
		     "au.com.westpac.pda.beans.report.ReportTasksBean" 13}
		    (extract-seq-of-relationships ...input-zip...) =>
		    [["au.com.westpac.pda.lodge.LodgementUtilities" "au.com.westpac.pda.lodge.rebuid.SystemRebuilderImpl"]
		     ["au.com.westpac.pda.lodge.LodgementUtilities" "au.com.westpac.pda.beans.report.ReportTasksBean"]
		     ["au.com.westpac.pda.beans.report.ReportTasksBean" "au.com.westpac.pda.beans.cct.CCTTasksBean"]]
		    (package-name-from-class "au.com.westpac.pda.lodge.rebuid.SystemRebuilderImpl") => "au.com.westpac.pda.lodge.rebuid"
		    (package-name-from-class "au.com.westpac.pda.lodge.LodgementUtilities") => "au.com.westpac.pda.lodge"
		    (package-name-from-class "au.com.westpac.pda.beans.cct.CCTTasksBean") => "au.com.westpac.pda.beans.cct"
		    (package-name-from-class "au.com.westpac.pda.beans.report.ReportTasksBean") => "au.com.westpac.pda.beans.report")))

(deftest should-create-new-linecount-entry-for-single-pair
  (fact (increment-linecount-hash '{} ["myclass" 6]) => (contains {"myclass" 6})))

(deftest should-sum-linecount-entry-for-existing-pair
  (fact (increment-linecount-hash '{"myclass" 3} ["myclass" 6]) => (contains {"myclass" 9})))

(deftest should-sum-linecount-entry-for-existing-pair-in-longer-hash
  (fact (increment-linecount-hash '{"myclass" 3 "myclass2" 3 "myclass3" 3} ["myclass" 6]) => (contains {"myclass" 9})))

(deftest should-extract-seq-of-block-linecounts-from-example-doc
  (let	[sim-zip (zip/xml-zip (xml/parse (simple-simian-report-stream)))]
    (fact (extract-seq-of-block-linecounts sim-zip) =>
	  ["au.com.westpac.pda.beans.report.ReportTasksBean" 6
	   "au.com.westpac.pda.beans.cct.CCTTasksBean" 6
	   "au.com.westpac.pda.lodge.LodgementUtilities" 7
	   "au.com.westpac.pda.lodge.rebuid.SystemRebuilderImpl" 7
	   "au.com.westpac.pda.beans.report.ReportTasksBean" 7])))
				    
(deftest should-extract-map-of-total-duplicated-lines
  (fact (extract-map-of-total-duplicated-lines ...input-xml...) => (contains {:a 1 :b 2 :c 4 :d 6})
	(provided (extract-seq-of-block-linecounts ...input-xml...) =>
		  [:a 1 :b 1 :b 1 :c 2 :c 2 :d 2 :d 2 :d 2])))

(deftest should-extract-map-from-empty-seq
  (fact (extract-map-of-total-duplicated-lines ...input-xml...) => (contains {})
	(provided (extract-seq-of-block-linecounts ...input-xml...) => [])))

(deftest should-extract-seq-of-file-pairs-within-sets
  (let [seq-of-relationships
	[["au.com.westpac.pda.lodge.LodgementUtilities" "au.com.westpac.pda.lodge.rebuid.SystemRebuilderImpl"]
	 ["au.com.westpac.pda.lodge.LodgementUtilities" "au.com.westpac.pda.beans.report.ReportTasksBean"]
	 ["au.com.westpac.pda.lodge.rebuid.SystemRebuilderImpl" "au.com.westpac.pda.beans.report.ReportTasksBean"]
	 ["au.com.westpac.pda.beans.report.ReportTasksBean" "au.com.westpac.pda.beans.cct.CCTTasksBean"]]
	first-seq-of-pairs [["au.com.westpac.pda.lodge.LodgementUtilities"
			     "au.com.westpac.pda.lodge.rebuid.SystemRebuilderImpl"]
			    ["au.com.westpac.pda.lodge.LodgementUtilities"
			     "au.com.westpac.pda.beans.report.ReportTasksBean"]
			    ["au.com.westpac.pda.lodge.rebuid.SystemRebuilderImpl"
			     "au.com.westpac.pda.beans.report.ReportTasksBean"]]
	second-seq-of-pairs[["au.com.westpac.pda.beans.report.ReportTasksBean"
			     "au.com.westpac.pda.beans.cct.CCTTasksBean"]]]
    (fact (extract-seq-of-relationships ...input-zip...) => seq-of-relationships
	  (provided (extract-seq-of-duplication-sets ...input-zip...) => [...first-set... ...second-set...]
		    (expand-pairs-from-set ...first-set...) => first-seq-of-pairs
		    (expand-pairs-from-set ...second-set...) => second-seq-of-pairs))))

(deftest should-expand-duplication-sets-pairwise
  (let [first-set ["au.com.westpac.pda.lodge.LodgementUtilities"
		   "au.com.westpac.pda.lodge.rebuid.SystemRebuilderImpl"
		   "au.com.westpac.pda.beans.report.ReportTasksBean"]
	second-set ["au.com.westpac.pda.beans.report.ReportTasksBean"
		    "au.com.westpac.pda.beans.cct.CCTTasksBean"]
	first-seq-of-pairs [["au.com.westpac.pda.lodge.LodgementUtilities"
			     "au.com.westpac.pda.lodge.rebuid.SystemRebuilderImpl"]
			    ["au.com.westpac.pda.lodge.LodgementUtilities"
			     "au.com.westpac.pda.beans.report.ReportTasksBean"]
			    ["au.com.westpac.pda.lodge.rebuid.SystemRebuilderImpl"
			     "au.com.westpac.pda.beans.report.ReportTasksBean"]]
	second-seq-of-pairs [["au.com.westpac.pda.beans.report.ReportTasksBean"
			      "au.com.westpac.pda.beans.cct.CCTTasksBean"]]]
    
    (fact (expand-pairs-from-set [:a :b :c :d]) => (just [[:a :b] [:a :c] [:a :d] [:b :c] [:b :d] [:c :d]] :in-any-order))
    (fact (expand-pairs-from-set first-set) => (just first-seq-of-pairs :in-any-order))
    (fact (expand-pairs-from-set second-set) => (just second-seq-of-pairs :in-any-order))))

(deftest should-expand-duplication-sets-pairwise-without-duplication
  (let 	[second-set ["au.com.westpac.pda.beans.report.ReportTasksBean"
		    "au.com.westpac.pda.beans.report.ReportTasksBean"
		    "au.com.westpac.pda.beans.cct.CCTTasksBean"]
	 second-seq-of-pairs [["au.com.westpac.pda.beans.report.ReportTasksBean"
			       "au.com.westpac.pda.beans.cct.CCTTasksBean"]]]
	(fact (expand-pairs-from-set [:a :b :c :c]) => (just [[:a :b] [:a :c] [:b :c]] :in-any-order))
	(fact (expand-pairs-from-set second-set) => (just second-seq-of-pairs :in-any-order))))

(deftest should-extract-seq-of-sets-from-zip
  (let [first-set ["au.com.westpac.pda.lodge.LodgementUtilities"
		   "au.com.westpac.pda.lodge.rebuid.SystemRebuilderImpl"
		   "au.com.westpac.pda.beans.report.ReportTasksBean"]
	second-set ["au.com.westpac.pda.beans.report.ReportTasksBean"
		    "au.com.westpac.pda.beans.cct.CCTTasksBean"]
	sim-zip (zip/xml-zip (xml/parse (simple-simian-report-stream)))]
    (fact (extract-seq-of-duplication-sets sim-zip) => (just [first-set second-set] :in-any-order))))

(deftest should-extract-zip-from-string
  (let [myxml (clojure.contrib.io/input-stream
	       (.getBytes
		"<root><parent><child>firstchild</child><child>secondchild</child></parent></root>"))
	myzip (zipper-from-xml-input-stream myxml)]
    (fact (zx/xml-> myzip :parent :child zx/text) => ["firstchild" "secondchild"])))

(deftest should-strip-classname-to-produce-package
  (fact (package-name-from-class "au.com.westpac.pda.beans.cct.CCTTasksBean") => "au.com.westpac.pda.beans.cct"))
