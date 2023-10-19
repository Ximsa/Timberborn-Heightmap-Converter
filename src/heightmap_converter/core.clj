(ns heightmap-converter.core
  (:require [clojure.string :as str]
            [cheshire.core :as json]
            [clojure.java.io :refer [output-stream] :as io])
  (:import [java.util.zip ZipOutputStream ZipEntry]
           [java.io FileOutputStream])
  (:gen-class))
(use 'mikera.image.core)

(defn low-bits
  ([x]
   (low-bits x 8))
  ([x n]
   (bit-and x (unchecked-dec (bit-shift-left 1 n)))))

(defn remove-extension [filename]
  (str/replace filename #".[^.]*$" ""))

(defn convert [elevation filename]
  (let [image (load-image filename)
        pixels (map low-bits (get-pixels image)) ;; extract the first color values
        min-elevation (apply min pixels)
        normalizedPixels (map #(int (* (- %1 min-elevation) %2))
                              pixels
                              (repeat (/ elevation (- (apply max pixels) min-elevation ))))
        size (count normalizedPixels)
        contents (-> (json/generate-string
                      {:GameVersion "0.5.0.1-4a285d3-xsw"
                       :Timestamp "2023-10-19 15:01:37"
                       :Singletons
                       {:MapSize
                        {:Size
                         {:X (width image)
                          :Y (height image)}}
                        :TerrainMap
                        {:Heights
                         {:Array (str/join " " (map str normalizedPixels))}}
                        :CameraStateRestorer
                        {:SavedCameraState
                         {:Target
                          {:X 0.0
                           :Y 0.0
                           :Z 0.0}
                          :ZoomLevel 0.0
                          :HorizontalAngle 30.0
                          :VerticalAngle 70.0}}
                        :WaterMap
                        {:WaterDepths
                         {:Array (str/join " " (repeat size "0"))}
                         :Outflows
                         {:Array (str/join " " (repeat size "0:0:0:0"))}}
                        :SoilMoistureSimulator
                        {:MoistureLevels
                         {:Array (str/join " " (repeat size "0"))}}}
                       :Entities []})
                     .getBytes)
        zip-entry (ZipEntry. "world.json")
        file (-> filename
                 remove-extension
                 (str ".timber")
                 FileOutputStream.
                 ZipOutputStream.)]
    (.putNextEntry file zip-entry)
    (.write file contents 0 (count contents))
    (.close file)))
  
(defn -main
  [& args]
  (if (== (count args) 2)
    (convert (Float/parseFloat (first args)) (second args))
    (println "Usage: [max height] [filename]")))
