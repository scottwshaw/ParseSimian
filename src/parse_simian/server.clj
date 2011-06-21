(ns parse-simian.server
  (:use compojure.core parse-simian.json-mods)
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [clojure.contrib.json :as json]
	    [parse-simian.core :as ps]))

(defroutes parse-simian-routes
  (POST "/xmlreport" request
	(str "var similarity_graph = " (json/json-str (ps/parse-simian-report (:body request))) ";"))
  (route/not-found "Page not found"))

(def server (handler/site parse-simian-routes))
