(ns crew.core-test
  (:require [clojure.test :refer [deftest, is, run-tests]]
            [crew.core :as sut]))

; TODO: quicktest for clojure?
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

; TODO: this breaks
(deftest t--turn-order
  (let [game {:players [1 2 3 4 #_ "or real players"]
              :last-winner-index 2}]
    (is (= (sut/turn-order game) [3 4 1 2]))))



(comment

  (run-tests)

  :rcf)