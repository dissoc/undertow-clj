(ns undertow-clj.handlers.reverse-proxy
  (:import
   (io.undertow.server HttpHandler)
   (io.undertow.server.handlers.proxy LoadBalancingProxyClient ProxyHandler)
   (java.net URI)))

(defn ^LoadBalancingProxyClient create-load-balancer-client
  "creates a load balancing client. When used with one host it doesn't
  load balance."
  [hosts & {:keys [thread-conns]
            :or   {thread-conns 20}}]
  (doto (new LoadBalancingProxyClient)
    (#(doseq [host hosts]
        (.addHost % (new URI host))))
    (.setConnectionsPerThread thread-conns)))

(defn ^HttpHandler reverse-proxy-handler
  "Returns a handler that reverse proxies to the given
  reverse-proxy-uri."
  [reverse-proxy-uri & {:keys [threads-io timeout]
                        :or   {threads-io 4
                               timeout    10000}}]
  (let [load-balancer (create-load-balancer-client [reverse-proxy-uri])]
    (-> (ProxyHandler/builder)
        (.setProxyClient load-balancer)
        (.setMaxRequestTime timeout)
        (.build))))
