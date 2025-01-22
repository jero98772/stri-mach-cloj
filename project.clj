(defproject stri_mach_cloj "0.1.0-SNAPSHOT"
  :description "String Matching Algorithms Benchmark"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [ring "1.9.4"]
                 [compojure "1.7.0"]]
  :main ^:skip-aot stri-mach-cloj.core
  :plugins [[lein-ring "0.12.5"]]
  :ring {:handler stri-mach-cloj.web/app}
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
