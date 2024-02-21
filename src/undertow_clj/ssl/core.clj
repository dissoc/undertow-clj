(ns undertow-clj.ssl.core
  "This namespace provides helper functions to deal with keystore, keymanagers,
  and ssl-contexts."
  (:require
   [clojure.java.io :refer [input-stream]])
  (:import
   (java.security KeyStore)
   (javax.net.ssl KeyManagerFactory SSLContext)))

(defn keystore->keymanager [keystore password]
  (let [factory (doto (KeyManagerFactory/getInstance
                       (KeyManagerFactory/getDefaultAlgorithm))
                  (.init keystore (.toCharArray password)))]
    (.getKeyManagers factory)))

(defn keystore->ssl-context [keystore password]
  (with-open [is (input-stream keystore)]
    (let [keystore (-> (KeyStore/getDefaultType)
                       KeyStore/getInstance)]
      (.load keystore is (.toCharArray password))
      keystore
      (doto (SSLContext/getInstance "TLSv1.2")
        (.init (keystore->keymanager keystore password)
               nil nil)))))
