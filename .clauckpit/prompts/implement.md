## Input

{{run.input}}

Normally a GitHub issue reference. Read it first: `gh issue view <NNN>`, including
comments.

## 1. Gate: is this task simple?

Simple means all of:

- the ticket states what to do clearly enough to implement without guessing;
- every decision it involves is already stated in the ticket — you make none;
- it needs no design change, no new abstraction and no broad refactoring.

Otherwise **stop**: write `"status": "fail"` naming the decision the ticket leaves open,
or the design change or refactoring it would require. No partial implementation.
Failing here is a correct outcome.

## 2. Branch

The work belongs on `NNN-short-slug` (e.g. `361-group-validation-options-in-dsl`). Check
`git rev-parse --abbrev-ref HEAD`:

- on `master`, or on another issue's branch: `git switch -c NNN-slug master` — only if
  the tree is clean, else fail;
- already on the right `NNN-…` branch: stay.

Everything below lands on this branch.

## 3. Implement

Implement exactly what the ticket asks, in `plugin-build/plugin/`, following the
surrounding code. Nothing beyond it: an improvement you notice on the way belongs in its
own issue, not in this commit.

Cover it with tests under `plugin-build/plugin/src/test`, regenerating snapshots with
`./gradlew -p plugin-build :plugin:test -PupdateSnapshot=<ClassName>` and reviewing the
`*.snap` diff — search for the old rendered fragment, not just the obviously-named test.

**If the change alters generated output**, it also needs an end-to-end test:

1. `example-build/example/src/main/resources/issues/openapi-issue-NNN.yml` — one spec
   per issue, variants as extra schemas in it.
2. `'NNN'` in `issueNumbers` in `example-build/example/build.gradle`, plus a
   `"${jacksonMajorVersion}IssueNNN { }"` block if the spec needs overrides.
3. `example-build/example/src/test/java/.../issues/issueNNN/IssueNNNTest.java`
   asserting the new behaviour, verified with
   `./gradlew -p example-build :example:test --tests "*IssueNNNTest"`.

A change that leaves generated output identical (a refactoring, a DSL or docs change)
needs no spec — say so in your summary rather than inventing one.

Add a changelog entry under `next` in `doc/130_change_log.md` if the change is
user-visible. Commit with an `Issue: #NNN` trailer.

## Result

Pass only when the implementation, its tests and any changelog entry are committed on
the issue branch. Name the branch and what you built in `summary`; add a `branch` field
and a `generated_output_changed` boolean.
