;;; Copyright © 2024 Justin Bishop <mail@dissoc.me>

(ns undertow-clj.handlers
  (:require
   [clojure.test :refer :all]
   [undertow-clj.handlers.ip-access :refer [allowlist-handler]]
   [undertow-clj.handlers.redirect :refer [http->https-redirect-handler]]
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

(deftest http->https-redirect-handler-test
  (testing "redirect no port or host provided"
    (let [handler                      (http->https-redirect-handler)
          response                     (-> (http->https-redirect-handler)
                                           (run-server :port 50324))
          {body         :body
           status       :status
           headers      :headers
           content-type :content-type} response]
      (is (= status 301))
      (is (= "https://127.0.0.1/" (get headers "location")))))

  (testing "redirect no port or host provided with path"
    (let [handler                      (http->https-redirect-handler)
          response                     (-> (http->https-redirect-handler)
                                           (run-server :port 50324
                                                       :request-path "/my/path"))
          {body         :body
           status       :status
           headers      :headers
           content-type :content-type} response]
      (is (= status 301))
      (is (= "https://127.0.0.1/my/path" (get headers "location")))))

  (testing "redirect with port and host provided"
    (let [handler                      (http->https-redirect-handler
                                        :redirect-host "localhost"
                                        :redirect-port 54321)
          response                     (-> handler
                                           (run-server :port 50324))
          {body         :body
           status       :status
           headers      :headers
           content-type :content-type} response]
      (is (= status 301))
      (is (= "https://localhost:54321/" (get headers "location"))))))
