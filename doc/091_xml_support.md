## XML Support

The plugin has basic support for XML serialization and deserialization.

Serialisation is supported through the same jackson annotations as for JSON. Everything beyond simple schemas is not
tested and may not work (e.g. compositions).

The OpenAPI specification defines some specific XML definitions. The plugin supports the following:

### Change element names

By default, XML elements get the same names that fields in the API declaration have. To change the default behavior, add
the xml/name field to your spec:

**Specification**

```yaml
components:
  schemas:
    book:
      type: object
      properties:
        id:
          type: integer
        title:
          type: string
        author:
          type: string
      xml:
        name: "xml-book"
```

**XML**

```xml

<xml-book>
    <id>1</id>
    <title>Title</title>
    <author>Author</author>
</xml-book>
```

This can be achieved for properties as well.

**Specification**

```yaml
components:
  schemas:
    book:
      type: object
      properties:
        id:
          type: integer
        title:
          type: string
        author:
          type: string
      xml:
        name: "xml-book"
```        

**XML**

```xml

<xml-book>
    <id>0</id>
    <title>string</title>
    <author>string</author>
</xml-book>
```

### Converting a property to an attribute

To make some property an attribute in the resulting XML data, use the xml/attribute:

**Specification**

```yaml
book:
  type: object
  properties:
    id:
      type: integer
      xml:
        attribute: true
    title:
      type: string
    author:
      type: string
```

**XML**

```xml

<book id="1">
    <title>Title</title>
    <author>Author</author>
</book>
```

### Wrapping arrays

Arrays are translated as a sequence of elements of the same name:

**Specification**

```yaml
components:
  schemas:
    Container:
      properties:
        books:
          type: array
          items:
            type: string
          example:
            - "one"
            - "two"
            - "three"
```

**XML**

```xml

<Container>
    <books>one</books>
    <books>two</books>
    <books>three</books>
</Container>
```

If needed, you can add a wrapping element by using the xml/wrapped property:

**Specification**

```yaml
components:
  schemas:
    Container:
      properties:
        books:
          type: array
          items:
            type: string
          xml:
            wrapped: true
          example:
            - "one"
            - "two"
            - "three"
```

**XML**

```xml

<Container>
    <book>
        <books>one</books>
        <books>two</books>
        <books>three</books>
    </book>
</Container>
```

As you can see, by default, the wrapping element has the same name as item elements. Use xml/name to give different
names to the wrapping element and array items (this will help you resolve possible naming issues):

**Specification**

```yaml
components:
  schemas:
    Container:
      properties:
        books:
          type: array
          items:
            type: string
            xml:
              name: "item"
          xml:
            name: "books-array"
            wrapped: true
          example:
            - "one"
            - "two"
            - "three"
```

**XML**

```xml

<Container>
    <books-array>
        <item>one</item>
        <item>two</item>
        <item>three</item>
    </books-array>
</Container>
```
