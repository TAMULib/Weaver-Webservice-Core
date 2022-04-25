
# Weaver WRO Documentation

Weaver WRO is a service that generates web resources dynamically from static files and/or runtime properties and is an extension and customization of [wro4j](https://github.com/wro4j/wro4j).

Weaver WRO is primarily used to generate compiled CSS from source SASS/CSS files, but could also potentially be configured and/or extended to provide any other wro4j function.

The main features provided by Weaver WRO over pure wro4j is simplified configuration, live re-building of web resources, and dynamic theming.

## Configuration Overview


Weaver WRO utilizes [Spring configuration](https://github.com/TAMULib/Weaver-Webservice-Core/blob/2.x/wro/src/main/java/edu/tamu/weaver/wro/config/WeaverWroConfiguration.java) to programmatically configure pure wro4j properties as well as integrate its own configurations. 

Any valid Spring configuration format should work to provide the following properties.

### wro4j properties

The following properties are used directly by wro4j. More details are available in the wro4j [ConfigurableWroManagerFactory documentation](https://wro4j.readthedocs.io/en/stable/ConfigurableWroManagerFactory/).

#### wro.managerFactoryClassName

Required

Default: `edu.tamu.weaver.wro.manager.factory.WeaverConfigurableWroManagerFactory`

The value of this property should match the class instance provided by `edu.tamu.weaver.wro.config.WeaverWroConfiguration::getWroManagerFactory()` and should only be changed if a client app is providing its own WroManagerFactory implementation.

Vireo is currently our only example of a client app providing its own WroManagerFactory implementation. This is accomplished by extending WeaverWroConfiguration, overriding its [getWroManagerFactory method](https://github.com/TexasDigitalLibrary/Vireo/blob/main/src/main/java/org/tdl/vireo/config/VireoWroConfiguration.java#L21"), and setting the wro.managerFactoryClassName to the [corresponding class name](https://github.com/TexasDigitalLibrary/Vireo/blob/main/src/main/resources/application.yaml#L172)

#### wro.preProcessors

Required

Default: lessCssImport

#### wro.postProcessors

Required

Default: rubySassCss

Any wro4j processor(s) of ResourceType CSS could theoretically be used here as pre or post processors.

Weaver WRO currently provides one [custom post processor](https://github.com/TAMULib/Weaver-Webservice-Core/blob/2.x/wro/src/main/java/edu/tamu/weaver/wro/processor/RepoPostProcessor.java) that is enabled by adding `repoPostProcessor` to the start of the postProcessors list. Its job is to retrieve and inject theme properties from a RepoThemeManager implementation into wro4j's CSS build pipeline. An example of this in action is the [Weaver Webservice Seed app](https://github.com/TAMULib/Weaver-Webservice-Seed/blob/master/src/main/resources/config/application.properties#L64).

Here's a [link](https://wro4j.readthedocs.io/en/stable/AvailableProcessors/) to the available wro4j's pre/post processors

There are a lot of options and possibilities there. Our current processor configurations are only concerned with processing imports and converting sass to css.

`cssMin` is a good one to add but it can sometimes cause problems if the source SASS/CSS has any formatting issues.



### Weaver WRO configuration properties

These configuration properties are specific to Weaver WRO.

#### theme.managerService 

Optional

Default: `edu.tamu.weaver.wro.service.SimpleThemeManagerService`

This is an optional property to define the [ThemeManager](#thememanagers) implementation to use. Weaver WRO will use [SimpleThemeManagerService](https://github.com/TAMULib/Weaver-Webservice-Core/blob/2.x/wro/src/main/java/edu/tamu/weaver/wro/service/SimpleThemeManagerService.java) when this property isn't configured by a client.

[Vireo](https://github.com/TexasDigitalLibrary/Vireo/blob/main/src/main/resources/application.yaml#L182) is an example of a client app providing its own ThemeManager implementation.


#### theme.default.css

Required

Define a comma separated list of the locations of any SASS/CSS files to be processed.

These files must be available on the classpath.

[SimpleThemeManagerService](https://github.com/TAMULib/Weaver-Webservice-Core/blob/2.x/wro/src/main/java/edu/tamu/weaver/wro/service/SimpleThemeManagerService.java#L45) and its extenders pass this list to wro4j's WroModelFactory via [WeaverConfigurableWroManagerFactory](https://github.com/TAMULib/Weaver-Webservice-Core/blob/2.x/wro/src/main/java/edu/tamu/weaver/wro/manager/factory/WeaverConfigurableWroManagerFactory.java#L47).


#### theme.default.location

Optional

Default: none

Optionally define the location of default themes json file, if using [RepoThemeManagerService](https://github.com/TAMULib/Weaver-Webservice-Core/blob/2.x/wro/src/main/java/edu/tamu/weaver/wro/service/RepoThemeManagerService.java). RepoThemeManagerService uses this file to initialize its repository with themes and properties.

The Weaver Webservice Seed app provides an [example](https://github.com/TAMULib/Weaver-Webservice-Seed/blob/master/src/main/resources/config/theme-defaults.json) of the format.


#### theme.reloadUrl

Required

Default: `${app.url}/wro/wroAPI/reloadCache`


The wro4j API endpoint for triggering a rebuild of the css. This doesn't normally need to be changed


## Technical Breakdown

The following details the different elements of Weaver WRO and how they function.

#### WeaverWroConfiguration

[WeaverWroConfiguration.java](https://github.com/TAMULib/Weaver-Webservice-Core/blob/2.x/wro/src/main/java/edu/tamu/weaver/wro/config/WeaverWroConfiguration.java) is the class that brings all aspects of Weaver WRO, wro4j, and Spring configuration together.

This class:

* Defines the 'wro' prefix used to scope the wro4j [configuration properties](#wro4j-properties) within Spring configuration files.
* Registers the [ConfigurableWroFilter](https://github.com/wro4j/wro4j/blob/master/wro4j-core/src/main/java/ro/isdc/wro/http/ConfigurableWroFilter.java) as a FilterRegistrationBean with Spring so we can define the server endpoint ('wro') and apply the client's wro4j config properties.
* Dynamically instantiates and registers the configured ThemeManager implementation as a Bean as well as makes it available to wro4j via our custom [WeaverConfigurableWroManagerFactory](#weaverconfigurablewromanagerfactory).
* Registers [WeaverRequestHandler](#weaverrequesthandler) to process requests to dynamically rebuild the css during runtime.

#### WeaverConfigurableWroManagerFactory

Extending wro4j's [ConfigurableWroManagerFactory](https://wro4j.readthedocs.io/en/stable/ConfigurableWroManagerFactory/) enables us to customize several aspects of wro4j's behavior.

This class:

* Provides wro4j with our [custom implementation](#weaverwromodelfactory) of [WroModelFactory](https://github.com/wro4j/wro4j/blob/master/wro4j-core/src/main/java/ro/isdc/wro/model/factory/WroModelFactory.java).
* Registers our custom [repoPostProcessor](#repopostprocessor) with wro4j
* Registers our custom [sassClassPathUri locator](#sassclasspathurilocator) with wro4j

#### WeaverWroModelFactory

[WeaverWroModelFactory](https://github.com/TAMULib/Weaver-Webservice-Core/blob/2.x/wro/src/main/java/edu/tamu/weaver/wro/model/factory/WeaverWroModelFactory.java) is responsible for building the group of files (SASS/CSS in our case) that will be sent to wro4j for processing.

#### RepoPostProcessor
The [RepoPostProcessor](https://github.com/TAMULib/Weaver-Webservice-Core/blob/2.x/wro/src/main/java/edu/tamu/weaver/wro/processor/RepoPostProcessor.java) is available for cases where the client app needs to store dynamic themes and theme properties in a Spring Repository.

The RepoPostProcessor can retrieve these theme properties by theme name and prepend them as SASS variables to the client configured SASS/CSS group.

It is activated by adding 'repoPostProcessor' as the first entry in the [wro.postProcessors](#wropostprocessors) list.

#### SassClassPathUriLocator

The [SassClasspathUriLocator](https://github.com/TAMULib/Weaver-Webservice-Core/blob/2.x/wro/src/main/java/edu/tamu/weaver/wro/resource/locator/SassClassPathUriLocator.java) is a Weaver customization that's necessary for **SASS imports** to work when the source SASS files **reside on the classpath**.

Its implementation is based on the wro4j docs [here](https://wro4j.readthedocs.io/en/stable/CreateCustomLocator/) and the PR [here](https://github.com/wro4j/wro4j/pull/1048/files).

#### WeaverRequestHandler

[WeaverRequestHandler](https://github.com/TAMULib/Weaver-Webservice-Core/blob/2.x/wro/src/main/java/wro4j/http/handler/WeaverRequestHandler.java) enables the dynamic regeneration of the optimized css file. 

By default, wro4j only builds the web resources, css in our case, once at app startup, and that's it.

Our WeaverRequestHandler extends wro4j's debug only [ReloadCacheRequestHandler](https://wro4j.readthedocs.io/en/stable/RequestHandler/) and sets it to always be enabled. This allows us to regenerate our css repeatedly at runtime by http request.

From wro4j docs:

>ReloadCacheRequestHandler - responsible for handling /wro/wroAPI/reloadCache request, which will trigger the clear cache operation. Thus, any cached content will be removed and any subsequent call for some resource will require a new processing cycle. This handler is available only in debug (DEVELOPMENT) mode."

### ThemeManagers

Weaver WRO introduces the ThemeManager interface to provide a flexible mechanism for supplying dynamic theme properties and refreshing the themed CSS.

Weaver WRO supplies [SimpleThemeManagerService](https://github.com/TAMULib/Weaver-Webservice-Core/blob/2.x/wro/src/main/java/edu/tamu/weaver/wro/service/SimpleThemeManagerService.java) for cases where no dynamic theming is required. It's enabled by default.

Weaver WRO also offers the RepoThemeManager interface and Service implementation for cases where dynamic theming is needed. It will load default themes and properties from a source json file, then provide those properties to wro4j. Client apps can use the CoreThemeRepo Repository to manipulate and activate themes at runtime. The Weaver Web Service Seed app [provides an example of this](https://github.com/TAMULib/Weaver-Webservice-Seed/blob/master/src/main/java/edu/tamu/app/controller/ThemeController.java).