(ns jeluard.boot-notify
  {:boot/export-tasks true}
  (:require [clojure.java.io :as io]
            [clojure.java.shell :as sh]
            [boot.core :as core]
            [boot.util :as util]))

(defn- program-exists?
  [s]
  (= 0 (:exit (sh/sh "sh" "-c" (format "command -v %s" s)))))

(defprotocol Notifier
  (-supported? [this] "Check if this notifier is supported on current platform")
  (-notify [this m] "Perform the notification"))

(deftype TerminalNotifierNotifier
  []
  Notifier
  (-supported? [_] (program-exists? "terminal-notifier"))
  (-notify [_ {:keys [message title icon pid]}] (sh/sh "terminal-notifier" "-message" message "-title" title "-contentImage" icon "-group" pid)))

(deftype NotifySendNotifier
  []
  Notifier
  (-supported? [_] (program-exists? "notify-send"))
  (-notify [_ {:keys [message title icon]}] (sh/sh "notify-send" title message "--icon" icon)))

(def default-notifier
  (condp = (System/getProperty "os.name")
    "Mac OS X" (TerminalNotifierNotifier.)
    "Linux" (NotifySendNotifier.)))

(defn- notify!
  [n s m]
  (-notify n (assoc m :message s)))

(defn boot-logo
  []
  (let [d (core/tmp-dir!)
        f (io/file d "logo.png")]
    (io/copy (io/input-stream (io/resource "boot-logo-3.png")) f)
    (.getAbsolutePath f)))

(core/deftask notify
  "Visible notifications during build."
  [n notifier VAL       sym       "Custom notifier. When not provided a platform specific notifier will be used."
   m template FOO=BAR   {kw str}  "Templates overriding default messages. Keys can be :success, :warning or :failure."
   t title    VAL       str       "Title of the notification"
   i icon     VAL       str       "Full path of the file used as notification icon"
   p pid      VAL       str       "Unique ID identifying this boot process"]
  (let [title (or title "Boot notify")
        base-message {:title title :pid (or pid title)
                      :icon (or icon (boot-logo))}
        messages (merge {:success "Success!" :warning "%s warning/s" :failure "%s"} template)]
    (if-let [n (or notifier default-notifier)]
      (if (-supported? n)
        (fn [next-task]
          (fn [fileset]
            (try
              (util/with-let [_ (next-task fileset)]
                (if (zero? @core/*warnings*)
                  (notify! n (:success messages) base-message)
                  (notify! n (format (:warning messages) @core/*warnings*) base-message)))
              (catch Throwable t
                (notify! n (format (:failure messages) (.getMessage t)) base-message)
                (throw t)))))
        (util/warn (str "Notifier <" n "> is not supported on this platform.")))
      (util/warn "Failed to find a Notifier for this platform."))))
