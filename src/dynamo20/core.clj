(ns dynamo20.core
  (:use [clojure.tools.cli :only [cli]])
  (:require
   [clj-http.client :as client])
  (:gen-class :main))

(def DEFAULT-SERVER "https://dynupdate.no-ip.com/nic/update")

(defn update-dynamic-ip
  "Update the no-ip DDNS system for the user and hosts specified.
Possible args (a map), * denotes a required field:

  server-url   : The URL to update at. Defaults to: https://dynupdate.no-ip.com/nic/update
* hosts        : A vector of the hosts to update.
  user         : The username
  password     : The password
  ip-address   : The ip address to update to.
                   Defaults to no value which will cause the server to use the addressed as
                   seen by that server.
  debug        : true for debug information. Defualts to false
"  
  [{:keys [server-url user password ip-address hosts debug]
    :or {server-url DEFAULT-SERVER
         ip-address nil
         user nil
         debug false}}]
  (:body (client/get server-url
                     (merge  {:save-request? debug
                              :debug debug
                              :debug-body debug
                              :headers {"User-Agent"
                                        "Dyanamo20 v0.0.1 johnwayner@gmail.com"}
                              :query-params (merge {"hostname" 
                                                    (apply str (interpose \, hosts))}
                                                   (if (not (nil? ip-address))
                                                     {"myip"
                                                      ip-address}))}
                             (if (not (nil? user))
                               {:basic-auth [user password]})))))

(defn -main [& args]
  (let [[param-map hosts help]
        (cli args
             ["-s" "--server-url" "The URL to update at." :default DEFAULT-SERVER] 
             ["-u" "--user" "The username."]
             ["-p" "--password" "The password."]
             ["-ip" "--ip-address" "The ip-address. No setting will cause server to use incoming ip."]
             ["-d" "--debug" "Debugging output if specified." :default false :flag true]
             ["-f" "--force" "Force update by setting to 0.0.0.0 then correct address" :flag true])
        update-params (merge param-map {:hosts hosts})]
    (if (empty? hosts)
      (println (str "Update the no-ip DDNS system for the user and hosts specified.
java -jar <jarfile> <switches> host1 host2 ...\n" help))
      (do
        (when (:force param-map)
          (println (update-dynamic-ip (merge update-params
                                              {:ip-address "0.0.0.0"}))))
        (println (update-dynamic-ip update-params))))))
