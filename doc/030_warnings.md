## Warnings
The plugin emit warnings for certain scenarios. These warnings are printed to the console of
the gradle build. These warnings can also be turned off completely if necessary via configuration of
the plugin.

The plugin can also be configured to let the generation fail in case warnings occurred (similar to the -Werror flag for
the Java compiler). This can be done globally for every warning or selective for any warning type, see the
[Configuration](#configuration) section.

The plugin generates the following warnings:

| Type                   | Description                                                                                                                                                                                                                                                                                                                      |
|------------------------|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| UNSUPPORTED_VALIDATION | Validation of custom types is currently not supported. This means, if a property has some constraints but is mapped to a custom type, no validation will be performed for this property. This may be supported in a future version of the plugin, see issue [#160](https://github.com/muehmar/gradle-openapi-schema/issues/160). |
