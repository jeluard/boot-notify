(set-env!
 :source-paths #{"src"}
 :resource-paths #{"resources"}
 :dependencies '[[org.clojure/clojure "1.6.0"     :scope "provided"]
                 [boot/core           "2.0.0-rc9" :scope "provided"]
                 [adzerk/bootlaces    "0.1.10"     :scope "test"]])

(require
 '[adzerk.bootlaces :refer :all]
 '[boot.pod         :as pod]
 '[boot.util        :as util]
 '[boot.core        :as core])

(def +version+ "0.1.2")

(bootlaces! +version+)

(task-options!
 pom {:project 'jeluard/boot-notify
      :version +version+
      :description "A boot task displaying visual notification based on the build status."
      :url         "https://github.com/jeluard/boot-notify"
      :scm         {:url "https://github.com/jeluard/boot-notify"}
      :license     {"Eclipse Public License" "http://www.eclipse.org/legal/epl-v10.html"}})
