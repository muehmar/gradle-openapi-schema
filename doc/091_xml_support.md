## XML Support

The plugin has basic support for XML serialization and deserialization.

Serialisation is supported through the same jackson annotations as for JSON. Everything beyond simple schemas
is not tested and may not work (e.g. compositions).

### Supported specific xml definitions

The OpenAPI specification defines some specific XML definitions. The plugin supports the following:

* `xml/name` on schema level. This can be used to define the name of the root XML element.
