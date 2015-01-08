(set-env!
 :source-paths #{"src"}
 :resource-paths #{"resources"}
 :dependencies '[[org.clojure/clojure "1.6.0"     :scope "provided"]
                 [boot/core           "2.0.0-rc5" :scope "provided"]
                 [adzerk/bootlaces    "0.1.8"     :scope "test"]])

(require
 '[adzerk.bootlaces :refer :all]
 '[boot.pod         :as pod]
 '[boot.util        :as util]
 '[boot.core        :as core])

(def +version+ "0.1.0")

(bootlaces! +version+)

(task-options!
 pom {:project 'jeluard/boot-notify
      :version +version+
      :description "A boot task displaying visual notification based on the build status."
      :url         "https://github.com/jeluard/boot-notify"
      :scm         {:url "https://github.com/jeluard/boot-notify"}
      :license     {:name "Eclipse Public License"
                    :url  "http://www.eclipse.org/legal/epl-v10.html"}})
