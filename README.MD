# Dependency Manager Enhancements
This project contains some fixes and enhancements done on the Apache Felix Dependency Manager.
It also provides a TODO section which need to be addressed before doing the release.

# How to build and test
if you are using a corporate http proxy, export the following variable:
```sh
$ export GRADLE_OPTS="-Dhttp.proxyHost=<your proxy ip> -Dhttp.proxyPort=<your proxy port> -Dhttps.proxyHost=<your proxy ip> -Dhttps.proxyPort=<your proxy port>"
```
then type:
```sh
$ gradlew gradlew org.apache.felix.dependencymanager.annotation:jar
$ gradlew jar
$ gradlew check
```
# New features and enhancements
Two kind of improvements have been done with regard to annotations and API. Also, the dependency manager is now built using latest release of bndtools 3.5.0 as well as latest bndlib 3.5.0

# Javadocs
You can find the javadoc for the new annotation API in `./javadocs/annotation`
and the enhanced (compatible) api in `./javadocs/api`

## Annotations enhancements
Essentially, the following enhancements and modifications have been done:
- added support for new user defined property type annotations (similar to OSGI R7).
- annotations using standard r7 @ComponentPropertyType are also supported. Indeed, not only declarative service is using this annotation, other r7 apis like jaxrs whiteboard are also defining some annotations that are themselves annotated with @ComponentPropertyType; so it is important to support this annotation (The dm annotation plugin has been enhanced by reusing some part of the ds annotation scanner from bndlib, which is full of reusable useful code which has been applied to dm (scanning of property types, PREFIX_, etc ...)
- Allow ServiceDependency to auto detect the service type when the annotation is applied on a collection class field
- removed FactoryComponentAdapterService (some attributes have been added in the Component annotation in order to declare factory pid components with the @Component annotation)
- removed some old annotations / attributes. The attributes and annotations related to metatype have been removed since you can now use the standard metatype annotations.  if users need to old version, then they can simply use the previous 4.2.1 from [old dm annotation api][oldAnnotApi]. Notice that the dm runtime is compatible with old and new annotations version, so you can use latest dm annotation runtime and old annotation api.

Since some incompatible changes have been made, the major version of the annotation bundle has been bumped to 5.0.0. 

### User defined property types

So far, you could define component service properties using @Property annotation, and component configururation could be declared as user defined interfaces.
You can now declare user-defined annotations which can be used to specify both service properties and component configuration. It means that instead of declaring service properties using @Property annotation, you can now use your own annotations (which must be annotated with the new @PropertyType annotation, or possibly using the standard @ComponentPropertyType annotation). 
### Usage example
Let’s assume your write an OSGi r7 jaxrs servlet context which needs the two following service properties:
 ```
osgi.http.whiteboard.context.name
osgi.http.whiteboard.context.path
```
Then you can first define your own annotation (but you could also reuse the default annotations provided by the new upcomming jaxrs whiteboard r7 api, from the  org.osgi.service.jaxrs.whiteboard.propertytypes package):

```java
import org.apache.felix.dependencymanager.annotation.PropertyType;

 @PropertyType
 @interface ServletContext {
     String osgi_http_whiteboard_context_name() default AppServletContext.NAME;
     String osgi_http_whiteboard_context_path();
 }
 ```
In the above, the underscore is mapped to ".", and you can apply the above annotation on top of your component like this:

```java
 @Component
 @ServletContext(osgi_http_whiteboard_context_path="/game")
 public class AppServletContext extends ServletContextHelper {
 }
  ```
You can also use configuration admin service in order to override the default service properties:
```java
 @Component
 @ServletContext(osgi_http_whiteboard_context_path="/game")
 public class AppServletContext extends ServletContextHelper {
     @ConfigurationDependency(propagate=true, pid="my.pid")
     void updated(ServletContext cnf) {
        // if some properties are not present in the configuration, then the ones used in the
        // annotation will be used.
        // The configuration admin properties, if defined, will override the default configurations
        // defined in the annotations
     }
 }
  ```
  
You can also define multiple property type annotations, and possibly single valued annotation, like it is the case with standard r7 DS. In this case, you can use the standard R7 PREFIX_ constants in order to specify the property prefix, and the property name will be derived from the single valued annotation (using camel case convention):
```java
 @PropertyType
 @interface ContextName { // will map to "osgi.http.whiteboard.context.name" property name
     String PREFIX="osgi.http.whiteboard.";
     String value();
 }
 
 @PropertyType
 @interface ContextPath { // will map to "osgi.http.whiteboard.context.path" property name
     String PREFIX="osgi.http.whiteboard.";
     String value();
 }
 
 @Component
 @ContextName(AppServletContext.NAME)
 @ContextPath("/game")
 public class AppServletContext extends ServletContextHelper {
 }
   ```
   
Same example as above, but also using configuration admin service in order to override default service properties: Here, as in OSGi r7 declarative service, you can define a callback method which accepts as arguments all (or some of) the defined property types:
```java
 @Component
 @ContextName(AppServletContext.NAME)
 @ContextPath("/game")
 public class AppServletContext extends ServletContextHelper {
     @ConfigurationDependency(propagate=true, pid="my.pid")
     void updated(ContextName ctxName, ContextPath ctxPath) {
        // if some properties are not present in the configuration, then the ones used in the annotation will be used.
        // The configuration admin properties, if defined, will override the default configurations defined in the annotations
     }
 }
 ```
 
The following is the same example as above, but this time the configuration callback can also define a Dictionary in the first argument (in case you want to also get the raw configuration dictionary:
```java
 @Component
 @ContextName(AppServletContext.NAME)
 @ContextPath("/game")
 public class AppServletContext extends ServletContextHelper {
     @ConfigurationDependency(propagate=true, pid="my.pid")
     void updated(Dictionary<String, Object> rawConfig, ContextName ctxName) {
        // if some properties are not present in the configuration, then the ones used in the annotation will be used.
        // The configuration admin properties, if defined, will override the default configurations defined in the annotations
     }
 }
 ```

User defined property types can also be applied on factory components, for example, in the following, the service properties are declared using the user-defined annotations (they will be overriden from the factory configuratin, if present in the config):
```java
@Component(factoryPid="my.factory.pid")
@ContextName(AppServletContext.NAME)
ContextPath("/game")
class Hello implements HelloService {                 
    void updated(ContextName ctxName, ContextPath ctxPath) {
          // Configure or reconfigure our component. the default service 
          // properties will be overriden by the factory configuration (if the
          // service properties are defined in the config)
    }
} 
```
### Not backward compatible annotation changes

This sections describes what has been removed in the annotation api:

- removed FactoryConfigurationAdapterService annotation, which was too verbose. when you need to define some factory pid component, just reuse the @Component annotation and declare the new factoryPid/propagate/updated attributes that have been added in the @Component annotation
- Removed PropertyMetadata annotation: it was related to metatypes, but as of today, osgi metatypes can be defined using standard metatype annotations. No need to support this anymore.
- Removed ResourceAdapterService and ResourceDependency annotations because it was needed to depend on some classes from the dependency manager API. The DM Api should be used directly.
- Removed the following attributes from the Component annotation:
-- FACTORY_NAME
-- FACTORY_INSTANCE
-- factorySet
-- factoryMethod
These attributes were used to be able to create component instances multiple times. Now, simply use factoryPid Component attribute and use standard Configuration Admin in order to instantiate multiple instances of a given service (using factory configuration).
- Removed PropertyMetaData annotation, which was related to osgi metatype. Simply use standard metatype annotation.

## BndTools

the new dependency manager bndtools workspace has been reworked in order to use the latest bndtools 3.5.0, and we are now using the "aQute.bnd.repository.maven.provider.MavenBndRepository" plugin, which allows to download 3rd party libraries from maven central, instead of copying them in the cnf/localrepo/.
Baselining is now using last released artifacts from maven central, and the dependency source tar.gz does not contain any binaries anymore

## Api enhancements
some enhancements have been done in the dm API:
- the api to define aspects and adapters have been reworked (but dm API remains `backward compatible`)
- you can now declare multiple property type interfaces when using Configuration Dependency or Factory Components (this was needed to implement the enhancements for the annotations)
- configuration dependency using metatypes can now declare property types
- Allow to specify if propagated configuration dependencies must override service service properties (it was not possible to override service properties with propagated service properties so far)

### Aspect/Adapters Api enhancements

So far, aspects or adapters were defined using many methods from DependencyManager or DependencyActivatorBase classes:

For example, in DependencyManager.java, we have many signatures

```java
public class DependencyManager {
    public Component createAdapterService(Class<?> serviceInterface, String serviceFilter) {...}
    public Component createAdapterService(Class<?> serviceInterface, String serviceFilter, String autoConfig) {...}
    public Component createAdapterService(Class<?> serviceInterface, String serviceFilter, String add, String change, String remove) {...}
    public Component createAdapterService(Class<?> serviceInterface, String serviceFilter, String add, String change, String remove, String swap) {...}
    public Component createAdapterService(Class<?> serviceInterface, String serviceFilter, String autoConfig, Object callbackInstance, String add, String change, String remove, String swap, boolean propagate) {...}
    
    public Component createFactoryConfigurationAdapterService(String factoryPid, String update, boolean propagate) {...}
    public Component createFactoryConfigurationAdapterService(String factoryPid, String update, boolean propagate, Object callbackInstance) {...}
    public Component createFactoryConfigurationAdapterService(String factoryPid, String update, boolean propagate, Class<?> configType) {...}
    public Component createFactoryConfigurationAdapterService(String factoryPid, String update, boolean propagate, Object callbackInstance, Class<?> configType) {...}
    public Component createAdapterFactoryConfigurationService(String factoryPid, String update, boolean propagate,String heading, String desc, String localization, PropertyMetaData[] propertiesMetaData) {...}
    
    public Component createBundleAdapterService(int bundleStateMask, String bundleFilter, boolean propagate) {...}
    public Component createBundleAdapterService(int bundleStateMask, String bundleFilter, boolean propagate, Object callbackInstance, String add, String change, String remove) {...}
    
    public Component createResourceAdapterService(String resourceFilter, boolean propagate, Object callbackInstance, String callbackChanged) {...}
    public Component createResourceAdapterService(String resourceFilter, boolean propagate, Object callbackInstance, String callbackSet, String callbackChanged)
    public Component createResourceAdapterService(String resourceFilter, Object propagateCallbackInstance, String propagateCallbackMethod, Object callbackInstance, String callbackChanged) {...}
    public Component createResourceAdapterService(String resourceFilter, Object propagateCallbackInstance, String propagateCallbackMethod, Object callbackInstance, String callbackSet, String callbackChanged) {...}
    
    public Component createAspectService(Class<?> serviceInterface, String serviceFilter, int ranking, String autoConfig) {...}
    public Component createAspectService(Class<?> serviceInterface, String serviceFilter, int ranking) {...}
    public Component createAspectService(Class<?> serviceInterface, String serviceFilter, int ranking, String add, String change, String remove) {...}
    public Component createAspectService(Class<?> serviceInterface, String serviceFilter, int ranking, String add, String change, String remove, String swap) {...}
    public Component createAspectService(Class<?> serviceInterface, String serviceFilter, int ranking, Object callbackInstance, String add, String change, String remove, String swap) {...}
```

So, in this new release, we have simplified the usage of the aspect/adapters like this: instead of having to use some of the many methods from the DependencyManager or DependencyActivatorBase, we have added some new interfaces for the aspect and adapters, and these interfaces are extending the Component interface. All other existing methods have been moved to DependencyManagerCompat/DependencyActivatorBaseCompat classes and the DependencyManager/DependencyActovatorBase classes are now extending the compat classes: this allows to simplify the reading of the javadocs for DependencyManager/DependencyActovatorBase.

For example, let's first show how an factory pid  component was declared so far (a factory pid component is one which can be instantated multiple times using a "factory configuration" created using standard "configuration admin" service):

```java
public class Activator extends DependencyActivatorBase {
    @Override
    public void init(BundleContext context, DependencyManager dm) throws Exception {
        dm.add(createFactoryConfigurationAdapterService("my.factory.pid", "updated", true, MyConfig.class)
            .setInterface(MyService.class.getName(), null)
            .setImplementation(MyServiceImpl.class)
            .add(createServiceDependency().setService(LogService.class))); // NullObject 
```

So, now, there is a new FactoryComponent interface which extends the Component interface and it contains all the various parameters used when declaring a factory pid component. So the example above becomes:

```java
public class Activator extends DependencyActivatorBase {
    @Override
    public void init(BundleContext context, DependencyManager dm) throws Exception {
        dm.add(createFactoryComponent()
            .setFactoryPid("my.factory.pid")
            .setPropagate(true)
            .setConfigType(MyConfig.class)
            .setInterface(MyService.class.getName(), null)
            .setImplementation(MyServiceImpl.class)
            .add(createServiceDependency().setService(LogService.class))); // NullObject 
```

now, we explain what the new interfaces look like: Indeed as you can imagine, there is an issue when you extend the Component interface, because the various Component setter methods return a Component type. So, what we would like is to be able to introduce a generic type in the Component interface , and such generic type can then be used to allow the Component setter methods to return not the Component type, but instead return the aspect/adapter sub classes.
This is what we have done, let's show a pseudo code, so you can understand how it works:

First, the Component annotation have been reworked like this:
```java
	public interface Component<T extends Component<T>> {
		T setInterface(String service, Dictionary properties)
		T setImplementation(Object ob);
		...
	}
```
As you can see the Component annotation is now using a recursive generic type which extends the Component interface. And now we have for example the following AdapterComponent interface which is defined like this:

```java
	public interface AdapterComponent extends Component<AdapterComponent> {
		AdapterComponent setAdaptee(Class<?> service, String filter);
		AdapterComponent setAdapteeCallbacks(String add, String change, String remove, String swap);
		...
	}
```
So, now, an adapter component can be defined like this:

```java
Component adapter = createAdapterComponent()
   .setAdaptee(Adaptee.class, "(foo=bar)")
   .setAdapteeCallbacks("setAdaptee", "changeAdaptee", null, null)
   .setImplementation(AdapterImpl.class)
   .setInterface(AdapterService.class, null)
   .add(createServiceDependency().setService(LogService.class));
```

### Support for multiple configuration types in callbacks
The ConfigurationDependency and Factory components can now support updated callbacks with multiple configuration types, for example, the following Activator defines a factory component (using the enhanced api) and multiple configuration types can now be provided:

```java
public class Activator extends DependencyActivatorBase {
    @Override
    public void init(BundleContext context, DependencyManager dm) throws Exception {
        Component factoryComponent = createFactoryComponent()
        	.setFactoryPid(pid).
        	.setConfigType(MyConfig1.class, MyConfig2.class)
        	.setImplementation(MyComponent.class);
        dm.add(factoryComponent);
    }
}

public class MyComponent {
    void updated(MyConfig1 cnf1, MyConfig2 cnf2) { ... }
}
```
Moreover, you can define a Dictionary parameter as the first argument in the updated callback, because sometimes, it's useful to be injected with the raw dictionary configuration, as well as with the configuration types. Example:

```java
public class MyComponent {
    void updated(Dictionary<String, Object> MyConfig1 cnf1, MyConfig2 cnf2) { ... }
}
```
so, the new signatures for the updated callbacks are now the following (for both ConfigurationDependency and Factory Component):

* updated(Dictionary cnf)
* updated(Component comp, Dictionary cnf)
* updated(Component comp, Property interfaces ...)
* updated(Property interfaces ...)
* updated(Dictionary cnf, Property interfaces ...)
* updated(Component comp, Dictionary cnf, Property interfaces ...)

# Bug fixed

- fixed a nasty deadlock when using latest DM r11 and latest SCR 2.0.12 in the same JVM (see FELIX-5716)

# TODO

## Add more signatures in configuration callbacks
The ConfigurationDependency and Factory component updated method currently supports the following method signatures:
 * updated(Dictionary cnf)
 * updated(Component comp, Dictionary cnf)
 * updated(Component comp, Property interfaces ...)
 * updated(Property interfaces ...)
 * updated(Dictionary cnf, Property interfaces ...)
 * updated(Component comp, Dictionary cnf, Property interfaces ...)

So, maybe it would make sense to also add the two following signatures:

* updated(Map cnf, Property interfaces ...)
* updated(Component comp, Map cnf, Property interfaces ...)

[oldAnnotApi]: http://repo1.maven.org/maven2/org/apache/felix/org.apache.felix.dependencymanager.annotation/4.2.1/org.apache.felix.dependencymanager.annotation-4.2.1.jar