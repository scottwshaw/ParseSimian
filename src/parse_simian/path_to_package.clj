(ns parse-simian.path-to-package
  (:require [clojure.string :as str]
	    [midje.sweet :as ms]))

(ms/unfinished prefix-before-domain)

(defn to-qualified-classname [path]
  (let [path-prefix (prefix-before-domain path)]
    (str/replace (str/replace (str/replace path path-prefix "") "/" ".") ".java" "")))

    
