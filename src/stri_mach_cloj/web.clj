(ns stri-mach-cloj.web
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.resource :refer [wrap-resource]]
            [stri-mach-cloj.core :refer [timed-kmp timed-rabin-karp timed-boyer-moore]]))

(defroutes app-routes
  (GET "/" [] {:status 200
               :headers {"Content-Type" "text/html"}
               :body (slurp (clojure.java.io/resource "public/index.html"))})
  
  (POST "/benchmark" req
        (let [params (:params req)
              algorithm (get params "algorithm")
              pattern (get params "pattern")
              text (get params "text")
              result (case algorithm
                       "kmp" (timed-kmp pattern text)
                       "rabin-karp" (timed-rabin-karp pattern text)
                       "boyer-moore" (timed-boyer-moore pattern text)
                       "Invalid algorithm")]
          {:status 200
           :headers {"Content-Type" "application/json"}
           :body (pr-str {:result result})}))

  (route/not-found "Not Found"))

(def app
  (-> app-routes
      (wrap-resource "public") ; This will serve files from the 'public' folder
      wrap-params))
