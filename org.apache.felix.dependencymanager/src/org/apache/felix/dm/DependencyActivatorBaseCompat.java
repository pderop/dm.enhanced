package org.apache.felix.dm;

public abstract class DependencyActivatorBaseCompat {
	private DependencyManager m_manager;

	protected void setDependencyManager(DependencyManager manager) {
		m_manager = manager;
	}
	
    /**
     * Creates a new aspect service.
     * 
     * @return the aspect service
     * @see DependencyManager#createAspectService(Class, String, int, String)
     * @deprecated use {@link #createAspectComponent()}
     */
    public Component createAspectService(Class<?> serviceInterface, String serviceFilter, int ranking, String attributeName) {
        return m_manager.createAspectService(serviceInterface, serviceFilter, ranking, attributeName);
    }

    /**
     * Creates a new aspect service.
     * 
     * @return the aspect service
     * @see DependencyManager#createAspectService(Class, String, int)
     * @deprecated use {@link #createAspectComponent()}
     */
    public Component createAspectService(Class<?> serviceInterface, String serviceFilter, int ranking) {
        return m_manager.createAspectService(serviceInterface, serviceFilter, ranking);
    }
    
    /**
     * Creates a new aspect service.
     * 
     * @return the aspect service
     * @see DependencyManager#createAspectService(Class, String, int, String, String, String)
     * @deprecated use {@link #createAspectComponent()}
     */
    public Component createAspectService(Class<?> serviceInterface, String serviceFilter, int ranking, String add, String change, String remove) {
        return m_manager.createAspectService(serviceInterface, serviceFilter, ranking, add, change, remove);
    }

    /**
     * Creates a new aspect service.
     * 
     * @return the aspect service
     * @see DependencyManager#createAspectService(Class, String, int, String, String, String, String)
     * @deprecated use {@link #createAspectComponent()}
     */
    public Component createAspectService(Class<?> serviceInterface, String serviceFilter, int ranking, String add, String change, String remove, String swap) {    
        return m_manager.createAspectService(serviceInterface, serviceFilter, ranking, add, change, remove, swap);
    }
    	
    /**
     * Creates a new aspect service.
     * 
     * @return the aspect service
     * @see DependencyManager#createAspectService(Class, String, int, Object, String, String, String, String)
     * @deprecated use {@link #createAspectComponent()}
     */
    public Component createAspectService(Class<?> serviceInterface, String serviceFilter, int ranking, Object callbackInstance, String add, String change, String remove, String swap) {    
        return m_manager.createAspectService(serviceInterface, serviceFilter, ranking, callbackInstance, add, change, remove, swap);
    }
       
    /**
     * Creates a new adapter service.
     * 
     * @return the adapter service
     * @see DependencyManager#createAdapterService(Class, String)
     * @deprecated use {@link #createAdapterComponent()}
     */
    public Component createAdapterService(Class<?> serviceInterface, String serviceFilter) {
        return m_manager.createAdapterService(serviceInterface, serviceFilter);
    }
    
    /**
     * Creates a new adapter service.
     * 
     * @return the adapter service
     * @see DependencyManager#createAdapterService(Class, String, String)
     * @deprecated use {@link #createAdapterComponent()}
     */
    public Component createAdapterService(Class<?> serviceInterface, String serviceFilter, String autoConfig) {
        return m_manager.createAdapterService(serviceInterface, serviceFilter, autoConfig);
    }
    
    /**
     * Creates a new adapter service.
     * 
     * @return the adapter service
     * @see DependencyManager#createAdapterService(Class, String, String, String, String)
     * @deprecated use {@link #createAdapterComponent()}
     */
    public Component createAdapterService(Class<?> serviceInterface, String serviceFilter, String add, String change, String remove) {
        return m_manager.createAdapterService(serviceInterface, serviceFilter, add, change, remove);
    }
    
    /**
     * Creates a new adapter service.
     * @return the adapter service
     * @see DependencyManager#createAdapterService(Class, String, String, String, String, String)
     * @deprecated use {@link #createAdapterComponent()}
     */
    public Component createAdapterService(Class<?> serviceInterface, String serviceFilter, String add, String change, String remove, String swap) {
        return m_manager.createAdapterService(serviceInterface, serviceFilter, add, change, remove, swap);
    }  
    
    /**
     * Creates a new adapter service.
     * @return the adapter service
     * @see DependencyManager#createAdapterService(Class, String, String, Object, String, String, String, String, boolean)
     * @deprecated use {@link #createAdapterComponent()}
     */
    public Component createAdapterService(Class<?> serviceInterface, String serviceFilter, 
        String autoConfig, Object callbackInstance, String add, String change, String remove, String swap) {
       return m_manager.createAdapterService(serviceInterface, serviceFilter, autoConfig, callbackInstance, add, change, remove, swap, true);
    }

    /**
     * Creates a new adapter service.
     * @return the adapter service
     * @see DependencyManager#createAdapterService(Class, String, String, Object, String, String, String, String, boolean)
     * @deprecated use {@link #createAdapterComponent()}
     */
    public Component createAdapterService(Class<?> serviceInterface, String serviceFilter, 
        String autoConfig, Object callbackInstance, String add, String change, String remove, String swap, boolean propagate) {
       return m_manager.createAdapterService(serviceInterface, serviceFilter, autoConfig, callbackInstance, add, change, remove, swap, propagate);
    }

    /**
     * Creates a new factory configuration adapter service.
     * 
     * @return the factory configuration adapter service
     * @deprecated use {@link #createFactoryComponent()}
     */
    public Component createFactoryConfigurationAdapterService(String factoryPid, String update, boolean propagate) {
        return m_manager.createFactoryConfigurationAdapterService(factoryPid, update, propagate);
    }
    
    /**
     * Creates a new factory configuration adapter service, using a specific callback instance
     * 
     * @return the factory configuration adapter service
     * @deprecated use {@link #createFactoryComponent()}
     */
    public Component createFactoryConfigurationAdapterService(String factoryPid, String update, boolean propagate, Object callbackInstance) {
        return m_manager.createFactoryConfigurationAdapterService(factoryPid, update, propagate, callbackInstance);
    }
  
    /**
     * Creates a new factory configuration adapter service, using a specific callback instance
     * 
     * @return the factory configuration adapter service
     * @see DependencyManager#createFactoryConfigurationAdapterService(String, String, boolean, Class)
     * @deprecated use {@link #createFactoryComponent()}
     */
    public Component createFactoryConfigurationAdapterService(String factoryPid, String update, boolean propagate, Class<?> configType) {
        return m_manager.createFactoryConfigurationAdapterService(factoryPid, update, propagate, configType);
    }

    /**
     * Creates a new factory configuration adapter service, using a specific callback instance
     * 
     * @return the factory configuration adapter service
     * @see DependencyManager#createFactoryConfigurationAdapterService(String, String, boolean, Object, Class)
     * @deprecated use {@link #createFactoryComponent()}
     */
    public Component createFactoryConfigurationAdapterService(String factoryPid, String update, boolean propagate, Object callbackInstance, Class<?> configType) {
        return m_manager.createFactoryConfigurationAdapterService(factoryPid, update, propagate, callbackInstance, configType);
    }
    
    /**
     * Creates a new factory configuration adapter service.
     * 
     * @return the factory configuration adapter service
     * @deprecated use {@link #createFactoryComponent()}
     */
    public Component createFactoryConfigurationAdapterService(String factoryPid, String update, boolean propagate, String heading, String desc, String localization, PropertyMetaData[] propertiesMetaData) {
        return m_manager.createAdapterFactoryConfigurationService(factoryPid, update, propagate, heading, desc, localization, propertiesMetaData);
    }

}
