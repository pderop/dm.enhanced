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
import java.util.Map;

import org.apache.felix.dm.annotation.Component;
import org.apache.felix.dm.annotation.PropertyType;
import org.apache.felix.dm.annotation.ServiceDependency;
import org.apache.felix.dm.itest.util.Ensure;

/**
 * @author <a href="mailto:dev@felix.apache.org">Felix Project Team</a>
 */
public class ComponentDMPropertyTypeAnnotation {
    public final static String ENSURE = "ComponentDMPropertyTypeAnnotation";
        
    @PropertyType
    @Retention(RetentionPolicy.CLASS)
    @interface MyProperties {
    	double double_value() default 123;    	
    	String string_value() default "defstring";
    }
    
	@Component(provides=MyComponent.class)
	@MyProperties(double_value=456)
    public static class MyComponent {
    }
	
    @Component
    public static class MyConsumer {
        @ServiceDependency(filter = "(name=" + ENSURE + ")")
        volatile Ensure m_ensure;

        @ServiceDependency
        void bind(MyComponent comp, Map<String, Object> props) {
        	m_ensure.step(1);
        	if ("defstring".equals(props.get("string.value"))) {
        		m_ensure.step(2);
        	}
        	if (new Double(456).equals(props.get("double.value"))) {
        		m_ensure.step(3);
        	}
        }
    }
    
}
