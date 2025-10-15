#!/usr/bin/env sh

# Remove Claude Code footers and explicit co-author lines for Claude
sed -r -e "/Claude Code/d" -e "/[Cc]o-[Aa]uthored-[Bb]y:[[:space:]]*Claude/d"

