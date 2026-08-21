#!/usr/bin/env bash
# Poll GitHub Actions for the pushed head commit until the build settles.
# Exits 0 only when every check succeeded.
set -euo pipefail

TIMEOUT_SECONDS=${CI_TIMEOUT_SECONDS:-2700}   # 45 minutes
POLL_SECONDS=${CI_POLL_SECONDS:-30}

branch=$(git rev-parse --abbrev-ref HEAD)
sha=$(git rev-parse HEAD)
echo "waiting for CI on $branch @ ${sha:0:8} (timeout ${TIMEOUT_SECONDS}s)"

deadline=$(( $(date +%s) + TIMEOUT_SECONDS ))

while :; do
  # status: queued | in_progress | completed ; conclusion: success | failure | ...
  runs=$(gh run list --branch "$branch" --commit "$sha" \
           --json status,conclusion,displayTitle,url --limit 20)

  total=$(printf '%s' "$runs" | jq 'length')

  if [ "$total" -eq 0 ]; then
    echo "no workflow run for ${sha:0:8} yet"
  else
    pending=$(printf '%s' "$runs" | jq '[.[] | select(.status != "completed")] | length')
    failed=$(printf '%s' "$runs" \
      | jq '[.[] | select(.status == "completed" and .conclusion != "success" and .conclusion != "skipped")] | length')

    if [ "$failed" -gt 0 ]; then
      echo "CI is red:"
      printf '%s' "$runs" \
        | jq -r '.[] | select(.status == "completed" and .conclusion != "success" and .conclusion != "skipped")
                 | "  \(.conclusion)  \(.displayTitle)  \(.url)"'
      exit 1
    fi

    if [ "$pending" -eq 0 ]; then
      echo "CI is green ($total run(s) succeeded)"
      exit 0
    fi

    echo "still running: $pending of $total"
  fi

  now=$(date +%s)
  if [ "$now" -ge "$deadline" ]; then
    echo "timed out after ${TIMEOUT_SECONDS}s waiting for CI on ${sha:0:8}" >&2
    exit 1
  fi

  remaining=$(( deadline - now ))
  [ "$POLL_SECONDS" -lt "$remaining" ] && sleep "$POLL_SECONDS" || sleep "$remaining"
done
