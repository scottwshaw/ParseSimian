(ns parse-simian.path-to-package-test
  (:use parse-simian.path-to-package
	clojure.test
	midje.sweet))

(deftest should-extract-prefix-before-au-dot-com-domain-name-from-path
  (fact (prefix-before-domain "/Users/avombatk/projects/healthcheck/build/src/au/com/westpac/pda/beans/report/ReportTasksBean.java")
	=> "/Users/avombatk/projects/healthcheck/build/src/"))

(deftest should-extract-package-from-path-with-au-com-domain
  (let [input-path "/Users/avombatk/projects/healthcheck/build/src/au/com/westpac/pda/beans/report/ReportTasksBean.java"]
    (fact (to-qualified-classname input-path) => "au.com.westpac.pda.beans.report.ReportTasksBean"
	  (provided (prefix-before-domain input-path) => "/Users/avombatk/projects/healthcheck/build/src/"))))
