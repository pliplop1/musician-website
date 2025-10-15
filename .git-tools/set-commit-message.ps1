param(
    [Parameter(Mandatory=$true, Position=0)]
    [string]$MessageFile
)

$text = Get-Content -Raw -LiteralPath $MessageFile

# Remove explicit Claude Code footer lines
$text = ($text -split "`r?`n") | Where-Object {
    $_ -notmatch 'Generated with Claude Code' -and \
    $_ -notmatch 'Claude Code' -and \
    $_ -notmatch '^Co-Authored-By:\s*Claude' -and \
    $_ -notmatch '^Co-authored-by:\s*Claude'
} | ForEach-Object {
    $_
} | Out-String

# Replace standalone names in messages
$text = $text -replace '\bClaude\b','pliplop1'
$text = $text -replace '\bpliplop\b','pliplop1'

# Trim trailing newline added by Out-String
$text = $text.TrimEnd("`r","`n")
Set-Content -LiteralPath $MessageFile -Value $text -NoNewline
exit 0
