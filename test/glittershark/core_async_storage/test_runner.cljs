(ns glittershark.core-async-storage.test-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [glittershark.core-async-storage-test]))

(doo-tests 'glittershark.core-async-storage-test)
