(ns crew.core
  (:require [crew.vec :as vec]))

(defprotocol GameEntity
  "Implicitly :idx is also defined (suerte rodri del futuro cuando haya algunas que no tengan índice)"
  (lookup [entity game]
    "Look yourself in game, return yourself" #_"otherwise throw?"))

(defrecord Player [idx]
  GameEntity
  (lookup [entity game] (get-in game [:players (:idx entity)])))

(defrecord Trick [idx]
  GameEntity
  (lookup [entity game] (get-in game [:tricks (:idx entity)])))

; forward declarations, in 2023
(declare do-play-trick)

(def suits [:blue :green :pink :yellow :trump])
(def suits-no-trump (remove #{:trump} suits))
(def numbers (into [] (range 1 9)))
(def numbers-trump [1 2 3 4])

(defn on-suit? [suit card-value]
  (= suit (:suit card-value)))

(defn trump? [card-value]
  (on-suit? :trump card-value))

(defn trick-rating [suit card-value]
  (let [number (:number card-value)]
    (cond
      (trump? card-value) (+ 100 number)
      (on-suit? suit card-value) (+ 10 number)
      :else number)))

(defn turn-order [game]
  (let [last-winner-idx (get-in game
                                ; the index of the last winner (all GameEntities are :idxables)
                                [:last-winner :idx]
                                ; first turn the first player goes first :)
                                0)]
    (->> [0 1 2 3]
        (vec/re-head last-winner-idx)
        (map ->Player))))

(defn- trick-suit [trick]
  ; El encapsulamiento está rotísimo
  (-> trick first :value :suit))

(defn trick-winner [trick]
  (->> trick
       (apply max-key (fn [card] (trick-rating (trick-suit trick) (:value card))))
       :player))

(comment

  (def trick [{:player (->Player 0) :value {:suit :yellow :number 8}}
              {:player (->Player 1) :value {:suit :yellow :number 3}}
              {:player (->Player 2) :value {:suit :yellow :number 2}}
              {:player (->Player 3) :value {:suit :yellow :number 6}}])
  
  (lookup (trick-winner trick) { :players [{:name "Rodri" }]})

  :rcf)

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
                        :won-tricks #{(->Trick 0) (->Trick 2)}
                        :hand [{:player (->Player 0) :value {:suit :yellow :number 1}}]}
                       {:name "Nico"
                        :won-tricks #{(->Trick 1)}
                        :hand [{:player (->Player 1) :value {:suit :green :number 2}}]}
                       {:name "Gonza"
                        :won-tricks #{(->Trick 3)}
                        :hand [{:player (->Player 2) :value {:suit :yellow :number 5}}]}
                       {:name "Jan"
                        :won-tricks #{}
                        :hand [{:player (->Player 3) :value {:suit :trump :number 4}}]}]

             :tricks [[{:player (->Player 0) :value {:suit :yellow :number 8}}
                       {:player (->Player 1) :value {:suit :yellow :number 3}}
                       {:player (->Player 2) :value {:suit :yellow :number 2}}
                       {:player (->Player 3) :value {:suit :yellow :number 6}}]

                      [{:player (->Player 1) :value {:suit :blue :number 2}}
                       {:player (->Player 2) :value {:suit :blue :number 9}}
                       {:player (->Player 3) :value {:suit :blue :number 1}}
                       {:player (->Player 0) :value {:suit :blue :number 3}}]

                      [{:player (->Player 0) :value {:suit :yellow :number 7}}
                       {:player (->Player 1) :value {:suit :blue :number 5}}
                       {:player (->Player 2) :value {:suit :blue :number 4}}
                       {:player (->Player 3) :value {:suit :yellow :number 9}}]

                      [{:player (->Player 2) :value {:suit :green :number 6}}
                       {:player (->Player 3) :value {:suit :green :number 1}}
                       {:player (->Player 0) :value {:suit :green :number 9}}
                       {:player (->Player 1) :value {:suit :green :number 5}}]]
             :last-winner (->Player 2)})


  (turn-order game)

  (def trick
    (->> game
         :players
         (map :hand)
         first))

  (pop-cards game [0 0 0 0])

  (play-trick game trick)

  (def game'
    (update-in game [:players] (fn [players] (for [player players] (assoc player :hand [])))))

  (let [order [2 3 0 1]
        trick (for [idx order] (-> game :players (nth idx) :hand first))]
    (-> game
        (play-trick trick)
        :players
        (nth 3)))

  :rcf)