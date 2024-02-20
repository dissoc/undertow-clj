;;; Copyright © 2024 Justin Bishop <mail@dissoc.me>

(ns undertow-clj.handlers.redirect
  (:import (io.undertow.server HttpHandler)
           (io.undertow.util Headers StatusCodes)))

(defn http->https-redirect-handler
  "handler that sends a 301 moved permanently to upgrade
  http or ws to https or wss. If a redirect to https or wss is required, it is
  not necessary in most cases to explicitly provide the updated port (443).
  parameters:
  redirect-port (number): if set it will redirect to the provided port
  redirect-host (string): if set it will redirect to the provided host"
  [& {:keys [redirect-port redirect-host]}]
  (reify HttpHandler
    (handleRequest [_ exchange]
      (let [redirect-location
            ;; generating the url of the redirect
            (str
             ;; mapped protocols
             (case (.getRequestScheme exchange)
               "ws"   "wss"
               "http" "https")
             "://"
             ;; if new host provided then use it
             ;; else use the same host
             (if redirect-host
               redirect-host
               (.getHostName exchange))
             ;; when there is redirect port then use it
             ;; it is important to remember that https and wss
             ;; are assumed to be port 443 and that in most cases
             ;; it does not need to be explicitly added
             (when (and redirect-port
                        (not (= 80 redirect-port))
                        (not (= 443 redirect-port)))
               (str ":" redirect-port))
             ;; add the request path at the end
             (.getRequestPath exchange))]
        (.setStatusCode exchange StatusCodes/MOVED_PERMANENTLY)
        (-> exchange
            .getResponseHeaders
            (.put Headers/LOCATION redirect-location))
        (.endExchange exchange)))))
