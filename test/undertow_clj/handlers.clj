;;; Copyright © 2024 Justin Bishop <mail@dissoc.me>

(ns undertow-clj.handlers
  (:require
   [clojure.test :refer :all]
   [undertow-clj.handlers.ip-access :refer [allowlist-handler]]
   [undertow-clj.util :as util :refer [run-server]]))

(deftest allowlist-handler-test
  (testing "Testing forbidden access"
    (let [response                     (-> {:ipv4-cidrs ["1.2.3.4/32"]}
                                           (allowlist-handler)
                                           (run-server :port 50324))
          {body         :body
           status       :status
           content-type :content-type} response]
      (is (= status 403))))

  (testing "Testing allowed access"
    (let [response                     (-> {:ipv4-cidrs ["127.0.0.1/32"]}
                                           (allowlist-handler)
                                           (run-server))
          {body         :body
           status       :status
           content-type :content-type} response]
      ;; the response of 404 is expected when success handler
      ;; isnt provided
      (is (= status 404)))))
