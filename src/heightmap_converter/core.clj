(ns heightmap-converter.core
  (:require [clojure.string :as str])
  (:require [cheshire.core :as json])
  (:gen-class))
(use 'mikera.image.core)
(use 'mikera.image.colours)

(defn low-bits
  ([x]
   (low-bits x 8))
  ([x n]
   (bit-and x (unchecked-dec (bit-shift-left 1 n)))))

(defn remove-extension [filename]
  (str/replace filename #".[^.]*$" ""))

(defn convert [scaling filename]
  (def image (load-image filename))
  (def pixels (map int (map (partial * scaling)(map low-bits (get-pixels image))))) ;; extract the first color values
  (def size (count pixels))
  (spit (str (remove-extension filename) ".json")
        (json/generate-string
         {:GameVersion "v20210915-3e7b0fa-gw"
          :Timestamp "2021-09-19 15:01:37"
          :Singletons {
                       :MapSize {
                                 :Size { :X (width image) :Y (height image)}}
                       :TerrainMap {
                                    :Heights {
                                              :Array (str/join " " (map str pixels))}}
                       :CameraStateRestorer {
                                             :SavedCameraState {
                                                                :Target {:X 0.0 :Y 0.0 :Z 0.0}
                                                                :ZoomLevel 0.0
                                                                :HorizontalAngle 30.0
                                                                :VerticalAngle 70.0}}
                       :WaterMap {
                                  :WaterDepths {
                                                              :Array (str/join " " (repeat size "0"))}
                                  :Outflows {
                                             :Array (str/join " " (repeat size "0:0:0:0"))}}
                       :SoilMoistureSimulator {
                                               :MoistureLevels{
                                                               :Array (str/join " " (repeat size "0"))}}}
          :Entities []})))
(defn -main
  [& args]
  (if (== (count args) 2)
    (convert (Float/parseFloat (first args)) (second args))
    (println "Usage: [scaling] [filename]")))
