## Extraction of enum description

The plugin supports the extraction of description for each member of an enum from the openapi specification. The idea is
to provide an optional default message/description for enums which may be used in the code and are subject to get out of
sync if updated manually.

The assumption is that the description for an enum is provided in form of a list, like the following:

```yaml
  role:
    type: string
    enum: [ "Admin", "User", "Visitor" ]
    description: |
      Role of the user
      * `Admin`: Administrator role
      * `User`: User role
      * `Visitor`: Visitor role
```

If the extraction is enabled, one can define a prefix to let the plugin extract the corresponding description, where the
placeholder `__ENUM__` can be used to match the corresponding member. In this example, the `prefixMatcher` can be set to
`` `__ENUM__`: ``. Everything after the matcher until the line break will get extracted as description for the
corresponding member. The description in the code is available via the `getDescription()` method on the enum.

The configuration setting `failOnIncompleteDescriptions` can be used to prevent missing descriptions for a member cause
of a typo in the enum name (for example if `` * `Vistor`: Visitor role `` is written in the spec) or if one adds a
member without adding the description.
