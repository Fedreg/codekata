# Kata 2 - Binary Search

Implement a binary search to find the index of an integer on a sorted array of integers

Fn Signature:
``` 
chop :: Int -> List Int -> Int
```
### Reference Implementation
```clj
(defn chop [n array-of-ints]
  (.indexOf array-of-ints n))
```
```clj
(chop 99999 (into [] (range 100000))) => 99999

"Elapsed time: 4.424195 msecs"
"Elapsed time: 4.184242 msecs"
"Elapsed time: 3.920148 msecs"
"Elapsed time: 3.959286 msecs"
"Elapsed time: 4.529486 msecs"
"Elapsed time: 4.160403 msecs"
"Elapsed time: 3.793933 msecs"
"Elapsed time: 6.787145 msecs"
"Elapsed time: 3.925691 msecs"
"Elapsed time: 4.238058 msecs"

Average ~ 4.39 msecs
```

```clj
(chop 100001 (into [] (range 100000))) => -1

Roughly the same time averages as successful search
```
## First Binary Search attempt - Recursive
This is a clumsy first attempt and I speicifically tried to NOT optimize anything.
It's been years since I implemented this algorithm.  I remembered the basic min/max approach that is
the standard way to do this but I thought I'd just try whatever popped into my head first since that's 
part of the point of this kata.

```clj
(defn chop [n array-of-ints]
  (let [length      (count array-of-ints)
        half        (-> length (/ 2) int)
        first-half  (take half array-of-ints)
        second-half (take-last
                     (if (odd? length)
                       (inc half)
                       half)
                     array-of-ints)
        half-to-use (if (< n (first second-half)) first-half second-half)]
    (if (= 1 (count half-to-use))
      (.indexOf half-to-use n)
      (chop n half-to-use))))
```
Because I am an idiot, the answer to this fn will always only be 0 or -1.
See why?  I'm just halving the array every time until I'll eventually be left with an array of 1 item 
which will either contain the target (whose index will always be 0) or won't and return -1 :disappointed:

So... I still use the basic algorithm but passed in another parameter, the `cur-idx` which tracks the
current index of the beginning of the section of array I'm examining.

With this new param I have a working implementation:
```clj
(defn chop
  ([n array-of-ints]
   (chop n array-of-ints 0))

  ([n array-of-ints cur-idx]
   (let [length      (count array-of-ints)
         half        (-> length (/ 2) int)
         first-half  (take half array-of-ints)
         second-half (take-last
                      (if (odd? length)
                        (inc half)
                        half)
                      array-of-ints)
         half-to-use (if (< n (first second-half)) first-half second-half)
         new-idx     (if (< n (first second-half)) cur-idx (+ cur-idx half))]
     (if (= 1 (count half-to-use))
       (if (= n (first half-to-use))
         new-idx
         -1)
       (chop n half-to-use new-idx)))))
```
Performance
```clj
(chop 99999 (into [] (range 100000))) => 99999

"Elapsed time: 9.163719 msecs"
"Elapsed time: 8.516807 msecs"
"Elapsed time: 8.34343 msecs"
"Elapsed time: 21.55271 msecs"
"Elapsed time: 8.302721 msecs"
"Elapsed time: 8.339336 msecs"
"Elapsed time: 8.38313 msecs"
"Elapsed time: 9.18578 msecs"
"Elapsed time: 8.308676 msecs"
"Elapsed time: 8.361676 msecs"
"Elapsed time: 8.413451 msecs"

Average ~ 8.53 msecs 
``` 
About twice as slow as the reference implementation :disappointed:

---

## Second attempt - Iterative

Curious if using loop/recur has any built-in optimizations?
```clj
(defn chop [n array-of-ints]
  (if (not-empty array-of-ints)
    (loop [arr array-of-ints
           idx 0]
      (let [length  (count arr)
            half    (-> length (/ 2) int)
            a       (take half arr) 
            b       (take-last (if (odd? length) (inc half) half) arr)
            new-arr (if (some->> b first (< n)) a b)
            new-idx (if (some->> b first (< n)) idx (+ idx half))]
        (if (= 1 (count new-arr))
          (if (= n (first new-arr))
            new-idx
            -1)
          (recur new-arr new-idx))))
    -1))
```
Same performance as first

### Optimizations

Trying a few optimizations to see if I can improve performance
NOTE: These are all tested with `time` in ms.. I could delve deeper with a
better optimization tool.

### 1. Transducers:
since `take` and `take-last` are lazy... wondering if a transducer might improve..
calling either of those without a collection will return a transducer.

So I can do something like this:
```clj
trns-tk (take half)
a       (transduce trns-tk conj arr)
```
(can't do this with `take-last`)

but that didn't improve performance.

---
### 2. First / Last
I've heard these can also be slow because they use laziness under the hood?

Try to replace calls to `first` with `(nth ... 0)`

```clj
(defn chop [n array-of-ints]
  (if (not-empty array-of-ints)
    (loop [arr array-of-ints
           idx 0]
      (let [length  (count arr)
            half    (-> length (/ 2) int)
            trns-t  (take half)
            a       (transduce trns-t  conj arr)
            b       (take-last (if (odd? length) (inc half) half) arr)
            new-arr (if (some-> b (nth 0) (> n)) a b)
            new-idx (if (some-> b (nth 0) (> n)) idx (+ idx half))]
        (if (= 1 (count new-arr))
          (if (= n (nth new-arr 0))
            new-idx
            -1)
          (recur new-arr new-idx))))
    -1))
```
No noticeable improvement

---
### 3. Type hints?
```clj
(defn chop [^long n ^clojure.lang.PersistentVector array-of-ints]
```
No dice.

## Third Attempt - better iterative
---
Not far off from the other iterative attempt but realized I was way overcomplicating it.  

Here is a slightly cleaned up iterative version first so we can compare.
```clj
(defn chop [n array-of-ints]
  (loop [arr   array-of-ints
         base  0]
    (if (not-empty arr)
      (let [mid    (int (Math/floor (/ (count arr) 2)))
            mid-n  (nth arr mid)]
        (cond 
          (< n (first arr)) -1
          (> n (last  arr)) -1
          (= n mid-n) (+ (.indexOf arr mid-n) base)
          (< n mid-n) (recur (take mid arr) 0) 
          (> n mid-n) (let [new-arr (take-last (inc mid) arr)]
                        (recur new-arr (inc (.indexOf arr (first new-arr)))))
          :else -1))
      -1)))
``` 

Turns out I don't need to keep track of all that array...

All I need is to track the start and end of the array I use

```clj
(defn chop [n array-of-ints]
  (loop [start 0
         end   (count array-of-ints)]
    (let [mid    (int (Math/floor (/ (+ start end) 2)))
          mid-n  (get array-of-ints mid -1)]
      (cond 
        (> start end) -1
        (= n mid-n)  mid
        (< n mid-n)  (recur start (dec mid)) 
        (> n mid-n)  (recur (inc mid) end)))))

```
Turns out it's actually much more performant to not pass an array around :smile:
```clj
(chop 99999 (into [] (range 100000))) => 99999

"Elapsed time: 3.071766 msecs"
"Elapsed time: 2.16022 msecs"
"Elapsed time: 2.242611 msecs"
"Elapsed time: 2.492596 msecs"
"Elapsed time: 2.055249 msecs"
"Elapsed time: 2.509279 msecs"
"Elapsed time: 2.156759 msecs"
"Elapsed time: 2.49744 msecs"
"Elapsed time: 2.152248 msecs"
"Elapsed time: 2.150235 msecs"

Average ~ 2.35 msecs 
``` 

