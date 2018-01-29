package org.apache.felix.dm.annotation.api;

import org.osgi.service.component.annotations.Component;

/**
 * Service scope for the {@link Component}/{@link AdapterService}/{@link AspectService}/{@link BundleAdapterService} annotations.
 */
public enum ServiceScope {
	/**
	 * When the component is registered as a service, it must be registered as a
	 * bundle scope service but only a single instance of the component must be
	 * used for all bundles using the service.
	 */
	SINGLETON,

	/**
	 * When the component is registered as a service, it must be registered as a
	 * bundle scope service and an instance of the component must be created for
	 * each bundle using the service.
	 */
	BUNDLE,

	/**
	 * When the component is registered as a service, it must be registered as a
	 * prototype scope service and an instance of the component must be created
	 * for each distinct request for the service.
	 */
	PROTOTYPE
}
