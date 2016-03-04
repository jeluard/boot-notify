(set-env!
 :source-paths #{"src"}
 :resource-paths #{"resources"}
 :dependencies '[[org.clojure/clojure "1.6.0"     :scope "provided"]
                 [adzerk/bootlaces    "0.1.13"    :scope "test"]])

(require
 '[adzerk.bootlaces :refer :all])

(def +version+ "0.2.1")

(bootlaces! +version+)

(task-options!
 pom {:project 'jeluard/boot-notify
      :version +version+
      :description "A boot task displaying visual notification based on the build status."
      :url         "https://github.com/jeluard/boot-notify"
      :scm         {:url "https://github.com/jeluard/boot-notify"}
      :license     {"Eclipse Public License" "http://www.eclipse.org/legal/epl-v10.html"}})
