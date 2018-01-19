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
package org.apache.felix.dm.runtime.itest.components;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Hashtable;
import java.util.Map;

import org.apache.felix.dm.annotation.Component;
import org.apache.felix.dm.annotation.ConfigurationDependency;
import org.apache.felix.dm.annotation.PropertyType;
import org.apache.felix.dm.annotation.ServiceDependency;
import org.apache.felix.dm.itest.util.Ensure;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsName;

/**
 * @author <a href="mailto:dev@felix.apache.org">Felix Project Team</a>
 */
public class JaxrsComponentPropertyTypeAnnotation {

	@Component(provides=JaxRsComponent.class)
	@JaxrsName("foo") 
    public static class JaxRsComponent {
    }

    @Component
    public static class JaxRsConsumer {
        public final static String ENSURE = "ComponentPropertyTypeAnnotations.JaxRsConsumer";
        
        @ServiceDependency(filter = "(name=" + ENSURE + ")")
        volatile Ensure m_ensure;

        @ServiceDependency
        void bind(JaxRsComponent jaxrs, Map<String, Object> props) {
        	m_ensure.step(1);
        	if ("foo".equals(props.get("osgi.jaxrs.name"))) {
        		m_ensure.step(2);
        	}
        }
    }
    
}
