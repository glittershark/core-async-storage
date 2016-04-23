(ns glittershark.core-async-storage)

(defmacro defcbfn
  "Define `fname' as a wrapper to a function `wrapped-fn' which takes a callback
   as its last argument that, instead of taking a callback, returns the result
   wrapped inside a core.async channel

   Supported options:

     :transducer     - transducer to be applied to all return values in the
                       channel
     :transform-args - function to apply to a sequence of the function arguments
                       (excluding the callback) before supplying to the wrapped
                       function"

  [fname wrapped-fn & {:keys [transducer transform-args]
                       :or   {transducer     nil
                              transform-args 'identity}}]

  ;; Evaluate in case the function expressions have side effects
  `(let [transducer# ~transducer
         transform-args# ~transform-args]
     (defn ~fname [& args#]
       (let
         [result-chan# (~'promise-chan transducer#)
          callback# (fn [& result#]
                      (~'put! result-chan# (vec result#)))
          wrap-args# (-> args#
                         transform-args#
                         vec
                         (conj callback#))]
         (apply ~wrapped-fn wrap-args#)
         result-chan#))))
