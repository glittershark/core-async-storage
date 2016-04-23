(ns glittershark.core-async-storage)

(defmacro defcbfn
  "Define `fname' as a wrapper to a function `wrapped-fn' that takes a callback
   as its last argument that, instead of taking a callback, returns the result
   wrapped inside a core.async channel"
  [fname wrapped-fn]
  ;; Evaluate in case the function expression has side effects
  `(let [wrapped-fn# ~wrapped-fn]
     (defn ~fname [& args#]
       (let
         [result-chan# (~'promise-chan)
          callback# (fn callback [& result#]
                      (~'put! result-chan# (vec result#)))
          args# (vec args#)
          wrap-args# (conj args# callback#)]
         (apply ~wrapped-fn wrap-args#)
         result-chan#))))
