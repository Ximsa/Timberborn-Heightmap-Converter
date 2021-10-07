(defproject heightmap-converter "0.1.3"
  :description "Converts a heightmap into the timberborn map format"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [net.mikera/imagez "0.12.0"]
                 [cheshire "5.10.0"]]
  :main ^:skip-aot heightmap-converter.core
  :target-path "target/%s"
  :resource-paths ["resources"]
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
