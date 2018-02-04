package org.apache.felix.dm.itest.api;

import java.util.Map;
import java.util.Properties;

import org.apache.felix.dm.Component;
import org.apache.felix.dm.Component.ServiceScope;
import org.apache.felix.dm.DependencyManager;
import org.apache.felix.dm.itest.util.Ensure;
import org.apache.felix.dm.itest.util.TestBase;
import org.junit.Assert;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceRegistration;

/**
 * Validates a simple scoped service, which adds a dynamic dependency from init method.
 * Notice the the prototype will add a dynamic dependency from its init method, and the dependency service
 * properties will be propagated.
 */
public class DynamicScopedServiceTest extends TestBase {
	final Ensure m_e = new Ensure();
	final Ensure.Steps m_serviceImplInitSteps = new Ensure.Steps(1, 2, 5, 12, 13);
	final Ensure.Steps m_serviceImplStartSteps = new Ensure.Steps(3, 6, 14);
	final Ensure.Steps m_serviceImplStopSteps = new Ensure.Steps(9, 11, 17);
    final Ensure.Steps m_serviceConsumerBindSteps = new Ensure.Steps(4, 7, 15);
    final Ensure.Steps m_serviceConsumerUnbindSteps = new Ensure.Steps(8, 10, 16);
    		
    public void testPrototypeComponent() {
        DependencyManager m = getDM();     
        
        Component provider = m.createComponent()
        	.setScope(ServiceScope.PROTOTYPE)
            .setFactory(this, "createServiceImpl")
            .setInterface(Service.class.getName(), null);
        
        Properties props = new Properties();
        props.put("foo", "bar");
        Component service2 = m.createComponent()
        	.setInterface(Service2.class.getName(), props)
        	.setImplementation(new Service2() {});
        
        Component<?> consumer1 = m.createComponent()
            .setFactory(this, "createServiceConsumer")
            .add(m.createServiceDependency().setService(Service.class).setRequired(true).setCallbacks("bind", "unbind"));
        
        Component<?> consumer2 = m.createComponent()
            .setFactory(this, "createServiceConsumer")
            .add(m.createServiceDependency().setService(Service.class).setRequired(true).setCallbacks("bind", "unbind"));
                
        m.add(provider); // add provider
        m.add(service2); // add service2 (the prototype depends on it)
        m.add(consumer1); // add first consumer
        m_e.waitForStep(1, 5000); // Service prototype instance called in init
        m_e.waitForStep(2, 5000); // first clone called in init
        m_e.waitForStep(3, 5000); // first clone called in start
        m_e.waitForStep(4, 5000); // first consumer bound with the first clone

        m.add(consumer2); // add second consumer
        m_e.waitForStep(5, 5000); // second clone called in init
        m_e.waitForStep(6, 5000); // second clone called in start
        m_e.waitForStep(7, 5000); // second consumer bound with the second clone

        // make sure both consumers have a different provider instances.
        ServiceConsumer consumer1Impl = consumer1.getInstance();
        Assert.assertNotNull(consumer1Impl.getService());
        ServiceConsumer consumer2Impl = consumer2.getInstance();
        Assert.assertNotNull(consumer2Impl.getService());
        Assert.assertNotEquals(consumer1Impl.getService(), consumer2Impl.getService());
        
        m.remove(consumer1); // remove consumer1
        m_e.waitForStep(8, 5000); // consumer1 unbound from first clone
        m_e.waitForStep(9, 5000); // first clone stopped
        
        m.remove(provider); // unregister the provider
        m_e.waitForStep(10, 5000); // consumer2 unbound from second clone
        m_e.waitForStep(11, 5000); // second clone stopped
        
        m.add(provider); // re-register the provider
        m_e.waitForStep(12, 5000); // prototype init called
        m_e.waitForStep(13, 5000); // clone init method called (because consumer2 is active)
        m_e.waitForStep(14, 5000); // clone start method called 
        m_e.waitForStep(15, 5000); // consumer2 bind method called
        
        m.remove(service2); // remove the service2 (it will destroy the clone)
        m_e.waitForStep(16, 5000); // consumer2 unbound
        m_e.waitForStep(17, 5000); // third clone stopped
        
        m.remove(provider);        
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
        volatile Bundle m_bundle; // bundle requesting the service, or null in case this is the prototype instance
        volatile ServiceRegistration m_registration; // registration of the requested service, or null in case this is the prototype instance
		volatile Service2 m_service2;
        
		void init(Component component) {
			DependencyManager m = component.getDependencyManager();
			component.add(m.createServiceDependency().setService(Service2.class).setRequired(true).setCallbacks("bind", null).setPropagate(true));
			m_e.steps(m_serviceImplInitSteps); // 1, 2, 5, 12, 13
		}
		
        void bind(Service2 service2, Map<String, Object> properties) {
        	// check if prototype service properties has propagated the Service2 dependency service properties
        	Assert.assertEquals("bar", properties.get("foo"));
        	m_service2 = service2;
        }

        void start() {
        	Assert.assertNotNull(m_bundle);
        	Assert.assertNotNull(m_service2);
        	m_e.steps(m_serviceImplStartSteps); // 3, 6, 14
        }
        
        void stop() {
        	m_e.steps(m_serviceImplStopSteps); // 9, 11, 17
        }
    }
    
    public class ServiceConsumer {
        volatile Service m_myService;

        public void bind(Service service) {
            m_myService = service;
        	m_e.steps(m_serviceConsumerBindSteps); // 4, 7, 15
        }
        
        public void unbind(Service service) {
        	Assert.assertEquals(m_myService, service);
        	m_e.steps(m_serviceConsumerUnbindSteps); // 8, 10, 16
        }
        
        public Service getService() {
            return m_myService;
        }
    }
}
