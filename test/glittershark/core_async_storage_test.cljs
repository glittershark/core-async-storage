(ns glittershark.core-async-storage-test
  (:require [cljs.test :refer-macros [deftest is testing async]]
            [cljs.core.async :refer [<!]]
            [glittershark.core-async-storage
             :refer [async-storage
                     get-item multi-get set-item remove-item clear]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn js-array-equals? [a1 a2]
  (and (js/Array.isArray a1)
       (js/Array.isArray a2)))

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
      (testing "when the key exists in storage"
        (let [args (mock-storage-fn "getItem" #(% nil ":foobar"))]
          (is (= [nil :foobar] (<! (get-item :test)))
              "reads return values of AsyncStorage.getItem as EDN")

          (is (= [[":test"]] @args)
              "converts the passed key to EDN before passing it to
               AsyncStorage.getItem"))

        (testing "when the key doesn't exist in storage"
          (let [args (mock-storage-fn "getItem" #(% nil nil))]
            (is (= [nil nil] (<! (get-item :test)))
                "returns nil for both error and value"))))

      (done))))

(deftest multi-get-test
  (async done
    (go
      (testing "when the keys all exist in storage"
        (let [args (mock-storage-fn "multiGet" #(% nil #js [":foo" ":bar"]))]
          (is (= [nil [:foo :bar]] (<! (multi-get [:test1 :test2])))
              "reads return values of AsyncStorage.multiGet as EDN")

          (is (= [[[":test1" ":test2"]]] (js->clj @args))
              "Converts all of the passed keys to EDN before passing them to
               AsyncStorage.multiGet")

          (is (js/Array.isArray (-> @args first first))
              "Converts the list of keys to a JS array")))

      (done))))

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


