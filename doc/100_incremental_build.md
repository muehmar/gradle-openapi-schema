## Incremental build and remote specifications

This plugin supports remote references, i.e. it will also parse any referenced remote specifications and create the java
classes for the schemas in the remote specifications. As the gradle task will depend on this remote specification files,
it must be registered as task-input to properly support incremental build.

The plugin parses by default the given main specification and resolves any referenced remote specifications and register
them as task inputs. This is done before the actual task is executed. This can be disabled (see
the [Configuration](#configuration) section) if needed to avoid parsing the specifications to determine the task inputs.
In case incremental build should still work properly, one has two options:

* In case of no remote reference in the main specification: The main specification is still registered as input,
  therefore incremental build will still work properly.
* In case of remote specifications, one could register the specifications manually as task inputs, like in the following
  example:

 ```groovy
afterEvaluate {
    tasks.named("generateRemoteRefModel") {
        inputs.file("$projectDir/src/main/resources/openapi-remote-ref-sub.yml")
    }
}
 ```
