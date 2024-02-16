;;; Copyright © 2024 Justin Bishop <mail@dissoc.me>

(ns undertow-clj.util
  (:require
   [hato.client :as http]
   [undertow-clj.server.core :refer [create-server]])
  (:import
   (io.undertow.server HttpHandler)
   (io.undertow.util Headers StatusCodes)))

(defn run-server
  [handler & {:keys [port host]
              :or   {port 50080
                     host "127.0.0.1"}}]
  (let [conf     [{:http {:port    port
                          :host    host
                          :handler handler}}]
        server   (create-server conf)
        url      (str "http://" host ":" port)
        response (http/get url {:throw-exceptions? false})
        _        (.stop server)]
    response))

;; use print-handler to see what the request looks like
;; to assist in writing tests
(def print-handler
  (reify HttpHandler
    (handleRequest [_ exchange]
      (println {:headers (.getRequestHeaders exchange)})
      (-> exchange
          .getResponseHeaders
          (.put Headers/CONTENT_TYPE "text/plain"))
      (-> exchange
          (.setStatusCode StatusCodes/OK))
      (-> exchange
          .getResponseSender
          (.send "hello")))))
