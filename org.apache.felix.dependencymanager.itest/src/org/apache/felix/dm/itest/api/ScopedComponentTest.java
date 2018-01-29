/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.felix.dm.itest.api;

import java.util.Map;
import java.util.Properties;

import org.apache.felix.dm.Component;
import org.apache.felix.dm.Component.ServiceScope;
import org.apache.felix.dm.ComponentState;
import org.apache.felix.dm.ComponentStateListener;
import org.apache.felix.dm.DependencyManager;
import org.apache.felix.dm.itest.util.Ensure;
import org.apache.felix.dm.itest.util.TestBase;
import org.junit.Assert;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceRegistration;

/**
 * Validates a simple scoped service, which does not add some dynamic dependencies with a component init method.
 */
public class ScopedComponentTest extends TestBase {
	final Ensure m_e = new Ensure();

	public void testScopedComponent() {
        DependencyManager m = getDM();     
        
        Component provider = m.createComponent()
        	.setScope(ServiceScope.PROTOTYPE)
            .setFactory(this, "createServiceImpl")
            .setInterface(Service.class.getName(), null)
            .add(m.createServiceDependency().setRequired(true).setService(Service2.class).setCallbacks("bind", null));
        
        Component service2 = m.createComponent()
        		.setInterface(Service2.class.getName(), null)
        		.setImplementation(new Service2() {});
        
        Component consumer1 = m.createComponent()
            .setFactory(this, "createServiceConsumer")
            .add(m.createServiceDependency().setService(Service.class).setRequired(true).setCallbacks("bind", "change", "unbind"));
                        
        m.add(provider);          // add provider
        m.add(consumer1);         // add first consumer
        m.add(service2);          // add service2 (the provider depends on it)
        
        m.remove(consumer1);

        m.clear();
    }
    
    @SuppressWarnings("unused")
    private ServiceImpl createServiceImpl() { 
        return new ServiceImpl();
    }
    
    @SuppressWarnings("unused")
    private ServiceConsumer createServiceConsumer() {
        return new ServiceConsumer();
    }
        
    public interface Service { 
    }
    
    public interface Service2 { 
    }
        
    public class ServiceImpl implements Service {
        volatile Bundle m_bundle; // bundle requesting the service
        volatile ServiceRegistration m_registration; // registration of the requested service
		volatile Service2 m_service2;
        
        void bind(Service2 service2) {
        	m_service2 = service2;
        }

        void start() {
        }
        
        void stop() {
        }
    }
    
    public class ServiceConsumer {
        volatile Service m_myService;

        public void bind(Service service) {
            m_myService = service;
        }
        
        public void change(Service service, Map<String, Object> properties) {
        }
        
        public void unbind(Service service) {
        }
    }
}
