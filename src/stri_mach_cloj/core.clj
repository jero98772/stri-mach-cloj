(ns stri-mach-cloj.core)

(defn time-decorator [f]
  (fn [& args]
    (let [start (System/nanoTime)
          result (apply f args)
          end (System/nanoTime)]
      (println (str "Execution time: " (/ (- end start) 1e6) " ms"))
      result)))

(defn build-lps [pattern]
  "Build the Longest Prefix Suffix (LPS) array."
  (let [m (count pattern)
        lps (int-array m)]
    (loop [i 1
           len 0]
      (if (< i m)
        (if (= (nth pattern i) (nth pattern len))
          (do
            (aset lps i (inc len))
            (recur (inc i) (inc len)))
          (if (pos? len)
            (recur i (aget lps (dec len)))
            (do
              (aset lps i 0)
              (recur (inc i) len))))
        lps))))

(defn kmp [pattern text]
  "Knuth-Morris-Pratt string matching algorithm."
  (let [m (count pattern)
        n (count text)
        lps (build-lps pattern)
        matches (atom [])]
    (loop [i 0
           j 0]
      (if (< i n)
        (if (= (nth pattern j) (nth text i))
          (if (= j (dec m))
            (do
              (swap! matches conj (- i j))
              (recur (inc i) (aget lps (dec j))))
            (recur (inc i) (inc j)))
          (if (pos? j)
            (recur i (aget lps (dec j)))
            (recur (inc i) j)))
        @matches))))

(defn calc-hash
  "Calculate initial hash value for a string"
  [s start end base prime]
  (reduce (fn [acc i]
            (mod (+ (* acc base) (int (nth s i))) prime))
          0
          (range start end)))

(defn pow-mod
  "Calculate (base^exp) % mod efficiently"
  [base exp prime]
  (reduce (fn [acc _]
            (mod (* acc base) prime))
          1
          (range exp)))

(defn rabin-karp [pattern text]
  "Rabin-Karp string matching algorithm."
  (let [m (count pattern)
        n (count text)
        base 256
        prime 101
        pattern-hash (calc-hash pattern 0 m base prime)
        first-window-hash (calc-hash text 0 m base prime)
        h (pow-mod base (dec m) prime)]  ; Precompute base^(m-1) % prime
    
    (loop [i 0
           curr-hash first-window-hash
           matches []]
      (if (<= i (- n m))
        (let [
              ; Check for match when hashes are equal
              matches (if (and (= curr-hash pattern-hash)
                             (= pattern (subs text i (+ i m))))
                        (conj matches i)
                        matches)
              
              ; Calculate hash for next window
              next-hash (when (< i (- n m))
                         (let [to-subtract (mod (* h (int (nth text i))) prime)
                               after-subtract (mod (- curr-hash to-subtract) prime)
                               shifted (mod (* after-subtract base) prime)
                               to-add (int (nth text (+ i m)))]
                           (mod (+ shifted to-add) prime)))]
          
          (recur (inc i)
                 (or next-hash curr-hash)
                 matches))
        matches))))

(defn build-bad-char-table [pattern]
  "Build the bad character table."
  (let [table (atom {})]
    (doseq [i (range (count pattern))]
      (swap! table assoc (nth pattern i) i))
    @table))

(defn boyer-moore [pattern text]
  "Boyer-Moore string matching algorithm."
  (let [m (count pattern)
        n (count text)
        bad-char (build-bad-char-table pattern)]
    (loop [i 0
           matches []]
      (if (<= i (- n m))
        (let [j (loop [j (dec m)]
                  (if (and (>= j 0)
                           (= (nth pattern j) (nth text (+ i j))))
                    (recur (dec j))
                    j))]
          (if (= j -1)
            (recur (+ i 1) (conj matches i))
            (recur (+ i (max 1 (- j (get bad-char (nth text (+ i j)) -1)))) matches)))
        matches))))



(def timed-kmp (time-decorator kmp))
(def timed-rabin-karp (time-decorator rabin-karp))
(def timed-boyer-moore (time-decorator boyer-moore))