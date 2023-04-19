(ns crew.core-test
  (:require [clojure.test :refer [deftest, is, testing]]
            [crew.core :as sut]))

(deftest t--trick-winner
  (testing "single suit"
    (let [trick [{:suit :yellow :number 2}
                 {:suit :yellow :number 8}
                 {:suit :yellow :number 5}
                 {:suit :yellow :number 4}]]

      (is (= (sut/trick-winner trick) 1))))

  (testing "multiple suites"
    (let [trick [{:suit :pink :number 6}
                 {:suit :blue :number 8}
                 {:suit :pink :number 5}
                 {:suit :pink :number 4}]]

      (is (= (sut/trick-winner trick) 0))))

  (testing "submarines"
    (let [trick [{:suit :yellow :number 6}
                 {:suit :submarine :number 4}
                 {:suit :yellow :number 5}
                 {:suit :submarine :number 2}]]

      (is (= (sut/trick-winner trick) 1)))))

(comment

  (clojure.test/run-tests 'crew.core-test)

  :rcf)