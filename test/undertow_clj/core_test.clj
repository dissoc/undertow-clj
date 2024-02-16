;;; Copyright © 2024 Justin Bishop <mail@dissoc.me>

(ns undertow-clj.core-test
  (:require
   [clojure.test :refer :all]
   [hato.client :as http]
   [undertow-clj.server.core :refer :all])
  (:import
   (io.undertow.server HttpHandler)
   (io.undertow.util Headers StatusCodes)))

(def hello-handler
  (reify HttpHandler
    (handleRequest [_ exchange]
      (-> exchange
          .getResponseHeaders
          (.put Headers/CONTENT_TYPE "text/plain"))
      (-> exchange
          (.setStatusCode StatusCodes/OK))
      (-> exchange
          .getResponseSender
          (.send "hello")))))

(deftest server-test
  (testing "Testing http hello"
    (let [port   50087
          host   "127.0.0.1"
          conf   [{:http {:port    port
                          :host    host
                          :handler hello-handler}}]
          server (create-server conf)
          url (str "http://" host ":" port)
          {body         :body
           status       :status
           content-type :content-type} (http/get url)

          _ (.stop server)]
      (is (= body "hello")))))
