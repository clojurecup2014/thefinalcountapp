(ns thefinalcountapp.data.store)


(defprotocol Store
  ;; Groups
  (create-group [_ group])

  (get-group [_ group])

  (group-exists? [_ group])

  ;; Counters
  (create-counter [_ group counter])

  (counter-exists? [_ group id])

  (get-counter [_ group id])

  (update-counter [_ group counter-id new-counter])

  (delete-counter [_ group counter-id]))
