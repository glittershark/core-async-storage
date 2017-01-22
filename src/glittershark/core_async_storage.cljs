(ns glittershark.core-async-storage
  "Clojurescript wrapper around react-native's AsyncStorage using core.async

   In general, all functions in this namespace are kebab-case versions of
   AsyncStorage's camelCase functions, that return a `core.async' channel rather
   than taking a callback

   All keys and values passed to functions in this namespace will also be
   serialized to and from EDN before being stored"
  {:author "Griffin Smith"}

  (:require [cljs.core.async :refer [promise-chan <! put!]]
            [cljs.reader :as reader])
  (:require-macros [glittershark.core-async-storage :refer [defcbfn]]))

(def ^:private async-storage
  (if (exists? js/require)
    (aget (js/require "react-native") "AsyncStorage")
    (js-obj)))

(defn- map-first ([f] (comp vector f first))
                 ([f v] ((map-first f) v)))

(defn- map-last
  ([f] (fn [coll] (vec (concat (butlast coll) [(f (last coll))]))))
  ([f v] ((map-last f) v)))

(defn- ?read-string [v] (if v (reader/read-string v) v))

(defcbfn
  ^{:doc "Fetches `key' and returns [error result] in a core.async channel, or
          [nil result] if no error"
    :arglists '([key])
    :added "1.0.0"}
  get-item (aget async-storage "getItem")
  :transducer (map (map-last ?read-string))
  :transform-args (map-first pr-str))

(defcbfn
  ^{:doc "Fetches all `keys` and returns [errors results], where each item in
          errors and results correspond to the element at the same index in key,
          in a core async channel, or [nil results] if no error"
    :arglists '([keys])
    :added "1.1.0"}
  multi-get (aget async-storage "multiGet")
  :transducer (map (map-last #(map ?read-string %)))
  :transform-args (map-first #(->> % (map pr-str) (apply array))))

(defcbfn
  ^{:doc "Sets `value' for `key' and returns [error] in a core.async channel
          upon completion, or [] if no error"
    :arglists '([key value])
    :added "1.0.0"}
  set-item (aget async-storage "setItem")
  :transform-args #(map pr-str %))

(defcbfn
  ^{:doc "Removes `key' from the storage and returns [error] in a core.async
          channel, or [] if no error"
    :arglists '([key value])
    :added "1.0.0"}
  remove-item (aget async-storage "removeItem")
  :transform-args (map-first pr-str))

(defcbfn
  ^{:doc "Erases *all* AsyncStorage for all clients, libraries, etc. You
          probably don't want to call this - use removeItem or multiRemove to
          clear only your own keys instead.
          Returns [error] in a core.async channel, or [] if no error"
    :arglists '([key value])
    :added "1.0.0"}
  clear (aget async-storage "clear"))
