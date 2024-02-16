;;; Copyright © 2024 Justin Bishop <mail@dissoc.me>

(ns undertow-clj.server.core
  (:import (io.undertow Undertow)))

(defn ^Undertow create-server
  "Create an instance of an undertow server
  entries should be provided as a map of listeners with
  structures as:
  [{:http  {:port    8080
            :host    \"127.0.0.1\"
            :handler my-http-handler}}
  {:https {:port        8443
           :host        \"127.0.0.1\"
           :ssl-context my-ssl-context
          :handler     my-https-handler}}
  {:http {:port    8081
          :host    \"127.0.0.1\"
          :handler my-other-http-handler}}]"
  [entries & {:keys [threads-io timeout]
              :or   {threads-io 4
                     timeout    10000}}]
  (let [builder (Undertow/builder)]
    (doseq [entry entries]

      ;; add http listeners
      (when (:http entry)
        (let [{port    :port
               host    :host
               handler :handler} (:http entry)]
          (.addHttpListener builder port host handler)))

      ;; add https listeners
      (when (:https entry)
        (let [{port        :port
               host        :host
               handler     :handler
               ssl-context :ssl-context} (:https entry)]
          (.addHttpsListener builder port host ssl-context handler))))

    (.setIoThreads builder threads-io)
    (let [server (.build builder)]
      (.start server)
      server)))
