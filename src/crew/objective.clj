(ns crew.objective)

(defn eval-game [game]
  game)

(defn matcher-id [matcher] 
  (first matcher))

(defmulti eval-matcher (fn [_ctx matcher] (matcher-id matcher)))

(defmethod eval-matcher :exactly [ctx [_ card]]
  (let [player ((:player-index ctx) (:game ctx))]
    
    ))
