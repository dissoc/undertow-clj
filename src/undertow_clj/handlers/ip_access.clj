(ns undertow-clj.handlers.ip-access
  (:import
   (inet.ipaddr IPAddressString)
   (io.undertow.util StatusCodes)
   (io.undertow.server.handlers
    ResponseCodeHandler
    IPAddressAccessControlHandler)))

(defn allowlist-handler
  "Only allows access from the provided cidrs.
  Otherwise access is not granted. If a service
  like cloudflare is used it can be useful to
  use this allowlist handler to restrict access
  from their servers only. See tests for examples.
  ip-cidrs has structure:
  {:ipv4-cidrs [127.0.0.1/32 1.2.3.0/24]
   :ipv6-cidrs [2405:b500::/32 2405:8100::/32]}"
  [ip-cidrs
   & {:keys [deny-response-code success-handler]
      :or   {deny-response-code StatusCodes/FORBIDDEN
             success-handler    ResponseCodeHandler/HANDLE_404}}]
  (let [{ipv4_cidrs :ipv4-cidrs
         ipv6_cidrs :ipv6-cidrs} ip-cidrs
        allowlist-handler        (new IPAddressAccessControlHandler
                                      success-handler
                                      deny-response-code)]
    (doseq [ipv4-cidr ipv4_cidrs]
      (.addAllow allowlist-handler (str ipv4-cidr)))
    (doseq [ipv6-cidr ipv6_cidrs]
      (let [full-addr (-> ipv6-cidr
                          IPAddressString.
                          .getAddress
                          .getSection
                          str)]
        (.addAllow allowlist-handler full-addr)))
    allowlist-handler))
