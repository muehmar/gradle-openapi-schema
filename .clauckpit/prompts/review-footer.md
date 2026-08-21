## Reporting

Fail on a blocking finding: the bug is not fixed, the diff breaks something, the example
spec or `IssueNNNTest` is missing or does not assert the reported behaviour, or no
plugin test would catch a regression. Everything else is a note.

```json
{
  "status": "pass | fail",
  "summary": "Is the bug fixed, is it covered by an example spec/test and by plugin tests.",
  "findings": [
    { "severity": "blocking | note", "title": "…", "detail": "…", "file": "path:line" }
  ]
}
```

A finding names file and line and states the concrete failure — which input produces
which wrong result. Empty `findings` on a clean pass.
