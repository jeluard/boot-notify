(ns jeluard.boot-notify
  {:boot/export-tasks true}
  (:require [clojure.java.io :as io]
            [clojure.java.shell :as sh]
            [boot.core :as core]
            [boot.util :as util]))

(defn- program-exists? [s] (= 0 (:exit (sh/sh "command" "-v" s))))

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
  (let [d (core/temp-dir!)
        f (io/file d "logo.png")]
    (io/copy (io/input-stream (io/resource "boot-logo-3.png")) f)
    (.getAbsolutePath f)))

(core/deftask notify
  "Visible notifications during build."
  [n notifier           sym       "Custom notifier. When not provided a platform specific notifier will be used."
   m template FOO=BAR   {kw str}  "Templates overriding default messages. Keys can be :success, :warning or :failure."
   t title              str       "Title of the notification"
   i icon               str       "Full path of the file used as notification icon"
   p pid                str       "Unique ID identifying this boot process"]
  (let [title (or title "Boot notify")
        base-message {:title title :pid (or pid title)
                      :icon (or icon (boot-logo))}
        messages (merge {:success "Success!" :warning "Got %s warnings during compilation" :failure "Failed to compile"} template)]
    (if-let [n (or notifier default-notifier)]
      (if (-supported? n)
        (core/with-post-wrap
          fileset
          (try
            (let [w @core/*warnings*]
                 (if (zero? w)
                   (when-let [s (:success messages)]
                     (notify! n s base-message))
                   (when-let [s (:warning messages)]
                     (notify! n (format s w) base-message)))
                 fileset)
            (catch Throwable t
              (when-let [s (:failure messages)]
                (notify! n s base-message))
              (throw t))))
        (util/warn (str "Notifier <" n "> is not supported on this platform.")))
      (util/warn "Failed to find a Notifier for this platform."))))
