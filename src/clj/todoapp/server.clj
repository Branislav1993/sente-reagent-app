(ns todoapp.server
  (:use [org.httpkit.server :only [run-server]])
  (:require [todoapp.handler :refer [app]]
            [environ.core :refer [env]])
  (:gen-class))

 (defn -main [& args]
   (let [port (Integer/parseInt (or (env :port) "3000"))]
     (run-server app {:port port :join? false})))
