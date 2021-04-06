(ns katas.kata5
  "Bloom Filters"
  (:require 
    [clojure.spec.alpha     :as s]
    [clojure.spec.gen.alpha :as gen]
    [clojure.string         :as str]
    [bigml.sketchy.murmur   :as skm]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Looked up info in   https://en.wikipedia.org/wiki/Bloom_filter"
;; , contained references, and a bit of googling
;;
;; Variables:
;; n = number of elements (int)
;; m = size of bit array (Int)
;; k = number of hash functions (Int)
;; p = false positive probability (Float)
;;
;; For the sake of this exercise I just have a global
;; state atom.  But I would probably just pass the 
;; state map into the individual fns instead.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def state
  (atom
  {:elements-num               nil
   :false-positive-probability nil
   :hash-functions-num         nil
   :bloom-filter               nil}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Create Bloom Filter
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn array-size 
  "Given the expected number of elements (n) and 
  false-positive-probability (p) provide array-size INT 
  m = -1 ((n ln p) / (ln2 ^ 2))"
  [n p]
  (unchecked-negate-int
    (/ (* n (Math/log p))
       (Math/pow (Math/log 2) 2))))

(defn number-of-hash-fns 
  "Given the size of bit-array (m) and number of elements (n), 
  return the optimal number of hash fns to use INT
  k = m / n * ln2 "
  [n m]
  (int (* (/ m n) (Math/log 2))))


(defn bloom-filter 
  "Given a number of items (n) and false-positive-probability (p)
  return an initialized bloom filter" 
  [n p]
  (let [arr-size       (array-size n p)
        no-of-hash-fns (number-of-hash-fns n arr-size)
        arr            (java.util.BitSet. arr-size)]
    (reset! state 
            {:elements-num               n
             :false-positive-probability p
             :hash-functions-num         no-of-hash-fns
             :bloom-filter               arr})))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Methods
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-hashed-bits
  "Given a string, hashes the string several times, 
  taking the modulus of the hash"
  [item]
  (let [hashes (:hash-functions-num @state)
        els    (:elements-num       @state)
        bits (set
               (reduce (fn [acc n]
                         (-> item
                             (skm/hash n)
                             (mod els)
                             (cons acc)))
                       []
                       (range 1 (inc hashes))))]
    bits))

(defn add-item 
  "Flips the bits of the bloom filter"
  [item]
  (if-let [bf (:bloom-filter @state)]
    (let [bits (get-hashed-bits item)]
      (map #(.set bf %) bits))
    :error-no-filter-yet))

(defn check-existance
  "Checks the bloom filter to see if that word has already been added"
  [item]
  (if-let [bf (:bloom-filter @state)]
    (let [bits (get-hashed-bits item)]
      (if (= (count bits)
             (->> bits
                  (map #(.get bf %))
                  (filter identity)
                  count))
        true
        false))
    false))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Generate Words 
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(s/def ::random-word (s/and string? #(<= 5 (count %))))
; (gen/generate (s/gen ::random-word))
(defn gen-words [n]
  (let [gen (gen/sample (s/gen ::random-word) n)]
    (mapv #(subs % 0 5) gen)))

(comment
  (def words (gen-words 200))
  (map add-item words)
  (add-item "hamburger")
  (check-existance "hamburger")
  (map check-existance words)
  (array-size 20 0.05)
  (bloom-filter 200 0.05)
  (:bloom-filter @state)
  :end)

