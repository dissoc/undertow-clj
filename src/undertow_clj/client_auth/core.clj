(ns undertow-clj.client-auth.core
  (:import
   (io.undertow Undertow$Builder)
   (org.xnio SslClientAuthMode)))

(defn enable-client-auth
  "Enables client auth on the undertow server.
  By default it will be :required but it can also be
  set to :requested"
  [^Undertow$Builder undertow-builder & auth-type]
  (.setSocketOption undertow-builder
                    (case auth-type
                      :required  SslClientAuthMode/REQUIRED
                      :requested SslClientAuthMode/REQUESTED
                      ;; default
                      SslClientAuthMode/REQUIRED)) )
