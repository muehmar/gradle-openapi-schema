Review the task implementation on this branch. Diff `{{run.base}}..HEAD`.

The ticket:

{{run.input}}

Read it with `gh issue view <NNN>` and review against what it asked for, not against
your own idea of the feature.

Three questions:

1. **Does it do what the ticket asked?** Everything the ticket asks for, and nothing
   beyond it — unrelated changes riding along are a finding, as is a decision the
   implementation made that the ticket did not state.
2. **Do tests cover it?** Plugin tests under `plugin-build/plugin/src/test` that fail
   without this change, and where generated output shifted, a `*.snap` diff showing
   corrected code rather than a rubber-stamped regeneration. If generated output
   changed, the diff must also contain the spec `openapi-issue-NNN.yml`, `'NNN'` in
   `issueNumbers` and `IssueNNNTest.java` under `example-build/example/` — verify, do
   not assume. If it did not change, no spec is required; confirm that from the diff
   rather than taking the claim on trust.
3. **Does it fit the codebase?** Does it follow the conventions of the code around it
   (rich model methods over `instanceof` at call sites, a small composable `Generator`
   gated with `.filter(...)` over conditionals in the caller)?

Out of scope: unrelated bugs, pre-existing architecture, style the code around it
already uses. A correct implementation you would have written differently is a pass.
