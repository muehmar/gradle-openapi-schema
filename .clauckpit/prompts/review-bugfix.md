Review the bug fix on this branch. Diff `{{run.base}}..HEAD`.

The issue:

{{run.input}}

Read it with `gh issue view <NNN>` and review against the reported behaviour, not your
own guess at it.

Three questions:

1. **Is the bug fixed?** Does the change address the reported cause, and hold for the
   neighbouring inputs (null/absent, empty, the nullable and optional variants this
   repo distinguishes)?
2. **Does an example spec and test cover it?** Verify, do not assume — the diff must
   contain the spec `openapi-issue-NNN.yml`, `'NNN'` in `issueNumbers`, and
   `IssueNNNTest.java`, all under `example-build/example/`. Read the test: it must
   assert the reported behaviour, not merely exercise the schema. Then prove it
   exercises the fix — run it against the fix's parent commit, or reason from the
   generated output why it could not have passed before. Say which you did.
3. **Do plugin tests cover it?** Is there a test under `plugin-build/plugin/src/test`
   that fails without this change — and where generated output shifted, does the
   `*.snap` diff show corrected code rather than a rubber-stamped regeneration?

Out of scope: unrelated bugs, architecture, style, anything the fix did not touch. A
correct fix you would have written differently is a pass.
