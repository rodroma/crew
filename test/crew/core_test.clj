(ns crew.core-test
  (:require [clojure.test :refer [deftest, is, run-tests]]
            [crew.core :as sut]))

(deftest t--on-suit? 
  (let [card {:suit :yellow #_"more stuff here"}]
    (is (true? (sut/on-suit? :yellow card)))

    (is (false? (sut/on-suit? :blue card)))

    (is (false? (sut/trump? card)))))

(deftest t--trick-rating
  (let [trump {:suit :trump :number 2}]
    ; suit doesn't matter
    (is (= (sut/trick-rating :yellow trump)
           102))

    (is (= (sut/trick-rating :trump  trump)
           102)))

  (let [card {:suit :yellow :number 5}]
    ; on suit
    (is (= (sut/trick-rating :yellow card)
           15)

        (= (sut/trick-rating :pink card)
           5))))

(deftest t--turn-order
  (let [game {:players [:player-1 :player-2 :player-3 :player-4 #_ "or real players, only the indexes matter"]
              :last-winner (sut/->Player 2)}]

    (is (= (sut/turn-order game)
           [(sut/->Player 2)
            (sut/->Player 3)
            (sut/->Player 0)
            (sut/->Player 1)]))))

(deftest t--trick-winner
  (let [trick [{:player (sut/->Player 0) :value {:suit :yellow :number 8}}
               {:player (sut/->Player 1) :value {:suit :yellow :number 3}}
               {:player (sut/->Player 2) :value {:suit :yellow :number 2}}
               {:player (sut/->Player 3) :value {:suit :yellow :number 6}}]]
    
    (is (= (sut/trick-winner trick)
           (sut/->Player 0))))
  
  (let [trick [{:player (sut/->Player 0) :value {:suit :yellow :number 8}}
               {:player (sut/->Player 1) :value {:suit :yellow :number 3}}
               {:player (sut/->Player 2) :value {:suit :trump  :number 2}}
               {:player (sut/->Player 3) :value {:suit :yellow :number 6}}]]

    (is (= (sut/trick-winner trick)
           (sut/->Player 2)))))

(comment

  (run-tests)

  :rcf)