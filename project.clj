(defproject core-async-storage "0.1.0"
  :description "Clojurescript wrapper for react-native's AsyncStorage using
                core.async"
  :url "https://github.com/glittershark/core-async-storage"
  :license {:name "MIT License"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.8.40"]
                 [org.clojure/core.async "0.2.374"]]
  :plugins [[lein-cljsbuild "1.1.3"]
            [lein-doo "0.1.6"]]
  :resource-paths ["resources" "target/resources"]
  :cljsbuild {:builds
              {:test
               {:source-paths ["src" "test"]
                :compiler {:output-to "target/resources/test.js"
                           :output-dir "target/test/"
                           :main glittershark.core-async-storage.test-runner
                           :optimizations :none
                           :pretty-print true
                           :source-map false}}}}
  :doo {:build "test"})
