#!/usr/bin/env sh

# Normalize author/committer names: Claude -> pliplop1, pliplop -> pliplop1
if [ "$GIT_AUTHOR_NAME" = "Claude" ] || [ "$GIT_AUTHOR_NAME" = "pliplop" ]; then
  GIT_AUTHOR_NAME=pliplop1
fi
if [ "$GIT_COMMITTER_NAME" = "Claude" ] || [ "$GIT_COMMITTER_NAME" = "pliplop" ]; then
  GIT_COMMITTER_NAME=pliplop1
fi
export GIT_AUTHOR_NAME GIT_COMMITTER_NAME

