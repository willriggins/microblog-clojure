(ns microblog-clojure.core
  (:require [compojure.core :as c]
            [ring.adapter.jetty :as j]
            [hiccup.core :as h]
            [ring.middleware.params :as p]
            [ring.util.response :as r])
  (:gen-class))

(defonce messages (atom []))

(c/defroutes app
  (c/GET "/" []
    (h/html [:html
             [:body
              [:form {:action "/add-message" :method "post"}
               [:input {:type "text" :placeholder "Add message" :name "message"}]
               [:button {:type "submit"} "Submit"]]
              [:ol
               (map (fn [message]
                      [:li message])
                 @messages)]]]))
                 
  (c/POST "/add-message" request
    (let [params (:params request)
           message (get params "message")]
      (swap! messages conj message)
      (r/redirect "/"))))

(defonce server (atom nil))

(defn -main []
  (when @server
    (.stop @server))
  (let [app (p/wrap-params app)]
    (reset! server (j/run-jetty app {:port 3000 :join? false}))))
