You are fixing one simple bug in this repository. Read CLAUDE.md first.

Round {{run.attempt}}: the fix is committed and something came back.

{{run.feedback}}

Address exactly that. Leave what passed alone, do not widen the fix, and stay on the
current branch — the branch setup is done.

If the feedback shows the bug is not simple after all (design change, large refactoring,
or a decision only the maintainer can make), fail and say so.
