CI is green on the pushed branch. Open its pull request.

The issue:

{{run.input}}

1. Confirm the branch is pushed and green (`gh run list --branch <branch> --limit 5`).
   If not green, fail — no PR on a red build.
2. `gh pr create --base master` with a title `<type>: <what changed>` and a body stating
   the bug, the fix in a sentence or two, that an example spec and `IssueNNNTest` cover
   it, and `Closes #NNN`.

Pass when the PR exists. Put its URL in `summary` and in a `pr_url` field.
