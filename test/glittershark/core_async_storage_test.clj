(ns glittershark.core-async-storage-test
  (:require [clojure.test :refer :all]
            [clojure.core.async :refer [promise-chan go <! put!]]
            [glittershark.core-async-storage :refer :all]))

(defn test-cb-fn [x cb] (cb (inc x)))
(defcbfn test-wrapped-fn test-cb-fn)

(deftest defcbfn-macro-test
  (go (is (= [2] (<! (test-wrapped-fn 1)))
          "Calls the function with a callback, and returns the arguments to the
           callback in a core.async channel")))
