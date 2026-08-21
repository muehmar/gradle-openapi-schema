## Input

{{run.input}}

Normally a GitHub issue reference. Read it first: `gh issue view <NNN>`, including
comments.

## 1. Gate: is this bug simple?

Simple means all of:

- the cause is clear and localised;
- the fix needs no design change, new abstraction or broad refactoring;
- no decision is left open — exactly one obviously correct behaviour.

Otherwise **stop**: write `"status": "fail"` naming what is unclear or what decision is
needed. No partial fix. Failing here is a correct outcome.

## 2. Branch

The fix belongs on `NNN-short-slug` (e.g. `374-uniqueitems-null-guard`). Check
`git rev-parse --abbrev-ref HEAD`:

- on `master`, or on another issue's branch: `git switch -c NNN-slug master` — only if
  the tree is clean, else fail;
- already on the right `NNN-…` branch: stay.

Everything below lands on this branch.

## 3. Failing example test

Write this **before** the fix, so it proves the bug.

1. `example-build/example/src/main/resources/issues/openapi-issue-NNN.yml` — one spec
   per issue, variants as extra schemas in it.
2. `'NNN'` in `issueNumbers` in `example-build/example/build.gradle`, plus a
   `"${jacksonMajorVersion}IssueNNN { }"` block if the spec needs overrides.
3. `example-build/example/src/test/java/.../issues/issueNNN/IssueNNNTest.java`
   asserting the behaviour the issue calls correct.
4. `./gradlew -p example-build :example:test --tests "*IssueNNNTest"` — it must fail
   for the reported reason. If it passes, the test is wrong, not the plugin.

Commit as its own `test:` commit with an `Issue: #NNN` trailer.

## 4. Fix the plugin

Fix `plugin-build/plugin/` minimally. Add or extend tests under
`plugin-build/plugin/src/test`, regenerating snapshots with
`./gradlew -p plugin-build :plugin:test -PupdateSnapshot=<ClassName>` and reviewing the
`*.snap` diff — search for the old rendered fragment, not just the obviously-named test.

Re-run the example test: it must now pass.

Add a changelog entry under `next` in `doc/130_change_log.md`. Commit with the trailer.

## Result

Pass only when spec, registration, `IssueNNNTest`, plugin fix, plugin tests and
changelog are committed on the issue branch. Name the branch and the fix in `summary`;
add `branch` and `spec` fields.
