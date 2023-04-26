(ns crew.core
  (:require [crew.vec :as vec]))

; forward declarations, in 2023
(declare do-play-trick)

(def suits [:blue :green :pink :yellow :trump])
(def suits-no-trump (remove #{:trump} suits))
(def numbers (into [] (range 1 9)))
(def numbers-trump [1 2 3 4])

(defn vector->card [[number suit]]
  {:number number :suit suit})

(defn on-suit? [suit card]
  (= suit (:suit card)))

(defn trump? [card]
  (on-suit? :trump card))

(defn trick-rating [suit card]
  (let [number (:number card)] 
    (cond
      (trump? card) (+ 100 number)
      (on-suit? suit card) (+ 10 number)
      :else number)))

(defn turn-order [game]
  (let [last-winner-idx (:last-winner-index game)]
    (->> game
         :players
         (map-indexed (fn [idx _] idx))
         (vec/re-head last-winner-idx))))

(defn do-trick-winner-no-shift [trick]
  (let [{trick-suit :suit} (first trick)]
    (->> trick
         (map-indexed vector)
         (apply max-key (fn [[_ card]]
                          (trick-rating trick-suit card)))
         first)))

(comment
  (play-trick game [0 0 0 0])

  (do-trick-winner-no-shift trick)
  :rcf)

(defn trick-winner [game trick] 
  (let [old-winner-idx          (:last-winner-index game)
        player-count            (count (:players game))
        new-winner-idx-no-shift (do-trick-winner-no-shift trick)]
    (mod
     (- new-winner-idx-no-shift old-winner-idx)
     player-count)))

(defn pop-cards [game indexes]
  (let [order          (turn-order game)
        player-to-card (map vector order indexes)] 
    (reduce (fn [acc [player-idx trick-idx]] 
              (update-in acc
                         [:players player-idx :hand] 
                         (fn [tricks]
                           (vec/remove-at trick-idx tricks))))
            game
            player-to-card)))

(defn generate-cards [suits numbers]
  (->> suits
       (map vector numbers)
       (map vector->card)))

(def cards-no-trumps (generate-cards suits-no-trump numbers))

; TODO: check if the player is trying to cheat by using an off suit card
(defn play-trick [game indexes]
  (let [player-idx-to-card-idx (map-indexed vector indexes)
        unordered-trick (map (fn [[player-idx card-idx]]
                               (get-in game [:players player-idx :hand card-idx]))
                             player-idx-to-card-idx)
        trick (vec/re-head (:last-winner-index game) unordered-trick)]
    (-> game 
        (do-play-trick trick)
        (pop-cards indexes))))

(defn- set-winner [game winner-idx trick-idx]
  (update-in game [:players winner-idx :won-tricks] #(conj % trick-idx)))

(defn- do-play-trick [game trick]
  (let [winner-idx (trick-winner game trick)
        trick-idx  (count (:tricks game))]
    (-> game
        (assoc :last-winner-index winner-idx)
        (update :tricks #(conj % trick))
        (set-winner winner-idx trick-idx))))

(comment
  (def game {:players [{:name "Rodri"
                        :won-tricks #{0 2}
                        :hand [{:suit :yellow :number 1}]}
                       {:name "Nico"
                        :won-tricks #{1}
                        :hand [{:suit :green :number 2}]}
                       {:name "Gonza"
                        :won-tricks #{3}
                        :hand [{:suit :yellow :number 5}]}
                       {:name "Jan"
                        :won-tricks #{}
                        :hand [{:suit :trump :number 4}]}]
             :tricks [[{:suit :yellow :number 8} {:suit :yellow :number 3} {:suit :yellow :number 2} {:suit :yellow :number 6}]
                      [{:suit :blue :number 2}   {:suit :blue :number 9}   {:suit :blue :number 1}   {:suit :blue :number 3}]
                      [{:suit :yellow :number 7} {:suit :blue :number 5}   {:suit :blue :number 4}   {:suit :yellow :number 9}]
                      [{:suit :green :number 6}  {:suit :green :number 1}  {:suit :green :number 9}  {:suit :green :number 5}]]
             :last-winner-index 2})


  (turn-order game)

  (def trick
    (->> game
         :players
         (map :hand)
         first))

  (pop-cards game [0 0 0 0])

  (play-trick game trick)

  ; TODO: remove-from-hands
  (def game'
    (update-in game [:players] (fn [players] (for [player players] (assoc player :hand [])))))

  (let [order [2 3 0 1]
        trick (for [idx order] (-> game :players (nth idx) :hand first))]
    (-> game
        (play-trick trick)
        :players
        (nth 3)))

  :rcf)