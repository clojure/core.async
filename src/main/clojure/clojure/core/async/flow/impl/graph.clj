;;   Copyright (c) Rich Hickey and contributors. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns clojure.core.async.flow.impl.graph)

(defprotocol Graph
  (start [g] "starts the entire graph from init values. The processes start paused. 
              Call resume-all or resume-proc to start flow.
              returns {:report-chan -  a core.async chan for reading 
                       :error-chan - a core.async chan for reading}")
  (stop [g] "shuts down the graph, stopping all procs, can be started again")
  (pause [g] "pauses a running graph")
  (resume [g] "resumes a paused graph")
  (ping [g timeout-ms] "pings all processes, which will put their status and state on the report channel")

  (pause-proc [g pid] "pauses a process")
  (resume-proc [g pid] "resumes a process")
  (ping-proc [g pid timeout-ms] "pings the process, which will put its status and state on the report channel")
  (command-proc [g pid cmd-id more-kvs] "synchronously sends a process-specific command with the given id 
                                         and additional kvs to the process")
  
  (inject [g [pid io-id] msgs] "synchronously puts the messages on the channel 
                         corresponding to the input or output of the process"))
