(ns crew.core)

(defn on-suit? [suit card]
  (= suit (:suit card)))

(defn submarine? [card]
  (on-suit? :submarine card))

(defn evaluate-objectives [game]
  game)

; para @nscarcella TODO
; * ejemplo trick-rating escrito en Haskell
; * que es destructuring
; * que es un as-pattern
; * estructura vs polimorfismo. Cual es mejor que el otro?
; * refactorizar para usar una typeclass? :P

(defn trick-rating [suit card]
  (let [number (:number card)]
    (cond
      (submarine? card) (+ 100 number)
      (on-suit? suit card) (+ 10 number)
      :else number)))

(defn trick-winner [trick]
  (let [{trick-suit :suit} (first trick)]
    (->> trick
         (map-indexed vector)
         (apply max-key (fn [[_ card]] (trick-rating trick-suit card)))
         first)))

(defn play-trick [game trick]
  (let [winner-index (trick-winner trick)]
    (-> game
        (assoc :last-winner-index winner-index)
        (update-in [:players winner-index :won-tricks] #(conj % trick))
        evaluate-objectives)))