(defproject heightmap-converter "0.1.0-SNAPSHOT"
  :description "Converts a heightmap into the timberborn map format"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [net.mikera/imagez "0.12.0"]
                 [cheshire "5.10.0"]]
  :main ^:skip-aot heightmap-converter.core
  :target-path "target/%s"
  :resource-paths ["resources"]
  :clean-targets ^{:protect false} []
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
