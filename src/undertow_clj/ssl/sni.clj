(ns undertow-clj.ssl.sni
  (:import (io.undertow.protocols.ssl SNIContextMatcher$Builder SNISSLContext)))

(defn create-sni-ssl-context
  "domain matches has form of:
  [{:host-name domain1.com
    :ssl-context ssl-context1}
  {:host-name domain2.com
    :ssl-context ssl-context2}]
  default-ssl-context is the ssl context that is used when
  the sni is not matched"
  [domain-matches default-ssl-context]
  (let [builder (new SNIContextMatcher$Builder)]
    (doseq [{host-name :host-name
             ssl-ctx   :ssl-context} domain-matches]
      (.addMatch builder host-name ssl-ctx))
    (.setDefaultContext builder default-ssl-context)
    (new SNISSLContext (.build builder))))
