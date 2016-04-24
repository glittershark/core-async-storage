(ns glittershark.core-async-storage-test
  (:require [cljs.test :refer-macros [deftest is testing async]]
            [cljs.core.async :refer [<!]]
            [glittershark.core-async-storage
             :refer [async-storage
                     get-item set-item remove-item clear]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn mock-storage-fn [fname mock-fn]
  (let [call-args (atom [])
        mock-fn* (fn [& args]
                   (swap! call-args conj (-> args butlast vec))
                   (mock-fn (last args)))]
    (aset async-storage fname mock-fn*)
    call-args))

(deftest get-item-test
  (async done
    (go
      (let [args (mock-storage-fn "getItem" #(% nil ":foobar"))]
        (is (= [nil :foobar] (<! (get-item :test)))
                "reads return values of AsyncStorage.getItem as EDN")

        (is (= [[":test"]] @args)
            "converts the passed key to EDN before passing it to
             AsyncStorage.getItem")

        (done)))))

(deftest set-item-test
  (async done
    (go
      (let [args (mock-storage-fn "setItem" #(% nil))]
        (is (= [nil] (<! (set-item :test {:foo "bar"})))
                "returns potential errors in a core.async channel")

        (is (= [[":test" "{:foo \"bar\"}"]] @args)
            "converts the passed key and value to EDN before passing it to
             AsyncStorage.removeItem")

        (done)))))

(deftest remove-item-test
  (async done
    (go
      (let [args (mock-storage-fn "removeItem" #(% nil))]
        (is (= [nil] (<! (remove-item :test)))
                "returns potential errors in a core.async channel")

        (is (= [[":test"]] @args)
            "converts the passed key to EDN before passing it to
             AsyncStorage.removeItem")

        (done)))))

(deftest clear-test
  (async done
    (go
      (let [args (mock-storage-fn "clear" #(% nil))]
        (is (= [nil] (<! (clear)))
                "returns potential errors in a core.async channel")

        (is (= [[]] @args)
            "calls the AsyncStorage function")

        (done)))))


