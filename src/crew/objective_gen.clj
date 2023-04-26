(ns crew.objective-gen
  (:require [crew.core :as core]))

(defn generate-won?-objectives []
  (for [card core/cards-no-trumps]
    [:won? [:exactly card]]))

(comment

  (generate-won?-objectives)

  :rcf)