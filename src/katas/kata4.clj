(ns katas.kata4
  "Data Munging"
  (:require
    [clojure.string :as str]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; General
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def weather-data  (slurp "../../Downloads/weather.dat"))
(def football-data (slurp "../../Downloads/football.dat"))

(defmulti munge-data :data/type)
(defmulti normalize-data :data/transform)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Methods
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defmethod normalize-data :split-rows [data]
  (-> data :data/value (str/split-lines)))

(defmethod normalize-data :remove-blank [data]
  (->> data :data/value (remove str/blank?)))

(defmethod normalize-data :remove-spaces [data]
  (reduce (fn [acc row]
            (conj acc (str/split row #"\s+")))
          []
          (:data/value data)))

(defmethod normalize-data :drop-rows [data]
  (let [to-drop (-> data :data/indexes set)]
    (reduce (fn [acc row]
              (if (contains? to-drop (.indexOf (:data/value data) row))
                acc
                (conj acc row)))
            []
            (:data/value data))))

(defmethod normalize-data :take-columns [data]
  (let [to-take (-> data :data/indexes)]
    (reduce (fn [acc row]
              (conj acc (mapv #(nth row %) to-take)))
            []
            (:data/value data))))

(defn str->int [s]
  (if (re-matches #"[0-9]+." s)
    (->> s
         (re-find #"\d+")
         Integer/parseInt)
    s))

(defmethod normalize-data :parse-ints [data]
  (reduce (fn [acc row]
            (conj acc (mapv str->int row)))
          []
          (:data/value data)))

(defn ->txfm [data transformations]
  (reduce (fn [d t]
            (normalize-data
              (if (vector? t)
                {:data/value     d
                 :data/transform (first t)
                 :data/indexes   (last t)}
                {:data/value     d
                 :data/transform t})))
          data
          transformations))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

; Weather

;In weather.dat you’ll find daily weather data for Morristown, NJ for June 2002.
;Download this text file, then write a program to output the day number
;(column one) with the smallest temperature spread (the maximum temperature
;is the second column, the minimum the third column).

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn smallest-temp-spread
  "List (List Int) -> List Int
  Returns an int representing the day number
  with smallest spread between min/max temps. 3 smallest"
  [data]
  (let [spreads
        (reduce (fn [acc row]
                  (conj acc [(nth row 0)
                             (- (nth row 1) (nth row 2))]))
                []
                data)]
    (->> spreads
         (sort-by second)
         (take 3)
         (mapv first)
         sort)))

(defmethod munge-data :weather [_]
  (-> weather-data
      (->txfm
        [:split-rows
         :remove-blank
         [:drop-rows    [0 31]]
         :remove-spaces
         ; [:take-columns [1 2 3]]
         #_:parse-ints])
      #_smallest-temp-spread))

(comment
  (munge-data {:data/type :weather}) ;=> [13 14 15]
  :end)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Football

;The file football.dat contains the results from the English Premier League
;for 2001/2. The columns labeled ‘F’ and ‘A’ contain the total number of
;goals scored for and against each team in that season (so Arsenal scored 79
;goals against opponents, and had 36 goals scored against them). Write a
;program to print the name of the team with the smallest difference in
;‘for’ and ‘against’ goals.

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn smallest-point-difference
  "List (List String Int Int) -> String
  Returns the name of a team with the smallest
  for/against differential"
  [data]
  (let [spreads
        (reduce (fn [acc row]
                  (let [n (nth row 0)
                        f (nth row 1)
                        a (nth row 2)]
                    (conj acc [n (if (>= f a) 
                                   (- f a)
                                   (- a f))])))
                []
                data)]
    (->> spreads
         (sort-by second)
         (take 1))))

(defmethod munge-data :football [_]
  (-> football-data
      (->txfm
        [:split-rows
         :remove-blank
         [:drop-rows    [0 18]]
         :remove-spaces
         [:take-columns [2 7 9]]
         :parse-ints])
      #_smallest-point-difference))

(comment
  (munge-data {:data/type :football}) ; => Aston_Villa (1)
  :end)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Kata Questions

;; Q:
;; To what extent did the design decisions you made when writing the original 
;; programs make it easier or harder to factor out common code?

;; A:
;; Easy.. I assumed we'd have commonalities between the various data-extraction 
;; programs so I made the fns generic  (Although not generic enough)

;; Q:
;; Was the way you wrote the second program influenced by writing the first?

;; A: 
;; Absolutely, I did not read ahead to see that there were only 2 programs 
;; and that the ask would be to refactor... But as soon as I started the program
;; I saw the utility in creating the abstractions and proceeded.

;; Q:
;; Is factoring out as much common code as possible always a good thing? 
;; Did the readability of the programs suffer because of this requirement? 
;; How about the maintainability?

;; A: 
;; No!!!
;; In this case, not at all.  
;; If the requirements change too much in the future, the maintainability 
;; can suffer greatly from having too premature an abstraction.

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

