(ns crew.vec-test 
  (:require [clojure.test :refer [deftest is run-test testing]]
            [crew.vec :as sut]))

(deftest t--re-head
  (testing "on empty collections"
    (is (= (sut/re-head 0 []) 
           []))

    (is (thrown? java.lang.IndexOutOfBoundsException 
                 (sut/re-head 1 []))))
  
  (testing "on non-empty collections"
    (is (= (sut/re-head 1 [1 2 3]) 
           [3 1 2]))

    (is (= (sut/re-head 3 [7 2 3 4 5]) 
           [3 4 5 7 2]))
    
    (is (= (sut/re-head 1 '(1 2 3)) 
           [3 1 2]) 
        "list gets converted to vec")))

(deftest t--remove-at
  (is (= (sut/remove-at 1 [1 2 3]) 
         [1 3]))

  (is (thrown? IndexOutOfBoundsException 
               (sut/remove-at 1 []))) 

  (is (= (sut/remove-at 1 '(1 2 3))
         [1 3])
      "list gets converted to vec"))

(comment
  (require '[clojure.repl :as repl]) 
  (run-test t--remove-at)
  :rcf)