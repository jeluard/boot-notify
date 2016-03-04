# boot-notify [![License](http://img.shields.io/badge/license-EPL-blue.svg?style=flat)](https://www.eclipse.org/legal/epl-v10.html)

[Boot](https://github.com/boot-clj/boot) task displaying visual notification based on the build status.

Relies on [terminal-notifier](https://github.com/alloy/terminal-notifier) on OSX and [notify-send](http://manpages.ubuntu.com/manpages/gutsy/man1/notify-send.1.html) on Linux.

## Usage

Add `boot-notify` to your `build.boot` dependencies and `require` the namespace:

```clj
(set-env! :dependencies '[[jeluard/boot-notify "0.2.1" :scope "test"]])
(require '[jeluard.boot-notify :refer [notify]])
```

Use `(notify)` wherever you would use `(speak)`.

You can see the options available on the command line:

```bash
boot notify -h
```

or in the REPL:

```clj
boot.user=> (doc notify)
```

## License

Copyright © 2015 Julien Eluard

Distributed under the Eclipse Public License, the same as Clojure.
