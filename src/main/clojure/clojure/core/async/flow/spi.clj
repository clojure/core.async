;;   Copyright (c) Rich Hickey and contributors. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns clojure.core.async.flow.spi)

(defprotocol ProcLauncher
  "Note - defining a ProcLauncher is an advanced feature and should not
  be needed for ordinary use of the library. This protocol is for
  creating new types of Processes that are not possible to create
  with ::flow/process.
  
  A ProcLauncher is a constructor for a process, a thread of activity.
  It has two functions - to describe the parameters and input/output
  requirements of the process and to start it. The launcher should
  acquire no resources, nor retain any connection to the started
  process. A launcher may be called upon to start a process more than
  once, and should start a new process each time start is called.
  
  The process launched process must obey the following:

  It must have 2 logical statuses, :paused and :running. In
  the :paused status operation is suspended and no output is
  produced.

  When the process starts it must be :paused

  Whenever it is reading or writing to any channel a process must use
  alts!! and include a read of the ::flow/control channel, giving it
  priority.

  Command messages sent over the ::flow/control channel have the keys:
  ::flow/to - either ::flow/all or a process id
  ::flow/command - ::flow/stop|pause|resume|ping or process-specific
  
  It must act upon any, and only, control messages whose ::flow/to key is its pid or ::flow/all
  It must act upon the following values of ::flow/command:

  ::flow/stop - all resources should be cleaned up and any thread(s)
     should exit ordinarily - there will be no more subsequent use
     of the process.
  ::flow/pause - enter the :paused status
  ::flow/resume - enter the :running status and resume processing
  ::flow/ping - emit a ping message (format TBD) to
     the ::flow/report channel containing at least its pid and status

  A process can define and respond to other commands in its own namespace.

  A process should not transmit channel objects (use [pid io-id] data
  coordinates instead) A process should not close channels

  Finally, if a process encounters an error it must report it on the
  ::flow/error channel (format TBD) and attempt to continue, though it
  may subsequently get a ::flow/stop command it must respect"
  
  (describe [p]
    "returns a map with keys - :params, :ins and :outs,
  each of which in turn is a map of keyword to docstring

  :params describes the initial arguments to setup the state for the process
  :ins enumerates the input[s], for which the graph will create channels
  :outs enumerates the output[s], for which the graph may create channels.

  describe may be called by users to understand how to use the
  proc. It will also be called by the impl in order to discover what
  channels are needed.")
  
  (start [p {:keys [pid args ins outs resolver]}]
    "return ignored, called for the
  effect of starting the process (typically, starting its thread)

  where:

  :pid - the id of the process in the graph, so that e.g. it can refer to itself in control, reporting etc
  :args - a map of param->val,  as supplied in the graph def
  :ins - a map of in-id->readable-channel, plus the ::flow/control channel
  :outs - a map of out-id->writeable-channel, plus the ::flow/error and ::flow/report channels
          N.B. outputs may be nil if not connected
  :resolver - an impl of spi/Resolver, which can be used to find
              channels given their logical [pid cid] coordinates, as well as to
              obtain ExecutorServices corresponding to the
              logical :mixed/:io/:compute contexts"))

(defprotocol Resolver
  (get-write-chan [_ coord]
    "Given a tuple of [pid cid], returns a core.async chan to
    write to or nil (in which case the output should be dropped,
    e.g. nothing is connected).")
  (get-exec [_ context]
    "returns the ExecutorService for the given context, one
     of :mixed, :io, :compute"))
