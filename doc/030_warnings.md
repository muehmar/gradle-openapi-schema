## Warnings

The plugin emit warnings for certain scenarios. These warnings are printed to the console of the gradle build. These
warnings can also be turned off completely if necessary via configuration of the plugin.

The plugin can also be configured to let the generation fail in case warnings occurred (similar to the -Werror flag for
the Java compiler). This can be done globally for every warning or selective for any warning type, see the
[Configuration](#configuration) section.

The plugin generates the following warnings:

| Type                       | Description                                                                                                                                                                                           |
|----------------------------|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| UNSUPPORTED_VALIDATION     | Mappings can be defined without conversions. For custom types without conversion, no validation annotations will be generated which will produce this warning.                                        |
| MISSING_MAPPING_CONVERSION | Mappings without conversion may lead to serialisation or validations issues (see [asd](010_configuration.md#conversions-for-mappings). This warning is generated for each mapping without conversion. |
