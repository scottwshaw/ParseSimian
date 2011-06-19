(ns parse-simian.server
  (:use compojure.core)
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [clojure.contrib.duck-streams :as ds]))

(defroutes parse-simian-routes
  (POST "/xmlreport" request
        (println (ds/slurp* (:body request)))
        "<html><body>this is the result</body></html>")
  (route/not-found "Page not found"))

(def server (handler/site parse-simian-routes))
