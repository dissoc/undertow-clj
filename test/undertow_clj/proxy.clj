(ns undertow-clj.proxy
  (:require
   [clojure.test :refer :all]
   [undertow-clj.handlers.reverse-proxy :refer [reverse-proxy-handler]]
   [undertow-clj.server.core :refer :all]
   [undertow-clj.util :refer :all]))

(deftest reverse-proxy-test
  (testing "Testing reverse proxy"
    (let [destinaion-server (create-server
                             [{:http {:port    50325
                                      :host    "localhost"
                                      :handler print-handler}}])
          rp-handler        (reverse-proxy-handler
                             "http://localhost:50325")
          proxied-response  (run-server->response rp-handler :port 50324)
          _                 (.stop destinaion-server)


          {body         :body
           status       :status
           content-type :content-type} proxied-response]
      (is (= status 200)))))
