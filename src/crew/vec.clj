(ns crew.vec)

; TODO: these may be useful for seqs, maybe refactor it? (devil's advocate: right now this is only being used with vecs)

; most collection HOF receive the coll last, while non-HOF tend to receive it first.
; this one receives it first so I can use threading macros without hating myself

(defn re-head
  "Moves the element at idx in coll idx spaces forward, sending the first 
  (idx - 1) elements to the back, creating a vec"
  [idx coll]
  (let [vec (into [] coll)]
    (into
     (subvec vec idx)
     (subvec vec 0 idx))))

(defn remove-at
  "Removes the element at idx from coll"
  [idx coll]
  (let [vec (into [] coll)]
    (into (subvec vec 0 idx)
          (subvec vec (inc idx)))))