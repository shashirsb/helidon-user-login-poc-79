/*
 * Copyright (c) 2018, 2020 Oracle and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.helidon.microprofile.poc;

import java.util.logging.Logger;
import io.helidon.microprofile.server.Server;




/**
 * Explicit example.
 */
public class Main {
	
	private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private Main() {
    }

    /**
     * Starts server manually.
     *
     * @param args command line arguments (ignored)
     */
    public static void main(String[] args) {
        Server server = Server.builder()
                .host("localhost")
                // use a random free port
                .port(0)
                .build();

        server.start();
        
        /*Tracer tracer = new ApmTracer.Builder("Helidon_MP_USER_LOGIN_POC", "UserLogin")            
                .          
                .build();*/

        String endpoint = "http://" + server.host() + ":" + server.port();
        LOGGER.info("Started application on     " + endpoint);
        LOGGER.info("Metrics available on       " + endpoint + "/metrics");
        LOGGER.info("Heatlh checks available on " + endpoint + "/health");

    }
}
