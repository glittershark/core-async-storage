(ns glittershark.core-async-storage-test
  (:require [clojure.test :refer :all]
            [clojure.core.async :refer [promise-chan go <! put! <!!]]
            [glittershark.core-async-storage :refer :all]))

(defn test-cb-fn [x cb] (cb (inc x)))

(defcbfn test-basic-wrapped-fn test-cb-fn)
(defcbfn test-xf-wrapped-fn test-cb-fn
  :transducer (map (comp vector inc first)))
(defcbfn test-arg-transform-wrapped-fn test-cb-fn
  :transform-args (comp vector (partial * 2) first))

(deftest defcbfn-macro-test
  (<!!
    (go
      (testing "without any extra options"
        (is (= [2] (<! (test-basic-wrapped-fn 1)))
            "Calls the function with a callback, and returns the arguments to
             the callback in a core.async channel"))

      (testing "with a :transducer option"
        (is (= [3] (<! (test-xf-wrapped-fn 1)))
            "Applies the transducer to the result values"))

      (testing "with a :transform-args option"
        (is (= [5] (<! (test-arg-transform-wrapped-fn 2)))
            "Applies the :transform-args function to the arguments before
             calling the wrapped function")))))
