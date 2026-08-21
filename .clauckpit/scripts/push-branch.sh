#!/usr/bin/env bash
# Push the issue branch to origin.
set -euo pipefail

branch=$(git rev-parse --abbrev-ref HEAD)

if [ "$branch" = "master" ] || [ "$branch" = "HEAD" ]; then
  echo "refusing to push: on '$branch', expected an issue branch" >&2
  exit 1
fi

if ! [[ ${branch%%-*} =~ ^[0-9]+$ ]]; then
  echo "refusing to push: branch '$branch' does not start with an issue number" >&2
  exit 1
fi

echo "pushing $branch"
git push --set-upstream origin "$branch"
