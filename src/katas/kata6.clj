(ns katas.kata6
  "Anagrams"
  (:require [clojure.string :as str]))

(defn find-anagrams
  "Given a list of words, group all words that are anagrams of eachother"
  [words]
  (reduce (fn [m w]
            (let [sorted (-> w str/lower-case sort str/join)]
              (if (contains? m sorted)
                (update-in m [sorted] conj w)
                (assoc m sorted [w]))))
          {}
          words))

(comment 
  (def word-list (str/split (slurp "../../Downloads/anagram-wordlist.txt") #"\n"))
  (count word-list)
  (def ana (find-anagrams word-list))
  (count ana)
  (count (flatten (vals ana)))
  (find-anagrams ["dog" "pig" "pgi" "cat" "tac" "frog" "lisp" "spil"])
  :end)

