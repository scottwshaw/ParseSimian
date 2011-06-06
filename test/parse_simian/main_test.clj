(ns parse-simian.main-test
  (:use :reload parse-simian.main)
  (:use	clojure.contrib.json clojure.test
	[midje.sweet :only [fact]]))

(deftest shouldPrintJSONSymbolsWithoutQuotes
  (fact (json-str {:a :b, :c :d}) => "{a:\"b\",c:\"d\"}"))

