(ns chromex.app.media-perception-private
  "Private API for receiving real-time media perception information.

     * available since Chrome 60"

  (:refer-clojure :only [defmacro defn apply declare meta let partial])
  (:require [chromex.wrapgen :refer [gen-wrap-helper]]
            [chromex.callgen :refer [gen-call-helper gen-tap-all-events-call]]))

(declare api-table)
(declare gen-call)

; -- functions --------------------------------------------------------------------------------------------------------------

(defmacro get-state
  "Gets the status of the media perception process.

   This function returns a core.async channel which eventually receives a result value and closes.
   Signature of the result value put on the channel is [state] where:

     |state| - ?

   In case of error the channel closes without receiving any result and relevant error object can be obtained via
   chromex.error/get-last-error."
  ([] (gen-call :function ::get-state &form)))

(defmacro set-state
  "Sets the desired state of the system.

     |state| - A dictionary with the desired new state. The only settable states are RUNNING, SUSPENDED, and RESTARTING.

   This function returns a core.async channel which eventually receives a result value and closes.
   Signature of the result value put on the channel is [state] where:

     |state| - ?

   In case of error the channel closes without receiving any result and relevant error object can be obtained via
   chromex.error/get-last-error."
  ([state] (gen-call :function ::set-state &form state)))

(defmacro get-diagnostics
  "Get a diagnostics buffer out of the video analytics process.

   This function returns a core.async channel which eventually receives a result value and closes.
   Signature of the result value put on the channel is [diagnostics] where:

     |diagnostics| - ?

   In case of error the channel closes without receiving any result and relevant error object can be obtained via
   chromex.error/get-last-error."
  ([] (gen-call :function ::get-diagnostics &form)))

(defmacro set-analytics-component
  "Attempts to download and load the media analytics component. This function should be called every time a client starts
   using this API. If the component is already loaded, the callback will simply return that information. The process must be
   STOPPED for this function to succeed. Note: If a different component type is desired, this function can be called with the
   new desired type and the new component will be downloaded and installed.

     |component| - The desired component to install and load.

   This function returns a core.async channel which eventually receives a result value and closes.
   Signature of the result value put on the channel is [component-state] where:

     |component-state| - ?

   In case of error the channel closes without receiving any result and relevant error object can be obtained via
   chromex.error/get-last-error."
  ([component] (gen-call :function ::set-analytics-component &form component)))

; -- events -----------------------------------------------------------------------------------------------------------------
;
; docs: https://github.com/binaryage/chromex/#tapping-events

(defmacro tap-on-media-perception-events
  "Fired when media perception information is received from the media analytics process.

   Events will be put on the |channel| with signature [::on-media-perception [media-perception]] where:

     |media-perception| - The dictionary which contains a dump of everything the analytics process has detected or determined
                          from the incoming media streams.

   Note: |args| will be passed as additional parameters into Chrome event's .addListener call."
  ([channel & args] (apply gen-call :event ::on-media-perception &form channel args)))

; -- convenience ------------------------------------------------------------------------------------------------------------

(defmacro tap-all-events
  "Taps all valid non-deprecated events in chromex.app.media-perception-private namespace."
  [chan]
  (gen-tap-all-events-call api-table (meta &form) chan))

; ---------------------------------------------------------------------------------------------------------------------------
; -- API TABLE --------------------------------------------------------------------------------------------------------------
; ---------------------------------------------------------------------------------------------------------------------------

(def api-table
  {:namespace "chrome.mediaPerceptionPrivate",
   :since "60",
   :functions
   [{:id ::get-state,
     :name "getState",
     :callback? true,
     :params
     [{:name "callback",
       :type :callback,
       :callback {:params [{:name "state", :type "mediaPerceptionPrivate.State"}]}}]}
    {:id ::set-state,
     :name "setState",
     :callback? true,
     :params
     [{:name "state", :type "mediaPerceptionPrivate.State"}
      {:name "callback",
       :type :callback,
       :callback {:params [{:name "state", :type "mediaPerceptionPrivate.State"}]}}]}
    {:id ::get-diagnostics,
     :name "getDiagnostics",
     :callback? true,
     :params [{:name "callback", :type :callback, :callback {:params [{:name "diagnostics", :type "object"}]}}]}
    {:id ::set-analytics-component,
     :name "setAnalyticsComponent",
     :since "64",
     :callback? true,
     :params
     [{:name "component", :type "object"}
      {:name "callback", :type :callback, :callback {:params [{:name "component-state", :type "object"}]}}]}],
   :events
   [{:id ::on-media-perception, :name "onMediaPerception", :params [{:name "media-perception", :type "object"}]}]})

; -- helpers ----------------------------------------------------------------------------------------------------------------

; code generation for native API wrapper
(defmacro gen-wrap [kind item-id config & args]
  (apply gen-wrap-helper api-table kind item-id config args))

; code generation for API call-site
(def gen-call (partial gen-call-helper api-table))