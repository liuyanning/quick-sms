package com.drondea.sms.windowing;

/*
 * #%L
 * ch-commons-util
 * %%
 * Copyright (C) 2012 Cloudhopper by Twitter
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Internal utility class to monitor the window and send events upstream
 * to listeners.
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class WindowMonitor<K, R, P> implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Window.class);

    private final WeakReference<Window<K, R, P>> windowRef;
    private final String monitorThreadName;
//    private final Window<K,R,P> window;

    public WindowMonitor(Window<K, R, P> window, String monitorThreadName) {
//        this.window = window;
        this.windowRef = new WeakReference<Window<K, R, P>>(window);
        this.monitorThreadName = monitorThreadName;
    }

    @Override
    public void run() {
        String currentThreadName = null;
        try {
            if (this.monitorThreadName != null) {
                currentThreadName = Thread.currentThread().getName();
                Thread.currentThread().setName(monitorThreadName);
            }
            Window<K, R, P> window = windowRef.get();
            // check if the window using this monitor was GC'ed
            if (window == null) {
                logger.debug("The parent Window was garbage collected in this WindowMonitor(): missing call to Window.reset() to stop this monitoring thread (will throw exception to cancel this recurring execution!)");
                throw new IllegalStateException("Parent Window was garbage collected (missing call to Window.reset() somewhere in code)");
            }

            if (logger.isTraceEnabled()) {
                logger.trace("Monitor running... (current window.size [" + window.getSize() + "])");
            }

            List<WindowFuture<K, R, P>> expired = window.cancelAllExpired();
            if (expired != null && expired.size() > 0) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Monitor found [" + expired.size() + "] requests expired");
                }
                // process each expired request and pass up the chain to handlers
                for (WindowFuture<K, R, P> future : expired) {
                    for (UnwrappedWeakReference<WindowListener<K, R, P>> listenerRef : window.getListeners()) {
                        WindowListener<K, R, P> listener = listenerRef.get();
                        if (listener == null) {
                            // remove this reference from our array (no good anymore)
                            window.removeListener(listener);
                        } else {
                            try {
                                listener.expired(future);
                            } catch (Throwable t) {
                                logger.error("Ignoring uncaught exception thrown in listener: ", t);
                            }
                        }
                    }
                }
            }
        } finally {
            if (currentThreadName != null) {
                // change the name of the thread back
                Thread.currentThread().setName(currentThreadName);
            }
        }
    }
}
